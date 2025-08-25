package com.example.challengeCupon.challengeCupon.adapter.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class CouponRequest(
    private var itemIds: List<String>,
    private var amount: Float
) {
    fun getItemsIds(): List<String>{
        return itemIds
    }

    fun getAmount(): Float {
        return amount
    }
}