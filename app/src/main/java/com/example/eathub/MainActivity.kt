package com.example.eathub

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
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
import androidx.fragment.app.FragmentManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.content_main.*
import android.graphics.Bitmap
import android.os.PersistableBundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer.createSpeechRecognizer
import android.util.Log
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import java.io.ByteArrayOutputStream
import java.net.URL
import com.loopj.android.http.*
import java.io.IOException
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.eathub.global.AppConstants
import com.example.eathub.utils.SpUtils
import com.github.matteobattilana.weather.PrecipType
import com.github.matteobattilana.weather.WeatherViewSensorEventListener
import com.github.ybq.android.spinkit.SpinKitView
import kotlinx.android.synthetic.main.activity_main.view.*
import org.jetbrains.anko.alert
import java.lang.Exception
import java.nio.ByteBuffer
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity(), View.OnClickListener{

    private enum class Tag {
        Permission,
        Voice
    }
    val RESULT_SPEECH = 1
    val RESULT_CAMERA = 1
    val authKey = "abcb47ca5d3c40bea34b91c6745f19c7"

    var recipeID = arrayOfNulls<Int>(8)
    var imageList = arrayOfNulls<String>(8)
    var cardID = 7

    var cuisine = "Thai"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        var isFirstOpen = SpUtils.getBoolean(this, AppConstants.FIRST_OPEN);
        if (!isFirstOpen) {
            val intent = Intent(this, WelcomeGuideActivity::class.java)
            startActivity(intent)
            finish()
            return
        }


        setContentView(R.layout.activity_main)
        var mealType:String
        var color:String

        var hour = getNow().toInt()
        when (hour) {
            in 6..10 -> mealType = "breakfast"
            in 11..14 -> mealType = "main course"
            in 15..16 -> mealType = "snack"
            in 17..20 -> mealType = "main course"
            in 21..24 -> mealType = "side dish"
            else -> mealType = "side dish"
        }

        when (hour) {
            in 4..10 -> color = "#87CEFA"
            in 11..14 -> color = "#FFE4C4"
            in 15..18 -> color = "#FF6347"
            in 19..24 -> color = "#708090"
            else -> color = "#708090"
        }


        val mainLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.linearLayout)
        mainLayout.setBackgroundColor(Color.parseColor(color))

        weather_view.angle = 0
        weather_view.fadeOutPercent = 100f
        weather_view.speed = 20
        weather_view.emissionRate = 20f

        weather_view.setWeatherData(PrecipType.CLEAR)

        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),123)
        var j = -37.813611
        var w = 144.963056
        var gps = GPStracker(applicationContext)
        var location = gps.location
        if (location!=null) {
            j = location.latitude
            w = location.longitude
        }
        var weatherURL = "https://api.darksky.net/forecast/f896ddcb8922975d14aebd0100a9efe1/" + j +"," + w

        doAsync {
            val requestW:WeatherData = Request(weatherURL).requestWeather()
            uiThread {
                var weather = requestW.currently.icon
                if (weather == "rain"){
                    weather_view.setWeatherData(PrecipType.RAIN)
                }
                if (weather == "snow" || weather == "sleet") {
                    weather_view.setWeatherData(PrecipType.SNOW)
                }
                var wC = 0.1
                when (weather) {
                    "clear-day" -> wC = 0.9
                    "clear-night" -> wC = 0.9
                    "rain" -> wC = 0.05

                    "snow" -> wC = 0.25

                    "wind" -> wC = 0.7

                    "fog" -> wC = 0.15
                    "cloudy" -> wC = 0.5
                    "partly-cloudy-day" -> wC = 0.5
                    "partly-cloudy-night" -> wC = 0.5
                    else -> wC = 0.5
                }
                if (savedInstanceState==null){
                    weatherCuisine(wC.toFloat())
                    refreshAct()
                }

            }
        }

        if (savedInstanceState != null) {
            cuisine = savedInstanceState.getString("cuisine").toString()
        }

        val btn = findViewById<FloatingActionButton>(R.id.microphone)
        val btn2 = findViewById<FloatingActionButton>(R.id.camera)
        val refreshText = findViewById<TextView>(R.id.textView)
        val progress = findViewById<SpinKitView>(R.id.progressBar)


        btn.setOnClickListener(this)
        btn2.setOnClickListener(this)
        refreshText.setOnClickListener(this)
        progress.visibility = View.VISIBLE
        refreshText.visibility = View.GONE

        btn.hide()
        btn2.hide()


        if (Build.VERSION.SDK_INT >= 21) {
            val decorView:View = window.decorView
            val option = SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or SYSTEM_UI_FLAG_LAYOUT_STABLE
            decorView.systemUiVisibility = option
            window.statusBarColor = Color.TRANSPARENT
        }

        val cardAdapter = SimpleCardStackAdapter(this)
        val cardContainer = findViewById<CardContainer>(R.id.cardContainer)

        doAsync{
            var number = 8
            var type = mealType
            var offsetList = (0..50)
            var offset = offsetList.random()
            var url:String = "https://api.spoonacular.com/recipes/complexSearch?instructionRequired=true&addRecipeInformation=true&number=" + number + "&type=" + type + "&apiKey=" + authKey + "&cuisine=" + cuisine + "&offset=" + offset
            val request:RecipeData = Request(url).request()
            cardID = 7
            recipeID = arrayOfNulls<Int>(8)
            imageList = arrayOfNulls<String>(8)
            uiThread {
                progress.visibility = View.GONE
                refreshText.visibility = View.VISIBLE
                btn.show()
                btn2.show()

                val length = request.results.size
                var resultList = request.results
                Collections.shuffle(resultList)
                for (i in 0..(length-1)) {

                    val recipe = resultList[i]
                    var minutes = recipe.cookingMinutes
                    recipeID[i] = recipe.id
                    imageList[i] = recipe.image
                    if (minutes==0){
                        minutes = 15
                    }
                    var readyMin = "Ready in " + minutes.toString() + " minutes."
                    val cardModel = CardModel(recipe.title, readyMin, recipe.image)

                    addDissmissListener(cardModel)
                    addSwipeListener(cardContainer)

                    cardAdapter.add(cardModel)

                }

                cardContainer.adapter = cardAdapter

            }
        }




    }

    private fun weatherCuisine(sentiment: Float) {
        cuisine = "Thai"
        if (sentiment < 0.1){
            cuisine  = "Japanese"
        }
        if (sentiment < 0.2 && sentiment > 0.1) {
            cuisine = "British"
        }
        if (sentiment > 0.2 && sentiment < 0.3) {
            cuisine = "American"
        }
        if (sentiment > 0.3 && sentiment < 0.4) {
            cuisine = "Vietnamese"
        }
        if (sentiment > 0.4 && sentiment < 0.6) {
            cuisine = "Italian"
        }
        if (sentiment > 0.6 && sentiment < 0.8) {
            cuisine = "Mexican"
        }
        if (sentiment > 0.8) {
            cuisine = "Chinese"
        }
    }

    private fun setCuisine(sentiment: Float) {
        cuisine = "Thai"
        if (sentiment < 0.1){
            cuisine  = "Japanese"
            alert("😭😭😭")
        }
        if (sentiment < 0.2 && sentiment > 0.1) {
            cuisine = "British"
            alert("😱😱😱")
        }
        if (sentiment > 0.2 && sentiment < 0.3) {
            cuisine = "American"
            alert("😡😡😡")
        }
        if (sentiment > 0.3 && sentiment < 0.4) {
            cuisine = "Vietnamese"
            alert("😢😢😢")
        }
        if (sentiment > 0.4 && sentiment < 0.6) {
            cuisine = "Italian"
            alert("😳😳😳")
        }
        if (sentiment > 0.6 && sentiment < 0.8) {
            cuisine = "Mexican"
            alert("😝😝😝")
        }
        if (sentiment > 0.8) {
            cuisine = "Chinese"
            alert("😄😄😄")
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.microphone -> {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Please start your voice")
                try {
                    startActivityForResult(intent, RESULT_SPEECH)

                } catch (e: ActivityNotFoundException){
                    alert("Sorry, your device doesn't support speech to text.")
                }
            }
            R.id.camera -> {

                val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                takePhotoIntent.putExtra("android.intent.extras.CAMERA_FACING", 2)

                if (takePhotoIntent.resolveActivity(packageManager) != null) {
                    startActivityForResult(takePhotoIntent, RESULT_CAMERA)
                }

            }

            R.id.textView -> {
                refreshAct()
            }

        }
    }

    private fun alert(str:String) {
        val t = Toast.makeText(applicationContext, "Alert", Toast.LENGTH_SHORT)
        t.setText(str)
        t.show()
    }

    override protected fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESULT_SPEECH){
            if (resultCode == Activity.RESULT_OK && data!=null){
                val text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                try {
                    doAsync {
                        val sentiment = GetSentiment.getTheSentiment(text[0].toString())

                        uiThread {
                            setCuisine(sentiment)
                            refreshAct()
                        }
                    }


                } catch (e: Exception){
                    Log.e("TAG",e.toString())
                }

            }

        }
        if(requestCode == RESULT_CAMERA){
            if (resultCode == Activity.RESULT_OK && data!= null){

                val bmp = data.getParcelableExtra<Bitmap>("data")

                val stream = ByteArrayOutputStream()
                if(bmp!=null) {
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    val byteArray = stream.toByteArray()
                    bmp.recycle()



                    try {
                        doAsync {
                            val senti = EmotionRegnition.getEmotion(byteArray)
                            Log.e("TAG", senti.toString())
                            uiThread {
                                if (senti > 1) {
                                    alert("Sorry, we cannot recognize your facial expression, please take another photo.")
                                } else {
                                    setCuisine(senti)
                                    refreshAct()
                                }

                            }
                        }


                    } catch (e: Exception) {
                        Log.e("TAG", e.toString())
                    }
                }
            }


        }
    }

    private fun refreshAct() {
        this.recreate()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("cuisine", cuisine)
    }

    private fun addSwipeListener(cardContainer: CardContainer) {
        cardContainer.setOnSwipeListener(object: CardContainer.onSwipeListener {

            override fun onSwipe(scrollProgressPercent: Float) {
                var view:View = cardContainer.getSelectedView()
                if (scrollProgressPercent < 0) {
                    view.findViewById<View>(R.id.item_swipe_right_indicator).setAlpha(-scrollProgressPercent)
                }else{
                    view.findViewById<View>(R.id.item_swipe_right_indicator).setAlpha(0.toFloat())
                }
                if (scrollProgressPercent > 0) {
                    view.findViewById<View>(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent)
                }else{
                    view.findViewById<View>(R.id.item_swipe_left_indicator).setAlpha(0.toFloat())
                }


            }
        })
    }


    private fun addDissmissListener(cardModel: CardModel) {
        cardModel.onCardDismissedListener = object : CardModel.OnCardDismissedListener {
            override fun onLike(callback: CardContainer.OnLikeListener) {
                val intent = Intent(this@MainActivity, RecipeActivity::class.java)
                var bundle = Bundle()
                bundle.putString("recipeID", recipeID[cardID].toString())
                bundle.putString("image", imageList[cardID].toString())

                callback.choose()
                cardID = cardID - 1
                intent.putExtras(bundle)
                startActivity(intent)
            }

            override fun onDislike() {
                cardID = cardID - 1
            }
        }
    }


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

    private fun getNow(): String {
        if (android.os.Build.VERSION.SDK_INT >= 24){
            return SimpleDateFormat("HH").format(Date())
        }else{
            var tms = Calendar.getInstance()
            return tms.get(Calendar.YEAR).toString() + "-" + tms.get(Calendar.MONTH).toString() + "-" + tms.get(Calendar.DAY_OF_MONTH).toString() + " " + tms.get(Calendar.HOUR_OF_DAY).toString() + ":" + tms.get(Calendar.MINUTE).toString() +":" + tms.get(Calendar.SECOND).toString() +"." + tms.get(Calendar.MILLISECOND).toString()
        }

    }
}
