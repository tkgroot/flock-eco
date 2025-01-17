package community.flock.eco.feature.user.repositories

import community.flock.eco.core.utils.toNullable
import community.flock.eco.feature.user.UserConfiguration
import community.flock.eco.feature.user.model.User
import community.flock.eco.feature.user.model.UserAccountPassword
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@ContextConfiguration(classes = [UserConfiguration::class])
@DataJpaTest
@AutoConfigureTestDatabase
class UserAccountPasswordRepositoryTest {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var userAccountPasswordRepository: UserAccountPasswordRepository

    @Test
    fun `save user via repository`() {

        val user = User(
                name = "User Name",
                email = "user@gmail.com"
        ).save()

        val account = UserAccountPassword(
                user= user,
                        secret = "123456"
        )
        userAccountPasswordRepository.save(account)

        val res1 = userAccountPasswordRepository.findByUserEmailIgnoreCaseContaining("USER@gmail.com").toNullable()
        assertEquals("User Name", res1?.user?.name)

        val res2 = userAccountPasswordRepository.findByUserEmailIgnoreCaseContaining("user@Gmail.com").toNullable()
        assertEquals("User Name", res2?.user?.name)

    }

    fun User.save() = userRepository.save(this)

}
