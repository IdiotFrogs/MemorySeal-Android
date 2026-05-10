package com.idiotfrogs.home.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.DrawType
import com.idiotfrogs.designsystem.util.wavyStroke

enum class HomeTab(val title: String) {
    CREATED("생성한 티켓"), JOINED("합류한 티켓")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTabBar(
    selectedTab: HomeTab,
    onClick: (HomeTab) -> Unit,
) {
    val homeTabs = remember { HomeTab.entries }

    CompositionLocalProvider(LocalRippleConfiguration provides null) {
        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = MSTheme.color.white,
            indicator = { tabPositions ->
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedTab.ordinal])
                        .padding(horizontal = 54.dp)
                        .wavyStroke(
                            color = MSTheme.color.greyG5,
                            drawType = DrawType.BOTTOM,
                            spacing = 3.dp
                    )
                )
            },
            divider = {}
        ) {
            homeTabs.forEach { homeTab ->
                HomeTabItem(
                    selected = homeTab == selectedTab,
                    text = homeTab.title,
                    onClick = { onClick.invoke(homeTab) }
                )
            }
        }
    }
}

@Composable
fun HomeTabItem(
    selected: Boolean,
    text: String,
    onClick: () -> Unit
) {
    Tab(
        selected = selected,
        selectedContentColor = MSTheme.color.black,
        unselectedContentColor = MSTheme.color.greyG3,
        onClick = onClick
    ) {
        MSText(
            modifier = Modifier.padding(vertical = 16.dp),
            text = text,
            fontSize = 16.dp,
        )
    }
}

@Preview
@Composable
private fun HomeTabBarPreview() {
    var currentTab by remember { mutableStateOf(HomeTab.CREATED) }

    HomeTabBar(
        selectedTab = currentTab,
        onClick = { currentTab = it }
    )
}