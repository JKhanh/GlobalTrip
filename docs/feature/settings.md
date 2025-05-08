# Settings Feature Module

The Settings module provides user preferences, app configuration, and account management capabilities for the GlobalTrip application across all supported platforms.

## Features

- User profile management
- Appearance customization
- Notification preferences
- Privacy and security settings
- Language and localization
- Offline mode configuration
- Unit conversion preferences
- Data management and backup
- Account information and deletion
- App information and help

## Functional Requirements

### Profile Management

- Edit user profile information
- Change profile picture
- Manage linked social accounts
- Update email and password
- Set travel preferences
- Manage profile visibility

### App Preferences

- Toggle dark/light theme
- Configure notification settings
- Set default language
- Define preferred units (distance, currency, etc.)
- Adjust map preferences
- Configure synchronization settings
- Manage offline mode behavior

### Privacy and Security

- Control data sharing settings
- Manage app permissions
- Set privacy preferences
- Enable/disable biometric authentication
- Configure auto-logout options
- Manage trusted devices

### Data Management

- View storage usage
- Backup and restore trip data
- Export/import trips in various formats
- Clear app cache
- Delete account and data
- Configure data synchronization frequency

## MVI Implementation

The Settings module follows the Model-View-Intent (MVI) architecture pattern:

### States

- `SettingsState`: Overall application settings state
- `ProfileState`: User profile information
- `NotificationSettingsState`: Notification preferences
- `PrivacySettingsState`: Privacy and security settings
- `DataManagementState`: Storage and data management state

### Intents

- `SettingsIntent`: General settings actions
- `ProfileIntent`: Profile management actions
- `NotificationIntent`: Notification preference changes
- `PrivacyIntent`: Privacy and security setting changes
- `DataManagementIntent`: Data and storage actions

### Effects

- Theme changes
- Language changes
- Profile updates
- Permission requests
- App restarts

## Dependencies

### Core Dependencies

- `core:domain`: Domain models and use cases
- `core:data`: Repository implementations
- `core:preferences`: Preference storage
- `core:security`: Security utilities
- `core:common`: Common utilities
- `core:ui`: UI components

### Feature Dependencies

- `feature:auth`: Authentication integration

## Implementation Examples

### Settings Screen

```kotlin
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    onNavigateToProfile: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToDataManagement: () -> Unit,
    onNavigateToHelp: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SettingsEffect.ThemeChanged -> {
                    // Apply theme change
                }
                is SettingsEffect.LanguageChanged -> {
                    // Apply language change
                }
                is SettingsEffect.RestartRequired -> {
                    // Show restart required dialog
                }
                is SettingsEffect.ShowError -> {
                    // Show error message
                }
            }
        }
    }
    
    // Load settings
    LaunchedEffect(Unit) {
        viewModel.processIntent(SettingsIntent.LoadSettings)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                // User profile section
                item {
                    SettingsSectionHeader(title = "Profile")
                    
                    SettingsItem(
                        title = "Edit Profile",
                        subtitle = "Change your profile information",
                        icon = Icons.Default.Person,
                        onClick = onNavigateToProfile
                    )
                    
                    Divider()
                }
                
                // Appearance section
                item {
                    SettingsSectionHeader(title = "Appearance")
                    
                    SettingsToggleItem(
                        title = "Dark Theme",
                        subtitle = "Use dark theme throughout the app",
                        icon = Icons.Default.DarkMode,
                        checked = state.useDarkTheme,
                        onCheckedChange = { useDarkTheme ->
                            viewModel.processIntent(
                                SettingsIntent.SetDarkTheme(useDarkTheme)
                            )
                        }
                    )
                    
                    SettingsDropdownItem(
                        title = "Language",
                        subtitle = "Set application language",
                        icon = Icons.Default.Language,
                        selectedOption = state.selectedLanguage,
                        options = state.availableLanguages,
                        onOptionSelected = { language ->
                            viewModel.processIntent(
                                SettingsIntent.SetLanguage(language)
                            )
                        }
                    )
                    
                    Divider()
                }
                
                // Units section
                item {
                    SettingsSectionHeader(title = "Measurements")
                    
                    SettingsRadioItem(
                        title = "Distance Units",
                        icon = Icons.Default.Straighten,
                        selectedOption = state.distanceUnit,
                        options = DistanceUnit.values().toList(),
                        optionNames = DistanceUnit.values().map { it.displayName },
                        onOptionSelected = { unit ->
                            viewModel.processIntent(
                                SettingsIntent.SetDistanceUnit(unit)
                            )
                        }
                    )
                    
                    SettingsDropdownItem(
                        title = "Currency",
                        subtitle = "Default currency for expenses",
                        icon = Icons.Default.AttachMoney,
                        selectedOption = state.defaultCurrency,
                        options = state.availableCurrencies,
                        onOptionSelected = { currency ->
                            viewModel.processIntent(
                                SettingsIntent.SetDefaultCurrency(currency)
                            )
                        }
                    )
                    
                    Divider()
                }
                
                // Notifications
                item {
                    SettingsSectionHeader(title = "Notifications")
                    
                    SettingsItem(
                        title = "Notification Preferences",
                        subtitle = "Configure notification settings",
                        icon = Icons.Default.Notifications,
                        onClick = onNavigateToNotifications
                    )
                    
                    Divider()
                }
                
                // Privacy and Security
                item {
                    SettingsSectionHeader(title = "Privacy & Security")
                    
                    SettingsItem(
                        title = "Privacy Settings",
                        subtitle = "Manage your privacy preferences",
                        icon = Icons.Default.Lock,
                        onClick = onNavigateToPrivacy
                    )
                    
                    SettingsToggleItem(
                        title = "Biometric Authentication",
                        subtitle = "Use fingerprint or face ID to secure the app",
                        icon = Icons.Default.Fingerprint,
                        checked = state.useBiometricAuth,
                        onCheckedChange = { useBiometric ->
                            viewModel.processIntent(
                                SettingsIntent.SetBiometricAuth(useBiometric)
                            )
                        },
                        enabled = state.isBiometricAvailable
                    )
                    
                    Divider()
                }
                
                // Data management
                item {
                    SettingsSectionHeader(title = "Data & Storage")
                    
                    SettingsItem(
                        title = "Data Management",
                        subtitle = "Backup, restore, and clear data",
                        icon = Icons.Default.Storage,
                        onClick = onNavigateToDataManagement
                    )
                    
                    SettingsToggleItem(
                        title = "Offline Mode",
                        subtitle = "Use app without internet connection",
                        icon = Icons.Default.SignalWifiOff,
                        checked = state.offlineModeEnabled,
                        onCheckedChange = { enabled ->
                            viewModel.processIntent(
                                SettingsIntent.SetOfflineMode(enabled)
                            )
                        }
                    )
                    
                    Divider()
                }
                
                // About and Help
                item {
                    SettingsSectionHeader(title = "About")
                    
                    SettingsItem(
                        title = "Help & Support",
                        subtitle = "Get help using the app",
                        icon = Icons.Default.Help,
                        onClick = onNavigateToHelp
                    )
                    
                    SettingsItem(
                        title = "About GlobalTrip",
                        subtitle = "Version ${state.appVersion}",
                        icon = Icons.Default.Info,
                        onClick = {
                            viewModel.processIntent(SettingsIntent.ShowAbout)
                        }
                    )
                    
                    Divider()
                }
                
                // Logout and account actions
                item {
                    SettingsActionItem(
                        title = "Logout",
                        icon = Icons.Default.Logout,
                        onClick = {
                            viewModel.processIntent(SettingsIntent.Logout)
                        }
                    )
                    
                    SettingsActionItem(
                        title = "Delete Account",
                        icon = Icons.Default.Delete,
                        onClick = {
                            viewModel.processIntent(SettingsIntent.ShowDeleteAccountConfirmation)
                        },
                        textColor = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
    
    // Show delete account confirmation dialog
    if (state.showDeleteAccountDialog) {
        DeleteAccountDialog(
            onConfirm = {
                viewModel.processIntent(SettingsIntent.DeleteAccount)
            },
            onDismiss = {
                viewModel.processIntent(SettingsIntent.HideDeleteAccountConfirmation)
            }
        )
    }
    
    // Show about dialog
    if (state.showAboutDialog) {
        AboutDialog(
            appVersion = state.appVersion,
            onDismiss = {
                viewModel.processIntent(SettingsIntent.HideAbout)
            }
        )
    }
}
```

### Settings ViewModel

```kotlin
class SettingsViewModel(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val biometricManager: BiometricManager
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()
    
    private val _effect = Channel<SettingsEffect>()
    val effect = _effect.receiveAsFlow()
    
    fun processIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.LoadSettings -> loadSettings()
            is SettingsIntent.SetDarkTheme -> setDarkTheme(intent.enabled)
            is SettingsIntent.SetLanguage -> setLanguage(intent.language)
            is SettingsIntent.SetDistanceUnit -> setDistanceUnit(intent.unit)
            is SettingsIntent.SetDefaultCurrency -> setDefaultCurrency(intent.currency)
            is SettingsIntent.SetBiometricAuth -> setBiometricAuth(intent.enabled)
            is SettingsIntent.SetOfflineMode -> setOfflineMode(intent.enabled)
            is SettingsIntent.ShowAbout -> showAbout()
            is SettingsIntent.HideAbout -> hideAbout()
            is SettingsIntent.Logout -> logout()
            is SettingsIntent.ShowDeleteAccountConfirmation -> showDeleteAccountDialog()
            is SettingsIntent.HideDeleteAccountConfirmation -> hideDeleteAccountDialog()
            is SettingsIntent.DeleteAccount -> deleteAccount()
        }
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            getSettingsUseCase().onSuccess { settings ->
                _state.update { it.copy(
                    useDarkTheme = settings.useDarkTheme,
                    selectedLanguage = settings.language,
                    distanceUnit = settings.distanceUnit,
                    defaultCurrency = settings.defaultCurrency,
                    availableCurrencies = settings.availableCurrencies,
                    useBiometricAuth = settings.useBiometricAuth,
                    isBiometricAvailable = biometricManager.canAuthenticate(),
                    offlineModeEnabled = settings.offlineModeEnabled,
                    appVersion = settings.appVersion,
                    isLoading = false
                )}
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false) }
                _effect.send(SettingsEffect.ShowError(
                    error.message ?: "Failed to load settings"
                ))
            }
        }
    }
    
    private fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            _state.update { it.copy(useDarkTheme = enabled) }
            
            updateSettingsUseCase(
                key = "dark_theme",
                value = enabled
            ).onSuccess {
                _effect.send(SettingsEffect.ThemeChanged(enabled))
            }.onFailure { error ->
                // Revert state on failure
                _state.update { it.copy(useDarkTheme = !enabled) }
                _effect.send(SettingsEffect.ShowError(
                    error.message ?: "Failed to update theme setting"
                ))
            }
        }
    }
    
    private fun setLanguage(language: String) {
        viewModelScope.launch {
            val previousLanguage = _state.value.selectedLanguage
            _state.update { it.copy(selectedLanguage = language) }
            
            updateSettingsUseCase(
                key = "language",
                value = language
            ).onSuccess {
                _effect.send(SettingsEffect.LanguageChanged(language))
                
                // Show restart dialog if needed
                if (language != previousLanguage) {
                    _effect.send(SettingsEffect.RestartRequired)
                }
            }.onFailure { error ->
                // Revert state on failure
                _state.update { it.copy(selectedLanguage = previousLanguage) }
                _effect.send(SettingsEffect.ShowError(
                    error.message ?: "Failed to update language setting"
                ))
            }
        }
    }
    
    private fun deleteAccount() {
        viewModelScope.launch {
            _state.update { it.copy(
                isLoading = true,
                showDeleteAccountDialog = false
            )}
            
            deleteAccountUseCase().onSuccess {
                // Logout will be handled by the auth system
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false) }
                _effect.send(SettingsEffect.ShowError(
                    error.message ?: "Failed to delete account"
                ))
            }
        }
    }
}

data class SettingsState(
    val isLoading: Boolean = false,
    val useDarkTheme: Boolean = false,
    val selectedLanguage: String = "en",
    val availableLanguages: List<String> = listOf("en", "es", "fr", "de", "zh", "ja"),
    val distanceUnit: DistanceUnit = DistanceUnit.KILOMETERS,
    val defaultCurrency: String = "USD",
    val availableCurrencies: List<String> = listOf("USD", "EUR", "GBP", "JPY"),
    val useBiometricAuth: Boolean = false,
    val isBiometricAvailable: Boolean = false,
    val offlineModeEnabled: Boolean = false,
    val appVersion: String = "1.0.0",
    val showDeleteAccountDialog: Boolean = false,
    val showAboutDialog: Boolean = false
)

sealed interface SettingsIntent {
    object LoadSettings : SettingsIntent
    data class SetDarkTheme(val enabled: Boolean) : SettingsIntent
    data class SetLanguage(val language: String) : SettingsIntent
    data class SetDistanceUnit(val unit: DistanceUnit) : SettingsIntent
    data class SetDefaultCurrency(val currency: String) : SettingsIntent
    data class SetBiometricAuth(val enabled: Boolean) : SettingsIntent
    data class SetOfflineMode(val enabled: Boolean) : SettingsIntent
    object ShowAbout : SettingsIntent
    object HideAbout : SettingsIntent
    object Logout : SettingsIntent
    object ShowDeleteAccountConfirmation : SettingsIntent
    object HideDeleteAccountConfirmation : SettingsIntent
    object DeleteAccount : SettingsIntent
}

sealed interface SettingsEffect {
    data class ThemeChanged(val darkModeEnabled: Boolean) : SettingsEffect
    data class LanguageChanged(val language: String) : SettingsEffect
    object RestartRequired : SettingsEffect
    data class ShowError(val message: String) : SettingsEffect
}

enum class DistanceUnit(val displayName: String) {
    KILOMETERS("Kilometers"),
    MILES("Miles")
}
```