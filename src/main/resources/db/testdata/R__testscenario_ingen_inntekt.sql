INSERT INTO inntekt_v1(id, inntekt)
VALUES ('01D8FSKGXWHE1KBXBSR9BBFAMA', '{}')
ON CONFLICT DO NOTHING;

INSERT INTO inntekt_v1_arena_mapping (inntektid, aktørid, vedtakid, beregningsdato)
VALUES ('01D8FSKGXWHE1KBXBSR9BBFAMA', 'INGEN_INNTEKT', 12345, '2019-07-01')
ON CONFLICT DO NOTHING;

UPDATE inntekt_v1
SET inntekt = '
{
  "arbeidsInntektMaaned": [],
  "ident": {
    "identifikator": "-1",
    "aktoerType": "NATURLIG_IDENT"
  }
}' , brukt = TRUE
WHERE id = '01D8FSKGXWHE1KBXBSR9BBFAMA';

