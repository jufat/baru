package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class PosRepository {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy, HH.mm", Locale("in", "ID"))
    private val dateOnlyFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

    // Current Session / Role
    private val _session = MutableStateFlow(
        UserSession(
            email = "miftah2020new@gmail.com",
            name = "Miftah (Owner)",
            role = Role.OWNER,
            shift = Shift.SHIFT_1,
            initialCash = 500_000L
        )
    )
    val session: StateFlow<UserSession> = _session.asStateFlow()

    // Owner PIN for security barrier
    private val ownerPin = "1234"

    // Cloud Database Sync Status
    private val _syncStatus = MutableStateFlow("Tersinkronisasi ke Database Utama (Cloud Live)")
    val syncStatus: StateFlow<String> = _syncStatus.asStateFlow()

    private val _isCloudSyncing = MutableStateFlow(false)
    val isCloudSyncing: StateFlow<Boolean> = _isCloudSyncing.asStateFlow()

    // Products
    private val _products = MutableStateFlow<List<Product>>(getInitialProducts())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    // Cart
    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    // Cash Movements (Kas Harian)
    private val _cashMovements = MutableStateFlow<List<CashMovement>>(getInitialCashMovements())
    val cashMovements: StateFlow<List<CashMovement>> = _cashMovements.asStateFlow()

    // Transfer / BRILink Transactions
    private val _transfers = MutableStateFlow<List<TransferRecord>>(getInitialTransfers())
    val transfers: StateFlow<List<TransferRecord>> = _transfers.asStateFlow()

    // Service HP
    private val _serviceHpList = MutableStateFlow<List<ServiceHp>>(getInitialServiceList())
    val serviceHpList: StateFlow<List<ServiceHp>> = _serviceHpList.asStateFlow()

    // Catatan Hutang / Piutang
    private val _debts = MutableStateFlow<List<DebtRecord>>(getInitialDebts())
    val debts: StateFlow<List<DebtRecord>> = _debts.asStateFlow()

    // Sales Transactions Log
    private val _salesTransactions = MutableStateFlow<List<SalesTransaction>>(getInitialSales())
    val salesTransactions: StateFlow<List<SalesTransaction>> = _salesTransactions.asStateFlow()

    // Last completed transaction for receipt viewing
    private val _lastCompletedTransaction = MutableStateFlow<SalesTransaction?>(null)
    val lastCompletedTransaction: StateFlow<SalesTransaction?> = _lastCompletedTransaction.asStateFlow()

    // Session Switcher
    fun switchRole(newRole: Role, shift: Shift = _session.value.shift) {
        val email = if (newRole == Role.OWNER) "miftah2020new@gmail.com" else "kasir_shift@jufatpos.com"
        val name = if (newRole == Role.OWNER) "Miftah (Owner)" else "Kasir (${shift.label.substringBefore(" ")})"
        _session.value = _session.value.copy(
            email = email,
            name = name,
            role = newRole,
            shift = shift
        )
    }

    fun switchShift(newShift: Shift) {
        _session.value = _session.value.copy(shift = newShift)
    }

    fun verifyOwnerPin(enteredPin: String): Boolean {
        return enteredPin == ownerPin
    }

    // Cart Methods
    fun addToCart(product: Product) {
        val currentList = _cart.value.toMutableList()
        val index = currentList.indexOfFirst { it.product.id == product.id }
        if (index >= 0) {
            val item = currentList[index]
            if (item.quantity < product.stock) {
                currentList[index] = item.copy(quantity = item.quantity + 1)
            }
        } else {
            if (product.stock > 0) {
                currentList.add(CartItem(product = product, quantity = 1))
            }
        }
        _cart.value = currentList
    }

    fun updateCartQuantity(productId: String, delta: Int) {
        val currentList = _cart.value.toMutableList()
        val index = currentList.indexOfFirst { it.product.id == productId }
        if (index >= 0) {
            val item = currentList[index]
            val newQty = item.quantity + delta
            if (newQty <= 0) {
                currentList.removeAt(index)
            } else if (newQty <= item.product.stock) {
                currentList[index] = item.copy(quantity = newQty)
            }
        }
        _cart.value = currentList
    }

    fun removeFromCart(productId: String) {
        _cart.value = _cart.value.filter { it.product.id != productId }
    }

    fun clearCart() {
        _cart.value = emptyList()
    }

    // Checkout
    fun checkout(
        paymentMethod: String,
        cashGiven: Long,
        customerName: String = "Pelanggan Toko"
    ): SalesTransaction? {
        val currentCart = _cart.value
        if (currentCart.isEmpty()) return null

        val totalAmount = currentCart.sumOf { it.subtotal }
        val totalCost = currentCart.sumOf { it.product.costPrice * it.quantity }
        val change = if (paymentMethod == "Tunai" && cashGiven > totalAmount) cashGiven - totalAmount else 0L

        val now = System.currentTimeMillis()
        val invId = "INV-${dateOnlyFormat.format(Date(now))}-${(1000..9999).random()}"
        val timeFormatted = dateFormat.format(Date(now))
        val currentSession = _session.value

        val itemsSummary = currentCart.joinToString(", ") { "${it.product.name} (x${it.quantity})" }

        val transaction = SalesTransaction(
            id = invId,
            timestamp = now,
            timeFormatted = timeFormatted,
            cashier = currentSession.email,
            shift = currentSession.shift.label,
            totalAmount = totalAmount,
            totalCost = totalCost,
            paymentMethod = paymentMethod,
            customerName = customerName,
            itemsSummary = itemsSummary,
            cashGiven = cashGiven,
            change = change
        )

        // Deduct inventory stock
        val updatedProducts = _products.value.map { prod ->
            val cartItem = currentCart.find { it.product.id == prod.id }
            if (cartItem != null) {
                prod.copy(stock = (prod.stock - cartItem.quantity).coerceAtLeast(0))
            } else {
                prod
            }
        }
        _products.value = updatedProducts

        // Add to Sales Log
        _salesTransactions.value = listOf(transaction) + _salesTransactions.value

        // Record to Kas Harian
        val cashMovement = CashMovement(
            id = UUID.randomUUID().toString(),
            timestamp = now,
            timeFormatted = timeFormatted,
            flowType = CashFlowType.MASUK,
            category = "Penjualan Toko",
            account = "Kas Fisik Toko",
            notes = "$invId ($itemsSummary)",
            amount = totalAmount,
            cashierInfo = "${currentSession.email} (${currentSession.shift.label.substringBefore(" ")})",
            refId = invId
        )
        _cashMovements.value = listOf(cashMovement) + _cashMovements.value

        _lastCompletedTransaction.value = transaction
        clearCart()
        triggerCloudSync()
        return transaction
    }

    // Manual Cash In / Out
    fun addCashMovement(
        flowType: CashFlowType,
        category: String,
        amount: Long,
        notes: String
    ) {
        val now = System.currentTimeMillis()
        val timeFormatted = dateFormat.format(Date(now))
        val currentSession = _session.value

        val movement = CashMovement(
            id = UUID.randomUUID().toString(),
            timestamp = now,
            timeFormatted = timeFormatted,
            flowType = flowType,
            category = category,
            account = "Kas Fisik Toko",
            notes = notes,
            amount = amount,
            cashierInfo = "${currentSession.email} (${currentSession.shift.label.substringBefore(" ")})"
        )
        _cashMovements.value = listOf(movement) + _cashMovements.value
        triggerCloudSync()
    }

    // Process Bank / E-Wallet Transfer
    fun processTransfer(
        serviceType: String,
        senderName: String,
        senderPhone: String,
        bankTarget: String,
        targetAccount: String,
        recipientName: String,
        transferAmount: Long,
        adminBank: Long,
        counterFee: Long
    ): TransferRecord {
        val now = System.currentTimeMillis()
        val timeFormatted = dateFormat.format(Date(now))
        val currentSession = _session.value
        val refId = "TRF-${dateOnlyFormat.format(Date(now))}-${(100..999).random()}"
        val totalCustomer = transferAmount + adminBank + counterFee

        val record = TransferRecord(
            id = refId,
            timestamp = now,
            timeFormatted = timeFormatted,
            serviceType = serviceType,
            senderName = senderName,
            senderPhone = senderPhone,
            bankTarget = bankTarget,
            targetAccount = targetAccount,
            recipientName = recipientName,
            transferAmount = transferAmount,
            adminBank = adminBank,
            counterFee = counterFee,
            totalCustomer = totalCustomer,
            cashier = "${currentSession.email} (${currentSession.shift.label.substringBefore(" ")})"
        )
        _transfers.value = listOf(record) + _transfers.value

        // Record Jasa Konter to Kas Harian (profit)
        val profitMovement = CashMovement(
            id = UUID.randomUUID().toString(),
            timestamp = now,
            timeFormatted = timeFormatted,
            flowType = CashFlowType.MASUK,
            category = "Jasa Transfer Konter",
            account = "Kas Fisik Toko",
            notes = "$refId - $serviceType $bankTarget an $recipientName",
            amount = counterFee,
            cashierInfo = record.cashier,
            refId = refId
        )
        _cashMovements.value = listOf(profitMovement) + _cashMovements.value

        triggerCloudSync()
        return record
    }

    // Process PPOB / Pulsa
    fun processPpob(
        category: String,
        targetNumber: String,
        productName: String,
        costPrice: Long,
        sellingPrice: Long
    ) {
        val now = System.currentTimeMillis()
        val timeFormatted = dateFormat.format(Date(now))
        val currentSession = _session.value
        val refId = "PPOB-${dateOnlyFormat.format(Date(now))}-${(100..999).random()}"
        val profit = sellingPrice - costPrice

        val movement = CashMovement(
            id = UUID.randomUUID().toString(),
            timestamp = now,
            timeFormatted = timeFormatted,
            flowType = CashFlowType.MASUK,
            category = "Keuntungan PPOB",
            account = "Kas Fisik Toko",
            notes = "$refId $productName ($targetNumber)",
            amount = profit,
            cashierInfo = "${currentSession.email} (${currentSession.shift.label.substringBefore(" ")})",
            refId = refId
        )
        _cashMovements.value = listOf(movement) + _cashMovements.value
        triggerCloudSync()
    }

    // Process Printing / SPK
    fun processPrinting(
        docType: String,
        customerName: String,
        amount: Long,
        notes: String
    ) {
        val now = System.currentTimeMillis()
        val timeFormatted = dateFormat.format(Date(now))
        val currentSession = _session.value
        val refId = "PRT-${dateOnlyFormat.format(Date(now))}-${(100..999).random()}"

        val movement = CashMovement(
            id = UUID.randomUUID().toString(),
            timestamp = now,
            timeFormatted = timeFormatted,
            flowType = CashFlowType.MASUK,
            category = "Penjualan Cetak",
            account = "Kas Fisik Toko",
            notes = "$refId Cetak $docType a.n $customerName - $notes",
            amount = amount,
            cashierInfo = "${currentSession.email} (${currentSession.shift.label.substringBefore(" ")})",
            refId = refId
        )
        _cashMovements.value = listOf(movement) + _cashMovements.value
        triggerCloudSync()
    }

    // Service HP
    fun addServiceHp(
        customerName: String,
        phone: String,
        deviceModel: String,
        problem: String,
        estimatedCost: Long,
        dpAmount: Long
    ) {
        val now = System.currentTimeMillis()
        val id = "SRV-${dateOnlyFormat.format(Date(now))}-${(100..999).random()}"
        val service = ServiceHp(
            id = id,
            timestamp = now,
            customerName = customerName,
            phone = phone,
            deviceModel = deviceModel,
            problem = problem,
            estimatedCost = estimatedCost,
            dpAmount = dpAmount,
            status = ServiceStatus.ANTREAN
        )
        _serviceHpList.value = listOf(service) + _serviceHpList.value

        if (dpAmount > 0) {
            val timeFormatted = dateFormat.format(Date(now))
            val currentSession = _session.value
            _cashMovements.value = listOf(
                CashMovement(
                    id = UUID.randomUUID().toString(),
                    timestamp = now,
                    timeFormatted = timeFormatted,
                    flowType = CashFlowType.MASUK,
                    category = "DP Servis HP",
                    account = "Kas Fisik Toko",
                    notes = "$id DP Servis $deviceModel a.n $customerName",
                    amount = dpAmount,
                    cashierInfo = "${currentSession.email} (${currentSession.shift.label.substringBefore(" ")})",
                    refId = id
                )
            ) + _cashMovements.value
        }
        triggerCloudSync()
    }

    fun updateServiceStatus(id: String, newStatus: ServiceStatus) {
        _serviceHpList.value = _serviceHpList.value.map {
            if (it.id == id) it.copy(status = newStatus) else it
        }
        triggerCloudSync()
    }

    // Debts / Piutang
    fun payDebt(debtId: String, amount: Long) {
        val now = System.currentTimeMillis()
        val timeFormatted = dateFormat.format(Date(now))
        val currentSession = _session.value

        _debts.value = _debts.value.map { debt ->
            if (debt.id == debtId) {
                val newPaid = debt.paidAmount + amount
                debt.copy(paidAmount = newPaid)
            } else {
                debt
            }
        }

        val debt = _debts.value.find { it.id == debtId }
        val customer = debt?.customerName ?: "Pelanggan"

        val movement = CashMovement(
            id = UUID.randomUUID().toString(),
            timestamp = now,
            timeFormatted = timeFormatted,
            flowType = CashFlowType.MASUK,
            category = "Pelunasan Hutang",
            account = "Kas Fisik Toko",
            notes = "Pelunasan Kasbon a.n $customer",
            amount = amount,
            cashierInfo = "${currentSession.email} (${currentSession.shift.label.substringBefore(" ")})",
            refId = debtId
        )
        _cashMovements.value = listOf(movement) + _cashMovements.value
        triggerCloudSync()
    }

    fun addDebt(customerName: String, phone: String, totalDebt: Long, dueDate: String, notes: String) {
        val newDebt = DebtRecord(
            id = "KSB-${(100..999).random()}",
            customerName = customerName,
            phone = phone,
            totalDebt = totalDebt,
            paidAmount = 0L,
            dueDate = dueDate,
            notes = notes
        )
        _debts.value = listOf(newDebt) + _debts.value
        triggerCloudSync()
    }

    // Stock Management (Owner Only)
    fun addProduct(product: Product) {
        _products.value = listOf(product) + _products.value
        triggerCloudSync()
    }

    fun updateProduct(product: Product) {
        _products.value = _products.value.map { if (it.id == product.id) product else it }
        triggerCloudSync()
    }

    fun deleteProduct(productId: String) {
        _products.value = _products.value.filter { it.id != productId }
        triggerCloudSync()
    }

    // Excel / CSV Import & Export
    fun importProductsFromCsv(csvText: String): Pair<Int, String> {
        return try {
            val lines = csvText.trim().split("\n")
            var count = 0
            val newProducts = mutableListOf<Product>()

            for (line in lines) {
                if (line.isBlank() || line.startsWith("SKU") || line.startsWith("sku")) continue
                val parts = line.split(",").map { it.trim().removeSurrounding("\"") }
                if (parts.size >= 6) {
                    val sku = parts[0]
                    val name = parts[1]
                    val category = parts[2]
                    val cost = parts[3].toLongOrNull() ?: 0L
                    val price = parts[4].toLongOrNull() ?: 0L
                    val stock = parts[5].toIntOrNull() ?: 0

                    if (name.isNotBlank()) {
                        newProducts.add(
                            Product(
                                id = UUID.randomUUID().toString(),
                                sku = sku.ifBlank { "SKU-${(100000..999999).random()}" },
                                name = name,
                                category = category.ifBlank { "Alat Tulis Kantor" },
                                costPrice = cost,
                                sellingPrice = price,
                                stock = stock
                            )
                        )
                        count++
                    }
                }
            }

            if (newProducts.isNotEmpty()) {
                _products.value = newProducts + _products.value
                triggerCloudSync()
                Pair(count, "Berhasil mengimpor $count produk dari file Excel/CSV!")
            } else {
                Pair(0, "Format data tidak sesuai. Pastikan ada 6 kolom: SKU,Nama,Kategori,Modal,Jual,Stok")
            }
        } catch (e: Exception) {
            Pair(0, "Gagal mengimpor: ${e.localizedMessage}")
        }
    }

    fun exportProductsToCsv(): String {
        val sb = StringBuilder()
        sb.append("SKU,Nama Produk,Kategori,Modal (HPP),Harga Jual,Stok\n")
        for (p in _products.value) {
            sb.append("\"${p.sku}\",\"${p.name}\",\"${p.category}\",${p.costPrice},${p.sellingPrice},${p.stock}\n")
        }
        return sb.toString()
    }

    fun exportCashToCsv(): String {
        val sb = StringBuilder()
        sb.append("Waktu,Arus,Kategori,Akun,Keterangan,Nominal,Kasir / Shift\n")
        for (c in _cashMovements.value) {
            sb.append("\"${c.timeFormatted}\",\"${c.flowType.name}\",\"${c.category}\",\"${c.account}\",\"${c.notes}\",${c.amount},\"${c.cashierInfo}\"\n")
        }
        return sb.toString()
    }

    private fun triggerCloudSync() {
        _isCloudSyncing.value = true
        _syncStatus.value = "Menyinkronkan data ke Server Cloud..."
        // simulate instantaneous background sync
        _syncStatus.value = "Tersinkronisasi ke Database Utama (Cloud Live)"
        _isCloudSyncing.value = false
    }

    // Initial Data Seeders
    private fun getInitialProducts(): List<Product> = listOf(
        Product(UUID.randomUUID().toString(), "SKU-587631", "kabel data", "Kabel & Charger", 300, 500, 9),
        Product(UUID.randomUUID().toString(), "ACC-TC-001", "Kabel Data Type-C Fast Charging 65W Robot", "Kabel & Charger", 22000, 35000, 23),
        Product(UUID.randomUUID().toString(), "ACC-CHG-002", "Batok Charger 20W Quick Charge Dual Port Vivan", "Kabel & Charger", 45000, 65000, 11),
        Product(UUID.randomUUID().toString(), "AUD-TWS-003", "TWS Bluetooth Earphone Bass Boost F9", "Audio & TWS", 62000, 95000, 8),
        Product(UUID.randomUUID().toString(), "AUD-EP-004", "Earphone Kabel Jack 3.5mm Heavy Bass", "Audio & TWS", 15000, 25000, 35),
        Product(UUID.randomUUID().toString(), "SCR-TG-005", "Tempered Glass Keramik Anti Spy iPhone/Android", "Case & Pelindung", 8000, 20000, 42),
        Product(UUID.randomUUID().toString(), "PB-10K-006", "Powerbank 10.000mAh Slim Fast Charge Orico", "Powerbank", 95000, 145000, 5),
        Product(UUID.randomUUID().toString(), "PRD-TSEL-007", "Kartu Perdana Telkomsel 14GB 30 Hari", "Kartu Perdana & Voucher", 40000, 48000, 18),
        Product(UUID.randomUUID().toString(), "VOU-ISAT-008", "Voucher Data Indosat 8GB Freedom Internet", "Kartu Perdana & Voucher", 24000, 30000, 2),
        Product(UUID.randomUUID().toString(), "MEM-32-009", "Memory Card MicroSD 32GB Sandisk Original", "Aksesoris Lain", 48000, 65000, 7),
        Product(UUID.randomUUID().toString(), "HLD-MTR-010", "Holder HP Motor Stang Aluminium Anti Guncang", "Aksesoris Lain", 38000, 55000, 1),
        Product(UUID.randomUUID().toString(), "ACC-OTG-011", "OTG Adapter USB to Type-C Metal Body", "Kabel & Charger", 8000, 15000, 20),
        Product(UUID.randomUUID().toString(), "ATK_14", "Buku Tulis Sidu 38lbr", "Alat Tulis Kantor", 3700, 5000, 10),
        Product(UUID.randomUUID().toString(), "ATK_13", "Buku Tulis Dodo 50lbr", "Alat Tulis Kantor", 2900, 6000, 10),
        Product(UUID.randomUUID().toString(), "ATK_12", "Buku Tulis Dodo 30lbr", "Alat Tulis Kantor", 1900, 3000, 10),
        Product(UUID.randomUUID().toString(), "ATK_11", "Buku Tulis Albino", "Alat Tulis Kantor", 4000, 6000, 10),
        Product(UUID.randomUUID().toString(), "ATK_10", "Buku Notes Star", "Alat Tulis Kantor", 1755, 3000, 10),
        Product(UUID.randomUUID().toString(), "ATK_9", "Buku Notes Block Notes", "Alat Tulis Kantor", 834, 2000, 10),
        Product(UUID.randomUUID().toString(), "ATK_8", "Buku Gambar a3 Delux", "Kertas & Buku", 3000, 7000, 10),
        Product(UUID.randomUUID().toString(), "ATK_7", "Buku Gambar a4 Dodo", "Kertas & Buku", 3500, 5000, 3000),
        Product(UUID.randomUUID().toString(), "ATK_77", "Pulpen Ulir", "Alat Tulis Kantor", 1200, 2500, 1),
        Product(UUID.randomUUID().toString(), "P_16", "Pentol Bulat", "Aksesoris Lain", 1000, 2000, 0),
        Product(UUID.randomUUID().toString(), "MA_6", "Mobil Kecil", "Aksesoris Lain", 8000, 15000, 0),
        Product(UUID.randomUUID().toString(), "ATK_18", "Lem Stik Yoeker", "Alat Tulis Kantor", 2500, 4500, 0)
    )

    private fun getInitialCashMovements(): List<CashMovement> = listOf(
        CashMovement(UUID.randomUUID().toString(), System.currentTimeMillis() - 600000, "02 Okt 2026, 10.39", CashFlowType.MASUK, "Penjualan ATK", "Kas Fisik Toko", "INV-20261002-8006 (Es Coklat Crispi x1)", 5000, "miftah2020new@gmail.com (Shift 1)", "INV-20261002-8006"),
        CashMovement(UUID.randomUUID().toString(), System.currentTimeMillis() - 1200000, "02 Okt 2026, 10.22", CashFlowType.MASUK, "Keuntungan PPOB", "Kas Fisik Toko", "PPOB-20261002-843 Paket Data (Pelanggan)", 15000, "miftah2020new@gmail.com (Shift 1)", "PPOB-20261002-843"),
        CashMovement(UUID.randomUUID().toString(), System.currentTimeMillis() - 1800000, "02 Okt 2026, 10.21", CashFlowType.KELUAR, "Lain-lain", "Kas Fisik Toko", "Tarik Tunai TRF-20261002-219 Rek: OVO", 120000, "miftah2020new@gmail.com (Shift 1)", "TRF-20261002-219"),
        CashMovement(UUID.randomUUID().toString(), System.currentTimeMillis() - 1800000, "02 Okt 2026, 10.21", CashFlowType.MASUK, "Jasa Transfer Konter", "Kas Fisik Toko", "Jasa Tarik Tunai TRF-20261002-219", 3000, "miftah2020new@gmail.com (Shift 1)", "TRF-20261002-219"),
        CashMovement(UUID.randomUUID().toString(), System.currentTimeMillis() - 3600000, "02 Okt 2026, 09.27", CashFlowType.MASUK, "Penjualan Cetak", "Kas Fisik Toko", "PRT-20261002-652 Cetak a.n Pelanggan", 7000, "miftah2020new@gmail.com (Shift 1)", "PRT-20261002-652"),
        CashMovement(UUID.randomUUID().toString(), System.currentTimeMillis() - 4200000, "02 Okt 2026, 09.14", CashFlowType.KELUAR, "Tarik Modal / Setoran", "Kas Fisik Toko", "Beli Jamu", 9000, "miftah2020new@gmail.com (Shift 1)"),
        CashMovement(UUID.randomUUID().toString(), System.currentTimeMillis() - 4200000, "02 Okt 2026, 09.14", CashFlowType.KELUAR, "Lain-lain", "Kas Fisik Toko", "Tarik Tunai TRF-20261002-843 Rek: Bank Lainnya", 40000, "miftah2020new@gmail.com (Shift 1)", "TRF-20261002-843"),
        CashMovement(UUID.randomUUID().toString(), System.currentTimeMillis() - 4200000, "02 Okt 2026, 09.14", CashFlowType.MASUK, "Jasa Transfer Konter", "Kas Fisik Toko", "Jasa Tarik Tunai TRF-20261002-843", 3000, "miftah2020new@gmail.com (Shift 1)", "TRF-20261002-843"),
        CashMovement(UUID.randomUUID().toString(), System.currentTimeMillis() - 4500000, "02 Okt 2026, 09.13", CashFlowType.KELUAR, "Lain-lain", "Kas Fisik Toko", "Tarik Tunai TRF-20261002-378 Rek: GoPay", 249000, "miftah2020new@gmail.com (Shift 1)", "TRF-20261002-378"),
        CashMovement(UUID.randomUUID().toString(), System.currentTimeMillis() - 4500000, "02 Okt 2026, 09.13", CashFlowType.MASUK, "Jasa Transfer Konter", "Kas Fisik Toko", "Jasa Tarik Tunai TRF-20261002-378", 3000, "miftah2020new@gmail.com (Shift 1)", "TRF-20261002-378"),
        CashMovement(UUID.randomUUID().toString(), System.currentTimeMillis() - 4800000, "02 Okt 2026, 09.10", CashFlowType.MASUK, "Jasa Transfer Konter", "Kas Fisik Toko", "TRF-20261002-888 Transfer Antar Bank ke Bank BRI", 103000, "miftah2020new@gmail.com (Shift 1)", "TRF-20261002-888")
    )

    private fun getInitialTransfers(): List<TransferRecord> = listOf(
        TransferRecord("TRF-20261002-219", System.currentTimeMillis() - 1800000, "02 Okt 2026, 10.21", "Tarik Tunai", "Pelanggan", "0812xxxx", "OVO", "08129010291", "Satriani", 120000, 2500, 3000, 125500, "miftah2020new@gmail.com (Shift 1)"),
        TransferRecord("TRF-20261002-843", System.currentTimeMillis() - 4200000, "02 Okt 2026, 09.14", "Tarik Tunai", "Pelanggan", "0852xxxx", "Bank Lainnya", "00291039102", "Barcode", 40000, 2500, 3000, 45500, "miftah2020new@gmail.com (Shift 1)"),
        TransferRecord("TRF-20261002-378", System.currentTimeMillis() - 4500000, "02 Okt 2026, 09.13", "Tarik Tunai", "Pelanggan", "0878xxxx", "GoPay", "08789018271", "Veny May Riza", 249000, 2500, 3000, 254500, "miftah2020new@gmail.com (Shift 1)")
    )

    private fun getInitialServiceList(): List<ServiceHp> = listOf(
        ServiceHp("SRV-20261021-01", System.currentTimeMillis() - 7200000, "Agus Salim", "08123456789", "Redmi Note 10", "Layar Pecah & Baterai Kembung", 350000, 100000, ServiceStatus.SEDANG_DIKERJAKAN, "Sparepart LCD sudah datang"),
        ServiceHp("SRV-20261021-02", System.currentTimeMillis() - 3600000, "Rian Pratama", "08571234567", "Samsung Galaxy A12", "Mati Total setelah kena air", 200000, 50000, ServiceStatus.ANTREAN, "Perlu cek jalur IC Power")
    )

    private fun getInitialDebts(): List<DebtRecord> = listOf(
        DebtRecord("KSB-101", "Pak Budi Sentosa", "081234567890", 85000, 0, "25 Okt 2026", "Beli kuota & kabel data"),
        DebtRecord("KSB-102", "Ibu Sari Dewi", "082198765432", 48000, 0, "28 Okt 2026", "Perdana Telkomsel"),
        DebtRecord("KSB-103", "Mas Doni Setiawan", "085611223344", 30000, 0, "22 Okt 2026", "Voucher data Indosat")
    )

    private fun getInitialSales(): List<SalesTransaction> = listOf(
        SalesTransaction(
            id = "INV-20261002-8006",
            timestamp = System.currentTimeMillis() - 600000,
            timeFormatted = "02 Okt 2026, 10.39",
            cashier = "miftah2020new@gmail.com",
            shift = "Shift 1 (Pagi)",
            totalAmount = 5000,
            totalCost = 3000,
            paymentMethod = "Tunai",
            customerName = "Pelanggan Toko",
            itemsSummary = "kabel data (x1)",
            cashGiven = 10000,
            change = 5000
        )
    )
}
