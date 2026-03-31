package com.idiotfrogs.profile.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.navigation.LocalComposeMSNavigator
import com.idiotfrogs.navigation.Routes
import com.idiotfrogs.profile.component.ProfileCard
import com.idiotfrogs.profile.component.ProfileHeader
import com.idiotfrogs.profile.component.ProfileTicketCard

const val HeaderHeight = 56

@Composable
fun ProfileRoute(
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val navigator = LocalComposeMSNavigator.current

    LaunchedEffect(Unit) {
        profileViewModel.event.collect { event ->
            when (event) {
                ProfileEvent.NavigateToBack -> navigator.popBackStack()
                ProfileEvent.NavigateToEditProfile -> navigator.navigate(Routes.EditProfile)
                ProfileEvent.NavigateToSetting -> navigator.navigate(Routes.Setting)
            }
        }
    }

    ProfileScreen(
        onAction = profileViewModel::onAction
    )
}

@Composable
fun ProfileScreen(
    onAction: (ProfileAction) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        ProfileHeader(
            modifier = Modifier.zIndex(1f),
            onBack = { onAction(ProfileAction.NavigateToBack) },
            onSetting = { onAction(ProfileAction.NavigateToSetting) }
        )
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF6F6F6))
                .padding(horizontal = 20.dp),
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            maxLineItem {
                ProfileCard(
                    modifier = Modifier.padding(top = (HeaderHeight + 24).dp),
                    nickname = "용감한 사자처럼",
                    onEditClick = { onAction(ProfileAction.NavigateToEditProfile) }
                )
            }
            maxLineItem {
                MSText(
                    modifier = Modifier.padding(top = 16.dp),
                    text = "오픈된 티켓",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.dp,
                    color = MSTheme.color.greyG5
                )
            }
            // TODO: 실제 데이터로 변경
            items(List(10) { "2026. 10. 24" }) {
                ProfileTicketCard(
                    imageUrl = "",
                    title = "제목입니다.다ㅏㅏ",
                    date = it,
                    onClick = {}
                )
            }
        }
    }
}

private fun LazyGridScope.maxLineItem(
    content: @Composable LazyGridItemScope.() -> Unit
) {
    item(
        span = { GridItemSpan(maxLineSpan) },
        content = content
    )
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen(onAction = {})
}