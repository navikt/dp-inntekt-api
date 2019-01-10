
package no.nav.dagpenger.inntekt

import spock.lang.Specification

class ApiTest extends Specification {
    def " test spock integration "() {
        setup:
        def lib = new Api()

        when:
        def result = lib.some()

        then:
        result
    }
}
