package no.nav.dagpenger.inntekt

import no.nav.dagpenger.events.inntekt.v1.PosteringsType
import no.nav.dagpenger.inntekt.mapping.toPosteringsTypeGrunnlag
import org.junit.jupiter.api.Test
import java.time.LocalDate

class GenererTestInntekt {


    @Test
    fun `generer inntekt`() {
        val posteringstyper = listOf(PosteringsType.L_AKSJER_GRUNNFONDSBEVIS_TIL_UNDERKURS, PosteringsType.L_ANNET, PosteringsType.L_BEREGNET_SKATT, PosteringsType.L_BIL, PosteringsType.L_ARBEIDSOPPHOLD_KOST, PosteringsType.L_BIL, PosteringsType.L_FERIEPENGER)
        val beløp = 45
        //val måned = neste måned
        val måned = LocalDate.now().toString()

        val posteringer = posteringstyper.subList(0, 5)

        val inntektListe = posteringer.fold("") { sum, postering ->
            val posteringsTypeGrunnlag = toPosteringsTypeGrunnlag(postering)
            val beskrivelse = posteringsTypeGrunnlag.beskrivelse.toString()
            val type = posteringsTypeGrunnlag.type.toString()

            sum + genererInntekt(beløp, beskrivelse, type)
        }

        val inntektJson = "[${inntektListe.dropLast(1)}]"

        val result = "{${genererInntektMåned(inntektJson, måned)}}"

        println(result)
    }

    private fun genererInntektMåned(inntektListe: String, måned: String): String {
        return """
            "arbeidsInntektMaaned": [
                {
                  "aarMaaned": "$måned",
                  "arbeidsInntektInformasjon": {
                    "inntektListe": $inntektListe
                  }
                }
              ],
              "ident": {
                "identifikator": "8888888888",
                "aktoerType": "AKTOER_ID"
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
            "beskrivelse": "$beskrivelse"
        },
    """.trimIndent()
    }
}