package com.bloomtregua.rgb.layout.calculator
import android.util.Log
import java.util.Stack
import kotlin.math.roundToInt
import kotlin.text.format
import java.util.Locale

fun evaluateExpression(
    expression: String,
    preferredLocale: Locale): String {
    return try {
        var result = simpleEvaluate(expression)
        result = (result * 100.00).roundToInt() / 100.00
        if (result < 0) {
            result = 0.0
        }
        String.format(preferredLocale, "%.2f", result)
    } catch (e: Exception) {
        "Error"
    }
}

fun simpleEvaluate(expr: String): Double {
    Log.d("CalculatorScreen", "Evaluating: $expr")

    val numbers = Stack<Double>()
    val operations = Stack<Char>()
    var i = 0
    while (i < expr.length) {
        when (val ch = expr[i]) {
            in '0'..'9', '.' -> {
                val sb = StringBuilder()
                while (i < expr.length && (expr[i].isDigit() || expr[i] == '.')) {
                    sb.append(expr[i])
                    i++
                }
                numbers.push(sb.toString().toDouble())
                continue
            }
            '+', '-', '*', '/' -> {
                while (operations.isNotEmpty() && hasPrecedence(ch, operations.peek())) {
                    val b = numbers.pop()
                    val a = numbers.pop()
                    val op = operations.pop()
                    numbers.push(applyOp(op, a, b))
                }
                operations.push(ch)
            }
        }
        i++
    }

    while (operations.isNotEmpty()) {
        val b = numbers.pop()
        val a = numbers.pop()
        val op = operations.pop()
        numbers.push(applyOp(op, a, b))
    }

    return numbers.pop()
}

fun hasPrecedence(op1: Char, op2: Char): Boolean {
    if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) return false
    return true
}

fun applyOp(op: Char, a: Double, b: Double): Double {
    return when (op) {
        '+' -> a + b
        '-' -> a - b
        '*' -> a * b
        '/' -> a / b
        else -> throw UnsupportedOperationException("Unknown operator: $op")
    }
}