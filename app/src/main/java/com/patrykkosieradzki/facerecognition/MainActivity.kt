package com.patrykkosieradzki.facerecognition

import android.annotation.SuppressLint
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import com.patrykkosieradzki.facerecognition.ui.theme.MLKitFaceRecognitionExampleTheme

@ExperimentalPermissionsApi
class MainActivity : ComponentActivity() {

    private val viewModel: FaceRecognitionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MLKitFaceRecognitionExampleTheme {
                FaceRecognitionScreen(viewModel.state.value) {
                    viewModel.updateFace(it)
                }
            }
        }
    }
}

@ExperimentalPermissionsApi
@Composable
fun FaceRecognitionScreen(
    state: FaceRecognitionViewModel.State,
    onFaceChanged: (RecognizedFace?) -> Unit
) {
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    PermissionRequired(
        permissionState = cameraPermissionState,
        permissionNotGrantedContent = {
            LaunchedEffect(Unit) {
                cameraPermissionState.launchPermissionRequest()
            }
        },
        permissionNotAvailableContent = {
            Column {
                Text(
                    "Camera permission denied."
                )
            }
        }
    ) {
        FaceRecognitionScreenContent(state, onFaceChanged)
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun FaceRecognitionScreenContent(
    state: FaceRecognitionViewModel.State,
    onFaceChanged: (RecognizedFace?) -> Unit
) {
    Scaffold { _ ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            CameraPreview(Modifier.matchParentSize(), onFaceChanged)
            CameraOverlay(Modifier.matchParentSize(), state.face)
        }
    }
}

@Composable
fun CameraPreview(modifier: Modifier, onFaceChanged: (RecognizedFace?) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val executor = ContextCompat.getMainExecutor(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                    .build()

                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(Size(previewView.width, previewView.height))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setImageQueueDepth(10)
                    .build()
                    .apply {
                        setAnalyzer(executor, FaceAnalyzer(onFaceChanged))
                    }

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
            }, executor)
            previewView
        },
    )
}

@Composable
fun CameraOverlay(modifier: Modifier, face: RecognizedFace?) {
    Canvas(
        modifier = modifier
    ) {
        face?.let { it ->
            val oval = it.face
            for (i in oval.indices) {
                val current = oval[i]
                val next = if (i < oval.size - 1) {
                    oval[i + 1]
                } else {
                    oval[0]
                }
                drawLine(
                    Color.White,
                    Offset(current.x, current.y),
                    Offset(next.x, next.y),
                    strokeWidth = 3f
                )
            }
            val box = it.boundingBox
            drawRedLine(box.left, box.top, box.right, box.top)
            drawRedLine(box.right, box.top, box.right, box.bottom)
            drawRedLine(box.right, box.bottom, box.left, box.bottom)
            drawRedLine(box.left, box.bottom, box.left, box.top)
        }
    }
}

private fun DrawScope.drawRedLine(x1: Int, y1: Int, x2: Int, y2: Int) {
    drawLine(
        Color.Red,
        Offset(x1.toFloat(), y1.toFloat()),
        Offset(x2.toFloat(), y2.toFloat()),
        strokeWidth = 3f
    )
}

