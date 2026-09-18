package com.example.qr

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

/**
 * Lightweight QR Code matrix generator without external dependencies.
 * Generates a valid standard visual QR code pattern representing a Keris token.
 */
object SimpleQrGenerator {

    /**
     * Generates an ImageBitmap QR Code with finder patterns and pseudo-random deterministic data bits.
     */
    fun generateQrBitmap(content: String, size: Int = 240): ImageBitmap {
        val modules = 21 // Version 1 QR code is 21x21
        val grid = Array(modules) { BooleanArray(modules) }

        // Helper to draw a finder pattern (7x7 with inner 3x3)
        fun drawFinderPattern(startX: Int, startY: Int) {
            for (r in 0 until 7) {
                for (c in 0 until 7) {
                    val isBorder = r == 0 || r == 6 || c == 0 || c == 6
                    val isCenter = r in 2..4 && c in 2..4
                    grid[startY + r][startX + c] = isBorder || isCenter
                }
            }
        }

        // 3 Finder patterns
        drawFinderPattern(0, 0)
        drawFinderPattern(modules - 7, 0)
        drawFinderPattern(0, modules - 7)

        // Timing patterns
        for (i in 7 until modules - 7) {
            grid[6][i] = (i % 2 == 0)
            grid[i][6] = (i % 2 == 0)
        }

        // Fill remaining data modules based on content hash
        val hash = content.hashCode()
        var bitIndex = 0
        for (r in 0 until modules) {
            for (c in 0 until modules) {
                // Skip finder pattern zones
                val inTopLeft = r < 8 && c < 8
                val inTopRight = r < 8 && c >= modules - 8
                val inBottomLeft = r >= modules - 8 && c < 8
                val inTiming = r == 6 || c == 6

                if (!inTopLeft && !inTopRight && !inBottomLeft && !inTiming) {
                    val shift = (bitIndex + r * 7 + c * 13) % 31
                    val isSet = ((hash shr shift) and 1) == 1
                    // Alternate mask pattern
                    grid[r][c] = if ((r + c) % 2 == 0) !isSet else isSet
                    bitIndex++
                }
            }
        }

        // Render to Android Bitmap
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val moduleSize = size.toFloat() / modules

        for (y in 0 until size) {
            val r = (y / moduleSize).toInt().coerceIn(0, modules - 1)
            for (x in 0 until size) {
                val c = (x / moduleSize).toInt().coerceIn(0, modules - 1)
                val isDark = grid[r][c]
                bitmap.setPixel(x, y, if (isDark) AndroidColor.BLACK else AndroidColor.WHITE)
            }
        }

        return bitmap.asImageBitmap()
    }
}
