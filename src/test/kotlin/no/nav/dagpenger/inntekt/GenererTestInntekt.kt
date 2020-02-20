package no.nav.dagpenger.inntekt

import com.squareup.moshi.JsonAdapter
import no.nav.dagpenger.events.inntekt.v1.PosteringsType
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.Aktoer
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.AktoerType
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.ArbeidsInntektInformasjon
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.ArbeidsInntektMaaned
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.Avvik
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.Inntekt
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektkomponentResponse
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.TilleggInformasjon
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.TilleggInformasjonsDetaljer
import no.nav.dagpenger.inntekt.mapping.toPosteringsTypeGrunnlag
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth

class GenererTestInntekt {

    @Test
    fun `generer all inntekt`() {
        val måneder = (36L downTo 1L).map { YearMonth.from(LocalDate.now().minusMonths(it)) }

        var count = 0

        val arbeidsInntektMaaneder = måneder.map { måned ->
            if (count + 4 >= allePosteringer.size) count = allePosteringer.size - 4
            val inntektListe = allePosteringer.subList(count, count + 4).map {
                val posteringsTypeGrunnlag = toPosteringsTypeGrunnlag(it)
                Inntekt(
                    beloep = BigDecimal((1000..10_000).random()),
                    fordel = "",
                    beskrivelse = posteringsTypeGrunnlag.beskrivelse,
                    inntektskilde = "",
                    inntektsstatus = "",
                    inntektsperiodetype = "",
                    utbetaltIMaaned = måned,
                    inntektType = posteringsTypeGrunnlag.type,
                    virksomhet = Aktoer(AktoerType.ORGANISASJON, "123456789"),
                    tilleggsinformasjon = posteringsTypeGrunnlag.forhold?.let {
                        TilleggInformasjon(
                            null,
                            TilleggInformasjonsDetaljer(null, it)
                        )
                    }
                )
            }

            val arbeidsInntektInformasjon = ArbeidsInntektInformasjon(inntektListe)

            count += 5
            ArbeidsInntektMaaned(måned, emptyList(), arbeidsInntektInformasjon)
        }

        val inntekt = InntektkomponentResponse(arbeidsInntektMaaneder, Aktoer(AktoerType.AKTOER_ID, "8888888888"))
        val adapter: JsonAdapter<InntektkomponentResponse> =
            moshiInstance.adapter(InntektkomponentResponse::class.java)
        println(adapter.toJson(inntekt))
    }

    @Test
    fun `generer noe inntekt`() {
        val måneder = (36L downTo 1L).map { YearMonth.from(LocalDate.now().minusMonths(it)) }
        val posteringstyper = listOf(
            PosteringsType.Y_DAGPENGER_VED_ARBEIDSLØSHET,
            PosteringsType.Y_FORELDREPENGER,
            PosteringsType.Y_SVANGERSKAPSPENGER,
            PosteringsType.Y_SYKEPENGER,
            PosteringsType.Y_SYKEPENGER_TIL_FISKER_SOM_BARE_HAR_HYRE
        )

        val arbeidsInntektMaaneder = måneder.map { måned ->
            val inntektListe = posteringstyper.map {
                val posteringsTypeGrunnlag = toPosteringsTypeGrunnlag(it)
                Inntekt(
                    beloep = BigDecimal((1000..10_000).random()),
                    fordel = "",
                    beskrivelse = posteringsTypeGrunnlag.beskrivelse,
                    inntektskilde = "",
                    inntektsstatus = "",
                    inntektsperiodetype = "",
                    utbetaltIMaaned = måned,
                    inntektType = posteringsTypeGrunnlag.type,
                    virksomhet = Aktoer(AktoerType.ORGANISASJON, "123456789")
                )
            }

            val arbeidsInntektInformasjon = ArbeidsInntektInformasjon(inntektListe)

            ArbeidsInntektMaaned(måned, emptyList(), arbeidsInntektInformasjon)
        }

        val inntekt = InntektkomponentResponse(arbeidsInntektMaaneder, Aktoer(AktoerType.AKTOER_ID, "8888888888"))
        val adapter: JsonAdapter<InntektkomponentResponse> =
            moshiInstance.adapter(InntektkomponentResponse::class.java)
        println(adapter.toJson(inntekt))
    }

    @Test
    fun `Generer inntekt med avvik`() {
        val måneder = (36L downTo 1L).map { YearMonth.from(LocalDate.now().minusMonths(it)) }
        val posteringstyper = listOf(
            PosteringsType.Y_DAGPENGER_VED_ARBEIDSLØSHET,
            PosteringsType.Y_FORELDREPENGER,
            PosteringsType.Y_SVANGERSKAPSPENGER,
            PosteringsType.Y_SYKEPENGER,
            PosteringsType.Y_SYKEPENGER_TIL_FISKER_SOM_BARE_HAR_HYRE
        )

        val arbeidsInntektMaaneder = måneder.mapIndexed { index, måned ->
            val inntektListe = posteringstyper.map {
                val posteringsTypeGrunnlag = toPosteringsTypeGrunnlag(it)
                Inntekt(
                    beloep = BigDecimal((1000..10_000).random()),
                    fordel = "",
                    beskrivelse = posteringsTypeGrunnlag.beskrivelse,
                    inntektskilde = "",
                    inntektsstatus = "",
                    inntektsperiodetype = "",
                    utbetaltIMaaned = måned,
                    inntektType = posteringsTypeGrunnlag.type,
                    virksomhet = Aktoer(AktoerType.ORGANISASJON, "123456789")
                )
            }

            val arbeidsInntektInformasjon = ArbeidsInntektInformasjon(inntektListe)

            val avvikListe = if (index == 3 || index == 30) listOf(
                Avvik(
                    ident = Aktoer(AktoerType.AKTOER_ID, "123456786910"),
                    opplysningspliktig = Aktoer(AktoerType.AKTOER_ID, "123456786910"),
                    avvikPeriode = måned,
                    virksomhet = null,
                    tekst = "tekst"
                )
            )
            else emptyList()

            ArbeidsInntektMaaned(
                aarMaaned = måned,
                avvikListe = avvikListe,
                arbeidsInntektInformasjon = arbeidsInntektInformasjon
            )
        }

        val inntekt = InntektkomponentResponse(arbeidsInntektMaaneder, Aktoer(AktoerType.AKTOER_ID, "8888888888"))
        val adapter: JsonAdapter<InntektkomponentResponse> =
            moshiInstance.adapter(InntektkomponentResponse::class.java)
        println(adapter.toJson(inntekt))
    }

    private val allePosteringer = listOf(
        PosteringsType.L_AKSJER_GRUNNFONDSBEVIS_TIL_UNDERKURS,
        PosteringsType.L_ANNET,
        PosteringsType.L_ANNET_H,
        PosteringsType.L_ANNET_T,
        PosteringsType.L_ARBEIDSOPPHOLD_KOST,
        PosteringsType.L_ARBEIDSOPPHOLD_LOSJI,
        PosteringsType.L_BEREGNET_SKATT,
        PosteringsType.L_BESØKSREISER_HJEMMET_ANNET,
        PosteringsType.L_BESØKSREISER_HJEMMET_KILOMETERGODTGJØRELSE_BIL,
        PosteringsType.L_BETALT_UTENLANDSK_SKATT,
        PosteringsType.L_BIL,
        PosteringsType.L_BOLIG,
        PosteringsType.L_BONUS,
        PosteringsType.L_BONUS_FRA_FORSVARET,
        PosteringsType.L_BONUS_H,
        PosteringsType.L_BONUS_T,
        PosteringsType.L_ELEKTRONISK_KOMMUNIKASJON,
        PosteringsType.L_FAST_BILGODTGJØRELSE,
        PosteringsType.L_FAST_TILLEGG,
        PosteringsType.L_FAST_TILLEGG_H,
        PosteringsType.L_FAST_TILLEGG_T,
        PosteringsType.L_FASTLØNN,
        PosteringsType.L_FASTLØNN_H,
        PosteringsType.L_FASTLØNN_T,
        PosteringsType.L_FERIEPENGER,
        PosteringsType.L_FERIEPENGER_H,
        PosteringsType.L_FERIEPENGER_T,
        PosteringsType.L_FOND_FOR_IDRETTSUTØVERE,
        PosteringsType.L_HELLIGDAGSTILLEGG,
        PosteringsType.L_HELLIGDAGSTILLEGG_H,
        PosteringsType.L_HELLIGDAGSTILLEGG_T,
        PosteringsType.L_HONORAR_AKKORD_PROSENT_PROVISJON,
        PosteringsType.L_HYRETILLEGG,
        PosteringsType.L_IKKE_SKATTEPLIKTIG_LØNN_FRA_UTENLANDSK_DIPLOM_KONSUL_STASJON,
        PosteringsType.L_INNBETALING_TIL_UTENLANDSK_PENSJONSORDNING,
        PosteringsType.L_KILOMETERGODTGJØRELSE_BIL,
        PosteringsType.L_KOMMUNAL_OMSORGSLØNN_OG_FOSTERHJEMSGODTGJØRELSE,
        PosteringsType.L_KOST_DAGER,
        PosteringsType.L_KOST_DØGN,
        PosteringsType.L_KOSTBESPARELSE_I_HJEMMET,
        PosteringsType.L_LØNN_FOR_BARNEPASS_I_BARNETS_HJEM,
        PosteringsType.L_LØNN_TIL_PRIVATPERSONER_FOR_ARBEID_I_HJEMMET,
        PosteringsType.L_LØNN_TIL_VERGE_FRA_FYLKESMANNEN,
        PosteringsType.L_LØNN_UTBETALT_AV_VELDEDIG_ELLER_ALLMENNYTTIG_INSTITUSJON_ELLER_ORGANISASJON,
        PosteringsType.L_LOSJI,
        PosteringsType.L_OPSJONER,
        PosteringsType.L_OVERTIDSGODTGJØRELSE,
        PosteringsType.L_OVERTIDSGODTGJØRELSE_H,
        PosteringsType.L_OVERTIDSGODTGJØRELSE_T,
        PosteringsType.L_REISE_ANNET,
        PosteringsType.L_REISE_KOST,
        PosteringsType.L_REISE_LOSJI,
        PosteringsType.L_RENTEFORDEL_LÅN,
        PosteringsType.L_SKATTEPLIKTIG_DEL_FORSIKRINGER,
        PosteringsType.L_SKATTEPLIKTIG_PERSONALRABATT,
        PosteringsType.L_SLUTTVEDERLAG,
        PosteringsType.L_SLUTTVEDERLAG_H,
        PosteringsType.L_SLUTTVEDERLAG_T,
        PosteringsType.L_SMUSSTILLEGG,
        PosteringsType.L_STIPEND,
        PosteringsType.L_STYREHONORAR_OG_GODTGJØRELSE_VERV,
        PosteringsType.L_TIMELØNN,
        PosteringsType.L_TIMELØNN_H,
        PosteringsType.L_TIMELØNN_T,
        PosteringsType.L_TIPS,
        PosteringsType.L_TREKK_I_LØNN_FOR_FERIE,
        PosteringsType.L_TREKK_I_LØNN_FOR_FERIE_H,
        PosteringsType.L_TREKK_I_LØNN_FOR_FERIE_T,
        PosteringsType.L_UREGELMESSIGE_TILLEGG_KNYTTET_TIL_ARBEIDET_TID,
        PosteringsType.L_UREGELMESSIGE_TILLEGG_KNYTTET_TIL_ARBEIDET_TID_H,
        PosteringsType.L_UREGELMESSIGE_TILLEGG_KNYTTET_TIL_ARBEIDET_TID_T,
        PosteringsType.L_UREGELMESSIGE_TILLEGG_KNYTTET_TIL_IKKE_ARBEIDET_TID,
        PosteringsType.L_UREGELMESSIGE_TILLEGG_KNYTTET_TIL_IKKE_ARBEIDET_TID_H,
        PosteringsType.L_UREGELMESSIGE_TILLEGG_KNYTTET_TIL_IKKE_ARBEIDET_TID_T,
        PosteringsType.L_YRKEBIL_TJENESTLIGBEHOV_KILOMETER,
        PosteringsType.L_YRKEBIL_TJENESTLIGBEHOV_LISTEPRIS,
        PosteringsType.N_DAGPENGER_TIL_FISKER,
        PosteringsType.N_LOTT_KUN_TRYGDEAVGIFT,
        PosteringsType.N_SYKEPENGER_TIL_FISKER,
        PosteringsType.N_VEDERLAG,
        PosteringsType.Y_DAGPENGER_TIL_FISKER_SOM_BARE_HAR_HYRE,
        PosteringsType.Y_DAGPENGER_VED_ARBEIDSLØSHET,
        PosteringsType.Y_FORELDREPENGER,
        PosteringsType.Y_SVANGERSKAPSPENGER,
        PosteringsType.Y_SYKEPENGER,
        PosteringsType.Y_SYKEPENGER_TIL_FISKER_SOM_BARE_HAR_HYRE
    )
}
