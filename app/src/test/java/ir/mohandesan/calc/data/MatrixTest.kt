package ir.mohandesan.calc.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MatrixTest {

    @Test
    fun `matrix addition`() {
        val a = Matrix(2, 2) { _, _ -> 1.0 }
        val b = Matrix(2, 2) { _, _ -> 2.0 }
        val c = a + b
        for (i in 0 until 2)
            for (j in 0 until 2)
                assertEquals(3.0, c[i, j], 1e-9)
    }

    @Test
    fun `matrix multiplication`() {
        val a = Matrix(2, 3) { i, j -> (i * 3 + j + 1).toDouble() }
        val b = Matrix(3, 2) { i, j -> (i * 2 + j + 1).toDouble() }
        val c = a * b
        assertEquals(2, c.rows)
        assertEquals(2, c.cols)
        // Verify a known cell: row 0, col 0 = 1*1 + 2*3 + 3*5 = 1 + 6 + 15 = 22
        assertEquals(22.0, c[0, 0], 1e-9)
        // row 1, col 1 = 4*2 + 5*4 + 6*6 = 8 + 20 + 36 = 64
        assertEquals(64.0, c[1, 1], 1e-9)
    }

    @Test
    fun `determinant 2x2`() {
        val a = Matrix(2, 2) { i, j ->
            when { i == 0 && j == 0 -> 4.0; i == 0 && j == 1 -> 7.0
                   i == 1 && j == 0 -> 2.0; else -> 6.0 }
        }
        // det = 4*6 - 7*2 = 24 - 14 = 10
        assertEquals(10.0, a.determinant(), 1e-9)
    }

    @Test
    fun `determinant 3x3`() {
        val a = Matrix(3, 3) { i, j -> (i * 3 + j + 1).toDouble() }
        // 1 2 3
        // 4 5 6
        // 7 8 9
        // det = 0 (linearly dependent)
        assertEquals(0.0, a.determinant(), 1e-9)
    }

    @Test
    fun `transpose`() {
        val a = Matrix(2, 3) { i, j -> (i * 3 + j + 1).toDouble() }
        val t = a.transpose()
        assertEquals(3, t.rows)
        assertEquals(2, t.cols)
        assertEquals(1.0, t[0, 0], 1e-9)
        assertEquals(4.0, t[0, 1], 1e-9)
    }

    @Test
    fun `inverse of identity is identity`() {
        val identity = Matrix(3, 3) { i, j -> if (i == j) 1.0 else 0.0 }
        val inv = identity.inverse()
        for (i in 0 until 3)
            for (j in 0 until 3)
                assertEquals(if (i == j) 1.0 else 0.0, inv[i, j], 1e-9)
    }

    @Test
    fun `inverse times original equals identity`() {
        val a = Matrix(2, 2) { i, j ->
            when { i == 0 && j == 0 -> 4.0; i == 0 && j == 1 -> 7.0
                   i == 1 && j == 0 -> 2.0; else -> 6.0 }
        }
        val product = a * a.inverse()
        for (i in 0 until 2)
            for (j in 0 until 2)
                assertEquals(if (i == j) 1.0 else 0.0, product[i, j], 1e-9)
    }

    @Test
    fun `solve linear system`() {
        // 2x + y = 5
        // x + 3y = 10
        val a = Matrix(2, 2) { i, j ->
            when { i == 0 && j == 0 -> 2.0; i == 0 && j == 1 -> 1.0
                   i == 1 && j == 0 -> 1.0; else -> 3.0 }
        }
        val b = Matrix(2, 1) { i, _ -> if (i == 0) 5.0 else 10.0 }
        val x = a.solve(b)!!
        // Solution: x=1, y=3
        assertEquals(1.0, x[0, 0], 1e-9)
        assertEquals(3.0, x[1, 0], 1e-9)
    }

    @Test
    fun `singular matrix returns null solution`() {
        val a = Matrix(2, 2) { i, j ->
            when { i == 0 && j == 0 -> 1.0; i == 0 && j == 1 -> 2.0
                   i == 1 && j == 0 -> 2.0; else -> 4.0 }
        }
        val b = Matrix(2, 1) { _, _ -> 1.0 }
        val x = a.solve(b)
        assertTrue(x == null)
    }
}
