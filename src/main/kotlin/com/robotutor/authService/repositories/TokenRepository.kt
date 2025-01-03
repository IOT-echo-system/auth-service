package com.robotutor.authService.repositories

import com.robotutor.authService.models.Token
import com.robotutor.authService.models.TokenId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Repository
interface TokenRepository : ReactiveCrudRepository<Token, TokenId> {
    fun findByValueAndExpiredAtAfter(token: String, expiredAtAfter: LocalDateTime = LocalDateTime.now()): Mono<Token>
    fun findByValue(token: String): Mono<Token>
}
