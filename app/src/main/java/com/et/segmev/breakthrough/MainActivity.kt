package com.et.segmev.breakthrough

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resetGame.setOnClickListener {
            gameView.resetGame()
            updateUI()
        }
        updateUI()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        super.dispatchTouchEvent(ev)
        updateUI()
        return true
    }

    private fun updateUI() {
        playerOnePieces.text = String.format("${getString(R.string.playerOne)}: %02d",
                gameView.getGameLogic().getPiecesNumber(GameView.GameLogic.Players.ONE))
        playerTwoPieces.text = String.format("${getString(R.string.playerTwo)}: %02d",
                gameView.getGameLogic().getPiecesNumber(GameView.GameLogic.Players.TWO))
        playerTurn.text =
                String.format("Turn: " + if (gameView.getGameLogic().getPlayingPlayer() == 1) getString(R.string.playerOne) else getString(R.string.playerTwo))
    }
}
