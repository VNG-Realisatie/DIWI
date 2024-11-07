UPDATE diwi.property_state
    SET mandatory = true
    WHERE property_name = 'municipality' AND change_end_date IS NULL;

UPDATE diwi.property_state
    SET single_select = true
    WHERE property_name = 'municipality' AND change_end_date IS NULL;
