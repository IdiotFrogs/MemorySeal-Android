package com.idiotfrogs.auth.login.component

import androidx.annotation.DrawableRes
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
import com.idiotfrogs.resource.pretendard

@Composable
fun LoginButton(
    modifier: Modifier = Modifier,
    @DrawableRes imageRes: Int,
    text: String,
    textColor: Color,
    backgroundColor: Color,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                modifier = Modifier.align(Alignment.CenterStart),
                painter = painterResource(imageRes),
                contentDescription = "button logo"
            )
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = text,
                color = textColor,
                fontFamily = pretendard,
                fontSize = 16.dp.toSp(),
                fontWeight = FontWeight.Bold
            )
        }
    }
}