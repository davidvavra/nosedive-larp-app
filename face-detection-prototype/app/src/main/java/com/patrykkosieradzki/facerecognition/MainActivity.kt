package com.patrykkosieradzki.facerecognition

import android.annotation.SuppressLint
import android.graphics.PointF
import android.os.Bundle
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            if (state.face != null) {
                FaceOverlay(Modifier.matchParentSize(), state.face.face)
                RatingOverlay(state.face.boundingBox[1], state.face.boundingBox[2])
            }
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
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()

                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(Size(previewView.width, previewView.height))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setImageQueueDepth(10)
                    .build()
                    .apply {
                        setAnalyzer(
                            executor,
                            FaceAnalyzer(previewView.width, previewView.height, onFaceChanged)
                        )
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
fun FaceOverlay(modifier: Modifier, face: List<PointF>) {
    Canvas(
        modifier = modifier
    ) {
        drawShape(face, Color.White)
    }
}

private fun DrawScope.drawShape(points: List<PointF>, color: Color) {
    for (i in points.indices) {
        val current = points[i]
        val next = if (i < points.size - 1) {
            points[i + 1]
        } else {
            points[0]
        }
        drawLine(
            color,
            Offset(current.x, current.y),
            Offset(next.x, next.y),
            strokeWidth = 3f
        )
    }
}

@Composable
fun RatingOverlay(topLeft: PointF, bottomLeft: PointF) {
    val height = (bottomLeft.y - topLeft.y).toDp()
    Box(
        modifier = Modifier
            .offset(topLeft.x.toDp() + 10.dp, topLeft.y.toDp())
            .defaultMinSize(minHeight = height),
        contentAlignment = Alignment.Center
    ) {
        RatingOverlayContent()
    }
}

@Composable
private fun Float.toDp(): Dp {
    return (this / LocalContext.current.resources.displayMetrics.density).dp
}

@Composable
fun RatingOverlayContent() {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.bara),
                contentDescription = "avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.White, CircleShape)
            )
            Text(
                "Bára",
                modifier = Modifier.padding(start = 4.dp),
                style = TextStyle(
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                    color = Color.White,
                    fontWeight = FontWeight.Light,
                    fontSize = 50.sp
                )
            )
        }
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                "4.2",
                Modifier.padding(start = 44.dp),
                style = TextStyle(
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                    color = Color.White,
                    fontWeight = FontWeight.Normal,
                    fontSize = 50.sp
                )
            )
            Text(
                "83",
                fontWeight = FontWeight.Normal,
                fontSize = 25.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 5.dp, start = 4.dp)
            )
        }

    }
}

@Composable
@androidx.compose.ui.tooling.preview.Preview
fun RatingOverlayContentPreview() {
    RatingOverlayContent()
}

