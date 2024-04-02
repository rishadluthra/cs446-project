package com.example.beacon

object VerificationManager {
    private var verificationCode: String = ""

    fun setVerificationCode(authToken: String) {
        this.verificationCode = authToken
    }

    fun getVerificationCode(): String {
        return this.verificationCode
    }
}