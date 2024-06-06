ALTER SCHEMA diwi_testset RENAME TO diwi;
ALTER FUNCTION create_demo_user_org
    SET SCHEMA diwi;

SET search_path TO public;
