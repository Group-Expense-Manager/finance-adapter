package pl.edu.agh.gem.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.edu.agh.gem.internal.job.ReconciliationJobConsumer
import pl.edu.agh.gem.internal.job.ReconciliationJobFinder
import pl.edu.agh.gem.internal.job.ReconciliationJobProcessor
import pl.edu.agh.gem.internal.persistence.ReconciliationJobRepository
import pl.edu.agh.gem.threads.ExecutorConfig
import pl.edu.agh.gem.threads.ExecutorFactory
import java.time.Duration
import java.util.concurrent.Executor

@Configuration
class ReconciliationJobConfig {

    @Bean(destroyMethod = "destroy")
    @ConditionalOnProperty(prefix = RECONCILIATION_PROCESSOR_PREFIX, name = ["enabled"], havingValue = "true")
    fun financialReconciliationJobConsumer(
        jobConsumerExecutor: Executor,
        reconciliationJobFinder: ReconciliationJobFinder,
        reconciliationJobProcessor: ReconciliationJobProcessor,
    ): ReconciliationJobConsumer {
        val reconciliationJobConsumer = ReconciliationJobConsumer(
            reconciliationJobFinder,
            reconciliationJobProcessor,
        )
        reconciliationJobConsumer.consume(jobConsumerExecutor)
        return reconciliationJobConsumer
    }

    @Bean
    fun financialReconciliationJobFinder(
        jobProducerExecutor: Executor,
        reconciliationJobProcessorProperties: ReconciliationJobProcessorProperties,
        reconciliationJobRepository: ReconciliationJobRepository,
    ) = ReconciliationJobFinder(
        jobProducerExecutor,
        reconciliationJobRepository,
        reconciliationJobProcessorProperties,
    )

    @Bean
    fun jobConsumerExecutor(
        executorFactory: ExecutorFactory,
        settings: ReconciliationJobExecutorProperties,
    ): Executor {
        val config = ExecutorConfig(
            corePoolSize = settings.corePoolSize,
            maxPoolSize = settings.maxPoolSize,
            taskQueueSize = settings.queueCapacity,
            threadPoolName = CONSUMER_POOL,
        )
        return executorFactory.createExecutor(config)
    }

    @Bean
    fun jobProducerExecutor(
        executorFactory: ExecutorFactory,
        settings: ReconciliationJobProducerProperties,
    ): Executor {
        val config = ExecutorConfig(
            corePoolSize = settings.corePoolSize,
            maxPoolSize = settings.maxPoolSize,
            taskQueueSize = settings.queueCapacity,
            threadPoolName = PRODUCER_POOL,
        )
        return executorFactory.createExecutor(config)
    }

    companion object {
        private const val CONSUMER_POOL = "reconciliation-job-consumer-pool"
        private const val PRODUCER_POOL = "reconciliation-job-producer-pool"
        private const val RECONCILIATION_PROCESSOR_PREFIX = "reconciliation-job-processor"
    }
}

@ConfigurationProperties("reconciliation-job-executor")
data class ReconciliationJobExecutorProperties(
    val corePoolSize: Int,
    val maxPoolSize: Int,
    val queueCapacity: Int,
)

@ConfigurationProperties("reconciliation-job-producer")
data class ReconciliationJobProducerProperties(
    val corePoolSize: Int,
    val maxPoolSize: Int,
    val queueCapacity: Int,
)

@ConfigurationProperties("reconciliation-job-processor")
data class ReconciliationJobProcessorProperties(
    val lockTime: Duration,
    val emptyCandidateDelay: Duration,
    val retryDelays: List<Duration>,
)
