package ir.mohandesan.calc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ir.mohandesan.calc.ui.navigation.Screen
import ir.mohandesan.calc.ui.screens.CalculatorScreen
import ir.mohandesan.calc.ui.screens.HistoryScreen
import ir.mohandesan.calc.ui.screens.MatrixScreen
import ir.mohandesan.calc.ui.screens.PlotterScreen
import ir.mohandesan.calc.ui.screens.SettingsScreen
import ir.mohandesan.calc.ui.screens.UnitConverterScreen
import ir.mohandesan.calc.ui.theme.MohandesCalcTheme
import ir.mohandesan.calc.ui.theme.ThemeMode
import ir.mohandesan.calc.viewmodel.CalculatorViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppRoot()
        }
    }
}

@Composable
private fun AppRoot() {
    // Simple state for theme + haptic. For persistence, wrap with DataStore later.
    var themeMode by remember { mutableStateOf(ThemeMode.SYSTEM) }
    var haptic by remember { mutableStateOf(true) }

    MohandesCalcTheme(themeMode = themeMode) {
        val navController = rememberNavController()
        val calculatorViewModel: CalculatorViewModel = viewModel()

        Scaffold(
            bottomBar = { BottomNavBar(navController) }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = Screen.CALCULATOR.route,
                modifier = Modifier.padding(padding)
            ) {
                composable(Screen.CALCULATOR.route) {
                    CalculatorScreen(viewModel = calculatorViewModel)
                }
                composable(Screen.CONVERTER.route) {
                    UnitConverterScreen()
                }
                composable(Screen.MATRIX.route) {
                    MatrixScreen()
                }
                composable(Screen.PLOTTER.route) {
                    PlotterScreen()
                }
                composable(Screen.HISTORY.route) {
                    HistoryScreen(
                        viewModel = calculatorViewModel,
                        onItemPicked = {
                            navController.navigate(Screen.CALCULATOR.route) {
                                popUpTo(Screen.CALCULATOR.route) { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable(Screen.SETTINGS.route) {
                    SettingsScreen(
                        themeMode = themeMode,
                        onThemeModeChange = { themeMode = it },
                        hapticEnabled = haptic,
                        onHapticChange = { haptic = it }
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomNavBar(navController: NavHostController) {
    val backStack by navController.currentBackStackEntryAsState()
    val current = backStack?.destination?.route

    NavigationBar {
        Screen.entries.forEach { screen ->
            NavigationBarItem(
                selected = current == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(screen.icon, contentDescription = null) },
                label = { Text(stringResource(screen.labelRes)) }
            )
        }
    }
}
