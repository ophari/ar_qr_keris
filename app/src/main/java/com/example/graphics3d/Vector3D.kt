package com.example.graphics3d

import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin

data class Point3D(val x: Float, val y: Float, val z: Float) {
    fun rotateX(radians: Float): Point3D {
        val cosA = cos(radians)
        val sinA = sin(radians)
        return Point3D(
            x = x,
            y = y * cosA - z * sinA,
            z = y * sinA + z * cosA
        )
    }

    fun rotateY(radians: Float): Point3D {
        val cosA = cos(radians)
        val sinA = sin(radians)
        return Point3D(
            x = x * cosA + z * sinA,
            y = y,
            z = -x * sinA + z * cosA
        )
    }

    fun rotateZ(radians: Float): Point3D {
        val cosA = cos(radians)
        val sinA = sin(radians)
        return Point3D(
            x = x * cosA - y * sinA,
            y = x * sinA + y * cosA,
            z = z
        )
    }

    fun translate(dx: Float, dy: Float, dz: Float): Point3D {
        return Point3D(x + dx, y + dy, z + dz)
    }

    fun scale(factor: Float): Point3D {
        return Point3D(x * factor, y * factor, z * factor)
    }
}

enum class RenderMode(val title: String, val subtitle: String) {
    REALISTIC_PAMOR("Logam & Pamor", "Shader tekstur pamor meteorit & tempa lipat"),
    HOLOGRAPHIC_AR("Hologram AR", "Tampilan wireframe cyber futuristik"),
    ROYAL_GOLD("Pusaka Emas", "Sepuhan emas keraton dan kilau suasa"),
    EXPLODED_VIEW("Terurai (Anatomi)", "Dekomposisi bilah, ganja, mendak & deder")
}

data class Polygon3D(
    val vertices: List<Point3D>,
    val baseColor: Color,
    val partTag: String = "general", // "wilah", "ganja", "mendak", "deder", "warangka"
    val isDecorativeLine: Boolean = false,
    val isPamorDetail: Boolean = false
) {
    /**
     * Calculates normal vector for Lambertian diffuse shading.
     */
    fun calculateNormal(): Point3D {
        if (vertices.size < 3) return Point3D(0f, 0f, 1f)
        val v0 = vertices[0]
        val v1 = vertices[1]
        val v2 = vertices[2]

        val ax = v1.x - v0.x
        val ay = v1.y - v0.y
        val az = v1.z - v0.z

        val bx = v2.x - v0.x
        val by = v2.y - v0.y
        val bz = v2.z - v0.z

        val nx = ay * bz - az * by
        val ny = az * bx - ax * bz
        val nz = ax * by - ay * bx

        val length = kotlin.math.sqrt(nx * nx + ny * ny + nz * nz)
        return if (length > 0.0001f) {
            Point3D(nx / length, ny / length, nz / length)
        } else {
            Point3D(0f, 0f, 1f)
        }
    }

    fun centerDepth(): Float {
        if (vertices.isEmpty()) return 0f
        var sumZ = 0f
        for (v in vertices) {
            sumZ += v.z
        }
        return sumZ / vertices.size
    }
}

data class Mesh3D(
    val polygons: List<Polygon3D>,
    val name: String
)
