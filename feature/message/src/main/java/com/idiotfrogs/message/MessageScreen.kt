package com.idiotfrogs.message

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.idiotfrogs.designsystem.component.MSDetailHeader
import com.idiotfrogs.designsystem.component.MSDim
import com.idiotfrogs.designsystem.component.MSLoadingOverlay
import com.idiotfrogs.designsystem.component.MSPlainTextField
import com.idiotfrogs.designsystem.component.MSTabBar
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.component.button.MSButton
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.noRippleClickable
import com.idiotfrogs.designsystem.util.rememberMultiPickerState
import com.idiotfrogs.designsystem.util.wavyStroke
import com.idiotfrogs.message.component.MessageCheckBox
import com.idiotfrogs.message.component.MessagePreviewBanner
import com.idiotfrogs.message.component.MessageSettingListItem
import com.idiotfrogs.navigation.LocalComposeMSNavigator
import com.idiotfrogs.resource.R
import com.skydoves.landscapist.glide.GlideImage
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun MessageRoute(
    capsuleId: Long,
    viewModel: MessageViewModel = hiltViewModel(),
) {
    val navigator = LocalComposeMSNavigator.current
    val uiState by viewModel.collectAsState()

    viewModel.collectSideEffect { event ->
        when (event) {
            MessageSideEffect.NavigateToBack -> navigator.popBackStack()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        MessageScreen(
            capsuleId = capsuleId,
            onAction = viewModel::onAction,
        )

        MSLoadingOverlay(visible = uiState.isLoading)
    }
}

@Composable
fun MessageScreen(
    capsuleId: Long,
    onAction: (MessageAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    var currentTab by remember { mutableStateOf(MessageTab.MESSAGE) }
    var isDeleteMode by rememberSaveable { mutableStateOf(false) }
    var showMessageInput by rememberSaveable { mutableStateOf(false) }
    var activeMessageId by rememberSaveable { mutableStateOf<Long?>(null) }
    var messageItems by remember { mutableStateOf(emptyList<MessageListItemUiModel>()) }
    var selectedIds by rememberSaveable { mutableStateOf(emptySet<Long>()) }
    var photoItems by remember { mutableStateOf(emptyList<PhotoListItemUiModel>()) }

    val messageTextFieldState = rememberTextFieldState()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val (pickedPhotoUris, launchPhotoPicker) = rememberMultiPickerState()
    val pagerState = rememberPagerState { MessageTab.entries.size }
    val canDelete = selectedIds.isNotEmpty()
    val activeMessageItem = messageItems.firstOrNull { it.id == activeMessageId }

    fun closeMessageInput() {
        showMessageInput = false
        activeMessageId = null
        messageTextFieldState.clearText()
        focusManager.clearFocus()
        keyboardController?.hide()
    }

    fun saveMessage() {
        val message = messageTextFieldState.text.toString()

        if (message.isBlank()) return

        if (activeMessageId == null) {
            messageItems = messageItems + MessageListItemUiModel(
                id = (messageItems.maxOfOrNull { it.id } ?: 0L) + 1L,
                title = "메시지 ${messageItems.size + 1}",
                description = message,
            )
        } else {
            messageItems = messageItems.map { item ->
                if (item.id == activeMessageId) {
                    item.copy(description = message)
                } else {
                    item
                }
            }
        }

        closeMessageInput()
    }

    LaunchedEffect(currentTab) {
        isDeleteMode = false
        showMessageInput = false
        activeMessageId = null
        selectedIds = emptySet()
        pagerState.animateScrollToPage(currentTab.ordinal)
    }

    LaunchedEffect(pagerState.currentPage) {
        currentTab = MessageTab.entries[pagerState.currentPage]
    }

    LaunchedEffect(pickedPhotoUris) {
        if (pickedPhotoUris.isNotEmpty()) {
            val existingUris = photoItems.map { it.uri }.toSet()
            val newUris = pickedPhotoUris
                .distinct()
                .filterNot { it in existingUris }
            val nextPhotoId = (photoItems.maxOfOrNull { it.id } ?: 0L) + 1L

            photoItems = photoItems + newUris.mapIndexed { index, uri ->
                PhotoListItemUiModel(
                    id = nextPhotoId + index,
                    uri = uri,
                )
            }
        }
    }

    LaunchedEffect(showMessageInput) {
        if (showMessageInput) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MSTheme.color.white),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
        ) {
            MSDetailHeader(
                title = "나의 추억 메시지",
                navigateToBack = { onAction(MessageAction.NavigateToBack) },
                paddingValues = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            ) {
                Image(
                    modifier = Modifier
                        .size(24.dp)
                        .noRippleClickable {
                            isDeleteMode = true
                            selectedIds = emptySet()
                            activeMessageId = null
                        },
                    painter = painterResource(R.drawable.ic_trashcan),
                    contentDescription = "삭제",
                )
            }

            MSTabBar(
                tabs = MessageTab.entries.map { it.title },
                selectedIndex = currentTab.ordinal,
                onClick = { currentTab = MessageTab.entries[it] },
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                val tab = MessageTab.entries[page]

                when (tab) {
                    MessageTab.MESSAGE -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = 20.dp,
                                end = 20.dp,
                                top = 20.dp,
                                bottom = if (isDeleteMode) 96.dp else 88.dp,
                            ),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            item {
                                MessagePreviewBanner(
                                    imageRes = tab.bannerImageRes,
                                    title = tab.bannerTitle,
                                    description = tab.bannerDescription,
                                    onPreviewClick = {
                                        // TODO 미리보기 화면이 추가되면 연결
                                    },
                                )
                            }

                            items(messageItems, key = { it.id }) { item ->
                                MessageSettingListItem(
                                    title = item.title,
                                    description = item.description,
                                    isDeleteMode = isDeleteMode,
                                    isSelected = item.id in selectedIds,
                                    onClick = {
                                        if (isDeleteMode) {
                                            selectedIds = selectedIds.toggle(item.id)
                                        } else {
                                            activeMessageId = item.id
                                        }
                                    },
                                )
                            }
                        }
                    }

                    MessageTab.PHOTO -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = 20.dp,
                                end = 20.dp,
                                top = 20.dp,
                                bottom = if (isDeleteMode) 96.dp else 88.dp,
                            ),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                MessagePreviewBanner(
                                    imageRes = tab.bannerImageRes,
                                    title = tab.bannerTitle,
                                    description = tab.bannerDescription,
                                    onPreviewClick = {
                                        // TODO 미리보기 화면이 추가되면 연결
                                    },
                                )
                            }

                            items(photoItems, key = { it.id }) { item ->
                                val isSelected = item.id in selectedIds

                                Box(
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .then(
                                            if (isSelected) {
                                                Modifier.wavyStroke(
                                                    color = MSTheme.color.primaryNormal,
                                                    strokeWidth = 4.dp,
                                                    amplitude = 1.dp,
                                                    spacing = 2.dp,
                                                    clipContent = true,
                                                )
                                            } else { Modifier.clip(RoundedCornerShape(12.dp)) },
                                        )
                                        .noRippleClickable {
                                            if (isDeleteMode) {
                                                selectedIds = selectedIds.toggle(item.id)
                                            }
                                        },
                                ) {
                                    GlideImage(
                                        modifier = Modifier.matchParentSize(),
                                        imageModel = { item.uri },
                                    )

                                    if (isDeleteMode) {
                                        MessageCheckBox(
                                            modifier = Modifier
                                                .align(Alignment.TopStart)
                                                .padding(12.dp),
                                            isSelected = isSelected,
                                            unselectedBorderColor = MSTheme.color.white,
                                            onClick = { selectedIds = selectedIds.toggle(item.id) },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (isDeleteMode) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                MSButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    onClick = {
                        isDeleteMode = false
                        selectedIds = emptySet()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MSTheme.color.greyG1,
                    ),
                    pressColors = ButtonDefaults.buttonColors(
                        containerColor = MSTheme.color.greyG1,
                    ),
                    wavyStrokeColor = MSTheme.color.greyG1,
                ) {
                    MSText(
                        text = "취소",
                        fontSize = 16.dp,
                        color = MSTheme.color.greyG4,
                    )
                }
                MSButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    enabled = canDelete,
                    onClick = {
                        when (currentTab) {
                            MessageTab.MESSAGE -> {
                                messageItems = messageItems.filterNot {
                                    it.id in selectedIds
                                }
                            }

                            MessageTab.PHOTO -> {
                                photoItems = photoItems.filterNot {
                                    it.id in selectedIds
                                }
                            }
                        }

                        selectedIds = emptySet()
                        isDeleteMode = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFED1E2F),
                        disabledContainerColor = Color(0xFFF3BBBB),
                    ),
                    pressColors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFED1E2F),
                        disabledContainerColor = Color(0xFFF3BBBB),
                    ),
                    wavyStrokeColor = if (canDelete) Color(0xFFED1E2F) else Color(0xFFF3BBBB),
                ) {
                    MSText(
                        text = "삭제",
                        fontSize = 16.dp,
                        color = if (canDelete) MSTheme.color.white else Color(0x59DA1B1B),
                    )
                }
            }
        } else if (!showMessageInput && activeMessageItem == null) {
            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(end = 20.dp, bottom = 24.dp)
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
                    hoveredElevation = 4.dp,
                ),
                onClick = {
                    when (currentTab) {
                        MessageTab.MESSAGE -> {
                            activeMessageId = null
                            messageTextFieldState.clearText()
                            showMessageInput = true
                        }
                        MessageTab.PHOTO -> launchPhotoPicker()
                    }
                },
            ) {
                Image(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = "추억 메시지 추가",
                )
            }
        }

        if (activeMessageItem != null && !showMessageInput) {
            MSDim(
                visible = true,
                onDismiss = { activeMessageId = null },
            )

            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(horizontal = 20.dp)
                    .wavyStroke(
                        color = MSTheme.color.white,
                        fillColor = MSTheme.color.white,
                        strokeWidth = 4.dp,
                        contentPadding = 16.dp,
                    )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        modifier = Modifier
                            .size(24.dp)
                            .noRippleClickable {
                                messageTextFieldState.setTextAndPlaceCursorAtEnd(activeMessageItem.description)
                                showMessageInput = true
                            },
                        painter = painterResource(R.drawable.img_message_edit),
                        contentDescription = "메시지 수정",
                    )
                    Spacer(Modifier.weight(1f))
                    Image(
                        modifier = Modifier
                            .size(24.dp)
                            .noRippleClickable {
                                activeMessageId = null
                            },
                        painter = painterResource(R.drawable.img_cancel),
                        contentDescription = "닫기",
                    )
                }

                Spacer(Modifier.height(16.dp))

                MSText(
                    text = activeMessageItem.description,
                    fontWeight = FontWeight.Normal,
                    color = MSTheme.color.greyG5,
                )
            }
        }

        if (showMessageInput) {
            val canSave = messageTextFieldState.text.isNotBlank()

            MSDim(
                visible = true,
                onDismiss = { closeMessageInput() },
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        color = MSTheme.color.white,
                        shape = RoundedCornerShape(
                            topStart = 24.dp,
                            topEnd = 24.dp,
                        ),
                    )
                    .height(370.dp)
                    .padding(horizontal = 20.dp, vertical = 24.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    MSText(
                        text = "메시지",
                        color = MSTheme.color.greyG5,
                    )
                    Spacer(Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .noRippleClickable { closeMessageInput() }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                    ) {
                        MSText(
                            text = "취소",
                            color = MSTheme.color.greyG3,
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                color = MSTheme.color.primaryLight,
                                shape = RoundedCornerShape(8.dp),
                            )
                            .then(
                                if (canSave) {
                                    Modifier.noRippleClickable { saveMessage() }
                                } else {
                                    Modifier
                                },
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                    ) {
                        MSText(
                            text = "저장",
                            color = if (canSave) {
                                MSTheme.color.primaryDark
                            } else {
                                Color(0xFF84B591)
                            },
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                MSPlainTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .focusRequester(focusRequester),
                    hint = "공유하고 싶은 메시지를 작성해보세요!",
                    textFieldState = messageTextFieldState,
                )
            }
        }
    }
}

private enum class MessageTab(
    val title: String,
    val bannerImageRes: Int,
    val bannerTitle: String,
    val bannerDescription: String,
) {
    MESSAGE(
        title = "메시지",
        bannerImageRes = R.drawable.img_message_banner,
        bannerTitle = "메시지",
        bannerDescription = "메시지로 추억을 남겨보세요.",
    ),
    PHOTO(
        title = "사진",
        bannerImageRes = R.drawable.img_photo_banner,
        bannerTitle = "사진",
        bannerDescription = "사진으로 추억을 남겨보세요.",
    ),
}

private data class MessageListItemUiModel(
    val id: Long,
    val title: String,
    val description: String,
)

private data class PhotoListItemUiModel(
    val id: Long,
    val uri: Uri,
)

private fun Set<Long>.toggle(id: Long): Set<Long> =
    if (id in this) this - id else this + id

@Preview
@Composable
fun MessageScreenPreview() {
    MessageScreen(
        capsuleId = 0L,
        onAction = {},
    )
}
