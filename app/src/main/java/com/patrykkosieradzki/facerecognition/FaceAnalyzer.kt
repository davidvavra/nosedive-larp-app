package com.patrykkosieradzki.facerecognition

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class FaceAnalyzer(private val onFaceChanged: (RecognizedFace?)->Unit) : ImageAnalysis.Analyzer {

    private val realTimeOpts = FaceDetectorOptions.Builder()
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
        .setMinFaceSize(0.20f)
        .enableTracking()
        .build()

    private val detector = FaceDetection.getClient(realTimeOpts)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        mediaImage?.let {
            val inputImage =
                InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            detector.process(inputImage)
                .addOnSuccessListener { faces ->
                    onFaceChanged(extractFace(faces))
                    imageProxy.close()
                }
                .addOnFailureListener {
                    onFaceChanged(null)
                    imageProxy.close()
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }

    private fun extractFace(faces: List<Face>): RecognizedFace? {
        faces.firstOrNull()?.let { face ->
            val faceContour = face.getContour(FaceContour.FACE)
            faceContour?.let {
                return RecognizedFace(it.points, face.boundingBox)
            }
        }
        return null
    }
}