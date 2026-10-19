package ir.mohandesan.calc.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.mohandesan.calc.data.UnitConverter

@Composable
fun UnitConverterScreen() {
    var category by remember { mutableStateOf(UnitConverter.Category.LENGTH) }
    var inputValue by remember { mutableStateOf("") }
    var fromSymbol by remember { mutableStateOf(UnitConverter.units[category]!!.first().symbol) }
    var toSymbol by remember { mutableStateOf(UnitConverter.units[category]!!.last().symbol) }

    val fromList = UnitConverter.units[category]!!
    val toList = fromList

    val result = remember(inputValue, fromSymbol, toSymbol, category) {
        val v = inputValue.toDoubleOrNull()
        if (v != null) {
            val r = UnitConverter.convert(v, category, fromSymbol, toSymbol)
            UnitConverter.roundTo(r).toString()
        } else ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(ir.mohandesan.calc.R.string.nav_converter),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = "Category",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium
        )

        CategoryChips(
            selected = category,
            onSelected = { c ->
                category = c
                fromSymbol = UnitConverter.units[c]!!.first().symbol
                toSymbol = UnitConverter.units[c]!!.last().symbol
            }
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputValue,
                onValueChange = { s ->
                    inputValue = s.filter { c -> c.isDigit() || c == '.' || c == '-' || c == 'e' || c == 'E' }
                },
                label = { Text(stringResource(category.titleRes)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f)
            )
            UnitDropdown(
                items = fromList,
                selected = fromSymbol,
                onSelected = { fromSymbol = it },
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("↓", fontSize = 28.sp, color = MaterialTheme.colorScheme.primary)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = result,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                label = { Text("Result") },
                modifier = Modifier.weight(1f)
            )
            UnitDropdown(
                items = toList,
                selected = toSymbol,
                onSelected = { toSymbol = it },
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            OutlinedButton(onClick = {
                val tmp = fromSymbol
                fromSymbol = toSymbol
                toSymbol = tmp
            }) { Text("Swap ⇄") }
        }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun CategoryChips(
    selected: UnitConverter.Category,
    onSelected: (UnitConverter.Category) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        UnitConverter.Category.entries.forEach { cat ->
            AssistChip(
                onClick = { onSelected(cat) },
                label = { Text(stringResource(cat.titleRes)) },
                colors = if (cat == selected)
                    AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        labelColor = MaterialTheme.colorScheme.onPrimary
                    )
                else AssistChipDefaults.assistChipColors()
            )
        }
    }
}

@Composable
private fun UnitDropdown(
    items: List<UnitConverter.UnitDef>,
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedItem = items.first { it.symbol == selected }

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "${selectedItem.symbol} — ${selectedItem.name}",
                fontSize = 13.sp,
                maxLines = 1
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { u ->
                DropdownMenuItem(
                    text = { Text("${u.symbol} — ${u.name}") },
                    onClick = { onSelected(u.symbol); expanded = false }
                )
            }
        }
    }
}
