package com.example.graphics3d

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun Interactive3DViewer(
    mesh: Mesh3D,
    renderMode: RenderMode,
    isAutoRotate: Boolean,
    modifier: Modifier = Modifier,
    highlightPartTag: String? = null,
    onPartTapped: ((String) -> Unit)? = null
) {
    // Rotation angles in degrees
    var pitchDeg by remember { mutableFloatStateOf(15f) }
    var yawDeg by remember { mutableFloatStateOf(25f) }
    var rollDeg by remember { mutableFloatStateOf(0f) }
    var zoomScale by remember { mutableFloatStateOf(1.0f) }
    var panOffsetX by remember { mutableFloatStateOf(0f) }
    var panOffsetY by remember { mutableFloatStateOf(0f) }

    // Auto rotate transition
    val infiniteTransition = rememberInfiniteTransition(label = "auto_rotate")
    val animatedYaw by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing)
        ),
        label = "yaw_spin"
    )

    val currentYaw = if (isAutoRotate) (yawDeg + animatedYaw) % 360f else yawDeg

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    yawDeg += pan.x * 0.45f
                    pitchDeg = (pitchDeg - pan.y * 0.45f).coerceIn(-85f, 85f)
                    zoomScale = (zoomScale * zoom).coerceIn(0.4f, 2.8f)
                    panOffsetX += pan.x * 0.2f
                    panOffsetY += pan.y * 0.2f
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f + panOffsetX
            val cy = size.height / 2f + panOffsetY
            val baseUnit = minOf(size.width, size.height) * 0.42f * zoomScale

            val pitchRad = pitchDeg * (PI.toFloat() / 180f)
            val yawRad = currentYaw * (PI.toFloat() / 180f)
            val rollRad = rollDeg * (PI.toFloat() / 180f)

            // Normalized light vector (from top-left forward)
            val lightX = -0.5f
            val lightY = 0.7f
            val lightZ = 0.8f
            val lightLen = sqrt(lightX * lightX + lightY * lightY + lightZ * lightZ)
            val nLx = lightX / lightLen
            val nLy = lightY / lightLen
            val nLz = lightZ / lightLen

            // Transform each polygon
            val transformedPolygons = mesh.polygons.map { poly ->
                // Apply exploded view offset if active
                val explodedDY = if (renderMode == RenderMode.EXPLODED_VIEW) {
                    when (poly.partTag) {
                        "wilah" -> 0.38f
                        "ganja" -> 0.05f
                        "mendak" -> -0.18f
                        "deder" -> -0.48f
                        else -> 0f
                    }
                } else {
                    0f
                }

                val transformedVertices = poly.vertices.map { v ->
                    val offsetV = if (explodedDY != 0f) v.translate(0f, explodedDY, 0f) else v
                    // Apply Yaw, then Pitch, then Roll
                    offsetV
                        .rotateY(yawRad)
                        .rotateX(pitchRad)
                        .rotateZ(rollRad)
                }

                // Normal calculation in world space
                val norm = calculatePolygonNormal(transformedVertices)
                // Center depth for Painter's algorithm
                var avgZ = 0f
                for (tv in transformedVertices) avgZ += tv.z
                avgZ /= transformedVertices.size

                TransformedPoly(
                    poly = poly,
                    screenVertices = transformedVertices.map { tv ->
                        // Perspective projection
                        val perspectiveFactor = 1f / (1f - tv.z * 0.22f).coerceAtLeast(0.4f)
                        Offset(
                            x = cx + tv.x * baseUnit * perspectiveFactor,
                            y = cy - tv.y * baseUnit * perspectiveFactor
                        )
                    },
                    depthZ = avgZ,
                    normal = norm,
                    isHighlighted = highlightPartTag != null && poly.partTag == highlightPartTag
                )
            }

            // Sort by depth (Painter's algorithm: draw farthest Z first)
            val sorted = transformedPolygons.sortedBy { it.depthZ }

            // Render polygons
            for (tp in sorted) {
                drawPolygon3D(tp, renderMode, nLx, nLy, nLz)
            }
        }
    }
}

private data class TransformedPoly(
    val poly: Polygon3D,
    val screenVertices: List<Offset>,
    val depthZ: Float,
    val normal: Point3D,
    val isHighlighted: Boolean
)

private fun calculatePolygonNormal(verts: List<Point3D>): Point3D {
    if (verts.size < 3) return Point3D(0f, 0f, 1f)
    val v0 = verts[0]
    val v1 = verts[1]
    val v2 = verts[2]

    val ax = v1.x - v0.x
    val ay = v1.y - v0.y
    val az = v1.z - v0.z

    val bx = v2.x - v0.x
    val by = v2.y - v0.y
    val bz = v2.z - v0.z

    val nx = ay * bz - az * by
    val ny = az * bx - ax * bz
    val nz = ax * by - ay * bx

    val len = sqrt(nx * nx + ny * ny + nz * nz)
    return if (len > 0.0001f) Point3D(nx / len, ny / len, nz / len) else Point3D(0f, 0f, 1f)
}

private fun DrawScope.drawPolygon3D(
    tp: TransformedPoly,
    renderMode: RenderMode,
    lx: Float,
    ly: Float,
    lz: Float
) {
    if (tp.screenVertices.size < 3) return

    val path = Path().apply {
        moveTo(tp.screenVertices[0].x, tp.screenVertices[0].y)
        for (i in 1 until tp.screenVertices.size) {
            lineTo(tp.screenVertices[i].x, tp.screenVertices[i].y)
        }
        close()
    }

    when (renderMode) {
        RenderMode.HOLOGRAPHIC_AR -> {
            // Neon cyan wireframe with semi-transparent dark cyan fill
            val fillAlpha = if (tp.isHighlighted) 0.55f else 0.15f
            val strokeColor = if (tp.isHighlighted) Color(0xFFFFD700) else Color(0xFF00E5FF)

            drawPath(
                path = path,
                color = (if (tp.isHighlighted) Color(0x66FFD700) else Color(0x2200E5FF))
            )
            drawPath(
                path = path,
                color = strokeColor,
                style = Stroke(width = if (tp.isHighlighted) 2.5f else 1.2f)
            )

            // Draw vertex glowing dots
            for (sv in tp.screenVertices) {
                drawCircle(
                    color = strokeColor,
                    radius = 2.0f,
                    center = sv
                )
            }
        }

        RenderMode.ROYAL_GOLD -> {
            // Metallic Gold shading with specular highlights
            val dot = max(0.2f, tp.normal.x * lx + tp.normal.y * ly + tp.normal.z * lz)
            val baseGold = if (tp.isHighlighted) Color(0xFFFFE082) else Color(0xFFD4AF37)
            val litColor = Color(
                red = (baseGold.red * dot * 1.3f).coerceIn(0f, 1f),
                green = (baseGold.green * dot * 1.2f).coerceIn(0f, 1f),
                blue = (baseGold.blue * dot * 0.9f).coerceIn(0f, 1f),
                alpha = 1.0f
            )

            drawPath(path = path, color = litColor)
            drawPath(
                path = path,
                color = Color(0x44FFE082),
                style = Stroke(width = 0.8f)
            )
        }

        RenderMode.REALISTIC_PAMOR, RenderMode.EXPLODED_VIEW -> {
            // Realistic metal / wood shader with pamor highlights
            val dot = max(0.25f, tp.normal.x * lx + tp.normal.y * ly + tp.normal.z * lz)
            val base = tp.poly.baseColor

            val shadedColor = if (tp.poly.isPamorDetail) {
                // Pamor lines glint with bright nickel silver
                val pamorDot = max(0.4f, dot * 1.2f)
                Color(
                    red = (0.95f * pamorDot).coerceIn(0f, 1f),
                    green = (0.97f * pamorDot).coerceIn(0f, 1f),
                    blue = (1.0f * pamorDot).coerceIn(0f, 1f),
                    alpha = 1.0f
                )
            } else {
                Color(
                    red = (base.red * dot).coerceIn(0f, 1f),
                    green = (base.green * dot).coerceIn(0f, 1f),
                    blue = (base.blue * dot).coerceIn(0f, 1f),
                    alpha = 1.0f
                )
            }

            val finalColor = if (tp.isHighlighted) {
                Color(
                    red = (shadedColor.red * 0.5f + 0.5f).coerceIn(0f, 1f),
                    green = (shadedColor.green * 0.5f + 0.45f).coerceIn(0f, 1f),
                    blue = (shadedColor.blue * 0.3f).coerceIn(0f, 1f),
                    alpha = 1.0f
                )
            } else {
                shadedColor
            }

            drawPath(path = path, color = finalColor)

            // Outline edge for crisp blade definition
            val outlineColor = if (tp.isHighlighted) {
                Color(0xFFFFD700)
            } else if (tp.poly.isDecorativeLine) {
                Color(0xFFFFC107)
            } else {
                Color(0x33FFFFFF)
            }
            drawPath(
                path = path,
                color = outlineColor,
                style = Stroke(width = if (tp.isHighlighted) 2.2f else 0.7f)
            )
        }
    }
}
