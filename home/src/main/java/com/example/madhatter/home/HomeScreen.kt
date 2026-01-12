package com.example.madhatter.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.madhatter.core.di.MetroDi

data class HomeDashboardItem(
    val title: String,
    val description: String,
    val status: String,
    val timestamp: String,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    refreshSignal: Int = 0,
    onAddTransaction: () -> Unit = {},
    onEditTransaction: (Long) -> Unit = {},
    onAddCategory: () -> Unit = {},
    onEditCategory: (Long) -> Unit = {},
) {
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            categoryRepository = MetroDi.categoryRepository(),
            transactionRepository = MetroDi.transactionRepository(),
        ),
    )
    val uiState = homeViewModel.uiState

    LaunchedEffect(refreshSignal) {
        homeViewModel.loadDashboard()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text(text = "ホーム") })
        },
    ) { paddingValues ->
        HomeContent(
            items = uiState.items,
            latestTransactionId = uiState.latestTransactionId,
            latestCategoryId = uiState.latestCategoryId,
            onAddTransaction = onAddTransaction,
            onEditTransaction = onEditTransaction,
            onAddCategory = onAddCategory,
            onEditCategory = onEditCategory,
            modifier = Modifier.padding(paddingValues),
        )
    }
}

@Composable
private fun HomeContent(
    items: List<HomeDashboardItem>,
    latestTransactionId: Long?,
    latestCategoryId: Long?,
    onAddTransaction: () -> Unit,
    onEditTransaction: (Long) -> Unit,
    onAddCategory: () -> Unit,
    onEditCategory: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "ダッシュボード",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "今日の予定と進捗をまとめて表示します。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onAddTransaction,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = "取引を追加")
                }
                if (latestTransactionId != null) {
                    OutlinedButton(
                        onClick = { onEditTransaction(latestTransactionId) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(text = "最新の取引を編集")
                    }
                }
                OutlinedButton(
                    onClick = onAddCategory,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = "カテゴリを追加")
                }
                if (latestCategoryId != null) {
                    OutlinedButton(
                        onClick = { onEditCategory(latestCategoryId) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(text = "最新のカテゴリを編集")
                    }
                }
            }
        }

        items(items) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = item.status,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = item.timestamp,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
