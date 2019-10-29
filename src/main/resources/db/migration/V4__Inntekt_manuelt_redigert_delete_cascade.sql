ALTER TABLE inntekt_v1_manuelt_redigert
    DROP CONSTRAINT inntekt_v1_manuelt_redigert_inntekt_id_fkey,
    ADD CONSTRAINT inntekt_v1_manuelt_redigert_inntekt_id_fkey FOREIGN KEY (inntekt_id) REFERENCES inntekt_v1 (id) ON DELETE CASCADE;