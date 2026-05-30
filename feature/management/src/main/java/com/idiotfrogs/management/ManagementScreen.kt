package com.idiotfrogs.management

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.idiotfrogs.designsystem.component.MSDetailHeader
import com.idiotfrogs.designsystem.component.MSDim
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.noRippleClickable
import com.idiotfrogs.management.component.ManagementDeleteContainer
import com.idiotfrogs.navigation.LocalComposeMSNavigator
import com.idiotfrogs.navigation.Routes
import com.idiotfrogs.util.UiState
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ManagementRoute(
    viewModel: ManagementViewModel = hiltViewModel(),
    capsuleId: Long,
    capsuleTitle: String,
) {
    val navigator = LocalComposeMSNavigator.current
    val uiState by viewModel.collectAsState()

    viewModel.collectSideEffect { event ->
        when (event) {
            ManagementSideEffect.NavigateToHome -> navigator.navigate(Routes.Home)
            ManagementSideEffect.NavigateToBack -> navigator.popBackStack()
        }
    }

    when (uiState) {
        UiState.Init -> Unit
        is UiState.Success -> {
            ManagementScreen(
                capsuleId = capsuleId,
                capsuleTitle = capsuleTitle,
                onAction = viewModel::onAction
            )
        }
        is UiState.Error -> Unit
    }
}

@Composable
fun ManagementScreen(
    modifier: Modifier = Modifier,
    capsuleId: Long,
    capsuleTitle: String,
    onAction: (ManagementAction) -> Unit,
) {
    var showDeleteContainer by remember { mutableStateOf(false) }
    val ime = WindowInsets.ime
    val density = LocalDensity.current
    val imeHeight by remember { derivedStateOf { ime.getBottom(density) } }
    val textFieldState = rememberTextFieldState()

    LaunchedEffect(imeHeight) { if (showDeleteContainer && imeHeight == 0) showDeleteContainer = false }

    Box {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MSTheme.color.white)
                .systemBarsPadding()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            MSDetailHeader(
                title = "티켓 관리",
                navigateToBack = { onAction(ManagementAction.NavigateToBack) },
                paddingValues = PaddingValues(horizontal = 0.dp, vertical = 16.dp)
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MSTheme.color.bgNormal,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(16.dp)
                    .noRippleClickable { showDeleteContainer = true },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(com.idiotfrogs.resource.R.drawable.ic_trashcan),
                    contentDescription = "삭제"
                )
                Spacer(Modifier.width(4.dp))
                MSText(
                    text = "티켓 삭제",
                    fontSize = 16.dp,
                    fontWeight = FontWeight.SemiBold,
                    color = MSTheme.color.greyG4
                )
            }
        }
        MSDim(
            visible = showDeleteContainer,
            onDismiss = { showDeleteContainer = false }
        )
        ManagementDeleteContainer(
            isShow = showDeleteContainer,
            textFieldState = textFieldState,
            capsuleTitle = capsuleTitle,
            onDelete = {
                onAction(ManagementAction.DeleteCapsule(capsuleId))
                showDeleteContainer = false
            },
            onCancel = { showDeleteContainer = false }
        )
    }
}

@Preview
@Composable
fun ManagementScreenPreview() {
    ManagementScreen(capsuleId = 0, capsuleTitle = "티켓 Title") {}
}
