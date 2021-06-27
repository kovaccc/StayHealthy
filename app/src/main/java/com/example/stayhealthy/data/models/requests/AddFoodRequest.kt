package com.example.stayhealthy.data.models.requests

data class AddFoodRequest (
        val quantity: Int,
        val calories: Int,
        val name: String
)