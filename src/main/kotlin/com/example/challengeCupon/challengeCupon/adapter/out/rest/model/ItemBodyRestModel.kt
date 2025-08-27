package com.example.challengeCupon.challengeCupon.adapter.out.rest.model

import com.example.challengeCupon.challengeCupon.application.usecase.model.Item
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.math.BigDecimal

@JsonIgnoreProperties(ignoreUnknown = true)
class ItemBodyRestModel(
    private var id: String,
    private var price: BigDecimal? = null,

    private var message: String? = null,
    private var error: String? = null,
) {

    fun getId(): String {
        return id
    }

    fun getPrice(): BigDecimal? {
        return price
    }

    fun getMessage(): String? {
        return message
    }

    fun getError(): String? {
        return error
    }

    fun toDomain(): Item {
        return Item(
            id,
            price!!
        )
    }

}