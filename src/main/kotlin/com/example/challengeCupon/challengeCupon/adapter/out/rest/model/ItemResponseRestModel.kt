package com.example.challengeCupon.challengeCupon.adapter.out.rest.model

import com.example.challengeCupon.challengeCupon.application.usecase.model.Item
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class ItemResponseRestModel(
    private var code: Int,
    private var body: ItemBodyRestModel?
) {

    fun getCode(): Int {
        return code
    }

    fun getBody(): ItemBodyRestModel? {
        return body
    }

}