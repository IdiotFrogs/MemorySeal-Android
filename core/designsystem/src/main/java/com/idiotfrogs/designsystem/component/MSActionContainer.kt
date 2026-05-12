package com.idiotfrogs.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.component.button.MSButton
import com.idiotfrogs.designsystem.theme.MSTheme

@Composable
fun MSActionContainer(
    title: String,
    hint: String,
    textFieldState: TextFieldState,
    primaryButtonText: String,
    primaryWavyStrokeColor: Color,
    secondaryWavyStrokeColor: Color,
    onPrimaryClick: () -> Unit,
    onSecondaryClick: () -> Unit,
    modifier: Modifier = Modifier,
    primaryButtonEnabled: Boolean = textFieldState.text.isNotEmpty(),
    secondaryButtonText: String= "취소",
    primaryButtonWeight: Float = 1f,
    secondaryButtonWeight: Float = 1f,
    focusedBorderColor: Color = MSTheme.color.primaryNormal,
    unfocusedBorderColor: Color = MSTheme.color.greyG1,
    primaryButtonColors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MSTheme.color.primaryNormal,
        disabledContainerColor = MSTheme.color.primaryLight,
    ),
    primaryPressColors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MSTheme.color.primaryDark,
        disabledContainerColor = MSTheme.color.primaryLight,
    ),
    primaryTextColor: Color = MSTheme.color.white,
    secondaryButtonColors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MSTheme.color.greyG1,
        disabledContainerColor = MSTheme.color.greyG1,
    ),
    secondaryPressColors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MSTheme.color.greyG1,
        disabledContainerColor = MSTheme.color.greyG1,
    ),
    secondaryTextColor: Color = MSTheme.color.greyG5,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MSTheme.color.white,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            )
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MSText(text = title, fontSize = 16.dp)
        Spacer(modifier = Modifier.height(16.dp))
        MSTextField(
            modifier = Modifier.fillMaxWidth(),
            textFieldState = textFieldState,
            hint = hint,
            focusedBorderColor = focusedBorderColor,
            unfocusedBorderColor = unfocusedBorderColor,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MSButton(
                modifier = Modifier
                    .weight(secondaryButtonWeight)
                    .height(48.dp),
                onClick = onSecondaryClick,
                colors = secondaryButtonColors,
                pressColors = secondaryPressColors,
                wavyStrokeColor = secondaryWavyStrokeColor,
            ) {
                MSText(
                    text = secondaryButtonText,
                    fontSize = 16.dp,
                    color = secondaryTextColor,
                )
            }
            MSButton(
                modifier = Modifier
                    .weight(primaryButtonWeight)
                    .height(48.dp),
                enabled = primaryButtonEnabled,
                onClick = onPrimaryClick,
                colors = primaryButtonColors,
                pressColors = primaryPressColors,
                wavyStrokeColor = primaryWavyStrokeColor,
            ) {
                MSText(
                    text = primaryButtonText,
                    fontSize = 16.dp,
                    color = primaryTextColor,
                )
            }
        }
    }
}