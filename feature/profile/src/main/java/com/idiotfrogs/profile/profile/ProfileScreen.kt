package com.idiotfrogs.profile.profile

import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.idiotfrogs.designsystem.component.MSLoadingOverlay
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.component.MSTitleDialog
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.DrawType
import com.idiotfrogs.designsystem.util.noRippleClickable
import com.idiotfrogs.designsystem.util.wavyStroke
import com.idiotfrogs.model.timecapsule.MyTimeCapsuleResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleRole
import com.idiotfrogs.model.timecapsule.TimeCapsuleStatus
import com.idiotfrogs.model.user.ProfileResponse
import com.idiotfrogs.navigation.LocalComposeMSNavigator
import com.idiotfrogs.navigation.Routes
import com.idiotfrogs.profile.component.ProfileCard
import com.idiotfrogs.profile.component.ProfileHeader
import com.idiotfrogs.resource.R
import kotlinx.datetime.TimeZone
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
            ProfileSideEffect.NavigateToLogin -> {
                navigator.clear()
                navigator.navigate(Routes.Login)
            }
            ProfileSideEffect.NavigateToBack -> navigator.popBackStack()
            ProfileSideEffect.NavigateToEditProfile -> navigator.navigate(Routes.EditProfile)
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
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        MSTitleDialog(
            title = "로그아웃",
            confirmText = "로그아웃",
            cancelText = "유지",
            onConfirm = {
                showLogoutDialog = false
                onAction.invoke(ProfileAction.LogoutConfirmed)
            },
            onCancel = { showLogoutDialog = false },
            content = {
                Spacer(modifier = Modifier.height(8.dp))
                MSText(
                    text = "메실에서 로그아웃 하시겠습니까?",
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.dp,
                    color = MSTheme.color.greyG5
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        )
    }

    if (showWithdrawDialog) {
        MSTitleDialog(
            title = "회원탈퇴",
            confirmText = "탈퇴",
            cancelText = "취소",
            onConfirm = {
                showWithdrawDialog = false
                onAction.invoke(ProfileAction.WithdrawConfirmed)
            },
            onCancel = { showWithdrawDialog = false },
            content = {
                Spacer(modifier = Modifier.height(8.dp))
                MSText(
                    text = "메실 회원을 탈퇴하시겠습니까?\n티켓에 저장된 내용은 삭제되지 않습니다.",
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.dp,
                    color = MSTheme.color.greyG5
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        ProfileHeader(
            modifier = Modifier.zIndex(1f),
            onBack = { onAction(ProfileAction.BackClicked) },
        )
        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileCard(
                modifier = Modifier.padding(top = (HeaderHeight + 24).dp),
                nickname = data.user?.nickname ?: "",
                imageUrl = data.user?.profileImageUrl?.ifEmpty { null },
                onEditClick = { onAction(ProfileAction.EditProfileClicked) }
            )
            Spacer(modifier = Modifier.height(44.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .wavyStroke(
                        drawType = DrawType.TOP_SIDES,
                        color = MSTheme.color.bgNormal,
                        fillColor = MSTheme.color.bgNormal
                    )
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val versionName = rememberAppVersion()
                    MSText(
                        text = "앱 버전",
                        fontSize = 16.dp,
                        fontWeight = FontWeight.Medium,
                        color = MSTheme.color.greyG5
                    )
                    MSText(
                        text = "v$versionName",
                        fontSize = 16.dp,
                        fontWeight = FontWeight.Normal,
                        color = MSTheme.color.greyG4
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MSText(
                        text = "이용 약관",
                        fontSize = 16.dp,
                        fontWeight = FontWeight.Medium,
                        color = MSTheme.color.greyG5
                    )
                    Image(
                        modifier = Modifier.size(16.dp),
                        painter = painterResource(R.drawable.ic_chevron_right),
                        contentDescription = "arrow_right"
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .noRippleClickable { showLogoutDialog = true },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MSText(
                        text = "로그아웃",
                        fontSize = 16.dp,
                        fontWeight = FontWeight.Medium,
                        color = MSTheme.color.greyG5
                    )
                    Image(
                        modifier = Modifier.size(16.dp),
                        painter = painterResource(R.drawable.ic_chevron_right),
                        contentDescription = "arrow_right"
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .noRippleClickable { showWithdrawDialog = true },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MSText(
                        text = "회원탈퇴",
                        fontSize = 16.dp,
                        fontWeight = FontWeight.Medium,
                        color = MSTheme.color.greyG5
                    )
                    Image(
                        modifier = Modifier.size(16.dp),
                        painter = painterResource(R.drawable.ic_chevron_right),
                        contentDescription = "arrow_right"
                    )
                }
            }
        }
    }
}

@Composable
fun rememberAppVersion(): String {
    val context = LocalContext.current
    return remember {
        runCatching {
            val packageName = context.packageName

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    packageName, PackageManager.PackageInfoFlags.of(0)
                )
                    .versionName ?: ""
            } else {
                context.packageManager.getPackageInfo(packageName, 0)
                    .versionName ?: ""
            }
        }.getOrDefault("")
    }
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
                    createdAt = Clock.System.todayIn(TimeZone.currentSystemDefault()),
                    mainImageUrl = "",
                    role = TimeCapsuleRole.CONTRIBUTOR,
                    timeCapsuleStatus = TimeCapsuleStatus.BURIED

                )
            )
        ),
        onAction = {},
    )
}
