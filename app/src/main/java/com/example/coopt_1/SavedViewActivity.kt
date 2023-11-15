package com.example.coopt_1

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.coopt_1.databinding.SavedViewMainBinding
import org.json.JSONArray

class SavedViewActivity : AppCompatActivity() {

    private lateinit var db: BookDatabase

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = SavedViewMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the database instance
        db = BookDatabase.getInstance(this)

        val btnDelete: Button = binding.btnDelete
        val btnSaved: Button = binding.btnSaved
        val btnPrev: Button = binding.btnPrev
        val btnNext: Button = binding.btnNext
        val btnGenerateQRCode: Button = binding.btnGenerateQRCode
        val imgResponse: ImageView = binding.imgResponse
        val txtTitle: TextView = binding.txtTitle

        val bookDao = db.bookDao()

        // Retrieves all our books in the database
        val books: List<Book> = bookDao.getAll()

        var index = 0
        // Determines if we have any books
        if (books.isEmpty()) {
            // If no books are present
            txtTitle.text = "No Saved Books"
            // Changes visibility of buttons.
            btnPrev.visibility = View.INVISIBLE
            btnNext.visibility = View.INVISIBLE
            btnGenerateQRCode.visibility = View.INVISIBLE
        } else {
            // If at least 1 book is present
            // Retrieves current book
            val current = books[index]
            // Get details of the book
            getBookDetails(current, imgResponse, txtTitle)
            // Determines the book's position
            checkIndex(index, (books.size - 1), btnPrev, btnNext)
            // Show the Generate QR Code button
            btnGenerateQRCode.visibility = View.VISIBLE
        }

        // Switches to the main activity
        btnSaved.setOnClickListener() {
            val switch = Intent(this, MainActivity::class.java)
            startActivity(switch)
            finish()
        }

        // Delete button clears the whole database.
        btnDelete.setOnClickListener() {
            // Checks if any books are present
            if (!books.isEmpty()) {
                val toast = Toast.makeText(this, "Entry Deleted", Toast.LENGTH_SHORT)
                toast.show()
                bookDao.delete(books[index])
                recreate()
            } else {
                val toast = Toast.makeText(this, "No Books to Delete", Toast.LENGTH_SHORT)
                toast.show()
            }
        }

        // Button to move between entries
        btnPrev.setOnClickListener() {
            index--
            val current = books[index]
            getBookDetails(current, imgResponse, txtTitle)
            checkIndex(index, (books.size - 1), btnPrev, btnNext)
        }

        // Button to move between entries
        btnNext.setOnClickListener() {
            index++
            val current = books[index]
            getBookDetails(current, imgResponse, txtTitle)
            checkIndex(index, (books.size - 1), btnPrev, btnNext)
        }

// Button to generate QR code
        btnGenerateQRCode.setOnClickListener {
            val currentBook: Book = books[index]
            val isbn = currentBook.isbn.toString() // Explicitly convert to String if needed
            val qrCodeSize = 500 // Set your desired QR code size

            // Generate QR code
            val qrCodeBitmap = QRCodeGenerator.generateQRCode(isbn, qrCodeSize, qrCodeSize)

            // Check if QR code was generated successfully
            if (qrCodeBitmap != null) {
                imgResponse.setImageBitmap(qrCodeBitmap)
            } else {
                // Handle the case where QR code generation failed
                Toast.makeText(this, "Failed to generate QR code", Toast.LENGTH_SHORT).show()
            }
        }

    }

    // Requests details of an individual book entry
    private fun getBookDetails(current: Book, imgResponse: ImageView, txtTitle: TextView) {
        val queue = Volley.newRequestQueue(this)

        val url = "https://openlibrary.org/isbn/${current.isbn}.json"

        // Clears the imageView
        Glide.with(this).clear(imgResponse)
        // Loading image
        val loadIMG =
            "https://media1.giphy.com/media/6036p0cTnjUrNFpAlr/giphy.gif?cid=ecf05e479j2w1xbpa3tk0fx0b5mo6nax6c74nd8ct4mk6b64&ep=v1_gifs_search&rid=giphy.gif&ct=g"

        // Retrieves jsonObject
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->

                // Parse JSON
                // Gets an array from JSON in the key called "covers"
                val responseJSON: JSONArray = response.getJSONArray("covers")
                // Stores the cover ID
                // If it has a cover sends image to the imageView
                // Using negative causes a failure
                val imageId: String = responseJSON.getString(0)

                // Loads received image
                // With thumbnail URL change the jpg if testing for errors
                Glide.with(imgResponse)
                    .load("https://covers.openlibrary.org/b/id/${imageId}-L.jpg")
                    .thumbnail(Glide.with(imgResponse).load(loadIMG))
                    .into(imgResponse)
                // Display book cover ID
                txtTitle.text = response.getString("title")
            },
            // If an error occurs in fetching the data
            {
                val text = "Failed to load image"
                val duration = Toast.LENGTH_SHORT

                val toast = Toast.makeText(this, text, duration) // in Activity

                toast.show()
            })

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest)
    }

    private fun checkIndex(index: Int, booksSize: Int, btnPrev: Button, btnNext: Button) {
        if (index == 0) {
            btnPrev.visibility = View.INVISIBLE
        } else {
            btnPrev.visibility = View.VISIBLE
        }
        if (index == booksSize) {
            btnNext.visibility = View.INVISIBLE
        } else {
            btnNext.visibility = View.VISIBLE
        }
    }
}
