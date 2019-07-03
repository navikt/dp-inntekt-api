package no.nav.dagpenger.inntekt.inntektskomponenten.v1

interface InntektskomponentClient {

    suspend fun getInntekt(request: InntektkomponentRequest): InntektkomponentResponse
}