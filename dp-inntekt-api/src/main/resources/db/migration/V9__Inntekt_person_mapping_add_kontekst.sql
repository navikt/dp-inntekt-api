ALTER TABLE inntekt_v1_person_mapping
    DROP CONSTRAINT inntekt_v1_person_mapping_pkey;
ALTER TABLE inntekt_v1_person_mapping
    RENAME vedtakId TO kontekstId;

CREATE TYPE kontekstTypeNavn AS ENUM ('vedtak', 'saksbehandling', 'veiledning', 'soknad', 'revurdering', 'corona');

ALTER TABLE inntekt_v1_person_mapping
    ADD COLUMN kontekstType kontekstTypeNavn NOT NULL DEFAULT 'vedtak',
    ADD PRIMARY KEY (inntektId, aktørId, kontekstId, kontekstType, beregningsdato);

UPDATE inntekt_v1_person_mapping
SET kontekstType = CASE
                       WHEN kontekstId = '-12345' THEN 'soknad'::kontekstTypeNavn
                       WHEN kontekstId = '-1337' THEN 'veiledning'::kontekstTypeNavn
                       WHEN kontekstId = '-3000' THEN 'saksbehandling'::kontekstTypeNavn
                       ELSE 'vedtak'::kontekstTypeNavn
    END;
