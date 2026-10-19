package ir.mohandesan.calc.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import ir.mohandesan.calc.R

enum class Screen(
    val route: String,
    val labelRes: Int,
    val icon: ImageVector
) {
    CALCULATOR("calculator", R.string.nav_calculator, Icons.Default.Calculate),
    CONVERTER("converter",  R.string.nav_converter,  Icons.Default.SwapHoriz),
    MATRIX("matrix",         R.string.nav_matrix,    Icons.Default.GridOn),
    PLOTTER("plotter",       R.string.nav_plotter,   Icons.Default.ShowChart),
    HISTORY("history",       R.string.nav_history,   Icons.Default.History),
    SETTINGS("settings",     R.string.nav_settings,  Icons.Default.Settings)
}
