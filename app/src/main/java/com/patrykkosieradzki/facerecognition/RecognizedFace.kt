package com.patrykkosieradzki.facerecognition

import android.graphics.PointF
import android.graphics.Rect

data class RecognizedFace(
    val face: List<PointF>,
    val boundingBox: Rect
)
