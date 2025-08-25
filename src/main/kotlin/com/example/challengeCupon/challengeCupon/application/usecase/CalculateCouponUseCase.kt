package com.example.challengeCupon.challengeCupon.application.usecase

import com.example.challengeCupon.challengeCupon.application.port.`in`.CalculateCouponCommand
import com.example.challengeCupon.challengeCupon.application.port.out.GetItemsByIdsRepository
import com.example.challengeCupon.challengeCupon.application.usecase.model.Coupon
import com.example.challengeCupon.challengeCupon.application.usecase.model.CouponCalculate
import org.springframework.stereotype.Component

@Component
class CalculateCouponUseCase(
    private val getItemsByIdsRepository: GetItemsByIdsRepository
): CalculateCouponCommand {

    override fun execute(command: CalculateCouponCommand.Command): CouponCalculate {
        val coupon = buildCoupon(command)
        val itemsWithPrices = getItemsByIdsRepository.execute(coupon.getItemsIds())
        val itemsSorted = itemsWithPrices.sortedBy { it.getPrice() }
        var amount = coupon.getAmount()

        val itemsAvailable = itemsSorted.filter { item ->
            val canAfford = item.getPrice() <= amount
            if(canAfford){
                amount -= item.getPrice()
            }
            canAfford
        }

        val total = itemsAvailable.sumOf { it.getPrice().toDouble() }.toFloat()
        val itemsAvailableIds = itemsAvailable.map { it.getId() }

        return CouponCalculate(itemsAvailableIds, total)
    }

    private fun buildCoupon(command: CalculateCouponCommand.Command): Coupon {
        return Coupon(
            command.getItemsIds(),
            command.getAmount()
        )
    }

}