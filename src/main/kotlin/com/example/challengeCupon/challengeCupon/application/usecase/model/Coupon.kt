package com.example.challengeCupon.challengeCupon.application.usecase.model

class Coupon (
    private var itemsIds: List<String>,
    private var amount: Float
) {
    fun getItemsIds(): List<String>{
        return itemsIds
    }

    fun getAmount(): Float {
        return amount
    }
}