INSERT INTO inntekt_v1(id, brukt, inntekt)
VALUES ('01E46449RBXT3S9AH4K8D10NQ6', TRUE, '
{
  "arbeidsInntektMaaned": [
    {
      "aarMaaned": "2019-01",
      "arbeidsInntektInformasjon": {
        "inntektListe": [
          {
            "inntektType": "LOENNSINNTEKT",
            "beloep": 70000,
            "fordel": "kontantytelse",
            "inntektskilde": "A-ordningen",
            "inntektsperiodetype": "Maaned",
            "inntektsstatus": "LoependeInnrapportert",
            "leveringstidspunkt": "2019-02",
            "utbetaltIMaaned": "2018-03",
            "opplysningspliktig": {
              "identifikator": "1111111",
              "aktoerType": "ORGANISASJON"
            },
            "virksomhet": {
              "identifikator": "1111111",
              "aktoerType": "ORGANISASJON"
            },
            "inntektsmottaker": {
              "identifikator": "99999999999",
              "aktoerType": "NATURLIG_IDENT"
            },
            "inngaarIGrunnlagForTrekk": true,
            "utloeserArbeidsgiveravgift": true,
            "informasjonsstatus": "InngaarAlltid",
            "beskrivelse": "fastloenn"
          }
        ]
      }
    },
    {
      "aarMaaned": "2019-03",
      "arbeidsInntektInformasjon": {
        "inntektListe": [
          {
            "inntektType": "LOENNSINNTEKT",
            "beloep": 25000,
            "fordel": "kontantytelse",
            "inntektskilde": "A-ordningen",
            "inntektsperiodetype": "Maaned",
            "inntektsstatus": "LoependeInnrapportert",
            "leveringstidspunkt": "2019-02",
            "utbetaltIMaaned": "2018-03",
            "opplysningspliktig": {
              "identifikator": "1111111",
              "aktoerType": "ORGANISASJON"
            },
            "virksomhet": {
              "identifikator": "1111111",
              "aktoerType": "ORGANISASJON"
            },
            "inntektsmottaker": {
              "identifikator": "99999999999",
              "aktoerType": "NATURLIG_IDENT"
            },
            "inngaarIGrunnlagForTrekk": true,
            "utloeserArbeidsgiveravgift": true,
            "informasjonsstatus": "InngaarAlltid",
            "beskrivelse": "fastloenn"
          }
        ]
      }
    },
    {
      "aarMaaned": "2019-04",
      "arbeidsInntektInformasjon": {
        "inntektListe": [
          {
            "inntektType": "LOENNSINNTEKT",
            "beloep": 55000,
            "fordel": "kontantytelse",
            "inntektskilde": "A-ordningen",
            "inntektsperiodetype": "Maaned",
            "inntektsstatus": "LoependeInnrapportert",
            "leveringstidspunkt": "2019-02",
            "utbetaltIMaaned": "2018-03",
            "opplysningspliktig": {
              "identifikator": "1111111",
              "aktoerType": "ORGANISASJON"
            },
            "virksomhet": {
              "identifikator": "1111111",
              "aktoerType": "ORGANISASJON"
            },
            "inntektsmottaker": {
              "identifikator": "99999999999",
              "aktoerType": "NATURLIG_IDENT"
            },
            "inngaarIGrunnlagForTrekk": true,
            "utloeserArbeidsgiveravgift": true,
            "informasjonsstatus": "InngaarAlltid",
            "beskrivelse": "fastloenn"
          }
        ]
      }
    },
    {
      "aarMaaned": "2018-12",
      "arbeidsInntektInformasjon": {
        "inntektListe": [
          {
            "inntektType": "LOENNSINNTEKT",
            "beloep": 25000,
            "fordel": "kontantytelse",
            "inntektskilde": "A-ordningen",
            "inntektsperiodetype": "Maaned",
            "inntektsstatus": "LoependeInnrapportert",
            "leveringstidspunkt": "2019-02",
            "utbetaltIMaaned": "2018-03",
            "opplysningspliktig": {
              "identifikator": "1111111",
              "aktoerType": "ORGANISASJON"
            },
            "virksomhet": {
              "identifikator": "1111111",
              "aktoerType": "ORGANISASJON"
            },
            "inntektsmottaker": {
              "identifikator": "99999999999",
              "aktoerType": "NATURLIG_IDENT"
            },
            "inngaarIGrunnlagForTrekk": true,
            "utloeserArbeidsgiveravgift": true,
            "informasjonsstatus": "InngaarAlltid",
            "beskrivelse": "fastloenn"
          }
        ]
      }
    }
  ],
  "ident": {
    "identifikator": "-1",
    "aktoerType": "NATURLIG_IDENT"
  }
}')
ON CONFLICT DO NOTHING;

INSERT INTO inntekt_v1_person_mapping (inntektid, aktørid, kontekstId, kontekstType, fnr, beregningsdato)
VALUES ('01E46449RBXT3S9AH4K8D10NQ6', '1.5_G_INNTEKT', '12345', 'corona'::kontekstTypeNavn, null, '2019-07-01')
ON CONFLICT DO NOTHING;
