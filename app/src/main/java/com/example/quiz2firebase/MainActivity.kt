package com.example.quiz2firebase

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val statusTextView = findViewById<TextView>(R.id.statusTextView)
        val testButton = findViewById<MaterialButton>(R.id.testButton)

        testButton.setOnClickListener {
            testFirebaseWrite(statusTextView)
        }
    }

    private fun testFirebaseWrite(statusTextView: TextView) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("connection_test")

        statusTextView.text = "Attempting to reach Pulse Cloud..."
        
        myRef.setValue("Pulse Sync: ${System.currentTimeMillis()}")
            .addOnSuccessListener {
                statusTextView.text = "Cloud Connection Verified!"
            }
            .addOnFailureListener { e ->
                statusTextView.text = "Connection Failed: ${e.message}"
            }
    }
}
