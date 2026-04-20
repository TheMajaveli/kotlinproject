package com.nextu.kotlinproject.ui.screens.transactions

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nextu.kotlinproject.ui.theme.KotlinprojectTheme
import androidx.compose.foundation.layout.height
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TransactionInput(
    val amount: Double,
    val description: String,
    val type: String,
    val date: String,
    val imageUri: String?
)

private fun todayFr(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
    return sdf.format(Date())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionFormScreen(
    title: String,
    initial: TransactionInput? = null,
    onSave: (TransactionInput) -> Unit,
    onCancel: (() -> Unit)? = null
) {

    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("expense") }
    var date by remember { mutableStateOf(todayFr()) }
    var imageUri by remember { mutableStateOf<String?>(null) }
    var typeMenuExpanded by remember { mutableStateOf(false) }

    val ctx = LocalContext.current
    val pickImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            if (uri != null) {
                try {
                    ctx.contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: SecurityException) {
                    // ignore, best effort
                }
                imageUri = uri.toString()
            }
        }

    LaunchedEffect(initial) {
        if (initial != null) {
            amount = initial.amount.toString()
            description = initial.description
            type = initial.type
            date = initial.date
            imageUri = initial.imageUri
        }
    }

    val buttonColors = ButtonDefaults.buttonColors(
        containerColor = Color.Black,
        contentColor = Color.White
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text(title) }) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            ExposedDropdownMenuBox(
                expanded = typeMenuExpanded,
                onExpandedChange = { typeMenuExpanded = !typeMenuExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                val typeLabel = if (type == "income") "Revenu" else "Dépense"
                TextField(
                    value = typeLabel,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeMenuExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = typeMenuExpanded,
                    onDismissRequest = { typeMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Revenu") },
                        onClick = {
                            type = "income"
                            typeMenuExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Dépense") },
                        onClick = {
                            type = "expense"
                            typeMenuExpanded = false
                        }
                    )
                }
            }

            TextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Montant") },
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Date (JJ/MM/AAAA)") },
                modifier = Modifier.fillMaxWidth()
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Image du produit",
                        modifier = Modifier.size(96.dp)
                    )
                    TextButton(onClick = { imageUri = null }) {
                        Text("Supprimer l’image")
                    }
                } else {
                    TextButton(onClick = { pickImageLauncher.launch(arrayOf("image/*")) }) {
                        Text("Ajouter une image (optionnel)")
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (onCancel != null) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = onCancel,
                        colors = buttonColors
                    ) {
                        Text("Annuler")
                    }
                }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onSave(
                            TransactionInput(
                                amount.toDoubleOrNull() ?: 0.0,
                                description,
                                type,
                                date,
                                imageUri
                            )
                        )
                    },
                    colors = buttonColors
                ) {
                    Text("Enregistrer")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddTransactionScreenPreview() {
    KotlinprojectTheme {
        TransactionFormScreen(title = "Ajouter", onSave = {}, onCancel = {})
    }
}

@Preview(showBackground = true)
@Composable
fun EditTransactionScreenPreview() {
    KotlinprojectTheme {
        TransactionFormScreen(
            title = "Modifier",
            initial = TransactionInput(12.5, "Déjeuner", "expense", "26/03/2026", null),
            onSave = {},
            onCancel = {}
        )
    }
}