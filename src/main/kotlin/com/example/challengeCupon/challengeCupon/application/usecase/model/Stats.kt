package com.example.challengeCupon.challengeCupon.application.usecase.model

class Stats(
    private var id: String,
    private var quantity: Int
) {

    private fun getId(): String {
        return id
    }

    private fun getQuantity(): Int {
        return quantity
    }

}