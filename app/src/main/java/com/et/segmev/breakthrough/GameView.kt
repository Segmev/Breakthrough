package com.et.segmev.breakthrough

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlin.math.roundToInt

class GameView : View {

    companion object {
        private var boardRect : Rect = Rect()
        private var case : Rect = Rect()
        private var piece : Rect = Rect()
        private var casePaint1: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private var casePaint2: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private var piecePaint1: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private var piecePaint2: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private var borderPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        private var circlePaintRed: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private var circlePaintGreen: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        private var boardSize : Int = 0
        private var squareSize : Int = 0

        private var touchedCellI: Int = 0
        private var touchedCellJ: Int = 0

        private var previousTouchedCellI : Int = 0
        private var previousTouchedCellJ : Int = 0


        var gameLogic = GameLogic()

        val GD = GameData(GameLogic.Players.ONE, GameLogic.Players.NONE,
                Array(8, { IntArray(8) }),
                Array(8, { IntArray(8)}), false)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        casePaint1.color = Color.rgb(128, 96, 0)
        casePaint2.color = Color.rgb(230, 172, 0)
        piecePaint1.color = Color.WHITE
        piecePaint2.color = Color.BLACK
        circlePaintRed.color = Color.argb( 55,255, 0, 0)
        circlePaintGreen.color = Color.argb( 55,0, 255, 0)
        borderPaint.color = Color.BLACK
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = 5f
        resetGame()
    }

    data class GameData(var currentPlayer: GameLogic.Players,
                        var winner: GameLogic.Players,
                        var board: Array<IntArray>,
                        var availableMoves: Array<IntArray>,
                        var moveAttempted: Boolean
    )

    class GameLogic {

        enum class Players {
            NONE, ONE, TWO
        }

        fun getPlayingPlayer() : Int {
            return GD.currentPlayer.ordinal
        }

        fun getWinner() : Int {
            return GD.winner.ordinal
        }

        private fun switchPlayers() {
            GD.currentPlayer = if (GD.currentPlayer == Players.ONE) Players.TWO else Players.ONE
        }

        private fun discoverAvailableMoves(i: Int, j: Int) {
            resetMovesArray()
            if (GD.moveAttempted && GD.board[j][i] == getPlayingPlayer()) {
                val direction = if (getPlayingPlayer() % 2 == 1) 1 else -1
                for (a in i-1..i+1) {
                    if (a in 0..7 && j+direction in 0..7
                            && (GD.board[j+direction][a] == 0
                                    || (GD.board[j + direction][a] != getPlayingPlayer() && a != i))) {
                        GD.availableMoves[j+direction][a] = 1
                    }
                }
            }
        }

        private fun resetMovesArray() {
            for (i in 0..7) {
                for (j in 0..7) {
                    GD.availableMoves[i][j] = 0
                }
            }
        }

        private fun movePiece() {
            GD.board[previousTouchedCellJ][previousTouchedCellI] = 0
            GD.board[touchedCellJ][touchedCellI] = getPlayingPlayer()
        }

        fun getPiecesNumber(player: Players) : Int {
            var count = 0
            for (i in 0..7) {
                for (j in 0..7) {
                    if (GD.board[j][i] == player.ordinal) {
                        count++
                    }
                }
            }
            return count
        }

        fun resetGame() {
            GD.moveAttempted = false
            GD.currentPlayer = Players.ONE
            GD.winner = Players.NONE
        }

        private fun checkWinningCondition() {
            if (getPlayingPlayer() == 1) {
                for (i in 0..7) {
                    if (GD.board[7][i] == 1) {
                        GD.winner = Players.ONE
                        break
                    }
                }
            } else if (getPlayingPlayer() == 2) {
                for (i in 0..7) {
                    if (GD.board[0][i] == 2) {
                        GD.winner = Players.TWO
                        break
                    }
                }
            }
        }

        fun play(upI : Int, upJ : Int) : Boolean {
            if (GD.winner != Players.NONE)
                return false
            GD.moveAttempted =
                    (touchedCellI == upI && touchedCellJ == upJ
                            && (gameLogic.getPlayingPlayer() == GD.board[touchedCellJ][touchedCellI]))
            if (GD.moveAttempted)
                discoverAvailableMoves(touchedCellI, touchedCellJ)
            else {
                if (GD.availableMoves[touchedCellJ][touchedCellI] > 0) {
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
            GD.board[i] = IntArray(8)
            GD.availableMoves[i] = IntArray(8)
            if (i <= 1)
                for (j in 0..7) {
                    GD.board[i][j] = 1
                }
            else if (i >= 6)
                for (j in 0..7) {
                    GD.board[i][j] = 2
                }
        }
        gameLogic.resetGame()
        invalidate()
    }

    fun getGameLogic() : GameLogic {
        return gameLogic
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
        boardRect.set(0, 0, boardSize, boardSize)
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
        canvas.drawRect(boardRect, casePaint2)
        for (i in 0..7) {
            for (j in 0..7) {
                canvas.save()
                canvas.translate(i * (squareSize)  * 1f, j * (squareSize) * 1f)
                if (i % 2 + j % 2 == 1)
                    canvas.drawRect(case, casePaint1)
                if (GD.board[j][i] == 1) {
                    canvas.drawRect(piece, piecePaint1)
                } else if (GD.board[j][i] == 2) {
                    canvas.drawRect(piece, piecePaint2)
                }
                if (GD.moveAttempted && i == touchedCellI && j == touchedCellJ)
                    canvas.drawCircle(squareSize.toFloat() / 2, squareSize.toFloat() / 2, squareSize.toFloat() / 2.2f, circlePaintRed)
                if (GD.availableMoves[j][i] > 0)
                    canvas.drawCircle(squareSize.toFloat() / 2, squareSize.toFloat() / 2, squareSize.toFloat() / 2.2f, circlePaintGreen)
                canvas.drawRect(case, borderPaint)
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
