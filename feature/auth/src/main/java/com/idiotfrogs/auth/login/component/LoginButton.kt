package com.idiotfrogs.auth.login.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.wavyStroke
import com.idiotfrogs.resource.R

@Immutable
data class LoginUiModel(
    val containerColor: Color,
    val iconRes: Int,
    val textColor: Color,
    val text: String,
    val borderColor: Color,
)

@Composable
internal fun LoginButton(
    modifier: Modifier = Modifier,
    loginType: LoginType,
    onClick: () -> Unit,
) {
    val uiModel = loginType.toUiModel()

    Button(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .wavyStroke(
                amplitude = 1.dp,
                spacing = 3.dp,
                color = MSTheme.color.black,
                fillColor = uiModel.containerColor,
            ),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        onClick = onClick,
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                modifier = Modifier.align(Alignment.CenterStart),
                painter = painterResource(uiModel.iconRes),
                contentDescription = "button logo"
            )
            MSText(
                modifier = Modifier.align(Alignment.Center),
                text = uiModel.text,
                color = uiModel.textColor,
                fontSize = 16.dp,
            )
        }
    }
}

enum class LoginType {
    GOOGLE, APPLE;

    @Composable
    internal fun toUiModel(): LoginUiModel {
        return when (this) {
            GOOGLE -> LoginUiModel(
                containerColor = MSTheme.color.white,
                iconRes = R.drawable.ic_google_logo,
                textColor = MSTheme.color.black,
                text = "Google로 로그인",
                borderColor = MSTheme.color.black
            )
            APPLE -> LoginUiModel(
                containerColor = MSTheme.color.black,
                iconRes = R.drawable.ic_apple_logo,
                textColor = MSTheme.color.white,
                text = "Apple로 로그인",
                borderColor = Color.Transparent
            )
        }
    }
}