package com.idiotfrogs.auth.login.component

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.idiotfrogs.resource.R

@Composable
fun GoogleLoginButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    LoginButton(
        modifier = modifier,
        imageRes = R.drawable.ic_google_logo,
        text = "Google로 로그인",
        onClick = onClick
    )
}

@Preview
@Composable
private fun GoogleLoginButtonPreview() {
    GoogleLoginButton(
        modifier = Modifier.padding(horizontal = 20.dp),
        onClick = {}
    )
}