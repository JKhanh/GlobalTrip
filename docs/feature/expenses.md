# Expenses Feature Module

The Expenses module provides comprehensive travel expense tracking, budgeting, and splitting capabilities for the GlobalTrip application across all supported platforms.

## Features

- Expense tracking and categorization
- Multi-currency support with real-time conversion
- Expense splitting between trip participants
- Budget planning and monitoring
- Receipt scanning and management
- Expense reports and exports
- Payment tracking between group members
- Statistical analysis and visualization
- Offline entry with synchronization

## Functional Requirements

### Expense Tracking

- Add, edit, and delete expenses
- Categorize expenses (food, accommodation, transportation, etc.)
- Track payment methods
- Assign expenses to specific trips and days
- Support multiple currencies with exchange rates
- Include notes and photos/receipts with expenses
- Track recurring expenses

### Budget Management

- Set overall trip budget and category-specific budgets
- Track spending against budget
- Provide spending alerts and notifications
- Show remaining budget by category
- Support budget adjustments during trips
- Visualize budget usage with charts

### Expense Splitting

- Split expenses equally or by custom ratios
- Track who paid for what and who owes whom
- Calculate balances between trip participants
- Support settlement tracking
- Generate payment summaries
- Support different splitting methods (equal, percentage, custom)

### Reporting and Analysis

- Generate expense reports by trip, category, or time period
- Export data in multiple formats (PDF, CSV)
- Visualize spending patterns
- Compare expenses across trips
- Analyze category distributions
- Provide spending insights and recommendations

## MVI Implementation

The Expenses module follows the Model-View-Intent (MVI) architecture pattern:

### States

- `ExpenseListState`: List of expenses with filters and summaries
- `ExpenseDetailState`: Detailed information for a specific expense
- `ExpenseFormState`: Form state for adding/editing expenses
- `BudgetState`: Budget tracking and visualization
- `SplitState`: Expense splitting and balances

### Intents

- `ExpenseListIntent`: Actions for the expense list (load, filter, sort)
- `ExpenseDetailIntent`: Actions for viewing and modifying expense details
- `ExpenseFormIntent`: Expense creation and editing actions
- `BudgetIntent`: Budget management actions
- `SplitIntent`: Expense splitting actions

### Effects

- Currency conversion updates
- PDF/CSV export
- Receipt scanning
- Payment reminders

## Dependencies

### Core Dependencies

- `core:domain`: Domain models and use cases
- `core:data`: Repository implementations
- `core:database`: Local storage
- `core:network`: API communication
- `core:common`: Common utilities
- `core:ui`: UI components

### Feature Dependencies

- `feature:trips`: Integration with trip data
- `feature:collaboration`: User management for splitting

### External Libraries

- Currency conversion APIs
- PDF generation libraries
- OCR libraries for receipt scanning
- Chart visualization libraries

## Implementation Examples

### Expense List Screen

```kotlin
@Composable
fun ExpenseListScreen(
    viewModel: ExpenseListViewModel = koinViewModel(),
    onNavigateToDetail: (String) -> Unit,
    onNavigateToAdd: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ExpenseListEffect.NavigateToDetail -> {
                    onNavigateToDetail(effect.expenseId)
                }
                is ExpenseListEffect.ShowError -> {
                    // Show error message
                }
            }
        }
    }
    
    // Initial load of expenses
    LaunchedEffect(Unit) {
        viewModel.processIntent(ExpenseListIntent.LoadExpenses)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Expenses") },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.processIntent(ExpenseListIntent.ToggleFilterPanel)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Expense"
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Budget summary card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Budget Overview",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LinearProgressIndicator(
                        progress = state.budgetUsagePercentage,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Spent: ${state.currencySymbol}${state.totalSpent}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Text(
                            text = "Budget: ${state.currencySymbol}${state.totalBudget}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Remaining: ${state.currencySymbol}${state.remainingBudget}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (state.remainingBudget < 0) 
                            MaterialTheme.colorScheme.error 
                        else 
                            MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            // Filter panel (conditionally shown)
            AnimatedVisibility(visible = state.isFilterPanelVisible) {
                ExpenseFilterPanel(
                    categories = state.availableCategories,
                    selectedCategories = state.selectedCategories,
                    startDate = state.filterStartDate,
                    endDate = state.filterEndDate,
                    onCategoryToggle = { category ->
                        viewModel.processIntent(
                            ExpenseListIntent.ToggleCategory(category)
                        )
                    },
                    onStartDateChange = { date ->
                        viewModel.processIntent(
                            ExpenseListIntent.UpdateFilterStartDate(date)
                        )
                    },
                    onEndDateChange = { date ->
                        viewModel.processIntent(
                            ExpenseListIntent.UpdateFilterEndDate(date)
                        )
                    },
                    onApplyFilters = {
                        viewModel.processIntent(ExpenseListIntent.ApplyFilters)
                    },
                    onResetFilters = {
                        viewModel.processIntent(ExpenseListIntent.ResetFilters)
                    }
                )
            }
            
            // Expense list
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                state.expenses.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No expenses found",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                else -> {
                    LazyColumn {
                        items(state.expenses) { expense ->
                            ExpenseItem(
                                expense = expense,
                                onClick = {
                                    viewModel.processIntent(
                                        ExpenseListIntent.ExpenseClicked(expense.id)
                                    )
                                }
                            )
                            Divider()
                        }
                    }
                }
            }
        }
    }
}
```

### Expense ViewModel

```kotlin
class ExpenseListViewModel(
    private val getExpensesUseCase: GetExpensesUseCase,
    private val getBudgetUseCase: GetBudgetUseCase,
    private val getTripUseCase: GetTripUseCase,
    private val currencyRepository: CurrencyRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ExpenseListState())
    val state = _state.asStateFlow()
    
    private val _effect = Channel<ExpenseListEffect>()
    val effect = _effect.receiveAsFlow()
    
    fun processIntent(intent: ExpenseListIntent) {
        when (intent) {
            is ExpenseListIntent.LoadExpenses -> loadExpenses()
            is ExpenseListIntent.ExpenseClicked -> expenseClicked(intent.expenseId)
            is ExpenseListIntent.ToggleFilterPanel -> toggleFilterPanel()
            is ExpenseListIntent.ToggleCategory -> toggleCategory(intent.category)
            is ExpenseListIntent.UpdateFilterStartDate -> updateStartDate(intent.date)
            is ExpenseListIntent.UpdateFilterEndDate -> updateEndDate(intent.date)
            is ExpenseListIntent.ApplyFilters -> applyFilters()
            is ExpenseListIntent.ResetFilters -> resetFilters()
        }
    }
    
    private fun loadExpenses() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            // Get active trip ID from navigation or preferences
            val tripId = getTripUseCase.getActiveTrip()?.id
            
            if (tripId != null) {
                // Load expenses for the trip
                getExpensesUseCase(
                    tripId = tripId,
                    startDate = _state.value.filterStartDate,
                    endDate = _state.value.filterEndDate,
                    categories = _state.value.selectedCategories.takeIf { 
                        it.isNotEmpty() && it.size < _state.value.availableCategories.size 
                    }
                ).onSuccess { expenses ->
                    _state.update { it.copy(
                        expenses = expenses,
                        isLoading = false
                    )}
                    
                    // Update budget information
                    updateBudgetInfo(tripId)
                }.onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(ExpenseListEffect.ShowError(
                        error.message ?: "Failed to load expenses"
                    ))
                }
            } else {
                _state.update { it.copy(isLoading = false) }
                _effect.send(ExpenseListEffect.ShowError(
                    "No active trip selected"
                ))
            }
        }
    }
    
    private suspend fun updateBudgetInfo(tripId: String) {
        getBudgetUseCase(tripId).onSuccess { budget ->
            val totalSpent = _state.value.expenses.sumOf { 
                currencyRepository.convertToPrimaryCurrency(
                    amount = it.amount,
                    fromCurrency = it.currency
                )
            }
            
            _state.update { it.copy(
                totalBudget = budget.total,
                totalSpent = totalSpent,
                remainingBudget = budget.total - totalSpent,
                budgetUsagePercentage = (totalSpent / budget.total).coerceIn(0f, 1f),
                currencySymbol = currencyRepository.getPrimaryCurrencySymbol()
            )}
        }
    }
    
    private fun expenseClicked(expenseId: String) {
        viewModelScope.launch {
            _effect.send(ExpenseListEffect.NavigateToDetail(expenseId))
        }
    }
}

data class ExpenseListState(
    val expenses: List<Expense> = emptyList(),
    val isLoading: Boolean = false,
    val totalBudget: Double = 0.0,
    val totalSpent: Double = 0.0,
    val remainingBudget: Double = 0.0,
    val budgetUsagePercentage: Float = 0f,
    val currencySymbol: String = "$",
    val isFilterPanelVisible: Boolean = false,
    val availableCategories: List<ExpenseCategory> = ExpenseCategory.values().toList(),
    val selectedCategories: List<ExpenseCategory> = availableCategories,
    val filterStartDate: LocalDate? = null,
    val filterEndDate: LocalDate? = null
)

sealed interface ExpenseListIntent {
    object LoadExpenses : ExpenseListIntent
    data class ExpenseClicked(val expenseId: String) : ExpenseListIntent
    object ToggleFilterPanel : ExpenseListIntent
    data class ToggleCategory(val category: ExpenseCategory) : ExpenseListIntent
    data class UpdateFilterStartDate(val date: LocalDate?) : ExpenseListIntent
    data class UpdateFilterEndDate(val date: LocalDate?) : ExpenseListIntent
    object ApplyFilters : ExpenseListIntent
    object ResetFilters : ExpenseListIntent
}

sealed interface ExpenseListEffect {
    data class NavigateToDetail(val expenseId: String) : ExpenseListEffect
    data class ShowError(val message: String) : ExpenseListEffect
}

enum class ExpenseCategory {
    FOOD, ACCOMMODATION, TRANSPORTATION, ACTIVITIES, SHOPPING, OTHER
}
```