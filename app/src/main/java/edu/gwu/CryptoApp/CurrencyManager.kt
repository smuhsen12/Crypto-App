package edu.gwu.CryptoApp

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import edu.gwu.CryptoApp.model.Constants
import edu.gwu.CryptoApp.model.Currency
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject

class CurrencyManager {


    private val okHttpClient: OkHttpClient

    init {
        val builder = OkHttpClient.Builder()

        // This will cause all network traffic to be logged to the console for easy debugging
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(loggingInterceptor)

        okHttpClient = builder.build()
    }


    fun retrieveCurrencyJSON(): MutableList<Currency> {
        val request: Request = Request.Builder()
            .url("https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?start=1&limit=50&convert=USD&CMC_PRO_API_KEY=${Constants.COIN_MARKET_CAP_API_KEY}")
            .get()
            .build()

        // This executes the request and waits for a response from the server
        val response: Response = okHttpClient.newCall(request).execute()
        val responseBody: String? = response.body?.string()

        // The .isSuccessful checks to see if the status code is 200-299
        if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
            val currencyList = mutableListOf<Currency>()
            // Parse our way through the JSON hierarchy, picking out what we need from each currency
            val json = JSONObject(responseBody)
            val statuses: JSONArray = json.getJSONArray("data")

            for (i in 0 until statuses.length()) {
                val curr: JSONObject = statuses.getJSONObject(i)
                Log.d("RESPONSE: ", curr.toString())

                val id = curr.getString("id")
                val name = curr.getString("name")
                val symbol = curr.getString("symbol")
                val slug = curr.getString("slug")
                val cmc_rank = curr.getString("cmc_rank")
                val num_market_pairs = curr.getString("num_market_pairs")
                val circulating_supply = curr.getString("circulating_supply")
                val total_supply = curr.getString("total_supply")
                val max_supply = curr.getString("max_supply")
                val last_updated = curr.getString("last_updated")
                val quoteJSON = curr.getJSONObject("quote")
                val usd = quoteJSON.getJSONObject("USD")
                val price = usd.getString("price")
                val volume_24h = usd.getString("volume_24h")
                val percent_change_1h = usd.getString("percent_change_1h")
                val percent_change_24h = usd.getString("percent_change_24h")
                val percent_change_7d = usd.getString("percent_change_7d")
                val market_cap = usd.getString("market_cap")

                val usdObj = USD( // create a USD object
                    price = price,
                    volumeChangeH = volume_24h,
                    percentChange1H = percent_change_1h,
                    percentChange24H = percent_change_24h,
                    percentChangeD = percent_change_7d,
                    marketCap = market_cap
                )

                val coinObj = COIN( // not required atm
                    percentChange1H = "curr_percent_change_1h",
                    lastUpdated = "",
                    percentChange24H = "curr_percent_change_24h",
                    marketCap = "curr_market_cap",
                    volumeChangeH = "curr_volume_24h",
                    price = "curr_price",
                    volumeH = "",
                    marketCapDominance = "0",
                    percentChangeD = "0"
                )

                val quote = Quote( // create a quote object
                    usd = usdObj,
                    coin = coinObj
                )

                val currency = Currency( // create a currency object
                    id = id,
                    name = name,
                    symbol = symbol,
                    cmc_rank = cmc_rank,
                    slug = slug,
                    quote = quote,
                )

                currencyList.add(currency) // add the currency to our list
            }

            return currencyList
        }

        return mutableListOf()
    }

    fun retrieveBookmarkedCurrencyJSON(mContext: Context): MutableList<Currency> {
        val request: Request = Request.Builder()
            .url("https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?start=1&limit=50&convert=USD&CMC_PRO_API_KEY=${Constants.COIN_MARKET_CAP_API_KEY}")
            .get()
            .build()

        // This executes the request and waits for a response from the server
        val response: Response = okHttpClient.newCall(request).execute()
        val responseBody: String? = response.body?.string()

        // The .isSuccessful checks to see if the status code is 200-299
        if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
            val currencyList = mutableListOf<Currency>()
            // Parse our way through the JSON hierarchy, picking out what we need from each currency
            val json = JSONObject(responseBody)
            val statuses: JSONArray = json.getJSONArray("data")

            for (i in 0 until statuses.length()) {
                val curr: JSONObject = statuses.getJSONObject(i)
                Log.d("RESPONSE: ", curr.toString())

                val id = curr.getString("id")
                val name = curr.getString("name")
                val symbol = curr.getString("symbol")
                val slug = curr.getString("slug")
                val cmc_rank = curr.getString("cmc_rank")
                val quoteJSON = curr.getJSONObject("quote")
                val usd = quoteJSON.getJSONObject("USD")
                val price = usd.getString("price")
                val volume_24h = usd.getString("volume_24h")
                val percent_change_1h = usd.getString("percent_change_1h")
                val percent_change_24h = usd.getString("percent_change_24h")
                val percent_change_7d = usd.getString("percent_change_7d")
                val market_cap = usd.getString("market_cap")

                val usdObj = USD(
                    price = price,
                    volumeChangeH = volume_24h,
                    percentChange1H = percent_change_1h,
                    percentChange24H = percent_change_24h,
                    percentChangeD = percent_change_7d,
                    marketCap = market_cap
                )

                val coinObj = COIN( // TODO: not required atm
                    percentChange1H = "curr_percent_change_1h",
                    lastUpdated = "",
                    percentChange24H = "curr_percent_change_24h",
                    marketCap = "curr_market_cap",
                    volumeChangeH = "curr_volume_24h",
                    price = "curr_price",
                    volumeH = "",
                    marketCapDominance = "0",
                    percentChangeD = "0"
                )

                val quote = Quote(
                    usd = usdObj,
                    coin = coinObj
                )

                val currency = Currency(
                    id = id,
                    name = name,
                    symbol = symbol,
                    cmc_rank = cmc_rank,
                    slug = slug,
                    quote = quote,
                )
                // get SharedPreferences
                val sharedPrefs: SharedPreferences =
                    mContext.getSharedPreferences("BookmarkedCurrencies", Context.MODE_PRIVATE)
                // check if the currency is bookmarked previously
                val isBookmarked = sharedPrefs.getBoolean(currency.slug, false)
                if (isBookmarked) {
                    // add to list only if it is bookmarked/watch-listed
                    currencyList.add(currency)
                }
            }

            return currencyList
        }

        return mutableListOf()
    }

    fun retrieveCurrencyConversionJSON(fromSymbol: String, toCurrency: String): Currency {
        val request: Request = Request.Builder()
            .url("https://pro-api.coinmarketcap.com/v2/tools/price-conversion?symbol=${fromSymbol}&amount=50&convert=${toCurrency}&CMC_PRO_API_KEY=${Constants.COIN_MARKET_CAP_API_KEY}")
            .get()
            .build()

        // This executes the request and waits for a response from the server
        val response: Response = okHttpClient.newCall(request).execute()
        val responseBody: String? = response.body?.string()

        // The .isSuccessful checks to see if the status code is 200-299
        if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
            val currencyList = mutableListOf<Currency>()
            // Parse our way through the JSON hierarchy, picking out what we need from each currency
            val json = JSONObject(responseBody)
            val statuses: JSONArray = json.getJSONArray("data")

            for (i in 0 until statuses.length()) {
                val curr: JSONObject = statuses.getJSONObject(i)
                Log.d("CONVERTED: ", curr.toString())

                val id = curr.getString("id")
                val name = curr.getString("name")
                val symbol = curr.getString("symbol")

                val quoteJSON = curr.getJSONObject("quote")
                val convertedCurrency = quoteJSON.getJSONObject(toCurrency)
                val price = convertedCurrency.getString("price")

                val usdObj = USD(
                    price = price,
                    volumeChangeH = "volume_24h",
                    percentChange1H = "percent_change_1h",
                    percentChange24H = "percent_change_24h",
                    percentChangeD = "percent_change_7d",
                    marketCap = "market_cap"
                )

                val coinObj = COIN( // TODO: not required atm
                    percentChange1H = "curr_percent_change_1h",
                    lastUpdated = "",
                    percentChange24H = "curr_percent_change_24h",
                    marketCap = "curr_market_cap",
                    volumeChangeH = "curr_volume_24h",
                    price = "curr_price",
                    volumeH = "",
                    marketCapDominance = "0",
                    percentChangeD = "0"
                )

                val quote = Quote(
                    usd = usdObj,
                    coin = coinObj
                )

                val currency = Currency(
                    id = id,
                    name = name,
                    symbol = symbol,
                    cmc_rank = "cmc_rank",
                    slug = "slug",
                    quote = quote,
                )
                // add to list
                currencyList.add(currency)
            }

            return currencyList[0]
        }

        return Currency()
    }

}