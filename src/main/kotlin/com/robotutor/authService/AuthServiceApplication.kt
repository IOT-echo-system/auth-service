package com.robotutor.authService

import com.robotutor.authService.services.PolicyInitializer
import com.robotutor.authService.services.RoleInitializer
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.ComponentScan
import org.springframework.stereotype.Service

@SpringBootApplication
@ComponentScan(basePackages = ["com.robotutor"])
@ConfigurationPropertiesScan(basePackages = ["com.robotutor"])
@EnableCaching
class AuthServiceApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplicationBuilder(AuthServiceApplication::class.java).run(*args)
        }
    }
}

@Service
class CommandLineRunner(
    private val policyInitializer: PolicyInitializer,
    private val roleInitializer: RoleInitializer
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        policyInitializer.initialize()
            .collectList()
            .flatMapMany {
                roleInitializer.initialize()
            }
            .subscribe()
    }

}
