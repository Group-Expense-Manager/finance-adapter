package pl.edu.agh.gem.external.dto.finance

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import pl.edu.agh.gem.external.dto.group.toDto
import pl.edu.agh.gem.helper.group.DummyGroup.GROUP_ID
import pl.edu.agh.gem.util.createReport
import pl.edu.agh.gem.util.createReportActivity
import pl.edu.agh.gem.util.createReportActivityMember

class ReportResponseTest : ShouldSpec({

    should("map ReportActivityMember to dto") {
        // given
        val reportActivityMember = createReportActivityMember()

        // when
        val reportActivityMemberDto = reportActivityMember.toDto()

        // then
        reportActivityMemberDto.also {
            it.userId shouldBe reportActivityMember.userId
            it.value shouldBe reportActivityMember.value
        }
    }

    should("map ReportActivity to dto") {
        // given
        val reportActivity = createReportActivity()

        // when
        val reportActivityDto = reportActivity.toDto()

        // then
        reportActivityDto.also {
            it.title shouldBe reportActivity.title
            it.date shouldBe reportActivity.date
            it.value shouldBe reportActivity.value
            it.currency shouldBe reportActivity.currency.toDto()
            it.members shouldBe reportActivity.members.map { member -> member.toDto() }
        }
    }

    should("map Report to ReportResponse") {
        // given
        val report = createReport()

        // when
        val reportResponse = report.toReportResponse(GROUP_ID)

        // then
        reportResponse.also {
            it.groupId shouldBe GROUP_ID
            it.activities shouldBe report.activities.map { activity -> activity.toDto() }
        }
    }
},)
