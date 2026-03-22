package com.idiotfrogs.profile.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.idiotfrogs.profile.component.ProfileCard
import com.idiotfrogs.profile.component.ProfileHeader

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier.background(Color(0xFFF6F6F6))
    ) {
        ProfileHeader()
        Spacer(modifier = Modifier.height(24.dp))
        ProfileCard(
            modifier = Modifier.padding(horizontal = 20.dp),
            nickname = "용감한 사자처럼",
            onEditClick = { /** todo: EditProfile 랜딩 */ }
        )
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen()
}