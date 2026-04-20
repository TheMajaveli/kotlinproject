package com.nextu.kotlinproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.nextu.kotlinproject.data.local.AppDb
import com.nextu.kotlinproject.data.model.Transaction
import com.nextu.kotlinproject.data.repository.TransactionRepository
import com.nextu.kotlinproject.ui.screens.dashboard.DashboardRoute
import com.nextu.kotlinproject.ui.screens.transactions.TransactionInput
import com.nextu.kotlinproject.ui.theme.KotlinprojectTheme
import com.nextu.kotlinproject.viewmodel.TransactionViewModel
import com.nextu.kotlinproject.viewmodel.TransactionViewModelFactory
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nextu.kotlinproject.ui.screens.transactions.TransactionFormScreen
import com.nextu.kotlinproject.ui.screens.profile.ProfileRoute
import com.nextu.kotlinproject.viewmodel.ProfileViewModel
import com.nextu.kotlinproject.viewmodel.ProfileViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = Room.databaseBuilder(
            applicationContext,
            AppDb::class.java,
            "budget.db"
        )
            .fallbackToDestructiveMigration()
            .build()
        val repo = TransactionRepository(db.transactionDao())
        val profileDao = db.profileDao()

        setContent {
            KotlinprojectTheme {
                val vm: TransactionViewModel = viewModel(factory = TransactionViewModelFactory(repo))
                val profileVm: ProfileViewModel = viewModel(
                    factory = ProfileViewModelFactory(
                        app = application,
                        dao = profileDao
                    )
                )
                BudgetApp(vm, profileVm)
            }
        }
    }
}

@Composable
private fun BudgetApp(vm: TransactionViewModel, profileVm: ProfileViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {
        composable("dashboard") {
            DashboardRoute(
                viewModel = vm,
                onAddClick = { navController.navigate("add") },
                onEditClick = { t -> navController.navigate("edit/${t.id}") },
                onProfileClick = { navController.navigate("profile") },
                profileViewModel = profileVm
            )
        }

        composable("profile") {
            ProfileRoute(
                viewModel = profileVm,
                onBack = { navController.popBackStack() }
            )
        }

        composable("add") {
            TransactionFormScreen(
                title = "Ajouter",
                onSave = { input: TransactionInput ->
                    vm.add(
                        Transaction(
                            amount = input.amount,
                            description = input.description,
                            type = input.type,
                            date = input.date,
                            imageUri = input.imageUri
                        )
                    )
                    navController.popBackStack()
                },
                onCancel = { navController.popBackStack() }
            )
        }

        composable(
            route = "edit/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: return@composable
            LaunchedEffect(id) {
                vm.loadById(id)
            }
            val selected by vm.selectedTransaction.collectAsState()
            val t = selected
            if (t != null) {
                TransactionFormScreen(
                    title = "Modifier",
                    initial = TransactionInput(
                        amount = t.amount,
                        description = t.description,
                        type = t.type,
                        date = t.date,
                        imageUri = t.imageUri
                    ),
                    onSave = { input: TransactionInput ->
                        vm.update(
                            t.copy(
                                amount = input.amount,
                                description = input.description,
                                type = input.type,
                                date = input.date,
                                imageUri = input.imageUri
                            )
                        )
                        vm.clearSelection()
                        navController.popBackStack()
                    },
                    onCancel = {
                        vm.clearSelection()
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}