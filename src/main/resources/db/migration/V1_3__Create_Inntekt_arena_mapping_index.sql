CREATE INDEX IF NOT EXISTS inntekt_v1_arena_mapping_index
    ON inntekt_v1_arena_mapping (aktørid, vedtakid, beregningsdato, timestamp DESC);
