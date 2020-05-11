ALTER TABLE inntekt_v1_arena_mapping
    DROP CONSTRAINT inntekt_v1_arena_mapping_inntektid_fkey,
    ADD CONSTRAINT inntekt_v1_arena_mapping_inntektid_fkey FOREIGN KEY (inntektid) REFERENCES inntekt_v1 (id) ON DELETE CASCADE;