package no.nav.dagpenger.inntekt.klassifisering

import io.kotlintest.shouldBe
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.Aktoer
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.AktoerType
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.ArbeidsInntektInformasjon
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.ArbeidsInntektMaaned
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.Avvik
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.Inntekt
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektBeskrivelse
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektType
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentResponse
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektskomponentHttpClientTest
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.SpesielleInntjeningsforhold
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.TilleggInformasjon
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.TilleggInformasjonsDetaljer
import no.nav.dagpenger.inntekt.moshiInstance
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.YearMonth

internal class InntektsklassifiseringTest {

    @Test
    fun `Klassifiser inntekt hvor inntektliste mangler`() {
        val now = YearMonth.now()
        val inntektkomponentResponse = InntektkomponentResponse(
            arbeidsInntektMaaned = listOf(
                ArbeidsInntektMaaned(now, null, ArbeidsInntektInformasjon(null))
            ),
            ident = Aktoer(AktoerType.AKTOER_ID, "1234")

        )

        val klassifisertInntekt = klassifiserInntekter(inntektkomponentResponse)
        klassifisertInntekt.first().årMåned shouldBe now
        klassifisertInntekt.first().klassifiserteInntekter shouldBe emptyList()
    }

    @Test
    fun `Klassifiser inntekt hvor ArbeidsInntektInformasjon mangler`() {
        val now = YearMonth.now()
        val inntektkomponentResponse = InntektkomponentResponse(
            arbeidsInntektMaaned = listOf(
                ArbeidsInntektMaaned(now, null, null)
            ),
            ident = Aktoer(AktoerType.AKTOER_ID, "1234")

        )

        val klassifisertInntekt = klassifiserInntekter(inntektkomponentResponse)
        klassifisertInntekt.first().årMåned shouldBe now
        klassifisertInntekt.first().klassifiserteInntekter shouldBe emptyList()
    }

    @Test
    fun `Klassifiser inntekt hvor inntekt er til stede`() {
        val now = YearMonth.now()
        val aktør = Aktoer(AktoerType.AKTOER_ID, "1234")
        val inntektkomponentResponse = InntektkomponentResponse(
            arbeidsInntektMaaned = listOf(
                ArbeidsInntektMaaned(
                    now,
                    null,
                    arbeidsInntektInformasjon = ArbeidsInntektInformasjon(
                        inntektListe = listOf(
                            Inntekt(
                                beloep = 100.toBigDecimal(),
                                beskrivelse = InntektBeskrivelse.FASTLOENN,
                                fordel = "",
                                inntektType = InntektType.LOENNSINNTEKT,
                                inntektskilde = "",
                                inntektsperiodetype = "",
                                inntektsstatus = "",
                                utbetaltIMaaned = now

                            )
                        )
                    )
                )
            ),
            ident = aktør

        )

        val klassifisertInntekt = klassifiserInntekter(inntektkomponentResponse)
        klassifisertInntekt.first().årMåned shouldBe now
        klassifisertInntekt.first().klassifiserteInntekter shouldBe listOf(KlassifisertInntekt(100.toBigDecimal(), InntektKlasse.ARBEIDSINNTEKT))
    }

    @Test
    fun `Klassifiser inntekt med spesielleIntjeningsforhold `() {
        val now = YearMonth.now()
        val aktør = Aktoer(AktoerType.AKTOER_ID, "1234")
        val inntektkomponentResponse = InntektkomponentResponse(
            arbeidsInntektMaaned = listOf(
                ArbeidsInntektMaaned(
                    now,
                    null,
                    arbeidsInntektInformasjon = ArbeidsInntektInformasjon(
                        inntektListe = listOf(
                            Inntekt(
                                beloep = 100.toBigDecimal(),
                                beskrivelse = InntektBeskrivelse.FASTLOENN,
                                fordel = "",
                                inntektType = InntektType.LOENNSINNTEKT,
                                inntektskilde = "",
                                inntektsperiodetype = "",
                                inntektsstatus = "",
                                utbetaltIMaaned = now,
                                tilleggsinformasjon = TilleggInformasjon("", TilleggInformasjonsDetaljer("", SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK))
                            )
                        )
                    )
                )
            ),
            ident = aktør

        )

        val klassifisertInntekt = klassifiserInntekter(inntektkomponentResponse)
        klassifisertInntekt.first().årMåned shouldBe now
        klassifisertInntekt.first().klassifiserteInntekter shouldBe listOf(KlassifisertInntekt(100.toBigDecimal(), InntektKlasse.TILTAKSLØNN))
    }

    @Test
    fun `Skal gi indikasjon på avvik om måneden inneholder avvik `() {
        val now = YearMonth.now()
        val aktør = Aktoer(AktoerType.AKTOER_ID, "1234")
        val inntektkomponentResponse = InntektkomponentResponse(
            arbeidsInntektMaaned = listOf(
                ArbeidsInntektMaaned(
                    now,
                    listOf(
                        Avvik(
                            ident = Aktoer(AktoerType.AKTOER_ID, "123456789"),
                            opplysningspliktig = Aktoer(AktoerType.ORGANISASJON, "12345678"),
                            virksomhet = Aktoer(AktoerType.ORGANISASJON, "12345678"),
                            avvikPeriode = now,
                            tekst = "Avvik"

                        )
                    ),
                    arbeidsInntektInformasjon = ArbeidsInntektInformasjon(
                        inntektListe = listOf(
                            Inntekt(
                                beloep = 100.toBigDecimal(),
                                beskrivelse = InntektBeskrivelse.FASTLOENN,
                                fordel = "",
                                inntektType = InntektType.LOENNSINNTEKT,
                                inntektskilde = "",
                                inntektsperiodetype = "",
                                inntektsstatus = "",
                                utbetaltIMaaned = now,
                                inntektsmottaker = aktør

                            )
                        )
                    )
                )
            ),
            ident = aktør

        )

        val klassifisertInntekt = klassifiserInntekter(inntektkomponentResponse)
        klassifisertInntekt.first().årMåned shouldBe now
        klassifisertInntekt.first().klassifiserteInntekter shouldBe listOf(KlassifisertInntekt(100.toBigDecimal(), InntektKlasse.ARBEIDSINNTEKT))
    }

    @Test
    fun ` Test avvik med eksempel response`() {
        val jsonResponseAdapter = moshiInstance.adapter(InntektkomponentResponse::class.java)
        val body = InntektskomponentHttpClientTest::class.java
            .getResource("/test-data/example-inntekt-avvik.json").readText()
        val inntektkomponentResponse = jsonResponseAdapter.fromJson(body)!!
        val klassifisertInntekt = klassifiserInntekter(inntektkomponentResponse)

        klassifisertInntekt.first().harAvvik shouldBe true
        klassifisertInntekt.first().klassifiserteInntekter shouldBe listOf(KlassifisertInntekt(1212.toBigDecimal(), InntektKlasse.ARBEIDSINNTEKT))
    }

    @Test
    fun `matchesSingularPredicate returns true when one predicate matches`() {
        fun pred1(datagrunnlag: DatagrunnlagKlassifisering): Boolean = true
        fun pred2(datagrunnlag: DatagrunnlagKlassifisering): Boolean = false
        fun pred3(datagrunnlag: DatagrunnlagKlassifisering): Boolean = false

        val predicates = listOf(::pred1, ::pred2, ::pred3)

        assert(
            matchesSingularPredicate(
                DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FASTLOENN),
                predicates
            )
        )
    }

    @Test
    fun `matchesSingularPredicate returns throws error when multiple predicates match`() {
        fun pred1(datagrunnlag: DatagrunnlagKlassifisering): Boolean = true
        fun pred2(datagrunnlag: DatagrunnlagKlassifisering): Boolean = true
        fun pred3(datagrunnlag: DatagrunnlagKlassifisering): Boolean = false

        val predicates = listOf(::pred1, ::pred2, ::pred3)

        assertThrows<MultipleMatchingPredicatesException> {
            matchesSingularPredicate(
                DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FASTLOENN),
                predicates
            )
        }
    }
}