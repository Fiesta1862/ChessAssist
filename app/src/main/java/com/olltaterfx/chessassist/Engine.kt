package com.olltaterfx.chessassist

import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.move.Move
import kotlin.math.abs

// Depth-3 alpha-beta: ~30ms on-device, ~1500-1800 Elo ceiling.
// For stronger: swap in Stockfish via JNI, or run search off-thread.
object Engine {

    private val values = mapOf(
        'p' to 100, 'n' to 320, 'b' to 330, 'r' to 500, 'q' to 900, 'k' to 20000
    )

    fun bestMove(fen: String, depth: Int = 3): Move? {
        val board = Board()
        board.loadFromFen(fen)
        var best: Move? = null
        var bestScore = Int.MIN_VALUE
        val moves = board.legalMoves()
        for (m in moves) {
            board.doMove(m)
            val score = -search(board, depth - 1, Int.MIN_VALUE + 1, Int.MAX_VALUE)
            board.undoMove()
            if (score > bestScore) { bestScore = score; best = m }
        }
        return best
    }

    private fun search(board: Board, depth: Int, alpha: Int, beta: Int): Int {
        if (depth == 0) return evaluate(board)
        if (board.isMated) return -99999 + depth
        if (board.isDraw) return 0
        var a = alpha
        for (m in board.legalMoves()) {
            board.doMove(m)
            val s = -search(board, depth - 1, -beta, -a)
            board.undoMove()
            if (s > a) a = s
            if (a >= beta) break
        }
        return a
    }

    private fun evaluate(board: Board): Int {
        var score = 0
        for (sq in 0 until 64) {
            val p = board.getPiece(com.github.bhlangonijr.chesslib.Square.allSquares[sq])
            if (p == com.github.bhlangonijr.chesslib.Piece.NONE) continue
            val v = values[p.fenSymbol.lowercaseChar()] ?: 0
            score += if (p.isWhite) v else -v
        }
        return if (board.sideToMove == com.github.bhlangonijr.chesslib.Side.WHITE) score else -score
    }
}