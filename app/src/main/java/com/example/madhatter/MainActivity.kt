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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.madhatter.category.CategoryEditorScreen
import com.example.madhatter.home.HomeScreen
import com.example.madhatter.settings.SettingsScreen
import com.example.madhatter.transaction.TransactionEditorScreen
import com.example.madhatter.ui.theme.MadHatterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MadHatterTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    var refreshSignal by remember { mutableStateOf(0) }
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = MainRoute.Home,
                    ) {
                        composable(MainRoute.Home) {
                            HomeScreen(
                                refreshSignal = refreshSignal,
                                onAddTransaction = {
                                    navController.navigate(MainRoute.TransactionEditor)
                                },
                                onEditTransaction = { transactionId ->
                                    navController.navigate(MainRoute.transactionEditorWithId(transactionId))
                                },
                                onAddCategory = {
                                    navController.navigate(MainRoute.CategoryEditor)
                                },
                                onEditCategory = { categoryId ->
                                    navController.navigate(MainRoute.categoryEditorWithId(categoryId))
                                },
                                onOpenSettings = {
                                    navController.navigate(MainRoute.Settings)
                                },
                            )
                        }
                        composable(
                            route = MainRoute.TransactionEditor,
                            arguments = listOf(
                                navArgument(MainRoute.TransactionIdArg) {
                                    type = NavType.StringType
                                    nullable = true
                                },
                            ),
                        ) { backStackEntry ->
                            val transactionId =
                                backStackEntry.arguments?.getString(MainRoute.TransactionIdArg)?.toLongOrNull()
                            TransactionEditorScreen(
                                transactionId = transactionId,
                                onSaveSuccess = {
                                    refreshSignal += 1
                                    navController.navigate(MainRoute.Home) {
                                        popUpTo(MainRoute.Home) { inclusive = true }
                                    }
                                },
                            )
                        }
                        composable(
                            route = MainRoute.CategoryEditor,
                            arguments = listOf(
                                navArgument(MainRoute.CategoryIdArg) {
                                    type = NavType.StringType
                                    nullable = true
                                },
                            ),
                        ) { backStackEntry ->
                            val categoryId =
                                backStackEntry.arguments?.getString(MainRoute.CategoryIdArg)?.toLongOrNull()
                            CategoryEditorScreen(
                                categoryId = categoryId,
                                onSaveSuccess = {
                                    refreshSignal += 1
                                    navController.navigate(MainRoute.Home) {
                                        popUpTo(MainRoute.Home) { inclusive = true }
                                    }
                                },
                                onDeleteSuccess = {
                                    refreshSignal += 1
                                    navController.navigate(MainRoute.Home) {
                                        popUpTo(MainRoute.Home) { inclusive = true }
                                    }
                                },
                            )
                        }
                        composable(MainRoute.Settings) {
                            SettingsScreen()
                        }
                    }
                }
            }
        }
    }
}

private object MainRoute {
    const val Home = "home"
    const val TransactionIdArg = "transactionId"
    const val CategoryIdArg = "categoryId"
    const val TransactionEditor = "transactionEditor?$TransactionIdArg={$TransactionIdArg}"
    const val CategoryEditor = "categoryEditor?$CategoryIdArg={$CategoryIdArg}"
    const val Settings = "settings"

    fun transactionEditorWithId(transactionId: Long) =
        "transactionEditor?$TransactionIdArg=$transactionId"

    fun categoryEditorWithId(categoryId: Long) =
        "categoryEditor?$CategoryIdArg=$categoryId"
}
