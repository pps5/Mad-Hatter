package com.example.madhatter.core

/**
 * Category model that supports a two-level hierarchy (category -> subcategory).
 *
 * User-defined categories and subcategories are allowed.
 *
 * Preset categories are identified via [isPreset] so the frontend can localize
 * their display names independently of the stored [name] value.
 */
data class Category(
    val id: Long,
    val name: String,
    val type: TransactionType,
    val isPreset: Boolean,
    val isActive: Boolean = true,
    val parentId: Long? = null,
) {
    fun validate(): List<String> {
        val errors = mutableListOf<String>()

        if (id <= 0L) {
            errors.add("id must be greater than 0")
        }

        if (name.isBlank()) {
            errors.add("name is required")
        }

        if (parentId != null && parentId <= 0L) {
            errors.add("parentId must be greater than 0")
        }

        return errors
    }
}
