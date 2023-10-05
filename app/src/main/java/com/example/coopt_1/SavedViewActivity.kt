package com.example.coopt_1

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.coopt_1.databinding.SavedViewMainBinding

class SavedViewActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = SavedViewMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val btnSaved: Button = binding.btnSaved

        btnSaved.setOnClickListener() {
            val switch = Intent(this, MainActivity::class.java)
            startActivity(switch)
        }
    }
}