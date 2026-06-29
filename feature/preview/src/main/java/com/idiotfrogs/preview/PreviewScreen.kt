package com.idiotfrogs.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.idiotfrogs.designsystem.component.MSDetailHeader
import com.idiotfrogs.designsystem.component.MSLoadingOverlay
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.navigation.LocalComposeMSNavigator
import com.idiotfrogs.preview.component.PreviewCollaboratorListItem
import com.idiotfrogs.preview.component.PreviewListItem
import kotlinx.coroutines.flow.distinctUntilChanged
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
    val collaborators = data.collaborators?.content.orEmpty()
    val myUserId = collaborators.firstOrNull { it.isMe }?.userId
    val collaboratorListState = rememberLazyListState()
    val contentListState = rememberLazyListState()
    var selectedUserId by rememberSaveable { mutableStateOf<Long?>(null) }
    val hasScrolledContent by remember {
        derivedStateOf {
            contentListState.firstVisibleItemScrollOffset > 0 ||
                contentListState.firstVisibleItemIndex > 0
        }
    }
    val visibleContentGroups = remember(data.contents) {
        data.contents.mapNotNull { contentGroup ->
            val visibleContents = contentGroup.capsuleContents.filter { content ->
                !content.content.isNullOrBlank() || !content.attachedFileUrls.isNullOrEmpty()
            }

            if (visibleContents.isEmpty()) null else contentGroup to visibleContents
        }
    }
    val currentSelectedUserId = selectedUserId
        ?: visibleContentGroups.firstOrNull()?.first?.userId
        ?: myUserId

    LaunchedEffect(visibleContentGroups.isNotEmpty(), collaborators) {
        if (selectedUserId == null) {
            selectedUserId = visibleContentGroups.firstOrNull()?.first?.userId ?: myUserId
        }
    }

    LaunchedEffect(contentListState, visibleContentGroups) {
        snapshotFlow { contentListState.firstVisibleItemIndex }
            .distinctUntilChanged()
            .collect { index ->
                visibleContentGroups
                    .getOrNull(index)
                    ?.first
                    ?.userId
                    ?.let { selectedUserId = it }
            }
    }

    LaunchedEffect(
        contentListState,
        visibleContentGroups.size,
        hasScrolledContent,
        data.isContentLast,
        data.isContentLoadingMore,
    ) {
        snapshotFlow { contentListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .distinctUntilChanged()
            .collect { lastVisibleIndex ->
                val shouldLoadNextPage = hasScrolledContent &&
                    lastVisibleIndex != null &&
                    visibleContentGroups.isNotEmpty() &&
                    lastVisibleIndex >= visibleContentGroups.lastIndex &&
                    !data.isContentLast &&
                    !data.isContentLoadingMore

                if (shouldLoadNextPage) {
                    onAction(PreviewAction.NextContentPageRequested)
                }
            }
    }

    LaunchedEffect(currentSelectedUserId, collaborators) {
        val collaboratorIndex = collaborators.indexOfFirst { it.userId == currentSelectedUserId }

        if (collaboratorIndex >= 0) {
            collaboratorListState.animateScrollToItem(collaboratorIndex)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MSTheme.color.white)
            .systemBarsPadding(),
    ) {
        MSDetailHeader(
            title = "미리보기",
            navigateToBack = { onAction(PreviewAction.BackClicked) },
        )

        LazyRow(
            state = collaboratorListState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items(
                items = collaborators,
                key = { it.userId },
            ) { collaborator ->
                PreviewCollaboratorListItem(
                    collaborator = collaborator,
                    isSelected = collaborator.userId == currentSelectedUserId,
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = MSTheme.color.greyG1
        )

        LazyColumn(
            state = contentListState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = 20.dp,
                vertical = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            if (visibleContentGroups.isEmpty()) {
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
                visibleContentGroups.forEach { (contentGroup, visibleContents) ->
                    item(key = "user-${contentGroup.userId}") {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            visibleContents.forEachIndexed { index, content ->
                                val isMine = contentGroup.userId == myUserId

                                PreviewListItem(
                                    contentGroup = contentGroup,
                                    content = content,
                                    isMine = isMine,
                                    showAuthor = !isMine && index == 0,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
