package com.example.challengeCupon.challengeCupon.adapter.out.rest

import com.example.challengeCupon.challengeCupon.adapter.exception.NotAvailableException
import com.example.challengeCupon.challengeCupon.adapter.out.rest.model.ItemResponseRestModel
import com.example.challengeCupon.challengeCupon.application.port.out.GetItemsByIdsRepository
import com.example.challengeCupon.challengeCupon.application.usecase.model.Item
import com.example.challengeCupon.challengeCupon.shared.config.model.RestConfigurationProperties
import com.example.challengeCupon.challengeCupon.shared.model.ErrorDescription
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

@Component
class GetItemsByIdsRestAdapter(
    private val restTemplate: RestTemplate,
    private val restConfiguration: RestConfigurationProperties

): GetItemsByIdsRepository {

    private val log = LoggerFactory.getLogger(this::class.java)

    private val mercadoLibreApi = restConfiguration.mercadoLibreApi.url
    private val itemsPath = "/items"


    override fun execute(itemsIds: List<String>): List<Item> {
        try {
            val formattedIds = itemsIds.joinToString(separator = ",")
            log.info("Getting items with ids: $formattedIds")

            val headers = HttpHeaders().apply {
                setBearerAuth(restConfiguration.mercadoLibreApi.token)
            }

            val httpEntity = HttpEntity<Void>(headers)

            val response = restTemplate.exchange(
                "$mercadoLibreApi$itemsPath?ids=$formattedIds",
                HttpMethod.GET,
                httpEntity,
                String::class.java
            )

            if (!response.statusCode.is2xxSuccessful) {
                log.error("Failed to fetch items. Status: ${response.statusCode}")
                throw NotAvailableException("Mercado Libre API service is not available")
            }

            val responseBody: String? = response.body
            val responseData: List<ItemResponseRestModel>?  = jacksonObjectMapper().readValue(responseBody ?: "[]")

            responseData?.forEach { itemResponse ->
                if(itemResponse.getCode() != HttpStatus.OK.value()){
                    log.error("Item ${itemResponse.getBody()?.getId()} failed with code ${itemResponse.getCode()}")
                    throw NotAvailableException(
                        "Item ${itemResponse.getBody()?.getId()} not available. Error: ${itemResponse.getBody()?.getMessage()}"
                    )
                }
            }

            return responseData?.map { itemResponse ->
                val body = itemResponse.getBody()
                Item(
                    id = body?.getId()!!,
                    price = body.getPrice()!!,
                )
            } ?: throw NotAvailableException("The API response came back empty.")

        } catch (ex: HttpClientErrorException) {
            log.error("Mercado Libre Api service unavailable error: ", ex)
            throw NotAvailableException(ErrorDescription.UNHANDLED.value)

        }
    }

}