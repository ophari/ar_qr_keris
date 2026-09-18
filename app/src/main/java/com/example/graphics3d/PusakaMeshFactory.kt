package com.example.graphics3d

import androidx.compose.ui.graphics.Color
import com.example.model.MeshType
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object PusakaMeshFactory {

    fun createMesh(type: MeshType): Mesh3D {
        return when (type) {
            MeshType.KERIS_LUK_9 -> createKerisMesh(lukCount = 9, name = "Keris Luk 9")
            MeshType.KERIS_LUK_13 -> createKerisMesh(lukCount = 13, name = "Keris Luk 13")
            MeshType.MANDAU_DAYAK -> createMandauMesh()
            MeshType.RENCONG_ACEH -> createRencongMesh()
            MeshType.KUJANG_SUNDA -> createKujangMesh()
            MeshType.CELURIT_MADURA -> createCeluritMesh()
        }
    }

    /**
     * Builds a full 3D Keris mesh with Bilah (Luk waves), Pamor pattern lines,
     * Ganja base, Mendak ring, and Deder hilt.
     */
    fun createKerisMesh(lukCount: Int, name: String = "Keris Luk $lukCount"): Mesh3D {
        val polygons = mutableListOf<Polygon3D>()

        // Colors for realistic pamor
        val steelDark = Color(0xFF33353A)
        val steelMid = Color(0xFF4A4E58)
        val steelEdge = Color(0xFF8A93A4)
        val pamorSilver = Color(0xFFE2E7EE)
        val goldAccent = Color(0xFFDAA520)
        val woodDark = Color(0xFF4A2511)
        val woodLight = Color(0xFF753E1E)

        // 1. Bilah (Wilah) with Luk (undulating waves)
        val segments = lukCount * 4
        val bladeLength = 1.6f
        val startY = 0.05f

        var prevXCenter = 0f
        var prevY = startY
        var prevHalfWidth = 0.16f
        var prevThick = 0.045f

        for (i in 1..segments) {
            val progress = i / segments.toFloat()
            val currY = startY + progress * bladeLength

            // Mathematical sine wave for the Luk lekukan
            val wavePhase = progress * lukCount * (2 * PI.toFloat())
            // Tapering width and thickness towards the tip
            val taper = 1f - progress * 0.92f
            val amplitude = 0.09f * (1f - progress * 0.4f)
            val currXCenter = sin(wavePhase) * amplitude
            val currHalfWidth = (0.16f * taper).coerceAtLeast(0.012f)
            val currThick = (0.045f * taper).coerceAtLeast(0.008f)

            // 4 vertices per cross-section: Left Edge, Front Ridge, Right Edge, Back Ridge
            // Diamond cross section of a traditional Keris blade
            val p0_L = Point3D(prevXCenter - prevHalfWidth, prevY, 0f)
            val p0_R = Point3D(prevXCenter + prevHalfWidth, prevY, 0f)
            val p0_F = Point3D(prevXCenter, prevY, prevThick)
            val p0_B = Point3D(prevXCenter, prevY, -prevThick)

            val p1_L = Point3D(currXCenter - currHalfWidth, currY, 0f)
            val p1_R = Point3D(currXCenter + currHalfWidth, currY, 0f)
            val p1_F = Point3D(currXCenter, currY, currThick)
            val p1_B = Point3D(currXCenter, currY, -currThick)

            // Front-Left facet
            polygons.add(
                Polygon3D(
                    vertices = listOf(p0_L, p1_L, p1_F, p0_F),
                    baseColor = if (i % 2 == 0) steelMid else steelDark,
                    partTag = "wilah"
                )
            )

            // Front-Right facet
            polygons.add(
                Polygon3D(
                    vertices = listOf(p0_F, p1_F, p1_R, p0_R),
                    baseColor = if (i % 2 == 0) steelDark else steelEdge,
                    partTag = "wilah"
                )
            )

            // Back-Left facet
            polygons.add(
                Polygon3D(
                    vertices = listOf(p0_B, p1_B, p1_L, p0_L),
                    baseColor = if (i % 2 == 0) steelDark else steelMid,
                    partTag = "wilah"
                )
            )

            // Back-Right facet
            polygons.add(
                Polygon3D(
                    vertices = listOf(p0_R, p1_R, p1_B, p0_B),
                    baseColor = steelDark,
                    partTag = "wilah"
                )
            )

            // Pamor Inlay Line running down the center front spine (ada-ada)
            if (i % 2 == 0) {
                val pamorP0 = Point3D(prevXCenter, prevY, prevThick + 0.003f)
                val pamorP1 = Point3D(currXCenter, currY, currThick + 0.003f)
                polygons.add(
                    Polygon3D(
                        vertices = listOf(
                            pamorP0.translate(-0.015f, 0f, 0f),
                            pamorP1.translate(-0.012f, 0f, 0f),
                            pamorP1.translate(0.012f, 0f, 0f),
                            pamorP0.translate(0.015f, 0f, 0f)
                        ),
                        baseColor = pamorSilver,
                        partTag = "wilah",
                        isPamorDetail = true
                    )
                )
            }

            prevXCenter = currXCenter
            prevY = currY
            prevHalfWidth = currHalfWidth
            prevThick = currThick
        }

        // Tip triangle
        val tip = Point3D(prevXCenter, prevY + 0.08f, 0f)
        val lastL = Point3D(prevXCenter - prevHalfWidth, prevY, 0f)
        val lastR = Point3D(prevXCenter + prevHalfWidth, prevY, 0f)
        val lastF = Point3D(prevXCenter, prevY, prevThick)
        val lastB = Point3D(prevXCenter, prevY, -prevThick)

        polygons.add(Polygon3D(listOf(lastL, tip, lastF), steelMid, "wilah"))
        polygons.add(Polygon3D(listOf(lastF, tip, lastR), steelEdge, "wilah"))
        polygons.add(Polygon3D(listOf(lastL, lastB, tip), steelDark, "wilah"))
        polygons.add(Polygon3D(listOf(lastR, tip, lastB), steelDark, "wilah"))

        // 2. Ganja (Asymmetric guard plate at the base of the blade)
        val ganjaYTop = 0.05f
        val ganjaYBottom = -0.05f
        val ganjaLeft = -0.32f // Ganja extends wider on the left (gandik side)
        val ganjaRight = 0.22f
        val ganjaThick = 0.07f

        val gTLF = Point3D(ganjaLeft, ganjaYTop, ganjaThick)
        val gTRF = Point3D(ganjaRight, ganjaYTop, ganjaThick)
        val gBLF = Point3D(ganjaLeft + 0.04f, ganjaYBottom, ganjaThick)
        val gBRF = Point3D(ganjaRight - 0.02f, ganjaYBottom, ganjaThick)

        val gTLB = Point3D(ganjaLeft, ganjaYTop, -ganjaThick)
        val gTRB = Point3D(ganjaRight, ganjaYTop, -ganjaThick)
        val gBLB = Point3D(ganjaLeft + 0.04f, ganjaYBottom, -ganjaThick)
        val gBRB = Point3D(ganjaRight - 0.02f, ganjaYBottom, -ganjaThick)

        // Front Face
        polygons.add(Polygon3D(listOf(gBLF, gBRF, gTRF, gTLF), steelMid, "ganja"))
        // Back Face
        polygons.add(Polygon3D(listOf(gTLB, gTRB, gBRB, gBLB), steelDark, "ganja"))
        // Top Face
        polygons.add(Polygon3D(listOf(gTLF, gTRF, gTRB, gTLB), steelEdge, "ganja"))
        // Bottom Face
        polygons.add(Polygon3D(listOf(gBLF, gBLB, gBRB, gBRF), steelDark, "ganja"))
        // Left Asymmetric Nose (Gandik/Kembang Kacang)
        polygons.add(Polygon3D(listOf(gTLF, gTLB, gBLB, gBLF), goldAccent, "ganja"))
        // Right Edge
        polygons.add(Polygon3D(listOf(gTRF, gBRF, gBRB, gTRB), steelDark, "ganja"))

        // 3. Mendak (Golden ring with bead ornaments)
        val mendakYTop = -0.05f
        val mendakYBottom = -0.15f
        val ringSegments = 10
        val ringRadius = 0.09f

        for (r in 0 until ringSegments) {
            val a0 = r * (2 * PI.toFloat() / ringSegments)
            val a1 = (r + 1) * (2 * PI.toFloat() / ringSegments)

            val p0T = Point3D(cos(a0) * ringRadius, mendakYTop, sin(a0) * ringRadius)
            val p1T = Point3D(cos(a1) * ringRadius, mendakYTop, sin(a1) * ringRadius)
            val p0B = Point3D(cos(a0) * (ringRadius * 0.85f), mendakYBottom, sin(a0) * (ringRadius * 0.85f))
            val p1B = Point3D(cos(a1) * (ringRadius * 0.85f), mendakYBottom, sin(a1) * (ringRadius * 0.85f))

            val beadColor = if (r % 2 == 0) goldAccent else Color(0xFFFFD700)
            polygons.add(Polygon3D(listOf(p0B, p1B, p1T, p0T), beadColor, "mendak"))
        }

        // 4. Deder / Hulu (Sculpted wooden handle bending forward)
        val dederSegments = 8
        val dederStartY = -0.15f
        val dederHeight = 0.55f

        for (d in 0 until dederSegments) {
            val prog0 = d / dederSegments.toFloat()
            val prog1 = (d + 1) / dederSegments.toFloat()

            val y0 = dederStartY - prog0 * dederHeight
            val y1 = dederStartY - prog1 * dederHeight

            // Ergonomic curve of traditional Javanese hulu (tilted posture)
            val bend0 = sin(prog0 * PI.toFloat()) * 0.07f - prog0 * 0.04f
            val bend1 = sin(prog1 * PI.toFloat()) * 0.07f - prog1 * 0.04f

            val r0 = 0.085f * (1f - prog0 * 0.2f)
            val r1 = 0.085f * (1f - prog1 * 0.2f)

            val dSlices = 8
            for (s in 0 until dSlices) {
                val ang0 = s * (2 * PI.toFloat() / dSlices)
                val ang1 = (s + 1) * (2 * PI.toFloat() / dSlices)

                val v00 = Point3D(bend0 + cos(ang0) * r0, y0, sin(ang0) * r0)
                val v10 = Point3D(bend0 + cos(ang1) * r0, y0, sin(ang1) * r0)
                val v01 = Point3D(bend1 + cos(ang0) * r1, y1, sin(ang0) * r1)
                val v11 = Point3D(bend1 + cos(ang1) * r1, y1, sin(ang1) * r1)

                val facetWood = if ((s + d) % 2 == 0) woodLight else woodDark
                polygons.add(Polygon3D(listOf(v01, v11, v10, v00), facetWood, "deder"))
            }
        }

        return Mesh3D(polygons, name)
    }

    /**
     * Builds Mandau Dayak (S-shaped curved belly, single edge, antler hilt).
     */
    private fun createMandauMesh(): Mesh3D {
        val polygons = mutableListOf<Polygon3D>()
        val steel = Color(0xFF6B7280)
        val steelEdge = Color(0xFFD1D5DB)
        val antlerBone = Color(0xFFE5DECE)
        val antlerDark = Color(0xFF8D7B68)
        val tasselRed = Color(0xFFB91C1C)

        val bladeSteps = 16
        val length = 1.7f

        for (i in 0 until bladeSteps) {
            val p0 = i / bladeSteps.toFloat()
            val p1 = (i + 1) / bladeSteps.toFloat()

            val y0 = p0 * length
            val y1 = p1 * length

            // Mandau blade widens towards the upper third
            val belly0 = sin(p0 * PI.toFloat() * 0.85f) * 0.12f
            val belly1 = sin(p1 * PI.toFloat() * 0.85f) * 0.12f

            val spineX0 = 0.02f * p0
            val spineX1 = 0.02f * p1

            val edgeX0 = -0.15f - belly0
            val edgeX1 = -0.15f - belly1

            val thick0 = 0.035f * (1f - p0 * 0.5f)
            val thick1 = 0.035f * (1f - p1 * 0.5f)

            val vSpineF0 = Point3D(spineX0, y0, thick0)
            val vSpineF1 = Point3D(spineX1, y1, thick1)
            val vEdge0 = Point3D(edgeX0, y0, 0f)
            val vEdge1 = Point3D(edgeX1, y1, 0f)
            val vSpineB0 = Point3D(spineX0, y0, -thick0)
            val vSpineB1 = Point3D(spineX1, y1, -thick1)

            // Front bevel
            polygons.add(Polygon3D(listOf(vEdge0, vEdge1, vSpineF1, vSpineF0), if (i % 2 == 0) steel else steelEdge, "wilah"))
            // Back bevel
            polygons.add(Polygon3D(listOf(vSpineB0, vSpineB1, vEdge1, vEdge0), steel, "wilah"))
            // Spine flat
            polygons.add(Polygon3D(listOf(vSpineF0, vSpineF1, vSpineB1, vSpineB0), Color(0xFF374151), "wilah"))
        }

        // Carved Deer Antler Handle (Hulu Mandau)
        for (h in 0..6) {
            val ph = h / 6f
            val hy0 = -ph * 0.45f
            val hy1 = -(ph + 0.16f) * 0.45f
            val rad = 0.08f * (1f + ph * 0.3f)

            val pL0 = Point3D(-rad + ph * 0.08f, hy0, rad * 0.8f)
            val pR0 = Point3D(rad + ph * 0.08f, hy0, rad * 0.8f)
            val pL1 = Point3D(-rad + (ph + 0.16f) * 0.08f, hy1, rad * 0.8f)
            val pR1 = Point3D(rad + (ph + 0.16f) * 0.08f, hy1, rad * 0.8f)

            polygons.add(Polygon3D(listOf(pL0, pR0, pR1, pL1), if (h % 2 == 0) antlerBone else antlerDark, "deder"))
        }

        // Tassel decoration (Bulu Enggang / Rambut ritual)
        polygons.add(
            Polygon3D(
                listOf(
                    Point3D(0.12f, -0.45f, 0f),
                    Point3D(0.18f, -0.7f, 0.02f),
                    Point3D(0.08f, -0.68f, -0.02f)
                ),
                tasselRed,
                "deder"
            )
        )

        return Mesh3D(polygons, "Mandau Dayak")
    }

    /**
     * Builds Rencong Aceh (characteristic L-shaped curved hulu and tapered thrust blade).
     */
    private fun createRencongMesh(): Mesh3D {
        val polygons = mutableListOf<Polygon3D>()
        val bladeSteel = Color(0xFF94A3B8)
        val bladeBright = Color(0xFFE2E8F0)
        val hornDark = Color(0xFF1E293B)
        val silverRing = Color(0xFFCBD5E1)

        // Straight piercing blade with slight forward bend
        val steps = 12
        val length = 1.35f
        for (i in 0 until steps) {
            val p0 = i / steps.toFloat()
            val p1 = (i + 1) / steps.toFloat()

            val y0 = p0 * length
            val y1 = p1 * length

            val curve0 = p0 * p0 * 0.06f
            val curve1 = p1 * p1 * 0.06f

            val width0 = 0.09f * (1f - p0 * 0.85f)
            val width1 = 0.09f * (1f - p1 * 0.85f)

            val vL0 = Point3D(-width0 + curve0, y0, 0f)
            val vR0 = Point3D(width0 + curve0, y0, 0f)
            val vT0 = Point3D(curve0, y0, 0.035f * (1f - p0 * 0.7f))

            val vL1 = Point3D(-width1 + curve1, y1, 0f)
            val vR1 = Point3D(width1 + curve1, y1, 0f)
            val vT1 = Point3D(curve1, y1, 0.035f * (1f - p1 * 0.7f))

            polygons.add(Polygon3D(listOf(vL0, vT0, vT1, vL1), if (i % 2 == 0) bladeSteel else bladeBright, "wilah"))
            polygons.add(Polygon3D(listOf(vT0, vR0, vR1, vT1), bladeSteel, "wilah"))
        }

        // Hulu Meucugek (L-shaped handle of Rencong)
        polygons.add(Polygon3D(listOf(Point3D(-0.06f, 0f, 0f), Point3D(0.06f, 0f, 0f), Point3D(0.05f, -0.1f, 0f), Point3D(-0.05f, -0.1f, 0f)), silverRing, "mendak"))
        polygons.add(Polygon3D(listOf(Point3D(-0.05f, -0.1f, 0f), Point3D(0.05f, -0.1f, 0f), Point3D(0.08f, -0.35f, 0f), Point3D(-0.03f, -0.35f, 0f)), hornDark, "deder"))
        // Right angled elbow extension (Meucugek hook)
        polygons.add(Polygon3D(listOf(Point3D(0.08f, -0.35f, 0f), Point3D(0.28f, -0.38f, 0f), Point3D(0.26f, -0.47f, 0f), Point3D(0.05f, -0.45f, 0f)), hornDark, "deder"))

        return Mesh3D(polygons, "Rencong Aceh")
    }

    /**
     * Builds Kujang Sunda (Distinctive broad curved blade with 3 sacred holes).
     */
    private fun createKujangMesh(): Mesh3D {
        val polygons = mutableListOf<Polygon3D>()
        val ironDark = Color(0xFF475569)
        val ironPamor = Color(0xFF94A3B8)
        val woodHandle = Color(0xFF78350F)
        val goldDetail = Color(0xFFEAB308)

        val steps = 14
        val length = 1.3f

        for (i in 0 until steps) {
            val p0 = i / steps.toFloat()
            val p1 = (i + 1) / steps.toFloat()

            val y0 = p0 * length
            val y1 = p1 * length

            // Kujang expands in the belly and has an arched back
            val arch0 = sin(p0 * PI.toFloat()) * 0.22f
            val arch1 = sin(p1 * PI.toFloat()) * 0.22f

            val back0 = arch0 * 0.4f
            val back1 = arch1 * 0.4f

            val edge0 = -arch0 * 0.9f - 0.05f
            val edge1 = -arch1 * 0.9f - 0.05f

            val vL0 = Point3D(edge0, y0, 0f)
            val vL1 = Point3D(edge1, y1, 0f)
            val vR0 = Point3D(back0, y0, 0f)
            val vR1 = Point3D(back1, y1, 0f)
            val vC0 = Point3D((edge0 + back0) / 2f, y0, 0.03f)
            val vC1 = Point3D((edge1 + back1) / 2f, y1, 0.03f)

            polygons.add(Polygon3D(listOf(vL0, vL1, vC1, vC0), ironDark, "wilah"))
            polygons.add(Polygon3D(listOf(vC0, vC1, vR1, vR0), if (i % 2 == 0) ironPamor else ironDark, "wilah"))
        }

        // 3 Mata Kujang (Sacred circular perforations in the body)
        val eyeCenters = listOf(0.45f, 0.65f, 0.85f)
        for (eyeY in eyeCenters) {
            polygons.add(
                Polygon3D(
                    listOf(
                        Point3D(-0.06f, eyeY, 0.032f),
                        Point3D(-0.02f, eyeY + 0.04f, 0.032f),
                        Point3D(0.02f, eyeY, 0.032f),
                        Point3D(-0.02f, eyeY - 0.04f, 0.032f)
                    ),
                    goldDetail,
                    "wilah",
                    isDecorativeLine = true
                )
            )
        }

        // Garuda beak curved handle
        polygons.add(Polygon3D(listOf(Point3D(-0.04f, 0f, 0f), Point3D(0.04f, 0f, 0f), Point3D(0.08f, -0.35f, 0f), Point3D(-0.02f, -0.35f, 0f)), woodHandle, "deder"))

        return Mesh3D(polygons, "Kujang Sunda")
    }

    /**
     * Builds Celurit Madura (Crescent sickle blade with ribbed handle).
     */
    private fun createCeluritMesh(): Mesh3D {
        val polygons = mutableListOf<Polygon3D>()
        val steelBlade = Color(0xFFCBD5E1)
        val steelSpine = Color(0xFF64748B)
        val woodDark = Color(0xFF3E2723)
        val brassCollar = Color(0xFFF59E0B)

        val steps = 20
        val centerOriginY = 0.5f
        val radius = 0.65f

        for (i in 0 until steps) {
            val ang0 = (i / steps.toFloat()) * (PI.toFloat() * 0.85f)
            val ang1 = ((i + 1) / steps.toFloat()) * (PI.toFloat() * 0.85f)

            val innerR0 = radius - 0.08f * (1f - i / steps.toFloat() * 0.8f)
            val innerR1 = radius - 0.08f * (1f - (i + 1) / steps.toFloat() * 0.8f)

            val vOut0 = Point3D(-cos(ang0) * radius, centerOriginY + sin(ang0) * radius, 0.02f)
            val vOut1 = Point3D(-cos(ang1) * radius, centerOriginY + sin(ang1) * radius, 0.02f)
            val vIn0 = Point3D(-cos(ang0) * innerR0, centerOriginY + sin(ang0) * innerR0, 0f)
            val vIn1 = Point3D(-cos(ang1) * innerR1, centerOriginY + sin(ang1) * innerR1, 0f)

            polygons.add(Polygon3D(listOf(vIn0, vIn1, vOut1, vOut0), if (i % 2 == 0) steelBlade else steelSpine, "wilah"))
        }

        // Brass ferrule and wooden handle
        polygons.add(Polygon3D(listOf(Point3D(-radius, centerOriginY, 0.03f), Point3D(-radius + 0.1f, centerOriginY, 0.03f), Point3D(-radius + 0.09f, centerOriginY - 0.1f, 0.03f), Point3D(-radius, centerOriginY - 0.1f, 0.03f)), brassCollar, "mendak"))
        polygons.add(Polygon3D(listOf(Point3D(-radius, centerOriginY - 0.1f, 0.03f), Point3D(-radius + 0.09f, centerOriginY - 0.1f, 0.03f), Point3D(-radius + 0.07f, centerOriginY - 0.5f, 0.03f), Point3D(-radius - 0.02f, centerOriginY - 0.5f, 0.03f)), woodDark, "deder"))

        return Mesh3D(polygons, "Celurit Madura")
    }
}
