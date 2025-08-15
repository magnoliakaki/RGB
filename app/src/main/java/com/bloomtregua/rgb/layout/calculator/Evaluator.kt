package com.bloomtregua.rgb.layout.calculator
import java.util.Stack

fun evaluateExpression(expression: String): String {
    return try {
        val result = simpleEvaluate(expression)
        result.toString()
    } catch (e: Exception) {
        "Error"
    }
}

fun simpleEvaluate(expr: String): Double {
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