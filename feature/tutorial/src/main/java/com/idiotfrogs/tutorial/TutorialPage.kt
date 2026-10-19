package com.idiotfrogs.tutorial

import androidx.annotation.DrawableRes

/**
 * 튜토리얼 페이지. 선언 순서(= Figma 의 `튜토리얼_N`)가 그대로 노출 순서가 된다.
 * 별도의 API 호출 없이 정적인 콘텐츠만 보여준다.
 */
enum class TutorialPage(
    @DrawableRes val illustration: Int,
    val title: String,
    val body: String,
    val buttonLabel: String,
) {
    CREATE(
        illustration = R.drawable.img_tutorial_1,
        title = "함께 열 타임 티켓을 만들어요",
        body = "+ 버튼을 눌러 새 티켓을 만들거나\n친구에게 받은 참여 코드로 합류할 수 있어요.",
        buttonLabel = "다음",
    ),
    INVITE(
        illustration = R.drawable.img_tutorial_2,
        title = "친구를 티켓에 초대해요",
        body = "참여 코드나 초대 링크를 보내면\n친구가 바로 티켓에 들어올 수 있어요.",
        buttonLabel = "다음",
    ),
    MESSAGE(
        illustration = R.drawable.img_tutorial_3,
        title = "추억 메시지를 담아요",
        body = "글, 사진, 음성으로 지금의 마음을 남겨요.\n티켓이 열리는 날 모두 함께 볼 수 있어요.",
        buttonLabel = "다음",
    ),
    BURY(
        illustration = R.drawable.img_tutorial_4,
        title = "오픈일을 정하고 묻어요",
        body = "티켓을 열 날짜를 정하고 묻으면\n그날까지 추억이 천천히 익어가요.",
        buttonLabel = "다음",
    ),
    OPEN(
        illustration = R.drawable.img_tutorial_5,
        title = "오픈일에 함께 열어봐요",
        body = "오픈일에 티켓을 열고\n모두의 추억 메시지를 확인해요.",
        buttonLabel = "메실 시작하기",
    ),
}
