package automate.profit.autocoin.exchange.arbitrage.orderbook

import automate.profit.autocoin.exchange.orderbook.OrderBook
import automate.profit.autocoin.exchange.orderbook.OrderBookListener
import automate.profit.autocoin.exchange.ticker.CurrencyPairWithExchangePair
import mu.KLogging

/**
 * Calculates arbitrage opportunities based on order books
 */
class TwoLegOrderBookArbitrageMonitor(
        private val currencyPairWithExchangePair: CurrencyPairWithExchangePair,
        private val profitCache: TwoLegOrderBookArbitrageProfitCache,
        private val profitCalculator: TwoLegOrderBookArbitrageProfitCalculator
) {
    companion object : KLogging()

    private val currencyPair = currencyPairWithExchangePair.currencyPair
    private val exchangePair = currencyPairWithExchangePair.exchangePair
    private var firstExchangeOrderBook: OrderBook? = null
    private var secondExchangeOrderBook: OrderBook? = null

    private fun onFirstExchangeOrderBook(orderBook: OrderBook) {
        firstExchangeOrderBook = orderBook
        onOrderBooks()
    }

    private fun onSecondExchangeOrderBook(orderBook: OrderBook) {
        secondExchangeOrderBook = orderBook
        onOrderBooks()
    }

    private fun onOrderBooks() {
        if (firstExchangeOrderBook != null && secondExchangeOrderBook != null) {
            val orderBookPair = OrderBookPair(firstExchangeOrderBook!!, secondExchangeOrderBook!!)
            val profit = profitCalculator.calculateProfit(currencyPairWithExchangePair, orderBookPair)
            if (profit == null) {
                profitCache.removeProfit(currencyPairWithExchangePair)
            } else {
                profitCache.setProfit(profit)
            }
        }
    }

    fun getOrderBookListeners(): Pair<OrderBookListener, OrderBookListener> = Pair(
            object : OrderBookListener {
                override fun currencyPair() = currencyPair
                override fun exchange() = exchangePair.firstExchange

                override fun onNoNewOrderBook(orderBook: OrderBook?) {
                    if (orderBook != null) onFirstExchangeOrderBook(orderBook)
                }

                override fun onOrderBook(orderBook: OrderBook) {
                    onFirstExchangeOrderBook(orderBook)
                }
            },
            object : OrderBookListener {
                override fun currencyPair() = currencyPair
                override fun exchange() = exchangePair.secondExchange

                override fun onNoNewOrderBook(orderBook: OrderBook?) {
                    if (orderBook != null) onSecondExchangeOrderBook(orderBook)
                }

                override fun onOrderBook(orderBook: OrderBook) {
                    onSecondExchangeOrderBook(orderBook)
                }
            }
    )

}