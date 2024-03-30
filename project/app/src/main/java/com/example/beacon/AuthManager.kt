package com.example.beacon

object AuthManager {
    private var authToken: String? = null

    fun setAuthToken(authToken: String?) {
        this.authToken = authToken
    }

    fun getAuthToken(): String? {
        return this.authToken
    }
}