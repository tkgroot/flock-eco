package community.flock.eco.feature.user

import community.flock.eco.feature.user.controllers.*
import community.flock.eco.feature.user.services.*
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
@EnableJpaRepositories
@EntityScan
@EnableConfigurationProperties(UserProperties::class)
@Import(UserControllerAdvice::class,
        UserController::class,
        UserGroupController::class,
        UserGroupService::class,
        UserAuthorityController::class,
        UserAccountController::class,
        UserStatusController::class,
        UserService::class,
        UserAccountService::class,
        UserAuthorityService::class,
        UserSecurityService::class)
class UserConfiguration {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

}
