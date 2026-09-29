package com.smartcity.greenpassport

import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.LoadingContent
import com.smartcity.greenpassport.feature.auth.presentation.auth.ui.AuthScreen
import com.smartcity.greenpassport.feature.auth.presentation.profilesetup.ui.ProfileSetupScreen
import com.smartcity.greenpassport.navigation.GreenPassportAppShell
import com.smartcity.greenpassport.onboarding.OnboardingScreen

@Composable
fun GreenPassportApp(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel(),
) {
    val startupState by viewModel.startupState.collectAsStateWithLifecycle()

    Surface(modifier = modifier, color = MaterialTheme.colorScheme.background) {
        when (startupState) {
            AppStartupState.Loading -> LoadingContent()
            AppStartupState.NeedsOnboarding -> OnboardingScreen(
                onGetStarted = viewModel::markOnboardingSeen,
                modifier = Modifier.safeDrawingPadding(),
            )
            AppStartupState.NeedsAuth -> AuthScreen(modifier = Modifier.safeDrawingPadding())
            AppStartupState.NeedsProfile -> ProfileSetupScreen(
                isEditing = false,
                onFinished = {},
                modifier = Modifier.safeDrawingPadding(),
            )
            AppStartupState.Ready -> GreenPassportAppShell()
        }
    }
}
