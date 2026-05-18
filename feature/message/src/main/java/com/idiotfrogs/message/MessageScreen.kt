package com.idiotfrogs.message

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items as lazyColumnItems
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.idiotfrogs.designsystem.component.MSDetailHeader
import com.idiotfrogs.designsystem.component.MSDim
import com.idiotfrogs.designsystem.component.MSPlainTextField
import com.idiotfrogs.designsystem.component.MSTabBar
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.component.button.MSButton
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.rememberMultiPickerState
import com.idiotfrogs.designsystem.util.noRippleClickable
import com.idiotfrogs.designsystem.util.wavyStroke
import com.idiotfrogs.message.component.MessageSettingListItem
import com.idiotfrogs.message.component.MessagePreviewBanner
import com.idiotfrogs.navigation.LocalComposeMSNavigator
import com.idiotfrogs.resource.R
import com.idiotfrogs.util.UiState
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

    when (uiState) {
        UiState.Init -> Unit
        is UiState.Success -> {
            MessageScreen(
                capsuleId = capsuleId,
                onAction = viewModel::onAction,
            )
        }
        is UiState.Error -> Unit
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
    var messageItems by remember { mutableStateOf(emptyList<MessageListItemUiModel>()) }
    var selectedMessageId by rememberSaveable { mutableStateOf<Long?>(null) }
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var photoUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val messageTextFieldState = rememberTextFieldState()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val (pickedPhotoUris, launchPhotoPicker) = rememberMultiPickerState()
    val pagerState = rememberPagerState { MessageTab.entries.size }
    val canDelete = when (currentTab) {
        MessageTab.MESSAGE -> selectedMessageId != null
        MessageTab.PHOTO -> selectedPhotoUri != null
    }

    LaunchedEffect(currentTab) {
        isDeleteMode = false
        showMessageInput = false
        selectedMessageId = null
        selectedPhotoUri = null
        pagerState.animateScrollToPage(currentTab.ordinal)
    }

    LaunchedEffect(pagerState.currentPage) {
        currentTab = MessageTab.entries[pagerState.currentPage]
    }

    LaunchedEffect(pickedPhotoUris) {
        if (pickedPhotoUris.isNotEmpty()) {
            photoUris = (photoUris + pickedPhotoUris).distinct()
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
                            selectedMessageId = null
                            selectedPhotoUri = null
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
                                top = 16.dp,
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

                            lazyColumnItems(messageItems, key = { it.id }) { item ->
                                MessageSettingListItem(
                                    title = item.title,
                                    description = item.description,
                                    isDeleteMode = isDeleteMode,
                                    isSelected = selectedMessageId == item.id,
                                    onClick = {
                                        if (isDeleteMode) {
                                            selectedMessageId = item.id
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
                                top = 16.dp,
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

                            items(photoUris, key = { it.toString() }) { uri ->
                                Box(
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .noRippleClickable {
                                            if (isDeleteMode) {
                                                selectedPhotoUri = uri
                                            }
                                        },
                                ) {
                                    GlideImage(
                                        modifier = Modifier.matchParentSize(),
                                        imageModel = { uri },
                                    )

                                    if (isDeleteMode) {
                                        RadioButton(
                                            modifier = Modifier
                                                .align(Alignment.TopStart)
                                                .padding(4.dp),
                                            selected = selectedPhotoUri == uri,
                                            onClick = { selectedPhotoUri = uri },
                                            colors = RadioButtonDefaults.colors(
                                                selectedColor = MSTheme.color.greyG5,
                                                unselectedColor = MSTheme.color.greyG5,
                                            ),
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
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                MSButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    onClick = {
                        isDeleteMode = false
                        selectedMessageId = null
                        selectedPhotoUri = null
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
                        fontSize = 14.dp,
                        color = MSTheme.color.greyG5,
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
                                    it.id == selectedMessageId
                                }
                                selectedMessageId = null
                            }

                            MessageTab.PHOTO -> {
                                photoUris = photoUris.filterNot {
                                    it == selectedPhotoUri
                                }
                                selectedPhotoUri = null
                            }
                        }

                        isDeleteMode = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MSTheme.color.red,
                        disabledContainerColor = Color(0xFFF3BBBB),
                    ),
                    pressColors = ButtonDefaults.buttonColors(
                        containerColor = MSTheme.color.red,
                        disabledContainerColor = Color(0xFFF3BBBB),
                    ),
                    wavyStrokeColor = if (canDelete) MSTheme.color.red else Color(0xFFF3BBBB),
                ) {
                    MSText(
                        text = "삭제",
                        fontSize = 14.dp,
                        color = if (canDelete) MSTheme.color.white else Color(0x59DA1B1B),
                    )
                }
            }
        } else if (!showMessageInput) {
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
                        MessageTab.MESSAGE -> showMessageInput = true
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

        if (showMessageInput) {
            MSDim(
                visible = showMessageInput,
                onDismiss = {
                    showMessageInput = false
                    focusManager.clearFocus()
                    keyboardController?.hide()
                },
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
                    .height(176.dp)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    MSText(
                        text = "메시지",
                        fontSize = 12.dp,
                    )
                    Spacer(Modifier.weight(1f))
                    MSText(
                        modifier = Modifier.noRippleClickable {
                            showMessageInput = false
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        },
                        text = "취소",
                        fontSize = 12.dp,
                        color = MSTheme.color.greyG3,
                    )
                    Spacer(Modifier.width(16.dp))
                    MSText(
                        modifier = Modifier.noRippleClickable {
                            val message = messageTextFieldState.text.toString()

                            if (message.isNotBlank()) {
                                messageItems = messageItems + MessageListItemUiModel(
                                    id = (messageItems.maxOfOrNull { it.id } ?: 0L) + 1L,
                                    title = "메시지 ${messageItems.size + 1}",
                                    description = message,
                                )
                                messageTextFieldState.edit {
                                    replace(0, length, "")
                                }
                                showMessageInput = false
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                        },
                        text = "저장",
                        fontSize = 12.dp,
                        color = if (messageTextFieldState.text.isNotBlank()) {
                            MSTheme.color.primaryNormal
                        } else {
                            MSTheme.color.primaryLight
                        },
                    )
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

@Preview
@Composable
fun MessageScreenPreview() {
    MessageScreen(
        capsuleId = 0L,
        onAction = {},
    )
}
