package com.olltaterfx.chessassist

import android.app.*
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import com.github.bhlangonijr.chesslib.Square
import kotlin.concurrent.thread

class OverlayService : Service() {

    private lateinit var wm: WindowManager
    private lateinit var arrow: ArrowView
    private lateinit var params: WindowManager.LayoutParams
    private val handler = Handler(Looper.getMainLooper())
    private var lastFen = ""
    private var running = true

    override fun onCreate() {
        super.onCreate()
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        arrow = ArrowView(this)
        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.TOP or Gravity.START }
        wm.addView(arrow, params)
        startForegroundNotif()
        thread(name = "assist") { loop() }
    }

    private fun loop() {
        while (running) {
            if (BoardState.ready && BoardState.fen != lastFen) {
                lastFen = BoardState.fen
                val mv = Engine.bestMove(BoardState.fen, depth = 3) ?: continue
                val (fx, fy) = squareToFraction(mv.from)
                val (tx, ty) = squareToFraction(mv.to)
                handler.post {
                    arrow.fx = fx; arrow.fy = fy; arrow.tx = tx; arrow.ty = ty
                    arrow.invalidate()
                }
            }
            Thread.sleep(400)
        }
    }

    private fun squareToFraction(sq: Square): Pair<Float, Float> {
        val name = sq.name.lowercase()
        val file = "abcdefgh".indexOf(name[0])
        val rank = 8 - name[1].digitToInt()
        return (file + 0.5f) / 8f to (rank + 0.5f) / 8f
    }

    private fun startForegroundNotif() {
        val ch = "chessassist"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mgr = getSystemService(NotificationManager::class.java)
            mgr.createNotificationChannel(NotificationChannel(ch, "chessassist", NotificationManager.IMPORTANCE_LOW))
        }
        val n = NotificationCompat.Builder(this, ch)
            .setContentTitle("chessassist running")
            .setContentText("overlay active")
            .setSmallIcon(android.R.drawable.ic_menu_view)
            .build()
        startForeground(42, n)
    }

    override fun onDestroy() {
        running = false
        try { wm.removeView(arrow) } catch (_: Exception) {}
        super.onDestroy()
    }

    override fun onBind(i: Intent?): IBinder? = null
}