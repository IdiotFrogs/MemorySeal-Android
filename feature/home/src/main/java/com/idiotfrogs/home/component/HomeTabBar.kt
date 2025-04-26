package com.idiotfrogs.home.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.toSp
import com.idiotfrogs.resource.pretendard

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
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedTab.ordinal])
                        .padding(
                            start = if (selectedTab.ordinal == 0) 20.dp else 0.dp,
                            end = if (selectedTab.ordinal == tabPositions.lastIndex) 20.dp else 0.dp
                        ),
                    height = 2.dp,
                    color = MSTheme.color.black
                )
            },
            divider = {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MSTheme.color.greyG1
                )
            }
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
        Text(
            modifier = Modifier.padding(vertical = (9.5).dp),
            text = text,
            fontFamily = pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 16.dp.toSp(),
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