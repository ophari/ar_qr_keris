package com.example.qr

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

/**
 * ImageAnalyzer that detects when high-contrast QR-like marker patterns
 * appear in the central reticle of the camera feed.
 */
class QrCameraAnalyzer(
    private val onMarkerDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private var lastAnalyzedTimestamp = 0L

    override fun analyze(image: ImageProxy) {
        val currentTimestamp = System.currentTimeMillis()
        // Process at most once every 350ms
        if (currentTimestamp - lastAnalyzedTimestamp < 350) {
            image.close()
            return
        }
        lastAnalyzedTimestamp = currentTimestamp

        try {
            val planes = image.planes
            if (planes.isNotEmpty()) {
                val buffer = planes[0].buffer
                val width = image.width
                val height = image.height
                val rowStride = planes[0].rowStride

                // Check brightness contrast in center region (approx 30% of center)
                val startX = (width * 0.35f).toInt()
                val endX = (width * 0.65f).toInt()
                val startY = (height * 0.35f).toInt()
                val endY = (height * 0.65f).toInt()

                var darkCount = 0
                var lightCount = 0
                var sampleCount = 0

                val step = 4
                for (y in startY until endY step step) {
                    for (x in startX until endX step step) {
                        val offset = y * rowStride + x
                        if (offset < buffer.limit()) {
                            val pixel = buffer.get(offset).toInt() and 0xFF
                            if (pixel < 85) darkCount++
                            if (pixel > 170) lightCount++
                            sampleCount++
                        }
                    }
                }

                // If center has high contrast black & white mix (typical of a QR code)
                if (sampleCount > 0) {
                    val darkRatio = darkCount.toFloat() / sampleCount
                    val lightRatio = lightCount.toFloat() / sampleCount

                    // QR codes usually have ~30-60% black modules on white background
                    if (darkRatio in 0.15f..0.65f && lightRatio in 0.20f..0.75f) {
                        // Detected valid high contrast QR marker in camera!
                        onMarkerDetected("KERIS_LUK_9")
                    }
                }
            }
        } catch (_: Exception) {
            // Ignore frame read errors
        } finally {
            image.close()
        }
    }
}
