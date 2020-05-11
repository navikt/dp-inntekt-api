ALTER TABLE inntekt_v1
    ADD COLUMN IF NOT EXISTS manuelt_redigert BOOLEAN DEFAULT false;