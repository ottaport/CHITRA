package com.ottaport.chitra.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ottaport.chitra.model.Vector2D
import com.ottaport.chitra.model.anim.InterpolationType
import com.ottaport.chitra.model.anim.Interpolator
import com.ottaport.chitra.model.anim.Keyframe
import com.ottaport.chitra.model.expression.ExpressionPreset
import com.ottaport.chitra.model.expression.FacialExpression
import com.ottaport.chitra.model.layer.PartType
import com.ottaport.chitra.model.layer.RigLayer
import com.ottaport.chitra.model.mesh.MeshGrid
import com.ottaport.chitra.model.project.ProjectData
import com.ottaport.chitra.model.project.ProjectTemplates
import com.ottaport.chitra.model.rig.Bone
import com.ottaport.chitra.model.rig.Skeleton
import com.ottaport.chitra.repository.ProjectRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.atan2

enum class StudioMode(val title: String) {
    RIGGING("Rig Bones"),
    ANIMATE("Animate Pose"),
    MESH("Mesh Warp"),
    LAYERS("Layers"),
    EXPRESSIONS("Facial Morphs"),
    AUDIO("Audio Track")
}

data class StudioUiState(
    val project: ProjectData = ProjectTemplates.createAnimeWaveProject(),
    val currentMode: StudioMode = StudioMode.ANIMATE,
    val evaluatedPose: Interpolator.EvaluatedPose = Interpolator.evaluatePoseAtFrame(
        0f,
        project.timeline.keyframes,
        project.skeleton,
        project.layers
    ),
    val selectedBoneId: String? = null,
    val selectedLayerId: String? = null,
    val selectedVertexId: Int? = null,
    val isIkActive: Boolean = true,
    val zoomScale: Float = 1.0f,
    val panOffset: Vector2D = Vector2D.Zero,
    val autosaveStatus: String = "Saved",
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val exportStatusMessage: String? = null
)

class ChitraViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProjectRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(StudioUiState())
    val uiState: StateFlow<StudioUiState> = _uiState.asStateFlow()

    private var playbackJob: Job? = null
    private val undoStack = mutableListOf<ProjectData>()
    private val redoStack = mutableListOf<ProjectData>()

    init {
        loadInitialProject()
    }

    private fun loadInitialProject() {
        viewModelScope.launch {
            val project = repository.loadAutosaveOrTemplate()
            setProject(project, recordUndo = false)
        }
    }

    private fun recordUndoState() {
        undoStack.add(_uiState.value.project)
        if (undoStack.size > 25) undoStack.removeAt(0)
        redoStack.clear()
        updateUndoRedoStatus()
    }

    private fun updateUndoRedoStatus() {
        _uiState.update {
            it.copy(
                canUndo = undoStack.isNotEmpty(),
                canRedo = redoStack.isNotEmpty()
            )
        }
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val prev = undoStack.removeAt(undoStack.lastIndex)
            redoStack.add(_uiState.value.project)
            setProject(prev, recordUndo = false)
            updateUndoRedoStatus()
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val next = redoStack.removeAt(redoStack.lastIndex)
            undoStack.add(_uiState.value.project)
            setProject(next, recordUndo = false)
            updateUndoRedoStatus()
        }
    }

    fun setMode(mode: StudioMode) {
        _uiState.update { it.copy(currentMode = mode) }
    }

    fun selectBone(boneId: String?) {
        _uiState.update { it.copy(selectedBoneId = boneId) }
    }

    fun selectLayer(layerId: String?) {
        _uiState.update { state ->
            val layer = state.project.layers.find { it.id == layerId }
            state.copy(
                selectedLayerId = layerId,
                selectedBoneId = layer?.attachedBoneId ?: state.selectedBoneId
            )
        }
    }

    fun selectVertex(vertexId: Int?) {
        _uiState.update { it.copy(selectedVertexId = vertexId) }
    }

    fun setZoomAndPan(zoom: Float, pan: Vector2D) {
        _uiState.update {
            it.copy(
                zoomScale = zoom.coerceIn(0.3f, 4.0f),
                panOffset = pan
            )
        }
    }

    fun resetView() {
        _uiState.update {
            it.copy(
                zoomScale = 1.0f,
                panOffset = Vector2D.Zero
            )
        }
    }

    // Playback engine
    fun togglePlayback() {
        val isCurrentlyPlaying = _uiState.value.project.timeline.isPlaying
        if (isCurrentlyPlaying) {
            pause()
        } else {
            play()
        }
    }

    fun play() {
        playbackJob?.cancel()
        _uiState.update {
            val updatedTimeline = it.project.timeline.copy(isPlaying = true)
            it.copy(project = it.project.copy(timeline = updatedTimeline))
        }

        playbackJob = viewModelScope.launch {
            val fps = _uiState.value.project.timeline.fps
            val frameDelayMs = (1000L / fps).coerceAtLeast(16L)

            while (isActive && _uiState.value.project.timeline.isPlaying) {
                delay(frameDelayMs)
                advanceFrame()
            }
        }
    }

    fun pause() {
        playbackJob?.cancel()
        _uiState.update {
            val updatedTimeline = it.project.timeline.copy(isPlaying = false)
            it.copy(project = it.project.copy(timeline = updatedTimeline))
        }
    }

    private fun advanceFrame() {
        _uiState.update { state ->
            val tl = state.project.timeline
            val total = tl.totalFrames
            var nextFrame = tl.currentFrame + 1f

            if (nextFrame >= total) {
                if (tl.isLooping) {
                    nextFrame = 0f
                } else {
                    nextFrame = total - 1f
                    pause()
                }
            }

            val updatedTimeline = tl.copy(
                currentFrame = nextFrame,
                selectedFrameIndex = nextFrame.toInt()
            )
            val updatedProject = state.project.copy(timeline = updatedTimeline)
            val evaluated = evaluatePose(updatedProject)

            state.copy(
                project = updatedProject,
                evaluatedPose = evaluated
            )
        }
    }

    fun seekToFrame(frameIndex: Int) {
        _uiState.update { state ->
            val clamped = frameIndex.coerceIn(0, state.project.timeline.totalFrames - 1)
            val updatedTimeline = state.project.timeline.copy(
                currentFrame = clamped.toFloat(),
                selectedFrameIndex = clamped
            )
            val updatedProject = state.project.copy(timeline = updatedTimeline)
            val evaluated = evaluatePose(updatedProject)
            state.copy(project = updatedProject, evaluatedPose = evaluated)
        }
    }

    fun stepForward() {
        seekToFrame(_uiState.value.project.timeline.selectedFrameIndex + 1)
    }

    fun stepBackward() {
        seekToFrame(_uiState.value.project.timeline.selectedFrameIndex - 1)
    }

    fun toggleLoop() {
        _uiState.update { state ->
            val current = state.project.timeline.isLooping
            val updatedTimeline = state.project.timeline.copy(isLooping = !current)
            state.copy(project = state.project.copy(timeline = updatedTimeline))
        }
    }

    fun toggleOnionSkin() {
        _uiState.update { state ->
            val current = state.project.timeline.onionSkinEnabled
            val updatedTimeline = state.project.timeline.copy(onionSkinEnabled = !current)
            state.copy(project = state.project.copy(timeline = updatedTimeline))
        }
    }

    fun toggleIkMode() {
        _uiState.update { state ->
            val newIk = !state.isIkActive
            val updatedSkeleton = state.project.skeleton.copy(isIkEnabled = newIk).updateAllWorldTransforms()
            val updatedProject = state.project.copy(skeleton = updatedSkeleton)
            state.copy(
                isIkActive = newIk,
                project = updatedProject,
                evaluatedPose = evaluatePose(updatedProject)
            )
        }
    }

    // Bone manipulation
    fun rotateSelectedBone(deltaDegrees: Float) {
        val selectedId = _uiState.value.selectedBoneId ?: return
        recordUndoState()

        _uiState.update { state ->
            val updatedBones = state.project.skeleton.bones.map { bone ->
                if (bone.id == selectedId) {
                    bone.copy(localAngle = (bone.localAngle + deltaDegrees) % 360f)
                } else bone
            }
            val updatedSkeleton = state.project.skeleton.copy(bones = updatedBones).updateAllWorldTransforms()
            val updatedProject = state.project.copy(skeleton = updatedSkeleton)
            state.copy(
                project = updatedProject,
                evaluatedPose = evaluatePose(updatedProject)
            )
        }
        triggerAutosave()
    }

    fun moveSelectedBoneIkTarget(worldTarget: Vector2D) {
        val selectedId = _uiState.value.selectedBoneId ?: return
        recordUndoState()

        _uiState.update { state ->
            val updatedBones = state.project.skeleton.bones.map { bone ->
                if (bone.id == selectedId) {
                    bone.copy(
                        isIkTarget = true,
                        ikTargetPosition = worldTarget
                    )
                } else bone
            }
            val updatedSkeleton = state.project.skeleton.copy(
                bones = updatedBones,
                isIkEnabled = true
            ).updateAllWorldTransforms()
            val updatedProject = state.project.copy(skeleton = updatedSkeleton)
            state.copy(
                project = updatedProject,
                evaluatedPose = evaluatePose(updatedProject)
            )
        }
        triggerAutosave()
    }

    // Mesh deformation
    fun moveSelectedVertex(delta: Vector2D) {
        val layerId = _uiState.value.selectedLayerId ?: return
        val vertexId = _uiState.value.selectedVertexId ?: return
        recordUndoState()

        _uiState.update { state ->
            val updatedLayers = state.project.layers.map { layer ->
                if (layer.id == layerId) {
                    val currentVertex = layer.meshGrid.vertices.find { it.id == vertexId }
                    val currentOffset = currentVertex?.offset ?: Vector2D.Zero
                    val newMesh = layer.meshGrid.updateVertexOffset(vertexId, currentOffset + delta)
                    layer.copy(meshGrid = newMesh)
                } else layer
            }
            val updatedProject = state.project.copy(layers = updatedLayers)
            state.copy(
                project = updatedProject,
                evaluatedPose = evaluatePose(updatedProject)
            )
        }
        triggerAutosave()
    }

    fun resetSelectedLayerMesh() {
        val layerId = _uiState.value.selectedLayerId ?: return
        recordUndoState()

        _uiState.update { state ->
            val updatedLayers = state.project.layers.map { layer ->
                if (layer.id == layerId) {
                    layer.copy(meshGrid = layer.meshGrid.reset())
                } else layer
            }
            val updatedProject = state.project.copy(layers = updatedLayers)
            state.copy(
                project = updatedProject,
                evaluatedPose = evaluatePose(updatedProject)
            )
        }
        triggerAutosave()
    }

    // Facial expression morphs
    fun setExpressionPreset(preset: ExpressionPreset) {
        recordUndoState()
        val newExpr = FacialExpression.fromPreset(preset)
        _uiState.update { state ->
            val updatedProject = state.project.copy(currentExpression = newExpr)
            state.copy(
                project = updatedProject,
                evaluatedPose = evaluatePose(updatedProject)
            )
        }
        triggerAutosave()
    }

    fun updateExpression(expression: FacialExpression) {
        _uiState.update { state ->
            val updatedProject = state.project.copy(currentExpression = expression)
            state.copy(
                project = updatedProject,
                evaluatedPose = evaluatePose(updatedProject)
            )
        }
        triggerAutosave()
    }

    // Layer management
    fun setLayerVisibility(layerId: String, isVisible: Boolean) {
        _uiState.update { state ->
            val updated = state.project.layers.map {
                if (it.id == layerId) it.copy(isVisible = isVisible) else it
            }
            val updatedProject = state.project.copy(layers = updated)
            state.copy(project = updatedProject, evaluatedPose = evaluatePose(updatedProject))
        }
        triggerAutosave()
    }

    fun setLayerOpacity(layerId: String, opacity: Float) {
        _uiState.update { state ->
            val updated = state.project.layers.map {
                if (it.id == layerId) it.copy(opacity = opacity.coerceIn(0f, 1f)) else it
            }
            val updatedProject = state.project.copy(layers = updated)
            state.copy(project = updatedProject, evaluatedPose = evaluatePose(updatedProject))
        }
        triggerAutosave()
    }

    fun moveLayerZ(layerId: String, direction: Int) {
        recordUndoState()
        _uiState.update { state ->
            val list = state.project.layers.sortedBy { it.zIndex }.toMutableList()
            val index = list.indexOfFirst { it.id == layerId }
            if (index != -1) {
                val newIndex = (index + direction).coerceIn(0, list.size - 1)
                val item = list.removeAt(index)
                list.add(newIndex, item)
                val reindexed = list.mapIndexed { idx, l -> l.copy(zIndex = idx) }
                val updatedProject = state.project.copy(layers = reindexed)
                state.copy(project = updatedProject, evaluatedPose = evaluatePose(updatedProject))
            } else state
        }
        triggerAutosave()
    }

    // Keyframe actions
    fun addKeyframeAtCurrentFrame(interpolation: InterpolationType = InterpolationType.EASE_IN_OUT) {
        recordUndoState()
        val currentFrame = _uiState.value.project.timeline.selectedFrameIndex
        val skeleton = _uiState.value.evaluatedPose.skeleton
        val layers = _uiState.value.evaluatedPose.layers
        val expr = _uiState.value.evaluatedPose.expression

        val boneAngles = skeleton.bones.associate { it.id to it.localAngle }
        val bonePositions = skeleton.bones.associate { it.id to it.localPosition }
        val ikTargets = skeleton.bones.filter { it.isIkTarget && it.ikTargetPosition != null }
            .associate { it.id to it.ikTargetPosition!! }
        val meshOffsets = layers.associate { l ->
            l.id to l.meshGrid.vertices.associate { v -> v.id to v.offset }
        }
        val layerOpacities = layers.associate { it.id to it.opacity }

        val newKey = Keyframe(
            frame = currentFrame,
            interpolation = interpolation,
            boneAngles = boneAngles,
            bonePositions = bonePositions,
            ikTargets = ikTargets,
            meshOffsets = meshOffsets,
            layerOpacities = layerOpacities,
            expression = expr
        )

        _uiState.update { state ->
            val updatedTimeline = state.project.timeline.addOrUpdateKeyframe(newKey)
            val updatedProject = state.project.copy(timeline = updatedTimeline)
            state.copy(
                project = updatedProject,
                evaluatedPose = evaluatePose(updatedProject)
            )
        }
        triggerAutosave()
    }

    fun removeKeyframeAtCurrentFrame() {
        recordUndoState()
        val currentFrame = _uiState.value.project.timeline.selectedFrameIndex
        _uiState.update { state ->
            val updatedTimeline = state.project.timeline.removeKeyframeAt(currentFrame)
            val updatedProject = state.project.copy(timeline = updatedTimeline)
            state.copy(
                project = updatedProject,
                evaluatedPose = evaluatePose(updatedProject)
            )
        }
        triggerAutosave()
    }

    fun setKeyframeInterpolation(type: InterpolationType) {
        val currentFrame = _uiState.value.project.timeline.selectedFrameIndex
        val key = _uiState.value.project.timeline.getKeyframeAt(currentFrame) ?: return
        recordUndoState()

        _uiState.update { state ->
            val updatedKey = key.copy(interpolation = type)
            val updatedTimeline = state.project.timeline.addOrUpdateKeyframe(updatedKey)
            val updatedProject = state.project.copy(timeline = updatedTimeline)
            state.copy(
                project = updatedProject,
                evaluatedPose = evaluatePose(updatedProject)
            )
        }
        triggerAutosave()
    }

    // Projects management
    fun loadTemplate(templateId: String) {
        val template = when (templateId) {
            "chibi_idle" -> ProjectTemplates.createChibiIdleProject()
            else -> ProjectTemplates.createAnimeWaveProject()
        }
        setProject(template, recordUndo = true)
    }

    fun importProjectFromJson(jsonString: String): Boolean {
        return try {
            val imported = repository.deserializeProject(jsonString)
            setProject(imported, recordUndo = true)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun exportProjectToJson(): String {
        return repository.serializeProject(_uiState.value.project)
    }

    private fun setProject(project: ProjectData, recordUndo: Boolean) {
        if (recordUndo) recordUndoState()
        val evaluated = evaluatePose(project)
        _uiState.update {
            it.copy(
                project = project,
                evaluatedPose = evaluated,
                selectedBoneId = project.skeleton.bones.firstOrNull()?.id,
                selectedLayerId = project.layers.firstOrNull()?.id,
                selectedVertexId = 4
            )
        }
        triggerAutosave()
    }

    private fun evaluatePose(project: ProjectData): Interpolator.EvaluatedPose {
        return Interpolator.evaluatePoseAtFrame(
            frame = project.timeline.currentFrame,
            keyframes = project.timeline.keyframes,
            baseSkeleton = project.skeleton,
            baseLayers = project.layers
        )
    }

    private fun triggerAutosave() {
        _uiState.update { it.copy(autosaveStatus = "Saving...") }
        viewModelScope.launch {
            repository.saveProject(_uiState.value.project)
            delay(300)
            _uiState.update { it.copy(autosaveStatus = "Autosaved") }
        }
    }
}
