package com.incplaylist.incoriginalmedia.customwave

import android.app.Activity

class UpdaterThread : Thread {
    private var REFRESH_INTERVAL_MS: Int
    private var c: Activity
    private var tr = 0.0f
    private var v: DrawView

    constructor(v: DrawView, c: Activity) {
        this.v = v
        this.c = c
        REFRESH_INTERVAL_MS = 30
    }

    constructor(refreshTime: Int, v: DrawView, c: Activity) {
        this.v = v
        this.c = c
        REFRESH_INTERVAL_MS = refreshTime
    }

    fun setPRog(prog: Float) {
        tr = prog
    }

    override fun run() {
        while (true) {
            try {
                sleep(
                    Math.max(
                        0,
                        REFRESH_INTERVAL_MS.toLong() - redraw()
                    )
                )
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    private fun redraw(): Long {
        val t = System.currentTimeMillis()
        display_game()
        return System.currentTimeMillis() - t
    }

    private fun display_game() {
        c.runOnUiThread { v.setMaxAmplitude(tr) }
    }
}