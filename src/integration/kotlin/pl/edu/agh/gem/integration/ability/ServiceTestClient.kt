package pl.edu.agh.gem.integration.ability

import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec
import org.springframework.test.web.servlet.client.MockMvcWebTestClient.bindToApplicationContext
import org.springframework.web.context.WebApplicationContext
import pl.edu.agh.gem.headers.HeadersUtils.withAppAcceptType
import pl.edu.agh.gem.headers.HeadersUtils.withValidatedUser
import pl.edu.agh.gem.internal.model.finance.ActivityStatus
import pl.edu.agh.gem.internal.model.finance.ActivityType
import pl.edu.agh.gem.internal.model.finance.filter.SortOrder
import pl.edu.agh.gem.internal.model.finance.filter.SortedBy
import pl.edu.agh.gem.paths.Paths.EXTERNAL
import pl.edu.agh.gem.security.GemUser
import java.util.Optional

@Component
@Lazy
class ServiceTestClient(applicationContext: WebApplicationContext) {
    private val webClient = bindToApplicationContext(applicationContext)
        .configureClient()
        .build()

    fun getActivities(
        gemUser: GemUser,
        groupId: String,
        title: String? = null,
        status: ActivityStatus? = null,
        isCreator: Boolean? = null,
        type: ActivityType? = null,
        sortedBy: SortedBy? = null,
        sortOrder: SortOrder? = null,

    ): ResponseSpec {
        return webClient.get()
            .uri {
                it.path("$EXTERNAL/activities/groups/$groupId")
                    .queryParamIfPresent("title", Optional.ofNullable(title))
                    .queryParamIfPresent("type", Optional.ofNullable(type))
                    .queryParamIfPresent("status", Optional.ofNullable(status))
                    .queryParamIfPresent("isCreator", Optional.ofNullable(isCreator))
                    .queryParamIfPresent("sortedBy", Optional.ofNullable(sortedBy))
                    .queryParamIfPresent("sortOrder", Optional.ofNullable(sortOrder))
                    .build()
            }
            .headers { it.withValidatedUser(gemUser).withAppAcceptType() }
            .exchange()
    }
}
