package com.idiotfrogs.memoryseal

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.idiotfrogs.auth.login.LoginRoute
import com.idiotfrogs.auth.signup.SignUpRoute
import com.idiotfrogs.create.CreateRoute
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.detail.DetailRoute
import com.idiotfrogs.friend.FriendRoute
import com.idiotfrogs.home.HomeRoute
import com.idiotfrogs.management.ManagementRoute
import com.idiotfrogs.memory.MemoryRoute
import com.idiotfrogs.message.MessageRoute
import com.idiotfrogs.navigation.LocalComposeMSNavigator
import com.idiotfrogs.navigation.MSNavigatorImpl
import com.idiotfrogs.navigation.Routes
import com.idiotfrogs.preview.PreviewRoute
import com.idiotfrogs.profile.editprofile.EditProfileRoute
import com.idiotfrogs.profile.profile.ProfileRoute
import com.idiotfrogs.setting.SettingRoute
import com.idiotfrogs.splash.SplashRoute
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handlePush(intent)
        handleAppLink(intent)
        mainViewModel.collectAppSideEffect()
        setContent {
            MSTheme {
                val backStack = rememberNavBackStack(Routes.Splash)
                val navigator = remember(backStack) { MSNavigatorImpl(backStack) }
                val currentRoute = backStack.lastOrNull() as? Routes
                val pendingInviteCapsuleId by mainViewModel.pendingInviteCapsuleId.collectAsState()
                val isAuthenticatedRoute =
                    currentRoute != null &&
                        currentRoute !is Routes.Splash &&
                        currentRoute !is Routes.Login &&
                        currentRoute !is Routes.SignUp

                LaunchedEffect(Unit) {
                    mainViewModel.event.collect { sideEffect ->
                        when(sideEffect) {
                            MainEvent.NavigateToLogin -> {
                                backStack.clear()
                                navigator.navigate(Routes.Login)
                            }
                            is MainEvent.ShowToast -> {
                                Toast.makeText(
                                    this@MainActivity,
                                    sideEffect.message,
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        }
                    }
                }

                LaunchedEffect(isAuthenticatedRoute, pendingInviteCapsuleId) {
                    if (isAuthenticatedRoute && pendingInviteCapsuleId != null) {
                        mainViewModel.joinPendingInvite()
                    }
                }

                if (isAuthenticatedRoute) {
                    LaunchedEffect(Unit) {
                        mainViewModel.navigationEvent.collect { event ->
                            when (event) {
                                is MainNavigationEvent.NavigateToFriend -> {
                                    navigator.navigate(Routes.Friend(event.capsuleId))
                                }
                                is MainNavigationEvent.NavigateToDetail -> {
                                    navigator.navigate(Routes.Detail(event.capsuleId))
                                    event.toastMessage?.let { message ->
                                        Toast.makeText(
                                            this@MainActivity,
                                            message,
                                            Toast.LENGTH_SHORT,
                                        ).show()
                                    }
                                }
                                is MainNavigationEvent.NavigateToHome -> {
                                    navigator.navigate(Routes.Home(event.capsuleId))
                                }
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
                            entryDecorators = listOf(
                                rememberSaveableStateHolderNavEntryDecorator(),
                                rememberViewModelStoreNavEntryDecorator()
                            ),
                            entryProvider = entryProvider {
                                entry<Routes.Splash> { SplashRoute() }
                                entry<Routes.Login> { LoginRoute() }
                                entry<Routes.SignUp> { SignUpRoute() }
                                entry<Routes.Home> { HomeRoute(openedId = it.openedId) }
                                entry<Routes.Create> { CreateRoute() }
                                entry<Routes.Profile> { ProfileRoute() }
                                entry<Routes.EditProfile> { EditProfileRoute() }
                                entry<Routes.Setting> { SettingRoute() }
                                entry<Routes.Detail> { DetailRoute(capsuleId = it.id) }
                                entry<Routes.Message> { MessageRoute(capsuleId = it.id) }
                                entry<Routes.Preview> { PreviewRoute(capsuleId = it.id) }
                                entry<Routes.Memory> { MemoryRoute(capsuleId = it.id) }
                                entry<Routes.Friend> { FriendRoute(it.id) }
                                entry<Routes.Management> {
                                    ManagementRoute(
                                        capsuleId = it.id,
                                        capsuleTitle = it.title,
                                    )
                                }
                            },
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handlePush(intent)
        handleAppLink(intent)
    }

    private fun handlePush(intent: Intent?) {
        mainViewModel.onPushReceived(
            type = intent?.getStringExtra("type"),
            capsuleId = intent?.getStringExtra("capsuleId"),
        )

        intent?.removeExtra("type")
        intent?.removeExtra("capsuleId")
    }

    private fun handleAppLink(intent: Intent?) {
        if (intent?.action != Intent.ACTION_VIEW) return

        val uri = intent.data ?: return
        mainViewModel.onAppLinkReceived(uri)
        intent.data = null
    }
}
