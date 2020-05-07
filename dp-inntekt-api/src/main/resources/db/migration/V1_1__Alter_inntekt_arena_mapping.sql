ALTER TABLE inntekt_V1_arena_mapping
    DROP CONSTRAINT inntekt_V1_arena_mapping_pkey;


ALTER TABLE inntekt_V1_arena_mapping
    ADD PRIMARY KEY (inntektId, aktørId, vedtakId, beregningsdato);