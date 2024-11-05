package com.robotutor.authService.repositories

import com.robotutor.authService.models.UserDetails
import com.robotutor.authService.models.UserId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UserRepository : ReactiveCrudRepository<UserDetails, UserId> {
    fun findByUserId(userId: UserId): Mono<UserDetails>
}
