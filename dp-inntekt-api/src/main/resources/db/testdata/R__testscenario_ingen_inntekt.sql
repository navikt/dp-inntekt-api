INSERT INTO inntekt_v1(id, brukt ,inntekt)
VALUES ('01E46501PMY105AFXE4XF088MV', TRUE, '{
  "arbeidsInntektMaaned": [],
  "ident": {
    "identifikator": "-1",
    "aktoerType": "NATURLIG_IDENT"
  }
}')
ON CONFLICT DO NOTHING;

INSERT INTO inntekt_v1_person_mapping (inntektid, aktørid, kontekstId, kontekstType, fnr, beregningsdato)
VALUES ('01E46501PMY105AFXE4XF088MV', 'INGEN_INNTEKT', '12345', 'vedtak'::kontekstTypeNavn, null, '2019-07-01')
ON CONFLICT DO NOTHING;
