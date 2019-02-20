package no.nav.dagpenger.inntekt.oidc

interface OidcClient {
    fun oidcToken(): OidcToken
}
