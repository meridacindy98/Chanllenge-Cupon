package com.example.challengeCupon.challengeCupon.adapter.`in`.controller

import com.example.challengeCupon.challengeCupon.adapter.model.CouponCalculateResponse
import com.example.challengeCupon.challengeCupon.adapter.model.CouponRequest
import com.example.challengeCupon.challengeCupon.application.port.`in`.CalculateCouponCommand
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/coupon")
class CouponController(
    private val calculateCouponCommand: CalculateCouponCommand,
) {

    @PostMapping("/")
    fun calculateCoupon(@RequestBody body: CouponRequest): ResponseEntity<CouponCalculateResponse?> {
        val couponCalculate = calculateCouponCommand.execute(buildCalculateCouponCommand(body))
        val couponCalculateResponse = CouponCalculateResponse.fromDomain(couponCalculate)
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(couponCalculateResponse)
    }

    private fun buildCalculateCouponCommand(body: CouponRequest): CalculateCouponCommand.Command {
        return CalculateCouponCommand.Command(
            body.getItemsIds(),
            body.getAmount()
        )
    }


}