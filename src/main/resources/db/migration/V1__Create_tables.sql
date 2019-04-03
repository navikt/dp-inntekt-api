CREATE TABLE IF NOT EXISTS inntekt_V1
(
  id        TEXT                     NOT NULL,
  inntekt   JSONB                    NOT NULL,
  timestamp TIMESTAMP WITH TIME ZONE NOT NULL default (now() at time zone 'utc'),
  PRIMARY KEY (id)

);


CREATE TABLE IF NOT EXISTS inntekt_V1_arena_mapping
(
  inntektId      TEXT REFERENCES inntekt_V1 (id) NOT NULL,
  aktørId        VARCHAR(20)                     NOT NULL,
  vedtakId       NUMERIC                         NOT NULL,
  beregningsdato DATE                            NOT NULL,
  timestamp      TIMESTAMP WITH TIME ZONE        NOT NULL default (now() at time zone 'utc'),
  PRIMARY KEY (inntektId, aktørId, vedtakId, beregningsdato)
)