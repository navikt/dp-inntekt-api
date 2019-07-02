package no.nav.dagpenger.inntekt.klassifisering

import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektBeskrivelse
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektType
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.SpesielleInntjeningsforhold
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse

internal class PredicatesArbeidsinntektTest {

    @Test
    fun `inntektklasseArbeidPredicates returns correct predicates`() {
        val predicates = predicatesInntektklasseArbeid()

        val arbeidPredicates = listOf(
            ::isTips,
            ::isLønnAksjerGrunnfondsbevisTilUnderkurs,
            ::isLønnAnnet,
            ::isLønnArbeidsoppholdKost,
            ::isLønnArbeidsoppholdLosji,
            ::isLønnBeregnetSkatt,
            ::isLønnBesøksreiserHjemmetAnnet,
            ::isLønnBesøksreiserHjemmetKilometergodtgjørelseBil,
            ::isLønnBetaltUtenlandskSkatt,
            ::isLønnBil,
            ::isLønnBolig,
            ::isLønnBonus,
            ::isLønnBonusFraForsvaret,
            ::isLønnElektroniskKommunikasjon,
            ::isLønnFastBilgodtgjørelse,
            ::isLønnFastTillegg,
            ::isLønnFastlønn,
            ::isLønnFeriepenger,
            ::isLønnFondForIdrettsutøvere,
            ::isYtelseForeldrepenger,
            ::isLønnHelligdagstillegg,
            ::isLønnHonorarAkkordProsentProvisjon,
            ::isLønnHyretillegg,
            ::isLønnInnbetalingTilUtenlandskPensjonsordning,
            ::isLønnKilometergodtgjørelseBil,
            ::isLønnKommunalOmsorgslønnOgFosterhjemsgodtgjørelse,
            ::isLønnKostDager,
            ::isLønnKostDøgn,
            ::isLønnKostbesparelseIHjemmet,
            ::isLønnLosji,
            ::isLønnIkkeSkattepliktigLønnFraUtenlandskDiplomKonsulStasjon,
            ::isLønnLønnForBarnepassIBarnetsHjem,
            ::isLønnLønnTilPrivatpersonerForArbeidIHjemmet,
            ::isLønnLønnUtbetaltAvVeldedigEllerAllmennyttigInstitusjonEllerOrganisasjon,
            ::isLønnLønnTilVergeFraFylkesmannen,
            ::isLønnOpsjoner,
            ::isLønnOvertidsgodtgjørelse,
            ::isLønnReiseAnnet,
            ::isLønnReiseKost,
            ::isLønnReiseLosji,
            ::isLønnRentefordelLån,
            ::isLønnSkattepliktigDelForsikringer,
            ::isLønnSluttvederlag,
            ::isLønnSmusstillegg,
            ::isLønnStipend,
            ::isLønnStyrehonorarOgGodtgjørelseVerv,
            ::isYtelseSvangerskapspenger,
            ::isLønnTimelønn,
            ::isLønnTrekkILønnForFerie,
            ::isLønnUregelmessigeTilleggKnyttetTilArbeidetTid,
            ::isLønnUregelmessigeTilleggKnyttetTilIkkeArbeidetTid,
            ::isLønnYrkebilTjenestligbehovKilometer,
            ::isLønnYrkebilTjenestligbehovListepris

        )

        assert(predicates.containsAll(arbeidPredicates))
        assertEquals(predicates.size, arbeidPredicates.size)
    }

    @Test
    fun `isLønnAksjerGrunnfondsbevisTilUnderkurs predicates correctly`() {
        assert(isLønnAksjerGrunnfondsbevisTilUnderkurs(
            DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.AKSJER_GRUNNFONDSBEVIS_TIL_UNDERKURS)))

        assertFalse(isLønnAksjerGrunnfondsbevisTilUnderkurs(
            DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.ANNET)))
        assertFalse(isLønnAksjerGrunnfondsbevisTilUnderkurs(
            DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.AKSJER_GRUNNFONDSBEVIS_TIL_UNDERKURS)))
    }

    @Test
    fun `isLønnAnnet predicates correctly`() {
        assert(isLønnAnnet(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.ANNET)))

        assertFalse(isLønnAnnet(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.ARBEIDSOPPHOLD_KOST)))
        assertFalse(isLønnAnnet(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.ANNET)))

        assertFalse(isLønnAnnet(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.ANNET,
            SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY)))

        assertFalse(isLønnAnnet(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.ANNET,
            SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK)))
    }

    @Test
    fun `isLønnArbeidsoppholdKost predicates correctly`() {
        assert(isLønnArbeidsoppholdKost(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.ARBEIDSOPPHOLD_KOST)))

        assertFalse(isLønnArbeidsoppholdKost(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.ARBEIDSOPPHOLD_LOSJI)))
        assertFalse(isLønnArbeidsoppholdKost(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.ARBEIDSOPPHOLD_KOST)))
    }

    @Test
    fun `isLønnArbeidsoppholdLosji predicates correctly`() {
        assert(isLønnArbeidsoppholdLosji(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.ARBEIDSOPPHOLD_LOSJI)))

        assertFalse(isLønnArbeidsoppholdLosji(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.BEREGNET_SKATT)))
        assertFalse(isLønnArbeidsoppholdLosji(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.ARBEIDSOPPHOLD_LOSJI)))
    }

    @Test
    fun `isLønnBeregnetSkatt predicates correctly`() {
        assert(isLønnBeregnetSkatt(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.BEREGNET_SKATT)))

        assertFalse(isLønnBeregnetSkatt(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.BESOEKSREISER_HJEMMET_ANNET)))
        assertFalse(isLønnBeregnetSkatt(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.BEREGNET_SKATT)))
    }

    @Test
    fun `isLønnBesøksreiserHjemmetAnnet predicates correctly`() {
        assert(isLønnBesøksreiserHjemmetAnnet(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.BESOEKSREISER_HJEMMET_ANNET)))

        assertFalse(isLønnBesøksreiserHjemmetAnnet(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.BESOEKSREISER_HJEMMET_KILOMETERGODTGJOERELSE_BIL)))
        assertFalse(isLønnBesøksreiserHjemmetAnnet(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.BESOEKSREISER_HJEMMET_ANNET)))
    }

    @Test
    fun `isLønnBesøksreiserHjemmetKilometergodtgjørelseBil predicates correctly`() {
        assert(isLønnBesøksreiserHjemmetKilometergodtgjørelseBil(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.BESOEKSREISER_HJEMMET_KILOMETERGODTGJOERELSE_BIL)))

        assertFalse(isLønnBesøksreiserHjemmetKilometergodtgjørelseBil(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.BETALT_UTENLANDSK_SKATT)))
        assertFalse(isLønnBesøksreiserHjemmetKilometergodtgjørelseBil(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.BESOEKSREISER_HJEMMET_KILOMETERGODTGJOERELSE_BIL)))
    }

    @Test
    fun `isLønnBetaltUtenlandskSkatt predicates correctly`() {
        assert(isLønnBetaltUtenlandskSkatt(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.BETALT_UTENLANDSK_SKATT)))

        assertFalse(isLønnBetaltUtenlandskSkatt(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.BIL)))
        assertFalse(isLønnBetaltUtenlandskSkatt(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.BETALT_UTENLANDSK_SKATT)))
    }

    @Test
    fun `isLønnBil predicates correctly`() {
        assert(isLønnBil(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.BIL)))

        assertFalse(isLønnBil(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.BOLIG)))
        assertFalse(isLønnBil(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.BIL)))
    }

    @Test
    fun `isLønnBolig predicates correctly`() {
        assert(isLønnBolig(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.BOLIG)))

        assertFalse(isLønnBolig(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.BONUS)))
        assertFalse(isLønnBolig(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.BOLIG)))
    }

    @Test
    fun `isLønnBonus predicates correctly`() {
        assert(isLønnBonus(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.BONUS)))

        assertFalse(isLønnBonus(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.BONUS_FRA_FORSVARET)))
        assertFalse(isLønnBonus(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.BONUS)))

        assertFalse(isLønnBonus(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.BONUS,
            SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY)))

        assertFalse(isLønnBonus(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.BONUS,
            SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK)))
    }

    @Test
    fun `isLønnBonusFraForsvaret predicates correctly`() {
        assert(isLønnBonusFraForsvaret(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.BONUS_FRA_FORSVARET)))

        assertFalse(isLønnBonusFraForsvaret(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.ELEKTRONISK_KOMMUNIKASJON)))
        assertFalse(isLønnBonusFraForsvaret(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.BONUS_FRA_FORSVARET)))
    }

    @Test
    fun `isLønnElektroniskKommunikasjon predicates correctly`() {
        assert(isLønnElektroniskKommunikasjon(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.ELEKTRONISK_KOMMUNIKASJON)))

        assertFalse(isLønnElektroniskKommunikasjon(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FAST_BILGODTGJOERELSE)))
        assertFalse(isLønnElektroniskKommunikasjon(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.ELEKTRONISK_KOMMUNIKASJON)))
    }

    @Test
    fun `isLønnFastBilgodtgjørelse predicates correctly`() {
        assert(isLønnFastBilgodtgjørelse(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FAST_BILGODTGJOERELSE)))

        assertFalse(isLønnFastBilgodtgjørelse(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FAST_TILLEGG)))
        assertFalse(isLønnFastBilgodtgjørelse(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.FAST_BILGODTGJOERELSE)))
    }

    @Test
    fun `isLønnFastTillegg predicates correctly`() {
        assert(isLønnFastTillegg(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FAST_TILLEGG)))

        assertFalse(isLønnFastTillegg(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.ARBEIDSOPPHOLD_KOST)))
        assertFalse(isLønnFastTillegg(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.FAST_TILLEGG)))

        assertFalse(isLønnFastTillegg(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.FAST_TILLEGG,
            SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY)))

        assertFalse(isLønnFastTillegg(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.FAST_TILLEGG,
            SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK)))
    }

    @Test
    fun `isLønnFastlønn predicates correctly`() {
        assert(isLønnFastlønn(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FASTLOENN)))

        assertFalse(isLønnFastlønn(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FERIEPENGER)))
        assertFalse(isLønnFastlønn(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.FASTLOENN)))

        assertFalse(isLønnFastlønn(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.FASTLOENN,
            SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY)))

        assertFalse(isLønnFastlønn(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.FASTLOENN,
            SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK)))
    }

    @Test
    fun `isLønnFeriepenger predicates correctly`() {
        assert(isLønnFeriepenger(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FERIEPENGER)))

        assertFalse(isLønnFeriepenger(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FASTLOENN)))
        assertFalse(isLønnFeriepenger(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.FERIEPENGER)))

        assertFalse(isLønnFeriepenger(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.FERIEPENGER,
            SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY)))

        assertFalse(isLønnFeriepenger(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.FERIEPENGER,
            SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK)))
    }

    @Test
    fun `isLønnFondForIdrettsutøvere predicates correctly`() {
        assert(isLønnFondForIdrettsutøvere(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FOND_FOR_IDRETTSUTOEVERE)))

        assertFalse(isLønnFondForIdrettsutøvere(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FASTLOENN)))
        assertFalse(isLønnFondForIdrettsutøvere(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.FOND_FOR_IDRETTSUTOEVERE)))
    }

    @Test
    fun `isYtelseForeldrepenger predicates correctly`() {
        assert(isYtelseForeldrepenger(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.FORELDREPENGER)))

        assertFalse(isYtelseForeldrepenger(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.FOND_FOR_IDRETTSUTOEVERE)))
        assertFalse(isYtelseForeldrepenger(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FORELDREPENGER)))
    }

    @Test
    fun `isLønnHelligdagstillegg predicates correctly`() {
        assert(isLønnHelligdagstillegg(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.HELLIGDAGSTILLEGG)))

        assertFalse(isLønnHelligdagstillegg(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FOND_FOR_IDRETTSUTOEVERE)))
        assertFalse(isLønnHelligdagstillegg(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.HELLIGDAGSTILLEGG)))

        assertFalse(isLønnHelligdagstillegg(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.HELLIGDAGSTILLEGG,
            SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY)))

        assertFalse(isLønnHelligdagstillegg(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.HELLIGDAGSTILLEGG,
            SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK)))
    }

    @Test
    fun `isLønnHonorarAkkordProsentProvisjon predicates correctly`() {
        assert(isLønnHonorarAkkordProsentProvisjon(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.HONORAR_AKKORD_PROSENT_PROVISJON)))

        assertFalse(isLønnHonorarAkkordProsentProvisjon(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.HELLIGDAGSTILLEGG)))
        assertFalse(isLønnHonorarAkkordProsentProvisjon(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.HONORAR_AKKORD_PROSENT_PROVISJON)))
    }

    @Test
    fun `isLønnHyretillegg predicates correctly`() {
        assert(isLønnHyretillegg(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.HYRETILLEGG)))

        assertFalse(isLønnHyretillegg(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.HONORAR_AKKORD_PROSENT_PROVISJON)))
        assertFalse(isLønnHyretillegg(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.HYRETILLEGG)))
    }

    @Test
    fun `isLønnInnbetalingTilUtenlandskPensjonsordning predicates correctly`() {
        assert(isLønnInnbetalingTilUtenlandskPensjonsordning(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.INNBETALING_TIL_UTENLANDSK_PENSJONSORDNING)))

        assertFalse(isLønnInnbetalingTilUtenlandskPensjonsordning(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.HYRETILLEGG)))
        assertFalse(isLønnInnbetalingTilUtenlandskPensjonsordning(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.INNBETALING_TIL_UTENLANDSK_PENSJONSORDNING)))
    }

    @Test
    fun `isLønnKilometergodtgjørelseBil predicates correctly`() {
        assert(isLønnKilometergodtgjørelseBil(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.KILOMETERGODTGJOERELSE_BIL)))

        assertFalse(isLønnKilometergodtgjørelseBil(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.INNBETALING_TIL_UTENLANDSK_PENSJONSORDNING)))
        assertFalse(isLønnKilometergodtgjørelseBil(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.KILOMETERGODTGJOERELSE_BIL)))
    }

    @Test
    fun `isLønnKommunalOmsorgslønnOgFosterhjemsgodtgjørelse predicates correctly`() {
        assert(isLønnKommunalOmsorgslønnOgFosterhjemsgodtgjørelse(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.KOMMUNAL_OMSORGSLOENN_OG_FOSTERHJEMSGODTGJOERELSE)))

        assertFalse(isLønnKommunalOmsorgslønnOgFosterhjemsgodtgjørelse(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.KILOMETERGODTGJOERELSE_BIL)))
        assertFalse(isLønnKommunalOmsorgslønnOgFosterhjemsgodtgjørelse(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.KOMMUNAL_OMSORGSLOENN_OG_FOSTERHJEMSGODTGJOERELSE)))
    }

    @Test
    fun `isLønnKostDager predicates correctly`() {
        assert(isLønnKostDager(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.KOST_DAGER)))

        assertFalse(isLønnKostDager(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.KOMMUNAL_OMSORGSLOENN_OG_FOSTERHJEMSGODTGJOERELSE)))
        assertFalse(isLønnKostDager(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.KOST_DAGER)))
    }

    @Test
    fun `isLønnKostDøgn predicates correctly`() {
        assert(isLønnKostDøgn(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.KOST_DOEGN)))

        assertFalse(isLønnKostDøgn(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.KOST_DAGER)))
        assertFalse(isLønnKostDøgn(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.KOST_DOEGN)))
    }

    @Test
    fun `isLønnKostbesparelseIHjemmet predicates correctly`() {
        assert(isLønnKostbesparelseIHjemmet(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.KOSTBESPARELSE_I_HJEMMET)))

        assertFalse(isLønnKostbesparelseIHjemmet(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.KOST_DAGER)))
        assertFalse(isLønnKostbesparelseIHjemmet(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.KOSTBESPARELSE_I_HJEMMET)))
    }

    @Test
    fun `isLønnLosji predicates correctly`() {
        assert(isLønnLosji(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.LOSJI)))

        assertFalse(isLønnLosji(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.KOST_DAGER)))
        assertFalse(isLønnLosji(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.LOSJI)))
    }

    @Test
    fun `isLønnIkkeSkattepliktigLønnFraUtenlandskDiplomKonsulStasjon predicates correctly`() {
        assert(isLønnIkkeSkattepliktigLønnFraUtenlandskDiplomKonsulStasjon(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.IKKE_SKATTEPLIKTIG_LOENN_FRA_UTENLANDSK_DIPLOM_KONSUL_STASJON)))

        assertFalse(isLønnIkkeSkattepliktigLønnFraUtenlandskDiplomKonsulStasjon(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.LOENN_FOR_BARNEPASS_I_BARNETS_HJEM)))
        assertFalse(isLønnIkkeSkattepliktigLønnFraUtenlandskDiplomKonsulStasjon(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.IKKE_SKATTEPLIKTIG_LOENN_FRA_UTENLANDSK_DIPLOM_KONSUL_STASJON)))
    }

    @Test
    fun `isLønnLønnForBarnepassIBarnetsHjem predicates correctly`() {
        assert(isLønnLønnForBarnepassIBarnetsHjem(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.LOENN_FOR_BARNEPASS_I_BARNETS_HJEM)))

        assertFalse(isLønnLønnForBarnepassIBarnetsHjem(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.IKKE_SKATTEPLIKTIG_LOENN_FRA_UTENLANDSK_DIPLOM_KONSUL_STASJON)))
        assertFalse(isLønnLønnForBarnepassIBarnetsHjem(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.LOENN_FOR_BARNEPASS_I_BARNETS_HJEM)))
    }

    @Test
    fun `isLønnLønnTilPrivatpersonerForArbeidIHjemmet predicates correctly`() {
        assert(isLønnLønnTilPrivatpersonerForArbeidIHjemmet(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.LOENN_TIL_PRIVATPERSONER_FOR_ARBEID_I_HJEMMET)))

        assertFalse(isLønnLønnTilPrivatpersonerForArbeidIHjemmet(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.LOENN_FOR_BARNEPASS_I_BARNETS_HJEM)))
        assertFalse(isLønnLønnTilPrivatpersonerForArbeidIHjemmet(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.LOENN_TIL_PRIVATPERSONER_FOR_ARBEID_I_HJEMMET)))
    }

    @Test
    fun `isLønnLønnUtbetaltAvVeldedigEllerAllmennyttigInstitusjonEllerOrganisasjon predicates correctly`() {
        assert(isLønnLønnUtbetaltAvVeldedigEllerAllmennyttigInstitusjonEllerOrganisasjon(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.LOENN_UTBETALT_AV_VELDEDIG_ELLER_ALLMENNYTTIG_INSTITUSJON_ELLER_ORGANISASJON)))

        assertFalse(isLønnLønnUtbetaltAvVeldedigEllerAllmennyttigInstitusjonEllerOrganisasjon(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.LOENN_TIL_PRIVATPERSONER_FOR_ARBEID_I_HJEMMET)))
        assertFalse(isLønnLønnUtbetaltAvVeldedigEllerAllmennyttigInstitusjonEllerOrganisasjon(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.LOENN_UTBETALT_AV_VELDEDIG_ELLER_ALLMENNYTTIG_INSTITUSJON_ELLER_ORGANISASJON)))
    }

    @Test
    fun `isLønnLønnTilVergeFraFylkesmannen predicates correctly`() {
        assert(isLønnLønnTilVergeFraFylkesmannen(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.LOENN_TIL_VERGE_FRA_FYLKESMANNEN)))

        assertFalse(isLønnLønnTilVergeFraFylkesmannen(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.LOENN_UTBETALT_AV_VELDEDIG_ELLER_ALLMENNYTTIG_INSTITUSJON_ELLER_ORGANISASJON)))
        assertFalse(isLønnLønnTilVergeFraFylkesmannen(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.LOENN_TIL_VERGE_FRA_FYLKESMANNEN)))
    }

    @Test
    fun `isLønnOpsjoner predicates correctly`() {
        assert(isLønnOpsjoner(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.OPSJONER)))

        assertFalse(isLønnOpsjoner(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.LOENN_TIL_VERGE_FRA_FYLKESMANNEN)))
        assertFalse(isLønnOpsjoner(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.OPSJONER)))
    }

    @Test
    fun `isLønnOvertidsgodtgjørelse predicates correctly`() {
        assert(isLønnOvertidsgodtgjørelse(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.OVERTIDSGODTGJOERELSE)))

        assertFalse(isLønnOvertidsgodtgjørelse(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.OPSJONER)))
        assertFalse(isLønnOvertidsgodtgjørelse(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.OVERTIDSGODTGJOERELSE)))

        assertFalse(isLønnOvertidsgodtgjørelse(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.OVERTIDSGODTGJOERELSE,
            SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY)))

        assertFalse(isLønnOvertidsgodtgjørelse(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.OVERTIDSGODTGJOERELSE,
            SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK)))
    }

    @Test
    fun `isLønnReiseAnnet predicates correctly`() {
        assert(isLønnReiseAnnet(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.REISE_ANNET)))

        assertFalse(isLønnReiseAnnet(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.OVERTIDSGODTGJOERELSE)))
        assertFalse(isLønnReiseAnnet(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.REISE_ANNET)))
    }

    @Test
    fun `isLønnReiseKost predicates correctly`() {
        assert(isLønnReiseKost(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.REISE_KOST)))

        assertFalse(isLønnReiseKost(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.REISE_ANNET)))
        assertFalse(isLønnReiseKost(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.REISE_KOST)))
    }

    @Test
    fun `isLønnReiseLosji predicates correctly`() {
        assert(isLønnReiseLosji(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.REISE_LOSJI)))

        assertFalse(isLønnReiseLosji(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.REISE_KOST)))
        assertFalse(isLønnReiseLosji(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.REISE_LOSJI)))
    }

    @Test
    fun `isLønnRentefordelLån predicates correctly`() {
        assert(isLønnRentefordelLån(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.RENTEFORDEL_LAAN)))

        assertFalse(isLønnRentefordelLån(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.REISE_LOSJI)))
        assertFalse(isLønnRentefordelLån(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.RENTEFORDEL_LAAN)))
    }

    @Test
    fun `isLønnSkattepliktigDelForsikringer predicates correctly`() {
        assert(isLønnSkattepliktigDelForsikringer(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.SKATTEPLIKTIG_DEL_FORSIKRINGER)))

        assertFalse(isLønnSkattepliktigDelForsikringer(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.RENTEFORDEL_LAAN)))
        assertFalse(isLønnSkattepliktigDelForsikringer(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.SKATTEPLIKTIG_DEL_FORSIKRINGER)))
    }

    @Test
    fun `isLønnSluttvederlag predicates correctly`() {
        assert(isLønnSluttvederlag(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.SLUTTVEDERLAG)))

        assertFalse(isLønnSluttvederlag(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.SKATTEPLIKTIG_DEL_FORSIKRINGER)))
        assertFalse(isLønnSluttvederlag(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.SLUTTVEDERLAG)))

        assertFalse(isLønnSluttvederlag(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.SLUTTVEDERLAG,
            SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY)))

        assertFalse(isLønnSluttvederlag(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.SLUTTVEDERLAG,
            SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK)))
    }

    @Test
    fun `isLønnSmusstillegg predicates correctly`() {
        assert(isLønnSmusstillegg(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.SMUSSTILLEGG)))

        assertFalse(isLønnSmusstillegg(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.SLUTTVEDERLAG)))
        assertFalse(isLønnSmusstillegg(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.SMUSSTILLEGG)))
    }

    @Test
    fun `isLønnStipend predicates correctly`() {
        assert(isLønnStipend(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.STIPEND)))

        assertFalse(isLønnStipend(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.SMUSSTILLEGG)))
        assertFalse(isLønnStipend(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.STIPEND)))
    }

    @Test
    fun `isLønnStyrehonorarOgGodtgjørelseVerv predicates correctly`() {
        assert(isLønnStyrehonorarOgGodtgjørelseVerv(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.STYREHONORAR_OG_GODTGJOERELSE_VERV)))

        assertFalse(isLønnStyrehonorarOgGodtgjørelseVerv(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.STIPEND)))
        assertFalse(isLønnStyrehonorarOgGodtgjørelseVerv(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.STYREHONORAR_OG_GODTGJOERELSE_VERV)))
    }

    @Test
    fun `isYtelseSvangerskapspenger predicates correctly`() {
        assert(isYtelseSvangerskapspenger(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.SVANGERSKAPSPENGER)))

        assertFalse(isYtelseSvangerskapspenger(DatagrunnlagKlassifisering(InntektType.YTELSE_FRA_OFFENTLIGE, InntektBeskrivelse.DAGPENGER_VED_ARBEIDSLOESHET)))
        assertFalse(isYtelseSvangerskapspenger(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.SVANGERSKAPSPENGER)))
    }

    @Test
    fun `isLønnTimelønn predicates correctly`() {
        assert(isLønnTimelønn(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.TIMELOENN)))

        assertFalse(isLønnTimelønn(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.STYREHONORAR_OG_GODTGJOERELSE_VERV)))
        assertFalse(isLønnTimelønn(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.TIMELOENN)))

        assertFalse(isLønnTimelønn(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.TIMELOENN,
            SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY)))

        assertFalse(isLønnTimelønn(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.TIMELOENN,
            SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK)))
    }

    @Test
    fun `isLønnTrekkILønnForFerie predicates correctly`() {
        assert(isLønnTrekkILønnForFerie(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.TREKK_I_LOENN_FOR_FERIE)))

        assertFalse(isLønnTrekkILønnForFerie(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.TIMELOENN)))
        assertFalse(isLønnTrekkILønnForFerie(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.TREKK_I_LOENN_FOR_FERIE)))

        assertFalse(isLønnTimelønn(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.TREKK_I_LOENN_FOR_FERIE,
            SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY)))

        assertFalse(isLønnTimelønn(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.TREKK_I_LOENN_FOR_FERIE,
            SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK)))
    }

    @Test
    fun `isLønnUregelmessigeTilleggKnyttetTilArbeidetTid predicates correctly`() {
        assert(isLønnUregelmessigeTilleggKnyttetTilArbeidetTid(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_ARBEIDET_TID)))

        assertFalse(isLønnUregelmessigeTilleggKnyttetTilArbeidetTid(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.TREKK_I_LOENN_FOR_FERIE)))
        assertFalse(isLønnUregelmessigeTilleggKnyttetTilArbeidetTid(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_ARBEIDET_TID)))

        assertFalse(isLønnUregelmessigeTilleggKnyttetTilArbeidetTid(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_ARBEIDET_TID,
            SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY)))

        assertFalse(isLønnUregelmessigeTilleggKnyttetTilArbeidetTid(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_ARBEIDET_TID,
            SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK)))
    }

    @Test
    fun `isLønnUregelmessigeTilleggKnyttetTilIkkeArbeidetTid predicates correctly`() {
        assert(isLønnUregelmessigeTilleggKnyttetTilIkkeArbeidetTid(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_IKKE_ARBEIDET_TID)))

        assertFalse(isLønnUregelmessigeTilleggKnyttetTilIkkeArbeidetTid(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_ARBEIDET_TID)))
        assertFalse(isLønnUregelmessigeTilleggKnyttetTilIkkeArbeidetTid(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_IKKE_ARBEIDET_TID)))

        assertFalse(isLønnUregelmessigeTilleggKnyttetTilIkkeArbeidetTid(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_IKKE_ARBEIDET_TID,
            SpesielleInntjeningsforhold.HYRE_TIL_MANNSKAP_PAA_FISKE_SMAAHVALFANGST_OG_SELFANGSTFARTOEY)))

        assertFalse(isLønnUregelmessigeTilleggKnyttetTilIkkeArbeidetTid(DatagrunnlagKlassifisering(
            InntektType.LOENNSINNTEKT,
            InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_IKKE_ARBEIDET_TID,
            SpesielleInntjeningsforhold.LOENN_VED_ARBEIDSMARKEDSTILTAK)))
    }

    @Test
    fun `isLønnYrkebilTjenestligbehovKilometer predicates correctly`() {
        assert(isLønnYrkebilTjenestligbehovKilometer(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.YRKEBIL_TJENESTLIGBEHOV_KILOMETER)))

        assertFalse(isLønnYrkebilTjenestligbehovKilometer(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.UREGELMESSIGE_TILLEGG_KNYTTET_TIL_IKKE_ARBEIDET_TID)))
        assertFalse(isLønnYrkebilTjenestligbehovKilometer(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.YRKEBIL_TJENESTLIGBEHOV_KILOMETER)))
    }

    @Test
    fun `isLønnYrkebilTjenestligbehovListepris predicates correctly`() {
        assert(isLønnYrkebilTjenestligbehovListepris(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.YRKEBIL_TJENESTLIGBEHOV_LISTEPRIS)))

        assertFalse(isLønnYrkebilTjenestligbehovListepris(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.YRKEBIL_TJENESTLIGBEHOV_KILOMETER)))
        assertFalse(isLønnYrkebilTjenestligbehovListepris(DatagrunnlagKlassifisering(InntektType.NAERINGSINNTEKT, InntektBeskrivelse.YRKEBIL_TJENESTLIGBEHOV_LISTEPRIS)))
    }

    @Test
    fun ` Tips skal klassifiseres som arbeidsinntekt `() {
        assert(isTips(DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.TIPS)))
    }
}