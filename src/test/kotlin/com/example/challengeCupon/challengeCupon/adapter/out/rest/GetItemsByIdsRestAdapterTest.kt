package com.example.challengeCupon.challengeCupon.adapter.out.rest

import com.example.challengeCupon.challengeCupon.adapter.exception.NotAvailableException
import com.example.challengeCupon.challengeCupon.adapter.out.rest.model.ItemBodyRestModel
import com.example.challengeCupon.challengeCupon.adapter.out.rest.model.ItemResponseRestModel
import com.example.challengeCupon.challengeCupon.application.usecase.model.Item
import com.example.challengeCupon.challengeCupon.shared.config.model.MercadoLibreApiProperties
import com.example.challengeCupon.challengeCupon.shared.config.model.RestConfigurationProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal

class GetItemsByIdsRestAdapterTest {

    @Mock
    lateinit var restTemplate: RestTemplate

    private lateinit var getItemsByIdsRestAdapter: GetItemsByIdsRestAdapter

    private val TOKEN = "6741818393598987-082519-2b47b130ea63972a7187ec11a3e54166-189519089"
    private val URL = "http://mercadolibreapi.com"
    private val PATH = "/items?ids=MLA1,MLA2,MLA3"
    private val itemsIds = listOf("MLA1", "MLA2", "MLA3")
    private val FULL_URL = "$URL$PATH"

    @BeforeEach
    fun setup() {
        val mercadoLibreApiProperties = MercadoLibreApiProperties()
        mercadoLibreApiProperties.url = URL
        mercadoLibreApiProperties.token = TOKEN
        val restConfigurationProperties = RestConfigurationProperties()
        restConfigurationProperties.mercadoLibreApi = mercadoLibreApiProperties
        MockitoAnnotations.openMocks(this)
        getItemsByIdsRestAdapter = GetItemsByIdsRestAdapter(restTemplate, restConfigurationProperties)
    }

    @Test
    fun `GIVEN items ids WHEN rest adapter is executed THEN response is list of items`() {
        val itemsIds = itemsIds

        val mockResponse = listOf(
            ItemResponseRestModel(
                code = HttpStatus.OK.value(),
                body = ItemBodyRestModel("MLA1", BigDecimal("100.00"))
            ),
            ItemResponseRestModel(
                code = HttpStatus.OK.value(),
                body = ItemBodyRestModel("MLA2", BigDecimal("200.00"))
            ),
            ItemResponseRestModel(
                code = HttpStatus.OK.value(),
                body = ItemBodyRestModel("MLA3", BigDecimal("300.00"))
            )
        )
        val responseJson = jacksonObjectMapper().writeValueAsString(mockResponse)

        Mockito.`when`(
            restTemplate.exchange(
                eq(FULL_URL),
                eq(HttpMethod.GET),
                any(),
                eq(String::class.java)
            )
        ).thenReturn(ResponseEntity(responseJson, HttpStatus.OK))

        val result = getItemsByIdsRestAdapter.execute(itemsIds)

        assertEquals(3, result.size)
        assertEquals(Item("MLA1", BigDecimal("100.00")), result[0])
        assertEquals(Item("MLA2", BigDecimal("200.00")), result[1])
        assertEquals(Item("MLA3", BigDecimal("300.00")), result[2])
    }

    @Test
    fun `GIVEN items ids WHEN one item has a status code error THEN throws NotAvailableException`() {
        val mockResponse = listOf(
            ItemResponseRestModel(
                code = HttpStatus.OK.value(),
                body = ItemBodyRestModel("MLA1", BigDecimal("100.00"))
            ),
            ItemResponseRestModel(
                code = HttpStatus.NOT_FOUND.value(),
                body = ItemBodyRestModel(
                    id = "MLA2",
                    message = "Item with id MLA1 not found",
                    error = "not_found",
                    status = 404
                )
            ),
            ItemResponseRestModel(
                code = HttpStatus.OK.value(),
                body = ItemBodyRestModel("MLA3", BigDecimal("300.00"))
            )
        )
        val responseJson = jacksonObjectMapper().writeValueAsString(mockResponse)

        Mockito.`when`(
            restTemplate.exchange(
                eq(FULL_URL),
                eq(HttpMethod.GET),
                any(),
                eq(String::class.java)
            )
        ).thenReturn(ResponseEntity(responseJson, HttpStatus.OK))

        val result = getItemsByIdsRestAdapter.execute(itemsIds)

        assertEquals(2, result.size)
        assertEquals(Item("MLA1", BigDecimal("100.00")), result[0])
        assertEquals(Item("MLA3", BigDecimal("300.00")), result[1])
    }

    @Test
    fun `GIVEN items ids WHEN ML throws RestClientException THEN throws NotAvailableException`() {
        Mockito.`when`(
            restTemplate.exchange(
                eq(FULL_URL),
                eq(HttpMethod.GET),
                any(),
                eq(String::class.java)
            )
        ).thenThrow(HttpClientErrorException(HttpStatus.SERVICE_UNAVAILABLE, "Mercado Libre Api is down"))

        assertThrows(NotAvailableException::class.java) {
            getItemsByIdsRestAdapter.execute(itemsIds)
        }

    }

}