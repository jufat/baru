package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Product
import com.example.data.model.formatRupiah
import com.example.ui.theme.*
import com.example.ui.viewmodel.PosViewModel
import java.util.UUID

@Composable
fun ManajemenStokScreen(
    viewModel: PosViewModel,
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Semua") }

    var showAddDialog by remember { mutableStateOf(false) }
    var productToEdit by remember { mutableStateOf<Product?>(null) }

    val categories = listOf(
        "Semua",
        "Kabel & Charger",
        "Case & Pelindung",
        "Audio & TWS",
        "Powerbank",
        "Kartu Perdana & Voucher",
        "Alat Tulis Kantor",
        "Kertas & Buku",
        "Aksesoris Lain"
    )

    val filtered = products.filter { p ->
        val matchesQuery = searchQuery.isBlank() ||
                p.name.contains(searchQuery, ignoreCase = true) ||
                p.sku.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "Semua" || p.category.equals(selectedCategory, ignoreCase = true)
        matchesQuery && matchesCategory
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Katalog & Stok ATK / Aksesoris",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateDark
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = DangerRedBg
                    ) {
                        Text(
                            text = "Live",
                            color = DangerRed,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "Daftar Produk, Harga Modal (HPP), Margin Keuntungan & Peringatan Stok",
                    fontSize = 12.sp,
                    color = SlateLight
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { viewModel.setShowExcelDialog(true) },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Impor / Ekspor Excel", fontSize = 12.sp)
                }

                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MagentaPink),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Tambah Produk Baru", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Cari SKU atau nama barang...", fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SlateLight) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Chips with counts
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { cat ->
                val count = if (cat == "Semua") products.size else products.count { it.category.equals(cat, ignoreCase = true) }
                val isSelected = selectedCategory == cat
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = cat },
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(cat, fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) Color.White.copy(alpha = 0.3f) else BackgroundLight
                            ) {
                                Text(
                                    "$count",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SlateDark,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Table
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            modifier = Modifier.weight(1f)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Table Headers
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundLight)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("SKU", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateLight, modifier = Modifier.weight(1f))
                    Text("NAMA PRODUK", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateLight, modifier = Modifier.weight(2.2f))
                    Text("KATEGORI", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateLight, modifier = Modifier.weight(1.3f))
                    Text("MODAL (HPP)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateLight, modifier = Modifier.weight(1.1f))
                    Text("HARGA JUAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateLight, modifier = Modifier.weight(1.1f))
                    Text("LABA SATUAN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateLight, modifier = Modifier.weight(1.1f))
                    Text("STOK", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateLight, modifier = Modifier.weight(0.9f))
                    Text("AKSI", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateLight, modifier = Modifier.weight(0.8f))
                }

                Divider(color = CardBorder)

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filtered, key = { it.id }) { product ->
                        val isLowStock = product.stock <= product.minStock
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(product.sku, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = SlateMedium, modifier = Modifier.weight(1f))
                            Text(product.name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SlateDark, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(2.2f))
                            Text(product.category, fontSize = 11.sp, color = SlateLight, modifier = Modifier.weight(1.3f))
                            Text(formatRupiah(product.costPrice), fontSize = 11.sp, color = SlateMedium, modifier = Modifier.weight(1.1f))
                            Text(formatRupiah(product.sellingPrice), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldDark, modifier = Modifier.weight(1.1f))

                            val profit = product.unitProfit
                            Text(
                                text = formatRupiah(profit, withPlusSign = true),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (profit >= 0) SuccessGreen else DangerRed,
                                modifier = Modifier.weight(1.1f)
                            )

                            // Stok with pill
                            Box(modifier = Modifier.weight(0.9f)) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (isLowStock) WarningAmberBg else SuccessGreenBg
                                ) {
                                    Text(
                                        text = "${product.stock} pcs",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isLowStock) Color(0xFFD97706) else SuccessGreen,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            // Actions
                            Row(
                                modifier = Modifier.weight(0.8f),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                IconButton(
                                    onClick = { productToEdit = product },
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = SlateMedium, modifier = Modifier.size(15.dp))
                                }
                                IconButton(
                                    onClick = { viewModel.deleteProduct(product.id) },
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = DangerRed, modifier = Modifier.size(15.dp))
                                }
                            }
                        }
                        Divider(color = Color(0xFFF1F5F9))
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        ProductFormDialog(
            product = null,
            categories = categories.filter { it != "Semua" },
            onDismiss = { showAddDialog = false },
            onSave = { newProd ->
                viewModel.addProduct(newProd)
                showAddDialog = false
            }
        )
    }

    productToEdit?.let { prod ->
        ProductFormDialog(
            product = prod,
            categories = categories.filter { it != "Semua" },
            onDismiss = { productToEdit = null },
            onSave = { updatedProd ->
                viewModel.updateProduct(updatedProd)
                productToEdit = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormDialog(
    product: Product?,
    categories: List<String>,
    onDismiss: () -> Unit,
    onSave: (Product) -> Unit
) {
    var sku by remember { mutableStateOf(product?.sku ?: "ATK-${(100..999).random()}") }
    var name by remember { mutableStateOf(product?.name ?: "") }
    var category by remember { mutableStateOf(product?.category ?: categories.first()) }
    var costPriceStr by remember { mutableStateOf(product?.costPrice?.toString() ?: "0") }
    var sellingPriceStr by remember { mutableStateOf(product?.sellingPrice?.toString() ?: "0") }
    var stockStr by remember { mutableStateOf(product?.stock?.toString() ?: "10") }
    var minStockStr by remember { mutableStateOf(product?.minStock?.toString() ?: "3") }

    var expandedCategory by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier.widthIn(max = 420.dp).fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = if (product == null) "Tambah Produk Baru" else "Edit Produk",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateDark
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = sku,
                        onValueChange = { sku = it },
                        label = { Text("Kode SKU", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )

                    ExposedDropdownMenuBox(
                        expanded = expandedCategory,
                        onExpandedChange = { expandedCategory = !expandedCategory },
                        modifier = Modifier.weight(1.3f)
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Kategori", fontSize = 11.sp) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                            modifier = Modifier.menuAnchor(),
                            shape = RoundedCornerShape(8.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCategory,
                            onDismissRequest = { expandedCategory = false }
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat, fontSize = 12.sp) },
                                    onClick = {
                                        category = cat
                                        expandedCategory = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Produk *", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = costPriceStr,
                        onValueChange = { costPriceStr = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Modal HPP (Rp)", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = sellingPriceStr,
                        onValueChange = { sellingPriceStr = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Harga Jual (Rp) *", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = stockStr,
                        onValueChange = { stockStr = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Stok Sekarang", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = minStockStr,
                        onValueChange = { minStockStr = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Batas Peringatan", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

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
                            if (name.isNotBlank()) {
                                val item = Product(
                                    id = product?.id ?: UUID.randomUUID().toString(),
                                    sku = sku.ifBlank { "SKU-${(1000..9999).random()}" },
                                    name = name,
                                    category = category,
                                    costPrice = costPriceStr.toLongOrNull() ?: 0L,
                                    sellingPrice = sellingPriceStr.toLongOrNull() ?: 0L,
                                    stock = stockStr.toIntOrNull() ?: 0,
                                    minStock = minStockStr.toIntOrNull() ?: 3
                                )
                                onSave(item)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MagentaPink),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Simpan Produk", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
