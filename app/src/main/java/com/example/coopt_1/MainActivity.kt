package com.example.coopt_1

//Import these items

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.coopt_1.databinding.ActivityMainBinding
import org.json.JSONArray
import android.content.Intent
import android.view.View
import androidx.room.Room
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var db: BookDatabase

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = BookDatabase.getInstance(this)

        //Setting up Volley new request
        val queue = Volley.newRequestQueue(this)
        val resultTextView: TextView = binding.txtHoldApi
        //Image items
        val imageView:  ImageView = binding.imgRecieved
        // Button items
        val btnSaved: Button = binding.btnSaved
        val btnSearch: Button = binding.btnSearchForImage
        val btnAdd: Button = binding.btnSave

        // Initially sets the save button to invisible until a successful response is handled.
        btnAdd.visibility = View.INVISIBLE

        // Switches the view to the Saved Titles View
        btnSaved.setOnClickListener() {
            val switch = Intent(this, SavedViewActivity::class.java)
            startActivity(switch)
            finish()
        }

        btnSearch.setOnClickListener()
        {
            //Declare a EditText variable to hold the user input
            val userInput: EditText = binding.txtInputISBN
            //Works with ISBN10 and ISBN13
            val url = "https://openlibrary.org/isbn/${userInput.text}.json"

            //Clears the imageView
            Glide.with(this).clear(imageView)
            //Loading image
            val loadIMG = "https://media1.giphy.com/media/6036p0cTnjUrNFpAlr/giphy.gif?cid=ecf05e479j2w1xbpa3tk0fx0b5mo6nax6c74nd8ct4mk6b64&ep=v1_gifs_search&rid=giphy.gif&ct=g"

            val text = "Failed to load image"
            val duration = Toast.LENGTH_SHORT

            val toast = Toast.makeText(this, text, duration) // in Activity

            //Retrieves jsonObject
            val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
                { response ->

                    //Parse JSON
                    //Gets an array from json in the key called "covers"
                    val responseJSON: JSONArray = response.getJSONArray("covers")
                    //Stores the cover ID
                    //If it has a cover sends image to the imageView
                    //Using negative causes a failure
                    val imageId: String = responseJSON.getString(0)

                    //Loads received image
                    // With thumbnail url change the jpg if testing for errors
                    Glide.with(imageView)
                        .load("https://covers.openlibrary.org/b/id/${imageId}-L.jpg")
                        .thumbnail(Glide.with(imageView).load(loadIMG))
                        .into(imageView)
                    //Display book cover ID
                    resultTextView.text = response.getString("title")
                    btnAdd.visibility = View.VISIBLE
                },//If an error occurs in fetching the data
                {
                    resultTextView.text = "Failed to fetch the api"
                    btnAdd.visibility = View.INVISIBLE
                    toast.show()
                })

            // Add the request to the RequestQueue.
            queue.add(jsonObjectRequest)
        }

        btnAdd.setOnClickListener()
        {
            val bookDao = db.bookDao()
            val books: List<Book> = bookDao.getAll()
            var size = books.size + 1
            val userInput: EditText = binding.txtInputISBN
            val newBook = Book(uid = size,isbn = "${userInput.text}")
            lifecycleScope.launch {
                bookDao.insertBook(newBook)
            }

            val toast = Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT)
            toast.show()
        }
    }
}