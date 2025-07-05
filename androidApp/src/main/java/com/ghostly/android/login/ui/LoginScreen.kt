package com.ghostly.android.login.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ghostly.android.R
import com.ghostly.android.login.LoginViewModel
import com.ghostly.login.models.LoginState
import com.ghostly.android.theme.accentRed
import com.ghostly.android.ui.components.AccentButton
import com.ghostly.android.ui.components.toast
import com.ghostly.android.utils.capitalize
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = koinViewModel(),
    onLoginSuccess: () -> Unit,
    didUserLogout: Boolean = false,
) {

    val context = LocalContext.current
    val uiState by loginViewModel.uiState.collectAsState()
    val isLoggedIn by loginViewModel.isLoggedIn.collectAsState()
    val isLoggedOut by loginViewModel.checkIfLoggedOut(didUserLogout).collectAsState(didUserLogout)

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            onLoginSuccess()
        }
    }

    LaunchedEffect(Unit) {
        if (isLoggedOut) {
            context.toast(R.string.successfully_logged_out)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(32.dp)
                .padding(top = 50.dp), verticalArrangement = Arrangement.Top
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = stringResource(R.string.welcome_to),
                style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.primary)
            )
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = stringResource(R.string.ghost),
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.height(80.dp))

            when (uiState) {
                is LoginState.CheckLogin -> CheckLogin()
                is LoginState.Start -> LoginStart(loginViewModel)
                is LoginState.InvalidDomain -> LoginStart(
                    loginViewModel, (uiState as LoginState.InvalidDomain)
                )

                is LoginState.ValidDomain -> DomainDetailsAvailable(loginViewModel)
                is LoginState.InvalidApiKey -> DomainDetailsAvailable(
                    loginViewModel, (uiState as LoginState.InvalidApiKey)
                )
            }
        }
    }
}

@Composable
fun ColumnScope.CheckLogin(
) {
    Spacer(modifier = Modifier.height(80.dp))
    CircularProgressIndicator(
        modifier = Modifier
            .size(48.dp)
            .align(Alignment.CenterHorizontally),
        color = MaterialTheme.colorScheme.tertiary
    )
}

@Composable
fun ColumnScope.LoginStart(
    loginViewModel: LoginViewModel,
    uiState: LoginState.InvalidDomain? = null,
) {
    val scope = rememberCoroutineScope()
    val domain by loginViewModel.domain.collectAsState()
    val validDomain by loginViewModel.validDomain.collectAsState()
    val showProgress = remember { mutableStateOf(false) }

    Spacer(modifier = Modifier.height(40.dp))
    if (showProgress.value) {
        Text(
            text = stringResource(R.string.getting_your_site_details),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))
        CircularProgressIndicator(
            modifier = Modifier
                .size(35.dp)
                .align(Alignment.CenterHorizontally),
            color = MaterialTheme.colorScheme.tertiary
        )
    } else {
        Text(
            text = stringResource(R.string.get_setup),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = domain,
            onValueChange = loginViewModel::onDomainChange,
            label = { Text(text = stringResource(R.string.domain_api_key)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.cd_domain_placeholder, "Ex:"),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.align(Alignment.Start)
        )
        if (uiState != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = uiState.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Start)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        AccentButton(
            onClick = {
                showProgress.value = true
                scope.launch {
                    loginViewModel.getSiteDetails()
                    showProgress.value = false
                }
            }, modifier = Modifier.fillMaxWidth(), enabled = validDomain
        ) {
            Text(
                text = stringResource(R.string.get_details),
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

@Composable
fun ColumnScope.DomainDetailsAvailable(
    loginViewModel: LoginViewModel,
    uiState: LoginState.InvalidApiKey? = null,
) {
    val showProgress = remember { mutableStateOf(false) }
    val siteDetails by loginViewModel.siteDetails.collectAsState()
    val token by loginViewModel.token.collectAsState()
    val inputValidated by loginViewModel.inputValidated.collectAsState()

    siteDetails?.let { site ->
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(site.icon).crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .heightIn(max = 80.dp),
            contentScale = ContentScale.Inside
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = site.title.capitalize(),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        TextButton(
            onClick = { loginViewModel.notYouClicked() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = stringResource(R.string.not_you),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight(600)),
                color = accentRed
            )
        }
    }
    if (showProgress.value) {
        Text(
            text = stringResource(R.string.checking_credentials),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))
        CircularProgressIndicator(
            modifier = Modifier
                .size(35.dp)
                .align(Alignment.CenterHorizontally),
            color = MaterialTheme.colorScheme.tertiary
        )
    } else {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.enter_admin_api_key),
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = token,
            onValueChange = loginViewModel::onTokenChange,
            label = { Text(stringResource(R.string.admin_key)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                showProgress.value = true
                loginViewModel.tryLogin()
            }),
        )
        if (uiState != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = uiState.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Start)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        AccentButton(
            onClick = {
                showProgress.value = true
                loginViewModel.tryLogin()
            }, modifier = Modifier.fillMaxWidth(), enabled = inputValidated
        ) {
            Text(
                text = stringResource(R.string.login).uppercase(Locale.getDefault()),
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}
