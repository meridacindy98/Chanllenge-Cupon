package com.example.challengeCupon.challengeCupon.application.usecase.model

data class Item(
    private var id: String,
    private var price: Float
) {

    fun getId(): String {
        return id
    }

    fun getPrice(): Float {
        return price
    }

}