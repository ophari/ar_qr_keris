package com.example.model

/**
 * Data models for Indonesian cultural weapons, Keris anatomy,
 * and academic project documentation references.
 */
data class PusakaItem(
    val id: String,
    val name: String,
    val origin: String,
    val island: String,
    val era: String,
    val category: String,
    val unescoRecognition: Boolean,
    val unescoYear: String? = null,
    val description: String,
    val philosophy: String,
    val materials: List<String>,
    val meshType: MeshType,
    val parts: List<AnatomyPart> = emptyList(),
    val lengthCm: Float,
    val weightGrams: Int,
    val trivia: String
)

enum class MeshType {
    KERIS_LUK_9,
    KERIS_LUK_13,
    MANDAU_DAYAK,
    RENCONG_ACEH,
    KUJANG_SUNDA,
    CELURIT_MADURA
}

data class AnatomyPart(
    val id: String,
    val javaneseName: String,
    val indonesianName: String,
    val description: String,
    val philosophy: String,
    val relativeFocusY: Float // Vertical position for 3D camera targeting (-1.0 to 1.0)
)

data class AcademicResearch(
    val title: String,
    val authors: String,
    val institution: String,
    val year: String,
    val arTechnology: String,
    val method: String,
    val summary: String,
    val keyFinding: String
)

data class SusQuestion(
    val id: Int,
    val questionIndonesian: String,
    val positiveAspect: Boolean
)
