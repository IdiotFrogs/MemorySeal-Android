package com.idiotfrogs.home.component

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.resource.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeMenuFab(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onClick: () -> Unit,
    onDismiss: () -> Unit,
    onCreateTicket: () -> Unit,
    onJoinWithCode: () -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd
    ) {
        DropdownMenu(
            offset = DpOffset(x = 0.dp, y = (-16).dp),
            expanded = expanded,
            onDismissRequest = onDismiss,
            shadowElevation = 0.dp,
            shape = RoundedCornerShape(12.dp)
        ) {
            val createTicketInteractionSource = remember { MutableInteractionSource() }
            val createTicketIsPressed by createTicketInteractionSource.collectIsPressedAsState()

            val joinWithCodeInteractionSource = remember { MutableInteractionSource() }
            val joinWithCodeIsPressed by joinWithCodeInteractionSource.collectIsPressedAsState()

            CompositionLocalProvider(LocalRippleConfiguration provides null) {
                DropdownMenuItem(
                    modifier = Modifier
                        .height(35.dp)
                        .padding(horizontal = 8.dp)
                        .background(
                            color = if (createTicketIsPressed) MSTheme.color.greyG1 else Color.Transparent,
                            shape = RoundedCornerShape(4.dp)
                        ),
                    text = {
                        MSText(
                            text = "새 티켓 생성하기",
                            fontSize = 16.dp,
                        )
                    },
                    contentPadding = PaddingValues(start = 8.dp, end = 47.dp),
                    interactionSource = createTicketInteractionSource,
                    onClick = { onCreateTicket.invoke() }
                )
                Spacer(modifier = Modifier.height(4.dp))
                DropdownMenuItem(
                    modifier = Modifier
                        .height(35.dp)
                        .padding(horizontal = 8.dp)
                        .background(
                            color = if (joinWithCodeIsPressed) MSTheme.color.greyG1 else Color.Transparent,
                            shape = RoundedCornerShape(4.dp)
                        ),
                    text = {
                        MSText(
                            text = "참여코드로 합류하기",
                            fontSize = 16.dp
                        )
                    },
                    contentPadding = PaddingValues(start = 8.dp, end = 23.dp),
                    interactionSource = joinWithCodeInteractionSource,
                    onClick = { onJoinWithCode.invoke() }
                )
            }
        }
        FloatingActionButton(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            containerColor = if (expanded) MSTheme.color.white else MSTheme.color.primaryNormal,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 4.dp,
                pressedElevation = 4.dp,
                focusedElevation = 4.dp,
                hoveredElevation = 4.dp
            ),
            onClick = onClick
        ) {
            val imageRes = if (expanded) R.drawable.ic_close else R.drawable.ic_plus
            Image(
                painter = painterResource(imageRes),
                contentDescription = "fab content"
            )
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview
@Composable
private fun HomeMenuFabPreview() {
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            HomeMenuFab(
                expanded = expanded,
                onClick = { expanded = !expanded },
                onDismiss = { expanded = false },
                onCreateTicket = { },
                onJoinWithCode = { }
            )
        },
        content = { }
    )
}