package no.nav.dagpenger.inntekt.oppslag

interface PersonOppslag {
    suspend fun hentPerson(aktørId: String): Person?
}

data class Person(
    val fødselsnummer: String,
    val fornavn: String,
    val mellomnavn: String? = null,
    val etternavn: String
) {
    fun sammensattNavn(): String = "$etternavn, $fornavn $mellomnavn"
}
