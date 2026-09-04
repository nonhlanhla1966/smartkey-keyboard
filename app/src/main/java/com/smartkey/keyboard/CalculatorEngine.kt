package com.smartkey.keyboard

import java.util.Locale
import java.util.Stack

class CalculatorEngine {
    var expression = ""

    private val _history = ArrayList<HistoryEntry>()
    val history: List<HistoryEntry> get() = _history.toList()
    val hasHistory: Boolean get() = _history.isNotEmpty()

    data class HistoryEntry(val expr: String, val result: String)

    fun clear() {
        expression = ""
    }

    fun append(s: String) {
        if (expression == "0" && s.isNotEmpty() && s[0] in '0'..'9') expression = ""
        expression += s
    }

    fun backspace() {
        if (expression.isNotEmpty()) expression = expression.dropLast(1)
    }

    fun recallHistory(index: Int): HistoryEntry? {
        if (index < 0 || index >= _history.size) return null
        val entry = _history[_history.size - 1 - index]
        expression = entry.result
        return entry
    }

    fun clearHistory() {
        _history.clear()
    }

    fun evaluate(): String {
        if (expression.isBlank()) return "0"
        return try {
            val result = evalExpression(expression)
            val formatted = format(result)
            record(expression, formatted)
            expression = ""
            formatted
        } catch (e: Exception) {
            expression
        }
    }

    private fun record(expr: String, result: String) {
        _history.add(HistoryEntry(expr, result))
        while (_history.size > 50) _history.removeAt(0)
    }

    private fun evalExpression(input: String): Double {
        val normalized = input
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("±", "-")
        return evaluate(normalized)
    }

    private fun format(v: Double): String {
        if (!v.isFinite()) return if (v.isNaN()) "Error" else "∞"
        if (v == Math.floor(v) && Math.abs(v) < 1e15) {
            return v.toLong().toString()
        }
        val s = String.format(Locale.US, "%.10f", v)
        var t = s
        while (t.endsWith("0")) t = t.dropLast(1)
        if (t.endsWith(".")) t = t.dropLast(1)
        return t
    }

    fun formatDisplay(): String = if (expression.isEmpty()) "0" else expression

    fun isValid(): Boolean {
        return try {
            expression.isNotBlank() && evalExpression(expression).isFinite()
        } catch (e: Exception) {
            false
        }
    }

    private fun evaluate(input: String): Double {
        val tokens = tokenize(input)
        val output = mutableListOf<Any>()
        val ops = Stack<Char>()
        val precedence = mapOf(
            '+' to 1, '-' to 1,
            '*' to 2, '/' to 2, '%' to 2,
            '^' to 3,
            'r' to 5,
            'u' to 6
        )
        var expectUnary = true
        for (raw in tokens) {
            val t = raw
            val first = t.first()
            if (first.isDigit() || first == '.') {
                output.add(t.toDouble())
                expectUnary = false
            } else if (first == '(') {
                ops.push('(')
                expectUnary = true
            } else if (first == ')') {
                while (ops.isNotEmpty() && ops.peek() != '(') output.add(ops.pop())
                if (ops.isNotEmpty() && ops.peek() == '(') ops.pop()
                expectUnary = false
            } else if (first == '%') {
                // Postfix percent: divide the last operand by 100 so that
                // "200*15%" evaluates as 200 * 0.15 and "10%" as 0.1.
                if (output.isNotEmpty() && output.last() is Double) {
                    val last = output.size - 1
                    output[last] = (output[last] as Double) / 100.0
                } else {
                    output.add(0.01)
                }
                expectUnary = false
            } else {
                var op = first
                val isUnary = expectUnary && (op == '-' || op == '+')
                if (isUnary) op = 'u'
                while (ops.isNotEmpty() && ops.peek() != '(' && (precedence[ops.peek()] ?: 0) >= (precedence[op] ?: 0)) {
                    output.add(ops.pop())
                }
                ops.push(op)
                expectUnary = true
            }
        }
        while (ops.isNotEmpty()) {
            val top = ops.pop()
            if (top != '(') output.add(top)
        }
        val stack = Stack<Double>()
        for (item in output) {
            when (item) {
                is Double -> stack.push(item)
                is Char -> {
                    when (item) {
                        'u' -> {
                            if (stack.isEmpty()) return Double.NaN
                            val a = stack.pop()
                            stack.push(-a)
                        }
                        'r' -> {
                            if (stack.isEmpty()) return Double.NaN
                            val a = stack.pop()
                            if (a < 0) return Double.NaN
                            stack.push(Math.sqrt(a))
                        }
                        else -> {
                            if (stack.size < 2) return Double.NaN
                            val b = stack.pop()
                            val a = stack.pop()
                            stack.push(apply(a, b, item))
                        }
                    }
                }
            }
        }
        return if (stack.isEmpty()) 0.0 else stack.pop()
    }

    private fun apply(a: Double, b: Double, op: Char): Double = when (op) {
        '+' -> a + b
        '-' -> a - b
        '*' -> a * b
        '/' -> if (b == 0.0) Double.NaN else a / b
        '%' -> a % b
        '^' -> Math.pow(a, b)
        else -> a
    }

    private fun tokenize(input: String): List<String> {
        val tokens = mutableListOf<String>()
        val sb = StringBuilder()
        for (c in input) {
            if (c.isDigit() || c == '.') {
                sb.append(c)
            } else {
                if (sb.isNotEmpty()) {
                    tokens.add(sb.toString())
                    sb.clear()
                }
                if (c == ' ') continue
                when (c) {
                    '√' -> tokens.add("r")
                    '^' -> tokens.add("^")
                    '%' -> tokens.add("%")
                    else -> tokens.add(c.toString())
                }
            }
        }
        if (sb.isNotEmpty()) tokens.add(sb.toString())
        return tokens
    }
}