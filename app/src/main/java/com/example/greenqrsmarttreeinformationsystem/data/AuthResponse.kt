package com.example.greenqrsmarttreeinformationsystem.data

data class AuthResponse(
    val error: Boolean,
    val message: String,
    val data: List<Auth>
){
    data class Auth(
        val id: Int,
        val username: String,
        val phone: String,
        val email: String,
        val password: String
    )
}