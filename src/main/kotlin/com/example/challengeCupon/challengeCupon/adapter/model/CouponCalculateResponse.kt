package com.example.challengeCupon.challengeCupon.adapter.model

import com.example.challengeCupon.challengeCupon.application.usecase.model.CouponCalculate

class CouponCalculateResponse(
    private var itemsIds: List<String>,
    private var total: Float
) {

    fun getItemsIds(): List<String>{
        return itemsIds
    }

    fun getTotal(): Float {
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