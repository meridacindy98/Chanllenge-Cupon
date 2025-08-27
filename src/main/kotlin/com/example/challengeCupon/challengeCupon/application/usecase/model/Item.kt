package com.example.challengeCupon.challengeCupon.application.usecase.model

import java.math.BigDecimal

data class Item(
    private var id: String,
    private var price: BigDecimal
) {

    fun getId(): String {
        return id
    }

    fun getPrice(): BigDecimal {
        return price
    }

}