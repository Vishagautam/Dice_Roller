package com.example.diceroller.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.ui.tooling.preview.Preview
import com.example.diceroller.theme.DiceRollerTheme

@Composable
fun DiceRollerScreen(
    viewModel: DiceRollerViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF4F46E5), // indigo-600
            Color(0xFF7C3AED), // violet-600
            Color(0xFFDB2777)  // pink-600
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            // Header
            Text(
                text = "🎲 Dice Roller",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (state.isRolling) "Rolling..." else "Tap the dice to roll!",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.75f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            // Dice + value
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AnimatedDice(
                        value = state.currentValue,
                        isRolling = state.isRolling,
                        onClick = { viewModel.rollDice() }
                    )

                    Spacer(Modifier.height(24.dp))

                    // Current value number
                    val valueScale by animateFloatAsState(
                        targetValue = if (state.isRolling) 1.2f else 1f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "valueScale"
                    )
                    Text(
                        text = "${state.currentValue}",
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        modifier = Modifier.scale(valueScale)
                    )
                }
            }

            // Stats panel
            if (state.rollHistory.isNotEmpty()) {
                StatsPanel(state = state)
                Spacer(Modifier.height(12.dp))
            }

            // Roll History
            HistoryPanel(
                history = state.rollHistory,
                onClear = { viewModel.clearHistory() }
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun AnimatedDice(
    value: Int,
    isRolling: Boolean,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "diceRoll")

    val rotation by if (isRolling) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(400, easing = LinearEasing)
            ),
            label = "rotation"
        )
    } else {
        animateFloatAsState(0f, label = "noRotation")
    }

    val scale by animateFloatAsState(
        targetValue = if (isRolling) 1.08f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "diceScale"
    )

    Box(
        modifier = Modifier
            .size(160.dp)
            .scale(scale)
            .rotate(rotation)
            .shadow(
                elevation = 24.dp,
                shape = RoundedCornerShape(32.dp),
                ambientColor = Color(0xFF000000).copy(alpha = 0.4f),
                spotColor = Color(0xFF000000).copy(alpha = 0.4f)
            )
            .clip(RoundedCornerShape(32.dp))
            .background(Color.White)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = !isRolling,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            drawDiceDots(value = value)
        }
    }
}

fun DrawScope.drawDiceDots(value: Int) {
    val dotRadius = size.minDimension * 0.09f
    val dotColor = Color(0xFF1E1B4B) // deep indigo
    val w = size.width
    val h = size.height

    val positions: List<Offset> = when (value) {
        1 -> listOf(Offset(w / 2f, h / 2f))
        2 -> listOf(Offset(w * 0.25f, h * 0.25f), Offset(w * 0.75f, h * 0.75f))
        3 -> listOf(Offset(w * 0.25f, h * 0.25f), Offset(w / 2f, h / 2f), Offset(w * 0.75f, h * 0.75f))
        4 -> listOf(
            Offset(w * 0.25f, h * 0.25f), Offset(w * 0.75f, h * 0.25f),
            Offset(w * 0.25f, h * 0.75f), Offset(w * 0.75f, h * 0.75f)
        )
        5 -> listOf(
            Offset(w * 0.25f, h * 0.25f), Offset(w * 0.75f, h * 0.25f),
            Offset(w / 2f, h / 2f),
            Offset(w * 0.25f, h * 0.75f), Offset(w * 0.75f, h * 0.75f)
        )
        6 -> listOf(
            Offset(w * 0.25f, h * 0.22f), Offset(w * 0.75f, h * 0.22f),
            Offset(w * 0.25f, h / 2f),  Offset(w * 0.75f, h / 2f),
            Offset(w * 0.25f, h * 0.78f), Offset(w * 0.75f, h * 0.78f)
        )
        else -> emptyList()
    }

    positions.forEach { offset ->
        drawCircle(color = dotColor, radius = dotRadius, center = offset)
    }
}

@Composable
fun StatsPanel(state: DiceRollerState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.18f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(label = "Rolls", value = "${state.totalRolls}")
            StatItem(label = "Average", value = "%.1f".format(state.average))
            StatItem(label = "Highest", value = "${state.highest}")
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.75f)
        )
    }
}

@Composable
fun HistoryPanel(
    history: List<Int>,
    onClear: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.18f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Roll History",
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    fontSize = 16.sp
                )
                if (history.isNotEmpty()) {
                    TextButton(onClick = onClear) {
                        Text(
                            text = "Clear",
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            if (history.isEmpty()) {
                Text(
                    text = "No rolls yet. Start rolling!",
                    color = Color.White.copy(alpha = 0.55f),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    itemsIndexed(history) { _, value ->
                        HistoryChip(value = value)
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryChip(value: Int) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "chipScale"
    )

    Box(
        modifier = Modifier
            .size(44.dp)
            .scale(scale)
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$value",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color(0xFF4F46E5)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DiceRollerScreenPreview() {
    DiceRollerTheme {
        DiceRollerScreen()
    }
}

@Preview
@Composable
fun HistoryChipPreview() {
    DiceRollerTheme {
        HistoryChip(value = 5)
    }
}
