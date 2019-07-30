package no.nav.dagpenger.inntekt.v1

import com.auth0.jwk.JwkProvider
import io.ktor.application.Application
import io.mockk.mockk
import no.nav.dagpenger.inntekt.AuthApiKeyVerifier
import no.nav.dagpenger.inntekt.BehandlingsInntektsGetter
import no.nav.dagpenger.inntekt.db.InntektStore
import no.nav.dagpenger.inntekt.inntektApi
import no.nav.dagpenger.inntekt.inntektskomponenten.v1.InntektskomponentClient
import no.nav.dagpenger.inntekt.oppslag.OppslagClient

internal fun mockInntektApi(
    inntektskomponentClient: InntektskomponentClient = mockk(),
    inntektStore: InntektStore = mockk(),
    behandlingsInntektsGetter: BehandlingsInntektsGetter = mockk(),
    oppslagClient: OppslagClient = mockk(),
    apiAuthApiKeyVerifier: AuthApiKeyVerifier = mockk(relaxed = true),
    jwkProvider: JwkProvider = mockk(relaxed = true)
): Application.() -> Unit {
    return fun Application.() {
        inntektApi(
            inntektskomponentClient,
            inntektStore,
            behandlingsInntektsGetter,
            oppslagClient,
            apiAuthApiKeyVerifier,
            jwkProvider
        )
    }
}