-- The script V2024.09.10.14.59__FixPriceCategoriesMigration.sql was modified to fix https://trello.com/c/JznXuVZ5/745-bug-in-161-upgrade
-- However, at this point there were already production systems with the old migration file.
-- On these systems, no additional statements need to be executed to reflect the change,
-- BUT we need to repair the Flyway checksum to match the expected value during the 'validate' command.
UPDATE public.flyway_schema_history
SET checksum = -1202401919
WHERE (version, checksum) = ('2024.09.10.14.00', 1122270745);
