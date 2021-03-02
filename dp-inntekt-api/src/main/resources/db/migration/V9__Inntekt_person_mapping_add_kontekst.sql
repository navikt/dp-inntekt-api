ALTER TABLE inntekt_V1_person_mapping
    DROP CONSTRAINT inntekt_V1_person_mapping_pkey,
    RENAME COLUMN vedtakId TO kontekstId,
    CREATE TYPE KONTEKSTTYPE as ENUM('vedtak', 'saksbehandling', 'veiledning', 'soknad', 'revurdering', 'corona'),
    ADD COLUMN kontekstType as KONTEKSTTYPE NOT NULL DEFAULT 'vedtak',
    ADD PRIMARY KEY (inntektId, aktørId, kontekstId, kontekstType, beregningsdato);


