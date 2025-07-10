package github.ankhell.bank_bot

enum class Permission(val description: String) {
    BANK_MANAGE("Add/remove banks"),
    BANK_VIEW("View list of all banks"),
    BALANCE_VIEW("View bank balance"),
    TRANSACTION_VIEW("View transactions log"),
    TRANSACTION_CREATE("Create transaction"),
    SPICE_MINERS_DEBT_VIEW("View debt of all spice miners"),
    SPICE_RUN_REGISTER("Register a spice run"),
    SPICE_RUN_ROLLBACK("Deregister a spice run"),
    SPICE_RUN_LIST("View list of all spice runs"),
    SPICE_RUN_CONFIGURE("Configure spice runs"),
    SPICE_RUN_PAY("Pay to spice miner"),
    SPICE_RUN_MINER_MERGE("Merge spice miners"),
    ADMIN("Administrator")
}