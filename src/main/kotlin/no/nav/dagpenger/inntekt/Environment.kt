package no.nav.dagpenger.inntekt

data class Environment(
    val username: String = getEnvVar("SRVDP_INNTEKT_API_USERNAME", "igroup"),
    val password: String = getEnvVar("SRVDP_INNTEKT_API_PASSWORD", "itest"),
    val oicdStsUrl: String = getEnvVar("OIDC_STS_ISSUERURL", ""),
    val hentinntektListeUrl: String = getEnvVar("HENTINNTEKTLISTE_URL", ""),
    val httpPort: Int = 8099
)

fun getEnvVar(varName: String, defaultValue: String? = null) =
        System.getenv(varName) ?: defaultValue ?: throw RuntimeException("Missing required variable \"$varName\"")
