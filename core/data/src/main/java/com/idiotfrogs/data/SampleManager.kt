package com.idiotfrogs.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SampleManager @Inject constructor(
    // 필요한 파라미터? (ex. 내부저장소 로직)
) {
    fun googleLogin() {
        // 구글 로그인 구현 필요
    }

    fun appleLogin() {
        // 애플 로그인 구현 필요
    }
}