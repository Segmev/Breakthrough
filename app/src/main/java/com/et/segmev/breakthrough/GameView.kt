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

class GameView : View {

    companion object {
        private var case : Rect = Rect()
        private var piece : Rect = Rect()
        private var casePaint1: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private var casePaint2: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private var piecePaint1: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private var piecePaint2: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        private var boardSize : Int = 0
        private var squareSize : Int = 0

        private var board = Array(8, { IntArray(8) })
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

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        for (i in 0..7) {
            board[i] = IntArray(8)
            if (i <= 1)
                for (j in 0..7) {
                    board[i][j] = 1
                }
            else if (i >= 6)
                for (j in 0..7) {
                    board[i][j] = 2
                }
        }
        casePaint1.color = Color.rgb(128, 96, 0)
        casePaint2.color = Color.rgb(230, 172, 0)
        piecePaint1.color = Color.WHITE
        piecePaint2.color = Color.BLACK
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
                canvas.restore()
            }
        }
        super.onDraw(canvas)
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
        Log.d("Measure", "$boardSize - $squareSize")
        super.onMeasure(boardSize, boardSize)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            Log.d("DOWN", "" + event.x + " - " + event.y)
            return true
        }
        if (event.actionMasked == MotionEvent.ACTION_UP) {
            Log.d("UP", "" + event.x + " - " + event.y)
            return true
        }

        return super.onTouchEvent(event)
    }
}
