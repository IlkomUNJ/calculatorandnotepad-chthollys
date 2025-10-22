package com.example.calcharator

import android.R
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calcharator.ui.theme.CalcharatorTheme
import org.mariuszgromada.math.mxparser.Expression
import org.mariuszgromada.math.mxparser.mXparser
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalcharatorTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable(route = "home") {
            HomeScreen(navController = navController)
        }

        composable(route = "calculator") {
            CalculatorScreen(navController = navController)
        }

        // Define the "profile" screen destination
        composable(route = "text-editor") {
            TextEditorScreen(navController = navController)
        }
    }
}
@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🏠 Home Screen", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            navController.navigate("calculator")
        }) {
            Text("📱 Go to Calculator")
        }
        Button(onClick = {
            navController.navigate("text-editor")
        }) {
            Text("📑 Go to text editor")
        }
    }
}

@Composable
fun TextEditorScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 48.dp)) {
        Column(modifier = Modifier.height(IntrinsicSize.Min)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Text Editor", fontSize = 24.sp)
                Button(onClick = {
                    navController.navigate("home")
                }) {
                    Text("📑 Go to Home")
                }
            }
        }
        Column(modifier = Modifier.fillMaxSize().padding(top = 32.dp)) {
            val paragraphs =
                listOf<String>(
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec iaculis risus eget tortor viverra, rhoncus bibendum dolor ultricies. Quisque ac dolor dictum, consectetur leo non, pretium dui. Phasellus mollis sapien nec nulla tincidunt, in mollis augue commodo. Nullam accumsan tellus eu purus imperdiet, ut vestibulum eros imperdiet. Ut eget finibus lorem. Proin commodo dolor ut ultrices dapibus. Nam volutpat, felis ornare varius efficitur, est turpis cursus neque, ac imperdiet lectus lacus id risus.", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec iaculis risus eget tortor viverra, rhoncus bibendum dolor ultricies. Quisque ac dolor dictum, consectetur leo non, pretium dui. Phasellus mollis sapien nec nulla tincidunt, in mollis augue commodo. Nullam accumsan tellus eu purus imperdiet, ut vestibulum eros imperdiet. Ut eget finibus lorem. Proin commodo dolor ut ultrices dapibus. Nam volutpat, felis ornare varius efficitur, est turpis cursus neque, ac imperdiet lectus lacus id risus.\n\n"
                , "Ut dictum rutrum tellus nec facilisis. Nunc pharetra molestie odio. Sed commodo, quam efficitur posuere sagittis, elit turpis dapibus est, ac luctus tellus nulla in justo. Praesent dignissim tincidunt diam, et suscipit elit interdum vitae. Fusce ex lorem, pretium eget sapien vel, ullamcorper dapibus mauris. Nunc aliquet, justo at ornare feugiat, purus diam varius mi, id vulputate massa arcu id elit. Proin ex orci, tincidunt a augue id, pretium ornare mi. Aenean suscipit non sapien malesuada commodo. Integer velit nisl, bibendum non interdum vel, feugiat a elit. Aliquam in lacinia ante. Aenean sapien augue, fringilla sed justo nec, tincidunt tristique est. Donec ullamcorper sem mauris, eu tristique risus laoreet ut. Duis et mollis diam, id sollicitudin ipsum. Vivamus ac molestie augue, at dapibus nisi.\n" +
                            "\n")
            paragraphs.forEach { text -> Text(text = text, fontSize = 16.sp)}
        }
    }
}

@Composable
fun CalculatorScreen(navController: NavController, modifier: Modifier = Modifier) {
    var expression by remember { mutableStateOf("0") }
    var cursorPosition by remember { mutableStateOf(1) } // start after "0"
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

@Composable
fun ClickableTextWithCursor(
    text: String,
    cursorPosition: Int,
    onCursorChange: (Int) -> Unit,
    fontSize: TextUnit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cursor-blink")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2000
                1f at 0
                1f at 999
                0f at 1000
                0f at 1999
            },
            repeatMode = RepeatMode.Restart
        ),        label = "cursor-alpha"
    )

    // Break text into left and right with a "|" as cursor indicator
    val left = text.take(cursorPosition)
    val right = text.drop(cursorPosition)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clickable { /* whole row clickable */ },
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Render each character clickable for cursor positioning
        left.forEachIndexed { i, c ->
            Text(
                text = c.toString(),
                fontSize = fontSize,
                modifier = Modifier.clickable { onCursorChange(i + 1) }
            )
        }

        // Cursor indicator
        Box(
            modifier = Modifier
                .width(2.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.primary.copy(alpha = cursorAlpha))
                .clickable { onCursorChange(left.length) }
        )

        right.forEachIndexed { i, c ->
            Text(
                text = c.toString(),
                fontSize = fontSize,
                modifier = Modifier.clickable { onCursorChange(left.length + i + 1) }
            )
        }
    }
}

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

@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    CalcharatorTheme {
        CalculatorScreen(navController = rememberNavController())
    }
}
