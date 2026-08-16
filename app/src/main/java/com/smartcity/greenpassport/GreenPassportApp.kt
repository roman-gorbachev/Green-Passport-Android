package com.smartcity.greenpassport

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.LoadingContent
import com.smartcity.greenpassport.feature.auth.presentation.AuthScreen
import com.smartcity.greenpassport.navigation.AppNavHost
import com.smartcity.greenpassport.onboarding.OnboardingScreen

@Composable
fun GreenPassportApp(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel(),
) {
    val startupState by viewModel.startupState.collectAsStateWithLifecycle()

    when (startupState) {
        AppStartupState.Loading -> LoadingContent(modifier = modifier)
        AppStartupState.NeedsOnboarding -> OnboardingScreen(
            onGetStarted = viewModel::markOnboardingSeen,
            modifier = modifier,
        )
        AppStartupState.NeedsAuth -> AuthScreen(onSignedIn = {}, modifier = modifier)
        AppStartupState.Ready -> AppNavHost(modifier = modifier)
    }
}
