INSERT INTO inntekt_v1(id, brukt, inntekt)
VALUES ('01D8G3TGH7Q9NKT7BPAG5XGXRT', TRUE, '
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

INSERT INTO inntekt_v1_arena_mapping (inntektid, aktørid, vedtakid, beregningsdato)
VALUES ('01D8G3TGH7Q9NKT7BPAG5XGXRT', '1.5_G_INNTEKT', 12345, '2019-07-01')
ON CONFLICT DO NOTHING;;
