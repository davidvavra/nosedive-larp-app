package com.patrykkosieradzki.facerecognition

import android.graphics.PointF

data class RecognizedFace(
    val face: List<PointF>,
    val boundingBox: List<PointF>
)
