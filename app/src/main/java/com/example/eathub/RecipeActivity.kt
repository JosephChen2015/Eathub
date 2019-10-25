package com.example.eathub

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.PersistableBundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.kidach1.tinderswipe.model.CardModel
import kotlinx.android.synthetic.main.recipe_layout.*
import kotlinx.android.synthetic.main.recipe_layout.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

class RecipeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recipe_layout)

        var mTbMain = findViewById<androidx.appcompat.widget.Toolbar>(R.id.my_toolbar)
        mTbMain.setTitle("Instructions")
        setSupportActionBar(mTbMain)
        val authKey = "abcb47ca5d3c40bea34b91c6745f19c7"

        var texttt = findViewById<AutoSplitTextView>(R.id.txt)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        var bundle = this.intent.extras
        var recipeID = bundle?.getString("recipeID")
        var image = bundle?.getString("image")


        var drawable = Drawable.createFromStream(URL(image).openStream(),"image.jpg")
        recipe.imageView2.setImageDrawable(drawable)

        doAsync {

            var url: String =
                "https://api.spoonacular.com/recipes/" + recipeID + "/analyzedInstructions" + "?apiKey=" + authKey
            val request: Instruction = Request(url).requestIn()
                var steps = request.steps

                uiThread {
                    var txt1 = ""
                    if (request.name!="noInstruction") {
                        var len = steps.size

                        for (i in 0..len - 1) {
                            txt1 = txt1 + steps[i].number + ". " + steps[i].step + "\n\n"
                        }

                        texttt.text = txt1
                        var newText = autoSplitText(texttt, "1.")
                        texttt.text = newText

                    } else {
                        txt1 = "Sorry, the instruction for this dish is temporarily unavailable.Please try another dish."
                        texttt.text = txt1
                    }

                }

        }


    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
//                returnHome(this)
//                return true
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun autoSplitText(tv: TextView, indent: String): String {
        val rawText = tv.text.toString() //原始文本
        val tvPaint = tv.paint //paint，包含字体等信息
        val tvWidth = (tv.width - tv.paddingLeft - tv.paddingRight).toFloat() //控件可用宽度

        //将缩进处理成空格
        var indentSpace = ""
        var indentWidth = 0f
        if (!TextUtils.isEmpty(indent)) {
            val rawIndentWidth = tvPaint.measureText(indent)
            if (rawIndentWidth < tvWidth) {
                indentWidth = tvPaint.measureText(indentSpace)
                while (indentWidth < rawIndentWidth) {
                    indentSpace += " "
                    indentWidth = tvPaint.measureText(indentSpace)
                }
            }
        }

        //将原始文本按行拆分
        val rawTextLines =
            rawText.replace("\r".toRegex(), "").split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val sbNewText = StringBuilder()
        for (rawTextLine in rawTextLines) {
            if (tvPaint.measureText(rawTextLine) <= tvWidth) {
                //如果整行宽度在控件可用宽度之内，就不处理了
                sbNewText.append(rawTextLine)
            } else {
                //如果整行宽度超过控件可用宽度，则按字符测量，在超过可用宽度的前一个字符处手动换行
                var lineWidth = 0f
                var cnt = 0
                while (cnt != rawTextLine.length) {
                    val ch = rawTextLine[cnt]
                    //从手动换行的第二行开始，加上悬挂缩进
                    if (lineWidth < 0.1f && cnt != 0) {
                        sbNewText.append(indentSpace)
                        lineWidth += indentWidth
                    }
                    lineWidth += tvPaint.measureText(ch.toString())
                    if (lineWidth <= tvWidth) {
                        sbNewText.append(ch)
                    } else {
                        sbNewText.append("\n")
                        lineWidth = 0f
                        --cnt
                    }
                    ++cnt
                }
            }
            sbNewText.append("\n")
        }

        //把结尾多余的\n去掉
        if (!rawText.endsWith("\n")) {
            sbNewText.deleteCharAt(sbNewText.length - 1)
        }

        return sbNewText.toString()
    }


}