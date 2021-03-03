UPDATE inntekt_v1_person_mapping
SET kontekstType = CASE
                       WHEN kontekstId = '-12345' THEN 'soknad'::kontekstTypeNavn
                       WHEN kontekstId = '-1337' THEN 'forskudd'::kontekstTypeNavn
                       WHEN kontekstId = '-3000' THEN 'saksbehandling'::kontekstTypeNavn
                       ELSE 'vedtak'::kontekstTypeNavn
    END;
