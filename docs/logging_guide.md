# Logging Guide for GlobalTrip

This project uses Napier for cross-platform logging with a centralized Logger wrapper.

## Setup

All logging is handled through the `Logger` object in `core.logging.Logger`. 

## Usage Pattern

Each class should define a `TAG` constant and use it for all logging:

```kotlin
class MyClass {
    companion object {
        private const val TAG = "MyClass"
    }
    
    fun doSomething() {
        Logger.d("Starting operation", TAG)
        
        try {
            // ... business logic
            Logger.i("Operation completed successfully", TAG)
        } catch (e: Exception) {
            Logger.e("Operation failed", TAG, e)
        }
    }
}

// For objects:
object MyObject {
    private const val TAG = "MyObject"
    
    fun initialize() {
        Logger.i("Initializing", TAG)
    }
}

// For top-level functions:
private const val TAG = "FileName"

fun topLevelFunction() {
    Logger.d("Top level function called", TAG)
}
```

## Log Levels

- `Logger.v()` - Verbose (detailed debugging)
- `Logger.d()` - Debug (general debugging)  
- `Logger.i()` - Info (important events)
- `Logger.w()` - Warning (unexpected but handled)
- `Logger.e()` - Error (errors and exceptions)

## Platform Output

- **Android**: Logs appear in Logcat with tag filtering
- **iOS**: Logs appear in Xcode console
- **WASM**: Logs appear in browser developer tools console

## Example Output

```
[SupabaseClient] Supabase config loaded - URL: https://xyz...
[AuthViewModel] Starting sign in process  
[SupabaseAuthRepository] Sign in successful
[SignInUseCase] SignInUseCase called with email: user@example.com
```

## Best Practices

1. Always use the class TAG constant
2. Keep log messages concise and informative
3. Use appropriate log levels
4. Include relevant context (user ID, operation type, etc.)
5. Use `Logger.e()` with exceptions for error tracking
6. No emojis - keep messages text-only and professional