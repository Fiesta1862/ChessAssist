package com.olltaterfx.chessassist

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import java.util.concurrent.CopyOnWriteArrayList

// Scrapes chess.com's board node tree into a FEN the engine can read.
object BoardState {
    @Volatile var fen: String = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1"
    @Volatile var ready: Boolean = false
}

class BoardReader : AccessibilityService() {

    private val pieceFiles = "abcdefgh"

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val root = rootInActiveWindow ?: return
        val board = findBoard(root) ?: return
        val fen = readBoard(board) ?: return
        BoardState.fen = fen
        BoardState.ready = true
    }

    private fun findBoard(node: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        if (node.viewIdResourceName?.contains("board") == true) return node
        for (i in 0 until node.childCount) {
            val c = node.getChild(i) ?: continue
            val hit = findBoard(c)
            if (hit != null) return hit
        }
        return null
    }

    // chess.com exposes each piece as a node with a "data-piece"/content-desc
    // like "White pawn e2". We read square + color, build 8x8, emit FEN.
    private fun readBoard(board: AccessibilityNodeInfo): String? {
        val grid = Array(8) { arrayOfNulls<String>(8) }
        var count = 0
        fun walk(n: AccessibilityNodeInfo) {
            val desc = n.contentDescription?.toString() ?: ""
            if (desc.isNotEmpty()) {
                val sq = Regex("[a-h][1-8]").find(desc)?.value
                if (sq != null) {
                    val file = pieceFiles.indexOf(sq[0])
                    val rank = 8 - sq[1].digitToInt()
                    val color = when {
                        desc.contains("White", true) -> "w"
                        desc.contains("Black", true) -> "b"
                        else -> return
                    }
                    val type = when {
                        desc.contains("pawn", true) -> "p"
                        desc.contains("knight", true) -> "n"
                        desc.contains("bishop", true) -> "b"
                        desc.contains("rook", true) -> "r"
                        desc.contains("queen", true) -> "q"
                        desc.contains("king", true) -> "k"
                        else -> return
                    }
                    grid[rank][file] = if (color == "w") type.uppercase() else type
                    count++
                }
            }
            for (i in 0 until n.childCount) n.getChild(i)?.let { walk(it) }
        }
        walk(board)
        if (count < 4) return null
        val rows = (0 until 8).joinToString("/") { r ->
            var empty = 0; val sb = StringBuilder()
            for (f in 0 until 8) {
                val p = grid[r][f]
                if (p == null) empty++ else { if (empty > 0) { sb.append(empty); empty = 0 }; sb.append(p) }
            }
            if (empty > 0) sb.append(empty)
            sb.toString()
        }
        val turn = if (isBoardFlipped(board)) "b" else "w"
        return "$rows $turn - - 0 1"
    }

    private fun isBoardFlipped(board: AccessibilityNodeInfo): Boolean {
        val desc = board.contentDescription?.toString() ?: ""
        return desc.contains("black", true) && !desc.contains("white", true)
    }

    override fun onInterrupt() {}
}