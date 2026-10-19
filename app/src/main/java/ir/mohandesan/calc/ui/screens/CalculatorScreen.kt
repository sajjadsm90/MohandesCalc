package ir.mohandesan.calc.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.mohandesan.calc.model.KeypadLayout
import ir.mohandesan.calc.model.KeyAction
import ir.mohandesan.calc.ui.components.CalculatorButton
import ir.mohandesan.calc.ui.components.CalculatorDisplay
import ir.mohandesan.calc.viewmodel.CalculatorViewModel

@Composable
fun CalculatorScreen(viewModel: CalculatorViewModel = viewModel()) {
    val expression by viewModel.expression.collectAsState()
    val preview by viewModel.preview.collectAsState()
    val lastResult by viewModel.lastResult.collectAsState()
    val error by viewModel.error.collectAsState()
    val angleMode by viewModel.angleMode.collectAsState()
    val secondActive by viewModel.secondActive.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CalculatorDisplay(
            expression = expression,
            preview = preview,
            result = lastResult,
            angleLabel = KeypadLayout.angleLabel(angleMode),
            error = error,
            modifier = Modifier.weight(1f)
        )

        KeypadLayout.primaryRows().forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                row.forEach { key ->
                    val weight = if (key.action == KeyAction.EVALUATE) 1.5f else 1f
                    CalculatorButton(
                        key = key,
                        secondActive = secondActive,
                        onClick = {
                            when (key.action) {
                                KeyAction.CLEAR -> viewModel.clear()
                                KeyAction.DELETE -> viewModel.delete()
                                KeyAction.EVALUATE -> viewModel.evaluate()
                                KeyAction.TOGGLE_2ND -> viewModel.toggleSecond()
                                KeyAction.TOGGLE_ANGLE -> viewModel.cycleAngleMode()
                                null -> {
                                    val insert = KeypadLayout.insertForKey(key, secondActive)
                                    if (insert != null) viewModel.insert(insert)
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(weight)
                            .height(64.dp)
                    )
                }
            }
        }
    }
}
