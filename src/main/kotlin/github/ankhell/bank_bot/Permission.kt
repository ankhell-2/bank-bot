package github.ankhell.bank_bot

enum class Permission(val description: String) {
    BANK_MANAGE("Permission necessary to add/remove banks"),
    BANK_VIEW("Permission to view list of all banks"),
    BALANCE_VIEW("Permission to view bank balance"),
    TRANSACTION_VIEW("Permission to view transactions log"),
    TRANSACTION_CREATE("Permission to create transaction"),
    ADMIN("Administrator")
}