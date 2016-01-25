package com.ud.plus1torch

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration

/**
 * Created by vince_000 on 11/16/2015.
 */

fun main(args: Array<String>) {
    val config = LwjglApplicationConfiguration()
    config.foregroundFPS = 60
    config.vSyncEnabled = true
    config.resizable = false
    config.width = 640
    config.height = 480
    LwjglApplication(TorchTestGame(), config)
}
