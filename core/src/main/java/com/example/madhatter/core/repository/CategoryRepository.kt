package com.example.madhatter.core.repository

import com.example.madhatter.core.Category

interface CategoryRepository {
    fun getById(id: Long): Category?

    fun getAll(): List<Category>

    fun getActive(): List<Category>

    fun getSubcategories(parentId: Long): List<Category>

    fun insert(category: Category)

    fun update(category: Category)

    fun deleteById(id: Long)
}
