package com.nextu.kotlinproject.ui.screens.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import coil.compose.AsyncImage
import com.nextu.kotlinproject.data.model.Transaction
import com.nextu.kotlinproject.ui.theme.KotlinprojectTheme
import com.nextu.kotlinproject.viewmodel.ProfileViewModel
import com.nextu.kotlinproject.viewmodel.TransactionViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardRoute(
    viewModel: TransactionViewModel,
    onAddClick: () -> Unit,
    onEditClick: (Transaction) -> Unit,
    onProfileClick: () -> Unit,
    profileViewModel: ProfileViewModel
) {
    val transactions by viewModel.transactions.collectAsState(initial = emptyList())
    val profile by profileViewModel.profile.collectAsState()
    val prenom = profile?.prenom?.takeIf { it.isNotBlank() } ?: "Junior"

    DashboardScreen(
        transactions = transactions,
        onAddClick = onAddClick,
        onEditClick = onEditClick,
        onProfileClick = onProfileClick,
        prenom = prenom,
        onDeleteClick = { viewModel.delete(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    transactions: List<Transaction>,
    onAddClick: () -> Unit,
    onEditClick: (Transaction) -> Unit,
    onProfileClick: () -> Unit,
    prenom: String,
    onDeleteClick: (Transaction) -> Unit
) {
    val buttonColors = ButtonDefaults.buttonColors(
        containerColor = Color.Black,
        contentColor = Color.White
    )

    var dialogTransaction by remember { mutableStateOf<Transaction?>(null) }

    val totalBalance = transactions.fold(0.0) { acc, t ->
        val signed = if (t.type == "income") t.amount else -t.amount
        acc + signed
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Tableau de bord") }) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = Color.Black,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation()
            ) {
                Text("+")
            }
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onProfileClick() },
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "Profil",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape),
                        tint = Color.Black
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = prenom,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Bonjour $prenom\nBienvenue de retour sur ton tracker",
                            color = Color.DarkGray
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Solde total", color = Color.DarkGray)
                        Text(
                            text = String.format(Locale.FRANCE, "%.2f €", totalBalance),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                Text("Transactions", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))
            }

            items(transactions) { transaction ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clickable { dialogTransaction = transaction }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (!transaction.imageUri.isNullOrBlank()) {
                            AsyncImage(
                                model = transaction.imageUri,
                                contentDescription = "Image du produit",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                            )
                        } else {
                            val icon = when {
                                transaction.description.contains("spotify", ignoreCase = true) -> Icons.Filled.MusicNote
                                transaction.description.contains("apple", ignoreCase = true) -> Icons.Filled.PhoneIphone
                                transaction.description.contains("nike", ignoreCase = true) -> Icons.Filled.ShoppingBag
                                transaction.type == "income" -> Icons.Filled.ShoppingCart
                                else -> Icons.Filled.ShoppingCart
                            }

                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(transaction.description, fontWeight = FontWeight.Medium)
                            Text(transaction.date, color = Color.DarkGray, fontSize = 12.sp)
                        }

                        Text(
                            text = String.format(Locale.FRANCE, "%.2f €", transaction.amount),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }

    if (dialogTransaction != null) {
        val t = dialogTransaction!!
        AlertDialog(
            onDismissRequest = { dialogTransaction = null },
            title = { Text("Que veux-tu faire ?") },
            text = { Text("Transaction : ${t.description}") },
            confirmButton = {
                Button(
                    onClick = {
                        dialogTransaction = null
                        onEditClick(t)
                    },
                    colors = buttonColors
                ) { Text("Modifier") }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = {
                            dialogTransaction = null
                            onDeleteClick(t)
                        },
                        colors = buttonColors
                    ) { Text("Supprimer") }
                    Button(
                        onClick = { dialogTransaction = null },
                        colors = buttonColors
                    ) { Text("Annuler") }
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    KotlinprojectTheme {
        DashboardScreen(
            transactions = listOf(
                Transaction(id = 1, amount = 89.99, description = "Nike chaussures", type = "expense", date = "26/03/2026", imageUri = null),
                Transaction(id = 2, amount = 9.99, description = "Spotify abonnement", type = "expense", date = "25/03/2026", imageUri = null),
                Transaction(id = 3, amount = 1200.0, description = "Salaire", type = "income", date = "24/03/2026", imageUri = null)
            ),
            onAddClick = {},
            onEditClick = {},
            onProfileClick = {},
            prenom = "Junior",
            onDeleteClick = {}
        )
    }
}