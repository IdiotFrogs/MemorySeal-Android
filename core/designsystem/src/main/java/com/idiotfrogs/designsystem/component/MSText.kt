package com.idiotfrogs.designsystem.component

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.toSp
import com.idiotfrogs.resource.pretendard

@Composable
fun MSText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: Dp = 14.dp, // @Preview를 위한 디폴트 값, 해당 값이 그대로 사용 될 경우 기본 값으로도 사용 가능 (코드 스타일 개념)
    color: Color = MSTheme.color.black,
    fontFamily: FontFamily = pretendard,
    fontWeight: FontWeight = FontWeight.Bold, // @Preview를 위한 디폴트 값, 해당 값이 그대로 사용 될 경우 기본 값으로도 사용 가능 (코드 스타일 개념)
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    style: TextStyle = LocalTextStyle.current
) {
    Text(
        text = text,
        color = color,
        fontSize = fontSize.toSp(),
        fontFamily = fontFamily,
        fontWeight = fontWeight,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = onTextLayout,
        style = style,
        modifier = modifier
    )
}

@Composable
fun MSAnnotatedText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    fontSize: Dp = 14.dp, // @Preview를 위한 디폴트 값, 해당 값이 그대로 사용 될 경우 기본 값으로도 사용 가능 (코드 스타일 개념)
    color: Color = MSTheme.color.black,
    fontFamily: FontFamily = pretendard,
    fontWeight: FontWeight = FontWeight.Bold, // @Preview를 위한 디폴트 값, 해당 값이 그대로 사용 될 경우 기본 값으로도 사용 가능 (코드 스타일 개념)
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    style: TextStyle = LocalTextStyle.current
) {
    Text(
        text = text,
        color = color,
        fontSize = fontSize.toSp(),
        fontFamily = fontFamily,
        fontWeight = fontWeight,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        style = style,
        modifier = modifier
    )
}