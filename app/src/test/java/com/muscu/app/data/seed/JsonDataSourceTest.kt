package com.muscu.app.data.seed

import android.content.Context
import android.content.res.AssetManager
import com.google.gson.Gson
import com.muscu.app.domain.model.Intensity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.ByteArrayInputStream

class JsonDataSourceTest {

    private lateinit var context: Context
    private lateinit var assets: AssetManager
    private val gson = Gson()

    @Before
    fun setUp() {
        context = mock()
        assets = mock()
        whenever(context.assets).thenReturn(assets)
    }

    @Test
    fun `loadExercises parses valid exercises json`() {
        // Given
        val json = """
            [
              {
                "id": "ex-test-1",
                "name": "Test Bench",
                "dayOfWeek": 2,
                "category": "Pectoraux/Dos",
                "targetSets": 3,
                "targetRepsMin": 8,
                "targetRepsMax": 10,
                "intensity": "MODERATE",
                "warning": "Garde la colonne neutre.",
                "orderIndex": 0,
                "equipment": "Haltères, Banc plat",
                "objective": "Hypertrophie",
                "difficulty": "Intermédiaire"
              }
            ]
        """.trimIndent()
        whenever(assets.open("exercises.json")).thenReturn(ByteArrayInputStream(json.toByteArray()))

        val dataSource = JsonDataSource(context, gson)

        // When
        val exercises = dataSource.loadExercises()

        // Then
        assertEquals(1, exercises.size)
        val ex = exercises.first()
        assertEquals("ex-test-1", ex.id)
        assertEquals("Test Bench", ex.name)
        assertEquals(2, ex.dayOfWeek)
        assertEquals("Pectoraux/Dos", ex.category)
        assertEquals(3, ex.targetSets)
        assertEquals(8, ex.targetRepsMin)
        assertEquals(10, ex.targetRepsMax)
        assertEquals(Intensity.MODERATE, ex.intensity)
        assertEquals("Garde la colonne neutre.", ex.warning)
        assertEquals(0, ex.orderIndex)
        assertEquals("Haltères, Banc plat", ex.equipment)
        assertEquals("Hypertrophie", ex.objective)
        assertEquals("Intermédiaire", ex.difficulty)
    }

    @Test
    fun `loadExercises returns empty list when file missing`() {
        // Given
        whenever(assets.open("exercises.json")).thenThrow(java.io.IOException("File not found"))

        val dataSource = JsonDataSource(context, gson)

        // When
        val exercises = dataSource.loadExercises()

        // Then
        assertTrue(exercises.isEmpty())
    }

    @Test
    fun `loadExercises returns empty list when json malformed`() {
        // Given
        val badJson = "{ not valid json }"
        whenever(assets.open("exercises.json")).thenReturn(ByteArrayInputStream(badJson.toByteArray()))

        val dataSource = JsonDataSource(context, gson)

        // When
        val exercises = dataSource.loadExercises()

        // Then
        assertTrue(exercises.isEmpty())
    }

    @Test
    fun `loadPrograms parses valid programs json`() {
        // Given
        val json = """
            [
              {
                "id": "prog-tuesday",
                "name": "Programme Mardi",
                "description": "Séance test",
                "dayOfWeek": 2,
                "exerciseIds": ["ex-1", "ex-2"]
              }
            ]
        """.trimIndent()
        whenever(assets.open("programs.json")).thenReturn(ByteArrayInputStream(json.toByteArray()))

        val dataSource = JsonDataSource(context, gson)

        // When
        val programs = dataSource.loadPrograms()

        // Then
        assertEquals(1, programs.size)
        val prog = programs.first()
        assertEquals("prog-tuesday", prog.id)
        assertEquals("Programme Mardi", prog.name)
        assertEquals("Séance test", prog.description)
        assertEquals(2, prog.dayOfWeek)
        assertEquals(listOf("ex-1", "ex-2"), prog.exerciseIds)
    }

    @Test
    fun `loadPrograms returns empty list when file missing`() {
        // Given
        whenever(assets.open("programs.json")).thenThrow(java.io.IOException("File not found"))

        val dataSource = JsonDataSource(context, gson)

        // When
        val programs = dataSource.loadPrograms()

        // Then
        assertTrue(programs.isEmpty())
    }

    @Test
    fun `loadPrograms returns empty list when json malformed`() {
        // Given
        val badJson = "[ broken"
        whenever(assets.open("programs.json")).thenReturn(ByteArrayInputStream(badJson.toByteArray()))

        val dataSource = JsonDataSource(context, gson)

        // When
        val programs = dataSource.loadPrograms()

        // Then
        assertTrue(programs.isEmpty())
    }

    @Test
    fun `loadExercises handles all intensity values`() {
        // Given - JSON au format ExerciseJson (avec les champs requis)
        val json = """
            [
              {"id":"1","name":"A","dayOfWeek":1,"category":"C","targetSets":1,"targetRepsMin":1,"targetRepsMax":1,"intensity":"MODERATE","orderIndex":0,"equipment":"","objective":"","difficulty":"Débutant"},
              {"id":"2","name":"B","dayOfWeek":1,"category":"C","targetSets":1,"targetRepsMin":1,"targetRepsMax":1,"intensity":"LIGHT","orderIndex":1,"equipment":"","objective":"","difficulty":"Débutant"},
              {"id":"3","name":"C","dayOfWeek":1,"category":"C","targetSets":1,"targetRepsMin":1,"targetRepsMax":1,"intensity":"BODYWEIGHT","orderIndex":2,"equipment":"","objective":"","difficulty":"Débutant"}
            ]
        """.trimIndent()
        whenever(assets.open("exercises.json")).thenReturn(ByteArrayInputStream(json.toByteArray()))

        val dataSource = JsonDataSource(context, gson)

        // When
        val exercises = dataSource.loadExercises()

        // Then
        assertEquals(3, exercises.size)
        assertEquals(Intensity.MODERATE, exercises[0].intensity)
        assertEquals(Intensity.LIGHT, exercises[1].intensity)
        assertEquals(Intensity.BODYWEIGHT, exercises[2].intensity)
    }
}
