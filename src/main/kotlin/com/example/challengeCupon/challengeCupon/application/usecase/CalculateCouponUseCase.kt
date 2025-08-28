package com.example.challengeCupon.challengeCupon.application.usecase

import com.example.challengeCupon.challengeCupon.adapter.exception.BadRequestException
import com.example.challengeCupon.challengeCupon.adapter.exception.UnprocessableException
import com.example.challengeCupon.challengeCupon.application.port.`in`.CalculateCouponCommand
import com.example.challengeCupon.challengeCupon.application.port.out.GetItemsByIdsRepository
import com.example.challengeCupon.challengeCupon.application.usecase.model.Choice
import com.example.challengeCupon.challengeCupon.application.usecase.model.Coupon
import com.example.challengeCupon.challengeCupon.application.usecase.model.CouponCalculate
import com.example.challengeCupon.challengeCupon.application.usecase.model.Item
import com.example.challengeCupon.challengeCupon.shared.model.ErrorDescription
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode

@Component
class CalculateCouponUseCase(
    private val getItemsByIdsRepository: GetItemsByIdsRepository
): CalculateCouponCommand {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun execute(command: CalculateCouponCommand.Command): CouponCalculate {
        validateCommand(command)

        val coupon = buildCoupon(command)

        val itemsWithPrices = getItemsByIdsRepository.execute(coupon.getItemsIds())

        val (chosenIds, total) = getAvailableItems(itemsWithPrices, coupon.getAmount())

        if (chosenIds.isEmpty()) {
            log.error("${ErrorDescription.INSUFFICIENT_AMOUNT.value}. amount=${coupon.getAmount()}")
            throw UnprocessableException(ErrorDescription.INSUFFICIENT_AMOUNT.value)
        }

        return CouponCalculate(chosenIds, total)
    }

    private fun validateCommand(command: CalculateCouponCommand.Command){

        val items = command.getItemsIds().map { it.trim() }
        val amount = command.getAmount().stripTrailingZeros()
        val itemsDuplicates = getItemsDuplicates(command)

        when {
            items.isEmpty() -> {
                log.error(ErrorDescription.EMPTY_ITEMS.value)
                throw BadRequestException(ErrorDescription.EMPTY_ITEMS.value)
            }

            items.any { it.isBlank() } -> {
                log.error(ErrorDescription.BLANK_ITEM_ID.value)
                throw BadRequestException(ErrorDescription.BLANK_ITEM_ID.value)
            }

            items.size > 100 -> {
                log.error(ErrorDescription.TOO_MANY_ITEMS.value)
                throw BadRequestException(ErrorDescription.TOO_MANY_ITEMS.value)
            }

            amount.scale() > 2 -> {
                log.error(ErrorDescription.WRONG_AMOUNT.value)
                throw BadRequestException(ErrorDescription.WRONG_AMOUNT.value)
            }

            amount > BigDecimal("1000000") -> {
                log.error(ErrorDescription.AMOUNT_TOO_LARGE.value)
                throw BadRequestException(ErrorDescription.AMOUNT_TOO_LARGE.value)
            }

            amount <= BigDecimal("0") -> {
                log.error(ErrorDescription.NON_POSITIVE_AMOUNT.value)
                throw BadRequestException(ErrorDescription.NON_POSITIVE_AMOUNT.value)
            }

            itemsDuplicates.isNotEmpty() -> {
                log.error(ErrorDescription.DUPLICATE_ITEMS.value + itemsDuplicates)
                throw BadRequestException(ErrorDescription.DUPLICATE_ITEMS.value + itemsDuplicates)
            }
        }

    }


    private fun getItemsDuplicates(command: CalculateCouponCommand.Command): Set<String>{
        return command.getItemsIds()
            .map { it.trim() }
            .groupingBy { it }
            .eachCount()
            .filter { it.value > 1 }
            .keys
    }

    private fun buildCoupon(command: CalculateCouponCommand.Command): Coupon {
        return Coupon(
            command.getItemsIds(),
            command.getAmount()
        )
    }

    private fun toCents(amount: BigDecimal): Long =
        amount.setScale(2, RoundingMode.HALF_UP)
            .movePointRight(2)
            .longValueExact()

    private fun fromCents(cents: Long): BigDecimal =
        BigDecimal(cents)
            .movePointLeft(2)
            .setScale(2, RoundingMode.UNNECESSARY)

    private fun getAvailableItems(items: List<Item>, amount: BigDecimal): Pair<List<String>, BigDecimal> {
        val maxAmountInCents = toCents(amount)
        if (maxAmountInCents <= 0L || items.isEmpty()) return emptyList<String>() to BigDecimal.ZERO

        val itemPricesInCents: List<Pair<String, Long>> = items.mapNotNull { item ->
            val id = item.getId()
            val price = item.getPrice()
            if (id.isNotBlank() && price >= BigDecimal.ZERO) id to toCents(price) else null
        }
        if (itemPricesInCents.isEmpty()) return emptyList<String>() to BigDecimal.ZERO

        val reachableSums = HashSet<Long>().apply { add(0L) }

        val backtrackingMap = HashMap<Long, Choice>()

        for ((itemId, priceInCents) in itemPricesInCents) {
            val snapshotOfSums = ArrayList(reachableSums)
            for (currentSum in snapshotOfSums) {
                val newSum = currentSum + priceInCents
                if (newSum <= maxAmountInCents && newSum !in reachableSums) {
                    reachableSums.add(newSum)
                    backtrackingMap[newSum] = Choice(prevSum = currentSum, itemId = itemId)
                }
            }
        }

        val maxReachableSum = reachableSums.maxOrNull() ?: 0L
        if (maxReachableSum == 0L) return emptyList<String>() to BigDecimal.ZERO

        val chosenItemIds = ArrayList<String>()
        var currentSum = maxReachableSum
        while (currentSum != 0L) {
            val choice = backtrackingMap[currentSum] ?: break
            chosenItemIds.add(choice.itemId)
            currentSum = choice.prevSum
        }
        chosenItemIds.reverse()

        return chosenItemIds to fromCents(maxReachableSum)
    }

}