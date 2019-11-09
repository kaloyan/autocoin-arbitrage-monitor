package automate.profit.autocoin.exchange.arbitrage

import automate.profit.autocoin.config.ExchangePair
import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.ticker.CurrencyPairWithExchangePair
import java.math.BigDecimal

data class TwoLegArbitrageProfit(
        val currencyPair: CurrencyPair,
        val exchangePair: ExchangePair,
        val sellPrice: BigDecimal,
        val buyPrice: BigDecimal,
        val sellAtExchange: SupportedExchange,
        val buyAtExchange: SupportedExchange,
        val relativeProfit: BigDecimal,
        val calculatedAtMillis: Long
) {

    val currencyPairWithExchangePair: CurrencyPairWithExchangePair = CurrencyPairWithExchangePair(currencyPair, exchangePair)

    override fun toString(): String {
        return "TwoLegArbitrageProfit(currencyPair=$currencyPair, sellPrice=$sellPrice, buyPrice=$buyPrice, sellAtExchange=$sellAtExchange, buyAtExchange=$buyAtExchange, relativeProfit=${relativeProfit.multiply(BigDecimal.valueOf(100))} %)"
    }
}