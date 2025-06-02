# GlobalTrip Setup Guide

## Prerequisites
- Android Studio or IntelliJ IDEA
- JDK 11 or higher
- Android SDK
- Xcode (for iOS development)

## Supabase Configuration

### 1. Create Supabase Project
1. Go to [supabase.com](https://supabase.com)
2. Create a new project
3. Get your project URL and anon key from project settings

### 2. Configure Authentication
In your Supabase dashboard:
1. Go to Authentication > Settings
2. Enable Email/Password authentication
3. Configure email templates (optional)
4. Set up OAuth providers if needed (Google, Facebook)

### 3. Set Environment Variables

#### Option A: Using local.properties (Recommended)
Add to `local.properties`:
```properties
SUPABASE_URL=https://your-project-id.supabase.co
SUPABASE_ANON_KEY=your-anon-key-here
```

#### Option B: Using gradle.properties
1. Copy `gradle.properties.template` to `gradle.properties`
2. Replace placeholder values with your actual Supabase credentials

### 4. Update SupabaseClient.kt
If you want to use BuildConfig (recommended for production):
1. Add BuildConfig fields in `build.gradle.kts`
2. Update `SupabaseClient.kt` to read from BuildConfig

## Running the App

### Android
```bash
./gradlew :composeApp:installDebug
```

### iOS
```bash
cd iosApp
xcodebuild -project iosApp.xcodeproj -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15'
```

### Web (WASM)
```bash
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```

## Testing Authentication
1. Run the app
2. Try creating a new account with email/password
3. Sign in with existing credentials
4. Test sign out functionality
5. Verify protected navigation works

## Troubleshooting
- Ensure Supabase credentials are correctly set
- Check network connectivity
- Verify Supabase project is active
- Check authentication settings in Supabase dashboard

## Security Notes
- Never commit `local.properties` or real credentials
- Use environment variables in CI/CD
- Rotate keys regularly
- Enable RLS (Row Level Security) in Supabase for production