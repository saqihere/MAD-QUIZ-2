package com.example.quiz2firebase

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quiz2firebase.models.Article
import java.net.URL
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executors

class NewsAdapter(
    private var articles: List<Article>,
    private val onItemClick: (Article) -> Unit
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private val executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())

    class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.newsImage)
        val title: TextView = view.findViewById(R.id.newsTitle)
        val source: TextView = view.findViewById(R.id.newsSource)
        val date: TextView = view.findViewById(R.id.newsDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = articles[position]
        holder.title.text = article.title
        holder.source.text = article.source.name
        holder.date.text = article.publishedAt

        // Simple image loader without external libraries
        holder.image.setImageResource(R.mipmap.ic_launcher) // Placeholder
        executor.execute {
            try {
                val url = URL(article.image)
                val bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                handler.post {
                    holder.image.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        holder.itemView.setOnClickListener { onItemClick(article) }
    }

    override fun getItemCount() = articles.size

    fun updateData(newArticles: List<Article>) {
        articles = newArticles
        notifyDataSetChanged()
    }
}
