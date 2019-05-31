package no.nav.dagpenger.inntekt.mapping

import de.huxhorn.sulky.ulid.ULID
import no.nav.dagpenger.inntekt.db.InntektId
import no.nav.dagpenger.inntekt.db.StoredInntekt
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.Aktoer
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.AktoerType
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.ArbeidsInntektInformasjon
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.ArbeidsInntektMaaned
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.Inntekt
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektBeskrivelse
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektType
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentResponse
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.SpesielleInntjeningsforhold
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.TilleggInformasjon
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.TilleggInformasjonsDetaljer
import no.nav.dagpenger.inntekt.moshiInstance
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.Customization
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import org.skyscreamer.jsonassert.JSONCompareResult
import org.skyscreamer.jsonassert.comparator.CustomComparator
import org.skyscreamer.jsonassert.comparator.JSONCompareUtil.getKeys
import org.skyscreamer.jsonassert.comparator.JSONCompareUtil.qualify
import java.math.BigDecimal
import java.time.YearMonth

val rawInntekt = InntektkomponentResponse(
    listOf(
        ArbeidsInntektMaaned(
            YearMonth.of(2019, 5),
            null,
            ArbeidsInntektInformasjon(
                listOf(
                    Inntekt(
                        BigDecimal.ONE,
                        "fordel",
                        InntektBeskrivelse.AKSJER_GRUNNFONDSBEVIS_TIL_UNDERKURS,
                        "kilde",
                        "status",
                        "periodetype",
                        inntektType = InntektType.LOENNSINNTEKT,
                        utbetaltIMaaned = YearMonth.of(2019, 5)
                    )
                )
            )
        ),
        ArbeidsInntektMaaned(
            YearMonth.of(2019, 6),
            null,
            ArbeidsInntektInformasjon(
                listOf(
                    Inntekt(
                        BigDecimal.ONE,
                        "fordel",
                        InntektBeskrivelse.FASTLOENN,
                        "kilde",
                        "status",
                        "periodetype",
                        inntektType = InntektType.LOENNSINNTEKT,
                        utbetaltIMaaned = YearMonth.of(2019, 6)
                    ),
                    Inntekt(
                        BigDecimal.ONE,
                        "fordel",
                        InntektBeskrivelse.ANNET,
                        "kilde",
                        "status",
                        "periodetype",
                        inntektType = InntektType.LOENNSINNTEKT,
                        utbetaltIMaaned = YearMonth.of(2019, 6),
                        tilleggsinformasjon = TilleggInformasjon(
                            "kategori",
                            TilleggInformasjonsDetaljer(
                                "detaljetyper",
                                SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY
                            )
                        )

                    )
                )
            )
        )
    ),
    Aktoer(AktoerType.AKTOER_ID, "aktorId")
)

internal class KategoriseringTest {

    @Test
    fun `mapToGUIInntekt adds correct verdikode`() {
        val storedInntekt = StoredInntekt(
            InntektId(ULID().nextULID()),
            rawInntekt, false
        )
        val guiInntekt = mapToGUIInntekt(storedInntekt)
        assertEquals(
            "Aksjer/grunnfondsbevis til underkurs",
            guiInntekt.inntekt.arbeidsInntektMaaned?.first()?.arbeidsInntektInformasjon?.inntektListe?.first()?.verdikode
        )
        assertEquals(
            "Fastlønn",
            guiInntekt.inntekt.arbeidsInntektMaaned?.filter {
                it.aarMaaned == YearMonth.of(
                    2019,
                    6
                )
            }?.first()?.arbeidsInntektInformasjon?.inntektListe?.first()?.verdikode
        )
        assertEquals(
            "Hyre - Annet",
            guiInntekt.inntekt.arbeidsInntektMaaned?.filter {
                it.aarMaaned == YearMonth.of(
                    2019,
                    6
                )
            }?.first()?.arbeidsInntektInformasjon?.inntektListe?.last()?.verdikode
        )
    }

    @Test
    fun `mapToGUIInntekt does not modify other fields than kategori`() {
        val storedInntekt = StoredInntekt(
            InntektId(ULID().nextULID()),
            rawInntekt, false
        )
        val guiInntekt = mapToGUIInntekt(storedInntekt)

        val beforeJson = moshiInstance.adapter(InntektkomponentResponse::class.java).toJson(rawInntekt)
        val mappedJson = moshiInstance.adapter(GUIInntektsKomponentResponse::class.java).toJson(guiInntekt.inntekt)

        JSONAssert.assertEquals(
            mappedJson, beforeJson,
            AttributeIgnoringComparator(
                JSONCompareMode.STRICT,
                setOf("verdikode"), Customization("") { _, _ -> true }
            )
        )
    }

    @Test
    fun `map to and from GUIInntekt results in original inntekt`() {
        val storedInntekt = StoredInntekt(
            InntektId(ULID().nextULID()),
            rawInntekt, false
        )

        assertEquals(storedInntekt, mapFromGUIInntekt(mapToGUIInntekt(storedInntekt)))
    }
}

/* Workaround to be able to use strict mode when one json is missing a field (like kategori)
    see https://stackoverflow.com/a/44328139
*/
class AttributeIgnoringComparator(
    mode: JSONCompareMode,
    var attributesToIgnore: Set<String>,
    customization: Customization
) : CustomComparator(
    mode,
    customization
) {
    override fun checkJsonObjectKeysExpectedInActual(
        prefix: String,
        expected: JSONObject,
        actual: JSONObject,
        result: JSONCompareResult
    ) {
        val expectedKeys = getKeys(expected)
        expectedKeys.removeAll(attributesToIgnore)
        for (key in expectedKeys) {
            var expectedValue = expected.get(key)
            if (actual.has(key)) {
                var actualValue = actual.get(key)
                compareValues(qualify(prefix, key), expectedValue, actualValue, result)
            } else {
                result.missing(prefix, key)
            }
        }
    }
}
