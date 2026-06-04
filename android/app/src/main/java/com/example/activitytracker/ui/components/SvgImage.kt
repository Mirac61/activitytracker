package com.example.activitytracker.ui.components

import android.graphics.Picture
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import com.caverock.androidsvg.SVG

/**
 * Renders a raw SVG file from res/raw with full gradient support via AndroidSVG.
 *
 * Usage:
 * ```kotlin
 *   SvgImage(rawResId = R.raw.noto_fire, modifier = Modifier.size(24.dp))
 *   ```
 */
@Composable
fun SvgImage(
    rawResId: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val svg = remember(rawResId) {
        context.resources.openRawResource(rawResId).use { SVG.getFromInputStream(it) }
    }

    Canvas(modifier = modifier) {
        drawIntoCanvas { canvas ->
            val picture = Picture()
            val width = size.width.toInt()
            val height = size.height.toInt()
            val recordingCanvas = picture.beginRecording(width, height)
            svg.documentWidth = width.toFloat()
            svg.documentHeight = height.toFloat()
            svg.renderToCanvas(recordingCanvas)
            picture.endRecording()
            canvas.nativeCanvas.drawPicture(picture)
        }
    }
}