# AI Code Generation Guidance

This document provides guidelines for using the README files and coding conventions to generate consistent, high-quality code for the GlobalTrip KMP application.

## Using the Documentation

When generating code for the GlobalTrip project, always:

1. **Consult the relevant README files first**:
   - Review the module's README to understand its purpose, features, and functional requirements
   - Check dependencies to ensure proper imports
   - Reference the implementation examples provided

2. **Follow the coding convention strictly**:
   - Use the defined architecture patterns (Clean Architecture + MVI)
   - Adhere to naming conventions and code style rules
   - Apply proper error handling and resource management

3. **Maintain architecture boundaries**:
   - Keep domain models independent of UI and data implementation details
   - Use proper dependency injection
   - Follow the unidirectional data flow pattern in MVI

## Key Architecture Points

### MVI Implementation

All UI features must follow the Model-View-Intent (MVI) pattern:

1. **States**: Immutable data classes representing UI state
2. **Intents**: Sealed interfaces defining user actions
3. **Effects**: Sealed interfaces for one-time side effects (navigation, snackbars, etc.)
4. **ViewModels**: Process intents, update state, and emit effects

Example flow:
```
User action → Intent → ViewModel → Use Case → Repository → Domain Entity → Updated State → UI
```

### Platform-Specific Code

- Place platform-specific implementations in the appropriate source sets (`androidMain`, `iosMain`, `wasmJsMain`)
- Use the expect/actual pattern for platform-specific behavior
- Keep platform-specific code to a minimum
- Never expose platform types across module boundaries

### Dependency Management

- Always use constructor injection for dependencies
- Use Koin for dependency injection
- Keep module definitions clean and organized in separate files
- Define single-responsibility modules

## Generation Checklist

When generating code, verify these elements:

- [ ] Code follows Clean Architecture principles
- [ ] UI components use the MVI pattern
- [ ] Class and function names follow conventions
- [ ] Error handling is properly implemented
- [ ] Coroutines and Flow are used correctly
- [ ] Platform-specific code is isolated
- [ ] Documentation includes KDoc comments
- [ ] Tests are provided or placeholders are included
- [ ] Proper validation is implemented
- [ ] Resource management is handled correctly

## Example Generation Process

When asked to generate a new feature:

1. **Understand requirements**: Clarify the feature's purpose and requirements
2. **Identify modules**: Determine which modules will be affected
3. **Create domain models**: Start with domain entities and use cases
4. **Implement data layer**: Create repository implementations and data sources
5. **Build MVI components**: Define states, intents, and effects
6. **Create UI components**: Build Compose UI using the design system
7. **Add platform-specific code**: Implement any platform-specific requirements
8. **Write tests**: Add unit and integration tests
9. **Document the code**: Add documentation and usage examples

## Helpful Patterns

### Implementing a New Feature

Follow this pattern for implementing new features:

1. Define domain models and repository interfaces
2. Implement repository implementation with proper data sources
3. Create use cases that encapsulate business logic
4. Define MVI components (states, intents, effects)
5. Implement ViewModel with intent processing
6. Create UI components that observe state and emit intents

### Error Handling

Use this pattern for consistent error handling:

```kotlin
fun someFunction(): Result<SuccessType> {
    return try {
        // Operation logic
        Result.success(result)
    } catch (e: SpecificException) {
        // Handle specific error
        Result.failure(e)
    } catch (e: Exception) {
        // Handle unexpected error
        Result.failure(UnexpectedError("Error message", e))
    }
}
```

### State Management

Use this pattern for state updates:

```kotlin
private val _state = MutableStateFlow(InitialState())
val state = _state.asStateFlow()

private fun updateState(update: (CurrentState) -> CurrentState) {
    _state.update(update)
}
```

## Additional Resources

- [Kotlin Official Style Guide](https://kotlinlang.org/docs/coding-conventions.html)
- [Compose Multiplatform Documentation](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Koin Documentation](https://insert-koin.io/docs/quickstart/kotlin-multiplatform)
- [Ktor Client Documentation](https://ktor.io/docs/client.html)
- [SQLDelight Documentation](https://cashapp.github.io/sqldelight/)