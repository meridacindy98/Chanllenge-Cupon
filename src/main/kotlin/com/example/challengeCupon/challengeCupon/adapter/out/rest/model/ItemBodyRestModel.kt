package com.example.challengeCupon.challengeCupon.adapter.out.rest.model

import com.example.challengeCupon.challengeCupon.application.usecase.model.Item
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class ItemBodyRestModel(
    private var id: String,
    private var price: Float? = null,

    private var message: String? = null,
    private var error: String? = null,
    private var status: Int? = null,
) {

    fun getId(): String {
        return id
    }

    fun getPrice(): Float? {
        return price
    }

    fun getMessage(): String? {
        return message
    }

    fun getError(): String? {
        return error
    }

    fun getStatus(): Int? {
        return status
    }

    fun toDomain(): Item {
        return Item(
            id,
            price!!
        )
    }

}