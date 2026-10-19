package ir.mohandesan.calc.data

/**
 * Matrix operations: addition, subtraction, multiplication, determinant,
 * inverse, transpose, and solving linear systems via Gaussian elimination.
 *
 * All operations are pure and return new Matrix instances.
 */
data class Matrix(val rows: Int, val cols: Int) {

    val data: Array<DoubleArray> = Array(rows) { DoubleArray(cols) }

    constructor(rows: Int, cols: Int, init: (i: Int, j: Int) -> Double) : this(rows, cols) {
        for (i in 0 until rows)
            for (j in 0 until cols)
                data[i][j] = init(i, j)
    }

    operator fun get(i: Int, j: Int): Double = data[i][j]
    operator fun set(i: Int, j: Int, v: Double) { data[i][j] = v }

    fun copy(): Matrix = Matrix(rows, cols) { i, j -> data[i][j] }

    fun isSquare(): Boolean = rows == cols

    operator fun plus(other: Matrix): Matrix {
        requireSameShape(other)
        return Matrix(rows, cols) { i, j -> data[i][j] + other[i, j] }
    }

    operator fun minus(other: Matrix): Matrix {
        requireSameShape(other)
        return Matrix(rows, cols) { i, j -> data[i][j] - other[i, j] }
    }

    operator fun times(other: Matrix): Matrix {
        require(cols == other.rows) { "Matrix shape mismatch for multiplication: $cols vs ${other.rows}" }
        return Matrix(rows, other.cols) { i, j ->
            var sum = 0.0
            for (k in 0 until cols) sum += data[i][k] * other[k, j]
            sum
        }
    }

    fun transpose(): Matrix = Matrix(cols, rows) { i, j -> data[j][i] }

    /** Determinant via LU decomposition with partial pivoting. */
    fun determinant(): Double {
        require(isSquare()) { "Determinant only defined for square matrices" }
        if (rows == 1) return data[0][0]
        if (rows == 2) return data[0][0] * data[1][1] - data[0][1] * data[1][0]

        val lu = Array(rows) { DoubleArray(cols) { data[it][it] } }
        for (i in 0 until rows) lu[i] = data[i].copyOf()
        var det = 1.0
        for (p in 0 until rows) {
            // pivot
            var max = p
            for (i in p + 1 until rows) if (abs(lu[i][p]) > abs(lu[max][p])) max = i
            if (max != p) {
                val tmp = lu[p]; lu[p] = lu[max]; lu[max] = tmp
                det = -det
            }
            if (lu[p][p] == 0.0) return 0.0
            det *= lu[p][p]
            for (i in p + 1 until rows) {
                val factor = lu[i][p] / lu[p][p]
                for (j in p until cols) lu[i][j] -= factor * lu[p][j]
            }
        }
        return det
    }

    /** Inverse via Gauss-Jordan elimination on augmented matrix [A | I]. */
    fun inverse(): Matrix {
        require(isSquare()) { "Inverse only defined for square matrices" }
        val n = rows
        val aug = Array(n) { DoubleArray(2 * n) }
        for (i in 0 until n) {
            for (j in 0 until n) aug[i][j] = data[i][j]
            aug[i][n + i] = 1.0
        }
        for (p in 0 until n) {
            var max = p
            for (i in p + 1 until n) if (abs(aug[i][p]) > abs(aug[max][p])) max = i
            if (max != p) { val t = aug[p]; aug[p] = aug[max]; aug[max] = t }
            require(aug[p][p] != 0.0) { "Matrix is singular (not invertible)" }
            val pivot = aug[p][p]
            for (j in 0 until 2 * n) aug[p][j] /= pivot
            for (i in 0 until n) {
                if (i == p) continue
                val factor = aug[i][p]
                for (j in 0 until 2 * n) aug[i][j] -= factor * aug[p][j]
            }
        }
        return Matrix(n, n) { i, j -> aug[i][j + n] }
    }

    /**
     * Solve Ax = b for square A. Returns null if system is singular or shapes mismatch.
     */
    fun solve(b: Matrix): Matrix? {
        require(isSquare() && rows == b.rows && b.cols == 1) { "Solve shape mismatch" }
        val n = rows
        val aug = Array(n) { DoubleArray(n + 1) }
        for (i in 0 until n) {
            for (j in 0 until n) aug[i][j] = data[i][j]
            aug[i][n] = b[i, 0]
        }
        for (p in 0 until n) {
            var max = p
            for (i in p + 1 until n) if (abs(aug[i][p]) > abs(aug[max][p])) max = i
            if (max != p) { val t = aug[p]; aug[p] = aug[max]; aug[max] = t }
            if (aug[p][p] == 0.0) return null
            for (i in p + 1 until n) {
                val factor = aug[i][p] / aug[p][p]
                for (j in p..n) aug[i][j] -= factor * aug[p][j]
            }
        }
        val x = Matrix(n, 1)
        for (i in n - 1 downTo 0) {
            var s = aug[i][n]
            for (j in i + 1 until n) s -= aug[i][j] * x[j, 0]
            x[i, 0] = s / aug[i][i]
        }
        return x
    }

    fun format(precision: Int = 4): String {
        val sb = StringBuilder()
        for (i in 0 until rows) {
            sb.append("[ ")
            for (j in 0 until cols) {
                sb.append("%.${precision}g".format(data[i][j]))
                if (j < cols - 1) sb.append("  ")
            }
            sb.append(" ]\n")
        }
        return sb.toString().trimEnd()
    }

    private fun requireSameShape(other: Matrix) {
        require(rows == other.rows && cols == other.cols) {
            "Matrix shape mismatch: ${rows}x$cols vs ${other.rows}x${other.cols}"
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Matrix) return false
        if (rows != other.rows || cols != other.cols) return false
        for (i in 0 until rows)
            for (j in 0 until cols)
                if (data[i][j] != other[i, j]) return false
        return true
    }

    override fun hashCode(): Int {
        var result = rows
        result = 31 * result + cols
        result = 31 * result + data.contentDeepHashCode()
        return result
    }
}
