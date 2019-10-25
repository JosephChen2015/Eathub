package com.example.eathub

import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log

class SampleRecognitionListener : RecognitionListener{

    override fun onResults(p0: Bundle?) {
        val result = SpeechRecognizer.RESULTS_RECOGNITION
        Log.e("TAG", result)
    }

    override fun onReadyForSpeech(p0: Bundle?) {
        Log.e("TAG", "onReadyForSpeech")
    }

    override fun onBeginningOfSpeech() {

    }

    override fun onRmsChanged(p0: Float) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBufferReceived(p0: ByteArray?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onEndOfSpeech() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onError(p0: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPartialResults(p0: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onEvent(p0: Int, p1: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}