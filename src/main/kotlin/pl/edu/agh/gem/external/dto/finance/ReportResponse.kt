package pl.edu.agh.gem.external.dto.finance

import pl.edu.agh.gem.external.dto.group.CurrencyDto
import pl.edu.agh.gem.external.dto.group.toDto
import pl.edu.agh.gem.internal.model.finance.report.Report
import pl.edu.agh.gem.internal.model.finance.report.ReportActivity
import pl.edu.agh.gem.internal.model.finance.report.ReportActivityMember
import java.math.BigDecimal
import java.time.Instant

data class ReportResponse(
    val groupId: String,
    val activities: List<ReportActivityDto>,
)

fun Report.toReportResponse(groupId: String) = ReportResponse(
    groupId = groupId,
    activities = activities.map { it.toDto() },
)

data class ReportActivityDto(
    val title: String,
    val date: Instant,
    val value: BigDecimal,
    val currency: CurrencyDto,
    val members: List<ReportActivityMemberDto>,
)

fun ReportActivity.toDto() = ReportActivityDto(
    title = title,
    date = date,
    value = value,
    currency = currency.toDto(),
    members = members.map { it.toDto() },
)

data class ReportActivityMemberDto(
    val userId: String,
    val value: BigDecimal,
)

fun ReportActivityMember.toDto() = ReportActivityMemberDto(
    userId = userId,
    value = value,
)
