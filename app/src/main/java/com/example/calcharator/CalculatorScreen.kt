package com.example.calcharator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.mariuszgromada.math.mxparser.Expression
import org.mariuszgromada.math.mxparser.mXparser


@Composable
fun CalculatorButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, colors: ButtonColors = ButtonDefaults.buttonColors()) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .height(64.dp),
        colors = colors
    ) {
        Text(text = text, fontSize = 20.sp)
    }
}


@Composable
fun CalculatorScreen(navController: NavController, modifier: Modifier = Modifier) {
    var expression by remember { mutableStateOf("0") }
    var cursorPosition by remember { mutableIntStateOf(1) } // start after "0"
    var scientificMode by remember { mutableStateOf(false) }
    var previewResult by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        mXparser.setDegreesMode()
    }

    LaunchedEffect(expression) {
        if (expression.startsWith("Math Error") || expression.startsWith("Syntax Error")) {
            previewResult = ""
            return@LaunchedEffect
        }
        val exp = Expression(expression)
        if (exp.checkSyntax()) {
            val result = exp.calculate()
            if (!result.isNaN()) {
                val resultString = if (result % 1.0 == 0.0) {
                    result.toLong().toString()
                } else {
                    result.toString()
                }
                if (resultString == expression) {
                    previewResult = ""
                } else {
                    previewResult = resultString
                }
            } else {
                previewResult = ""
            }
        } else {
            previewResult = ""
        }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 48.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Calculator", fontSize = 24.sp)
                Button(onClick = {
                    navController.navigate("home")
                }) {
                    Text("📑 Go to Home")
                }
            }
            // --- Toggle ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Scientific Mode", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                Switch(
                    checked = scientificMode,
                    onCheckedChange = { scientificMode = it }
                )
            }

            // --- Expression display with cursor ---
            Row(modifier = Modifier.padding(top = 32.dp)) {
                ClickableTextWithCursor(
                    text = expression,
                    cursorPosition = cursorPosition,
                    onCursorChange = { cursorPosition = it },
                    fontSize = 36.sp
                )
                Text(
                    text = previewResult,
                    fontSize = 24.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // --- Buttons grid ---
        val normalButtons = listOf(
            listOf("7", "8", "9", "/"),
            listOf("4", "5", "6", "*"),
            listOf("1", "2", "3", "-"),
            listOf("0", ".", "+", "DEL"),
            listOf("C", "=") // C for clear
        )

        val scientificButtons = listOf(
            listOf("sin", "cos", "tan", "log"),
            listOf("(", ")", "^", "sqrt")
        )

        val onButtonClick: (String) -> Unit = { label ->
            if ((expression == "0" && label != ".") || expression.startsWith("Math Error") || expression.startsWith("Syntax Error")) {
                expression = label
                cursorPosition = label.length
            } else {
                expression = expression.substring(0, cursorPosition) + label + expression.substring(cursorPosition)
                cursorPosition += label.length
            }
        }

        val onScientificButtonClick: (String) -> Unit = { label ->
            val textToInsert = when (label) {
                "sin", "cos", "tan", "sqrt" -> "$label("
                "log" -> "log10("
                else -> label
            }
            if (expression == "0" || expression.startsWith("Math Error") || expression.startsWith("Syntax Error")) {
                expression = textToInsert
                cursorPosition = textToInsert.length
            } else {
                expression = expression.substring(0, cursorPosition) + textToInsert + expression.substring(cursorPosition)
                cursorPosition += textToInsert.length
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (scientificMode) {
                scientificButtons.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { label ->
                            CalculatorButton(
                                text = label,
                                onClick = { onScientificButtonClick(label) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                            )
                        }
                    }
                }
            }

            normalButtons.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { label ->
                        val buttonColors = when (label) {
                            "+" -> ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            "C", "=" -> ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                            "/", "*", "-", "DEL" -> ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                            else -> ButtonDefaults.buttonColors()
                        }
                        CalculatorButton(
                            text = label,
                            onClick = {
                                when (label) {
                                    "=" -> {
                                        val exp = Expression(expression)
                                        if (exp.checkSyntax()) {
                                            val result = exp.calculate()
                                            if (!result.isNaN()) {
                                                expression = if (result % 1.0 == 0.0) {
                                                    result.toLong().toString()
                                                } else {
                                                    result.toString()
                                                }
                                                cursorPosition = expression.length
                                            } else {
                                                expression = "Math Error"
                                                cursorPosition = expression.length
                                            }
                                        } else {
                                            expression = "Syntax Error: " + exp.errorMessage
                                            cursorPosition = expression.length
                                        }
                                        previewResult = ""
                                    }
                                    "DEL" -> {
                                        if (expression.isNotEmpty() && cursorPosition > 0) {
                                            val deletePosition = cursorPosition - 1
                                            expression = expression.removeRange(deletePosition, cursorPosition)
                                            cursorPosition = deletePosition
                                            if (expression.isEmpty()) {
                                                expression = "0"
                                                cursorPosition = 1
                                            }
                                        }
                                    }
                                    "C" -> {
                                        expression = "0"
                                        cursorPosition = 1
                                    }
                                    else -> onButtonClick(label)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = buttonColors
                        )
                    }
                }
            }
        }
    }
}

