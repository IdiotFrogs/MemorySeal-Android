package com.idiotfrogs.memory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.idiotfrogs.designsystem.component.MSDetailHeader
import com.idiotfrogs.designsystem.component.MSLoadingIndicator
import com.idiotfrogs.designsystem.component.MSLoadingOverlay
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.component.MSTimeCapsuleContentItem
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.LoadNextPageEffect
import com.idiotfrogs.memory.component.MemoryCollaboratorListItem
import com.idiotfrogs.navigation.LocalComposeMSNavigator
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun MemoryRoute(
    capsuleId: Long,
    viewModel: MemoryViewModel = hiltViewModel<MemoryViewModel, MemoryViewModel.Factory>(key = capsuleId.toString()) {
        it.create(capsuleId)
    },
) {
    val navigator = LocalComposeMSNavigator.current
    val uiState by viewModel.collectAsState()

    viewModel.collectSideEffect { event ->
        when (event) {
            MemorySideEffect.NavigateToBack -> navigator.popBackStack()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        MemoryScreen(
            data = uiState.data ?: MemoryData(),
            onAction = viewModel::onAction,
        )

        MSLoadingOverlay(visible = uiState.isLoading)
    }
}

@Composable
fun MemoryScreen(
    data: MemoryData,
    onAction: (MemoryAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val collaborators = data.collaborators.items
    val memoryContents = data.memoryContents.items
    val myUserId = collaborators.firstOrNull { it.isMe }?.userId
    val collaboratorListState = rememberLazyListState()
    val contentListState = rememberLazyListState()
    val sequentialMemoryContents = memoryContents.filter {
        it.collaboratorIndex <= data.memoryContents.currentPage
    }
    val visibleMemoryContents = data.selectedCollaboratorIndex
        ?.let { selectedIndex ->
            memoryContents.filter { it.collaboratorIndex == selectedIndex }
        }
        ?: sequentialMemoryContents

    LoadNextPageEffect(
        listState = collaboratorListState,
        canLoadMore = data.collaborators.canLoadMore,
        onLoadNextPage = { onAction(MemoryAction.NextCollaboratorsPageRequested) },
    )

    if (data.selectedCollaboratorIndex == null) {
        LoadNextPageEffect(
            listState = contentListState,
            canLoadMore = data.memoryContents.canLoadMore,
            expectedLastItemKey = sequentialMemoryContents.lastOrNull()?.let { "user-${it.collaboratorIndex}" },
            onLoadNextPage = { onAction(MemoryAction.NextMemoryContentPageRequested) },
        )
    }

    LaunchedEffect(data.selectedCollaboratorIndex) {
        contentListState.scrollToItem(0)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MSTheme.color.white)
            .systemBarsPadding(),
    ) {
        MSDetailHeader(
            title = "추억 메시지",
            navigateToBack = { onAction(MemoryAction.BackClicked) },
        )

        LazyRow(
            state = collaboratorListState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            itemsIndexed(
                items = collaborators,
                key = { _, collaborator -> collaborator.userId },
            ) { index, collaborator ->
                MemoryCollaboratorListItem(
                    collaborator = collaborator,
                    isSelected = index == data.selectedCollaboratorIndex,
                    onClick = {
                        onAction(MemoryAction.CollaboratorClicked(index))
                    },
                )
            }

            if (data.collaborators.isLoadingMore) {
                item(key = "collaborator-loading") {
                    Box(
                        modifier = Modifier.size(60.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        MSLoadingIndicator()
                    }
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = MSTheme.color.greyG1,
        )

        LazyColumn(
            state = contentListState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = 20.dp,
                vertical = 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            if (visibleMemoryContents.isEmpty()) {
                item {
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
                visibleMemoryContents.forEach { memoryContent ->
                    item(key = "user-${memoryContent.collaboratorIndex}") {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            memoryContent.contents.forEachIndexed { index, content ->
                                val isMine = memoryContent.userId == myUserId

                                MSTimeCapsuleContentItem(
                                    uiModel = content.uiModel,
                                    isMine = isMine,
                                    authorNickname = memoryContent.nickname,
                                    authorProfileImageUrl = memoryContent.profileImageUrl,
                                    showAuthor = !isMine && index == 0,
                                )
                            }
                        }
                    }
                }
            }

            if (data.memoryContents.isLoadingMore) {
                item(key = "memory-content-loading") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        MSLoadingIndicator()
                    }
                }
            }
        }
    }
}
