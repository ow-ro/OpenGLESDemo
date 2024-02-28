package com.wemesh.myapplication

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class Renderer : GLSurfaceView.Renderer {
    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private lateinit var squares: Array<Square>

    private var scrollOffset = -6f
    private var scrollDirection = 0.02f

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
    }

    override fun onDrawFrame(unused: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        Matrix.setLookAtM(viewMatrix, 0, scrollOffset, 0f, -3f, scrollOffset, 0f, 0f, 0f, 1.0f, 0.0f)
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        val spacing = 30.0f / squares.size // Adjust spacing based on the number of squares and virtual width
        squares.forEachIndexed { index, square ->
            val modelMatrix = FloatArray(16)
            Matrix.setIdentityM(modelMatrix, 0)
            // Position the squares so they do not overlap, adjusting by the index and spacing
            val position = -2f + index * spacing + spacing / 2 // Adjust so squares are side by side
            Matrix.translateM(modelMatrix, 0, position, 0f, 0f)

            val mvpMatrix = FloatArray(16)
            Matrix.multiplyMM(mvpMatrix, 0, vPMatrix, 0, modelMatrix, 0)

            square.draw(mvpMatrix)
        }

        scrollOffset += scrollDirection
        if (scrollOffset > 30.0f || scrollOffset <= -6.0f) {
            scrollDirection *= -1
        }
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val virtualWidth = 2 * width
        val ratio: Float = virtualWidth.toFloat() / height.toFloat()
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)

        squares = Array(20) {
            val color = floatArrayOf(
                Math.random().toFloat(), // Red
                Math.random().toFloat(), // Green
                Math.random().toFloat(), // Blue
                1.0f                     // Alpha
            )
            Square(color)
        }
    }

}