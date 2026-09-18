package com.example.qr

import android.graphics.Bitmap
import android.graphics.ImageFormat
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.nio.ByteBuffer

/**
 * QR Code Target representing a Keris model unlocked through AR scanning.
 */
data class KerisQrTarget(
    val qrCode: String,
    val pusakaName: String,
    val description: String,
    val origin: String,
    val lukCount: Int
)

object KerisQrCatalog {
    val targets = listOf(
        KerisQrTarget(
            qrCode = "KERIS_LUK_9",
            pusakaName = "Keris Kyai Sengkelat (Luk 9)",
            description = "Keris pusaka era Kerajaan Majapahit dengan 9 lekukan melambangkan kematangan batin dan kepemimpinan.",
            origin = "Jawa Tengah (Surakarta)",
            lukCount = 9
        ),
        KerisQrTarget(
            qrCode = "KERIS_LUK_13",
            pusakaName = "Keris Nagasasra (Luk 13)",
            description = "Keris agung berluk 13 dengan simbol naga penjaga ketentraman, kemakmuran, dan wibawa raja.",
            origin = "Jawa Timur (Majapahit)",
            lukCount = 13
        ),
        KerisQrTarget(
            qrCode = "KERIS_LUK_5",
            pusakaName = "Keris Pandhawa (Luk 5)",
            description = "Keris berluk 5 melambangkan ksatria Pandawa Lima, simbol keadilan dan keteguhan moral.",
            origin = "Yogyakarta Hadiningrat",
            lukCount = 5
        ),
        KerisQrTarget(
            qrCode = "KERIS_LUK_7",
            pusakaName = "Keris Balebang (Luk 7)",
            description = "Keris berluk 7 melambangkan 'pitu' (pitulungan/pertolongan dari Sang Pencipta).",
            origin = "Cirebon / Sunda",
            lukCount = 7
        ),
        KerisQrTarget(
            qrCode = "KERIS_LUK_3",
            pusakaName = "Keris Jangkung (Luk 3)",
            description = "Keris berluk 3 melambangkan harapan hidup sentosa dan perlindungan marabahaya.",
            origin = "Bali / Lombok",
            lukCount = 3
        )
    )

    fun findTarget(qrContent: String): KerisQrTarget? {
        val clean = qrContent.trim().uppercase()
        return targets.find { target ->
            clean.contains(target.qrCode) || clean.contains("KERIS") && clean.contains("${target.lukCount}")
        } ?: targets.find { target ->
            clean.contains(target.qrCode)
        } ?: targets.firstOrNull() // Default to Keris Luk 9 if generic QR scanned
    }
}
