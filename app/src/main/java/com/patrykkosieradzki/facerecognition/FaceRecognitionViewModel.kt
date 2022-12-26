package com.patrykkosieradzki.facerecognition

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class FaceRecognitionViewModel: ViewModel() {

    val state = mutableStateOf(State(null))

    fun updateFace(face: RecognizedFace?) {
        state.value = state.value.copy(face = face)
    }

    data class State(
        val face: RecognizedFace?
    )
}