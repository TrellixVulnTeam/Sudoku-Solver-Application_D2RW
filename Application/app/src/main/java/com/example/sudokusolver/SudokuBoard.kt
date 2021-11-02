package com.example.sudokusolver

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp


data class SudokuBoardItem(
    val number: Int,
    val backgroundColor: Color = Color.White
)

fun mockBoard(verticalLength: Int): MutableList<Int> {
    val board: MutableList<Int> = mutableListOf()
    for (i in 0 until verticalLength * verticalLength) {
        board.add(i % 10)
    }
    return board
}

@Composable
fun SudokuBoard(
    items: List<SudokuBoardItem>,
    verticalLength: Int,
    onItemClick: (index: Int) -> Unit
) {
    validateBoard(items.map { it.number }, verticalLength)
    var currentItem = 0

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column {
            for (row in verticalLength downTo 1) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (rowItem in 0 until verticalLength) {
                        SudokuBoxItem(
                            index = currentItem,
                            item = items[currentItem],
                            onItemClick = onItemClick
                        )
                        currentItem++
                    }
                }
            }
        }
    }
}


// TODO: figure out a nice way to handle error and display them to user
fun validateBoard(items: List<Int>, verticalLength: Int) {
    if (verticalLength == 0) {
        throw Error("board length cannot be 0")
    }

    if (!isAxesSameNumber(items, verticalLength)) {
        throw Error("The board can only be 9x9 or 6x6")
    }
    if (!isValidSudokuNumbers(items)) {
        throw Error("accepted numbers are 0-9")
    }
}

// The board can only be 9x9 or 6x6.
fun isAxesSameNumber(items: List<Int>, verticalLength: Int) =
    items.size / verticalLength == verticalLength

// 0-9 is the only numbers accepted where 0 is empty/blank
fun isValidSudokuNumbers(items: List<Int>): Boolean = items.all { it in 0..9 }


@Composable
fun RowScope.SudokuBoxItem(
    index: Int,
    item: SudokuBoardItem,
    onItemClick: (index: Int) -> Unit
) {
    Box(
        modifier = Modifier
            .border(
                // TODO: fix color on different modes https://developer.android.com/jetpack/compose/themes/material
                border = BorderStroke(1.dp, Color.Black),
                shape = RectangleShape
            )
            .fillMaxWidth()
            .aspectRatio(1f)
            .weight(1f)
            .clickable { onItemClick(index) }
            .background(item.backgroundColor)
    ) {
        Text(
            text = if (item.number == 0) "" else item.number.toString(),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}