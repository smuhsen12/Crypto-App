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


class WatchlistActivity : AppCompatActivity() {
    private val tag = "PriceTrackingActivity"
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView

    // data vars
    private lateinit var currencyManager: CurrencyManager
    private lateinit var currencyList: MutableList<Currency>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watchlist)

        // init progress bar
        loadingProgressBar = findViewById(R.id.progressBar)
        loadingProgressBar.visibility = View.VISIBLE
        // init CurrencyManager
        currencyManager = CurrencyManager()
        // init views
        recyclerView = findViewById(R.id.recyclerViewWatchlist)

        // populate recyclerview
        populateCurrency()
    }


    private fun populateCurrency() {
        // Networking needs to be done on a background thread
        doAsync {
            // Use our CurrencyManager to get Currency JSON from the CMC API. If there is network
            // connection issues, the catch-block will fire and we'll show the user an error message.
            // load currency details from API
            currencyList = try {
                currencyManager.retrieveBookmarkedCurrencyJSON(mContext = this@WatchlistActivity)
            } catch (exception: Exception) {
                Log.e(tag, "Retrieving Currency List failed", exception)
                mutableListOf()
            }

            runOnUiThread {
                // update recyclerview and show prompt
                if (currencyList.isNotEmpty()) {
                    val adapter = CurrencyItemAdapter(this@WatchlistActivity, currencyList)
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(this@WatchlistActivity)
                    loadingProgressBar.visibility = View.GONE
                } else {
                    // failure message, hide loading indicator
                    loadingProgressBar.visibility = View.GONE
                    Toast.makeText(
                        this@WatchlistActivity,
                        getString(R.string.failed_to_retrieve),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}