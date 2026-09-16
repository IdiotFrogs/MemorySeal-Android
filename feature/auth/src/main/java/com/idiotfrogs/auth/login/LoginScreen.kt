package com.idiotfrogs.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.idiotfrogs.auth.login.component.LoginButton
import com.idiotfrogs.auth.login.component.LoginType
import com.idiotfrogs.auth.util.rememberLoginManager
import com.idiotfrogs.designsystem.component.MSLoadingOverlay
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.DevicePreview
import com.idiotfrogs.navigation.LocalComposeMSNavigator
import com.idiotfrogs.navigation.Routes
import com.idiotfrogs.resource.R
import com.idiotfrogs.resource.hsSantokki
import com.idiotfrogs.social_login.LoginManager
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun LoginRoute(
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val navigator = LocalComposeMSNavigator.current
    val uiState by viewModel.collectAsState()
    val loginManager = rememberLoginManager()

    viewModel.collectSideEffect { event ->
        when (event) {
            LoginSideEffect.NavigateToSignUp -> navigator.navigate(Routes.SignUp)
            LoginSideEffect.NavigateToHome -> {
                navigator.clear()
                navigator.navigate(Routes.Home)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LoginScreen(
            loginManager = loginManager,
            onAction = viewModel::onAction,
        )

        MSLoadingOverlay(visible = uiState.isLoading)
    }
}

@Composable
fun LoginScreen(
    loginManager: LoginManager?,
    onAction: (LoginAction) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MSTheme.color.white)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))
            MSText(
                text = "메실",
                fontFamily = hsSantokki,
                fontWeight = FontWeight.Normal,
                fontSize = 56.dp,
            )
            Spacer(modifier = Modifier.height(32.dp))
            MSText(
                text = "익을수록 달콤한 기억",
                fontWeight = FontWeight.Medium,
                fontSize = 16.dp,
            )
            Spacer(modifier = Modifier.height(32.dp))
            Image(
                modifier = Modifier.size(
                    width = 320.dp,
                    height = 300.dp
                ),
                painter = painterResource(R.drawable.img_login_logo),
                contentDescription = "login logo"
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LoginButton(
                loginType = LoginType.GOOGLE,
                onClick = {
                    loginManager?.let { onAction(LoginAction.SocialLoginClicked(it::googleLogin)) }
                }
            )
            LoginButton(
                loginType = LoginType.APPLE,
                onClick = {
                    loginManager?.let { onAction(LoginAction.SocialLoginClicked(it::appleLogin)) }
                }
            )
        }
    }
}


@DevicePreview
@Composable
private fun LoginScreenPreview() {
    LoginScreen(
        loginManager = null,
        onAction = {}
    )
}
