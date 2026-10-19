package ir.mohandesan.calc.viewmodel

import androidx.lifecycle.ViewModel
import ir.mohandesan.calc.data.AngleMode
import ir.mohandesan.calc.data.CalculatorEngine
import ir.mohandesan.calc.data.EvalResult
import ir.mohandesan.calc.model.HistoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Holds the calculator state: expression, live preview result, history,
 * angle mode, 2nd shift state, and the last error.
 *
 * History is in-memory (limited to 50 entries) — for persistence across
 * app launches wrap this VM with a Room database or DataStore.
 */
class CalculatorViewModel : ViewModel() {

    private val engine = CalculatorEngine(AngleMode.DEG)

    private val _expression = MutableStateFlow("")
    val expression: StateFlow<String> = _expression.asStateFlow()

    private val _preview = MutableStateFlow<String?>(null)
    val preview: StateFlow<String?> = _preview.asStateFlow()

    private val _history = MutableStateFlow<List<HistoryItem>>(emptyList())
    val history: StateFlow<List<HistoryItem>> = _history.asStateFlow()

    private val _angleMode = MutableStateFlow(AngleMode.DEG)
    val angleMode: StateFlow<AngleMode> = _angleMode.asStateFlow()

    private val _secondActive = MutableStateFlow(false)
    val secondActive: StateFlow<Boolean> = _secondActive.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _lastResult = MutableStateFlow<String?>(null)
    val lastResult: StateFlow<String?> = _lastResult.asStateFlow()

    /** Insert text into the expression at the cursor position (end). */
    fun insert(token: String) {
        _error.value = null
        val current = _expression.value
        _expression.value = current + token
        updatePreview()
    }

    /** Delete the last character / token from the expression. */
    fun delete() {
        _error.value = null
        val current = _expression.value
        if (current.isEmpty()) return
        // Try to remove a trailing function call like "sin(" entirely
        val trailingFunc = TRAILING_FUNC_REGEX.find(current)
        if (trailingFunc != null) {
            _expression.value = current.removeRange(trailingFunc.range)
        } else {
            // Remove a unicode/compound token wholesale if matched
            val compound = TRAILING_COMPOUND_REGEX.find(current)
            if (compound != null) {
                _expression.value = current.removeRange(compound.range)
            } else {
                _expression.value = current.dropLast(1)
            }
        }
        updatePreview()
    }

    fun clear() {
        _error.value = null
        _expression.value = ""
        _preview.value = null
        _lastResult.value = null
    }

    /** Toggle the 2nd shift state. */
    fun toggleSecond() {
        _secondActive.value = !_secondActive.value
    }

    /** Cycle angle mode: DEG → RAD → GRAD → DEG. */
    fun cycleAngleMode() {
        _angleMode.value = when (_angleMode.value) {
            AngleMode.DEG -> AngleMode.RAD
            AngleMode.RAD -> AngleMode.GRAD
            AngleMode.GRAD -> AngleMode.DEG
        }
        engine.angleMode = _angleMode.value
        updatePreview()
    }

    /** Evaluate the current expression and store the result. */
    fun evaluate() {
        val expr = _expression.value
        if (expr.isBlank()) return
        engine.angleMode = _angleMode.value
        when (val r = engine.evaluate(expr)) {
            is EvalResult.Success -> {
                _preview.value = null
                _lastResult.value = r.displayString
                _error.value = null
                _history.update { existing ->
                    val item = HistoryItem(
                        id = System.currentTimeMillis(),
                        expression = expr,
                        result = r.displayString,
                        timestamp = System.currentTimeMillis(),
                        angleModeLabel = _angleMode.value.name
                    )
                    (existing + item).takeLast(MAX_HISTORY)
                }
                // Replace expression with result so user can chain further operations
                _expression.value = r.displayString
            }
            is EvalResult.Error -> {
                _error.value = r.message
                _lastResult.value = null
            }
        }
    }

    /** Reuse an old history entry as the new expression. */
    fun loadFromHistory(item: HistoryItem) {
        _expression.value = item.expression
        _lastResult.value = item.result
        updatePreview()
    }

    /** Clear all history. */
    fun clearHistory() {
        _history.value = emptyList()
    }

    /** Replace the entire expression (used for paste / external input). */
    fun setExpression(value: String) {
        _expression.value = value
        updatePreview()
    }

    /** Append the result of the previous calculation back into the expression. */
    fun recallLastResult() {
        val last = _lastResult.value ?: return
        _expression.value = _expression.value + last
        updatePreview()
    }

    private fun updatePreview() {
        val expr = _expression.value
        if (expr.isBlank()) {
            _preview.value = null
            return
        }
        engine.angleMode = _angleMode.value
        // Don't preview if the expression ends with an operator (likely incomplete)
        if (expr.lastOrNull()?.let { it in "+-*/^%×÷−.(," } == true) {
            _preview.value = null
            return
        }
        when (val r = engine.evaluate(expr)) {
            is EvalResult.Success -> _preview.value = r.displayString
            is EvalResult.Error -> _preview.value = null
        }
    }

    companion object {
        private const val MAX_HISTORY = 50
        private val TRAILING_FUNC_REGEX = Regex("(sin\\(|cos\\(|tan\\(|asin\\(|acos\\(|atan\\(|sinh\\(|cosh\\(|tanh\\(|log\\(|ln\\(|sqrt\\(|cbrt\\(|abs\\(|exp\\(|floor\\(|ceil\\(|round\\(|sign\\(|logbase\\(|root\\(|perm\\(|comb\\(|gcd\\(|lcm\\(|mod\\(|gamma\\()$")
        private val TRAILING_COMPOUND_REGEX = Regex("(π|phi|tau|√|×|÷|−|×10\\^)$")
    }
}
