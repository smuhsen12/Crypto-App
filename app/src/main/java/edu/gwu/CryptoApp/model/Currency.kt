package edu.gwu.CryptoApp.model

import edu.gwu.CryptoApp.COIN
import edu.gwu.CryptoApp.Quote
import edu.gwu.CryptoApp.USD
import java.io.Serializable

// Currency Object data class

data class Currency(
    val id: String?,
    val name: String?,
    val symbol: String?,
    val cmc_rank: String?,
    val slug: String?,
    val quote: Quote?,
) : Serializable {
    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        Quote(
            COIN("", "", "", "", "", ""),
            USD("", "", "", "", "", "")
        )
    )
}



