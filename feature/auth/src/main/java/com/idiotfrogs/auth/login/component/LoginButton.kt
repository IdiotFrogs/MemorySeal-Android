package com.idiotfrogs.auth.login.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.util.toSp
import com.idiotfrogs.resource.R
import com.idiotfrogs.resource.pretendard

data class LoginUiModel(
    val containerColor: Color,
    val iconRes: Int,
    val textColor: Color,
    val text: String
)

@Composable
fun LoginButton(
    modifier: Modifier = Modifier,
    loginType: LoginType,
    onClick: () -> Unit,
) {
    val uiModel = loginType.toUiModel()

    Button(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = uiModel.containerColor
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                modifier = Modifier.align(Alignment.CenterStart),
                painter = painterResource(uiModel.iconRes),
                contentDescription = "button logo"
            )
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = uiModel.text,
                color = uiModel.textColor,
                fontFamily = pretendard,
                fontSize = 16.dp.toSp(),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

enum class LoginType {
    GOOGLE, APPLE;

    internal fun toUiModel(): LoginUiModel {
        return when (this) {
            GOOGLE -> LoginUiModel(
                containerColor = Color.White,
                iconRes = R.drawable.ic_google_logo,
                textColor = Color.Black,
                text = "Google로 로그인"
            )
            APPLE -> LoginUiModel(
                containerColor = Color.Black,
                iconRes = R.drawable.ic_apple_logo,
                textColor = Color.White,
                text = "Apple로 로그인"
            )
        }
    }
}