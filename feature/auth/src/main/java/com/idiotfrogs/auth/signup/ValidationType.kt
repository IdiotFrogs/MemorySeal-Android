package com.idiotfrogs.auth.signup

sealed class ValidationType(val errorMessage: String) {
    data object Empty : ValidationType("")
    data object FirstOrLastWhiteSpace : ValidationType("첫 글자와 끝 글자 공백 금지")
    data object AllWhiteSpace: ValidationType("공백 닉네임 금지")
    data object RepeatedWhiteSpace: ValidationType("중복 공백 금지")
    data object LengthOutOfRange: ValidationType("1~16글자 범위 밖")
    data object InvalidChar: ValidationType("유효하지 않은 문자")
    data object Duplicated: ValidationType("닉네임 중복")
}