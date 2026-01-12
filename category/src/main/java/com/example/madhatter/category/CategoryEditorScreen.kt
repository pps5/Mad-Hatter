package com.example.madhatter.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.madhatter.core.TransactionType
import com.example.madhatter.core.di.MetroDi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryEditorScreen(
    categoryId: Long? = null,
    modifier: Modifier = Modifier,
    onSaveSuccess: () -> Unit = {},
    onDeleteSuccess: () -> Unit = {},
) {
    val categoryViewModel: CategoryEditorViewModel = viewModel(
        factory = CategoryEditorViewModelFactory(
            categoryRepository = MetroDi.categoryRepository(),
            categoryId = categoryId,
        ),
    )
    val uiState = categoryViewModel.uiState
    val titleRes = if (uiState.isEditing) {
        R.string.category_editor_edit_title
    } else {
        R.string.category_editor_add_title
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text(text = stringResource(titleRes)) })
        },
    ) { paddingValues ->
        CategoryEditorContent(
            uiState = uiState,
            modifier = Modifier.padding(paddingValues),
            onNameChange = categoryViewModel::onNameChange,
            onTypeChange = categoryViewModel::onTypeChange,
            onParentSelected = categoryViewModel::onParentSelected,
            onActiveChange = categoryViewModel::onActiveChange,
            onSave = {
                if (categoryViewModel.saveCategory()) {
                    onSaveSuccess()
                }
            },
            onDelete = {
                if (categoryViewModel.deleteCategory()) {
                    onDeleteSuccess()
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryEditorContent(
    uiState: CategoryEditorUiState,
    onNameChange: (String) -> Unit,
    onTypeChange: (TransactionType) -> Unit,
    onParentSelected: (Long?) -> Unit,
    onActiveChange: (Boolean) -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var parentExpanded by remember { mutableStateOf(false) }
    val selectedParent = uiState.parentOptions.firstOrNull { it.id == uiState.selectedParentId }

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
            value = uiState.nameInput,
            onValueChange = onNameChange,
            label = { Text(text = stringResource(R.string.category_editor_name_label)) },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            modifier = Modifier.fillMaxWidth(),
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.category_editor_type_label),
                style = MaterialTheme.typography.titleSmall,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = uiState.type == TransactionType.INCOME,
                    onClick = { onTypeChange(TransactionType.INCOME) },
                    label = { Text(text = stringResource(R.string.category_editor_type_income)) },
                )
                FilterChip(
                    selected = uiState.type == TransactionType.EXPENSE,
                    onClick = { onTypeChange(TransactionType.EXPENSE) },
                    label = { Text(text = stringResource(R.string.category_editor_type_expense)) },
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.category_editor_parent_label),
                style = MaterialTheme.typography.titleSmall,
            )
            ExposedDropdownMenuBox(
                expanded = parentExpanded,
                onExpandedChange = { parentExpanded = !parentExpanded },
            ) {
                OutlinedTextField(
                    value = selectedParent?.name
                        ?: stringResource(R.string.category_editor_parent_placeholder),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = parentExpanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                )
                ExposedDropdownMenu(
                    expanded = parentExpanded,
                    onDismissRequest = { parentExpanded = false },
                ) {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(R.string.category_editor_parent_none)) },
                        onClick = {
                            onParentSelected(null)
                            parentExpanded = false
                        },
                    )
                    uiState.parentOptions.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(text = category.name) },
                            onClick = {
                                onParentSelected(category.id)
                                parentExpanded = false
                            },
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.category_editor_active_label),
                style = MaterialTheme.typography.titleSmall,
            )
            Switch(
                checked = uiState.isActive,
                onCheckedChange = onActiveChange,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.category_editor_save))
            }
            if (uiState.isEditing) {
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = stringResource(R.string.category_editor_delete))
                }
            }
        }
    }
}

@Composable
private fun ValidationSummary(
    errors: List<CategoryEditorValidationError>,
    modifier: Modifier = Modifier,
) {
    val messages = errors.mapNotNull { error ->
        when (error) {
            CategoryEditorValidationError.NameRequired ->
                stringResource(R.string.category_editor_error_name_required)
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
                text = stringResource(R.string.category_editor_error_header),
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
