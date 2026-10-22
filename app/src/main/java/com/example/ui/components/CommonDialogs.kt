package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.SalesTransaction
import com.example.data.model.formatRupiah
import com.example.ui.theme.*

@Composable
fun PinUnlockDialog(
    onDismiss: () -> Unit,
    onSubmitPin: (String) -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SurfaceLight,
            shadowElevation = 8.dp,
            modifier = Modifier.widthIn(max = 380.dp).fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(MagentaContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock",
                        tint = MagentaPink,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Akses Khusus Owner",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = SlateDark
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Menu ini dibatasi untuk melindungi data keuangan dan stok toko. Masukkan PIN Owner (Default: 1234).",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SlateLight,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = pin,
                    onValueChange = {
                        if (it.length <= 6) pin = it
                        errorMessage = null
                    },
                    label = { Text("PIN Owner") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = DangerRed,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Batal")
                    }

                    Button(
                        onClick = {
                            if (pin.isBlank()) {
                                errorMessage = "Masukkan PIN terlebih dahulu"
                            } else {
                                onSubmitPin(pin)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Buka Kunci")
                    }
                }
            }
        }
    }
}

@Composable
fun ReceiptDialog(
    transaction: SalesTransaction,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFFAFAFA),
            shadowElevation = 10.dp,
            modifier = Modifier
                .widthIn(max = 380.dp)
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Receipt Header
                Text(
                    text = "JU-FAT CELL",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = EmeraldDark
                )
                Text(
                    text = "Solusi Gadget, ATK, Pulsa & Agen Bank",
                    fontSize = 11.sp,
                    color = SlateLight
                )
                Text(
                    text = "WhatsApp: 0812-xxxx-xxxx",
                    fontSize = 11.sp,
                    color = SlateLight
                )

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = Color(0xFFD1D5DB), thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("No. Nota:", fontSize = 11.sp, color = SlateLight)
                    Text(transaction.id, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Waktu:", fontSize = 11.sp, color = SlateLight)
                    Text(transaction.timeFormatted, fontSize = 11.sp, color = SlateDark)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Kasir:", fontSize = 11.sp, color = SlateLight)
                    Text(transaction.cashier, fontSize = 11.sp, color = SlateDark)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Shift:", fontSize = 11.sp, color = SlateLight)
                    Text(transaction.shift, fontSize = 11.sp, color = SlateDark)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = Color(0xFFD1D5DB), thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))

                // Items list
                Text(
                    text = transaction.itemsSummary,
                    fontSize = 12.sp,
                    color = SlateDark,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = Color(0xFFD1D5DB), thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("TOTAL BAYAR:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                    Text(formatRupiah(transaction.totalAmount), fontSize = 14.sp, fontWeight = FontWeight.Black, color = EmeraldDark)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Metode Pembayaran:", fontSize = 11.sp, color = SlateLight)
                    Text(transaction.paymentMethod, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = SlateDark)
                }
                if (transaction.paymentMethod == "Tunai") {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tunai Diterima:", fontSize = 11.sp, color = SlateLight)
                        Text(formatRupiah(transaction.cashGiven), fontSize = 11.sp, color = SlateDark)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Kembalian:", fontSize = 11.sp, color = SlateLight)
                        Text(formatRupiah(transaction.change), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "*** TERIMA KASIH ATAS KUNJUNGAN ANDA ***",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = SlateLight,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Barang yang sudah dibeli dapat ditukar jika ada bukti struk",
                    fontSize = 9.sp,
                    color = SlateLight,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Struk POS", "${transaction.id}\nTotal: ${formatRupiah(transaction.totalAmount)}\n${transaction.itemsSummary}")
                            clipboard.setPrimaryClip(clip)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Salin Teks", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MagentaPink),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Selesai", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ExcelImportExportDialog(
    onDismiss: () -> Unit,
    onImportCsv: (String) -> Unit,
    getExportProductsCsv: () -> String,
    getExportCashCsv: () -> String
) {
    val context = LocalContext.current
    var inputCsv by remember {
        mutableStateOf(
            "SKU,Nama Produk,Kategori,Modal (HPP),Harga Jual,Stok\n" +
            "ATK-NEW-01,Spidol Whiteboard Snowman,Alat Tulis Kantor,6000,9000,24\n" +
            "ATK-NEW-02,Map Folio Kancing Plastik,Alat Tulis Kantor,2500,4000,50\n" +
            "ACC-NEW-03,Kabel Micro USB 2A Joyseus,Kabel & Charger,10000,18000,15"
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SurfaceLight,
            shadowElevation = 8.dp,
            modifier = Modifier
                .widthIn(max = 500.dp)
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SuccessGreenBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.FileDownload, contentDescription = null, tint = SuccessGreen)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Impor & Ekspor Data Excel", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SlateDark)
                            Text("Format Tabel CSV / Excel", fontSize = 12.sp, color = SlateLight)
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Impor Produk dari Excel (Paste Format CSV):",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = SlateDark
                )
                Text(
                    text = "Kolom: SKU, Nama Produk, Kategori, Modal, Jual, Stok",
                    fontSize = 11.sp,
                    color = SlateLight
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = inputCsv,
                    onValueChange = { inputCsv = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    shape = RoundedCornerShape(10.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { onImportCsv(inputCsv) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.UploadFile, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Proses Impor Produk ke Database Toko")
                }

                Spacer(modifier = Modifier.height(20.dp))
                Divider()
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Ekspor Laporan ke Excel / CSV:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = SlateDark
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val data = getExportProductsCsv()
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Katalog Produk CSV", data))
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Salin CSV Stok", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            val data = getExportCashCsv()
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Kas Harian CSV", data))
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Salin CSV Kas", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ConnectPcDialog(
    currentMode: com.example.ui.viewmodel.ViewMode,
    onSelectMode: (com.example.ui.viewmodel.ViewMode) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val webUrl = "https://ais-dev-qmvexguh75i4co3qce6sej-845273421109.asia-east1.run.app"

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SurfaceLight,
            shadowElevation = 8.dp,
            modifier = Modifier
                .widthIn(max = 440.dp)
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(EmeraldContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Computer,
                                contentDescription = null,
                                tint = EmeraldDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "Hubungkan HP & PC",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = SlateDark
                            )
                            Text(
                                "Akses Kasir dari Browser Komputer Toko",
                                fontSize = 11.sp,
                                color = SlateLight
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Simulated QR Code Box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BackgroundLight,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    modifier = Modifier.size(160.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.QrCode2,
                            contentDescription = "QR Code",
                            tint = EmeraldDark,
                            modifier = Modifier.size(100.dp)
                        )
                        Text(
                            "Scan QR dari HP",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Buka Web Kasir di PC:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = SlateDark,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BackgroundLight,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = webUrl,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = SlateDark,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("URL Kasir Web", webUrl))
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Salin", tint = EmeraldDark, modifier = Modifier.size(14.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Pilih Format Tampilan Aplikasi:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = SlateDark,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Anda dapat memaksa tampilan Desktop PC (Layar Lebar) atau HP (Ponsel):",
                    fontSize = 10.sp,
                    color = SlateLight,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val isPc = currentMode == com.example.ui.viewmodel.ViewMode.PC_DESKTOP
                    val isMobile = currentMode == com.example.ui.viewmodel.ViewMode.MOBILE

                    Button(
                        onClick = { onSelectMode(com.example.ui.viewmodel.ViewMode.PC_DESKTOP) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPc) MagentaPink else BackgroundLight,
                            contentColor = if (isPc) Color.White else SlateDark
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.DesktopWindows, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("View PC", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onSelectMode(com.example.ui.viewmodel.ViewMode.MOBILE) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isMobile) EmeraldDark else BackgroundLight,
                            contentColor = if (isMobile) Color.White else SlateDark
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.PhoneAndroid, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("View HP", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Tutup")
                }
            }
        }
    }
}

