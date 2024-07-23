UPDATE diwi.project_state
    SET confidentiality_level = 'INTERNAL_CIVIL'
WHERE confidentiality_level = 'PRIVATE' AND change_end_date IS NULL;
