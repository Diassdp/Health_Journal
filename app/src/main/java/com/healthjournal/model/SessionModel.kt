package com.healthjournal.model

data class SessionModel (
    val userId: String,
    val name: String,
    val statusLogin: Boolean,
    val loginTimestamp: Long
)