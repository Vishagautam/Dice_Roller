package com.example.diceroller.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DiceRollerState(
    val currentValue: Int = 1,
    val isRolling: Boolean = false,
    val rollHistory: List<Int> = emptyList(),
    val totalRollsCount: Int = 0,
    val totalSum: Int = 0,
    val sessionHighest: Int = 0
) {
    val totalRolls: Int get() = totalRollsCount
    val average: Float get() = if (totalRollsCount == 0) 0f else totalSum.toFloat() / totalRollsCount
    val highest: Int get() = sessionHighest
}

class DiceRollerViewModel : ViewModel() {
    private val _state = MutableStateFlow(DiceRollerState())
    val state: StateFlow<DiceRollerState> = _state.asStateFlow()

    fun rollDice() {
        if (_state.value.isRolling) return

        viewModelScope.launch {
            _state.update { it.copy(isRolling = true) }

            // Animate through 10 random values
            repeat(10) {
                _state.update { it.copy(currentValue = (1..6).random()) }
                delay(80)
            }

            // Settle on a final value
            val finalValue = (1..6).random()
            _state.update { state ->
                state.copy(
                    currentValue = finalValue,
                    isRolling = false,
                    rollHistory = (listOf(finalValue) + state.rollHistory).take(10),
                    totalRollsCount = state.totalRollsCount + 1,
                    totalSum = state.totalSum + finalValue,
                    sessionHighest = maxOf(state.sessionHighest, finalValue)
                )
            }
        }
    }

    fun clearHistory() {
        _state.update { it.copy(rollHistory = emptyList()) }
    }
}
