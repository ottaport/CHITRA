package com.ottaport.chitra

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ottaport.chitra.ui.canvas.AnimationCanvas
import com.ottaport.chitra.ui.dialogs.ExportDialog
import com.ottaport.chitra.ui.dialogs.HelpDialog
import com.ottaport.chitra.ui.dialogs.ProjectDialog
import com.ottaport.chitra.ui.panels.AudioPanel
import com.ottaport.chitra.ui.panels.ExpressionPanel
import com.ottaport.chitra.ui.panels.LayersPanel
import com.ottaport.chitra.ui.panels.MeshPanel
import com.ottaport.chitra.ui.panels.RiggingPanel
import com.ottaport.chitra.ui.theme.StudioIcons
import com.ottaport.chitra.ui.timeline.PlaybackControls
import com.ottaport.chitra.ui.timeline.TimelineView
import com.ottaport.chitra.viewmodel.ChitraViewModel
import com.ottaport.chitra.viewmodel.StudioMode

@Composable
fun ChitraApp(viewModel: ChitraViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    var showExportDialog by remember { mutableStateOf(false) }
    var showProjectDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding(),
        containerColor = Color.White,
        topBar = {
            StudioTopBar(
                projectName = uiState.project.name,
                autosaveStatus = uiState.autosaveStatus,
                canUndo = uiState.canUndo,
                canRedo = uiState.canRedo,
                onUndo = { viewModel.undo() },
                onRedo = { viewModel.redo() },
                onResetView = { viewModel.resetView() },
                onOpenProjects = { showProjectDialog = true },
                onOpenExport = { showExportDialog = true },
                onOpenHelp = { showHelpDialog = true }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            // Mode Selector Tabs (Rig Bones, Animate, Mesh Warp, Layers, Facial Morphs, Audio)
            StudioModeSelector(
                activeMode = uiState.currentMode,
                onSelectMode = { viewModel.setMode(it) }
            )

            HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

            // Central Viewport: Multi-touch Animation Canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                AnimationCanvas(
                    uiState = uiState,
                    onSelectBone = { viewModel.selectBone(it) },
                    onRotateBone = { viewModel.rotateSelectedBone(it) },
                    onMoveIkTarget = { viewModel.moveSelectedBoneIkTarget(it) },
                    onSelectVertex = { viewModel.selectVertex(it) },
                    onMoveVertex = { viewModel.moveSelectedVertex(it) },
                    onUpdateZoomAndPan = { zoom, pan -> viewModel.setZoomAndPan(zoom, pan) },
                    onResetView = { viewModel.resetView() }
                )
            }

            HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

            // Contextual Mode Tool Panel
            when (uiState.currentMode) {
                StudioMode.RIGGING, StudioMode.ANIMATE -> {
                    RiggingPanel(
                        uiState = uiState,
                        onSelectBone = { viewModel.selectBone(it) },
                        onRotateBone = { viewModel.rotateSelectedBone(it) },
                        onToggleIk = { viewModel.toggleIkMode() }
                    )
                }
                StudioMode.LAYERS -> {
                    LayersPanel(
                        uiState = uiState,
                        onSelectLayer = { viewModel.selectLayer(it) },
                        onToggleVisibility = { id, vis -> viewModel.setLayerVisibility(id, vis) },
                        onSetOpacity = { id, op -> viewModel.setLayerOpacity(id, op) },
                        onMoveLayerZ = { id, dir -> viewModel.moveLayerZ(id, dir) }
                    )
                }
                StudioMode.MESH -> {
                    MeshPanel(
                        uiState = uiState,
                        onSelectVertex = { viewModel.selectVertex(it) },
                        onMoveVertex = { viewModel.moveSelectedVertex(it) },
                        onResetMesh = { viewModel.resetSelectedLayerMesh() }
                    )
                }
                StudioMode.EXPRESSIONS -> {
                    ExpressionPanel(
                        uiState = uiState,
                        onSelectPreset = { viewModel.setExpressionPreset(it) },
                        onUpdateExpression = { viewModel.updateExpression(it) }
                    )
                }
                StudioMode.AUDIO -> {
                    AudioPanel(uiState = uiState)
                }
            }

            HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

            // Playback Controls (Play/Pause, Step, Loop, Onion-Skin, IK, Add Keyframe)
            PlaybackControls(
                uiState = uiState,
                onTogglePlay = { viewModel.togglePlayback() },
                onStepPrev = { viewModel.stepBackward() },
                onStepNext = { viewModel.stepForward() },
                onFirstFrame = { viewModel.seekToFrame(0) },
                onToggleLoop = { viewModel.toggleLoop() },
                onToggleOnionSkin = { viewModel.toggleOnionSkin() },
                onToggleIk = { viewModel.toggleIkMode() },
                onAddKeyframe = { viewModel.addKeyframeAtCurrentFrame() },
                onRemoveKeyframe = { viewModel.removeKeyframeAtCurrentFrame() },
                onSetInterpolation = { viewModel.setKeyframeInterpolation(it) }
            )

            HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

            // Visual Dope Sheet Scrubber Timeline
            TimelineView(
                timeline = uiState.project.timeline,
                onSeekFrame = { viewModel.seekToFrame(it) }
            )
        }
    }

    // Dialogs
    if (showExportDialog) {
        ExportDialog(
            uiState = uiState,
            onExportProjectJson = { viewModel.exportProjectToJson() },
            onDismiss = { showExportDialog = false }
        )
    }

    if (showProjectDialog) {
        ProjectDialog(
            uiState = uiState,
            onLoadTemplate = { viewModel.loadTemplate(it) },
            onImportJson = { viewModel.importProjectFromJson(it) },
            onDismiss = { showProjectDialog = false }
        )
    }

    if (showHelpDialog) {
        HelpDialog(onDismiss = { showHelpDialog = false })
    }
}

@Composable
private fun StudioTopBar(
    projectName: String,
    autosaveStatus: String,
    canUndo: Boolean,
    canRedo: Boolean,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onResetView: () -> Unit,
    onOpenProjects: () -> Unit,
    onOpenExport: () -> Unit,
    onOpenHelp: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // App Branding & Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                // App logo badge
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .border(1.dp, Color(0xFF0F172A), RoundedCornerShape(6.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = com.ottaport.chitra.R.drawable.chitra_logo),
                        contentDescription = "CHITRA Logo",
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(Modifier.width(8.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "CHITRA",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFF1F5F9))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = autosaveStatus,
                                fontSize = 9.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                    Text(
                        text = projectName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF475569)
                    )
                }
            }

            // Top action icons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Undo
                IconButton(
                    onClick = onUndo,
                    enabled = canUndo,
                    modifier = Modifier.size(32.dp).testTag("btn_undo")
                ) {
                    Icon(
                        StudioIcons.Undo,
                        "Undo",
                        tint = if (canUndo) Color(0xFF0F172A) else Color(0xFFCBD5E1),
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Redo
                IconButton(
                    onClick = onRedo,
                    enabled = canRedo,
                    modifier = Modifier.size(32.dp).testTag("btn_redo")
                ) {
                    Icon(
                        StudioIcons.Redo,
                        "Redo",
                        tint = if (canRedo) Color(0xFF0F172A) else Color(0xFFCBD5E1),
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Reset / Fit View
                IconButton(
                    onClick = onResetView,
                    modifier = Modifier.size(32.dp).testTag("btn_fit_view")
                ) {
                    Icon(StudioIcons.Refresh, "Fit View", tint = Color(0xFF475569), modifier = Modifier.size(18.dp))
                }

                // Projects
                IconButton(
                    onClick = onOpenProjects,
                    modifier = Modifier.size(32.dp).testTag("btn_projects")
                ) {
                    Icon(StudioIcons.Projects, "Projects", tint = Color(0xFF475569), modifier = Modifier.size(18.dp))
                }

                // Export
                IconButton(
                    onClick = onOpenExport,
                    modifier = Modifier.size(32.dp).testTag("btn_export")
                ) {
                    Icon(StudioIcons.Share, "Export", tint = Color(0xFF2563EB), modifier = Modifier.size(18.dp))
                }

                // Help
                IconButton(
                    onClick = onOpenHelp,
                    modifier = Modifier.size(32.dp).testTag("btn_help")
                ) {
                    Icon(StudioIcons.Info, "Help", tint = Color(0xFF64748B), modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun StudioModeSelector(
    activeMode: StudioMode,
    onSelectMode: (StudioMode) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(StudioMode.values()) { mode ->
            val isSelected = mode == activeMode
            Box(
                modifier = Modifier
                    .height(30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        width = 1.dp,
                        color = if (isSelected) Color(0xFF0F172A) else Color(0xFFE2E8F0),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .background(if (isSelected) Color(0xFF0F172A) else Color(0xFFFAFAFC))
                    .clickable { onSelectMode(mode) }
                    .padding(horizontal = 10.dp)
                    .testTag("mode_${mode.name.lowercase()}"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = mode.title,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else Color(0xFF475569)
                )
            }
        }
    }
}
