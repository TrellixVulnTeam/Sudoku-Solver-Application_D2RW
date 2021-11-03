package com.example.sudokusolver

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.sudokusolver.ui.theme.ColorBoxSelected
import com.example.sudokusolver.ui.theme.SudokuSolverTheme
import org.opencv.android.OpenCVLoader

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val verticalLength = 9
        setContent {
            val sudokuBoard: SnapshotStateList<SudokuBoardItem> =
                remember { setupBoard(mockBoard(9)) }
            val (sudokuBoxClicked, setSudokuBoxClicked) = remember { mutableStateOf(0) }
            val history: SnapshotStateList<HistoryItem> = remember { mutableStateListOf() }

            SudokuSolverTheme {
                Surface(
                    color = MaterialTheme.colors.background,
                ) {
                    Column(
                        modifier = Modifier
                            .padding(5.dp)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Header {
                            HistoryIcon()
                        }
                        SudokuBoard(
                            items = sudokuBoard,
                            verticalLength = verticalLength,
                            onItemClick = { index: Int ->
                                handleSudokuItemClicked(
                                    newSudokuBox = index,
                                    currentSudokuBox = sudokuBoxClicked,
                                    setSudokuBox = setSudokuBoxClicked,
                                    sudokuBoard = sudokuBoard
                                )
                            }
                        )
                        ActionMenu(
                            handleActionMenuItems(
                                sudokuItemClicked = sudokuBoxClicked,
                                sudokuBoard = sudokuBoard,
                                history = history,
                                startImageLoadingActivity = {
                                    Intent(
                                        applicationContext,
                                        ImageLoadingActivity::class.java
                                    ).also {
                                        startActivity(it)
                                    }
                                }
                            )
                        )
                        BottomNumbers(handleClick = { numberClicked ->
                            handleBottomNumberClicked(
                                bottomNumber = numberClicked,
                                sudokuBox = sudokuBoxClicked,
                                sudokuBoard = sudokuBoard,
                                history = history
                            )
                        })
                        Spacer(modifier = Modifier.padding(bottom = 10.dp))
                    }
                }
            }
        }
        if (!OpenCVLoader.initDebug())
            Log.e("OpenCV", "Unable to load OpenCV!");
        else
            Log.d("OpenCV", "OpenCV loaded Successfully!");
    }
}

fun setupBoard(items: List<Int>): SnapshotStateList<SudokuBoardItem> {
    val list = mutableStateListOf<SudokuBoardItem>()
    items.forEach {
        list.add(
            SudokuBoardItem(
                number = it,
            )
        )
    }
    return list
}


fun handleBottomNumberClicked(
    sudokuBox: Int,
    bottomNumber: Int,
    sudokuBoard: SnapshotStateList<SudokuBoardItem>,
    history: SnapshotStateList<HistoryItem>
) {
    if (isBoxWithinBoard(sudokuBox, sudokuBoard.size) && isValidSudokuNum(bottomNumber)) {
        history.add(
            HistoryItem(
                newValue = bottomNumber,
                oldValue = sudokuBoard[sudokuBox].number,
                sudokuItem = sudokuBox
            )
        )
        mutateBoard(
            index = sudokuBox,
            board = sudokuBoard,
            number = bottomNumber
        )
    }
}

fun handleSudokuItemClicked(
    newSudokuBox: Int,
    currentSudokuBox: Int,
    setSudokuBox: (Int) -> Unit,
    sudokuBoard: SnapshotStateList<SudokuBoardItem>,
) {
    if (isBoxWithinBoard(newSudokuBox, sudokuBoard.size)) {
        if (newSudokuBox != currentSudokuBox) {
            updateBackgroundColor(newSudokuBox, currentSudokuBox, setSudokuBox, sudokuBoard)
        }
    }
}

fun updateBackgroundColor(
    newSudokuBox: Int,
    currentSudokuBox: Int,
    setSudokuBox: (Int) -> Unit,
    sudokuBoard: SnapshotStateList<SudokuBoardItem>,
) {
    // reset old box
    mutateBoard(
        index = currentSudokuBox,
        board = sudokuBoard,
        backgroundColor = Color.White,
    )

    setSudokuBox(newSudokuBox)

    // update new clicked
    mutateBoard(
        index = newSudokuBox,
        board = sudokuBoard,
        backgroundColor = ColorBoxSelected,
    )

}

fun isBoxWithinBoard(n: Int, boardSize: Int) = n in 0 until boardSize

fun isValidSudokuNum(n: Int) = n in 1..9

// mutateBoard mutates a sudoku item and allows for optional arguments
fun mutateBoard(
    index: Int,
    backgroundColor: Color? = null,
    number: Int? = null,
    board: SnapshotStateList<SudokuBoardItem>,
) {
    board[index] = board[index].copy(
        backgroundColor = backgroundColor ?: board[index].backgroundColor,
        number = number ?: board[index].number
    )
}
