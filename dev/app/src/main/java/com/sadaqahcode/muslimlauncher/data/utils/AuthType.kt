package com.sadaqahcode.muslimlauncher.data.utils

enum class AuthType {
    HMAC,    // challenge only, HMAC
    SESSION, // dhikr etc., session nonce
    NONE     // public, no auth
}