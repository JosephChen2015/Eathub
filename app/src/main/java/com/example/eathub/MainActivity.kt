package com.example.eathub

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu
import android.view.View
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
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
import android.graphics.Bitmap
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import java.io.ByteArrayOutputStream
import java.net.URL
import com.loopj.android.http.*
import java.io.IOException
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread







class MainActivity : AppCompatActivity(), View.OnClickListener,VoiceSpeechRecognizer.ResultsListener{

    private enum class Tag {
        Permission,
        Voice
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val btn = findViewById<FloatingActionButton>(R.id.microphone)
        val btn2 = findViewById<FloatingActionButton>(R.id.camera)
        val refreshText = findViewById<TextView>(R.id.textView)

        btn.setOnClickListener(this)
        btn2.setOnClickListener(this)
        refreshText.setOnClickListener(this)

        val mainLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.linearLayout)
        mainLayout.setBackgroundColor(Color.parseColor("#2F4F4F"))


        if (Build.VERSION.SDK_INT >= 21) {
            val decorView:View = window.decorView
            val option = SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or SYSTEM_UI_FLAG_LAYOUT_STABLE
            decorView.systemUiVisibility = option
            window.statusBarColor = Color.TRANSPARENT
        }

        val cardAdapter = SimpleCardStackAdapter(this)
        val cardContainer = findViewById<CardContainer>(R.id.cardContainer)

        doAsync{
            var url:String = "https://api.edamam.com/search?q=chicken&app_id=\$f199cde3&app_key=\$08b6bfc892c17d28fa620fa36621cf33 -&from=0&to=8&"
            val request:RecipeData = Request(url).request()
            uiThread {

                for (i in 0..7) {

                    val recipe = request.hits[i].recipe
                    val cardModel = CardModel(recipe.label, recipe.dietLabels[0], recipe.image)
                    addClickListener(cardModel)
                    addDissmissListener(cardModel)
                    cardAdapter.add(cardModel)
                }

                cardContainer.adapter = cardAdapter

//                cardContainer.bringToFront()
            }
        }



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

                val bitmap = takePhotoIntent.getParcelableExtra("BitmapImage") as Bitmap
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
//                val image = stream.toByteArray()

                if (takePhotoIntent.resolveActivity(packageManager) != null) {
                    startActivityForResult(takePhotoIntent, 1)
                }
            }

            R.id.textView -> {
                refreshAct()
            }

        }
    }

    private fun refreshAct() {
        this.recreate()
    }


    private fun addClickListener(cardModel: CardModel) {
        cardModel.onClickListener = object : CardModel.OnClickListener {
            override fun OnClickListener() {
                val intent = Intent()
                intent.setClass(this@MainActivity, RecipeActivity::class.java)
//                startActivity(intent)

            }
        }
    }

    private fun addDissmissListener(cardModel: CardModel) {
        cardModel.onCardDismissedListener = object : CardModel.OnCardDismissedListener {
            override fun onLike(callback: CardContainer.OnLikeListener) {
//                refreshAct()
            }

            override fun onDislike() {
//                refreshAct()
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
