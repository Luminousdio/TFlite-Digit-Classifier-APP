package com.example.digitclassifier

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.divyanshu.draw.widget.DrawView


class MainActivity : AppCompatActivity() {

    private var drawView: DrawView? = null
    private var clearButton: Button? = null
    private var predictedTextView: TextView? = null
    private var digitClassifier = DigitClassifier(this)

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawView = findViewById(R.id.draw_view)
        drawView?.setStrokeWidth(70.0f)
        drawView?.setColor(Color.WHITE)
        drawView?.setBackgroundColor(Color.BLACK)
        clearButton = findViewById(R.id.clear_button)
        predictedTextView = findViewById(R.id.predicted_text)

        clearButton?.setOnClickListener {
            drawView?.clearCanvas()
            predictedTextView?.text = getString(R.string.prediction_text_placeholder)
        }

        drawView?.setOnTouchListener { _, event ->
            drawView?.onTouchEvent(event)

            if (event.action == MotionEvent.ACTION_UP) {
                classifyDrawing()
            }

            true
        }

        digitClassifier
            .initialize()
            .addOnFailureListener { e -> Log.e(TAG, "Error to setting up digit classifier.", e) }
    }

    override fun onDestroy() {
        digitClassifier.close()
        super.onDestroy()
    }

    private fun classifyDrawing() {
        val bitmap = drawView?.getBitmap()

        if ((bitmap != null) && (digitClassifier.isInitialized)) {
            digitClassifier
                .classifyAsync(bitmap)
                .addOnSuccessListener { resultText -> predictedTextView?.text = resultText }
                .addOnFailureListener { e ->
                    predictedTextView?.text = getString(
                        R.string.classification_error_message,
                        e.localizedMessage
                    )
                    Log.e(TAG, "Error classifying drawing.", e)
                }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
