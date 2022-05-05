package edu.gwu.CryptoApp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import edu.gwu.androidtweetsspring2022.R

class Search : AppCompatActivity() {

    // Search Bar
    private lateinit var searchEditText: EditText

    // Search
    private lateinit var MRButton: Button

    // View Map
    private lateinit var PWButton: Button

    // Top Headlines
    private lateinit var SearchButton: Button

    // shared prefs
    private lateinit var sharedPreference: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        title = "Menu"

        // init shared prefs
        sharedPreference = getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE)
        editor = sharedPreference.edit()

        // get previously searched term from shared prefs
        val previousSearch = sharedPreference.getString("searchtext", "")

        // Locates matching elements
        searchEditText = findViewById(R.id.searchEditText)
        // set text to previously searched term
        searchEditText.setText(previousSearch.toString())
        searchEditText.addTextChangedListener(textWatcher)
        SearchButton = findViewById(R.id.SearchButton)
        PWButton = findViewById(R.id.PWButton)
        MRButton = findViewById(R.id.MRButton)

        SearchButton.isEnabled = false

        //Button to view particular coin search
        SearchButton.setOnClickListener {
            saveSharedPrefs()
            val intent = Intent(this, PriceTrackingActivity::class.java)
            val searchVal = searchEditText.text
            intent.putExtra("search", searchVal.toString())
            startActivity(intent)
        }

        // View market rankings
        MRButton.setOnClickListener {
            val intent = Intent(this, PriceTrackingActivity::class.java)
            startActivity(intent)
        }

        //View Personal Watch List
        PWButton.setOnClickListener {
            val intent = Intent(this, WatchlistActivity::class.java)
            startActivity(intent)
        }
    }

    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        // We can use any of the three functions -- here, we just use onTextChanged -- the goal
        // is the enable the login button only if there is text in both the username & password fields.
        override fun onTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            // Kotlin shorthand for username.getText().toString()
            // .toString() is needed because getText() returns an Editable (basically a char array).
            val inputtedSearch: String = searchEditText.text.toString()
            val enableButton: Boolean =
                inputtedSearch.isNotBlank()

            // Kotlin shorthand for login.setEnabled(enableButton)
            SearchButton.isEnabled = enableButton
        }

        override fun afterTextChanged(p0: Editable?) {}

    }

    override fun onBackPressed() {
        super.onBackPressed()
        // save updated text on back pressed
        saveSharedPrefs()
    }

    private fun saveSharedPrefs() {
        editor.putString("searchtext", searchEditText.text.toString())
        editor.apply()
    }
}