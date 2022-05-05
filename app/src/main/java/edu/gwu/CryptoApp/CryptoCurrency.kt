package edu.gwu.CryptoApp

// Data class for crypto currency, constructed from JSON

data class USD(
    val fullyDilutedMarketCap: String = "0.0",
    val percentChange1H: String = "0.0",
    val lastUpdated: String = "",
    val percentChange24H: String = "0.0",
    val marketCap: String = "0.0",
    val volumeChangeH: String = "0.0",
    val price: String = "0.0",
    val volumeH: Long = 0,
    val marketCapDominance: Int = 0,
    val percentChangeD: String = "0.0"
)

data class COIN(
    val fullyDilutedMarketCap: String = "0.0",
    val percentChange1H: String = "0",
    val lastUpdated: String = "",
    val percentChange24H: String = "0",
    val marketCap: String = "0",
    val volumeChangeH: String = "0",
    val price: String = "0",
    val volumeH: String = "0",
    val marketCapDominance: String = "0",
    val percentChangeD: String = "0"
)


data class Quote(
    val coin: COIN,
    val usd: USD
)

// To be used later
//data class Status(val errorMessage: String = "",
//                  val elapsed: Int = 0,
//                  val creditCount: Int = 0,
//                  val errorCode: Int = 0,
//                  val timestamp: String = "")


//data class Crypto(val data: List<DataItem>?,
//                  val status: Status)


//data class DataItem(val symbol: String = "",
//                    val circulatingSupply: Int = 0,
//                    val lastUpdated: String = "",
//                    val totalSupply: Int = 0,
//                    val cmcRank: Int = 0,
//                    val tags: List<String>?,
//                    val dateAdded: String = "",
//                    val quote: Quote,
//                    val numMarketPairs: Int = 0,
//                    val name: String = "",
//                    val maxSupply: Int = 0,
//                    val id: Int = 0,
//                    val slug: String = "")




