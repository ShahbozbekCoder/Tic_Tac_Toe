package com.shahbozbek.tictactoe

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun GameScreen(player: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .width(180.dp)
                    .height(60.dp),
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
                content = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .background(Color(0xFF735C00)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Tic Tac Toe",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }
        Spacer(modifier = Modifier.height(64.dp))
        Text(
            text = "Player's Turn",
            color = Color.Black,
            fontSize = 34.sp
        )
        Spacer(modifier = Modifier.height(32.dp))
        TicTacToeGame()
    }
}

@Composable
fun TicTacToeGame() {
    var boardState by remember { mutableStateOf(Array(3) { Array(3) { "" } }) }
    var currentPlayer by remember { mutableStateOf("X") }
    var winner by remember { mutableStateOf<String?>(null) }
    var winningCells by remember { mutableStateOf<List<Pair<Int, Int>>?>(null) }
    var isGameOverDialogVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (isGameOverDialogVisible) {
        GameOverBottomSheet(
            winner = winner,
            onRestart = {
                boardState = Array(3) { Array(3) { "" } }
                currentPlayer = "X"
                winner = null
                winningCells = null
                isGameOverDialogVisible = false
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = winner?.let { "Winner: $it" } ?: "Current Turn: $currentPlayer",
            modifier = Modifier
                .padding(16.dp)
        )

        // O‘yin taxtasi
        boardState.forEachIndexed { rowIndex, row ->
            Row {
                row.forEachIndexed { colIndex, cell ->
                    val isWinningCell = winningCells?.contains(Pair(rowIndex, colIndex)) == true
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .padding(4.dp)
                            .background(
                                if (isWinningCell) Color.Green else Color.LightGray,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable(enabled = cell.isEmpty() && winner == null) {
                                if (boardState[rowIndex][colIndex].isEmpty() && winner == null) {
                                    boardState[rowIndex][colIndex] = currentPlayer
                                    if (checkWinner(boardState, currentPlayer)) {
                                        winner = currentPlayer
                                        winningCells = getWinningCells(boardState, currentPlayer)
                                        isGameOverDialogVisible = true
                                    } else if (isBoardFull(boardState)) {
                                        winner = "Draw"
                                        isGameOverDialogVisible = true
                                    } else {
                                        currentPlayer = if (currentPlayer == "X") "O" else "X"
                                        if (currentPlayer == "O") {
                                            phoneMove(boardState)
                                            if (checkWinner(boardState, "O")) {
                                                winner = "O"
                                                winningCells = getWinningCells(boardState, "O")
                                                isGameOverDialogVisible = true
                                            } else if (isBoardFull(boardState)) {
                                                winner = "Draw"
                                                isGameOverDialogVisible = true
                                            }
                                            currentPlayer = "X"
                                        }
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cell,
                            fontSize = 50.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (cell == "X") Color(0xFF735C00) else Color(0xFFB1293C)
                        )
                    }
                }
            }
        }
//
//        // Restart tugmasi
//        Button(
//            onClick = {
//                boardState = Array(3) { Array(3) { "" } }
//                currentPlayer = "X"
//                winner = null
//                winningCells = null
//            },
//            modifier = Modifier.padding(16.dp)
//        ) {
//            Text("Restart")
//        }
    }
}

// Telefon (Phone) yurishi uchun aqlli algoritm
fun phoneMove(board: Array<Array<String>>) {
    // G‘alabani keltiradigan yurishni qidirish
    for (i in 0..2) {
        for (j in 0..2) {
            if (board[i][j].isEmpty()) {
                board[i][j] = "O"
                if (checkWinner(board, "O")) return
                board[i][j] = ""
            }
        }
    }
    // O‘yinchining g‘alabasini to‘xtatish
    for (i in 0..2) {
        for (j in 0..2) {
            if (board[i][j].isEmpty()) {
                board[i][j] = "X"
                if (checkWinner(board, "X")) {
                    board[i][j] = "O"
                    return
                }
                board[i][j] = ""
            }
        }
    }
    // Birinchi bo‘sh joyga yurish
    for (i in 0..2) {
        for (j in 0..2) {
            if (board[i][j].isEmpty()) {
                board[i][j] = "O"
                return
            }
        }
    }
}

// G‘alabani tekshirish
fun checkWinner(board: Array<Array<String>>, player: String): Boolean {
    // Gorizontal va vertikal
    for (i in 0..2) {
        if ((board[i][0] == player && board[i][1] == player && board[i][2] == player) ||
            (board[0][i] == player && board[1][i] == player && board[2][i] == player)
        ) return true
    }
    // Diagonal
    return (board[0][0] == player && board[1][1] == player && board[2][2] == player) ||
            (board[0][2] == player && board[1][1] == player && board[2][0] == player)
}

// G‘alaba chizig‘ini olish
fun getWinningCells(board: Array<Array<String>>, player: String): List<Pair<Int, Int>> {
    // Gorizontal
    for (i in 0..2) {
        if (board[i][0] == player && board[i][1] == player && board[i][2] == player) {
            return listOf(Pair(i, 0), Pair(i, 1), Pair(i, 2))
        }
    }
    // Vertikal
    for (i in 0..2) {
        if (board[0][i] == player && board[1][i] == player && board[2][i] == player) {
            return listOf(Pair(0, i), Pair(1, i), Pair(2, i))
        }
    }
    // Diagonal
    if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
        return listOf(Pair(0, 0), Pair(1, 1), Pair(2, 2))
    }
    if (board[0][2] == player && board[1][1] == player && board[2][0] == player) {
        return listOf(Pair(0, 2), Pair(1, 1), Pair(2, 0))
    }

    return emptyList()
}

// Taxta to‘ldi yoki yo‘q
fun isBoardFull(board: Array<Array<String>>): Boolean {
    return board.all { row -> row.all { it.isNotEmpty() } }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameOverBottomSheet(winner: String?, onRestart: () -> Unit) {
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        sheetState = bottomSheetState,
        onDismissRequest = {

        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(
                        id = if (winner == "Draw") R.drawable.tic_tac_toe
                        else R.drawable.winner_cup
                    ),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth()
                        .size(200.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = winner?.let { if (it == "Draw") "It's a Draw!" else "Winner: $it" }
                        ?: "",
                    modifier = Modifier.padding(8.dp)
                )
                Text(
                    text = if (winner == "Draw") "Congrats to both of you for equally excelling in the art of not winning."
                    else "Congrats on being the undisputed champion of pressing buttons like a pro.",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        scope.launch { bottomSheetState.hide() }
                        onRestart()
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFF005BC0))
                ) {
                    Text(
                        "Restart",
                        color = Color.White,
                        fontSize = 20.sp
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GameScreenPreview() {
    GameScreen(player = "X")
}