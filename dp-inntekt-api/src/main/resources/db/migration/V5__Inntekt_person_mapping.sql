CREATE TABLE IF NOT EXISTS inntekt_V1_person_mapping
(
    inntektId      TEXT REFERENCES inntekt_V1 (id) ON DELETE CASCADE NOT NULL,
    aktørId        VARCHAR(20)                                       NOT NULL,
    fnr            VARCHAR(11)                                       NULL,
    vedtakId       VARCHAR(26)                                       NOT NULL,
    beregningsdato DATE                                              NOT NULL,
    timestamp      TIMESTAMP WITH TIME ZONE                          NOT NULL default (now() at time zone 'utc'),
    PRIMARY KEY (inntektId, aktørId, vedtakId, beregningsdato)
);


