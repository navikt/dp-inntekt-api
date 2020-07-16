CREATE TABLE IF NOT EXISTS temp_inntekt_V1_person_mapping_
(
    inntektId      TEXT                     NOT NULL,
    aktørId        VARCHAR(20)              NOT NULL,
    fnr            VARCHAR(11)              NULL,
    vedtakId       VARCHAR(26)              NOT NULL,
    beregningsdato DATE                     NOT NULL,
    timestamp      TIMESTAMP WITH TIME ZONE NOT NULL default (now() at time zone 'utc'),
    PRIMARY KEY (inntektId, aktørId, vedtakId, beregningsdato)
);


