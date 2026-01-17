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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.madhatter.core.di.MetroDi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    refreshSignal: Int = 0,
    onAddTransaction: () -> Unit = {},
    onEditTransaction: (Long) -> Unit = {},
    onAddCategory: () -> Unit = {},
    onEditCategory: (Long) -> Unit = {},
    onOpenSettings: () -> Unit = {},
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
            summary = uiState.summary,
            latestTransactions = uiState.latestTransactions,
            onAddTransaction = onAddTransaction,
            onEditTransaction = onEditTransaction,
            onOpenSettings = onOpenSettings,
            modifier = Modifier.padding(paddingValues),
        )
    }
}

@Composable
private fun HomeContent(
    summary: TransactionSummary,
    latestTransactions: List<LatestTransactionItem>,
    onAddTransaction: () -> Unit,
    onEditTransaction: (Long) -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
            Button(
                onClick = onAddTransaction,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "取引を追加")
            }
        }

        item {
            Button(
                onClick = onOpenSettings,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "設定")
            }
        }

        item {
            SummaryCard(summary = summary)
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "最新の取引",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                if (latestTransactions.isEmpty()) {
                    Text(
                        text = "まだ取引が登録されていません。",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        items(latestTransactions) { transaction ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                onClick = { onEditTransaction(transaction.id) },
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = transaction.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = transaction.typeLabel,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    Text(
                        text = "${transaction.amount} ${transaction.currencyCode}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = transaction.timestamp,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    summary: TransactionSummary,
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
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "取引サマリー",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            SummaryRow(label = "取引件数", value = "${summary.transactionCount}件")
            SummaryRow(
                label = "収入合計",
                value = "${summary.incomeTotal.stripTrailingZeros().toPlainString()} ${summary.currencyLabel}",
            )
            SummaryRow(
                label = "支出合計",
                value = "${summary.expenseTotal.stripTrailingZeros().toPlainString()} ${summary.currencyLabel}",
            )
            SummaryRow(
                label = "収支",
                value = "${summary.netTotal.stripTrailingZeros().toPlainString()} ${summary.currencyLabel}",
            )
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
        )
    }
}
