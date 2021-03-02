INSERT INTO inntekt_v1(id, brukt ,inntekt)
VALUES ('01EZSG85NR53Z3FB3KGJQDYCYY', TRUE, '{
"arbeidsInntektMaaned": [],
"ident": {
    "identifikator": "-1",
    "aktoerType": "NATURLIG_IDENT"
}
}')
ON CONFLICT DO NOTHING;

INSERT INTO inntekt_v1_person_mapping (inntektid, aktørid, vedtakid, fnr, beregningsdato)
VALUES ('01EZSG85NR53Z3FB3KGJQDYCYY', 'AKTØR_ID', '-1337', null, '2019-01-01')
ON CONFLICT DO NOTHING;