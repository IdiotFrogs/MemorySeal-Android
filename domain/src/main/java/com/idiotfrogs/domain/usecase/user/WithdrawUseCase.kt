package com.idiotfrogs.domain.usecase.user

import com.idiotfrogs.data.repository.user.UserRepository
import com.idiotfrogs.util.safeCatching
import javax.inject.Inject

class WithdrawUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() = safeCatching {
        userRepository.withdraw()
    }
}