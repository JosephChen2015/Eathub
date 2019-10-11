package com.example.eathub

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu
import android.view.View
import android.view.MenuItem
//import com.andtinder.model.CardModel

import kotlinx.android.synthetic.main.activity_main.*
//import com.andtinder.view.SimpleCardStackAdapter
//import android.R
import com.kidach1.tinderswipe.model.CardModel
import com.kidach1.tinderswipe.view.SimpleCardStackAdapter
import com.kidach1.tinderswipe.view.CardContainer
import java.util.*
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.os.Build
import android.provider.MediaStore
import android.widget.LinearLayout
import androidx.fragment.app.FragmentManager
import com.algolia.instantsearch.voice.ui.Voice.isRecordAudioPermissionGranted
import com.algolia.instantsearch.voice.ui.VoiceInputDialogFragment
import com.algolia.instantsearch.voice.ui.VoicePermissionDialogFragment
import com.algolia.instantsearch.voice.VoiceSpeechRecognizer
import com.algolia.instantsearch.voice.ui.Voice
import com.algolia.instantsearch.voice.ui.Voice.shouldExplainPermission
import com.algolia.instantsearch.voice.ui.Voice.showPermissionRationale

import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.content_main.*


//import com.andtinder.view.CardContainer





class MainActivity : AppCompatActivity(), View.OnClickListener,VoiceSpeechRecognizer.ResultsListener{

    private val imgs = Arrays.asList(
        "http://img.peco-japan.com/image/93127",
        "https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcRmZfrA31MKjExHzG83ycVshteNDg5hUAoGZ30HzTu9so_PjXnftQ",
        "https://pbs.twimg.com/profile_images/3129300560/9c13c196eaa4f1940641f2cf08878727.jpeg",
        "https://pbs.twimg.com/profile_images/581025665727655936/9CnwZZ6j.jpg"
    )
    private enum class Tag {
        Permission,
        Voice
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val btn = findViewById<FloatingActionButton>(R.id.microphone)
        val btn2 = findViewById<FloatingActionButton>(R.id.camera)
        btn.setOnClickListener(this)
        btn2.setOnClickListener(this)

        val mainLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.linearLayout)
        mainLayout.setBackgroundColor(Color.parseColor("#2F4F4F"))

        val cardAdapter = SimpleCardStackAdapter(this)
        if (Build.VERSION.SDK_INT >= 21) {
            val decorView:View = window.decorView
            val option = SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or SYSTEM_UI_FLAG_LAYOUT_STABLE
            decorView.systemUiVisibility = option
            window.statusBarColor = Color.TRANSPARENT
        }


        for (i in 0..7) {
            val cardModel = CardModel("Dish Name", "Description for card.", imgs[i % 4])
//            addClickListener(cardModel)
//            addDissmissListener(cardModel)
            cardAdapter.add(cardModel)
        }

        val cardContainer = findViewById<CardContainer>(R.id.cardContainer)
        cardContainer.adapter = cardAdapter
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.microphone -> {
                if (!isRecordAudioPermissionGranted()) {
                    VoicePermissionDialogFragment().show(supportFragmentManager, "fafaef")
                } else {
                    VoiceInputDialogFragment().show(supportFragmentManager, "DIALOG_INPUT")
                }
            }
            R.id.camera -> {
                println("camera")
                val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePhotoIntent.resolveActivity(packageManager) != null) {
                    startActivityForResult(takePhotoIntent, 1)
                }
            }
            R.id.voicePermission -> {
                println("hahaha")
            }

        }
    }

    override fun onResults(possibleTexts: Array<out String>) {
        val voiceText = possibleTexts.firstOrNull()?.capitalize()
        println(voiceText)
    }

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (Voice.isRecordPermissionWithResults(requestCode, grantResults)) {
//            when {
//                Voice.isPermissionGranted(grantResults) -> showVoiceDialog()
//                shouldExplainPermission() -> showPermissionRationale(getPermissionView())
//                else -> Voice.showPermissionManualInstructions(getPermissionView())
//            }
//        }
//    }

    private fun showVoiceDialog() {
        getPermissionDialog()?.dismiss()
        (getVoiceDialog() ?: VoiceInputDialogFragment()).let {
            it.setSuggestions(
                "Phone case",
                "Running shoes"
            )
            it.show(supportFragmentManager, Tag.Voice.name)
        }
    }

    private fun getVoiceDialog() = (supportFragmentManager.findFragmentByTag(Tag.Voice.name) as? VoiceInputDialogFragment)

    private fun getPermissionDialog() = (supportFragmentManager.findFragmentByTag(Tag.Permission.name) as? VoicePermissionDialogFragment)

    private fun getPermissionView(): View = getPermissionDialog()!!.view!!.findViewById(R.id.positive)

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
