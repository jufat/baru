package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CartItem
import com.example.data.model.Product
import com.example.data.model.formatRupiah
import com.example.ui.theme.*
import com.example.ui.viewmodel.PosViewModel

@Composable
fun KasirScreen(
    viewModel: PosViewModel,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val filteredProducts by viewModel.filteredProducts.collectAsState()
    val cart by viewModel.cart.collectAsState()

    var showCheckoutDialog by remember { mutableStateOf(false) }

    val categories = listOf(
        "Semua",
        "Kabel & Charger",
        "Audio & TWS",
        "Case & Pelindung",
        "Powerbank",
        "Kartu Perdana & Voucher",
        "Alat Tulis Kantor",
        "Kertas & Buku",
        "Aksesoris Lain"
    )

    Row(modifier = modifier.fillMaxSize()) {
        // Main Catalog Area
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            // Search and Fast Barcode Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Cari nama barang, SKU, atau ketik kata kunci...", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Cari", tint = SlateLight)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Hapus")
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("search_field"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = EmeraldDark
                    )
                )

                // Bayar Kasbon button
                Button(
                    onClick = { viewModel.navigateTo(com.example.ui.viewmodel.AppScreen.PIUTANG) },
                    colors = ButtonDefaults.buttonColors(containerColor = WarningAmberBg),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier.height(52.dp)
                ) {
                    Icon(
                        Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = Color(0xFFB45309),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Bayar Kasbon", color = Color(0xFFB45309), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFF59E0B),
                        modifier = Modifier.size(18.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("3", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Scan Barcode action
                Button(
                    onClick = {
                        // Demo barcode scan trigger
                        val randomProduct = filteredProducts.randomOrNull()
                        if (randomProduct != null) {
                            viewModel.addToCart(randomProduct)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(52.dp)
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Scan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Category Chips Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setCategory(cat) },
                        label = {
                            Text(
                                text = cat,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldDark,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = SlateMedium
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) EmeraldDark else CardBorder
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Products Grid
            if (filteredProducts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Inventory2,
                            contentDescription = null,
                            tint = SlateLight,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Produk tidak ditemukan",
                            color = SlateMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Coba ubah kata kunci atau pilih kategori lain",
                            color = SlateLight,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 175.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredProducts, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            onAddToCart = { viewModel.addToCart(product) }
                        )
                    }
                }
            }
        }

        // Right Shopping Cart Sidebar
        Surface(
            color = Color.White,
            shadowElevation = 4.dp,
            modifier = Modifier
                .width(340.dp)
                .fillMaxHeight()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Cart Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = EmeraldDark,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Keranjang Transaksi",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = SlateDark
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = EmeraldContainer
                    ) {
                        Text(
                            text = "${cart.sumOf { it.quantity }} item",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDark,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = CardBorder)
                Spacer(modifier = Modifier.height(8.dp))

                // Cart Content
                if (cart.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.ShoppingBag,
                                contentDescription = null,
                                tint = Color(0xFFCBD5E1),
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Keranjang Masih Kosong",
                                fontWeight = FontWeight.Bold,
                                color = SlateDark,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Pilih produk dari katalog di sebelah kiri untuk menambahkan barang.",
                                color = SlateLight,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(cart, key = { it.product.id }) { item ->
                            CartItemRow(
                                item = item,
                                onPlus = { viewModel.updateCartQty(item.product.id, 1) },
                                onMinus = { viewModel.updateCartQty(item.product.id, -1) },
                                onDelete = { viewModel.removeFromCart(item.product.id) }
                            )
                        }
                    }
                }

                Divider(color = CardBorder)
                Spacer(modifier = Modifier.height(12.dp))

                // Cart Totals and Checkout
                val totalQty = cart.sumOf { it.quantity }
                val totalAmount = cart.sumOf { it.subtotal }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Item:", fontSize = 12.sp, color = SlateLight)
                    Text("$totalQty pcs", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SlateDark)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Bayar:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                    Text(
                        formatRupiah(totalAmount),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = EmeraldDark
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (cart.isNotEmpty()) {
                            showCheckoutDialog = true
                        }
                    },
                    enabled = cart.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MagentaPink,
                        disabledContainerColor = Color(0xFFCBD5E1)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("checkout_button")
                ) {
                    Text("Lanjut Pembayaran (F12) →", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }

    if (showCheckoutDialog) {
        val totalAmount = cart.sumOf { it.subtotal }
        CheckoutDialog(
            totalAmount = totalAmount,
            onDismiss = { showCheckoutDialog = false },
            onConfirmCheckout = { method, cashGiven, custName ->
                viewModel.processCheckout(method, cashGiven, custName)
                showCheckoutDialog = false
            }
        )
    }
}

@Composable
fun ProductCard(
    product: Product,
    onAddToCart: () -> Unit
) {
    val isLowStock = product.stock <= product.minStock

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onAddToCart)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Badges row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = product.sku,
                    fontSize = 9.sp,
                    color = SlateLight,
                    fontWeight = FontWeight.Medium
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isLowStock) WarningAmberBg else SuccessGreenBg
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isLowStock) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                        }
                        Text(
                            text = "Stok: ${product.stock}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLowStock) Color(0xFFD97706) else SuccessGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = product.name,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = SlateDark,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 17.sp,
                modifier = Modifier.height(34.dp)
            )

            // Category
            Text(
                text = product.category,
                fontSize = 10.sp,
                color = SlateLight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Price and Add Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatRupiah(product.sellingPrice),
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = EmeraldDark
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldContainer,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable(onClick = onAddToCart)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Tambah",
                            tint = EmeraldDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onPlus: () -> Unit,
    onMinus: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = BackgroundLight,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.product.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = SlateDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Hapus",
                        tint = DangerRed,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatRupiah(item.subtotal),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = EmeraldDark
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                        modifier = Modifier
                            .size(26.dp)
                            .clickable(onClick = onMinus)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("-", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }

                    Text(
                        text = "${item.quantity}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateDark,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )

                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                        modifier = Modifier
                            .size(26.dp)
                            .clickable(onClick = onPlus)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("+", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CheckoutDialog(
    totalAmount: Long,
    onDismiss: () -> Unit,
    onConfirmCheckout: (paymentMethod: String, cashGiven: Long, customerName: String) -> Unit
) {
    var selectedMethod by remember { mutableStateOf("Tunai") }
    var cashGivenStr by remember { mutableStateOf(totalAmount.toString()) }
    var customerName by remember { mutableStateOf("Pelanggan Toko") }

    val cashGiven = cashGivenStr.toLongOrNull() ?: 0L
    val change = if (cashGiven > totalAmount) cashGiven - totalAmount else 0L

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier
                .widthIn(max = 420.dp)
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Konfirmasi Pembayaran",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateDark
                )

                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    color = EmeraldContainer,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total Tagihan:", fontSize = 13.sp, color = EmeraldDark)
                        Text(
                            formatRupiah(totalAmount),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = EmeraldDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("Pilih Metode:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SlateLight)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Tunai", "QRIS", "Kasbon").forEach { method ->
                        val isSel = selectedMethod == method
                        OutlinedButton(
                            onClick = { selectedMethod = method },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSel) EmeraldDark else Color.Transparent
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSel) EmeraldDark else CardBorder
                            )
                        ) {
                            Text(
                                method,
                                fontSize = 12.sp,
                                color = if (isSel) Color.White else SlateMedium,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (selectedMethod == "Tunai") {
                    Text("Nominal Uang Tunai:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SlateLight)
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = cashGivenStr,
                        onValueChange = { cashGivenStr = it.filter { ch -> ch.isDigit() } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        prefix = { Text("Rp ", fontSize = 13.sp) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick Cash buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(totalAmount, 20000L, 50000L, 100000L).forEach { amount ->
                            if (amount >= totalAmount) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = BackgroundLight,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { cashGivenStr = amount.toString() }
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.padding(vertical = 6.dp)
                                    ) {
                                        Text(
                                            if (amount == totalAmount) "Pas" else formatRupiah(amount),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = SlateMedium
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Kembalian:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SlateDark)
                        Text(
                            formatRupiah(change),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text("Nama Pelanggan", fontSize = 12.sp) },
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
                            onConfirmCheckout(selectedMethod, cashGiven, customerName)
                        },
                        modifier = Modifier.weight(1.5f),
                        colors = ButtonDefaults.buttonColors(containerColor = MagentaPink),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Selesaikan & Cetak", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
