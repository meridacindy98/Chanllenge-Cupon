package com.example.challengeCupon.challengeCupon.adapter.model

import com.example.challengeCupon.challengeCupon.application.usecase.model.CouponCalculate
import java.math.BigDecimal

class CouponCalculateResponse(
    private var itemsIds: List<String>,
    private var total: BigDecimal
) {

    fun getItemsIds(): List<String>{
        return itemsIds
    }

    fun getTotal(): BigDecimal {
        return total
    }

    companion object{
        fun fromDomain(couponCalculate: CouponCalculate): CouponCalculateResponse {
            return CouponCalculateResponse(
                couponCalculate.getItemsIds(),
                couponCalculate.getTotal()
            )
        }
    }

}