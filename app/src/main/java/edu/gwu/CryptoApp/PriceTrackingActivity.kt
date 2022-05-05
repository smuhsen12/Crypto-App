package edu.gwu.CryptoApp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.gwu.androidtweetsspring2022.R
import edu.gwu.CryptoApp.model.Currency
import org.jetbrains.anko.doAsync


class PriceTrackingActivity : AppCompatActivity() {
    private val tag = "PriceTrackingActivity"
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView

    // data vars
    private lateinit var currencyManager: CurrencyManager
    private lateinit var currencyList: MutableList<Currency>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_price_tracking)
        // init progress bar
        loadingProgressBar = findViewById(R.id.progressBar)
        loadingProgressBar.visibility = View.VISIBLE
        // init CurrencyManager
        currencyManager = CurrencyManager()
        // init views
        recyclerView = findViewById(R.id.recyclerViewCurrencies)

        // populate recyclerview
        populateCurrency()
    }

    override fun onResume() {
        super.onResume()
        // to make sure the list is always up to date
        populateCurrency()
        Log.d(tag, "onResume")
    }


    private fun populateCurrency() {
        loadingProgressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        // Networking needs to be done on a background thread
        doAsync {
            // Use our CurrencyManager to get Currency JSON from the CMC API. If there is network
            // connection issues, the catch-block will fire and we'll show the user an error message.
            // load currency details from API
            currencyList = try {
                currencyManager.retrieveCurrencyJSON()
            } catch (exception: Exception) {
                Log.e(tag, "Retrieving Currency List failed", exception)
                mutableListOf()
            }
            val searchedCurrency = intent.getStringExtra("search")
            if (searchedCurrency != null) {
                for (currency in currencyList) {
                    // if the currency name contains the search term, update the list to have only the currency that matched
                    if (currency.name?.contains(searchedCurrency, true) == true || currency.symbol?.contains(searchedCurrency, true) == true) {
                        currencyList = mutableListOf(currency)
                        Log.d(tag, "Currency: ${currency.name}")
                    }
                }
            }

            runOnUiThread {
                // update recyclerview and show prompt
                if (currencyList.isNotEmpty()) {
                    val adapter = CurrencyItemAdapter(this@PriceTrackingActivity, currencyList)
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(this@PriceTrackingActivity)
                    loadingProgressBar.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                } else {
                    // failure message, hide loading indicator
                    loadingProgressBar.visibility = View.GONE
                    Toast.makeText(
                        this@PriceTrackingActivity,
                        getString(R.string.failed_to_retrieve),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}