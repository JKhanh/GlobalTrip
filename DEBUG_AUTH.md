# Authentication Debug Guide

## Debug Logging Added

I've added comprehensive debug logging throughout the authentication flow to help identify issues. Here's what to look for:

### Debug Log Prefixes
- 🔧 **Supabase Config**: Configuration loading and client creation
- 🔐 **Auth Repository**: Supabase API calls and responses  
- 🔑 **Use Cases**: Business logic layer
- 📱 **ViewModel**: UI state management
- 🧭 **Navigation**: Route changes and auth state navigation
- 🏗️ **DI**: Dependency injection container

### Testing Steps

1. **Run the Android app**:
   ```bash
   ./gradlew :composeApp:installDebug
   ```

2. **Monitor logs** using logcat:
   ```bash
   adb logcat | grep -E "(🔧|🔐|🔑|📱|🧭|🏗️)"
   ```

3. **Test Sign Up**:
   - Try creating a new account with email/password
   - Look for logs starting with these prefixes in order:
     - 🔧 → 🏗️ → 📱 → 🔑 → 🔐 → 🧭

4. **Test Sign In**:
   - Try signing in with existing credentials
   - Follow the same log flow

### Expected Log Flow

**Successful Sign Up** (with email confirmation):
```
🔧 DEBUG: Supabase config loaded - URL: https://...
🔧 DEBUG: Supabase client created successfully
📱 DEBUG: Starting sign up process in ViewModel
🔑 DEBUG: SignUpUseCase called with email: test@example.com
🔑 DEBUG: SignUpUseCase - delegating to repository
🔐 DEBUG: Starting sign up for email: test@example.com
🔐 DEBUG: Supabase signUp call completed
🔐 DEBUG: Current user: null, session exists: false
🔐 DEBUG: Sign up successful - user created, email confirmation required
📱 DEBUG: Sign up use case failed: EmailNotVerified
[UI shows: "Account created! Please check your email..."]
```

**Successful Sign In**:
```
📱 DEBUG: Starting sign in process in ViewModel
🔑 DEBUG: SignInUseCase called with email: test@example.com
🔑 DEBUG: SignInUseCase - delegating to repository
🔐 DEBUG: Starting sign in for email: test@example.com
🔐 DEBUG: Sign in successful
📱 DEBUG: Sign in use case successful
🧭 DEBUG: Auth successful - navigating to main app
```

**Failed Authentication**:
```
🔐 DEBUG: Sign in exception: [error message]
🔐 DEBUG: Exception type: [exception class]
🔐 DEBUG: Mapping exception to AuthError
🔐 DEBUG: Exception message: [detailed error]
🔐 DEBUG: Mapped to [error type]
📱 DEBUG: Sign in use case failed: [error]
```

### Common Issues to Check For

1. **Config Loading Issues**:
   - Missing 🔧 logs = BuildConfig not loading credentials
   - Check `local.properties` has correct values
   - Look for "URL: https://..." and "Key length: [number]"

2. **Network Issues**:
   - Look for connection/network/timeout error messages
   - Verify Supabase project is active and not paused
   - Check internet connectivity

3. **Credential Issues**:
   - "Invalid login credentials" = wrong email/password
   - "User already registered" = email exists for sign up
   - "Invalid API key" or "Unauthorized" = wrong Supabase credentials

4. **Client Creation Issues**:
   - Missing "Supabase client created successfully" 
   - Check URL/key format in config

5. **Email Confirmation Issues**:
   - "Email not confirmed" = Supabase requires email verification
   - Check Supabase Auth settings to disable email confirmation for testing

6. **Supabase Project Issues**:
   - "Project paused" = Supabase project is suspended
   - "Rate limit" = Too many requests

### Manual Testing

Try these test cases and note which logs appear:

1. **Valid Sign Up**: `test+debug@example.com` / `password123`
2. **Invalid Email**: `invalid-email` / `password123`  
3. **Weak Password**: `test@example.com` / `123`
4. **Existing Email**: Try signing up with same email twice
5. **Valid Sign In**: Use credentials from successful sign up
6. **Invalid Sign In**: Wrong email or password

### What to Report

When reporting issues, please include:
1. The complete debug log output
2. Which test case failed
3. Expected vs actual behavior
4. Any error messages shown in the UI

This will help pinpoint exactly where the authentication flow is breaking.