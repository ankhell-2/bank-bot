package github.ankhell.bank_bot

enum class Permission(val description: String) {
    BANK_MANAGE("Add/remove banks"),
    BANK_VIEW("View list of all banks"),
    BALANCE_VIEW("View bank balance"),
    TRANSACTION_VIEW("View transactions log"),
    TRANSACTION_CREATE("Create transaction"),
    SPICE_MINERS_DEBT_VIEW("View debt of all spice miners"),
    SPICE_RUN_REGISTER("Register a spice run"),
    SPICE_RUN_CONFIGURE("Configure spice runs"),
    SPICE_RUN_PAY("Pay to spice miner"),
    ADMIN("Administrator")
}