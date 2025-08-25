package com.example.challengeCupon.challengeCupon.application.port.`in`

import com.example.challengeCupon.challengeCupon.application.usecase.model.CouponCalculate

interface CalculateCouponCommand {

    fun execute(command:Command) : CouponCalculate

    class Command(
        private var itemsIds: List<String>,
        private var amount: Float
    ) {

        fun getItemsIds(): List<String> {
            return itemsIds
        }

        fun getAmount(): Float {
            return amount
        }
    }


}