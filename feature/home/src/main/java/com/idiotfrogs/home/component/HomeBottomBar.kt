package com.idiotfrogs.home.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.component.MSDim
import com.idiotfrogs.designsystem.component.MSMenuFab
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.model.MSMenuFabModel
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.noRippleClickable
import com.idiotfrogs.designsystem.util.wavyStroke
import com.idiotfrogs.resource.R

enum class BottomMenu { HOME, OPENED }

@Composable
fun HomeBottomBar(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    showDim: Boolean,
    fabMenuList: List<MSMenuFabModel>,
    selectedMenu: BottomMenu,
    onSelectChange: (BottomMenu) -> Unit,
    onExpandChange: (Boolean) -> Unit,
    onClickDim: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .wavyStroke(
                        color = MSTheme.color.greyG5,
                        cornerRadius = 54.dp,
                        fillColor = MSTheme.color.white,
                        amplitude = (0.5).dp,
                        spacing = 2.dp
                    )
            ) {
                val itemWidth = maxWidth / BottomMenu.entries.size
                val indicatorOffset by animateDpAsState(
                    targetValue = itemWidth * (BottomMenu.entries.find { it == selectedMenu }?.ordinal ?: 0),
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    label = "indicatorOffset"
                )

                Box(
                    modifier = Modifier
                        .width(itemWidth)
                        .offset(x = indicatorOffset)
                        .fillMaxHeight()
                        .wavyStroke(
                            color = MSTheme.color.greyG5,
                            cornerRadius = 54.dp,
                            fillColor = MSTheme.color.greyG5,
                            amplitude = (0.7).dp,
                            spacing = 2.dp,
                        )
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .noRippleClickable(onClick = { onSelectChange.invoke(BottomMenu.HOME) }),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(
                                if (selectedMenu == BottomMenu.HOME) {
                                    R.drawable.ic_home_colored
                                } else {
                                    R.drawable.ic_home
                                }
                            ),
                            contentDescription = "ic_home"
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        MSText(
                            text = "홈",
                            color = if (selectedMenu == BottomMenu.HOME) {
                                MSTheme.color.white
                            } else {
                                MSTheme.color.greyG3
                            },
                            fontWeight = if (selectedMenu == BottomMenu.HOME) {
                                FontWeight.SemiBold
                            } else {
                                FontWeight.Medium
                            },
                            fontSize = 10.dp
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .noRippleClickable(onClick = { onSelectChange.invoke(BottomMenu.OPENED) }),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(
                                if (selectedMenu == BottomMenu.OPENED) {
                                    R.drawable.ic_opened_ticket_colored
                                } else {
                                    R.drawable.ic_opened_ticket
                                }
                            ),
                            contentDescription = "ic_home"
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        MSText(
                            text = "오픈된 티켓",
                            color = if (selectedMenu == BottomMenu.OPENED) {
                                MSTheme.color.white
                            } else {
                                MSTheme.color.greyG3
                            },
                            fontWeight = if (selectedMenu == BottomMenu.OPENED) {
                                FontWeight.SemiBold
                            } else {
                                FontWeight.Medium
                            },
                            fontSize = 10.dp
                        )
                    }
                }
            }
            // 실제 FAB 는 딤 위에 그리므로 여기서는 자리만 잡아둔다
            Spacer(modifier = Modifier.size(56.dp))
        }
        // 하단바 영역 전체를 딤으로 덮고 FAB 만 그 위로 올려 딤에서 제외한다
        MSDim(
            visible = showDim,
            onDismiss = onClickDim
        )
        MSMenuFab(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 20.dp),
            expanded = expanded,
            hasFab = true,
            offset = DpOffset(x = 0.dp, y = (-16).dp),
            menuList = fabMenuList,
            onClick = { onExpandChange.invoke(!expanded) },
            onDismiss = { onExpandChange.invoke(false) },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeBottomBarPreview() {
    var showDim by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var selectedMenu by remember { mutableStateOf(BottomMenu.HOME) }
    val menuList by remember {
        mutableStateOf(
            listOf(
                MSMenuFabModel("새 티켓 생성하기") { expanded = false },
                MSMenuFabModel("참여코드로 합류하기") { expanded = false },
            )
        )
    }
    HomeBottomBar(
        fabMenuList = menuList,
        showDim = showDim,
        expanded = expanded,
        selectedMenu = selectedMenu,
        onSelectChange = { selectedMenu = it },
        onExpandChange = { expanded = it },
        onClickDim = { }
    )
}