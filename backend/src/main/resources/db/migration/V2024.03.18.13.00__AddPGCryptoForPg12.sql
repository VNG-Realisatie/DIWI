-- gen_random_uuid is needed for migration V2024.03.18.14.00__AddSystemUser,
-- but is not present on older postgres instances than version 13. Linux mint
-- has version 12 by default so add pgcrypto to add support for it.
CREATE EXTENSION IF NOT EXISTS pgcrypto;
