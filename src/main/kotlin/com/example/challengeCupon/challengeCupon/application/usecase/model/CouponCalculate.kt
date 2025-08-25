package com.example.challengeCupon.challengeCupon.application.usecase.model

class CouponCalculate(
    private var itemsIds: List<String>,
    private var total: Float
) {
    fun getItemsIds(): List<String>{
        return itemsIds
    }

    fun getTotal(): Float {
        return total
    }
}