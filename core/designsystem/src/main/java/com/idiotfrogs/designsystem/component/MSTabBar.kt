package com.idiotfrogs.designsystem.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.DrawType
import com.idiotfrogs.designsystem.util.wavyStroke

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MSTabBar(
    tabs: List<String>,
    showBorder: Boolean = false,
    selectedIndex: Int,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var tabPositions by remember { mutableStateOf<List<TabPosition>?>(null) }

    Column {
        CompositionLocalProvider(LocalRippleConfiguration provides null) {
            TabRow(
                modifier = modifier.then(
                    if (showBorder) {
                        Modifier.border(
                            width = 2.dp,
                            color = MSTheme.color.bgNormal
                        )
                    } else {
                        Modifier
                    }
                ),
                selectedTabIndex = selectedIndex,
                containerColor = MSTheme.color.white,
                indicator = { tabPositions = it },
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
        Box(
            modifier = Modifier
                .then(
                    if (tabPositions != null) {
                        Modifier
                            .tabIndicatorOffset(tabPositions!![selectedIndex])
                    } else {
                        Modifier
                    }
                )
                .offset(y = 1.dp)
                .padding(horizontal = 54.dp)
                .wavyStroke(
                    color = MSTheme.color.greyG5,
                    drawType = DrawType.BOTTOM,
                    spacing = 3.dp
                )
        )
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
