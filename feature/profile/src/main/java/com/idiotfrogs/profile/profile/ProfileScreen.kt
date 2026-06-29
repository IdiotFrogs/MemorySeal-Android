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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.idiotfrogs.designsystem.component.MSLoadingOverlay
import com.idiotfrogs.designsystem.component.MSDashHorizontalDivider
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.extension.toYearMonthDay
import com.idiotfrogs.model.timecapsule.MyTimeCapsuleResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleRole
import com.idiotfrogs.model.timecapsule.TimeCapsuleStatus
import com.idiotfrogs.model.user.ProfileResponse
import com.idiotfrogs.navigation.LocalComposeMSNavigator
import com.idiotfrogs.navigation.Routes
import com.idiotfrogs.profile.component.ProfileCard
import com.idiotfrogs.profile.component.ProfileHeader
import com.idiotfrogs.profile.component.ProfileTicketCard
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.todayIn
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import kotlin.time.Clock

const val HeaderHeight = 56

@Composable
fun ProfileRoute(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val navigator = LocalComposeMSNavigator.current
    val uiState by viewModel.collectAsState()

    viewModel.collectSideEffect { event ->
        when (event) {
            ProfileSideEffect.NavigateToBack -> navigator.popBackStack()
            ProfileSideEffect.NavigateToEditProfile -> navigator.navigate(Routes.EditProfile)
            ProfileSideEffect.NavigateToSetting -> navigator.navigate(Routes.Setting)
            is ProfileSideEffect.NavigateToDetail -> navigator.navigate(Routes.Detail(event.id))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        uiState.data?.let { data ->
            ProfileScreen(
                data = data,
                onAction = viewModel::onAction
            )
        }

        MSLoadingOverlay(visible = uiState.data != null && uiState.isLoading)
    }
}

@Composable
fun ProfileScreen(
    data: ProfileData,
    onAction: (ProfileAction) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        ProfileHeader(
            modifier = Modifier.zIndex(1f),
            onBack = { onAction(ProfileAction.BackClicked) },
            onSetting = { onAction(ProfileAction.SettingClicked) }
        )
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize()
                .background(MSTheme.color.white)
                .padding(horizontal = 20.dp),
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            maxLineItem {
                ProfileCard(
                    modifier = Modifier.padding(top = (HeaderHeight + 24).dp),
                    nickname = data.user?.nickname ?: "",
                    imageUrl = data.user?.profileImageUrl?.ifEmpty { null },
                    onEditClick = { onAction(ProfileAction.EditProfileClicked) }
                )
            }
            maxLineItem {
                MSText(
                    modifier = Modifier.padding(top = 16.dp),
                    text = "오픈된 티켓",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.dp,
                    color = MSTheme.color.greyG5,
                    textAlign = TextAlign.Center
                )
            }
            maxLineItem {
                MSDashHorizontalDivider(
                    thickness = 2.dp,
                    dashWidth = 10.dp,
                    gapWidth = 10.dp
                )
            }
            items(data.capsules) {
                ProfileTicketCard(
                    imageUrl = it.mainImageUrl,
                    title = it.title,
                    date = it.createdAt.toYearMonthDay(),
                    onClick = { onAction.invoke(ProfileAction.TicketClicked(it.timeCapsuleId))}
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
    ProfileScreen(
        data = ProfileData(
            user = ProfileResponse(
                id = 0L,
                nickname = "용감한 사자처럼",
                profileImageUrl = "",
                email = "",
                isOnboarding = true
            ),
            capsules = listOf(
                MyTimeCapsuleResponse(
                    timeCapsuleId = 0L,
                    title = "제목입니다. 제목입니다.",
                    createdAt = Clock.System
                        .todayIn(TimeZone.currentSystemDefault())
                        .atTime(0, 0, 0, 0),
                    mainImageUrl = "",
                    role = TimeCapsuleRole.CONTRIBUTOR,
                    timeCapsuleStatus = TimeCapsuleStatus.BURIED

                )
            )
        ),
        onAction = {},
    )
}
