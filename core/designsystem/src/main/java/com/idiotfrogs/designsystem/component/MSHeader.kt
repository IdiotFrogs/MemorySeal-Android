package com.idiotfrogs.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.util.noRippleClickable
import com.idiotfrogs.resource.R

@Composable
fun MSDetailHeader(
    navigateToBack: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "",
    paddingValues: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
    trailingContent: @Composable (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(paddingValues),
    ) {
        Icon(
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.CenterStart)
                .noRippleClickable { navigateToBack() },
            painter = painterResource(R.drawable.ic_chevron_left),
            contentDescription = "Back"
        )
        MSText(
            modifier = Modifier.align(Alignment.Center),
            text = title
        )
        trailingContent?.let {
            Box(Modifier.align(Alignment.CenterEnd)) { it() }
        }
    }
}

@Preview
@Composable
fun MSDetailHeaderPreview() {
    Column {
        MSDetailHeader(
            title = "타임 티켓 생성하기",
            navigateToBack = {}
        )
        Spacer(Modifier.height(30.dp))
        MSDetailHeader(
            title = "맴버 추가",
            navigateToBack = {}
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_plus),
                contentDescription = "Back"
            )
        }
    }
}