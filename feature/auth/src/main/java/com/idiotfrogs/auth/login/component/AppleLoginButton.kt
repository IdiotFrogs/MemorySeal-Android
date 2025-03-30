package com.idiotfrogs.auth.login.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.util.toSp
import com.idiotfrogs.resource.R
import com.idiotfrogs.resource.pretendard

@Composable
fun AppleLoginButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    ButtonDefaults.shape
    Button(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black
        ),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                modifier = Modifier.align(Alignment.CenterStart),
                painter = painterResource(R.drawable.ic_apple_logo),
                contentDescription = "apple logo"
            )
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "Apple로 로그인",
                color = Color.White,
                fontSize = 16.dp.toSp(),
                fontFamily = pretendard,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview
@Composable
private fun AppleLoginButtonPreview() {
    AppleLoginButton(
        modifier = Modifier.padding(horizontal = 20.dp),
        onClick = {}
    )
}