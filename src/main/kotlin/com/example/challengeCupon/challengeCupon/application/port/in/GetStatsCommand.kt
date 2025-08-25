package com.example.challengeCupon.challengeCupon.application.port.`in`

import com.example.challengeCupon.challengeCupon.application.usecase.model.Stats

interface GetStatsCommand {

    fun execute(command:Command) : Stats

    class Command(
        private var id: String,
        private var quantity: Int
    )

}