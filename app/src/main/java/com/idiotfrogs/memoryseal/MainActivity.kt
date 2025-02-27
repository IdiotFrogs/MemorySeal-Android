package com.idiotfrogs.memoryseal

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.idiotfrogs.auth.login.loginScreen
import com.idiotfrogs.auth.util.LocalLoginManager
import com.idiotfrogs.data.LoginManager
import com.idiotfrogs.memoryseal.ui.theme.CodinTheme
import com.idiotfrogs.navigation.Routes
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var loginManager: LoginManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CodinTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    CompositionLocalProvider(LocalLoginManager provides loginManager) {
                        NavHost(
                            navController = rememberNavController(),
                            startDestination = Routes.Login
                        ) {
                            loginScreen()
                        }
                    }
                }
            }
        }
    }
}