package no.nav.dagpenger.inntekt.v1

import io.ktor.application.Application
import io.mockk.mockk
import no.nav.dagpenger.inntekt.AuthApiKeyVerifier
import no.nav.dagpenger.inntekt.brreg.enhetsregisteret.EnhetsregisteretHttpClient
import no.nav.dagpenger.inntekt.db.InntektStore
import no.nav.dagpenger.inntekt.ident.AktørregisterHttpClient
import no.nav.dagpenger.inntekt.inntektApi
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektskomponentClient
import no.nav.dagpenger.inntekt.oppslag.PersonNameHttpClient

fun Application.mockedInntektApi(
    inntektskomponentClient: InntektskomponentClient = mockk(),
    inntektStore: InntektStore = mockk(relaxed = true),
    enhetsregisteretHttpClient: EnhetsregisteretHttpClient = mockk(),
    personNameHttpClient: PersonNameHttpClient = mockk(),
    aktørregisterHttpClient: AktørregisterHttpClient = mockk(),
    apiAuthApiKeyVerifier: AuthApiKeyVerifier = mockk(relaxed = true)
) {
    return inntektApi(
        inntektskomponentClient,
        inntektStore,
        enhetsregisteretHttpClient,
        personNameHttpClient,
        aktørregisterHttpClient,
        apiAuthApiKeyVerifier
    )
}
