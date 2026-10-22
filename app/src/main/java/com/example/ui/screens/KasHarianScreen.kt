package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CashFlowType
import com.example.data.model.CashMovement
import com.example.data.model.formatRupiah
import com.example.ui.theme.*
import com.example.ui.viewmodel.PosViewModel

@Composable
fun KasHarianScreen(
    viewModel: PosViewModel,
    modifier: Modifier = Modifier
) {
    val cashMovements by viewModel.cashMovements.collectAsState()
    val summary by viewModel.dashboardSummary.collectAsState()
    var showAddCashDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Title and Actions Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Buku Arus Kas Harian & Mutasi",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateDark
                )
                Text(
                    text = "Pencatatan real-time otomatis dari transaksi POS dan pengeluaran operasional toko",
                    fontSize = 12.sp,
                    color = SlateLight
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = { viewModel.setShowExcelDialog(true) },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Unduh Laporan Kas (Excel/CSV)", fontSize = 12.sp)
                }

                Button(
                    onClick = { showAddCashDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Catat Kas Masuk / Keluar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Three Top Summary Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Kas Masuk Card
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TOTAL KAS FISIK MASUK",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateLight,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatRupiah(summary.totalCashIn, withPlusSign = true),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = SuccessGreen
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Penjualan, servis, cetak & pelunasan",
                            fontSize = 10.sp,
                            color = SlateLight
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SuccessGreenBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Kas Keluar Card
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TOTAL PENGELUARAN KAS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateLight,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatRupiah(-summary.totalCashOut),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = DangerRed
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Belanja stok, operasional, makan & beban",
                            fontSize = 10.sp,
                            color = SlateLight
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DangerRedBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.TrendingDown,
                            contentDescription = null,
                            tint = DangerRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Saldo Kas Fisik Bersih (Laci)
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = SlateDark
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SALDO KAS FISIK BERSIH (LACI)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8),
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatRupiah(summary.netCashInDrawer),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = if (summary.netCashInDrawer >= 0) Color(0xFFFBBF24) else DangerRed
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Total kas riil di toko saat ini",
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E293B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Table Card
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            modifier = Modifier.weight(1f)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Table Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundLight)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("WAKTU", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SlateLight, modifier = Modifier.weight(1.3f))
                    Text("ARUS", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SlateLight, modifier = Modifier.weight(0.9f))
                    Text("KATEGORI", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SlateLight, modifier = Modifier.weight(1.3f))
                    Text("KETERANGAN", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SlateLight, modifier = Modifier.weight(2.5f))
                    Text("NOMINAL", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SlateLight, modifier = Modifier.weight(1.1f))
                    Text("KASIR / SHIFT", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SlateLight, modifier = Modifier.weight(1.5f))
                }

                Divider(color = CardBorder)

                // Movements list
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(cashMovements, key = { it.id }) { item ->
                        CashMovementRow(item = item)
                        Divider(color = Color(0xFFF1F5F9))
                    }
                }
            }
        }
    }

    if (showAddCashDialog) {
        AddCashMovementDialog(
            onDismiss = { showAddCashDialog = false },
            onSave = { type, cat, amount, notes ->
                viewModel.addCashMovement(type, cat, amount, notes)
                showAddCashDialog = false
            }
        )
    }
}

@Composable
fun CashMovementRow(item: CashMovement) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Waktu
        Text(
            text = item.timeFormatted,
            fontSize = 11.sp,
            color = SlateLight,
            modifier = Modifier.weight(1.3f)
        )

        // Arus
        Box(modifier = Modifier.weight(0.9f)) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = if (item.flowType == CashFlowType.MASUK) SuccessGreenBg else DangerRedBg
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (item.flowType == CashFlowType.MASUK) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                        contentDescription = null,
                        tint = if (item.flowType == CashFlowType.MASUK) SuccessGreen else DangerRed,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = item.flowType.name,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.flowType == CashFlowType.MASUK) SuccessGreen else DangerRed
                    )
                }
            }
        }

        // Kategori
        Text(
            text = item.category,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = SlateDark,
            modifier = Modifier.weight(1.3f)
        )

        // Keterangan
        Text(
            text = item.notes,
            fontSize = 11.sp,
            color = SlateMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(2.5f)
        )

        // Nominal
        Text(
            text = formatRupiah(item.amount, withPlusSign = item.flowType == CashFlowType.MASUK),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (item.flowType == CashFlowType.MASUK) SuccessGreen else DangerRed,
            modifier = Modifier.weight(1.1f)
        )

        // Kasir
        Text(
            text = item.cashierInfo,
            fontSize = 11.sp,
            color = SlateLight,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1.5f)
        )
    }
}

@Composable
fun AddCashMovementDialog(
    onDismiss: () -> Unit,
    onSave: (type: CashFlowType, category: String, amount: Long, notes: String) -> Unit
) {
    var flowType by remember { mutableStateOf(CashFlowType.KELUAR) }
    var category by remember { mutableStateOf("Operasional Toko") }
    var amountStr by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val categoriesKeluar = listOf("Belanja Stok", "Makan & Konsumsi", "Operasional Toko", "Listrik & Internet", "Tarik Modal", "Lain-lain")
    val categoriesMasuk = listOf("Modal Kas Tambahan", "Pendapatan Lain", "Kasbon Pelunasan")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier.widthIn(max = 420.dp).fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Catat Arus Kas Fisik",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateDark
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Toggle Masuk / Keluar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            flowType = CashFlowType.KELUAR
                            category = categoriesKeluar.first()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (flowType == CashFlowType.KELUAR) DangerRed else BackgroundLight,
                            contentColor = if (flowType == CashFlowType.KELUAR) Color.White else SlateMedium
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Kas Keluar (Biaya)", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            flowType = CashFlowType.MASUK
                            category = categoriesMasuk.first()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (flowType == CashFlowType.MASUK) SuccessGreen else BackgroundLight,
                            contentColor = if (flowType == CashFlowType.MASUK) Color.White else SlateMedium
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Kas Masuk", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("Kategori:", fontSize = 12.sp, color = SlateLight, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))

                val availableCats = if (flowType == CashFlowType.KELUAR) categoriesKeluar else categoriesMasuk
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    availableCats.take(3).forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 10.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Nominal (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Keterangan Pengeluaran / Pemasukan") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Batal")
                    }

                    Button(
                        onClick = {
                            val amount = amountStr.toLongOrNull() ?: 0L
                            if (amount > 0 && notes.isNotBlank()) {
                                onSave(flowType, category, amount, notes)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (flowType == CashFlowType.MASUK) SuccessGreen else DangerRed
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Simpan Kas")
                    }
                }
            }
        }
    }
}
