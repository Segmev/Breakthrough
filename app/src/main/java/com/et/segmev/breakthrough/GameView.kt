package com.et.segmev.breakthrough

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlin.math.roundToInt

class GameView : View {

    companion object {
        private var case : Rect = Rect()
        private var piece : Rect = Rect()
        private var casePaint1: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private var casePaint2: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private var piecePaint1: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private var piecePaint2: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        private var circlePaintRed: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private var circlePaintGreen: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        private var boardSize : Int = 0
        private var squareSize : Int = 0

        private var board = Array(8, { IntArray(8) })

        private var availableMoves = Array(8, { IntArray(8) })

        private var touchedCellI: Int = 0
        private var touchedCellJ: Int = 0

        private var previousTouchedCellI : Int = 0
        private var previousTouchedCellJ : Int = 0

        private var moveAttempted = false

        var gameLogic = GameLogic()
    }

    class GameLogic {
        private var currentPlayer = Players.ONE
        private var winner = Players.NONE

        enum class Players {
            NONE, ONE, TWO
        }

        fun getPlayingPlayer() : Int {
            return currentPlayer.ordinal
        }

        fun getWinner() : Int {
            return winner.ordinal
        }

        private fun switchPlayers() {
            currentPlayer = if (currentPlayer == Players.ONE) Players.TWO else Players.ONE
        }

        private fun discoverAvailableMoves(i: Int, j: Int) {
            resetMovesArray()
            if (moveAttempted && board[j][i] == getPlayingPlayer()) {
                val direction = if (getPlayingPlayer() % 2 == 1) 1 else -1
                for (a in i-1..i+1) {
                    if (a in 0..7 && j+direction in 0..7
                            && (board[j+direction][a] == 0
                                    || (board[j + direction][a] != getPlayingPlayer() && a != i))) {
                        availableMoves[j+direction][a] = 1
                    }
                }
            }
        }

        private fun resetMovesArray() {
            for (i in 0..7) {
                for (j in 0..7) {
                    availableMoves[i][j] = 0
                }
            }
        }

        private fun movePiece() {
            board[previousTouchedCellJ][previousTouchedCellI] = 0
            board[touchedCellJ][touchedCellI] = getPlayingPlayer()
        }

        fun getPiecesNumber(player: Players) : Int {
            var count = 0
            for (i in 0..7) {
                for (j in 0..7) {
                    if (board[j][i] == player.ordinal) {
                        count++
                    }
                }
            }
            return count
        }

        fun resetGame() {
            moveAttempted = false
            currentPlayer = Players.ONE
            winner = Players.NONE
        }

        private fun checkWinningCondition() {
            if (getPlayingPlayer() == 1) {
                for (i in 0..7) {
                    if (board[7][i] == 1) {
                        winner = Players.ONE
                        break
                    }
                }
            } else if (getPlayingPlayer() == 2) {
                for (i in 0..7) {
                    if (board[0][i] == 2) {
                        winner = Players.TWO
                        break
                    }
                }
            }
        }

        fun play(upI : Int, upJ : Int) : Boolean {
            if (winner != Players.NONE)
                return false
            moveAttempted =
                    (touchedCellI == upI && touchedCellJ == upJ
                            && (gameLogic.getPlayingPlayer() == board[touchedCellJ][touchedCellI]))
            if (moveAttempted)
                discoverAvailableMoves(touchedCellI, touchedCellJ)
            else {
                if (availableMoves[touchedCellJ][touchedCellI] > 0) {
                    movePiece()
                    checkWinningCondition()
                    switchPlayers()

                }
                resetMovesArray()
            }

            return true
        }
    }

    fun resetGame() {
        for (i in 0..7) {
            board[i] = IntArray(8)
            availableMoves[i] = IntArray(8)
            if (i <= 1)
                for (j in 0..7) {
                    board[i][j] = 1
                }
            else if (i >= 6)
                for (j in 0..7) {
                    board[i][j] = 2
                }
        }
        gameLogic.resetGame()
        invalidate()
    }

    fun getGameLogic() : GameLogic {
        return gameLogic
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        casePaint1.color = Color.rgb(128, 96, 0)
        casePaint2.color = Color.rgb(230, 172, 0)
        piecePaint1.color = Color.WHITE
        piecePaint2.color = Color.BLACK
        circlePaintRed.color = Color.argb( 55,255, 0, 0)
        circlePaintGreen.color = Color.argb( 55,0, 255, 0)
        resetGame()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        boardSize =
                if (MeasureSpec.getSize(widthMeasureSpec) < MeasureSpec.getSize(heightMeasureSpec))
                    MeasureSpec.getSize(widthMeasureSpec)
                else
                    MeasureSpec.getSize(heightMeasureSpec)
        squareSize = boardSize / 8
        case.set(0, 0, squareSize, squareSize)
        piece.set((squareSize * 25 / 100),
                (squareSize * 25 / 100),
                squareSize - (squareSize * 25 / 100),
                squareSize - (squareSize * 25 / 100))
        super.onMeasure(boardSize, boardSize)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            if (event.x.roundToInt() / squareSize >= 8 ||
                    event.y.roundToInt() / squareSize >= 8)
                return true
            previousTouchedCellI = touchedCellI
            previousTouchedCellJ = touchedCellJ
            touchedCellI = (event.x.roundToInt() / squareSize)
            touchedCellJ = (event.y.roundToInt() / squareSize)
            performClick()
            return true
        }
        if (event.actionMasked == MotionEvent.ACTION_UP) {
            gameLogic.play(event.x.roundToInt() / squareSize, event.y.roundToInt() / squareSize)
            invalidate()
            performClick()
            return true
        }

        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        for (i in 0..7) {
            for (j in 0..7) {
                canvas.save()
                canvas.translate(i * (squareSize)  * 1f, j * (squareSize) * 1f)
                if (i % 2 + j % 2 == 1)
                    canvas.drawRect(case, casePaint1)
                else
                    canvas.drawRect(case, casePaint2)
                if (board[j][i] == 1) {
                    canvas.drawRect(piece, piecePaint1)
                } else if (board[j][i] == 2) {
                    canvas.drawRect(piece, piecePaint2)
                }
                if (moveAttempted && i == touchedCellI && j == touchedCellJ)
                    canvas.drawCircle(squareSize.toFloat() / 2, squareSize.toFloat() / 2, squareSize.toFloat() / 2.2f, circlePaintRed)
                if (availableMoves[j][i] > 0)
                    canvas.drawCircle(squareSize.toFloat() / 2, squareSize.toFloat() / 2, squareSize.toFloat() / 2.2f, circlePaintGreen)
                canvas.restore()
            }
        }
        super.onDraw(canvas)
    }

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }
}
