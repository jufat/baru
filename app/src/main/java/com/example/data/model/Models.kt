package com.example.data.model

import java.text.NumberFormat
import java.util.Locale

enum class Role(val title: String, val description: String) {
    OWNER("Owner", "Hak Akses Penuh"),
    CASHIER("Kasir", "Akses Kasir & Jasa")
}

enum class Shift(val label: String) {
    SHIFT_1("Shift 1 (Pagi)"),
    SHIFT_2("Shift 2 (Sore)")
}

data class UserSession(
    val email: String = "miftah2020new@gmail.com",
    val name: String = "Miftah (Owner)",
    val role: Role = Role.OWNER,
    val shift: Shift = Shift.SHIFT_1,
    val initialCash: Long = 500_000L
)

data class Product(
    val id: String,
    val sku: String,
    val name: String,
    val category: String,
    val costPrice: Long, // Modal (HPP)
    val sellingPrice: Long, // Harga Jual
    val stock: Int,
    val minStock: Int = 3
) {
    val unitProfit: Long
        get() = sellingPrice - costPrice
}

data class CartItem(
    val product: Product,
    val quantity: Int
) {
    val subtotal: Long
        get() = product.sellingPrice * quantity
    val subtotalProfit: Long
        get() = product.unitProfit * quantity
}

enum class CashFlowType {
    MASUK,
    KELUAR
}

data class CashMovement(
    val id: String,
    val timestamp: Long,
    val timeFormatted: String,
    val flowType: CashFlowType,
    val category: String,
    val account: String = "Kas Fisik Toko",
    val notes: String,
    val amount: Long,
    val cashierInfo: String,
    val refId: String = ""
)

data class TransferRecord(
    val id: String,
    val timestamp: Long,
    val timeFormatted: String,
    val serviceType: String, // Transfer Antar Bank, Tarik Tunai, Top Up E-Wallet, etc.
    val senderName: String,
    val senderPhone: String,
    val bankTarget: String,
    val targetAccount: String,
    val recipientName: String,
    val transferAmount: Long,
    val adminBank: Long,
    val counterFee: Long,
    val totalCustomer: Long,
    val cashier: String
)

enum class ServiceStatus(val label: String) {
    ANTREAN("Antrean"),
    SEDANG_DIKERJAKAN("Sedang Dikerjakan"),
    SELESAI("Selesai"),
    SUDAH_DIAMBIL("Sudah Diambil")
}

data class ServiceHp(
    val id: String,
    val timestamp: Long,
    val customerName: String,
    val phone: String,
    val deviceModel: String,
    val problem: String,
    val estimatedCost: Long,
    val dpAmount: Long,
    val status: ServiceStatus,
    val technicianNote: String = ""
)

data class DebtRecord(
    val id: String,
    val customerName: String,
    val phone: String,
    val totalDebt: Long,
    val paidAmount: Long,
    val dueDate: String,
    val notes: String
) {
    val remainingDebt: Long
        get() = totalDebt - paidAmount
    val isPaidOff: Boolean
        get() = remainingDebt <= 0
}

data class SalesTransaction(
    val id: String,
    val timestamp: Long,
    val timeFormatted: String,
    val cashier: String,
    val shift: String,
    val totalAmount: Long,
    val totalCost: Long,
    val paymentMethod: String,
    val customerName: String = "Pelanggan Toko",
    val itemsSummary: String,
    val cashGiven: Long = 0L,
    val change: Long = 0L
)

fun formatRupiah(amount: Long, withPlusSign: Boolean = false): String {
    val formatter = NumberFormat.getNumberInstance(Locale("in", "ID"))
    val absFormatted = "Rp " + formatter.format(Math.abs(amount))
    return when {
        amount > 0 && withPlusSign -> "+$absFormatted"
        amount < 0 -> "-$absFormatted"
        else -> absFormatted
    }
}
