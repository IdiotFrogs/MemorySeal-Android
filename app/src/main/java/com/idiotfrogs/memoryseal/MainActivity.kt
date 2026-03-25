package com.idiotfrogs.memoryseal

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.idiotfrogs.auth.login.LoginRoute
import com.idiotfrogs.auth.signup.SignUpRoute
import com.idiotfrogs.create.CreateRoute
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.detail.DetailScreen
import com.idiotfrogs.friend.FriendScreen
import com.idiotfrogs.home.HomeScreen
import com.idiotfrogs.navigation.LocalComposeMSNavigator
import com.idiotfrogs.navigation.MSNavigatorImpl
import com.idiotfrogs.navigation.Routes
import com.idiotfrogs.profile.editprofile.EditProfileScreen
import com.idiotfrogs.profile.profile.ProfileScreen
import com.idiotfrogs.setting.SettingScreen
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        mainViewModel.collectAppSideEffect()
        setContent {
            MSTheme {
                val backStack = rememberNavBackStack(Routes.Login)
                val navigator = remember(backStack) { MSNavigatorImpl(backStack) }

                LaunchedEffect(Unit) {
                    mainViewModel.event.collect { sideEffect ->
                        when(sideEffect) {
                            MainEvent.NavigateToLogin -> {
                                backStack.clear()
                                navigator.navigate(Routes.Login)
                            }
                        }
                    }
                }

                CompositionLocalProvider(
                    LocalComposeMSNavigator provides navigator
                ) {
                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .imePadding()
                    ) { _ ->
                        NavDisplay(
                            backStack = backStack,
                            entryProvider = entryProvider {
                                entry<Routes.Login> { LoginRoute() }
                                entry<Routes.SignUp> { SignUpRoute() }
                                entry<Routes.Home> { HomeScreen() }
                                entry<Routes.Create> { CreateRoute() }
                                entry<Routes.Profile> { ProfileScreen() }
                                entry<Routes.EditProfile> { EditProfileScreen() }
                                entry<Routes.Setting> { SettingScreen() }
                                entry<Routes.Detail> {
                                    DetailScreen(
                                        title = "테스트 Title",
                                        date = "2025-11-28",
                                        isMember = true,
                                        isVoteStart = true,
                                        iSSeal = false,
                                        onSealClicked = {},
                                        onVoteClicked = {}
                                    )
                                    // TODO 추 후 데이터 구조 및 id 가져가서 정보 조회되게 수정 필요
                                    it.id
                                }
                                entry<Routes.Message> {
                                    // TODO 메시지 화면 퍼블리싱 후 추가하기
                                    it.id
                                }
                                entry<Routes.Friend> {
                                    FriendScreen()
                                    // TODO 추 후 id 가져가서 정보 조회되게 수정 필요
                                    it.id
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}