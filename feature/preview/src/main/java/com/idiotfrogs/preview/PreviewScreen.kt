package com.idiotfrogs.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.idiotfrogs.designsystem.component.MSDetailHeader
import com.idiotfrogs.designsystem.component.MSLoadingOverlay
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.component.MSTimeCapsuleContentItem
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.wavyStroke
import com.idiotfrogs.navigation.LocalComposeMSNavigator
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun PreviewRoute(
    capsuleId: Long,
    viewModel: PreviewViewModel = hiltViewModel<PreviewViewModel, PreviewViewModel.Factory>(key = capsuleId.toString()) {
        it.create(capsuleId)
    },
) {
    val navigator = LocalComposeMSNavigator.current
    val uiState by viewModel.collectAsState()

    viewModel.collectSideEffect { event ->
        when (event) {
            PreviewSideEffect.NavigateToBack -> navigator.popBackStack()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        PreviewScreen(
            data = uiState.data ?: PreviewData(),
            onAction = viewModel::onAction,
        )

        MSLoadingOverlay(visible = uiState.isLoading)
    }
}

@Composable
fun PreviewScreen(
    data: PreviewData,
    onAction: (PreviewAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MSTheme.color.white)
            .systemBarsPadding(),
    ) {
        MSDetailHeader(
            title = "미리보기",
            fontSize = 20.dp,
            navigateToBack = { onAction(PreviewAction.BackClicked) },
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 12.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            item(key = "preview-guide") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .wavyStroke(
                            color = MSTheme.color.bgNormal,
                            fillColor = MSTheme.color.bgNormal,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    MSText(
                        text = "미리보기는 자신이 등록한 내용만\n확인하실 수 있습니다.",
                        fontSize = 14.dp,
                        fontWeight = FontWeight.Medium,
                        color = MSTheme.color.greyG4,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            if (data.contents.isEmpty()) {
                item(key = "preview-empty") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 80.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        MSText(
                            text = "아직 등록된 추억이 없어요.",
                            fontSize = 14.dp,
                            fontWeight = FontWeight.Medium,
                            color = MSTheme.color.greyG3,
                        )
                    }
                }
            } else {
                items(
                    items = data.contents,
                    key = { it.contentId },
                ) { content ->
                    MSTimeCapsuleContentItem(
                        message = content.content?.takeIf { it.isNotBlank() },
                        imageUrls = content.attachedFiles
                            .orEmpty()
                            .map { it.fileUrl },
                        isMine = true,
                    )
                }
            }
        }
    }
}
