package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Role
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.PosViewModel
import com.example.ui.viewmodel.ViewMode
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppRoot()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppRoot(viewModel: PosViewModel = viewModel()) {
    val context = LocalContext.current
    val currentScreen by viewModel.currentScreen.collectAsState()
    val session by viewModel.session.collectAsState()
    val syncStatus by viewModel.syncStatus.collectAsState()
    val showPinDialog by viewModel.showPinDialog.collectAsState()
    val showReceiptDialog by viewModel.showReceiptDialog.collectAsState()
    val lastTx by viewModel.lastCompletedTransaction.collectAsState()
    val showExcelDialog by viewModel.showExcelDialog.collectAsState()
    val showConnectPcDialog by viewModel.showConnectPcDialog.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenWidth = this.maxWidth
        val isWideScreen = when (viewMode) {
            ViewMode.PC_DESKTOP -> true
            ViewMode.MOBILE -> false
            ViewMode.AUTO -> screenWidth >= 840.dp
        }

        if (isWideScreen) {
            // Wide Screen / Tablet / PC POS Terminal (Side-by-side Layout like the Screenshot)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(if (screenWidth < 960.dp) Modifier.horizontalScroll(rememberScrollState()) else Modifier)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .then(if (screenWidth < 960.dp) Modifier.width(980.dp) else Modifier.fillMaxWidth())
                ) {
                    AppSidebar(
                        currentScreen = currentScreen,
                        session = session,
                        onNavigate = { viewModel.navigateTo(it) },
                        onSwitchRole = { viewModel.switchRole(it) },
                        onSwitchShift = { viewModel.switchShift(it) },
                        onOpenConnectPc = { viewModel.setShowConnectPcDialog(true) }
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(BackgroundLight)
                    ) {
                        TopHeaderBar(
                            session = session,
                            syncStatus = syncStatus,
                            onOpenKasbon = { viewModel.navigateTo(AppScreen.PIUTANG) },
                            onOpenExcel = { viewModel.setShowExcelDialog(true) },
                            onToggleRole = {
                                val nextRole = if (session.role == Role.OWNER) Role.CASHIER else Role.OWNER
                                viewModel.switchRole(nextRole)
                            },
                            onOpenConnectPc = { viewModel.setShowConnectPcDialog(true) },
                            currentViewMode = viewMode,
                            onToggleViewMode = {
                                val nextMode = if (viewMode == ViewMode.PC_DESKTOP) ViewMode.MOBILE else ViewMode.PC_DESKTOP
                                viewModel.setViewMode(nextMode)
                            }
                        )

                        Box(modifier = Modifier.weight(1f)) {
                            ActiveScreenContent(currentScreen = currentScreen, viewModel = viewModel)
                        }
                    }
                }
            }
        } else {
            // Compact / Mobile Layout with Modal Drawer
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(drawerContainerColor = EmeraldDark) {
                        AppSidebar(
                            currentScreen = currentScreen,
                            session = session,
                            onNavigate = {
                                viewModel.navigateTo(it)
                                scope.launch { drawerState.close() }
                            },
                            onSwitchRole = {
                                viewModel.switchRole(it)
                                scope.launch { drawerState.close() }
                            },
                            onSwitchShift = {
                                viewModel.switchShift(it)
                                scope.launch { drawerState.close() }
                            },
                            onOpenConnectPc = {
                                viewModel.setShowConnectPcDialog(true)
                                scope.launch { drawerState.close() }
                            }
                        )
                    }
                }
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Column {
                                    Text("JU-FAT CELL", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.White)
                                    Text(currentScreen.title, fontSize = 11.sp, color = Color(0xFF80CBC4))
                                }
                            },
                            navigationIcon = {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Menu Navigasi",
                                        tint = Color.White
                                    )
                                }
                            },
                            actions = {
                                // Switch to PC Mode button on mobile top bar
                                IconButton(onClick = { viewModel.setViewMode(ViewMode.PC_DESKTOP) }) {
                                    Icon(
                                        imageVector = Icons.Default.DesktopWindows,
                                        contentDescription = "Beralih ke Tampilan PC",
                                        tint = Color.White
                                    )
                                }
                                IconButton(onClick = { viewModel.setShowConnectPcDialog(true) }) {
                                    Icon(
                                        imageVector = Icons.Default.QrCodeScanner,
                                        contentDescription = "Hubungkan PC",
                                        tint = Color.White
                                    )
                                }
                                IconButton(onClick = { viewModel.setShowExcelDialog(true) }) {
                                    Icon(
                                        imageVector = Icons.Default.FileDownload,
                                        contentDescription = "Excel",
                                        tint = Color.White
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = EmeraldDark)
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(BackgroundLight)
                    ) {
                        ActiveScreenContent(currentScreen = currentScreen, viewModel = viewModel)
                    }
                }
            }
        }
    }

    // PIN Security barrier dialog
    if (showPinDialog) {
        PinUnlockDialog(
            onDismiss = { viewModel.dismissPinDialog() },
            onSubmitPin = { pin -> viewModel.submitPin(pin) }
        )
    }

    // Receipt Dialog
    if (showReceiptDialog && lastTx != null) {
        ReceiptDialog(
            transaction = lastTx!!,
            onDismiss = { viewModel.dismissReceipt() }
        )
    }

    // Excel Dialog
    if (showExcelDialog) {
        ExcelImportExportDialog(
            onDismiss = { viewModel.setShowExcelDialog(false) },
            onImportCsv = { csv -> viewModel.importCsv(csv) },
            getExportProductsCsv = { viewModel.getExportProductsCsv() },
            getExportCashCsv = { viewModel.getExportCashCsv() }
        )
    }

    // Connect PC Dialog
    if (showConnectPcDialog) {
        ConnectPcDialog(
            currentMode = viewMode,
            onSelectMode = { viewModel.setViewMode(it) },
            onDismiss = { viewModel.setShowConnectPcDialog(false) }
        )
    }
}

@Composable
fun ActiveScreenContent(currentScreen: AppScreen, viewModel: PosViewModel) {
    when (currentScreen) {
        AppScreen.KASIR -> KasirScreen(viewModel = viewModel)
        AppScreen.DASHBOARD -> DashboardScreen(viewModel = viewModel)
        AppScreen.KAS_HARIAN -> KasHarianScreen(viewModel = viewModel)
        AppScreen.TRANSFER -> TransferScreen(viewModel = viewModel)
        AppScreen.MANAJEMEN_STOK -> ManajemenStokScreen(viewModel = viewModel)
        AppScreen.SERVICE_HP -> ServiceHpScreen(viewModel = viewModel)
        AppScreen.PPOB -> PulsaPpobScreen(viewModel = viewModel)
        AppScreen.PERCETAKAN -> PercetakanScreen(viewModel = viewModel)
        AppScreen.PIUTANG -> PiutangScreen(viewModel = viewModel)
        AppScreen.RETUR -> PiutangScreen(viewModel = viewModel)
        AppScreen.SISTEM_DATABASE -> DatabaseSyncScreen(viewModel = viewModel)
    }
}
