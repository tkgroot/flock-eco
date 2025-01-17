package community.flock.eco.feature.member

import community.flock.eco.feature.member.data.MemberLoadData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import
import javax.annotation.PostConstruct

@SpringBootApplication
@Import(MemberConfiguration::class, MemberLoadData::class)
class MemberApplication(memberLoadData: MemberLoadData) {

    init {
        memberLoadData.load(999)
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(MemberApplication::class.java, *args)
}
