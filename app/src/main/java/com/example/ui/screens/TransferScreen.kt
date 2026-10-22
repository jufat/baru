package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransferRecord
import com.example.data.model.formatRupiah
import com.example.ui.theme.*
import com.example.ui.viewmodel.PosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferScreen(
    viewModel: PosViewModel,
    modifier: Modifier = Modifier
) {
    val transfers by viewModel.transfers.collectAsState()
    val summary by viewModel.dashboardSummary.collectAsState()

    var selectedServiceType by remember { mutableStateOf("Transfer Antar Bank") }
    var senderName by remember { mutableStateOf("") }
    var senderPhone by remember { mutableStateOf("") }
    var bankTarget by remember { mutableStateOf("Bank BRI") }
    var targetAccount by remember { mutableStateOf("") }
    var recipientName by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("500000") }
    var adminBankStr by remember { mutableStateOf("2500") }
    var counterFeeStr by remember { mutableStateOf("5000") }

    var expandedBankDropdown by remember { mutableStateOf(false) }

    val serviceTypes = listOf(
        "Transfer Antar Bank",
        "Transfer Sesama Bank",
        "Tarik Tunai",
        "Setor Tunai",
        "Top Up E-Wallet",
        "Pembayaran BRIVA/VA"
    )

    val banks = listOf(
        "Bank BRI", "Bank BCA", "Bank Mandiri", "Bank BNI",
        "DANA", "ShopeePay", "GoPay", "OVO", "LinkAja"
    )

    val amount = amountStr.toLongOrNull() ?: 0L
    val adminBank = adminBankStr.toLongOrNull() ?: 0L
    val counterFee = counterFeeStr.toLongOrNull() ?: 0L
    val totalReceived = amount + adminBank + counterFee

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Title Bar
        Text(
            text = "Transaksi BRILink & Transfer Bank Baru",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = SlateDark
        )
        Text(
            text = "Layanan transfer bank, tarik tunai, top up e-wallet dan pembayaran instan",
            fontSize = 12.sp,
            color = SlateLight
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Top Stats Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
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
                        Text("TOTAL JASA KONTER HARI INI", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateLight)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(formatRupiah(summary.todayCounterFeeProfit), fontSize = 18.sp, fontWeight = FontWeight.Black, color = EmeraldDark)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("✓ Laba bersih Ju-Fat Cell dari biaya jasa", fontSize = 10.sp, color = SuccessGreen)
                    }
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(EmeraldContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = EmeraldDark, modifier = Modifier.size(20.dp))
                    }
                }
            }

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
                        Text("VOLUME TRANSAKSI TRANSFER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateLight)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${summary.transferCountToday} Transaksi", fontSize = 18.sp, fontWeight = FontWeight.Black, color = SlateDark)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Total Nominal: ${formatRupiah(summary.totalTransferVolume)}", fontSize = 10.sp, color = SlateLight)
                    }
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(InfoBlueBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Sync, contentDescription = null, tint = InfoBlue, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main Layout: Form and Service Selector side-by-side or stacked
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left Form
            Surface(
                modifier = Modifier.weight(1.5f),
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
                        Text("Formulir Transaksi Transfer & Agen", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SlateDark)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = InfoBlueBg
                        ) {
                            Text(
                                selectedServiceType,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = InfoBlue,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = senderName,
                            onValueChange = { senderName = it },
                            label = { Text("Nama Pengirim *", fontSize = 11.sp) },
                            placeholder = { Text("Nama konsumen di konter", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        OutlinedTextField(
                            value = senderPhone,
                            onValueChange = { senderPhone = it },
                            label = { Text("No HP / WA Pengirim", fontSize = 11.sp) },
                            placeholder = { Text("0812xxxx (opsional)", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Dropdown Bank
                        ExposedDropdownMenuBox(
                            expanded = expandedBankDropdown,
                            onExpandedChange = { expandedBankDropdown = !expandedBankDropdown },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = bankTarget,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Bank / E-Wallet Tujuan *", fontSize = 11.sp) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBankDropdown) },
                                modifier = Modifier.menuAnchor(),
                                shape = RoundedCornerShape(8.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = expandedBankDropdown,
                                onDismissRequest = { expandedBankDropdown = false }
                            ) {
                                banks.forEach { bank ->
                                    DropdownMenuItem(
                                        text = { Text(bank, fontSize = 12.sp) },
                                        onClick = {
                                            bankTarget = bank
                                            expandedBankDropdown = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = targetAccount,
                            onValueChange = { targetAccount = it },
                            label = { Text("Nomor Rekening / No E-Wallet *", fontSize = 11.sp) },
                            placeholder = { Text("Contoh: 012901048892501", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = recipientName,
                        onValueChange = { recipientName = it },
                        label = { Text("Nama Penerima / Pemilik Rekening *", fontSize = 11.sp) },
                        placeholder = { Text("Contoh: Budi Santoso (Pastikan sesuai rekening)", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = amountStr,
                            onValueChange = { amountStr = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Nominal Transfer (Rp) *", fontSize = 10.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1.3f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        OutlinedTextField(
                            value = adminBankStr,
                            onValueChange = { adminBankStr = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Admin Bank (Modal)", fontSize = 10.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        OutlinedTextField(
                            value = counterFeeStr,
                            onValueChange = { counterFeeStr = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Jasa Konter (Laba)", fontSize = 10.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Calculation breakdown Box
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = BackgroundLight,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Nominal Pokok:", fontSize = 11.sp, color = SlateLight)
                                Text(formatRupiah(amount), fontSize = 11.sp, color = SlateDark)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Biaya Jasa Konter:", fontSize = 11.sp, color = SlateLight)
                                Text("+${formatRupiah(counterFee)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Biaya Admin Bank:", fontSize = 11.sp, color = SlateLight)
                                Text("+${formatRupiah(adminBank)}", fontSize = 11.sp, color = SlateDark)
                            }
                            Divider(modifier = Modifier.padding(vertical = 6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("TOTAL DITERIMA DARI KONSUMEN:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                                Text(formatRupiah(totalReceived), fontSize = 16.sp, fontWeight = FontWeight.Black, color = EmeraldDark)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (amount > 0 && targetAccount.isNotBlank()) {
                                viewModel.processTransfer(
                                    serviceType = selectedServiceType,
                                    senderName = senderName.ifBlank { "Konsumen Konter" },
                                    senderPhone = senderPhone,
                                    bankTarget = bankTarget,
                                    targetAccount = targetAccount,
                                    recipientName = recipientName.ifBlank { "Penerima" },
                                    transferAmount = amount,
                                    adminBank = adminBank,
                                    counterFee = counterFee
                                )
                                targetAccount = ""
                                recipientName = ""
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SlateDark),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Proses Transfer & Cetak Struk Bukti Kirim", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            // Right: Service Type Selector & Auto note
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Pilih Jenis Transaksi Agen", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SlateDark)
                        Text("Pilihan mutasi kas otomatis untuk layanan perbankan konter", fontSize = 10.sp, color = SlateLight)

                        Spacer(modifier = Modifier.height(10.dp))

                        serviceTypes.forEach { type ->
                            val isSel = selectedServiceType == type
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) SlateDark else BackgroundLight,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) SlateDark else CardBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clickable { selectedServiceType = type }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (type.contains("Transfer")) Icons.Default.Send else if (type.contains("Tarik")) Icons.Default.CallReceived else Icons.Default.AccountBalanceWallet,
                                        contentDescription = null,
                                        tint = if (isSel) Color.White else EmeraldDark,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        type,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSel) Color.White else SlateDark
                                    )
                                }
                            }
                        }
                    }
                }

                // Automation Notice card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SuccessGreenBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.Verified,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Otomatisasi Arus Kas & Saldo Modal", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "Ketika Anda memproses Transfer Keluar, keuntungan biaya jasa konter otomatis masuk ke Buku Kas Harian, memastikan pembukuan toko selalu seimbang.",
                                fontSize = 10.sp,
                                color = SlateMedium,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // History Table
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Riwayat Mutasi Transfer Agen Terakhir",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = SlateDark,
                    modifier = Modifier.padding(16.dp)
                )

                Divider(color = CardBorder)

                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundLight)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("NO. TRANSAKSI", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateLight, modifier = Modifier.weight(1.2f))
                    Text("WAKTU", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateLight, modifier = Modifier.weight(1f))
                    Text("LAYANAN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateLight, modifier = Modifier.weight(1.1f))
                    Text("BANK / TUJUAN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateLight, modifier = Modifier.weight(1.3f))
                    Text("PENERIMA", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateLight, modifier = Modifier.weight(1.1f))
                    Text("NOMINAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateLight, modifier = Modifier.weight(1.1f))
                    Text("JASA KONTER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateLight, modifier = Modifier.weight(1f))
                }

                Divider(color = CardBorder)

                transfers.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(item.id, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateDark, modifier = Modifier.weight(1.2f))
                        Text(item.timeFormatted, fontSize = 10.sp, color = SlateLight, modifier = Modifier.weight(1f))
                        Text(item.serviceType, fontSize = 11.sp, color = SlateDark, modifier = Modifier.weight(1.1f))
                        Text("${item.bankTarget} - ${item.targetAccount}", fontSize = 10.sp, color = SlateMedium, modifier = Modifier.weight(1.3f))
                        Text(item.recipientName, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = SlateDark, modifier = Modifier.weight(1.1f))
                        Text(formatRupiah(item.transferAmount), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateDark, modifier = Modifier.weight(1.1f))
                        Text("+${formatRupiah(item.counterFee)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SuccessGreen, modifier = Modifier.weight(1f))
                    }
                    Divider(color = Color(0xFFF1F5F9))
                }
            }
        }
    }
}
