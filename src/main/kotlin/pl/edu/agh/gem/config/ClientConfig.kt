package pl.edu.agh.gem.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import pl.edu.agh.gem.helper.http.GemRestTemplateFactory
import java.time.Duration

@Configuration
class ClientConfig {

    @Bean
    @Qualifier("GroupManagerRestTemplate")
    fun groupManagerRestTemplate(
        groupManagerProperties: GroupManagerProperties,
        gemRestTemplateFactory: GemRestTemplateFactory,
    ): RestTemplate {
        return gemRestTemplateFactory
            .builder()
            .withReadTimeout(groupManagerProperties.readTimeout)
            .withConnectTimeout(groupManagerProperties.connectTimeout)
            .build()
    }

    @Bean
    @Qualifier("ExpenseManagerRestTemplate")
    fun expenseManagerRestTemplate(
        expenseManagerProperties: ExpenseManagerProperties,
        gemRestTemplateFactory: GemRestTemplateFactory,
    ): RestTemplate {
        return gemRestTemplateFactory
            .builder()
            .withReadTimeout(expenseManagerProperties.readTimeout)
            .withConnectTimeout(expenseManagerProperties.connectTimeout)
            .build()
    }

    @Bean
    @Qualifier("PaymentManagerRestTemplate")
    fun paymentManagerRestTemplate(paymentManagerProperties: PaymentManagerProperties): RestTemplate {
        return RestTemplateBuilder()
            .setConnectTimeout(paymentManagerProperties.connectTimeout)
            .setReadTimeout(paymentManagerProperties.readTimeout)
            .build()
    }
}

@ConfigurationProperties(prefix = "group-manager")
data class GroupManagerProperties(
    val url: String,
    val connectTimeout: Duration,
    val readTimeout: Duration,
)

@ConfigurationProperties(prefix = "expense-manager")
data class ExpenseManagerProperties(
    val url: String,
    val connectTimeout: Duration,
    val readTimeout: Duration,
)

@ConfigurationProperties(prefix = "payment-manager")
data class PaymentManagerProperties(
    val url: String,
    val connectTimeout: Duration,
    val readTimeout: Duration,
)
