package no.nav.dagpenger.inntekt.inntektskomponenten.v1

interface InntektskomponentClient {

    fun getInntekt(request: InntektkomponentRequest): InntektkomponentResponse
}