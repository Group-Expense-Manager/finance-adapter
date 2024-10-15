package pl.edu.agh.gem.integration.persistence

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import pl.edu.agh.gem.config.ReconciliationJobProcessorProperties
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.internal.persistence.MissingReconciliationJobException
import pl.edu.agh.gem.internal.persistence.ReconciliationJobRepository
import pl.edu.agh.gem.util.createReconciliationJob

class ReconciliationJobRepositoryIT(
    private val reconciliationJobRepository: ReconciliationJobRepository,
    private val reconciliationJobProcessorProperties: ReconciliationJobProcessorProperties,
) : BaseIntegrationSpec({

    should("save and find reconciliation job by id") {
        // given
        val reconciliationJob = createReconciliationJob()

        // when
        val savedJob = reconciliationJobRepository.save(reconciliationJob)

        // then
        savedJob.id shouldBe reconciliationJob.id

        // when
        val foundJob = reconciliationJobRepository.findById(reconciliationJob.id)

        // then
        foundJob.shouldNotBeNull()
        foundJob.id shouldBe reconciliationJob.id
    }

    should("find and lock job to process") {
        // given
        val reconciliationJob = createReconciliationJob(
            nextProcessAt = FIXED_TIME,
        )
        reconciliationJobRepository.save(reconciliationJob)

        // when
        reconciliationJobRepository.findJobToProcessAndLock()
        val jobToProcess = reconciliationJobRepository.findById(reconciliationJob.id)

        // then
        jobToProcess.shouldNotBeNull()
        jobToProcess.id shouldBe reconciliationJob.id
        jobToProcess.nextProcessAt shouldBe FIXED_TIME.plus(reconciliationJobProcessorProperties.lockTime)
    }

    should("update nextProcessAt and retry count") {
        // given
        val reconciliationJob = createReconciliationJob(
            nextProcessAt = FIXED_TIME,
            retry = 0L,
        )
        reconciliationJobRepository.save(reconciliationJob)

        // when
        val updatedJob = reconciliationJobRepository.updateNextProcessAtAndRetry(reconciliationJob)

        // then
        updatedJob.shouldNotBeNull()
        updatedJob.nextProcessAt shouldBe FIXED_TIME.plus(reconciliationJobProcessorProperties.retryDelays.first())
        updatedJob.retry shouldBe 1
    }

    should("remove reconciliation job") {
        // given
        val reconciliationJob = createReconciliationJob()
        reconciliationJobRepository.save(reconciliationJob)

        // when
        reconciliationJobRepository.remove(reconciliationJob)

        // then
        val foundJob = reconciliationJobRepository.findById(reconciliationJob.id)
        foundJob.shouldBeNull()
    }

    should("throw MissingReconciliationJobException when updating non-existing job") {
        // given
        val nonExistingJob = createReconciliationJob(id = "non-existing-id", nextProcessAt = FIXED_TIME, retry = 0L)

        // when & then
        shouldThrow<MissingReconciliationJobException> {
            reconciliationJobRepository.updateNextProcessAtAndRetry(nonExistingJob)
        }
    }

    should("return null when finding non-existing job by id") {
        // given
        val nonExistingJobId = "non-existing-id"

        // when
        val foundJob = reconciliationJobRepository.findById(nonExistingJobId)

        // then
        foundJob.shouldBeNull()
    }

    should("return null when no job to process is found") {
        // given
        val reconciliationJob = createReconciliationJob(
            nextProcessAt = FIXED_TIME.plusSeconds(3600),
        )
        reconciliationJobRepository.save(reconciliationJob)

        // when
        val jobToProcess = reconciliationJobRepository.findJobToProcessAndLock()

        // then
        jobToProcess.shouldBeNull()
    }

    should("cancel all jobs for group and currency") {
        // given
        val reconciliationJob = createReconciliationJob(groupId = "group1", currency = "USD")
        reconciliationJobRepository.save(reconciliationJob)

        // when
        reconciliationJobRepository.cancelAllJobsForGroupWithCurrency("group1", "USD")

        // then
        val foundJob = reconciliationJobRepository.findById(reconciliationJob.id)
        foundJob?.canceled shouldBe true
    }

    should("remove canceled job") {
        // given
        val reconciliationJob = createReconciliationJob(groupId = "group1", currency = "USD", canceled = true)
        reconciliationJobRepository.save(reconciliationJob)

        // when
        val result = reconciliationJobRepository.removeIfCanceled(reconciliationJob.id)

        // then
        result shouldBe true
        val foundJob = reconciliationJobRepository.findById(reconciliationJob.id)
        foundJob.shouldBeNull()
    }

    should("not remove not canceled job") {
        // given
        val reconciliationJob = createReconciliationJob(groupId = "group1", currency = "USD", canceled = false)
        reconciliationJobRepository.save(reconciliationJob)

        // when
        val result = reconciliationJobRepository.removeIfCanceled(reconciliationJob.id)

        // then
        result shouldBe false
        val foundJob = reconciliationJobRepository.findById(reconciliationJob.id)
        foundJob.shouldNotBeNull()
    }
},)
