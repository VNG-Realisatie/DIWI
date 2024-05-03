ALTER TABLE diwi_testset.user_state 
DROP COLUMN IF EXISTS "role";

DROP TYPE IF EXISTS diwi_testset.user_role;
CREATE TYPE diwi_testset.user_role AS ENUM ( 
	'Admin',
    'UserPlus',
    'User',
    'Management',
    'Council',
    'External'
);

ALTER TABLE diwi_testset.user_state 
ADD COLUMN "role" diwi_testset.user_role NOT NULL DEFAULT 'UserPlus';

ALTER TABLE diwi_testset.user_state 
ALTER COLUMN "role" DROP DEFAULT;