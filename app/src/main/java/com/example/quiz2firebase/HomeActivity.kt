package com.example.quiz2firebase

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quiz2firebase.api.NewsApiService
import com.example.quiz2firebase.models.NewsResponse
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NewsAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var errorLayout: View
    private lateinit var errorText: TextView
    private lateinit var countrySpinner: Spinner
    private lateinit var refreshButton: FloatingActionButton

    private val API_KEY = "5e9c6ab87ce13d55e232563e325af761"
    
    private val countries = mapOf(
        "United States" to "us",
        "Pakistan" to "pk",
        "United Kingdom" to "gb",
        "India" to "in",
        "Saudi Arabia" to "sa",
        "UAE" to "ae"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        recyclerView = findViewById(R.id.newsRecyclerView)
        progressBar = findViewById(R.id.progressBar)
        errorLayout = findViewById(R.id.errorLayout)
        errorText = findViewById(R.id.errorText)
        countrySpinner = findViewById(R.id.countrySpinner)
        refreshButton = findViewById(R.id.refreshButton)

        setupRecyclerView()
        setupSpinner()

        refreshButton.setOnClickListener {
            val selectedCountry = countries[countrySpinner.selectedItem.toString()] ?: "us"
            fetchNews(selectedCountry)
        }
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = NewsAdapter(emptyList()) { article ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("article", article)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }

    private fun setupSpinner() {
        val countryList = countries.keys.toList()
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, countryList)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        countrySpinner.adapter = spinnerAdapter

        countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCountryCode = countries[countryList[position]] ?: "us"
                fetchNews(selectedCountryCode)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun fetchNews(country: String) {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        errorLayout.visibility = View.GONE

        val retrofit = Retrofit.Builder()
            .baseUrl("https://gnews.io/api/v4/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(NewsApiService::class.java)
        service.getTopHeadlines(country = country, apiKey = API_KEY).enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body() != null) {
                    recyclerView.visibility = View.VISIBLE
                    adapter.updateData(response.body()!!.articles)
                } else {
                    showError("API Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                showError("Network Error: ${t.message}")
            }
        })
    }

    private fun showError(message: String) {
        errorText.text = message
        errorLayout.visibility = View.VISIBLE
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
