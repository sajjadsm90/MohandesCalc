package ir.mohandesan.calc.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.mohandesan.calc.data.Matrix

@Composable
fun MatrixScreen() {
    var rowsA by remember { mutableStateOf(2) }
    var colsA by remember { mutableStateOf(2) }
    var rowsB by remember { mutableStateOf(2) }
    var colsB by remember { mutableStateOf(2) }
    var cellsA by remember { mutableStateOf(MutableMatrix(2, 2)) }
    var cellsB by remember { mutableStateOf(MutableMatrix(2, 2)) }
    var resultText by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }

    fun resizeA(r: Int, c: Int) {
        rowsA = r; colsA = c
        cellsA = cellsA.resizedTo(r, c)
    }
    fun resizeB(r: Int, c: Int) {
        rowsB = r; colsB = c
        cellsB = cellsB.resizedTo(r, c)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Matrix Calculator",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Text("Matrix A (${rowsA}×${colsA})", style = MaterialTheme.typography.titleSmall)
        SizeSelector(rowsA, colsA, ::resizeA)
        MatrixEditor(cellsA, onEdit = { i, j, v -> cellsA = cellsA.with(i, j, v) })

        Spacer(Modifier.height(4.dp))

        Text("Matrix B (${rowsB}×${colsB})", style = MaterialTheme.typography.titleSmall)
        SizeSelector(rowsB, colsB, ::resizeB)
        MatrixEditor(cellsB, onEdit = { i, j, v -> cellsB = cellsB.with(i, j, v) })

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            MatrixOpButton("A+B", Modifier.weight(1f)) {
                resultText = runOp(rowsA, colsA, rowsB, colsB, cellsA, cellsB, { e -> errorText = e; resultText = "" }) { a, b -> a + b }
            }
            MatrixOpButton("A−B", Modifier.weight(1f)) {
                resultText = runOp(rowsA, colsA, rowsB, colsB, cellsA, cellsB, { e -> errorText = e; resultText = "" }) { a, b -> a - b }
            }
            MatrixOpButton("A×B", Modifier.weight(1f)) {
                if (colsA != rowsB) {
                    errorText = "Cols of A must equal rows of B for multiplication"
                    resultText = ""
                } else {
                    resultText = runOp(rowsA, colsA, rowsB, colsB, cellsA, cellsB, { e -> errorText = e; resultText = "" }) { a, b -> a * b }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            MatrixOpButton("det(A)", Modifier.weight(1f)) {
                if (rowsA != colsA) {
                    errorText = "Determinant needs square matrix"
                    resultText = ""
                } else {
                    try {
                        val m = toMatrix(cellsA)
                        resultText = "%.6g".format(m.determinant())
                        errorText = ""
                    } catch (e: Exception) { errorText = e.message ?: "Error"; resultText = "" }
                }
            }
            MatrixOpButton("Aᵀ", Modifier.weight(1f)) {
                try {
                    val m = toMatrix(cellsA).transpose()
                    resultText = m.format()
                    errorText = ""
                } catch (e: Exception) { errorText = e.message ?: "Error"; resultText = "" }
            }
            MatrixOpButton("A⁻¹", Modifier.weight(1f)) {
                if (rowsA != colsA) {
                    errorText = "Inverse needs square matrix"
                    resultText = ""
                } else {
                    try {
                        val m = toMatrix(cellsA).inverse()
                        resultText = m.format()
                        errorText = ""
                    } catch (e: Exception) { errorText = e.message ?: "Error"; resultText = "" }
                }
            }
        }

        if (errorText.isNotBlank()) {
            Text(errorText, color = MaterialTheme.colorScheme.error)
        }
        if (resultText.isNotBlank()) {
            Text(
                text = "Result:\n$resultText",
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun runOp(
    rowsA: Int, colsA: Int, rowsB: Int, colsB: Int,
    cellsA: MutableMatrix, cellsB: MutableMatrix,
    onError: (String) -> Unit,
    op: (Matrix, Matrix) -> Matrix
): String {
    return try {
        if (rowsA != rowsB || colsA != colsB) {
            onError("Shapes mismatch: A is ${rowsA}×${colsA}, B is ${rowsB}×${colsB}")
            return ""
        }
        val m = op(toMatrix(cellsA), toMatrix(cellsB))
        m.format()
    } catch (e: Exception) {
        onError(e.message ?: "Error")
        ""
    }
}

@Composable
private fun MatrixOpButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Text(text, fontSize = 13.sp, maxLines = 1)
    }
}

@Composable
private fun SizeSelector(rows: Int, cols: Int, onChange: (Int, Int) -> Unit) {
    val sizes = listOf(2, 3, 4)
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("Rows:", modifier = Modifier.padding(top = 8.dp))
        sizes.forEach { r ->
            OutlinedButton(onClick = { onChange(r, cols) }, enabled = r != rows) { Text("$r") }
        }
        Spacer(Modifier.weight(1f))
        Text("Cols:", modifier = Modifier.padding(top = 8.dp))
        sizes.forEach { c ->
            OutlinedButton(onClick = { onChange(rows, c) }, enabled = c != cols) { Text("$c") }
        }
    }
}

@Composable
private fun MatrixEditor(m: MutableMatrix, onEdit: (Int, Int, String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        for (i in 0 until m.rows) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                for (j in 0 until m.cols) {
                    OutlinedTextField(
                        value = m.cells[i][j],
                        onValueChange = { onEdit(i, j, it.filter { c -> c.isDigit() || c == '.' || c == '-' }) },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace)
                    )
                }
            }
        }
    }
}

internal data class MutableMatrix(val rows: Int, val cols: Int, val cells: List<List<String>>) {
    constructor(r: Int, c: Int) : this(r, c, List(r) { List(c) { "" } })
    fun with(i: Int, j: Int, v: String): MutableMatrix {
        val new = cells.map { it.toMutableList() }.toMutableList()
        new[i][j] = v
        return copy(cells = new.map { it.toList() })
    }
    fun resizedTo(newR: Int, newC: Int): MutableMatrix {
        val new = List(newR) { i -> List(newC) { j ->
            if (i < rows && j < cols) cells[i][j] else ""
        } }
        return copy(rows = newR, cols = newC, cells = new)
    }
}

private fun toMatrix(m: MutableMatrix): Matrix {
    val matrix = Matrix(m.rows, m.cols)
    for (i in 0 until m.rows) {
        for (j in 0 until m.cols) {
            matrix[i, j] = m.cells[i][j].ifBlank { "0" }.toDoubleOrNull() ?: 0.0
        }
    }
    return matrix
}
