package no.nav.dagpenger.inntekt.v1

import com.auth0.jwk.JwkProvider
import io.ktor.application.Application
import io.mockk.mockk
import no.nav.dagpenger.inntekt.AuthApiKeyVerifier
import no.nav.dagpenger.inntekt.BehandlingsInntektsGetter
import no.nav.dagpenger.inntekt.db.InntektStore
import no.nav.dagpenger.inntekt.inntektApi
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektskomponentClient

internal fun mockInntektApi(
    inntektskomponentClient: InntektskomponentClient = mockk(),
    inntektStore: InntektStore = mockk(),
    behandlingsInntektsGetter: BehandlingsInntektsGetter = mockk(),
    apiAuthApiKeyVerifier: AuthApiKeyVerifier = mockk(relaxed = true),
    jwkProvider: JwkProvider = mockk(relaxed = true)
): Application.() -> Unit {
    return fun Application.() {
        inntektApi(
            inntektskomponentHttpClient = inntektskomponentClient,
            inntektStore = inntektStore,
            behandlingsInntektsGetter = behandlingsInntektsGetter,
            apiAuthApiKeyVerifier = apiAuthApiKeyVerifier,
            jwkProvider = jwkProvider
        )
    }
}