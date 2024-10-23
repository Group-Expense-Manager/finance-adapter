package pl.edu.agh.gem.external.dto.finance

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import pl.edu.agh.gem.util.createReport
import pl.edu.agh.gem.util.createReportActivity
import pl.edu.agh.gem.util.createReportActivityMember

class ReportsResponseTest : ShouldSpec({

    should("map ReportActivityMember to dto") {
        // given
        val reportActivityMember = createReportActivityMember()

        // when
        val reportActivityMemberDto = reportActivityMember.toReportActivityMemberDto()

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
        val reportActivityDto = reportActivity.toReportActivityDto()

        // then
        reportActivityDto.also {
            it.title shouldBe reportActivity.title
            it.date shouldBe reportActivity.date
            it.value shouldBe reportActivity.value
            it.members shouldBe reportActivity.members.map { member -> member.toReportActivityMemberDto() }
        }
    }

    should("map Report to dto") {
        // given
        val report = createReport()

        // when
        val reportDto = report.toReportDto()

        // then
        reportDto.also {
            it.currency shouldBe report.currency
            it.activities shouldBe report.activities.map { activity -> activity.toReportActivityDto() }
            it.balances shouldBe report.balances.map { balance -> balance.toUserBalanceDto() }
            it.settlements shouldBe report.settlements.map { settlement -> settlement.toSettlementDto() }
        }
    }

    should("map Report list  to ReportsResponse") {
        // given
        val reports = listOf(createReport())

        // when
        val reportsResponse = reports.toReportsResponse()

        // then
        reportsResponse.also {
            it.groupId shouldBe reports.first().groupId
            it.reports shouldBe reports.map { report -> report.toReportDto() }
        }
    }
},)
