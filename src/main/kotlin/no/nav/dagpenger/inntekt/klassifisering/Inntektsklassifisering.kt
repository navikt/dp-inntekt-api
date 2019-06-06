package no.nav.dagpenger.inntekt.klassifisering

import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektBeskrivelse
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektType
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentResponse
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.SpesielleInntjeningsforhold
import kotlin.reflect.KFunction

// todo - TEST THIS!!! MISSING UNIT TEST

fun klassifiserInntekter(uklassifiserteInntekter: InntektkomponentResponse): List<KlassifisertInntektMåned> {

    return uklassifiserteInntekter.arbeidsInntektMaaned?.map { måned ->
        val årMåned = måned.aarMaaned
        val avvik = måned.avvikListe ?: emptyList()
        val klassifiserteInntekter = måned.arbeidsInntektInformasjon?.inntektListe?.map { inntekt ->
            val datagrunnlagKlassifisering = DatagrunnlagKlassifisering(
                inntekt.inntektType,
                inntekt.beskrivelse,
                inntekt.tilleggsinformasjon?.tilleggsinformasjonDetaljer?.spesielleInntjeningsforhold
            )

            val inntektKlasse = klassifiserInntekt(datagrunnlagKlassifisering)
            KlassifisertInntekt(inntekt.beloep, inntektKlasse)
        } ?: emptyList()
        KlassifisertInntektMåned(årMåned, avvik.isNotEmpty(), klassifiserteInntekter)
    } ?: emptyList()
}

private fun klassifiserInntekt(datagrunnlag: DatagrunnlagKlassifisering): InntektKlasse {
    val inntektKlassePredicates = listOf(
        predicatesInntektklasseArbeid() to InntektKlasse.ARBEIDSINNTEKT,
        predicatesInntektklasseDagpenger() to InntektKlasse.DAGPENGER,
        predicatesInntektklasseDagpengerFangstFiske() to InntektKlasse.DAGPENGER_FANGST_FISKE,
        predicatesInntektklasseNæringsinntekt() to InntektKlasse.FANGST_FISKE,
        predicatesInntektklasseSykepenger() to InntektKlasse.SYKEPENGER,
        predicatesInntektklasseSykepengerFangstFiske() to InntektKlasse.SYKEPENGER_FANGST_FISKE,
        predicatesInntektklasseTiltakslønn() to InntektKlasse.TILTAKSLØNN
    )

    val klasser = inntektKlassePredicates
        .filter { (predicates, _) -> matchesSingularPredicate(datagrunnlag, predicates) }
        .map { (_, klasse) -> klasse }

    return when {
        klasser.size > 1 -> throw KlassifiseringException("$datagrunnlag klassifisert til flere klasser: $klasser")
        klasser.isEmpty() -> throw KlassifiseringException("Fant ingen klasse til $datagrunnlag")
        else -> klasser.first()
    }
}

fun matchesSingularPredicate(
    datagrunnlag: DatagrunnlagKlassifisering,
    predicates: List<(DatagrunnlagKlassifisering) -> Boolean>
): Boolean {

    val matchingPredicates = predicates.filter { predicate -> predicate(datagrunnlag) }

    if (matchingPredicates.size > 1) {
        throw MultipleMatchingPredicatesException(
            "Multiple matching predicates: ${matchingPredicates.map { functionName(it) }}. " +
                "Predicates: ${predicates.map { functionName(it) }}."
        )
    }

    return matchingPredicates.size == 1
}

fun <T, V> functionName(function: (V) -> T): String {
    val functionAsKFunction: KFunction<*> = function as KFunction<*>
    return functionAsKFunction.name
}

data class DatagrunnlagKlassifisering(
    val type: InntektType,
    val beskrivelse: InntektBeskrivelse,
    val forhold: SpesielleInntjeningsforhold? = null
)

class KlassifiseringException(override val message: String) : RuntimeException(message)

class MultipleMatchingPredicatesException(override val message: String) : RuntimeException(message)