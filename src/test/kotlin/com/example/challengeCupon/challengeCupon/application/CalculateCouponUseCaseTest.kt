package com.example.challengeCupon.challengeCupon.application

import com.example.challengeCupon.challengeCupon.adapter.exception.BadRequestException
import com.example.challengeCupon.challengeCupon.adapter.exception.NotAvailableException
import com.example.challengeCupon.challengeCupon.application.port.`in`.CalculateCouponCommand
import com.example.challengeCupon.challengeCupon.application.port.out.GetItemsByIdsRepository
import com.example.challengeCupon.challengeCupon.application.usecase.CalculateCouponUseCase
import com.example.challengeCupon.challengeCupon.application.usecase.model.Item
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class CalculateCouponUseCaseTest {

    @Mock
    private lateinit var getItemsByIdsRepository: GetItemsByIdsRepository

    private lateinit var calculateCouponUseCase: CalculateCouponUseCase

    @BeforeEach
    fun setup(){
        MockitoAnnotations.openMocks(this)
        calculateCouponUseCase = CalculateCouponUseCase(getItemsByIdsRepository)
    }

    @Test
    fun `GIVEN items with prices and a coupon of 500 WHEN the use case is executed THEN it returns the optimal items with total dont exceed the coupon`() {
        val itemsIds = listOf("MLA1","MLA2","MLA3","MLA4","MLA5")
        val amount = BigDecimal("500.00")

        Mockito.`when`(getItemsByIdsRepository.execute(itemsIds)).thenReturn(
            listOf(
                Item("MLA1", BigDecimal("100.00")),
                Item("MLA2",BigDecimal("210.00")),
                Item("MLA3",BigDecimal("260.00")),
                Item("MLA4",BigDecimal("80.00")),
                Item("MLA5",BigDecimal("90.00")),
            )
        )

        val command = CalculateCouponCommand.Command(
            itemsIds,
            amount
        )

        val result = calculateCouponUseCase.execute(command)

        assertEquals(listOf("MLA4","MLA5","MLA1","MLA2"), result.getItemsIds())
        assertEquals(BigDecimal("480.00"), result.getTotal())
    }

    @Test
    fun `GIVEN items with prices and a coupon of 500 WHEN the use case is executed THEN it returns the optimal items with total dont exceed the coupon and is de max`() {
        val itemsIds = listOf("MLA1","MLA2","MLA3","MLA4","MLA5")
        val amount = BigDecimal("500.00")

        Mockito.`when`(getItemsByIdsRepository.execute(itemsIds)).thenReturn(
            listOf(
                Item("MLA1", BigDecimal("250.00")),
                Item("MLA2",BigDecimal("250.00")),
                Item("MLA3",BigDecimal("100.00")),
                Item("MLA4",BigDecimal("100.00")),
                Item("MLA5",BigDecimal("290.00")),
            )
        )

        val command = CalculateCouponCommand.Command(
            itemsIds,
            amount
        )

        val result = calculateCouponUseCase.execute(command)

        assertEquals(listOf("MLA1","MLA2"), result.getItemsIds())
        assertEquals(BigDecimal("500.00"), result.getTotal())
    }

    @Test
    fun `GIVEN items that are all more expensive than the coupon amount WHEN the use case is executed THEN it returns an empty list of items with total 0`() {
        val itemsIds = listOf("MLA1","MLA2")
        val amount = BigDecimal("50.00")

        Mockito.`when`(getItemsByIdsRepository.execute(itemsIds)).thenReturn(
            listOf(Item("MLA1",BigDecimal("60.00")), Item("MLA2",BigDecimal("70.00")))
        )

        val command = CalculateCouponCommand.Command(
            itemsIds,
            amount
        )

        val result = calculateCouponUseCase.execute(command)

        assertTrue(result.getItemsIds().isEmpty())
        assertEquals(BigDecimal("0"), result.getTotal())
    }

    @Test
    fun `GIVEN duplicated items WHEN executing use case THEN throws BadRequestException`() {
        val command = CalculateCouponCommand.Command(
            listOf("MLA1", "MLA1", "MLA2"),
            BigDecimal("500.00")
        )

        assertThrows(BadRequestException::class.java) {
            calculateCouponUseCase.execute(command)
        }
    }

    @Test
    fun `GIVEN repo fails (meli down) WHEN execute THEN propagates NotAvailableException`() {
        val itemsIds = listOf("MLA1","MLA2")
        Mockito.`when`(getItemsByIdsRepository.execute(itemsIds)).thenThrow(NotAvailableException("ML unavailable"))

        assertThrows(NotAvailableException::class.java) {
            calculateCouponUseCase.execute(CalculateCouponCommand.Command(itemsIds, BigDecimal("500.00")))
        }
    }

    @Test
    fun `GIVEN items with decimal prices with coupon of 100 WHEN the use case is executed THEN it returns the optimal items with total dont exceed the coupon`() {
        val itemsIds = listOf("MLA1","MLA2","MLA3","MLA4")
        val amount = BigDecimal("100.00")

        Mockito.`when`(getItemsByIdsRepository.execute(itemsIds)).thenReturn(
            listOf(Item("MLA1",BigDecimal("33.33")), Item("MLA2",BigDecimal("33.33")), Item("MLA3",BigDecimal("33.33")), Item("MLA4",BigDecimal("33.33")))
        )

        val command = CalculateCouponCommand.Command(
            itemsIds,
            amount
        )

        val result = calculateCouponUseCase.execute(command)

        assertEquals(listOf("MLA1","MLA2","MLA3"), result.getItemsIds())
        assertEquals(BigDecimal("99.99"), result.getTotal())
    }

}