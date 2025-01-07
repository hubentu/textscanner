package com.example.textscaner

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.google.mlkit.vision.text.Text.TextBlock

class RectangleOverlay(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val selectionPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }
    
    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 40f
        style = Paint.Style.FILL
        setShadowLayer(3f, 0f, 0f, Color.BLACK)
    }
    
    private val textBackgroundPaint = Paint().apply {
        color = Color.argb(150, 0, 0, 0)
        style = Paint.Style.FILL
    }
    
    private var rect = RectF()
    private var startX = 0f
    private var startY = 0f
    private var recognizedText: String = ""
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(rect, selectionPaint)
        
        if (recognizedText.isNotEmpty()) {
            // Draw text background
            val textBounds = Rect()
            textPaint.getTextBounds(recognizedText, 0, recognizedText.length, textBounds)
            
            // Split text into lines
            val lines = recognizedText.split("\n")
            val lineHeight = textBounds.height() + 10
            
            // Draw each line
            lines.forEachIndexed { index, line ->
                val y = rect.top + lineHeight * (index + 1)
                val backgroundRect = RectF(
                    rect.left,
                    y - lineHeight + 5,
                    rect.left + textPaint.measureText(line) + 10,
                    y + 5
                )
                canvas.drawRect(backgroundRect, textBackgroundPaint)
                canvas.drawText(line, rect.left + 5, y, textPaint)
            }
        }
    }
    
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y
                rect.left = startX
                rect.top = startY
                recognizedText = "" // Clear text when starting new selection
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                rect.right = event.x
                rect.bottom = event.y
                invalidate()
            }
        }
        return true
    }
    
    fun getSelectedRect(): Rect {
        return Rect(
            rect.left.toInt(),
            rect.top.toInt(),
            rect.right.toInt(),
            rect.bottom.toInt()
        )
    }
    
    fun showRecognizedText(text: String) {
        recognizedText = text
        invalidate()
    }
} 