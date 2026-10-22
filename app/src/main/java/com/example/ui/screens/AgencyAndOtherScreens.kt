package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.PosViewModel

// ----------------- SERVICE HP SCREEN -----------------
@Composable
fun ServiceHpScreen(
    viewModel: PosViewModel,
    modifier: Modifier = Modifier
) {
    val serviceList by viewModel.serviceHpList.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Layanan & Antrean Service HP", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                Text("Penerimaan unit servis, estimasi biaya, DP dan status pengerjaan", fontSize = 12.sp, color = SlateLight)
            }
            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Terima Servis Baru", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(serviceList, key = { it.id }) { item ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(InfoBlueBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = InfoBlue, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(item.deviceModel, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SlateDark)
                                    Text("${item.id} • a.n ${item.customerName} (${item.phone})", fontSize = 11.sp, color = SlateLight)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when (item.status) {
                                    ServiceStatus.ANTREAN -> WarningAmberBg
                                    ServiceStatus.SEDANG_DIKERJAKAN -> InfoBlueBg
                                    ServiceStatus.SELESAI -> SuccessGreenBg
                                    ServiceStatus.SUDAH_DIAMBIL -> BackgroundLight
                                }
                            ) {
                                Text(
                                    item.status.label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (item.status) {
                                        ServiceStatus.ANTREAN -> Color(0xFFD97706)
                                        ServiceStatus.SEDANG_DIKERJAKAN -> InfoBlue
                                        ServiceStatus.SELESAI -> SuccessGreen
                                        ServiceStatus.SUDAH_DIAMBIL -> SlateMedium
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Keluhan: ${item.problem}", fontSize = 12.sp, color = SlateMedium)
                        if (item.technicianNote.isNotBlank()) {
                            Text("Catatan Teknisi: ${item.technicianNote}", fontSize = 11.sp, color = SlateLight)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = Color(0xFFF1F5F9))
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Estimasi: ${formatRupiah(item.estimatedCost)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                                Text("DP Diterima: ${formatRupiah(item.dpAmount)}", fontSize = 10.sp, color = SuccessGreen)
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (item.status == ServiceStatus.ANTREAN) {
                                    Button(
                                        onClick = { viewModel.updateServiceStatus(item.id, ServiceStatus.SEDANG_DIKERJAKAN) },
                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = InfoBlue),
                                        modifier = Modifier.height(32.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                    ) {
                                        Text("Kerjakan", fontSize = 11.sp)
                                    }
                                }
                                if (item.status == ServiceStatus.SEDANG_DIKERJAKAN) {
                                    Button(
                                        onClick = { viewModel.updateServiceStatus(item.id, ServiceStatus.SELESAI) },
                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                        modifier = Modifier.height(32.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                    ) {
                                        Text("Tandai Selesai", fontSize = 11.sp)
                                    }
                                }
                                if (item.status == ServiceStatus.SELESAI) {
                                    Button(
                                        onClick = { viewModel.updateServiceStatus(item.id, ServiceStatus.SUDAH_DIAMBIL) },
                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                                        modifier = Modifier.height(32.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                    ) {
                                        Text("Unit Diambil", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddServiceDialog(
            onDismiss = { showAddDialog = false },
            onSave = { name, phone, device, problem, est, dp ->
                viewModel.addServiceHp(name, phone, device, problem, est, dp)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AddServiceDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, phone: String, device: String, problem: String, est: Long, dp: Long) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var device by remember { mutableStateOf("") }
    var problem by remember { mutableStateOf("") }
    var estCostStr by remember { mutableStateOf("150000") }
    var dpStr by remember { mutableStateOf("50000") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier.widthIn(max = 420.dp).fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Terima Servis HP Baru", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SlateDark)
                Spacer(modifier = Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Pelanggan") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp))
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("No WhatsApp") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(value = device, onValueChange = { device = it }, label = { Text("Tipe / Model HP (Contoh: Redmi Note 10)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(value = problem, onValueChange = { problem = it }, label = { Text("Keluhan Kerusakan") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = estCostStr, onValueChange = { estCostStr = it.filter { ch -> ch.isDigit() } }, label = { Text("Estimasi Biaya (Rp)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp))
                    OutlinedTextField(value = dpStr, onValueChange = { dpStr = it.filter { ch -> ch.isDigit() } }, label = { Text("Uang Muka / DP (Rp)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp))
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)) { Text("Batal") }
                    Button(
                        onClick = {
                            if (name.isNotBlank() && device.isNotBlank()) {
                                onSave(name, phone, device, problem, estCostStr.toLongOrNull() ?: 0L, dpStr.toLongOrNull() ?: 0L)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("Simpan") }
                }
            }
        }
    }
}

// ----------------- PULSA & PPOB SCREEN -----------------
@Composable
fun PulsaPpobScreen(
    viewModel: PosViewModel,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("Pulsa & Data") }
    var targetNumber by remember { mutableStateOf("") }
    var selectedItemName by remember { mutableStateOf("Telkomsel 25.000") }
    var costPrice by remember { mutableStateOf(25200L) }
    var sellingPrice by remember { mutableStateOf(27000L) }

    val ppobPackages = listOf(
        Triple("Telkomsel 10.000", 10200L, 12000L),
        Triple("Telkomsel 25.000", 25200L, 27000L),
        Triple("Telkomsel 50.000", 49800L, 52000L),
        Triple("Indosat Freedom 8GB", 24000L, 30000L),
        Triple("XL Xtra Combo 15GB", 38000L, 45000L),
        Triple("Token Listrik PLN 50.000", 50000L, 53000L),
        Triple("Token Listrik PLN 100.000", 100000L, 103000L),
        Triple("Tagihan PDAM Air", 45000L, 48000L),
        Triple("Iuran BPJS Kesehatan", 70000L, 73000L)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Layanan Pulsa, Paket Data & PPOB", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SlateDark)
        Text("Transaksi pulsa, paket kuota, token PLN, PDAM dan tagihan otomatis dengan margin langsung ke kas", fontSize = 12.sp, color = SlateLight)

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = targetNumber,
                    onValueChange = { targetNumber = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Nomor HP / No Meter Listrik / No Pelanggan") },
                    placeholder = { Text("Contoh: 081234567890 / 1402910291") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))
                Text("Pilih Produk PPOB:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SlateDark)
                Spacer(modifier = Modifier.height(8.dp))

                ppobPackages.forEach { (name, cost, sell) ->
                    val isSel = selectedItemName == name
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSel) EmeraldContainer else BackgroundLight,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) EmeraldDark else CardBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                selectedItemName = name
                                costPrice = cost
                                sellingPrice = sell
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(name, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium, fontSize = 12.sp, color = SlateDark)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Modal: ${formatRupiah(cost)}", fontSize = 11.sp, color = SlateLight)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(formatRupiah(sell), fontWeight = FontWeight.Black, fontSize = 13.sp, color = EmeraldDark)
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(shape = RoundedCornerShape(4.dp), color = SuccessGreenBg) {
                                    Text("+${formatRupiah(sell - cost)}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SuccessGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (targetNumber.isNotBlank()) {
                            viewModel.processPpob(selectedCategory, targetNumber, selectedItemName, costPrice, sellingPrice)
                            targetNumber = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MagentaPink),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.FlashOn, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Proses Transaksi PPOB & Masukkan Laba ke Kas", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ----------------- PERCETAKAN & SPK SCREEN -----------------
@Composable
fun PercetakanScreen(
    viewModel: PosViewModel,
    modifier: Modifier = Modifier
) {
    var docType by remember { mutableStateOf("Cetak Dokumen Hitam Putih") }
    var customerName by remember { mutableStateOf("") }
    var qtyStr by remember { mutableStateOf("10") }
    var pricePerUnitStr by remember { mutableStateOf("500") }
    var notes by remember { mutableStateOf("Tugas Kuliah / Makalah") }

    val qty = qtyStr.toLongOrNull() ?: 1L
    val pricePerUnit = pricePerUnitStr.toLongOrNull() ?: 500L
    val total = qty * pricePerUnit

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Percetakan & Surat Perintah Kerja (SPK)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SlateDark)
        Text("Layanan cetak dokumen, jilid skripsi, spanduk/banner, foto dan fotocopy", fontSize = 12.sp, color = SlateLight)

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Pilih Jenis Layanan Cetak:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SlateDark)
                Spacer(modifier = Modifier.height(8.dp))

                listOf(
                    Pair("Cetak Dokumen Hitam Putih", 500L),
                    Pair("Cetak Dokumen Warna / Full", 1500L),
                    Pair("Jilid Skripsi / Mika Lakban", 6000L),
                    Pair("Cetak Spanduk / Banner (per meter)", 25000L),
                    Pair("Laminating Dokumen Presisi", 4000L),
                    Pair("Fotocopy KTP / Berkas", 300L)
                ).forEach { (type, price) ->
                    val isSel = docType == type
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSel) EmeraldContainer else BackgroundLight,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) EmeraldDark else CardBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clickable {
                                docType = type
                                pricePerUnitStr = price.toString()
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(type, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium, fontSize = 12.sp, color = SlateDark)
                            Text(formatRupiah(price), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = EmeraldDark)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = customerName, onValueChange = { customerName = it }, label = { Text("Nama Pelanggan") }, modifier = Modifier.weight(1.5f), shape = RoundedCornerShape(8.dp))
                    OutlinedTextField(value = qtyStr, onValueChange = { qtyStr = it.filter { ch -> ch.isDigit() } }, label = { Text("Jumlah / Lembar") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Catatan / Keterangan Order") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))

                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BackgroundLight,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("TOTAL BIAYA CETAK:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SlateDark)
                        Text(formatRupiah(total), fontWeight = FontWeight.Black, fontSize = 18.sp, color = EmeraldDark)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (total > 0) {
                            viewModel.processPrinting(docType, customerName.ifBlank { "Pelanggan" }, total, notes)
                            customerName = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Print, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cetak SPK & Catat Kas Masuk", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ----------------- CATATAN HUTANG / KASBON SCREEN -----------------
@Composable
fun PiutangScreen(
    viewModel: PosViewModel,
    modifier: Modifier = Modifier
) {
    val debts by viewModel.debts.collectAsState()
    var selectedDebtToPay by remember { mutableStateOf<DebtRecord?>(null) }
    var payAmountStr by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Buku Piutang & Kasbon Pelanggan", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SlateDark)
        Text("Pantau pinjaman / hutang barang belanjaan pelanggan beserta tanggal jatuh tempo", fontSize = 12.sp, color = SlateLight)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(debts, key = { it.id }) { debt ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(debt.customerName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SlateDark)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(debt.phone, fontSize = 11.sp, color = SlateLight)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Catatan: ${debt.notes}", fontSize = 11.sp, color = SlateMedium)
                            Text("Jatuh tempo: ${debt.dueDate}", fontSize = 10.sp, color = DangerRed)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(formatRupiah(debt.remainingDebt), fontWeight = FontWeight.Black, fontSize = 15.sp, color = if (debt.isPaidOff) SuccessGreen else DangerRed)
                            Spacer(modifier = Modifier.height(4.dp))
                            if (!debt.isPaidOff) {
                                Button(
                                    onClick = {
                                        selectedDebtToPay = debt
                                        payAmountStr = debt.remainingDebt.toString()
                                    },
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = WarningAmber),
                                    modifier = Modifier.height(32.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                ) {
                                    Text("Bayar Kasbon", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Surface(shape = RoundedCornerShape(4.dp), color = SuccessGreenBg) {
                                    Text("LUNAS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SuccessGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    selectedDebtToPay?.let { debt ->
        Dialog(onDismissRequest = { selectedDebtToPay = null }) {
            Surface(shape = RoundedCornerShape(16.dp), color = Color.White, modifier = Modifier.widthIn(max = 380.dp).fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Pelunasan Kasbon", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SlateDark)
                    Text("Pelanggan: ${debt.customerName}", fontSize = 12.sp, color = SlateLight)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = payAmountStr,
                        onValueChange = { payAmountStr = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Nominal Pelunasan (Rp)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(onClick = { selectedDebtToPay = null }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)) { Text("Batal") }
                        Button(
                            onClick = {
                                val amount = payAmountStr.toLongOrNull() ?: 0L
                                if (amount > 0) {
                                    viewModel.payDebt(debt.id, amount)
                                    selectedDebtToPay = null
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                            shape = RoundedCornerShape(8.dp)
                        ) { Text("Konfirmasi") }
                    }
                }
            }
        }
    }
}

// ----------------- SISTEM & DATABASE SCREEN -----------------
@Composable
fun DatabaseSyncScreen(
    viewModel: PosViewModel,
    modifier: Modifier = Modifier
) {
    val syncStatus by viewModel.syncStatus.collectAsState()
    val isSyncing by viewModel.isCloudSyncing.collectAsState()
    val session by viewModel.session.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Sistem & Sinkronisasi Database", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SlateDark)
        Text("Manajemen sinkronisasi cloud live gratis berbasis web, hak akses Owner/Kasir dan keamanan data", fontSize = 12.sp, color = SlateLight)

        // Cloud live card
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SuccessGreenBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CloudDone, contentDescription = null, tint = SuccessGreen)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Database Utama (Live Web Sync)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SlateDark)
                            Text(syncStatus, fontSize = 11.sp, color = SuccessGreen)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = SuccessGreenBg
                    ) {
                        Text("GRATIS / AKTIF", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SuccessGreen, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    "Operasional toko aman dan dapat diakses kapan saja secara real-time. Setiap transaksi yang dibuat oleh kasir maupun owner otomatis tersinkronisasi dan memiliki catatan audit lengkap.",
                    fontSize = 12.sp,
                    color = SlateMedium,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { viewModel.setShowExcelDialog(true) },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Backup / Export Data Excel (CSV)", fontSize = 12.sp)
                    }
                }
            }
        }

        // Security & Access Control Card
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Batasan Hak Akses: Admin (Owner) vs Kasir", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SlateDark)
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(shape = RoundedCornerShape(8.dp), color = BackgroundLight, modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("👑 Owner (Hak Penuh)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MagentaPink)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("• Akses seluruh laporan keuangan & HPP\n• Manajemen harga modal & margin\n• Tambah, ubah, dan hapus stok\n• Tarik modal kas fisik laci\n• Dilindungi PIN Keamanan (1234)", fontSize = 11.sp, color = SlateMedium, lineHeight = 16.sp)
                        }
                    }

                    Surface(shape = RoundedCornerShape(8.dp), color = BackgroundLight, modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("👤 Kasir / Anggota (Akses Terbatas)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = EmeraldDark)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("• Hanya akses transaksi kasir & jasa\n• Tidak bisa melihat harga modal / laba\n• Tidak bisa menghapus riwayat\n• Setiap transaksi mencatat nama & shift\n• Pembatasan otomatis dengan PIN", fontSize = 11.sp, color = SlateMedium, lineHeight = 16.sp)
                        }
                    }
                }
            }
        }
    }
}
