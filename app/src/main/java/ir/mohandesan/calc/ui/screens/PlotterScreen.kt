package ir.mohandesan.calc.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.mohandesan.calc.data.AngleMode
import ir.mohandesan.calc.data.FunctionEvaluator
import androidx.compose.foundation.text.KeyboardOptions

@Composable
fun PlotterScreen() {
    var expression by remember { mutableStateOf("sin(x)") }
    var xMin by remember { mutableStateOf("-6.28") }
    var xMax by remember { mutableStateOf("6.28") }
    var plotted by remember { mutableStateOf<List<Pair<Double, Double?>>>(emptyList()) }
    var yRange by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var error by remember { mutableStateOf("") }

    val evaluator = remember { FunctionEvaluator(AngleMode.RAD) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Function Plotter",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        OutlinedTextField(
            value = expression,
            onValueChange = { expression = it },
            label = { Text("f(x) — e.g. sin(x)*x") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = xMin,
                onValueChange = { xMin = it.filter { c -> c.isDigit() || c == '.' || c == '-' } },
                label = { Text("x min") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = xMax,
                onValueChange = { xMax = it.filter { c -> c.isDigit() || c == '.' || c == '-' } },
                label = { Text("x max") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = {
                    val xn = xMin.toDoubleOrNull() ?: -2 * Math.PI
                    val xx = xMax.toDoubleOrNull() ?: 2 * Math.PI
                    if (xx <= xn) {
                        error = "x max must be greater than x min"
                        plotted = emptyList()
                        yRange = null
                        return@OutlinedButton
                    }
                    val pts = evaluator.sample(expression, xn, xx, samples = 400)
                    val yr = evaluator.autoYRange(pts)
                    if (yr == null) {
                        error = "Could not evaluate function. Check syntax."
                        plotted = emptyList()
                        yRange = null
                    } else {
                        error = ""
                        plotted = pts
                        yRange = yr
                    }
                },
                modifier = Modifier.weight(1f)
            ) { Text("Plot") }

            OutlinedButton(
                onClick = {
                    expression = "sin(x)"
                    xMin = "-6.28"
                    xMax = "6.28"
                },
                modifier = Modifier.weight(1f)
            ) { Text("Reset") }
        }

        if (error.isNotBlank()) {
            Text(error, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(8.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            if (plotted.isEmpty() || yRange == null) return@Canvas
            drawPlot(plotted, xMin.toDouble(), xMax.toDouble(), yRange!!.first, yRange!!.second)
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPlot(
    points: List<Pair<Double, Double?>>,
    xMin: Double,
    xMax: Double,
    yMin: Double,
    yMax: Double
) {
    val w = size.width
    val h = size.height
    val xRange = xMax - xMin
    val yRange = yMax - yMin
    if (xRange == 0.0 || yRange == 0.0) return

    fun mapX(x: Double): Float = ((x - xMin) / xRange * w).toFloat()
    fun mapY(y: Double): Float = (h - ((y - yMin) / yRange * h)).toFloat()

    val axisColor = Color(0xFF888888)
    val gridColor = Color(0xFF2C3036).copy(alpha = 0.3f)
    val curveColor = Color(0xFF1E6FFF)
    val textColor = Color.White.copy(alpha = 0.9f)

    // Grid lines
    val xGridCount = 10
    val yGridCount = 8
    for (i in 0..xGridCount) {
        val x = (w * i / xGridCount)
        drawLine(gridColor, Offset(x, 0f), Offset(x, h), strokeWidth = 1f)
    }
    for (i in 0..yGridCount) {
        val y = (h * i / yGridCount)
        drawLine(gridColor, Offset(0f, y), Offset(w, y), strokeWidth = 1f)
    }

    // X axis (y = 0)
    if (yMin < 0 && yMax > 0) {
        val y0 = mapY(0.0)
        drawLine(axisColor, Offset(0f, y0), Offset(w, y0), strokeWidth = 2f)
    }
    // Y axis (x = 0)
    if (xMin < 0 && xMax > 0) {
        val x0 = mapX(0.0)
        drawLine(axisColor, Offset(x0, 0f), Offset(x0, h), strokeWidth = 2f)
    }

    // The curve
    val path = Path()
    var penDown = false
    for ((x, y) in points) {
        if (y == null || y.isNaN() || y.isInfinite()) {
            penDown = false
            continue
        }
        // Clip extreme vertical jumps to avoid weird lines
        if (y > yMax + 2 * yRange || y < yMin - 2 * yRange) {
            penDown = false
            continue
        }
        val px = mapX(x)
        val py = mapY(y)
        if (!penDown) {
            path.moveTo(px, py)
            penDown = true
        } else {
            path.lineTo(px, py)
        }
    }
    drawPath(path, color = curveColor, style = Stroke(width = 3f))
}
