package com.idiotfrogs.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.component.MSTextField
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.noRippleClickable
import com.idiotfrogs.designsystem.util.rememberPickerState
import com.idiotfrogs.profile.component.ProfileHeader
import com.idiotfrogs.profile.component.ProfileOption
import com.idiotfrogs.resource.R
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun ProfileScreen(
    navigateToBack: () -> Unit,
) {
    // TODO: 추후 기존 프로필과 비교 로직 작성
    var isChanged by remember { mutableStateOf(false) }

    val (imageUri, launchImagePicker) = rememberPickerState()
    val textFieldState = rememberTextFieldState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MSTheme.color.white)
            .systemBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        ProfileHeader(
            isChanged = isChanged,
            onBack = { navigateToBack() },
            onSave = {
                /** TODO: 저장 로직 */
                navigateToBack()
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        imageUri?.let {
            GlideImage(
                imageModel = { imageUri },
                modifier = Modifier
                    .noRippleClickable { launchImagePicker() }
                    .size(128.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterHorizontally),

                )
        } ?: Image(
            modifier = Modifier
                .noRippleClickable { launchImagePicker() }
                .size(128.dp)
                .align(Alignment.CenterHorizontally),
            painter = painterResource(R.drawable.img_empty_profile),
            contentDescription = "Profile"
        )
        Spacer(modifier = Modifier.height(16.dp))
        MSText(
            text = "닉네임",
            fontWeight = FontWeight.Normal,
            fontSize = 12.dp,
            color = MSTheme.color.greyG5
        )
        Spacer(modifier = Modifier.height(8.dp))
        MSTextField(
            modifier = Modifier.fillMaxWidth(),
            textFieldState = textFieldState,
            hint = ""
        )
        Spacer(modifier = Modifier.height(16.dp))
        ProfileOption(
            option = "앱 버전",
            trailingContent = {
                MSText(
                    text = "v0.2",
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.dp,
                    color = MSTheme.color.greyG4
                )
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        ProfileOption(
            option = "이용 약관",
            trailingContent = {
                Image(
                    painter = painterResource(R.drawable.ic_chevron_right),
                    contentDescription = "terms"
                )
            }
        )
        Spacer(modifier = Modifier.weight(1f))
        ProfileOption(
            option = "로그아웃",
            trailingContent = {
                Image(
                    painter = painterResource(R.drawable.ic_chevron_right),
                    contentDescription = "terms"
                )
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        ProfileOption(
            option = "회원탈퇴",
            optionColor = MSTheme.color.red,
            trailingContent = {
                Image(
                    painter = painterResource(R.drawable.ic_chevron_right),
                    contentDescription = "logout"
                )
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(
        navigateToBack = { }
    )
}