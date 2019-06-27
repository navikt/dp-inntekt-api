CREATE TABLE IF NOT EXISTS inntekt_V1_manuelt_redigert
(
    inntekt_id  TEXT REFERENCES inntekt_V1 (id) NOT NULL,
    redigert_av TEXT                            NOT NULL,
    timestamp   TIMESTAMP WITH TIME ZONE        NOT NULL default (now() at time zone 'utc'),
    PRIMARY KEY (inntekt_id)
);

