package com.ottaport.chitra.repository

import android.content.Context
import com.ottaport.chitra.model.project.ProjectData
import com.ottaport.chitra.model.project.ProjectTemplates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class ProjectRepository(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        encodeDefaults = true
    }

    private val autosaveFile: File
        get() = File(context.filesDir, "chitra_autosave.json")

    suspend fun saveProject(project: ProjectData) = withContext(Dispatchers.IO) {
        try {
            val updated = project.copy(updatedAt = System.currentTimeMillis())
            val text = json.encodeToString(updated)
            autosaveFile.writeText(text)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun loadAutosaveOrTemplate(): ProjectData = withContext(Dispatchers.IO) {
        try {
            if (autosaveFile.exists()) {
                val text = autosaveFile.readText()
                if (text.isNotBlank()) {
                    return@withContext json.decodeFromString<ProjectData>(text)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // Fallback to pre-built Anime Wave template
        ProjectTemplates.createAnimeWaveProject()
    }

    fun serializeProject(project: ProjectData): String {
        return json.encodeToString(project)
    }

    fun deserializeProject(jsonString: String): ProjectData {
        return json.decodeFromString<ProjectData>(jsonString)
    }
}
