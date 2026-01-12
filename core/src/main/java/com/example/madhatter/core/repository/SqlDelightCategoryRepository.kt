package com.example.madhatter.core.repository

import com.example.madhatter.core.Category
import com.example.madhatter.core.TransactionType
import com.example.madhatter.core.database.Categories as DbCategory
import com.example.madhatter.core.database.MadHatterDatabase

class SqlDelightCategoryRepository(
    private val database: MadHatterDatabase,
) : CategoryRepository {
    override fun getById(id: Long): Category? {
        return database.categoryQueries.selectCategoryById(id)
            .executeAsOneOrNull()
            ?.toDomain()
    }

    override fun getAll(): List<Category> {
        return database.categoryQueries.selectAllCategories()
            .executeAsList()
            .map { it.toDomain() }
    }

    override fun getActive(): List<Category> {
        return database.categoryQueries.selectActiveCategories()
            .executeAsList()
            .map { it.toDomain() }
    }

    override fun getSubcategories(parentId: Long): List<Category> {
        return database.categoryQueries.selectSubcategories(parentId)
            .executeAsList()
            .map { it.toDomain() }
    }

    override fun insert(category: Category) {
        database.categoryQueries.insertCategory(
            id = category.id,
            name = category.name,
            type = category.type.name,
            is_preset = category.isPreset,
            is_active = category.isActive,
            parent_id = category.parentId,
        )
    }

    override fun update(category: Category) {
        database.categoryQueries.updateCategory(
            name = category.name,
            type = category.type.name,
            is_preset = category.isPreset,
            is_active = category.isActive,
            parent_id = category.parentId,
            id = category.id,
        )
    }

    override fun deleteById(id: Long) {
        database.categoryQueries.deleteCategory(id)
    }
}

private fun DbCategory.toDomain(): Category {
    return Category(
        id = id,
        name = name,
        type = TransactionType.valueOf(type),
        isPreset = is_preset,
        isActive = is_active,
        parentId = parent_id,
    )
}
