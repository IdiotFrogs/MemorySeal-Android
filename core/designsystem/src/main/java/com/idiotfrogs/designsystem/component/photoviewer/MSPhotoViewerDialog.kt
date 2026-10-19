package com.idiotfrogs.designsystem.component.photoviewer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import com.idiotfrogs.designsystem.component.MSLoadingIndicator
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.toSp
import com.idiotfrogs.resource.R
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kotlin.math.abs
import kotlinx.coroutines.launch

private const val PHOTO_ASPECT_RATIO = 375f / 470f

@Immutable
data class MSPhotoViewerUiModel(
    val authorNickname: String,
    val imageUrls: List<String>,
    val initialIndex: Int,
)

@Composable
fun MSPhotoViewerDialog(
    viewer: MSPhotoViewerUiModel,
    onDismiss: () -> Unit,
) {
    val images = viewer.imageUrls
    if (images.isEmpty()) return

    val initialIndex = viewer.initialIndex.coerceIn(images.indices)
    val pagerState = rememberPagerState(
        initialPage = initialIndex,
        pageCount = { images.size },
    )
    val thumbnailState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialIndex,
    )
    var isThumbnailUserScroll by remember { mutableStateOf(false) }
    val thumbnailScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // Automatic centering uses LazyListState directly, outside this user-input path.
                if (source == NestedScrollSource.UserInput && available.x != 0f) {
                    isThumbnailUserScroll = true
                }
                return Offset.Zero
            }
        }
    }
    var thumbnailCenterRequest by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    val zoomState = remember(pagerState.settledPage) {
        PhotoZoomState()
    }
    val allowPageSwipe by remember(zoomState) {
        derivedStateOf { !zoomState.isZoomed && !zoomState.gestureActive }
    }

    LaunchedEffect(thumbnailState, pagerState) {
        snapshotFlow {
            if (isThumbnailUserScroll) {
                thumbnailState.layoutInfo.centeredItemIndex() to thumbnailState.isScrollInProgress
            } else {
                null
            }
        }.collect { selection ->
            val (index, isScrolling) = selection ?: return@collect
            if (index != null && index != pagerState.currentPage) {
                pagerState.requestScrollToPage(index)
            }
            if (!isScrolling) {
                // Finger release and the following fling can be one frame apart.
                withFrameNanos { }
                if (!thumbnailState.isScrollInProgress) isThumbnailUserScroll = false
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
            dismissOnBackPress = true,
        ),
    ) {
        val dialogView = LocalView.current
        val dialogWindow = (dialogView.parent as? DialogWindowProvider)?.window

        LaunchedEffect(dialogWindow, dialogView) {
            if (dialogWindow != null) {
                WindowCompat.getInsetsController(dialogWindow, dialogView).apply {
                    isAppearanceLightStatusBars = true
                    isAppearanceLightNavigationBars = true
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MSTheme.color.white)
                .systemBarsPadding(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 56.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    MSText(
                        text = viewer.authorNickname,
                        fontSize = 16.dp,
                        fontWeight = FontWeight.Bold,
                        color = MSTheme.color.greyG5,
                        lineHeight = 16.dp.toSp(),
                        letterSpacing = 0.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    MSText(
                        text = "${pagerState.currentPage + 1} / ${images.size}",
                        fontSize = 14.dp,
                        fontWeight = FontWeight.Medium,
                        color = MSTheme.color.greyG3,
                        lineHeight = 14.dp.toSp(),
                        letterSpacing = 0.sp,
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.CenterEnd),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = "사진 보기 닫기",
                        tint = MSTheme.color.black,
                    )
                }
            }

            Spacer(modifier = Modifier.height(60.dp))

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
            ) {
                val photoHeight = (maxWidth / PHOTO_ASPECT_RATIO).coerceAtMost(maxHeight)

                HorizontalPager(
                    state = pagerState,
                    flingBehavior = PagerDefaults.flingBehavior(
                        state = pagerState,
                        pagerSnapDistance = PagerSnapDistance.atMost(1),
                    ),
                    userScrollEnabled = allowPageSwipe && !isThumbnailUserScroll,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(photoHeight),
                ) { page ->
                    GlideImage(
                        imageModel = { images[page] },
                        modifier = Modifier.fillMaxSize(),
                        imageOptions = ImageOptions(
                            contentScale = ContentScale.Fit,
                            alignment = Alignment.Center,
                            contentDescription = "${viewer.authorNickname}님의 ${page + 1}번째 사진",
                        ),
                        success = { _, painter ->
                            val description = "${viewer.authorNickname}님의 ${page + 1}번째 사진"
                            if (page == pagerState.settledPage) {
                                ZoomablePhoto(
                                    painter = painter,
                                    zoomState = zoomState,
                                    contentDescription = description,
                                    canStartZoom = {
                                        !pagerState.isScrollInProgress && !isThumbnailUserScroll
                                    },
                                )
                            } else {
                                Image(
                                    painter = painter,
                                    contentDescription = description,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }
                        },
                        loading = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                MSLoadingIndicator()
                            }
                        },
                        failure = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                MSText(
                                    text = "사진을 불러오지 못했어요.",
                                    color = MSTheme.color.greyG3,
                                )
                            }
                        },
                    )
                }
            }

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp),
            ) {
                val thumbnailSize = 48.dp
                val edgePadding = ((maxWidth - thumbnailSize) / 2).coerceAtLeast(0.dp)
                val thumbnailSnapProvider =
                    rememberPhotoThumbnailSnapLayoutInfoProvider(thumbnailState)

                LaunchedEffect(
                    pagerState.currentPage,
                    maxWidth,
                    thumbnailCenterRequest,
                    isThumbnailUserScroll,
                ) {
                    if (!isThumbnailUserScroll &&
                        !thumbnailState.layoutInfo.isItemCentered(pagerState.currentPage)
                    ) {
                        thumbnailState.animateScrollToItem(pagerState.currentPage)
                    }
                }

                LazyRow(
                    state = thumbnailState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .nestedScroll(thumbnailScrollConnection),
                    contentPadding = PaddingValues(horizontal = edgePadding),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    flingBehavior = rememberSnapFlingBehavior(
                        snapLayoutInfoProvider = thumbnailSnapProvider,
                    ),
                    overscrollEffect = null,
                ) {
                    itemsIndexed(
                        items = images,
                        key = { index, _ -> index },
                    ) { index, url ->
                        val isSelected = pagerState.currentPage == index
                        val shape = RoundedCornerShape(8.dp)

                        Box(
                            modifier = Modifier
                                .size(thumbnailSize)
                                .clip(shape)
                                .background(MSTheme.color.white)
                                .selectable(
                                    selected = isSelected,
                                    role = Role.Tab,
                                    onClick = {
                                        isThumbnailUserScroll = false
                                        scope.launch {
                                            zoomState.reset()
                                            pagerState.scrollToPage(index)
                                            thumbnailCenterRequest++
                                        }
                                    },
                                )
                                .border(
                                    width = 2.dp,
                                    color = if (isSelected) {
                                        MSTheme.color.primaryNormal
                                    } else {
                                        Color.Transparent
                                    },
                                    shape = shape,
                                ),
                        ) {
                            GlideImage(
                                imageModel = { url },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .alpha(if (isSelected) 1f else 0.6f),
                                imageOptions = ImageOptions(
                                    contentScale = ContentScale.Crop,
                                    contentDescription = "${index + 1}번째 사진 보기",
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun LazyListLayoutInfo.centeredItemIndex(): Int? {
    val center = (viewportStartOffset + viewportEndOffset) / 2f
    return visibleItemsInfo.minByOrNull { item ->
        abs(item.offset + item.size / 2f - center)
    }?.index
}

private fun LazyListLayoutInfo.isItemCentered(index: Int): Boolean {
    val item = visibleItemsInfo.firstOrNull { it.index == index } ?: return false
    val center = (viewportStartOffset + viewportEndOffset) / 2f
    return abs(item.offset + item.size / 2f - center) <= 1f
}
