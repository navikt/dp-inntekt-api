package no.nav.dagpenger.inntekt.oppslag.pdl

import com.expediagroup.graphql.client.GraphQLClient
import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import com.expediagroup.graphql.types.GraphQLResponse
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.util.KtorExperimentalAPI
import mu.KotlinLogging
import no.nav.dagpenger.inntekt.oppslag.Person
import no.nav.dagpenger.inntekt.oppslag.PersonOppslag
import no.nav.pdl.HentPerson
import java.net.URL
import java.util.UUID

private val log = KotlinLogging.logger { }
private val sikkerlogg = KotlinLogging.logger("tjenestekall")

@KtorExperimentalAPI
class PdlGraphQLRepository constructor(
    client: GraphQLClient
) : PersonOppslag {

    private val query = HentPerson(client)
    override suspend fun hentPerson(aktørId: String): Person? {
        val result = query.execute(HentPerson.Variables(ident = aktørId))

        return if (result.errors?.isNotEmpty() == true) {
            log.error { "Feil i GraphQL-responsen: ${result.errors}" }
            null
        } else {
            result.toPerson()
        }
    }

    private fun GraphQLResponse<HentPerson.Result>.toPerson(): Person? {
        val navn: HentPerson.Navn? = data?.hentPerson?.navn?.firstOrNull()
        val fødselsnummer = data?.hentIdenter?.identer?.firstOrNull()?.ident
        return fødselsnummer?.let { fnr ->
            navn?.let { navn ->
                Person(
                    fødselsnummer = fnr,
                    etternavn = navn.etternavn,
                    mellomnavn = navn.mellomnavn,
                    fornavn = navn.fornavn
                )
            }
        }
    }
}

@KtorExperimentalAPI
fun PdlGraphQLClientFactory(
    url: String,
    oidcProvider: () -> String
) = GraphQLKtorClient(url = URL(url)) {
    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) = sikkerlogg.info { message }
        }

        level = LogLevel.HEADERS
    }

    defaultRequest {
        oidcProvider().also {
            header(HttpHeaders.Authorization, "Bearer $it")
            header("Nav-Consumer-Token", "Bearer $it")
        }

        header("User-Agent", "dp-inntekt-api")
        header("Tema", "DAG")
        header("Nav-Call-Id", UUID.randomUUID())
    }
}
