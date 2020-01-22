package no.nav.dagpenger.inntekt

import no.nav.dagpenger.events.inntekt.v1.PosteringsType
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektBeskrivelse
import no.nav.dagpenger.inntekt.mapping.toPosteringsTypeGrunnlag
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.YearMonth

class GenererTestInntekt {

    val adapter = moshiInstance.adapter(InntektBeskrivelse::class.java)

    @Test
    fun `generer inntekt`() {
        val måneder = (36L downTo 1L).map { YearMonth.from(LocalDate.now().minusMonths(it)) }
        val posteringstyper = listOf(
            PosteringsType.L_AKSJER_GRUNNFONDSBEVIS_TIL_UNDERKURS,
            PosteringsType.L_ANNET,
            PosteringsType.L_BEREGNET_SKATT,
            PosteringsType.L_BIL,
            PosteringsType.L_ARBEIDSOPPHOLD_KOST,
            PosteringsType.L_BIL,
            PosteringsType.L_FERIEPENGER
        )

        val resultat = måneder.fold("") { månedsjson, måned ->
            val beløp = (1000..10_000).random()

            val posteringer = posteringstyper.subList(0, 5)

            val inntektListe = posteringer.fold("") { sum, postering ->
                val posteringsTypeGrunnlag = toPosteringsTypeGrunnlag(postering)
                val beskrivelse = adapter.toJson(posteringsTypeGrunnlag.beskrivelse)
                val type = posteringsTypeGrunnlag.type.toString()

                sum + genererInntekt(beløp, beskrivelse, type)
            }

            val inntektJson = "[${inntektListe.dropLast(1)}]"

            månedsjson + "${genererInntektMåned(inntektJson, måned.toString())},"
        }

        val månedsjson = resultat.dropLast(1)

        val json = """
            {
            "arbeidsInntektMaaned": [
                $månedsjson
              ],
              "ident": {
                "identifikator": "8888888888",
                "aktoerType": "AKTOER_ID"
              }
            }
        """.trimIndent()

        println(json)
    }

    private fun genererInntektMåned(inntektListe: String, måned: String): String {
        return """
            {
                  "aarMaaned": "$måned",
                  "arbeidsInntektInformasjon": {
                    "inntektListe": $inntektListe
                  }
                }
             
        """.trimIndent()
    }

    private fun genererInntekt(beløp: Int, beskrivelse: String, type: String): String {
        return """
        {
            "inntektType": "$type",
            "beloep": $beløp,
            "fordel": "kontantytelse",
            "inntektskilde": "A-ordningen",
            "inntektsperiodetype": "Maaned",
            "inntektsstatus": "LoependeInnrapportert",
            "leveringstidspunkt": "2019-05",
            "utbetaltIMaaned": "2019-01",
            "opplysningspliktig": {
            "identifikator": "8888888888",
            "aktoerType": "ORGANISASJON"
        },
            "virksomhet": {
            "identifikator": "8888888888",
            "aktoerType": "ORGANISASJON"
        },
            "inntektsmottaker": {
            "identifikator": "8888888888",
            "aktoerType": "NATURLIG_IDENT"
        },
            "inngaarIGrunnlagForTrekk": true,
            "utloeserArbeidsgiveravgift": true,
            "informasjonsstatus": "InngaarAlltid",
            "beskrivelse": $beskrivelse
        },
    """.trimIndent()
    }
}