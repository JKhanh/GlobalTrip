# Collaboration Feature Module

The Collaboration module enables trip sharing and real-time collaboration between travelers in the GlobalTrip application across all supported platforms.

## Features

- Trip sharing with fellow travelers
- Collaborative trip planning
- Role-based permissions
- Real-time activity feed
- Chat and messaging
- Task assignment and tracking
- Voting and decision making
- Change tracking and history
- Notifications for collaborative actions

## Functional Requirements

### Trip Sharing

- Share trips with other users via email, phone, or username
- Generate shareable links with expiration and access controls
- Control access levels (view-only, edit, admin)
- Support removing collaborators
- Show pending and accepted invitations
- Track shared trips and their owners

### Real-time Collaboration

- Enable simultaneous editing of trip details
- Show who is currently viewing/editing the trip
- Resolve conflicts when multiple people edit the same item
- Provide real-time updates when others make changes
- Implement offline editing with synchronization
- Support presence indicators and activity status

### Communication

- In-app messaging between trip participants
- Group chat for each trip
- Direct messaging between users
- Support for text, images, and location sharing
- Message read indicators
- Push notifications for new messages

### Activity Tracking

- Activity feed showing recent changes
- Track who made what changes and when
- Allow commenting on changes
- Highlight important updates
- Filter activities by type, user, or date

## MVI Implementation

The Collaboration module follows the Model-View-Intent (MVI) architecture pattern:

### States

- `CollaborationState`: Overall collaboration state for a trip
- `ParticipantListState`: List of trip participants and roles
- `ChatState`: Messaging and chat state
- `ActivityState`: Activity feed state

### Intents

- `CollaborationIntent`: Trip sharing and collaboration actions
- `ParticipantIntent`: Actions for managing trip participants
- `ChatIntent`: Messaging and communication actions
- `ActivityIntent`: Activity feed actions

### Effects

- Invitation emails/notifications
- Real-time updates
- Push notifications
- Permission changes

## Dependencies

### Core Dependencies

- `core:domain`: Domain models and use cases
- `core:data`: Repository implementations
- `core:network`: API communication
- `core:database`: Local storage
- `core:common`: Common utilities
- `core:security`: Secure data handling
- `core:ui`: UI components

### Feature Dependencies

- `feature:trips`: Integration with trip data
- `feature:auth`: User authentication and profiles

### External Libraries

- Real-time messaging infrastructure
- Push notification libraries
- Conflict resolution utilities

## Implementation Examples

### Participant Management Screen

```kotlin
@Composable
fun ParticipantManagementScreen(
    viewModel: ParticipantViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ParticipantEffect.ShowInviteLink -> {
                    // Show share dialog with invite link
                }
                is ParticipantEffect.ShowPermissionDenied -> {
                    // Show permission denied message
                }
                is ParticipantEffect.ShowError -> {
                    // Show error message
                }
            }
        }
    }
    
    // Initial load
    LaunchedEffect(Unit) {
        viewModel.processIntent(ParticipantIntent.LoadParticipants)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trip Participants") },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.processIntent(ParticipantIntent.ShowInviteDialog)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "Invite"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Show owner info
            state.tripOwner?.let { owner ->
                OwnerCard(
                    owner = owner,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
            
            Divider()
            
            // Participants list
            Text(
                text = "Participants (${state.participants.size})",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.participants.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No participants yet. Invite some friends!",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn {
                    items(state.participants) { participant ->
                        ParticipantItem(
                            participant = participant,
                            isCurrentUser = participant.id == state.currentUserId,
                            canModify = state.canModifyParticipants,
                            onRoleChange = { newRole ->
                                viewModel.processIntent(
                                    ParticipantIntent.ChangeRole(
                                        participantId = participant.id,
                                        newRole = newRole
                                    )
                                )
                            },
                            onRemove = {
                                viewModel.processIntent(
                                    ParticipantIntent.RemoveParticipant(participant.id)
                                )
                            }
                        )
                        Divider()
                    }
                }
            }
            
            // Pending invitations
            if (state.pendingInvitations.isNotEmpty()) {
                Text(
                    text = "Pending Invitations (${state.pendingInvitations.size})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                
                LazyColumn {
                    items(state.pendingInvitations) { invitation ->
                        PendingInvitationItem(
                            invitation = invitation,
                            onCancel = {
                                viewModel.processIntent(
                                    ParticipantIntent.CancelInvitation(invitation.id)
                                )
                            },
                            onResend = {
                                viewModel.processIntent(
                                    ParticipantIntent.ResendInvitation(invitation.id)
                                )
                            }
                        )
                        Divider()
                    }
                }
            }
        }
    }
    
    // Invite dialog
    if (state.showInviteDialog) {
        InviteDialog(
            onDismiss = {
                viewModel.processIntent(ParticipantIntent.HideInviteDialog)
            },
            onInviteByEmail = { email, role ->
                viewModel.processIntent(
                    ParticipantIntent.InviteByEmail(email, role)
                )
            },
            onInviteByUsername = { username, role ->
                viewModel.processIntent(
                    ParticipantIntent.InviteByUsername(username, role)
                )
            },
            onGenerateLink = { role, expiration ->
                viewModel.processIntent(
                    ParticipantIntent.GenerateInviteLink(role, expiration)
                )
            }
        )
    }
}
```

### Participant ViewModel

```kotlin
class ParticipantViewModel(
    private val tripId: String,
    private val getTripParticipantsUseCase: GetTripParticipantsUseCase,
    private val inviteParticipantUseCase: InviteParticipantUseCase,
    private val changeRoleUseCase: ChangeParticipantRoleUseCase,
    private val removeParticipantUseCase: RemoveParticipantUseCase,
    private val cancelInvitationUseCase: CancelInvitationUseCase,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ParticipantState())
    val state = _state.asStateFlow()
    
    private val _effect = Channel<ParticipantEffect>()
    val effect = _effect.receiveAsFlow()
    
    init {
        viewModelScope.launch {
            // Get current user ID
            val currentUser = userRepository.getCurrentUser()
            _state.update { it.copy(currentUserId = currentUser.id) }
        }
    }
    
    fun processIntent(intent: ParticipantIntent) {
        when (intent) {
            is ParticipantIntent.LoadParticipants -> loadParticipants()
            is ParticipantIntent.ShowInviteDialog -> showInviteDialog()
            is ParticipantIntent.HideInviteDialog -> hideInviteDialog()
            is ParticipantIntent.InviteByEmail -> inviteByEmail(intent.email, intent.role)
            is ParticipantIntent.InviteByUsername -> inviteByUsername(intent.username, intent.role)
            is ParticipantIntent.GenerateInviteLink -> generateInviteLink(intent.role, intent.expiration)
            is ParticipantIntent.ChangeRole -> changeRole(intent.participantId, intent.newRole)
            is ParticipantIntent.RemoveParticipant -> removeParticipant(intent.participantId)
            is ParticipantIntent.CancelInvitation -> cancelInvitation(intent.invitationId)
            is ParticipantIntent.ResendInvitation -> resendInvitation(intent.invitationId)
        }
    }
    
    private fun loadParticipants() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            getTripParticipantsUseCase(tripId).onSuccess { result ->
                _state.update { it.copy(
                    tripOwner = result.owner,
                    participants = result.participants,
                    pendingInvitations = result.pendingInvitations,
                    canModifyParticipants = result.canModifyParticipants,
                    isLoading = false
                )}
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false) }
                _effect.send(ParticipantEffect.ShowError(
                    error.message ?: "Failed to load participants"
                ))
            }
        }
    }
    
    private fun inviteByEmail(email: String, role: ParticipantRole) {
        viewModelScope.launch {
            _state.update { it.copy(isInviting = true) }
            
            inviteParticipantUseCase(
                tripId = tripId,
                email = email,
                role = role
            ).onSuccess {
                _state.update { it.copy(
                    isInviting = false,
                    showInviteDialog = false
                )}
                loadParticipants() // Refresh the list
            }.onFailure { error ->
                _state.update { it.copy(isInviting = false) }
                _effect.send(ParticipantEffect.ShowError(
                    error.message ?: "Failed to send invitation"
                ))
            }
        }
    }
    
    private fun generateInviteLink(role: ParticipantRole, expiration: Duration) {
        viewModelScope.launch {
            _state.update { it.copy(isGeneratingLink = true) }
            
            inviteParticipantUseCase.generateLink(
                tripId = tripId,
                role = role,
                expiration = expiration
            ).onSuccess { link ->
                _state.update { it.copy(isGeneratingLink = false) }
                _effect.send(ParticipantEffect.ShowInviteLink(link))
            }.onFailure { error ->
                _state.update { it.copy(isGeneratingLink = false) }
                _effect.send(ParticipantEffect.ShowError(
                    error.message ?: "Failed to generate invitation link"
                ))
            }
        }
    }
    
    private fun changeRole(participantId: String, newRole: ParticipantRole) {
        viewModelScope.launch {
            if (!_state.value.canModifyParticipants) {
                _effect.send(ParticipantEffect.ShowPermissionDenied(
                    "You don't have permission to change roles"
                ))
                return@launch
            }
            
            changeRoleUseCase(
                tripId = tripId,
                participantId = participantId,
                newRole = newRole
            ).onSuccess {
                loadParticipants() // Refresh the list
            }.onFailure { error ->
                _effect.send(ParticipantEffect.ShowError(
                    error.message ?: "Failed to change role"
                ))
            }
        }
    }
}

data class ParticipantState(
    val tripOwner: User? = null,
    val participants: List<Participant> = emptyList(),
    val pendingInvitations: List<PendingInvitation> = emptyList(),
    val currentUserId: String = "",
    val canModifyParticipants: Boolean = false,
    val isLoading: Boolean = false,
    val showInviteDialog: Boolean = false,
    val isInviting: Boolean = false,
    val isGeneratingLink: Boolean = false
)

sealed interface ParticipantIntent {
    object LoadParticipants : ParticipantIntent
    object ShowInviteDialog : ParticipantIntent
    object HideInviteDialog : ParticipantIntent
    data class InviteByEmail(val email: String, val role: ParticipantRole) : ParticipantIntent
    data class InviteByUsername(val username: String, val role: ParticipantRole) : ParticipantIntent
    data class GenerateInviteLink(val role: ParticipantRole, val expiration: Duration) : ParticipantIntent
    data class ChangeRole(val participantId: String, val newRole: ParticipantRole) : ParticipantIntent
    data class RemoveParticipant(val participantId: String) : ParticipantIntent
    data class CancelInvitation(val invitationId: String) : ParticipantIntent
    data class ResendInvitation(val invitationId: String) : ParticipantIntent
}

sealed interface ParticipantEffect {
    data class ShowInviteLink(val link: String) : ParticipantEffect
    data class ShowPermissionDenied(val message: String) : ParticipantEffect
    data class ShowError(val message: String) : ParticipantEffect
}

enum class ParticipantRole {
    VIEWER, EDITOR, ADMIN
}

data class Participant(
    val id: String,
    val name: String,
    val email: String,
    val profilePicture: String?,
    val role: ParticipantRole,
    val joinedAt: LocalDateTime
)

data class PendingInvitation(
    val id: String,
    val email: String,
    val role: ParticipantRole,
    val sentAt: LocalDateTime,
    val expiresAt: LocalDateTime?
)
```