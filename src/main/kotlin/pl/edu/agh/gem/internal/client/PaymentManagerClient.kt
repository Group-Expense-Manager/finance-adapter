package pl.edu.agh.gem.internal.client

import pl.edu.agh.gem.internal.model.finance.Activity
import pl.edu.agh.gem.internal.model.finance.filter.ClientFilterOptions
import pl.edu.agh.gem.internal.model.payment.AcceptedPayment

interface PaymentManagerClient {
    fun getActivities(groupId: String, clientFilterOptions: ClientFilterOptions): List<Activity>
    fun getAcceptedPayments(groupId: String, currency: String): List<AcceptedPayment>
}

class PaymentManagerClientException(override val message: String?) : RuntimeException()

class RetryablePaymentManagerClientException(override val message: String?) : RuntimeException()
