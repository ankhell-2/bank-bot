package github.ankhell.bank_bot.converters

import java.math.BigInteger

fun ULong.toBigInteger(): BigInteger = this.toString().toBigInteger()