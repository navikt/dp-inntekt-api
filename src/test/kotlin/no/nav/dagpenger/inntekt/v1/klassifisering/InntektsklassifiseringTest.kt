package no.nav.dagpenger.inntekt.v1.klassifisering

import no.nav.dagpenger.inntekt.v1.InntektBeskrivelse
import no.nav.dagpenger.inntekt.v1.InntektType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class InntektsklassifiseringTest {

    @Test
    fun `matchesSingularPredicate returns true when one predicate matches`() {
        fun pred1(datagrunnlag: DatagrunnlagKlassifisering): Boolean = true
        fun pred2(datagrunnlag: DatagrunnlagKlassifisering): Boolean = false
        fun pred3(datagrunnlag: DatagrunnlagKlassifisering): Boolean = false

        var predicates = listOf(::pred1, ::pred2, ::pred3)

        assert(matchesSingularPredicate(
            DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FASTLOENN),
            predicates
            )
        )
    }

    @Test
    fun `matchesSingularPredicate returns throws error when multiple predicates match`() {
        fun pred1(datagrunnlag: DatagrunnlagKlassifisering): Boolean = true
        fun pred2(datagrunnlag: DatagrunnlagKlassifisering): Boolean = true
        fun pred3(datagrunnlag: DatagrunnlagKlassifisering): Boolean = false

        var predicates = listOf(::pred1, ::pred2, ::pred3)

        assertThrows<MultipleMatchingPredicatesException> {
            matchesSingularPredicate(
                DatagrunnlagKlassifisering(InntektType.LOENNSINNTEKT, InntektBeskrivelse.FASTLOENN),
                predicates
            )
        }
    }
}