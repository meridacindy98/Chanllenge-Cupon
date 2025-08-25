package com.example.challengeCupon.challengeCupon.adapter.model

class StatsResponse(
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