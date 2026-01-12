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
import com.example.madhatter.home.HomeScreen
import com.example.madhatter.transaction.TransactionEditorScreen
import com.example.madhatter.ui.theme.MadHatterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MadHatterTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    var refreshSignal by remember { mutableStateOf(0) }

                    NavHost(
                        navController = navController,
                        startDestination = MainRoute.Home.route,
                    ) {
                        composable(MainRoute.Home.route) {
                            HomeScreen(
                                refreshSignal = refreshSignal,
                                onAddTransaction = {
                                    navController.navigate(MainRoute.TransactionEditor.route)
                                },
                                onEditTransaction = { transactionId ->
                                    navController.navigate(
                                        MainRoute.TransactionEditor.withTransactionId(transactionId),
                                    )
                                },
                            )
                        }
                        composable(MainRoute.TransactionEditor.route) {
                            TransactionEditorScreen(
                                transactionId = null,
                                onSaveSuccess = {
                                    navController.popBackStack(MainRoute.Home.route, false)
                                    refreshSignal += 1
                                },
                            )
                        }
                        composable(
                            route = MainRoute.TransactionEditor.routeWithId,
                            arguments = listOf(
                                navArgument(MainRoute.TransactionEditor.transactionIdArgument) {
                                    type = NavType.LongType
                                },
                            ),
                        ) { backStackEntry ->
                            val transactionId = backStackEntry.arguments?.getLong(
                                MainRoute.TransactionEditor.transactionIdArgument,
                            )
                            TransactionEditorScreen(
                                transactionId = transactionId,
                                onSaveSuccess = {
                                    navController.popBackStack(MainRoute.Home.route, false)
                                    refreshSignal += 1
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

private sealed class MainRoute(val route: String) {
    data object Home : MainRoute("home")

    data object TransactionEditor : MainRoute("transaction-editor") {
        const val transactionIdArgument = "transactionId"
        const val routeWithId = "transaction-editor/{$transactionIdArgument}"

        fun withTransactionId(transactionId: Long) = "transaction-editor/$transactionId"
    }
}
