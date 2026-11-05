package com.olltaterfx.chessassist

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pad = (16 * resources.displayMetrics.density).toInt()

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad, pad, pad)
        }
        root.addView(TextView(this).apply {
            text = "chessassist — overlay engine"
            textSize = 20f
        })
        root.addView(Button(this).apply {
            text = "1. Grant overlay permission"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")))
            }
        })
        root.addView(Button(this).apply {
            text = "2. Enable accessibility (board reader)"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        })
        root.addView(Button(this).apply {
            text = "3. Start assist"
            setOnClickListener { startOverlay() }
        })
        root.addView(Button(this).apply {
            text = "4. Stop assist"
            setOnClickListener { stopService(Intent(this@MainActivity, OverlayService::class.java)) }
        })
        setContentView(root)
    }

    private fun startOverlay() {
        if (!Settings.canDrawOverlays(this)) {
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")))
            return
        }
        val i = Intent(this, OverlayService::class.java)
        ContextCompat.startForegroundService(this, i)
    }
}