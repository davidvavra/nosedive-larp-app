package com.patrykkosieradzki.facerecognition

import android.annotation.SuppressLint
import android.graphics.PointF
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class FaceAnalyzer(
    private val previewWidth: Int,
    private val previewHeight: Int,
    private val onFaceChanged: (RecognizedFace?) -> Unit
) : ImageAnalysis.Analyzer {

    private val realTimeOpts = FaceDetectorOptions.Builder()
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
        .setMinFaceSize(0.20f)
        .enableTracking()
        .build()

    private val detector = FaceDetection.getClient(realTimeOpts)
    private var lastFace: RecognizedFace? = null

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        mediaImage?.let {
            val inputImage =
                InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            detector.process(inputImage)
                .addOnSuccessListener { faces ->
                    val newFace = extractFace(faces, inputImage.width, inputImage.height)
                    if (newFace != null && newFace != lastFace) {
                        lastFace = newFace
                        onFaceChanged(lastFace)
                    }
                    imageProxy.close()
                }
                .addOnFailureListener {
                    onFaceChanged(null)
                    imageProxy.close()
                }
        }
    }

    private fun extractFace(faces: List<Face>, imageWidth: Int, imageHeight: Int): RecognizedFace? {
        faces.firstOrNull()?.let { face ->
            val faceContour = face.getContour(FaceContour.FACE)
            val box = face.boundingBox
            val boundingBox = listOf(
                PointF(box.left.toFloat(), box.top.toFloat()),
                PointF(box.right.toFloat(), box.top.toFloat()),
                PointF(box.right.toFloat(), box.bottom.toFloat()),
                PointF(box.left.toFloat(), box.bottom.toFloat())
            )
            faceContour?.let {
                return RecognizedFace(
                    convertToPreview(it.points, imageWidth, imageHeight),
                    convertToPreview(boundingBox, imageWidth, imageHeight)
                )
            }
        }
        return null
    }

    private fun convertToPreview(
        points: List<PointF>,
        imageWidth: Int,
        imageHeight: Int
    ): List<PointF> {
        val scaleX = previewWidth.toFloat() / imageHeight.toFloat()
        val scaleY = previewHeight.toFloat() / imageWidth.toFloat()
        return points.map {
            PointF(it.x * scaleX, it.y * scaleY)
        }
    }
}