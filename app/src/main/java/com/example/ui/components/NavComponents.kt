package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Role
import com.example.data.model.Shift
import com.example.data.model.UserSession
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen

@Composable
fun AppSidebar(
    currentScreen: AppScreen,
    session: UserSession,
    onNavigate: (AppScreen) -> Unit,
    onSwitchRole: (Role) -> Unit,
    onSwitchShift: (Shift) -> Unit,
    onOpenConnectPc: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showUserMenu by remember { mutableStateOf(false) }

    Surface(
        color = EmeraldDark,
        modifier = modifier
            .fillMaxHeight()
            .width(260.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp, horizontal = 12.dp)
        ) {
            // Brand Logo & Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "JF",
                        color = MagentaPink,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "JU-FAT CELL",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Modern POS & Agency",
                        color = Color(0xFF80CBC4),
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Navigation Items Scrollable List
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // Section 1: MENU OPERASIONAL
                Text(
                    text = "MENU OPERASIONAL",
                    color = Color(0xFF80CBC4),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )

                SidebarMenuItem(
                    title = "Kasir (Penjualan)",
                    icon = Icons.Default.ShoppingCart,
                    isSelected = currentScreen == AppScreen.KASIR,
                    activeColor = MagentaPink,
                    testTag = "menu_kasir",
                    onClick = { onNavigate(AppScreen.KASIR) }
                )

                SidebarMenuItem(
                    title = "Percetakan & SPK",
                    icon = Icons.Default.Print,
                    badge = "4",
                    isSelected = currentScreen == AppScreen.PERCETAKAN,
                    testTag = "menu_percetakan",
                    onClick = { onNavigate(AppScreen.PERCETAKAN) }
                )

                SidebarMenuItem(
                    title = "Transfer / E-Wallet",
                    icon = Icons.Default.AccountBalance,
                    isSelected = currentScreen == AppScreen.TRANSFER,
                    testTag = "menu_transfer",
                    onClick = { onNavigate(AppScreen.TRANSFER) }
                )

                SidebarMenuItem(
                    title = "Pulsa / PPOB",
                    icon = Icons.Default.FlashOn,
                    isSelected = currentScreen == AppScreen.PPOB,
                    testTag = "menu_ppob",
                    onClick = { onNavigate(AppScreen.PPOB) }
                )

                SidebarMenuItem(
                    title = "Service HP",
                    icon = Icons.Default.Build,
                    isSelected = currentScreen == AppScreen.SERVICE_HP,
                    testTag = "menu_service_hp",
                    onClick = { onNavigate(AppScreen.SERVICE_HP) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Section 2: KHUSUS OWNER
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "KHUSUS OWNER",
                        color = Color(0xFF80CBC4),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    if (session.role != Role.OWNER) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Terkunci",
                            tint = Color(0xFFFFB74D),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                SidebarMenuItem(
                    title = "Dashboard Analitik",
                    icon = Icons.Default.Dashboard,
                    isRestricted = session.role != Role.OWNER,
                    isSelected = currentScreen == AppScreen.DASHBOARD,
                    testTag = "menu_dashboard",
                    onClick = { onNavigate(AppScreen.DASHBOARD) }
                )

                SidebarMenuItem(
                    title = "Kas Harian & Mutasi",
                    icon = Icons.Default.AccountBalanceWallet,
                    isRestricted = session.role != Role.OWNER,
                    isSelected = currentScreen == AppScreen.KAS_HARIAN,
                    testTag = "menu_kas_harian",
                    onClick = { onNavigate(AppScreen.KAS_HARIAN) }
                )

                SidebarMenuItem(
                    title = "Manajemen Stok",
                    icon = Icons.Default.Inventory2,
                    badge = "2",
                    isRestricted = session.role != Role.OWNER,
                    isSelected = currentScreen == AppScreen.MANAJEMEN_STOK,
                    testTag = "menu_manajemen_stok",
                    onClick = { onNavigate(AppScreen.MANAJEMEN_STOK) }
                )

                SidebarMenuItem(
                    title = "Catatan Hutang",
                    icon = Icons.Default.MonetizationOn,
                    isRestricted = session.role != Role.OWNER,
                    isSelected = currentScreen == AppScreen.PIUTANG,
                    testTag = "menu_piutang",
                    onClick = { onNavigate(AppScreen.PIUTANG) }
                )

                SidebarMenuItem(
                    title = "Retur Barang",
                    icon = Icons.Default.Autorenew,
                    isRestricted = session.role != Role.OWNER,
                    isSelected = currentScreen == AppScreen.RETUR,
                    testTag = "menu_retur",
                    onClick = { onNavigate(AppScreen.RETUR) }
                )

                SidebarMenuItem(
                    title = "Sistem & Database",
                    icon = Icons.Default.CloudSync,
                    isRestricted = session.role != Role.OWNER,
                    isSelected = currentScreen == AppScreen.SISTEM_DATABASE,
                    testTag = "menu_database",
                    onClick = { onNavigate(AppScreen.SISTEM_DATABASE) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Hubungkan HP (Scan QR) button matching screenshot
            Surface(
                color = Color(0xFF0F574E),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenConnectPc() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = "Hubungkan HP",
                        tint = Color(0xFF80CBC4),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Hubungkan HP (Scan QR)",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Profile Box
            Surface(
                color = Color(0xFF093933),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showUserMenu = !showUserMenu }
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (session.role == Role.OWNER) MagentaPink else SuccessGreen)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = session.name,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = session.shift.label,
                                    color = Color(0xFF80CBC4),
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (session.role == Role.OWNER) MagentaPink else Color(0xFF135D54)
                        ) {
                            Text(
                                text = if (session.role == Role.OWNER) "OWNER" else "KASIR",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    AnimatedVisibility(visible = showUserMenu) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            Divider(color = Color(0xFF135D54), thickness = 1.dp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Ganti Hak Akses:",
                                fontSize = 10.sp,
                                color = Color(0xFF80CBC4)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = {
                                        onSwitchRole(Role.OWNER)
                                        showUserMenu = false
                                    },
                                    modifier = Modifier.weight(1f).height(32.dp),
                                    contentPadding = PaddingValues(0.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (session.role == Role.OWNER) MagentaPink else Color(0xFF135D54)
                                    ),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("Owner", fontSize = 10.sp)
                                }
                                Button(
                                    onClick = {
                                        onSwitchRole(Role.CASHIER)
                                        showUserMenu = false
                                    },
                                    modifier = Modifier.weight(1f).height(32.dp),
                                    contentPadding = PaddingValues(0.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (session.role == Role.CASHIER) MagentaPink else Color(0xFF135D54)
                                    ),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("Kasir", fontSize = 10.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        onSwitchShift(Shift.SHIFT_1)
                                        showUserMenu = false
                                    },
                                    modifier = Modifier.weight(1f).height(30.dp),
                                    contentPadding = PaddingValues(0.dp),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("Shift 1", fontSize = 9.sp, color = Color.White)
                                }
                                OutlinedButton(
                                    onClick = {
                                        onSwitchShift(Shift.SHIFT_2)
                                        showUserMenu = false
                                    },
                                    modifier = Modifier.weight(1f).height(30.dp),
                                    contentPadding = PaddingValues(0.dp),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("Shift 2", fontSize = 9.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SidebarMenuItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    badge: String? = null,
    isRestricted: Boolean = false,
    activeColor: Color = MagentaPink,
    testTag: String = "",
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) activeColor else Color.Transparent
    val contentColor = if (isSelected) Color.White else Color(0xFFD1E7E4)

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = contentColor,
                modifier = Modifier.size(19.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                color = contentColor,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.weight(1f)
            )

            if (isRestricted) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Perlu PIN Owner",
                    tint = Color(0xFFFFA726),
                    modifier = Modifier.size(13.dp)
                )
            } else if (badge != null) {
                Surface(
                    shape = CircleShape,
                    color = if (isSelected) Color.White else MagentaPink,
                    modifier = Modifier.size(18.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = badge,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) activeColor else Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TopHeaderBar(
    session: UserSession,
    syncStatus: String,
    onOpenKasbon: () -> Unit,
    onOpenExcel: () -> Unit,
    onToggleRole: () -> Unit,
    onOpenConnectPc: () -> Unit = {},
    currentViewMode: com.example.ui.viewmodel.ViewMode = com.example.ui.viewmodel.ViewMode.PC_DESKTOP,
    onToggleViewMode: () -> Unit = {}
) {
    Surface(
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(EmeraldContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = "Toko",
                        tint = EmeraldDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "JU-FAT CELL",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = EmeraldDark
                    )
                    Text(
                        text = "Solusi Gadget, Pulsa, Service HP & Agen Bank",
                        fontSize = 10.sp,
                        color = SlateLight
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Hubungkan HP & PC button matching screenshot
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFE0F2F1),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF80CBC4)),
                    modifier = Modifier
                        .height(34.dp)
                        .clickable(onClick = onOpenConnectPc)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = EmeraldDark,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Hubungkan HP & PC",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldDark
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(SuccessGreen)
                        )
                    }
                }

                // Fast View Mode Switcher Pill (PC vs HP)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (currentViewMode == com.example.ui.viewmodel.ViewMode.PC_DESKTOP) MagentaContainer else EmeraldContainer,
                    modifier = Modifier
                        .height(34.dp)
                        .clickable(onClick = onToggleViewMode)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (currentViewMode == com.example.ui.viewmodel.ViewMode.PC_DESKTOP) Icons.Default.DesktopWindows else Icons.Default.PhoneAndroid,
                            contentDescription = null,
                            tint = if (currentViewMode == com.example.ui.viewmodel.ViewMode.PC_DESKTOP) MagentaDark else EmeraldDark,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (currentViewMode == com.example.ui.viewmodel.ViewMode.PC_DESKTOP) "Tampilan PC" else "Tampilan HP",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (currentViewMode == com.example.ui.viewmodel.ViewMode.PC_DESKTOP) MagentaDark else EmeraldDark
                        )
                    }
                }

                // Excel Import/Export shortcut
                OutlinedButton(
                    onClick = onOpenExcel,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = EmeraldDark
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Excel CSV", fontSize = 11.sp, color = EmeraldDark)
                }

                // Role switcher indicator button
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (session.role == Role.OWNER) MagentaContainer else EmeraldContainer,
                    modifier = Modifier
                        .height(34.dp)
                        .clickable(onClick = onToggleRole)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (session.role == Role.OWNER) Icons.Default.Shield else Icons.Default.Person,
                            contentDescription = null,
                            tint = if (session.role == Role.OWNER) MagentaDark else EmeraldDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (session.role == Role.OWNER) "Owner (Full)" else "Kasir (Shift)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (session.role == Role.OWNER) MagentaDark else EmeraldDark
                        )
                    }
                }
            }
        }
    }
}
