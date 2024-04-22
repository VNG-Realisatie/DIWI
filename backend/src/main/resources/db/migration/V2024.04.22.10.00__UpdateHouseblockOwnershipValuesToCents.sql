UPDATE diwi_testset.woningblok_eigendom_en_waarde_changelog
    SET waarde_value = waarde_value * 100
    WHERE waarde_value IS NOT NULL;

UPDATE diwi_testset.woningblok_eigendom_en_waarde_changelog
    SET huurbedrag_value = huurbedrag_value * 100
    WHERE huurbedrag_value IS NOT NULL;

UPDATE diwi_testset.woningblok_eigendom_en_waarde_changelog
    SET waarde_value_range = int4range(lower(waarde_value_range) * 100, upper(waarde_value_range) * 100, '[]')
    WHERE waarde_value_range IS NOT NULL;

UPDATE diwi_testset.woningblok_eigendom_en_waarde_changelog
    SET huurbedrag_value_range = int4range(lower(huurbedrag_value_range) * 100, upper(huurbedrag_value_range) * 100, '[]')
    WHERE huurbedrag_value_range IS NOT NULL;
