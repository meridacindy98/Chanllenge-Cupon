package com.example.challengeCupon.challengeCupon.application.usecase.model

import java.math.BigDecimal

class Coupon (
    private var itemsIds: List<String>,
    private var amount: BigDecimal
) {
    fun getItemsIds(): List<String>{
        return itemsIds
    }

    fun getAmount(): BigDecimal {
        return amount
    }
}