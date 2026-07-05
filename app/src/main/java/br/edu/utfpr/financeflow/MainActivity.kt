package br.edu.utfpr.financeflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.edu.utfpr.financeflow.ui.HomeScreen
import br.edu.utfpr.financeflow.ui.StatementScreen
import br.edu.utfpr.financeflow.ui.theme.FinanceFlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinanceFlowTheme {

                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "home",
                ) {
                    composable("home") {
                        HomeScreen(onNavigateToStatement = {
                            navController.navigate("statement")
                        })
                    }
                    composable("statement") {
                        StatementScreen(onNavigateBack = {
                            navController.popBackStack()
                        })
                    }
                }
            }
        }
    }
}
