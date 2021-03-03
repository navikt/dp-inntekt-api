ALTER TABLE inntekt_v1_person_mapping
    DROP CONSTRAINT inntekt_v1_person_mapping_pkey;
ALTER TABLE inntekt_v1_person_mapping
    RENAME vedtakId TO kontekstId;

CREATE TYPE kontekstTypeNavn AS ENUM ('vedtak', 'saksbehandling', 'veiledning', 'soknad', 'revurdering', 'forskudd');

ALTER TABLE inntekt_v1_person_mapping
    ADD COLUMN kontekstType kontekstTypeNavn NOT NULL DEFAULT 'vedtak',
    ADD PRIMARY KEY (inntektId, aktørId, kontekstId, kontekstType, beregningsdato);
