package com.example.challengeCupon.challengeCupon.shared.config.model

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "spring.services")
class RestConfigurationProperties {
    var mercadoLibreApi: MercadoLibreApiProperties = MercadoLibreApiProperties()
}