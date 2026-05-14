package com.example.quiz2firebase

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.quiz2firebase.models.Article
import com.google.android.material.button.MaterialButton
import java.net.URL
import java.util.concurrent.Executors

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val toolbar = findViewById<Toolbar>(R.id.detailToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        val article = intent.getSerializableExtra("article") as? Article

        if (article != null) {
            val imgView = findViewById<ImageView>(R.id.detailImage)
            val titleView = findViewById<TextView>(R.id.detailTitle)
            val sourceDateView = findViewById<TextView>(R.id.detailSourceDate)
            val descriptionView = findViewById<TextView>(R.id.detailDescription)
            val contentView = findViewById<TextView>(R.id.detailContent)
            val btnReadFull = findViewById<MaterialButton>(R.id.btnReadFull)

            titleView.text = article.title
            sourceDateView.text = "${article.source.name} • ${article.publishedAt}"
            descriptionView.text = article.description
            contentView.text = article.content

            // Improved image loader using background thread
            val executor = Executors.newSingleThreadExecutor()
            val handler = Handler(Looper.getMainLooper())
            executor.execute {
                try {
                    val url = URL(article.image)
                    val bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    handler.post {
                        imgView.setImageBitmap(bitmap)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            btnReadFull.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                startActivity(browserIntent)
            }
        }
    }
}
