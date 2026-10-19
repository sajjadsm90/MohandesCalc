package ir.mohandesan.calc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.mohandesan.calc.model.KeyDef
import ir.mohandesan.calc.model.KeyType
import ir.mohandesan.calc.ui.theme.appColors

/**
 * A single calculator key. Background color depends on [KeyType] and the
 * active theme via [MaterialTheme.appColors].
 */
@Composable
fun CalculatorButton(
    key: KeyDef,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    secondActive: Boolean = false
) {
    val colors = MaterialTheme.appColors
    val (bg, fg) = when (key.type) {
        KeyType.NUMBER     -> colors.keyNumber    to MaterialTheme.colorScheme.onSurface
        KeyType.OPERATOR   -> colors.keyOperator  to MaterialTheme.colorScheme.primary
        KeyType.FUNCTION   -> colors.keyFunction  to MaterialTheme.colorScheme.onSurface
        KeyType.SCIENTIFIC -> colors.keyFunction  to MaterialTheme.colorScheme.primary
        KeyType.CONTROL    -> colors.historyItem  to MaterialTheme.colorScheme.onSurfaceVariant
        KeyType.ACCENT     -> colors.keyAccent    to Color.White
    }
    val label = if (secondActive && key.altLabel != null) key.altLabel!! else key.label

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = fg,
            fontWeight = if (key.type == KeyType.ACCENT) FontWeight.SemiBold else FontWeight.Medium,
            fontSize = when (key.type) {
                KeyType.NUMBER, KeyType.ACCENT, KeyType.OPERATOR -> 22.sp
                KeyType.FUNCTION, KeyType.SCIENTIFIC -> 16.sp
                KeyType.CONTROL -> 14.sp
            },
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}
