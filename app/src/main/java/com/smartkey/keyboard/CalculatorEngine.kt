package com.smartkey.keyboard

import java.util.Locale
import java.util.Stack

class CalculatorEngine {
    var expression = ""

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

    fun evaluate(): String {
        if (expression.isBlank()) return "0"
        return try {
            val result = evaluate(expression.replace("×", "*").replace("÷", "/").replace("−", "-").replace("±", "-"))
            format(result)
        } catch (e: Exception) {
            expression
        }
    }

    private fun format(v: Double): String {
        if (v == Math.floor(v) && !v.isInfinite() && Math.abs(v) < 1e15) {
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
            expression.isNotBlank() && evaluate(expression.replace("×", "*").replace("÷", "/").replace("−", "-").replace("±", "-")).isFinite()
        } catch (e: Exception) {
            false
        }
    }

    private fun evaluate(input: String): Double {
        val tokens = tokenize(input)
        val output = mutableListOf<Any>()
        val ops = Stack<Char>()
        val precedence = mapOf('+' to 1, '-' to 1, '*' to 2, '/' to 2, '%' to 2, 'u' to 3)
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
                if (ops.isNotEmpty() && (ops.peek() == '*' || ops.peek() == '/' || ops.peek() == 'u')) {
                    output.add(0.01)
                } else if (output.isNotEmpty()) {
                    val top = output.removeAt(output.size - 1) as Double
                    output.add(top / 100.0)
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
                    if (item == 'u') {
                        val a = stack.pop()
                        stack.push(-a)
                    } else {
                        val b = stack.pop()
                        val a = stack.pop()
                        stack.push(apply(a, b, item))
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
                if (c == '×' || c == '÷' || c == '−') {
                    tokens.add(c.toString())
                } else {
                    tokens.add(c.toString())
                }
            }
        }
        if (sb.isNotEmpty()) tokens.add(sb.toString())
        return tokens
    }
}