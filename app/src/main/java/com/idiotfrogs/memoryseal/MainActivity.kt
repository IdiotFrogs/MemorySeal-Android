package com.idiotfrogs.memoryseal

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.idiotfrogs.auth.LoginScreen
import com.idiotfrogs.memoryseal.ui.theme.CodinTheme
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CodinTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // TODO: Navigation 처리
                    LoginScreen()
                }
            }
        }
    }
}