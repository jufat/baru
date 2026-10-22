package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.PosRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppScreen(val title: String, val isOwnerOnly: Boolean) {
    KASIR("Kasir (Penjualan)", false),
    PERCETAKAN("Percetakan & SPK", false),
    TRANSFER("Transfer / E-Wallet", false),
    PPOB("Pulsa / PPOB", false),
    SERVICE_HP("Service HP", false),
    DASHBOARD("Dashboard Analitik", true),
    KAS_HARIAN("Kas Harian & Mutasi", true),
    MANAJEMEN_STOK("Manajemen Stok", true),
    PIUTANG("Catatan Hutang", true),
    RETUR("Retur Barang", true),
    SISTEM_DATABASE("Sistem & Database", true)
}

data class DashboardSummary(
    val totalCashIn: Long,
    val totalCashOut: Long,
    val netCashInDrawer: Long,
    val totalTurnover: Long,
    val serviceHpInProgressCount: Int,
    val lowStockCount: Int,
    val totalUnpaidDebt: Long,
    val todayCounterFeeProfit: Long,
    val transferCountToday: Int,
    val totalTransferVolume: Long
)

enum class ViewMode(val label: String) {
    AUTO("Otomatis"),
    PC_DESKTOP("Layar PC (Desktop)"),
    MOBILE("Layar HP (Ponsel)")
}

class PosViewModel(
    private val repository: PosRepository = PosRepository()
) : ViewModel() {

    val session = repository.session
    val products = repository.products
    val cart = repository.cart
    val cashMovements = repository.cashMovements
    val transfers = repository.transfers
    val serviceHpList = repository.serviceHpList
    val debts = repository.debts
    val salesTransactions = repository.salesTransactions
    val lastCompletedTransaction = repository.lastCompletedTransaction
    val syncStatus = repository.syncStatus
    val isCloudSyncing = repository.isCloudSyncing

    // View Mode: default to PC_DESKTOP so user gets the exact PC view from the screenshot
    private val _viewMode = MutableStateFlow(ViewMode.PC_DESKTOP)
    val viewMode: StateFlow<ViewMode> = _viewMode.asStateFlow()

    private val _showConnectPcDialog = MutableStateFlow(false)
    val showConnectPcDialog: StateFlow<Boolean> = _showConnectPcDialog.asStateFlow()

    private val _currentScreen = MutableStateFlow(AppScreen.KASIR)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    fun setViewMode(mode: ViewMode) {
        _viewMode.value = mode
        val modeName = when (mode) {
            ViewMode.PC_DESKTOP -> "Mode Tampilan PC Desktop (Layar Lebar)"
            ViewMode.MOBILE -> "Mode Tampilan HP (Ponsel)"
            ViewMode.AUTO -> "Mode Otomatis"
        }
        _toastMessage.value = "Beralih ke $modeName"
    }

    fun setShowConnectPcDialog(show: Boolean) {
        _showConnectPcDialog.value = show
    }

    // Search & Filter state for Kasir
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Semua")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // PIN dialog state when restricted menu is accessed by Cashier
    private val _showPinDialog = MutableStateFlow(false)
    val showPinDialog: StateFlow<Boolean> = _showPinDialog.asStateFlow()

    private val _pendingScreenTarget = MutableStateFlow<AppScreen?>(null)
    val pendingScreenTarget: StateFlow<AppScreen?> = _pendingScreenTarget.asStateFlow()

    // Receipt dialog
    private val _showReceiptDialog = MutableStateFlow(false)
    val showReceiptDialog: StateFlow<Boolean> = _showReceiptDialog.asStateFlow()

    // Excel Dialog
    private val _showExcelDialog = MutableStateFlow(false)
    val showExcelDialog: StateFlow<Boolean> = _showExcelDialog.asStateFlow()

    // Toast / Feedback message
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Filtered products for Kasir catalog
    val filteredProducts: StateFlow<List<Product>> = combine(
        products,
        _searchQuery,
        _selectedCategory
    ) { prodList, query, cat ->
        prodList.filter { p ->
            val matchQuery = query.isBlank() ||
                    p.name.contains(query, ignoreCase = true) ||
                    p.sku.contains(query, ignoreCase = true)
            val matchCat = cat == "Semua" || p.category.equals(cat, ignoreCase = true)
            matchQuery && matchCat
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dashboard calculations
    val dashboardSummary: StateFlow<DashboardSummary> = combine(
        cashMovements,
        serviceHpList,
        products,
        debts,
        transfers
    ) { cashList, srvList, prodList, debtList, trfList ->
        val cashIn = cashList.filter { it.flowType == CashFlowType.MASUK }.sumOf { it.amount }
        val cashOut = cashList.filter { it.flowType == CashFlowType.KELUAR }.sumOf { it.amount }
        val netCash = cashIn - cashOut

        val srvCount = srvList.count { it.status == ServiceStatus.ANTREAN || it.status == ServiceStatus.SEDANG_DIKERJAKAN }
        val lowStock = prodList.count { it.stock <= it.minStock }
        val unpaidDebt = debtList.filter { !it.isPaidOff }.sumOf { it.remainingDebt }

        val counterFees = trfList.sumOf { it.counterFee }
        val trfVolume = trfList.sumOf { it.transferAmount }

        DashboardSummary(
            totalCashIn = cashIn,
            totalCashOut = cashOut,
            netCashInDrawer = netCash,
            totalTurnover = cashIn,
            serviceHpInProgressCount = srvCount,
            lowStockCount = lowStock,
            totalUnpaidDebt = unpaidDebt,
            todayCounterFeeProfit = counterFees,
            transferCountToday = trfList.size,
            totalTransferVolume = trfVolume
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        DashboardSummary(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
    )

    fun navigateTo(screen: AppScreen) {
        val currentRole = session.value.role
        if (screen.isOwnerOnly && currentRole != Role.OWNER) {
            _pendingScreenTarget.value = screen
            _showPinDialog.value = true
        } else {
            _currentScreen.value = screen
        }
    }

    fun submitPin(pin: String): Boolean {
        val isValid = repository.verifyOwnerPin(pin)
        if (isValid) {
            _showPinDialog.value = false
            _pendingScreenTarget.value?.let {
                _currentScreen.value = it
                _pendingScreenTarget.value = null
            }
            _toastMessage.value = "Akses Owner diverifikasi!"
            return true
        } else {
            _toastMessage.value = "PIN Salah! Hanya Owner yang berwenang."
            return false
        }
    }

    fun dismissPinDialog() {
        _showPinDialog.value = false
        _pendingScreenTarget.value = null
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun switchRole(newRole: Role) {
        repository.switchRole(newRole)
        _toastMessage.value = "Beralih ke akun ${newRole.title}"
        if (newRole != Role.OWNER && _currentScreen.value.isOwnerOnly) {
            _currentScreen.value = AppScreen.KASIR
        }
    }

    fun switchShift(newShift: Shift) {
        repository.switchShift(newShift)
        _toastMessage.value = "Beralih ke ${newShift.label}"
    }

    fun addToCart(product: Product) {
        if (product.stock <= 0) {
            _toastMessage.value = "Stok ${product.name} telah habis!"
            return
        }
        repository.addToCart(product)
    }

    fun updateCartQty(productId: String, delta: Int) {
        repository.updateCartQuantity(productId, delta)
    }

    fun removeFromCart(productId: String) {
        repository.removeFromCart(productId)
    }

    fun clearCart() {
        repository.clearCart()
    }

    fun processCheckout(paymentMethod: String, cashGiven: Long, customerName: String) {
        val tx = repository.checkout(paymentMethod, cashGiven, customerName)
        if (tx != null) {
            _showReceiptDialog.value = true
            _toastMessage.value = "Transaksi ${tx.id} Berhasil!"
        }
    }

    fun dismissReceipt() {
        _showReceiptDialog.value = false
    }

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
    ) {
        val record = repository.processTransfer(
            serviceType, senderName, senderPhone, bankTarget,
            targetAccount, recipientName, transferAmount, adminBank, counterFee
        )
        _toastMessage.value = "Transfer ${record.id} Sukses! Laba Rp $counterFee dicatat ke kas."
    }

    fun processPpob(
        category: String,
        targetNumber: String,
        productName: String,
        costPrice: Long,
        sellingPrice: Long
    ) {
        repository.processPpob(category, targetNumber, productName, costPrice, sellingPrice)
        _toastMessage.value = "Transaksi $productName berhasil!"
    }

    fun processPrinting(
        docType: String,
        customerName: String,
        amount: Long,
        notes: String
    ) {
        repository.processPrinting(docType, customerName, amount, notes)
        _toastMessage.value = "Pesanan Cetak $docType berhasil diproses!"
    }

    fun addCashMovement(flowType: CashFlowType, category: String, amount: Long, notes: String) {
        repository.addCashMovement(flowType, category, amount, notes)
        _toastMessage.value = "Arus kas berhasil dicatat!"
    }

    fun addServiceHp(
        customerName: String,
        phone: String,
        deviceModel: String,
        problem: String,
        estimatedCost: Long,
        dpAmount: Long
    ) {
        repository.addServiceHp(customerName, phone, deviceModel, problem, estimatedCost, dpAmount)
        _toastMessage.value = "Antrean Servis HP berhasil ditambahkan!"
    }

    fun updateServiceStatus(id: String, newStatus: ServiceStatus) {
        repository.updateServiceStatus(id, newStatus)
        _toastMessage.value = "Status servis diperbarui ke ${newStatus.label}"
    }

    fun payDebt(debtId: String, amount: Long) {
        repository.payDebt(debtId, amount)
        _toastMessage.value = "Pembayaran kasbon berhasil dicatat!"
    }

    fun addDebt(customerName: String, phone: String, totalDebt: Long, dueDate: String, notes: String) {
        repository.addDebt(customerName, phone, totalDebt, dueDate, notes)
        _toastMessage.value = "Data kasbon baru berhasil disimpan!"
    }

    fun addProduct(product: Product) {
        repository.addProduct(product)
        _toastMessage.value = "Produk ${product.name} berhasil ditambahkan!"
    }

    fun updateProduct(product: Product) {
        repository.updateProduct(product)
        _toastMessage.value = "Produk ${product.name} diperbarui!"
    }

    fun deleteProduct(productId: String) {
        repository.deleteProduct(productId)
        _toastMessage.value = "Produk dihapus!"
    }

    fun setShowExcelDialog(show: Boolean) {
        _showExcelDialog.value = show
    }

    fun importCsv(csvContent: String) {
        val result = repository.importProductsFromCsv(csvContent)
        _toastMessage.value = result.second
        if (result.first > 0) {
            _showExcelDialog.value = false
        }
    }

    fun getExportProductsCsv(): String = repository.exportProductsToCsv()
    fun getExportCashCsv(): String = repository.exportCashToCsv()

    fun clearToast() {
        _toastMessage.value = null
    }
}
