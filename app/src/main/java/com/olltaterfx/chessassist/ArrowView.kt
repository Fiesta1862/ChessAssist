package com.olltaterfx.chessassist

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.View

// Draws an arrow from `from` to `to` in board-relative (0..1) coords.
class ArrowView(ctx: Context) : View(ctx) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#00FF88")
        strokeWidth = 8f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val head = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#00FF88"); style = Paint.Style.FILL }

    var fx = 0f; var fy = 0f; var tx = 0f; var ty = 0f

    override fun onDraw(canvas: Canvas) {
        if (width == 0 || height == 0) return
        val x1 = fx * width; val y1 = fy * height
        val x2 = tx * width; val y2 = ty * height
        canvas.drawLine(x1, y1, x2, y2, paint)
        val ang = Math.atan2((y2 - y1).toDouble(), (x2 - x1).toDouble())
        val size = 24.0
        val path = Path()
        path.moveTo(x2.toFloat(), y2.toFloat())
        path.lineTo((x2 - size * Math.cos(ang - Math.PI/6)).toFloat(), (y2 - size * Math.sin(ang - Math.PI/6)).toFloat())
        path.lineTo((x2 - size * Math.cos(ang + Math.PI/6)).toFloat(), (y2 - size * Math.sin(ang + Math.PI/6)).toFloat())
        path.close()
        canvas.drawPath(path, head)
    }
}