package github.ankhell.bank_bot

enum class Permission(val description: String) {
    BANK_MANAGE("Add/remove banks"),
    BANK_VIEW("View list of all banks"),
    BALANCE_VIEW("View bank balance"),
    TRANSACTION_VIEW("View transactions log"),
    TRANSACTION_CREATE("Create transaction"),
    ADMIN("Administrator")
}