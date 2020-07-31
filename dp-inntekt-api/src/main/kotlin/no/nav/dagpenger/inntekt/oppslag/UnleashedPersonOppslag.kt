package no.nav.dagpenger.inntekt.oppslag

import no.finn.unleash.Unleash

class UnleashedPersonOppslag(
    private val unleash: Unleash,
    private val pdlPersonOppslag: PersonOppslag,
    private val legacyPersonOppslag: PersonOppslag
) : PersonOppslag {
    override suspend fun hentPerson(aktørId: String): Person? {
        return if (unleash.isEnabled("dp-inntekt-api.Pdl")) {
            pdlPersonOppslag.hentPerson(aktørId)
        } else {
            legacyPersonOppslag.hentPerson(aktørId)
        }
    }
}
