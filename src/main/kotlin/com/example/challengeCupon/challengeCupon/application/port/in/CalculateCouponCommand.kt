package com.example.challengeCupon.challengeCupon.application.port.`in`

import com.example.challengeCupon.challengeCupon.application.usecase.model.CouponCalculate
import java.math.BigDecimal

interface CalculateCouponCommand {

    fun execute(command:Command) : CouponCalculate

    class Command(
        private var itemsIds: List<String>,
        private var amount: BigDecimal
    ) {

        fun getItemsIds(): List<String> {
            return itemsIds
        }

        fun getAmount(): BigDecimal {
            return amount
        }
    }


}