# Network Module

The Network module handles all API communication for the GlobalTrip application across all supported platforms using Ktor client.

## Features

- Cross-platform HTTP client configuration
- API request/response handling
- Authentication and authorization
- Error handling and retry strategies
- Caching strategies
- Rate limiting
- Request interceptors
- API versioning support
- Serialization/deserialization
- Mock API for testing

## Functional Requirements

### HTTP Client

- Configure platform-specific HTTP engines
- Implement connection timeouts and retry policies
- Support proper logging and monitoring
- Configure content negotiation
- Handle platform-specific networking behaviors
- Support TLS/SSL and certificate pinning

### Authentication

- Implement OAuth2 authentication
- Handle token storage and refresh
- Support different auth methods (bearer, basic, etc.)
- Implement proper logout and token invalidation
- Provide session management
- Handle authentication errors

### API Integration

- Define API endpoints and data models
- Implement API versioning
- Map API responses to domain models
- Provide proper error mapping
- Support content types (JSON, multipart, etc.)
- Implement request/response compression

### Error Handling

- Map HTTP errors to domain exceptions
- Implement retry strategies for transient errors
- Handle network connectivity issues
- Support offline detection
- Log errors for diagnostics
- Implement circuit breaker pattern

## Dependencies

### Ktor

- `io.ktor:ktor-client-core`: Core Ktor client
- `io.ktor:ktor-client-auth`: Authentication support
- `io.ktor:ktor-client-content-negotiation`: Content negotiation
- `io.ktor:ktor-serialization-kotlinx-json`: JSON serialization
- `io.ktor:ktor-client-logging`: Logging support
- Platform-specific Ktor engines:
  - `io.ktor:ktor-client-android`: Android engine
  - `io.ktor:ktor-client-darwin`: iOS engine
  - `io.ktor:ktor-client-js`: JavaScript engine

### Core Modules

- `core:common`: Common utilities
- `core:security`: Secure token storage

### Testing

- `io.ktor:ktor-client-mock`: Mock client for testing
- `kotlin-test`: Kotlin testing library

## Implementation Examples

### HTTP Client Configuration

```kotlin
class NetworkClient(
    private val engine: HttpClientEngine,
    private val authTokenProvider: AuthTokenProvider,
    private val logger: NetworkLogger
) {
    val client = HttpClient(engine) {
        // Configure timeout
        install(HttpTimeout) {
            requestTimeoutMillis = 30000
            connectTimeoutMillis = 15000
            socketTimeoutMillis = 60000
        }
        
        // Configure content negotiation
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = false
            })
        }
        
        // Authentication
        install(Auth) {
            bearer {
                loadTokens {
                    BearerTokens(
                        accessToken = authTokenProvider.getAccessToken() ?: "",
                        refreshToken = authTokenProvider.getRefreshToken() ?: ""
                    )
                }
                refreshTokens {
                    // Refresh logic here
                    val refreshToken = authTokenProvider.getRefreshToken() ?: ""
                    val tokenInfo = authApi.refreshToken(refreshToken)
                    
                    authTokenProvider.saveTokens(
                        accessToken = tokenInfo.accessToken,
                        refreshToken = tokenInfo.refreshToken
                    )
                    
                    BearerTokens(
                        accessToken = tokenInfo.accessToken,
                        refreshToken = tokenInfo.refreshToken
                    )
                }
                sendWithoutRequest { request ->
                    // Only send token for API requests, not for external services
                    request.url.host == "api.globaltrip.com"
                }
            }
        }
        
        // Logging
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    this@NetworkClient.logger.log(message)
                }
            }
            level = when {
                logger.isDebugEnabled -> LogLevel.ALL
                else -> LogLevel.INFO
            }
        }
        
        // Default request headers
        install(DefaultRequest) {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            header(HttpHeaders.AcceptCharset, Charsets.UTF_8)
            header(HttpHeaders.Accept, ContentType.Application.Json)
            header("X-App-Version", BuildConfig.VERSION_NAME)
            header("X-Platform", getPlatformName())
        }
        
        // Retry logic
        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = 3)
            retryOnException(maxRetries = 3)
            exponentialDelay()
        }
        
        // HTTP response validation
        expectSuccess = true
    }
    
    private fun getPlatformName(): String {
        return when (Platform.current) {
            Platform.ANDROID -> "android"
            Platform.IOS -> "ios"
            else -> "web"
        }
    }
}
```

### API Service

```kotlin
interface TripApiService {
    suspend fun getTrips(): List<TripDto>
    suspend fun getTripById(id: String): TripDto
    suspend fun createTrip(trip: CreateTripRequest): TripDto
    suspend fun updateTrip(id: String, trip: UpdateTripRequest): TripDto
    suspend fun deleteTrip(id: String)
    suspend fun getSharedTrips(): List<TripDto>
    suspend fun shareTrip(id: String, request: ShareTripRequest)
}

class TripApiServiceImpl(
    private val client: HttpClient,
    private val baseUrl: String
) : TripApiService {
    override suspend fun getTrips(): List<TripDto> {
        return client.get("$baseUrl/trips").body()
    }
    
    override suspend fun getTripById(id: String): TripDto {
        return client.get("$baseUrl/trips/$id").body()
    }
    
    override suspend fun createTrip(trip: CreateTripRequest): TripDto {
        return client.post("$baseUrl/trips") {
            contentType(ContentType.Application.Json)
            setBody(trip)
        }.body()
    }
    
    override suspend fun updateTrip(id: String, trip: UpdateTripRequest): TripDto {
        return client.put("$baseUrl/trips/$id") {
            contentType(ContentType.Application.Json)
            setBody(trip)
        }.body()
    }
    
    override suspend fun deleteTrip(id: String) {
        client.delete("$baseUrl/trips/$id")
    }
    
    override suspend fun getSharedTrips(): List<TripDto> {
        return client.get("$baseUrl/trips/shared").body()
    }
    
    override suspend fun shareTrip(id: String, request: ShareTripRequest) {
        client.post("$baseUrl/trips/$id/share") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }
}
```

### Error Handling

```kotlin
class NetworkExceptionMapper {
    suspend fun <T> execute(block: suspend () -> T): Result<T> {
        return try {
            Result.success(block())
        } catch (e: ClientRequestException) {
            // 4xx errors
            val statusCode = e.response.status.value
            val errorBody = e.response.bodyAsText()
            
            val error = when (statusCode) {
                401 -> AuthenticationException("Authentication failed")
                403 -> PermissionDeniedException("Permission denied")
                404 -> NotFoundException("Resource not found")
                else -> try {
                    // Try to parse error response
                    val errorResponse = Json.decodeFromString<ErrorResponse>(errorBody)
                    ApiException(errorResponse.message, statusCode)
                } catch (e: Exception) {
                    ApiException("Request failed with status $statusCode", statusCode)
                }
            }
            
            Result.failure(error)
        } catch (e: ServerResponseException) {
            // 5xx errors
            Result.failure(
                ServerException("Server error: ${e.response.status.description}", e)
            )
        } catch (e: ConnectTimeoutException) {
            Result.failure(
                NetworkTimeoutException("Connection timed out", e)
            )
        } catch (e: SocketTimeoutException) {
            Result.failure(
                NetworkTimeoutException("Socket timed out", e)
            )
        } catch (e: IOException) {
            Result.failure(
                NetworkConnectivityException("Network error", e)
            )
        } catch (e: Exception) {
            Result.failure(
                UnexpectedNetworkException("Unexpected error", e)
            )
        }
    }
}

// Data models for API
@Serializable
data class TripDto(
    val id: String,
    val name: String,
    val description: String? = null,
    val startDate: String,
    val endDate: String,
    val destinations: List<DestinationDto> = emptyList(),
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String
)

@Serializable
data class CreateTripRequest(
    val name: String,
    val description: String? = null,
    val startDate: String,
    val endDate: String,
    val destinations: List<DestinationDto> = emptyList()
)

@Serializable
data class UpdateTripRequest(
    val name: String? = null,
    val description: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val destinations: List<DestinationDto>? = null
)

@Serializable
data class ShareTripRequest(
    val email: String,
    val role: String
)

@Serializable
data class ErrorResponse(
    val code: String,
    val message: String
)
```