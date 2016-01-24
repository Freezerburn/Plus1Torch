package com.ud.quill

import com.badlogic.gdx.ApplicationListener
import com.ud.quill.screen.AsciiScreen

/**
 * Created by vince_000 on 11/16/2015.
 */


public class QuillGame : ApplicationListener {
    val screen = AsciiScreen(100, 100, 80, 20, 640, 480);

    override fun create() {
        // TODO: Initialize some basic rendering capabilities.
    }

    override fun pause() {
    }

    override fun resize(w: Int, h: Int) {
    }

    override fun render() {
        screen.render { c, x, y, font ->
            // Render each character that the screen has decided to render.
            // TODO: Put together a libgdx string renderer.
        }
    }

    override fun resume() {
    }

    override fun dispose() {
    }
}