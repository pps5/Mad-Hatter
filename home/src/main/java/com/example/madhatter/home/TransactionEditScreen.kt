package com.example.madhatter.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.madhatter.core.TransactionType

data class TransactionCategoryOption(
    val id: Long,
    val name: String,
    val type: TransactionType,
    val parentName: String? = null,
)

data class TransactionEditUiState(
    val timestamp: String,
    val amount: String,
    val currencyCode: String,
    val type: TransactionType,
    val categories: List<TransactionCategoryOption>,
    val selectedCategoryId: Long?,
    val memo: String,
    val isEditing: Boolean,
)

@Composable
fun TransactionEditScreenRoute(
    viewModel: TransactionEditViewModel = viewModel(),
    modifier: Modifier = Modifier,
) {
    val state = viewModel.state

    TransactionEditScreen(
        state = state,
        onTimestampChange = viewModel::onTimestampChange,
        onAmountChange = viewModel::onAmountChange,
        onCurrencyCodeChange = viewModel::onCurrencyCodeChange,
        onTypeChange = viewModel::onTypeChange,
        onCategoryChange = viewModel::onCategoryChange,
        onMemoChange = viewModel::onMemoChange,
        onSave = viewModel::onSave,
        onDelete = viewModel::onDelete,
        onCancel = viewModel::onCancel,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionEditScreen(
    state: TransactionEditUiState,
    onTimestampChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onCurrencyCodeChange: (String) -> Unit,
    onTypeChange: (TransactionType) -> Unit,
    onCategoryChange: (Long) -> Unit,
    onMemoChange: (String) -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(
                            if (state.isEditing) {
                                R.string.transaction_edit_title_edit
                            } else {
                                R.string.transaction_edit_title_new
                            },
                        ),
                    )
                },
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = stringResource(R.string.transaction_edit_header),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = stringResource(R.string.transaction_edit_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            item {
                TransactionEditForm(
                    state = state,
                    onTimestampChange = onTimestampChange,
                    onAmountChange = onAmountChange,
                    onCurrencyCodeChange = onCurrencyCodeChange,
                    onTypeChange = onTypeChange,
                    onCategoryChange = onCategoryChange,
                    onMemoChange = onMemoChange,
                )
            }
            item {
                TransactionEditActions(
                    isEditing = state.isEditing,
                    onSave = onSave,
                    onDelete = onDelete,
                    onCancel = onCancel,
                )
            }
        }
    }
}

@Composable
private fun TransactionEditForm(
    state: TransactionEditUiState,
    onTimestampChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onCurrencyCodeChange: (String) -> Unit,
    onTypeChange: (TransactionType) -> Unit,
    onCategoryChange: (Long) -> Unit,
    onMemoChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = state.timestamp,
                onValueChange = onTimestampChange,
                label = { Text(text = stringResource(R.string.transaction_edit_timestamp_label)) },
                placeholder = { Text(text = stringResource(R.string.transaction_edit_timestamp_placeholder)) },
                supportingText = { Text(text = stringResource(R.string.transaction_edit_timestamp_support)) },
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value = state.amount,
                    onValueChange = onAmountChange,
                    label = { Text(text = stringResource(R.string.transaction_edit_amount_label)) },
                    placeholder = { Text(text = stringResource(R.string.transaction_edit_amount_placeholder)) },
                    modifier = Modifier.weight(1f),
                )
                OutlinedTextField(
                    value = state.currencyCode,
                    onValueChange = onCurrencyCodeChange,
                    label = { Text(text = stringResource(R.string.transaction_edit_currency_label)) },
                    placeholder = { Text(text = stringResource(R.string.transaction_edit_currency_placeholder)) },
                    modifier = Modifier.width(110.dp),
                )
            }
            TransactionTypeSelector(
                selectedType = state.type,
                onTypeChange = onTypeChange,
            )
            TransactionCategorySelector(
                categories = state.categories,
                selectedCategoryId = state.selectedCategoryId,
                onCategoryChange = onCategoryChange,
            )
            OutlinedTextField(
                value = state.memo,
                onValueChange = onMemoChange,
                label = { Text(text = stringResource(R.string.transaction_edit_memo_label)) },
                placeholder = { Text(text = stringResource(R.string.transaction_edit_memo_placeholder)) },
                supportingText = { Text(text = stringResource(R.string.transaction_edit_memo_support)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                minLines = 3,
                maxLines = 5,
            )
        }
    }
}

@Composable
private fun TransactionTypeSelector(
    selectedType: TransactionType,
    onTypeChange: (TransactionType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.transaction_edit_type_label),
            style = MaterialTheme.typography.labelLarge,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            FilterChip(
                selected = selectedType == TransactionType.INCOME,
                onClick = { onTypeChange(TransactionType.INCOME) },
                label = { Text(text = stringResource(R.string.transaction_edit_type_income)) },
            )
            FilterChip(
                selected = selectedType == TransactionType.EXPENSE,
                onClick = { onTypeChange(TransactionType.EXPENSE) },
                label = { Text(text = stringResource(R.string.transaction_edit_type_expense)) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionCategorySelector(
    categories: List<TransactionCategoryOption>,
    selectedCategoryId: Long?,
    onCategoryChange: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = categories.firstOrNull { it.id == selectedCategoryId }
    val label = stringResource(R.string.transaction_edit_category_label)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                value = selected?.let { buildCategoryLabel(it) }
                    ?: stringResource(R.string.transaction_edit_category_placeholder),
                onValueChange = {},
                readOnly = true,
                label = { Text(text = label) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(text = buildCategoryLabel(category)) },
                        onClick = {
                            onCategoryChange(category.id)
                            expanded = false
                        },
                    )
                }
            }
        }
        Text(
            text = stringResource(R.string.transaction_edit_category_support),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun TransactionEditActions(
    isEditing: Boolean,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(R.string.transaction_edit_save))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isEditing) {
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = stringResource(R.string.transaction_edit_delete))
                }
                Spacer(modifier = Modifier.width(12.dp))
            }
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
            ) {
                Text(text = stringResource(R.string.transaction_edit_cancel))
            }
        }
        Text(
            text = stringResource(R.string.transaction_edit_actions_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

private fun buildCategoryLabel(category: TransactionCategoryOption): String {
    return if (category.parentName.isNullOrBlank()) {
        category.name
    } else {
        "${category.parentName} / ${category.name}"
    }
}
