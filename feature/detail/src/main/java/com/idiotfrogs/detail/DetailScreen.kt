package com.idiotfrogs.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.component.MSAnnotatedText
import com.idiotfrogs.designsystem.component.MSDialog
import com.idiotfrogs.designsystem.component.MSMessageItem
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.component.MessageType
import com.idiotfrogs.designsystem.component.button.MSButton
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.noRippleClickable
import com.idiotfrogs.detail.component.MembarListItem
import com.idiotfrogs.detail.component.RoundedProgressBar
import com.idiotfrogs.navigation.LocalComposeMSNavigator
import com.idiotfrogs.navigation.Routes
import com.idiotfrogs.resource.R

@Composable
fun DetailScreen(
    title: String,
    date: String, // TODO 추 후 날짜 로직 설계 후 변경 필요
    isMember: Boolean,
    isVoteStart: Boolean,
    iSSeal: Boolean,
    onSealClicked: () -> Unit,
    onVoteClicked: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val navigator = LocalComposeMSNavigator.current

    val scrollState = rememberScrollState()
    var showVoteDialog by remember { mutableStateOf(false) }
    val messageList = listOf("")

    if (showVoteDialog) {
        MSDialog(
            title = "봉인 투표에 반대 하시겠습니까?",
            content = "투표를 반대하면 투표를 다시 시작해야 합니다.",
            onConfirm = {
                onVoteClicked(false)
                showVoteDialog = false
            },
            onCancel = { showVoteDialog = false },
            confirmText = "반대",
            confirmButtonColor = MSTheme.color.black
        )
    }

    Column (
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .background(MSTheme.color.bgNormal)
            .padding(0.dp)
            .verticalScroll(scrollState)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp)
        ) {
            Image( // TODO 추 후 AsyncImage로 수정 필요
                modifier = Modifier.matchParentSize(),
                painter = painterResource(R.drawable.img_sample),
                contentDescription = "Thumbnail",
                contentScale = ContentScale.Crop
            )
            Image(
                modifier = Modifier
                    .systemBarsPadding()
                    .align(Alignment.TopStart)
                    .padding(top = 20.dp, start = 20.dp)
                    .noRippleClickable { navigator.popBackStack() },
                painter = painterResource(R.drawable.img_close),
                contentDescription = "Close"
            )
            Image(
                modifier = Modifier
                    .systemBarsPadding()
                    .align(Alignment.TopEnd)
                    .padding(top = 20.dp, end = 20.dp),
                painter = painterResource(R.drawable.img_management),
                contentDescription = "Management"
            )
            MSText(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 20.dp, bottom = 38.dp),
                text = title,
                fontSize = 40.dp,
                color = MSTheme.color.white
            )
            MSText(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 20.dp, bottom = 24.dp),
                text = date,
                fontSize = 12.dp,
                color = MSTheme.color.white
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MSTheme.color.white)
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            MSText(
                text = "첫 여행, 첫 추억",
                fontSize = 20.dp,
            )
            Spacer(Modifier.height(8.dp))
            MSText(
                text = "처음 함께 떠났던 여행의 순간들을 담은 우리의 작은 시간 저장소.",
                fontSize = 16.dp,
                fontWeight = FontWeight.Normal
            )
            if (!iSSeal) {
                Spacer(Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MSText(text = "티켓 봉인 투표")
                    MSAnnotatedText(
                        text = buildAnnotatedString {
                            append("1  ")
                            withStyle(
                                SpanStyle(color = MSTheme.color.greyG4)
                            ) { append("/  7") }
                        },
                        color = MSTheme.color.primaryNormal
                    )
                }
                Spacer(Modifier.height(8.dp))
                if (isMember) {
                    if (isVoteStart) { // TODO isVoteStart 변수 관리 방법 고민 필요
                        Spacer(Modifier.height(8.dp))
                        RoundedProgressBar(0.2f)
                        Spacer(Modifier.height(24.dp))
                        Row {
                            MSButton(
                                modifier = Modifier.weight(1f),
                                onClick = { showVoteDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MSTheme.color.greyG1
                                ),
                                contentPadding = PaddingValues(11.dp)
                            ) {
                                MSText(
                                    text = "반대",
                                    fontSize = 16.dp,
                                    color = MSTheme.color.greyG4
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            MSButton(
                                modifier = Modifier.weight(3f),
                                onClick = { onVoteClicked(true) },
                                contentPadding = PaddingValues(11.dp)
                            ) {
                                MSText(
                                    text = "찬성",
                                    fontSize = 16.dp,
                                    color = MSTheme.color.white
                                )
                            }
                        }
                    } else {
                        MSText(
                            text = "티켓 봉인을 봉인하려면 방장이 투표를 시작하고, 모든 맴버의 동의가 필요합니다.",
                            fontSize = 16.dp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                } else {
                    if (isVoteStart) {
                        Spacer(Modifier.height(8.dp))
                        RoundedProgressBar(0.2f)
                        Spacer(Modifier.height(24.dp))
                        Row {
                            MSButton(
                                modifier = Modifier.weight(1f),
                                onClick = { showVoteDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MSTheme.color.greyG1
                                ),
                                contentPadding = PaddingValues(11.dp)
                            ) {
                                MSText(
                                    text = "투표 취소",
                                    fontSize = 16.dp,
                                    color = MSTheme.color.greyG4
                                )
                            }
                        }
                    } else {
                        MSButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onSealClicked,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MSTheme.color.greyG5
                            ),
                            contentPadding = PaddingValues(11.dp)
                        ) {
                            MSText(
                                text = "티켓 봉인하기",
                                fontSize = 16.dp,
                                color = MSTheme.color.white
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MSTheme.color.white)
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MSText(
                    text = "추억 메시지",
                    color = MSTheme.color.greyG4
                )
                Image(
                    modifier = Modifier.size(16.dp),
                    painter = painterResource(R.drawable.ic_detail_rigt),
                    contentDescription = "추억 메시지 상세 아이콘"
                )
            }
            Spacer(Modifier.height(24.dp))
            if (messageList.isEmpty()) {
                MSButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {},
                    contentPadding = PaddingValues(11.dp)
                ) {
                    MSText(
                        text = "시작하기",
                        fontSize = 16.dp,
                        color = MSTheme.color.white
                    )
                }
            } else {
                MSMessageItem(
                    type = MessageType.TEXT,
                    text = "테스트 문구 입니다.",
                    isSeal = iSSeal
                )
                Spacer(Modifier.height(8.dp))
                MSMessageItem(
                    type = MessageType.PHOTO,
                    imageList = messageList,
                    isSeal = iSSeal
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MSTheme.color.white)
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MSAnnotatedText(
                    text = buildAnnotatedString {
                        append("멤버 ")
                        withStyle(
                            SpanStyle(color = MSTheme.color.primaryNormal)
                        ) { append("7") }
                    },
                    color = MSTheme.color.greyG4
                )
                Image(
                    modifier = Modifier
                        .size(16.dp)
                        .noRippleClickable { navigator.navigate(Routes.Friend(0)) }, // TODO 추 후 실제 타임캡슐 id 필요
                    painter = painterResource(R.drawable.ic_detail_rigt),
                    contentDescription = "추억 메시지 상세 아이콘"
                )
            }
            Spacer(Modifier.height(24.dp))
            MembarListItem("파란 바나나 (나)", isMembar = false) // TODO 실제 본인 nickName 필요
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(
                thickness = 1.dp,
                color = MSTheme.color.greyG1
            )
            repeat(6) { // TODO 테스트용 코드 -> 추 후 실제 list 변경 필요
                Spacer(Modifier.height(16.dp))
                MembarListItem(
                    nickName = when (it) {
                        0 -> "파란 바나나"
                        1 -> "검정 복숭아"
                        2 -> "별 모양 파인애플"
                        3 -> "초코 체리"
                        4 -> "자두 수박"
                        else -> "민트 네모 수박"
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun DetailScreenPreview() {
    DetailScreen(
        "D-60",
        "2025. 02. 20. (목) ~ 2025. 04. 20. (일)",
        true,
        true,
        false,
        {},
        {}
    )
}