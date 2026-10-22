package com.ottaport.chitra.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object StudioIcons {
    val Play = Icons.Default.PlayArrow
    val Add = Icons.Default.Add
    val Delete = Icons.Default.Delete
    val Refresh = Icons.Default.Refresh
    val Info = Icons.Default.Info
    val Share = Icons.Default.Share
    val Projects = Icons.Default.Menu

    val Pause: ImageVector = ImageVector.Builder(
        name = "Pause",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(6f, 19f)
            horizontalLineTo(10f)
            verticalLineTo(5f)
            horizontalLineTo(6f)
            verticalLineTo(19f)
            close()
            moveTo(14f, 5f)
            verticalLineTo(19f)
            horizontalLineTo(18f)
            verticalLineTo(5f)
            horizontalLineTo(14f)
            close()
        }
    }.build()

    val SkipNext: ImageVector = ImageVector.Builder(
        name = "SkipNext",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(6f, 18f)
            lineTo(14.5f, 12f)
            lineTo(6f, 6f)
            verticalLineTo(18f)
            close()
            moveTo(16f, 6f)
            verticalLineTo(18f)
            horizontalLineTo(18f)
            verticalLineTo(6f)
            horizontalLineTo(16f)
            close()
        }
    }.build()

    val SkipPrevious: ImageVector = ImageVector.Builder(
        name = "SkipPrevious",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(6f, 6f)
            horizontalLineTo(8f)
            verticalLineTo(18f)
            horizontalLineTo(6f)
            close()
            moveTo(9.5f, 12f)
            lineTo(18f, 18f)
            verticalLineTo(6f)
            lineTo(9.5f, 12f)
            close()
        }
    }.build()

    val Undo: ImageVector = ImageVector.Builder(
        name = "Undo",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(12.5f, 8f)
            curveTo(8.91f, 8f, 5.8f, 9.42f, 3.53f, 11.7f)
            lineTo(0f, 8.17f)
            verticalLineTo(18f)
            horizontalLineTo(9.83f)
            lineTo(6.36f, 14.53f)
            curveTo(8.06f, 12.83f, 10.4f, 11.77f, 12.5f, 11.77f)
            curveTo(16.5f, 11.77f, 19.8f, 14.39f, 21.05f, 18f)
            lineTo(23.9f, 17.07f)
            curveTo(22.28f, 12.39f, 17.78f, 8f, 12.5f, 8f)
            close()
        }
    }.build()

    val Redo: ImageVector = ImageVector.Builder(
        name = "Redo",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(18.47f, 11.7f)
            curveTo(16.2f, 9.42f, 13.09f, 8f, 9.5f, 8f)
            curveTo(4.22f, 8f, 0.22f, 12.39f, 0.1f, 17.07f)
            lineTo(2.95f, 18f)
            curveTo(4.2f, 14.39f, 7.5f, 11.77f, 11.5f, 11.77f)
            curveTo(13.6f, 11.77f, 15.94f, 12.83f, 17.64f, 14.53f)
            lineTo(14.17f, 18f)
            horizontalLineTo(24f)
            verticalLineTo(8.17f)
            lineTo(18.47f, 11.7f)
            close()
        }
    }.build()

    val Visibility: ImageVector = ImageVector.Builder(
        name = "Visibility",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(12f, 4.5f)
            curveTo(7f, 4.5f, 2.73f, 7.61f, 1f, 12f)
            curveTo(2.73f, 16.39f, 7f, 19.5f, 12f, 19.5f)
            curveTo(17f, 19.5f, 21.27f, 16.39f, 23f, 12f)
            curveTo(21.27f, 7.61f, 17f, 4.5f, 12f, 4.5f)
            close()
            moveTo(12f, 17f)
            curveTo(9.24f, 17f, 7f, 14.76f, 7f, 12f)
            curveTo(7f, 9.24f, 9.24f, 7f, 12f, 7f)
            curveTo(14.76f, 7f, 17f, 9.24f, 17f, 12f)
            curveTo(17f, 14.76f, 14.76f, 17f, 12f, 17f)
            close()
        }
    }.build()

    val VisibilityOff: ImageVector = ImageVector.Builder(
        name = "VisibilityOff",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(12f, 7f)
            curveTo(13.66f, 7f, 15f, 8.34f, 15f, 10f)
            curveTo(15f, 10.64f, 14.8f, 11.23f, 14.46f, 11.72f)
            lineTo(16.73f, 13.99f)
            curveTo(18.11f, 12.92f, 19.23f, 11.56f, 20f, 10f)
            curveTo(18.36f, 6.32f, 14.52f, 3.75f, 10f, 3.75f)
            curveTo(8.73f, 3.75f, 7.51f, 4.02f, 6.39f, 4.52f)
            lineTo(8.28f, 6.41f)
            curveTo(8.81f, 6.15f, 9.39f, 6f, 10f, 6f)
            close()
            moveTo(2.71f, 3.16f)
            lineTo(1.44f, 4.43f)
            lineTo(4.68f, 7.67f)
            curveTo(3.38f, 8.87f, 2.37f, 10.33f, 1.75f, 12f)
            curveTo(3.39f, 15.68f, 7.23f, 18.25f, 11.75f, 18.25f)
            curveTo(13.23f, 18.25f, 14.65f, 17.92f, 15.93f, 17.32f)
            lineTo(19.29f, 20.68f)
            lineTo(20.56f, 19.41f)
            lineTo(2.71f, 3.16f)
            close()
        }
    }.build()

    val ArrowUp: ImageVector = ImageVector.Builder(
        name = "ArrowUp",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(4f, 12f)
            lineTo(5.41f, 13.41f)
            lineTo(11f, 7.83f)
            verticalLineTo(20f)
            horizontalLineTo(13f)
            verticalLineTo(7.83f)
            lineTo(18.59f, 13.41f)
            lineTo(20f, 12f)
            lineTo(12f, 4f)
            close()
        }
    }.build()

    val ArrowDown: ImageVector = ImageVector.Builder(
        name = "ArrowDown",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(20f, 12f)
            lineTo(18.59f, 10.59f)
            lineTo(13f, 16.17f)
            verticalLineTo(4f)
            horizontalLineTo(11f)
            verticalLineTo(16.17f)
            lineTo(5.41f, 10.59f)
            lineTo(4f, 12f)
            lineTo(12f, 20f)
            close()
        }
    }.build()

    val ArrowLeft: ImageVector = ImageVector.Builder(
        name = "ArrowLeft",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(15.41f, 7.41f)
            lineTo(14f, 6f)
            lineTo(8f, 12f)
            lineTo(14f, 18f)
            lineTo(15.41f, 16.59f)
            lineTo(10.83f, 12f)
            close()
        }
    }.build()

    val ArrowRight: ImageVector = ImageVector.Builder(
        name = "ArrowRight",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(10f, 6f)
            lineTo(8.59f, 7.41f)
            lineTo(13.17f, 12f)
            lineTo(8.59f, 16.59f)
            lineTo(10f, 18f)
            lineTo(16f, 12f)
            close()
        }
    }.build()
}
