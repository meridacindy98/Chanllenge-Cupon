package com.example.challengeCupon.challengeCupon.application.port.out

import com.example.challengeCupon.challengeCupon.application.usecase.model.Item

interface GetItemsByIdsRepository {
    fun execute(itemsIds: List<String>): List<Item>
}