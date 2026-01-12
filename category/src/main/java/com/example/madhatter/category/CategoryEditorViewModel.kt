package com.example.madhatter.category

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.madhatter.core.Category
import com.example.madhatter.core.TransactionType
import com.example.madhatter.core.repository.CategoryRepository

class CategoryEditorViewModel(
    private val categoryRepository: CategoryRepository,
    private val categoryId: Long?,
) : ViewModel() {
    var uiState by mutableStateOf(CategoryEditorUiState())
        private set

    init {
        loadInitialState()
    }

    fun onNameChange(name: String) {
        uiState = uiState.copy(nameInput = name)
    }

    fun onTypeChange(type: TransactionType) {
        val parentOptions = buildParentOptions(type, uiState.allCategories, uiState.categoryId)
        val selectedParentId = uiState.selectedParentId
        val isValidParent = selectedParentId != null && parentOptions.any { it.id == selectedParentId }
        uiState = uiState.copy(
            type = type,
            parentOptions = parentOptions,
            selectedParentId = if (isValidParent) selectedParentId else null,
        )
    }

    fun onParentSelected(parentId: Long?) {
        uiState = uiState.copy(selectedParentId = parentId)
    }

    fun onActiveChange(isActive: Boolean) {
        uiState = uiState.copy(isActive = isActive)
    }

    fun saveCategory(): Boolean {
        val validationErrors = validateInputs()
        if (validationErrors.isNotEmpty()) {
            uiState = uiState.copy(validationErrors = validationErrors)
            return false
        }

        val name = uiState.nameInput.trim()
        val type = uiState.type
        val parentId = uiState.selectedParentId
        val isActive = uiState.isActive

        val category = Category(
            id = uiState.categoryId ?: nextCategoryId(uiState.allCategories),
            name = name,
            type = type,
            isPreset = uiState.isPreset,
            isActive = isActive,
            parentId = parentId,
        )

        if (uiState.isEditing) {
            categoryRepository.update(category)
        } else {
            categoryRepository.insert(category)
        }
        uiState = uiState.copy(validationErrors = emptyList())
        return true
    }

    fun deleteCategory(): Boolean {
        val id = uiState.categoryId ?: return false
        categoryRepository.deleteById(id)
        return true
    }

    private fun loadInitialState() {
        val categories = categoryRepository.getAll()
        val storedCategory = categoryId?.let { categoryRepository.getById(it) }
        val type = storedCategory?.type ?: TransactionType.EXPENSE
        uiState = uiState.copy(
            isEditing = storedCategory != null,
            categoryId = storedCategory?.id,
            nameInput = storedCategory?.name.orEmpty(),
            type = type,
            isPreset = storedCategory?.isPreset ?: false,
            isActive = storedCategory?.isActive ?: true,
            allCategories = categories,
            parentOptions = buildParentOptions(type, categories, storedCategory?.id),
            selectedParentId = storedCategory?.parentId,
        )
    }

    private fun validateInputs(): List<CategoryEditorValidationError> {
        val errors = mutableListOf<CategoryEditorValidationError>()
        if (uiState.nameInput.trim().isBlank()) {
            errors.add(CategoryEditorValidationError.NameRequired)
        }
        return errors
    }

    private fun nextCategoryId(categories: List<Category>): Long {
        val maxId = categories.maxOfOrNull { it.id } ?: 0L
        return maxId + 1L
    }

    private fun buildParentOptions(
        type: TransactionType,
        categories: List<Category>,
        currentId: Long?,
    ): List<Category> {
        return categories.filter { category ->
            category.type == type && category.parentId == null && category.id != currentId
        }
    }
}

data class CategoryEditorUiState(
    val isEditing: Boolean = false,
    val categoryId: Long? = null,
    val nameInput: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val isPreset: Boolean = false,
    val isActive: Boolean = true,
    val allCategories: List<Category> = emptyList(),
    val parentOptions: List<Category> = emptyList(),
    val selectedParentId: Long? = null,
    val validationErrors: List<CategoryEditorValidationError> = emptyList(),
)

enum class CategoryEditorValidationError {
    NameRequired,
}

class CategoryEditorViewModelFactory(
    private val categoryRepository: CategoryRepository,
    private val categoryId: Long?,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryEditorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryEditorViewModel(categoryRepository, categoryId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
