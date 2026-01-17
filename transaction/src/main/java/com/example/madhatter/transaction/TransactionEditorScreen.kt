package com.example.madhatter.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.madhatter.core.TransactionType
import com.example.madhatter.core.di.MetroDi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionEditorScreen(
    transactionId: Long? = null,
    modifier: Modifier = Modifier,
    onSaveSuccess: () -> Unit = {},
) {
    val transactionViewModel: TransactionEditorViewModel = viewModel(
        factory = TransactionEditorViewModelFactory(
            categoryRepository = MetroDi.categoryRepository(),
            transactionRepository = MetroDi.transactionRepository(),
            settingsRepository = MetroDi.settingsRepository(),
            transactionId = transactionId,
        ),
    )
    val uiState = transactionViewModel.uiState
    val titleRes = if (uiState.isEditing) {
        R.string.transaction_editor_edit_title
    } else {
        R.string.transaction_editor_add_title
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text(text = stringResource(titleRes)) })
        },
    ) { paddingValues ->
        TransactionEditorContent(
            uiState = uiState,
            modifier = Modifier.padding(paddingValues),
            onAmountChange = transactionViewModel::onAmountChange,
            onCurrencyChange = transactionViewModel::onCurrencyChange,
            onMemoChange = transactionViewModel::onMemoChange,
            onCategorySelected = transactionViewModel::onCategorySelected,
            onTypeChange = transactionViewModel::onTypeChange,
            onSave = {
                if (transactionViewModel.saveTransaction()) {
                    onSaveSuccess()
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionEditorContent(
    uiState: TransactionEditorUiState,
    onAmountChange: (String) -> Unit,
    onCurrencyChange: (String) -> Unit,
    onMemoChange: (String) -> Unit,
    onCategorySelected: (Long) -> Unit,
    onTypeChange: (TransactionType) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var categoryExpanded by remember { mutableStateOf(false) }
    val selectedCategory = uiState.categories.firstOrNull { it.id == uiState.selectedCategoryId }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (uiState.validationErrors.isNotEmpty()) {
            ValidationSummary(errors = uiState.validationErrors)
        }

        OutlinedTextField(
            value = uiState.amountInput,
            onValueChange = onAmountChange,
            label = { Text(text = stringResource(R.string.transaction_editor_amount_label)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = uiState.currencyInput,
            onValueChange = onCurrencyChange,
            label = { Text(text = stringResource(R.string.transaction_editor_currency_label)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Ascii,
                capitalization = KeyboardCapitalization.Characters,
            ),
            modifier = Modifier.fillMaxWidth(),
        )

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = stringResource(R.string.transaction_editor_currency_default_label),
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = uiState.defaultCurrencyCode,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = stringResource(R.string.transaction_editor_currency_default_helper),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.transaction_editor_type_label),
                style = MaterialTheme.typography.titleSmall,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = uiState.type == TransactionType.INCOME,
                    onClick = { onTypeChange(TransactionType.INCOME) },
                    label = { Text(text = stringResource(R.string.transaction_editor_type_income)) },
                )
                FilterChip(
                    selected = uiState.type == TransactionType.EXPENSE,
                    onClick = { onTypeChange(TransactionType.EXPENSE) },
                    label = { Text(text = stringResource(R.string.transaction_editor_type_expense)) },
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.transaction_editor_category_label),
                style = MaterialTheme.typography.titleSmall,
            )
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded },
            ) {
                OutlinedTextField(
                    value = selectedCategory?.name
                        ?: stringResource(R.string.transaction_editor_category_placeholder),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false },
                ) {
                    uiState.categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(text = category.name) },
                            onClick = {
                                onCategorySelected(category.id)
                                categoryExpanded = false
                            },
                        )
                    }
                }
            }
            if (uiState.categories.isEmpty()) {
                Text(
                    text = stringResource(R.string.transaction_editor_category_empty),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        OutlinedTextField(
            value = uiState.memoInput,
            onValueChange = onMemoChange,
            label = { Text(text = stringResource(R.string.transaction_editor_memo_label)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
        )

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = stringResource(R.string.transaction_editor_timestamp_label),
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = uiState.formattedTimestamp,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(R.string.transaction_editor_save))
        }
    }
}

@Composable
private fun ValidationSummary(
    errors: List<TransactionEditorValidationError>,
    modifier: Modifier = Modifier,
) {
    val messages = errors.mapNotNull { error ->
        when (error) {
            TransactionEditorValidationError.AmountRequired ->
                stringResource(R.string.transaction_editor_error_amount_required)
            TransactionEditorValidationError.AmountInvalid ->
                stringResource(R.string.transaction_editor_error_amount_invalid)
            TransactionEditorValidationError.CurrencyRequired ->
                stringResource(R.string.transaction_editor_error_currency_required)
            TransactionEditorValidationError.CurrencyInvalid ->
                stringResource(R.string.transaction_editor_error_currency_invalid)
            TransactionEditorValidationError.CategoryRequired ->
                stringResource(R.string.transaction_editor_error_category_required)
            TransactionEditorValidationError.MemoTooLong ->
                stringResource(R.string.transaction_editor_error_memo_too_long)
        }
    }

    if (messages.isEmpty()) {
        return
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
        ),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = stringResource(R.string.transaction_editor_error_header),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
            Spacer(modifier = Modifier.height(8.dp))
            messages.forEach { message ->
                Text(
                    text = "• $message",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        }
    }
}
