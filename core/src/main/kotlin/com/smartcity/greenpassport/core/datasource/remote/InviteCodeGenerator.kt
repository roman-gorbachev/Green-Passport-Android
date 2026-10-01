package com.smartcity.greenpassport.core.datasource.remote

import java.security.SecureRandom

object InviteCodeGenerator {
    private const val ALPHABET = "ABCDEFGHJKMNPQRSTUVWXYZ23456789"
    private const val LENGTH = 6

    private val random = SecureRandom()

    fun generate(): String {
        return (1..LENGTH).map { ALPHABET[random.nextInt(ALPHABET.length)] }.joinToString("")
    }
}
