package com.idiotfrogs.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.component.MSDim
import com.idiotfrogs.designsystem.component.MSMenuFab
import com.idiotfrogs.designsystem.model.MSMenuFabModel
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.DevicePreview
import com.idiotfrogs.home.component.HomeHeader
import com.idiotfrogs.home.component.HomeJoinContainer
import com.idiotfrogs.home.component.HomeTab
import com.idiotfrogs.home.component.HomeTabBar
import com.idiotfrogs.home.component.HomeTicket

@Composable
fun HomeScreen() {
    var expanded by remember { mutableStateOf(false) }
    var currentTab by remember { mutableStateOf(HomeTab.CREATED) }
    var showJoinContainer by remember { mutableStateOf(false) }

    val showDim by remember {
        derivedStateOf { expanded || showJoinContainer }
    }

    val menuList by remember {
        mutableStateOf(
            listOf(
                MSMenuFabModel("새 티켓 생성하기") { /** TODO: 타임 티켓 생성 */ },
                MSMenuFabModel("참여코드로 합류하기") {
                    expanded = false
                    showJoinContainer = true
                },
            )
        )
    }

    val textFieldState = rememberTextFieldState()

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MSTheme.color.bgNormal)
                .systemBarsPadding()
        ) {
            HomeHeader()
            HomeTabBar(
                selectedTab = currentTab,
                onClick = { currentTab = it },
            )
            LazyColumn(
                modifier = Modifier.padding(horizontal = 20.dp),
                contentPadding = PaddingValues(top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),

                ) {
                items(5) {
                    HomeTicket(
                        countdown = "D-5",
                        targetDate = "2027. 10. 24.",
                        title = "제목입니다."
                    )
                }
            }
        }
        MSDim(
            visible = showDim,
            onDismiss = {
                expanded = false
                showJoinContainer = false
            }
        )
        MSMenuFab(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 24.dp),
            expanded = expanded,
            menuList = menuList,
            onClick = { expanded = !expanded },
            onDismiss = { expanded = false },
        )
        HomeJoinContainer(
            isShow = showJoinContainer,
            textFieldState = textFieldState,
            onJoin = { /** TODO: 타임 티켓 참여 */ },
            onCancel = { showJoinContainer = false }
        )
    }
}

@DevicePreview
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}