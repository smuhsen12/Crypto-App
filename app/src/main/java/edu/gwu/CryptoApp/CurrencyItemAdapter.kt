package edu.gwu.CryptoApp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import edu.gwu.androidtweetsspring2022.R
import edu.gwu.CryptoApp.model.Currency
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread


class CurrencyItemAdapter(
    private val mContext: Context,
    private val currencyList: MutableList<Currency>
) :
    RecyclerView.Adapter<CurrencyItemAdapter.ViewHolder>() {
    // A ViewHolder represents the Views that comprise a single row in our list (e.g.
    // our row to display a Tweet contains three TextViews and one ImageView).
    // The "rootLayout" passed into the constructor comes from onCreateViewHolder. From the root layout, we can
    // call findViewById to search through the hierarchy to find the Views we care about in our new row.
    class ViewHolder(rootLayout: View) : RecyclerView.ViewHolder(rootLayout) {
        val currencyName: TextView = rootLayout.findViewById(R.id.currency_name)
        val currencySymbol: TextView = rootLayout.findViewById(R.id.currency_symbol)
        val marketCap: TextView = rootLayout.findViewById(R.id.market_cap)
        val priceInUSD: TextView = rootLayout.findViewById(R.id.price_in_usd)
        val cardView: CardView = rootLayout.findViewById(R.id.cardView)
        val image: ImageView = rootLayout.findViewById(R.id.currencyImg)
    }

    // The RecyclerView needs a "fresh" / new row, so we need to:
    // 1. Read in the XML file for the row type
    // 2. Use the new row to build a ViewHolder to return
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // A LayoutInflater is an object that knows how to read & parse an XML file
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)

        // Read & parse the XML file to create a new row at runtime
        // The 'inflate' function returns a reference to the root layout (the "top" view in the hierarchy) in our newly created row
        val rootLayout: View = layoutInflater.inflate(R.layout.currency_row_item, parent, false)

        // We can now create a ViewHolder from the root view
        return ViewHolder(rootLayout)
    }


    // The RecyclerView is ready to display a new (or recycled) row on the screen, represented a our ViewHolder.
    // We're given the row position / index that needs to be rendered.
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currency = currencyList[position]

        // update text views
        holder.currencyName.text = mContext.getString(R.string.curr_name) + currency.name
        holder.currencySymbol.text = mContext.getString(R.string.symbol_short) + currency.symbol
        holder.marketCap.text =
            mContext.getString(R.string.mrkt_cap) + currency.quote?.usd?.marketCap
        holder.priceInUSD.text =
            mContext.getString(R.string.price_short) + currency.quote?.usd?.price

        // set on item click listener to open browser when tapped
        holder.cardView.setOnClickListener {
            val url = "https://coinmarketcap.com/currencies/" + currency.slug
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            mContext.startActivity(intent)
        }
        // set on item long click listener to open dialog for currency conversion.
        holder.cardView.setOnLongClickListener {

            doAsync {
                // Use our CurrencyManager to get Currency JSON from the CMC API. If there is network
                // connection issues, the catch-block will fire and we'll show the user an error message.
                // load currency details from API
                // data vars
                val currencyManager = CurrencyManager()

                val currencyEUR: Currency = try {
                    currencyManager.retrieveCurrencyConversionJSON(
                        currency.symbol!!,
                        "EUR"
                    )
                } catch (exception: Exception) {
                    Log.e("EUR", "Retrieving Currency Conversion failed", exception)
                    Currency()
                }
                val currencyCAD: Currency = try {
                    currencyManager.retrieveCurrencyConversionJSON(
                        currency.symbol!!,
                        "CAD"
                    )
                } catch (exception: Exception) {
                    Log.e("CAD", "Retrieving Currency Conversion failed", exception)
                    Currency()
                }

                mContext.runOnUiThread {
                    // update recyclerview and show prompt
                    val builder = AlertDialog.Builder(mContext)
                    builder.setTitle(currency.name + getString(R.string.conversion))
                    val message = currency.symbol + ": " + getString(R.string.curr_conversion) + "\n" +
                            "\n" + currency.symbol + ": " + currency.quote?.usd?.price + " USD" +
                            "\n" + currency.symbol + ": " + currencyCAD.quote?.usd?.price + " CAD" +
                            "\n" + currency.symbol + ": " + currencyEUR.quote?.usd?.price + " EUR"
                    builder.setMessage(message)
                    builder.setPositiveButton(android.R.string.ok) { dialog, _ ->
                        dialog.dismiss() // dismiss
                    }.show()
                }
            }
            true
        }

        val sharedPrefs: SharedPreferences =
            mContext.getSharedPreferences("BookmarkedCurrencies", Context.MODE_PRIVATE)
        var isBookmarked = sharedPrefs.getBoolean(currency.slug, false)
        // set click listener to bookmark currency
        holder.image.setOnClickListener {
            // save this currency on click
            isBookmarked = if (isBookmarked) {
                // remove from bookmarks
                sharedPrefs.edit().remove(currency.slug).apply()
                false
            } else {
                // save this currency on click
                sharedPrefs.edit().putBoolean(currency.slug, true).apply()
                true
            }
            // tell the adapter to update the item, recreate this view
            notifyItemChanged(position)
            Log.d("CurrencyItemAdapter", "onClick: clicked on bookmark")
        }
        if (isBookmarked) {
            // update background color if bookmarked
            holder.image.setColorFilter(mContext.getColor(R.color.gold))
        } else {
            holder.image.setColorFilter(mContext.getColor(R.color.purple_200))
        }
    }

    // How many rows (total) do you want the adapter to render?
    override fun getItemCount(): Int {
        return currencyList.size
    }
}