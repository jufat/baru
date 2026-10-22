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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.formatRupiah
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.PosViewModel

@Composable
fun DashboardScreen(
    viewModel: PosViewModel,
    modifier: Modifier = Modifier
) {
    val summary by viewModel.dashboardSummary.collectAsState()
    val session by viewModel.session.collectAsState()
    val products by viewModel.products.collectAsState()
    val serviceHpList by viewModel.serviceHpList.collectAsState()

    val lowStockProducts = products.filter { it.stock <= it.minStock }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Blue Banner
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = SlateDark,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "JU-FAT CELL POS",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF0F766E)
                        ) {
                            Text(
                                text = "Online",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Quick buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.navigateTo(AppScreen.KASIR) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Kasir ATK", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.navigateTo(AppScreen.PPOB) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFFBBF24))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Pulsa/PPOB", fontSize = 11.sp)
                        }

                        Button(
                            onClick = { viewModel.navigateTo(AppScreen.TRANSFER) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.AccountBalance, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF60A5FA))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("BRILink", fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Selamat Bertugas, ${session.email}!",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Sesi aktif: ${session.shift.label} - Kasir: ${session.name} - Modal kas awal: ${formatRupiah(session.initialCash)}",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
            }
        }

        // 4 KPI Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KpiCard(
                title = "TOTAL OMSET KESELURUHAN",
                value = formatRupiah(summary.totalTurnover),
                subtitle = "✓ 5 Layanan Terintegrasi",
                subtitleColor = SuccessGreen,
                modifier = Modifier.weight(1f)
            )

            KpiCard(
                title = "SERVICE HP DIPROSES",
                value = "${summary.serviceHpInProgressCount} Unit",
                subtitle = "Klik untuk kelola status servis",
                subtitleColor = SlateLight,
                onClick = { viewModel.navigateTo(AppScreen.SERVICE_HP) },
                modifier = Modifier.weight(1f)
            )

            KpiCard(
                title = "STOK MENIPIS",
                value = "${summary.lowStockCount} Produk",
                subtitle = "Perlu kulakan/restock segera",
                subtitleColor = WarningAmber,
                onClick = { viewModel.navigateTo(AppScreen.MANAJEMEN_STOK) },
                modifier = Modifier.weight(1f)
            )

            KpiCard(
                title = "BUKU PIUTANG BELUM LUNAS",
                value = formatRupiah(summary.totalUnpaidDebt),
                subtitle = "3 Pelanggan kasbon",
                subtitleColor = DangerRed,
                onClick = { viewModel.navigateTo(AppScreen.PIUTANG) },
                modifier = Modifier.weight(1f)
            )
        }

        // Service Division Shortcuts
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Kategori Layanan Ju-Fat Cell",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = SlateDark
                )
                Text(
                    text = "Pilih layanan untuk membuka transaksi kasir",
                    fontSize = 10.sp,
                    color = SlateLight
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DivisionShortcut(
                        title = "Kasir ATK",
                        subtitle = "Alat Tulis & Aksesoris",
                        icon = Icons.Default.ShoppingCart,
                        iconColor = Color(0xFFEA580C),
                        bgColor = Color(0xFFFFF7ED),
                        onClick = { viewModel.navigateTo(AppScreen.KASIR) },
                        modifier = Modifier.weight(1f)
                    )

                    DivisionShortcut(
                        title = "BRILink",
                        subtitle = "Transfer & Tarik Tunai",
                        icon = Icons.Default.AccountBalance,
                        iconColor = Color(0xFF0284C7),
                        bgColor = Color(0xFFF0F9FF),
                        onClick = { viewModel.navigateTo(AppScreen.TRANSFER) },
                        modifier = Modifier.weight(1f)
                    )

                    DivisionShortcut(
                        title = "Pulsa & PPOB",
                        subtitle = "PLN, BPJS, E-Wallet",
                        icon = Icons.Default.FlashOn,
                        iconColor = Color(0xFF10B981),
                        bgColor = Color(0xFFECFDF5),
                        onClick = { viewModel.navigateTo(AppScreen.PPOB) },
                        modifier = Modifier.weight(1f)
                    )

                    DivisionShortcut(
                        title = "Service HP",
                        subtitle = "Hardware & Software",
                        icon = Icons.Default.Build,
                        iconColor = Color(0xFF8B5CF6),
                        bgColor = Color(0xFFF5F3FF),
                        onClick = { viewModel.navigateTo(AppScreen.SERVICE_HP) },
                        modifier = Modifier.weight(1f)
                    )

                    DivisionShortcut(
                        title = "Percetakan",
                        subtitle = "Print, Jilid, Banner",
                        icon = Icons.Default.Print,
                        iconColor = Color(0xFF3B82F6),
                        bgColor = Color(0xFFEFF6FF),
                        onClick = { viewModel.navigateTo(AppScreen.PERCETAKAN) },
                        modifier = Modifier.weight(1f)
                    )

                    DivisionShortcut(
                        title = "Buku Piutang",
                        subtitle = "Hutang & Cicilan",
                        icon = Icons.Default.MenuBook,
                        iconColor = Color(0xFFE11D48),
                        bgColor = Color(0xFFFFF1F2),
                        onClick = { viewModel.navigateTo(AppScreen.PIUTANG) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Two Bottom Split Panels: Antrean Service HP & Peringatan Stok Menipis
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left Panel: Antrean Service HP Berjalan
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ANTREAN SERVICE HP BERJALAN",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = SlateDark
                        )
                        Text(
                            text = "Lihat Semua →",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldDark,
                            modifier = Modifier.clickable { viewModel.navigateTo(AppScreen.SERVICE_HP) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = CardBorder)
                    Spacer(modifier = Modifier.height(10.dp))

                    if (serviceHpList.isEmpty()) {
                        Text("Tidak ada antrean servis aktif", fontSize = 11.sp, color = SlateLight)
                    } else {
                        serviceHpList.take(3).forEach { srv ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(srv.deviceModel, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SlateDark)
                                    Text("a.n ${srv.customerName} - ${srv.problem}", fontSize = 10.sp, color = SlateLight)
                                }
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = InfoBlueBg
                                ) {
                                    Text(
                                        srv.status.label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = InfoBlue,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Right Panel: Peringatan Stok Menipis
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PERINGATAN STOK MENIPIS (PERLU RESTOCK)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = SlateDark
                        )
                        Text(
                            text = "Kelola Stok →",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldDark,
                            modifier = Modifier.clickable { viewModel.navigateTo(AppScreen.MANAJEMEN_STOK) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = CardBorder)
                    Spacer(modifier = Modifier.height(10.dp))

                    if (lowStockProducts.isEmpty()) {
                        Text("Semua persediaan barang dalam batas aman", fontSize = 11.sp, color = SuccessGreen)
                    } else {
                        lowStockProducts.take(4).forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(item.name, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = SlateDark)
                                    Text("SKU: ${item.sku} - Kategori: ${item.category}", fontSize = 10.sp, color = SlateLight)
                                }
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (item.stock == 0) DangerRedBg else WarningAmberBg
                                ) {
                                    Text(
                                        text = "Sisa: ${item.stock} Pcs",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (item.stock == 0) DangerRed else Color(0xFFD97706),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
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
private fun KpiCard(
    title: String,
    value: String,
    subtitle: String,
    subtitleColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        modifier = modifier.then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = SlateLight,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                color = SlateDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = subtitleColor
            )
        }
    }
}

@Composable
private fun DivisionShortcut(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    bgColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = BackgroundLight,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SlateDark)
            Text(subtitle, fontSize = 9.sp, color = SlateLight, maxLines = 1)
        }
    }
}
