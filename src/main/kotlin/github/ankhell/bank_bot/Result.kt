package github.ankhell.bank_bot

data class Result(
    val isSuccess: Boolean = true,
    val description: String
) {
    val isFailure = !isSuccess

    companion object {
        fun success(description: String = ""): Result = Result(true, description)
        fun failure(description: String = ""): Result = Result(false, description)
    }
}
