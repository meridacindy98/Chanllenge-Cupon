package com.example.challengeCupon.challengeCupon.application.usecase.model

import java.math.BigDecimal

class CouponCalculate(
    private var itemsIds: List<String>,
    private var total: BigDecimal
) {
    fun getItemsIds(): List<String>{
        return itemsIds
    }

    fun getTotal(): BigDecimal {
        return total
    }
}