INSERT INTO inntekt_v1(id, brukt ,inntekt)
VALUES ('01E46501PMY105AFXE4XF088MV', TRUE, '{
  "arbeidsInntektMaaned": [],
  "ident": {
    "identifikator": "-1",
    "aktoerType": "NATURLIG_IDENT"
  }
}')
ON CONFLICT DO NOTHING;

INSERT INTO inntekt_v1_person_mapping (inntektid, aktørid, vedtakid, fnr, beregningsdato)
VALUES ('01E46501PMY105AFXE4XF088MV', 'INGEN_INNTEKT', 12345, null, '2019-01-01')
ON CONFLICT DO NOTHING;

INSERT INTO temp_inntekt_v1_person_mapping (inntektid, aktørid, vedtakid, fnr, beregningsdato)
VALUES ('01E46501PMY105AFXE4XF088MV', 'INGEN_INNTEKT', 12345, null, '2019-02-02')
ON CONFLICT DO NOTHING;

INSERT INTO temp_inntekt_v1_person_mapping (inntektid, aktørid, vedtakid, fnr, beregningsdato)
VALUES ('01EDBSHDENAHCVBYT02W160E6X', 'INGEN_INNTEKT', 12345, null, '2019-03-03')
ON CONFLICT DO NOTHING;
