package community.flock.eco.feature.payment.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import community.flock.eco.feature.payment.model.PaymentFrequency
import community.flock.eco.feature.payment.model.PaymentMandate
import community.flock.eco.feature.payment.model.PaymentTransaction
import community.flock.eco.feature.payment.model.PaymentType
import community.flock.eco.feature.payment.repositories.PaymentMandateRepository
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module
import com.fasterxml.jackson.module.kotlin.KotlinModule


@RunWith(SpringRunner::class)
@DataJpaTest(showSql=false)
@AutoConfigureTestDatabase
class MapperTest {

    @Autowired
    lateinit var paymentMandateRepository: PaymentMandateRepository

    @Test
    fun tests1() {
        val model = PaymentMandate(
                id = 1,
                amount = 10.0,
                frequency = PaymentFrequency.MONTHLY,
                type = PaymentType.SEPA
        )

        val mapper = ObjectMapper()
        val res = mapper.writeValueAsString(model)
        println(res)
    }

    @Test
    fun tests2() {
        val mandate = PaymentMandate(
                id = 1,
                amount = 10.0,
                frequency = PaymentFrequency.MONTHLY,
                type = PaymentType.SEPA

        )

        val transactions = PaymentTransaction(
                amount = 10.0,
                mandate = mandate
        )

        val model = mandate.copy(
                transactions = setOf(transactions)
        )

        val mapper = ObjectMapper()
        val res = mapper.writeValueAsString(model)
        println(res)
    }

    @Test
    fun test3(){
        val data =paymentMandateRepository.findAll()

        val mapper = ObjectMapper()
        mapper.registerModule(KotlinModule())
        mapper.registerModule(Hibernate5Module())

        val res = mapper.writeValueAsString(data)
        println(res)
    }


}