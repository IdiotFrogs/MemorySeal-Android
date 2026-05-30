package com.idiotfrogs.designsystem.component

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.model.MSMenuFabModel
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.rememberPressState
import com.idiotfrogs.designsystem.util.toSp
import com.idiotfrogs.designsystem.util.wavyStroke
import com.idiotfrogs.resource.R
import com.idiotfrogs.resource.pretendard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MSMenuFab(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    hasFab: Boolean,
    offset: DpOffset,
    menuList: List<MSMenuFabModel>,
    onClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd
    ) {
        DropdownMenu(
            modifier = Modifier.wavyStroke(
                color = MSTheme.color.white,
                fillColor = MSTheme.color.white
            ),
            offset = offset,
            expanded = expanded,
            onDismissRequest = onDismiss,
            shadowElevation = 0.dp,
            shape = RoundedCornerShape(12.dp),
            containerColor = Color.Transparent
        ) {
            CompositionLocalProvider(LocalRippleConfiguration provides null) {
                menuList.forEach {
                    val (msMenuFabInteractionSource, msMenuFabIsPressed) = rememberPressState()

                    DropdownMenuItem(
                        modifier = Modifier
                            .height(35.dp)
                            .padding(horizontal = 8.dp)
                            .background(
                                color = if (msMenuFabIsPressed) MSTheme.color.greyG1 else Color.Transparent,
                                shape = RoundedCornerShape(4.dp)
                            ),
                        text = {
                            Text(
                                text = it.text,
                                fontFamily = pretendard,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.dp.toSp(),
                            )
                        },
                        contentPadding = PaddingValues(start = 8.dp, end = 23.dp),
                        interactionSource = msMenuFabInteractionSource,
                        onClick = { it.onClick.invoke() }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
        if (hasFab) {
            FloatingActionButton(
                modifier = Modifier
                    .wavyStroke(
                        color = Color(0xFF29A047),
                        strokeWidth = 4.dp,
                        cornerRadius = 28.dp,
                        amplitude = 1.dp,
                        spacing = 2.dp,
                    )
                    .size(56.dp)
                    .padding(2.dp),
                shape = CircleShape,
                containerColor = MSTheme.color.primaryNormal,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 4.dp,
                    focusedElevation = 4.dp,
                    hoveredElevation = 4.dp
                ),
                onClick = onClick
            ) {
                val imageRes = R.drawable.ic_add
                Image(
                    modifier = Modifier
                        .size(24.dp)
                        .graphicsLayer {
                            rotationZ = if (expanded) 45f else 0f
                        },
                    painter = painterResource(imageRes),
                    contentDescription = "fab content"
                )
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview
@Composable
private fun MSMenuFabPreview() {
    var expanded by remember { mutableStateOf(false) }
    val menuList = listOf(
        MSMenuFabModel(
            text = "새 티켓 생성하기",
            onClick = {}
        ),
        MSMenuFabModel(
            text = "참여코드로 합류하기",
            onClick = {}
        ),
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            MSMenuFab(
                expanded = expanded,
                offset = DpOffset(x = 0.dp, y = ((-16).dp)),
                hasFab = true,
                menuList = menuList,
                onClick = { expanded = !expanded },
                onDismiss = { expanded = false },
            )
        },
        content = { }
    )
}