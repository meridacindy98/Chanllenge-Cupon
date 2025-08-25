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
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import javax.naming.ServiceUnavailableException

class GetItemsByIdsRestAdapterTest {

    @Mock
    lateinit var restTemplate: RestTemplate

    private lateinit var getItemsByIdsRestAdapter: GetItemsByIdsRestAdapter

    private val URL = "http://mercadolibreapi.com"
    private val PATH = "/items?ids=MLA1,MLA2,MLA3"
    private val itemsIds = listOf("MLA1", "MLA2", "MLA3")

    @BeforeEach
    fun setup(){
        val mercadoLibreApiProperties = MercadoLibreApiProperties()
        mercadoLibreApiProperties.url = URL
        val restConfigurationProperties = RestConfigurationProperties()
        restConfigurationProperties.mercadoLibreApi = mercadoLibreApiProperties
        MockitoAnnotations.openMocks(this)
        getItemsByIdsRestAdapter = GetItemsByIdsRestAdapter(restTemplate, restConfigurationProperties)
    }

    @Test
    fun `GIVEN items ids WHEN rest adapter is executed THEN response is list of items`() {
        // given
        val itemsIds = itemsIds

        val mockResponse = listOf(
            ItemResponseRestModel(
                code = HttpStatus.OK.value(),
                body = ItemBodyRestModel("MLA1", 100.0F)
            ),
            ItemResponseRestModel(
                code = HttpStatus.OK.value(),
                body = ItemBodyRestModel("MLA2", 200.0F)
            ),
            ItemResponseRestModel(
                code = HttpStatus.OK.value(),
                body = ItemBodyRestModel("MLA3", 300.0F)
            )
        )
        val responseJson = jacksonObjectMapper().writeValueAsString(mockResponse)

        Mockito.`when`(restTemplate.getForEntity("${URL}${PATH}", String::class.java))
            .thenReturn(ResponseEntity(responseJson, HttpStatus.OK))

        // when
        val result = getItemsByIdsRestAdapter.execute(itemsIds)

        // then
        assertEquals(2, result.size)
        assertEquals(Item("MLA1", 100.0F), result[0])
        assertEquals(Item("MLA2", 200.0F), result[1])
    }

    @Test
    fun `GIVEN items ids WHEN one item has a status code error THEN throws NotAvailableException`() {
        //given
        val mockResponse = listOf(
            ItemResponseRestModel(
                code = HttpStatus.OK.value(),
                body = ItemBodyRestModel("MLA1", 100.0F)
            ),
            ItemResponseRestModel(
                code = HttpStatus.NOT_FOUND.value(),
                body = ItemBodyRestModel(id = "MLA2", message = "Item with id MLA1 not found", error = "not_found", status = 404)
            ),
            ItemResponseRestModel(
                code = HttpStatus.OK.value(),
                body = ItemBodyRestModel("MLA3", 300.0F)
            )
        )
        val responseJson = jacksonObjectMapper().writeValueAsString(mockResponse)

        Mockito.`when`(restTemplate.getForEntity("${URL}${PATH}", String::class.java))
            .thenReturn(ResponseEntity(responseJson, HttpStatus.OK))

        //when & then
        assertThrows(NotAvailableException::class.java) {
            getItemsByIdsRestAdapter.execute(itemsIds)
        }
    }

    @Test
    fun `GIVEN items ids WHEN call Mercado Libre Api and is down THEN throws NotAvailableException`() {
        // Given
        Mockito.`when`(restTemplate.getForEntity("$URL$PATH", String::class.java))
            .thenThrow(HttpClientErrorException(HttpStatus.SERVICE_UNAVAILABLE, "Service is down"))

        // When & Then
        assertThrows(NotAvailableException::class.java) {
            getItemsByIdsRestAdapter.execute(itemsIds)
        }
    }

}