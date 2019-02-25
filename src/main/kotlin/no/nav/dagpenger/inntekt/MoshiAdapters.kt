package no.nav.dagpenger.inntekt

import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.adapters.EnumJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import no.nav.dagpenger.inntekt.v1.SpesielleInntjeningsforhold
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

val moshiInstance: Moshi = Moshi.Builder()
    .add(YearMonthJsonAdapter())
    .add(LocalDateJsonAdapter())
    .add(KotlinJsonAdapterFactory())
    .add(BigDecimalJsonAdapter())
    .add(SpesielleInntjeningsforhold::class.java,
        EnumJsonAdapter.create(SpesielleInntjeningsforhold::class.java).withUnknownFallback(SpesielleInntjeningsforhold.UNKNOWN))
    .build()!!

class YearMonthJsonAdapter {
    @ToJson
    fun toJson(yearMonth: YearMonth): String {
        return yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"))
    }

    @FromJson
    fun fromJson(json: String): YearMonth {
        return YearMonth.parse(json)
    }
}

class LocalDateJsonAdapter {
    @ToJson
    fun toJson(localDate: LocalDate): String {
        return localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    @FromJson
    fun fromJson(json: String): LocalDate {
        return LocalDate.parse(json)
    }
}

class BigDecimalJsonAdapter {

    @ToJson
    fun toJson(bigDecimal: BigDecimal): String {
        return bigDecimal.toString()
    }

    @FromJson
    fun fromJson(json: String): BigDecimal {
        return BigDecimal(json)
    }
}