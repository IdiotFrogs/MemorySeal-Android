package com.idiotfrogs.designsystem.util

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun rememberPickerState(mimeType: String = "image/*"): Pair<Uri?, () -> Unit> {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { newUri ->
        if (newUri != null) {
            imageUri = newUri
        }
    }
    val launch = { launcher.launch(mimeType) }
    return imageUri to launch
}

@Composable
fun rememberMultiPickerState(mimeType: String = "image/*"): Pair<List<Uri>, () -> Unit> {
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { newUris ->
        if (newUris.isNotEmpty()) {
            imageUris = newUris
        }
    }
    val launch = { launcher.launch(mimeType) }
    return imageUris to launch
}