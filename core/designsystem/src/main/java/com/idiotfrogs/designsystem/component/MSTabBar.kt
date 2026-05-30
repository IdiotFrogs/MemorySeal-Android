package com.idiotfrogs.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.DrawType
import com.idiotfrogs.designsystem.util.wavyStroke

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MSTabBar(
    tabs: List<String>,
    selectedIndex: Int,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    CompositionLocalProvider(LocalRippleConfiguration provides null) {
        TabRow(
            modifier = modifier,
            selectedTabIndex = selectedIndex,
            containerColor = MSTheme.color.white,
            indicator = { tabPositions ->
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedIndex])
                        .padding(horizontal = 54.dp)
                        .wavyStroke(
                            color = MSTheme.color.greyG5,
                            drawType = DrawType.BOTTOM,
                            spacing = 3.dp,
                        )
                )
            },
            divider = {},
        ) {
            tabs.forEachIndexed { index, tab ->
                MSTabItem(
                    selected = selectedIndex == index,
                    text = tab,
                    onClick = { onClick(index) },
                )
            }
        }
    }
}

@Composable
private fun MSTabItem(
    selected: Boolean,
    text: String,
    onClick: () -> Unit,
) {
    Tab(
        selected = selected,
        selectedContentColor = MSTheme.color.black,
        unselectedContentColor = MSTheme.color.greyG3,
        onClick = onClick,
    ) {
        MSText(
            modifier = Modifier.padding(vertical = 16.dp),
            text = text,
            fontSize = 16.dp,
        )
    }
}
