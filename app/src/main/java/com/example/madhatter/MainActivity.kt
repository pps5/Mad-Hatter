package com.example.madhatter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.madhatter.category.CategoryEditorScreen
import com.example.madhatter.home.HomeScreen
import com.example.madhatter.transaction.TransactionEditorScreen
import com.example.madhatter.ui.theme.MadHatterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MadHatterTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    var currentScreen by remember { mutableStateOf<MainScreen>(MainScreen.Home) }
                    var refreshSignal by remember { mutableStateOf(0) }

                    when (val screen = currentScreen) {
                        MainScreen.Home -> HomeScreen(
                            refreshSignal = refreshSignal,
                            onAddTransaction = {
                                currentScreen = MainScreen.TransactionEditor()
                            },
                            onEditTransaction = { transactionId ->
                                currentScreen = MainScreen.TransactionEditor(transactionId)
                            },
                            onAddCategory = {
                                currentScreen = MainScreen.CategoryEditor()
                            },
                            onEditCategory = { categoryId ->
                                currentScreen = MainScreen.CategoryEditor(categoryId)
                            },
                        )
                        is MainScreen.TransactionEditor -> TransactionEditorScreen(
                            transactionId = screen.transactionId,
                            onSaveSuccess = {
                                currentScreen = MainScreen.Home
                                refreshSignal += 1
                            },
                        )
                        is MainScreen.CategoryEditor -> CategoryEditorScreen(
                            categoryId = screen.categoryId,
                            onSaveSuccess = {
                                currentScreen = MainScreen.Home
                                refreshSignal += 1
                            },
                            onDeleteSuccess = {
                                currentScreen = MainScreen.Home
                                refreshSignal += 1
                            },
                        )
                    }
                }
            }
        }
    }
}

private sealed class MainScreen {
    data object Home : MainScreen()

    data class TransactionEditor(val transactionId: Long? = null) : MainScreen()

    data class CategoryEditor(val categoryId: Long? = null) : MainScreen()
}
