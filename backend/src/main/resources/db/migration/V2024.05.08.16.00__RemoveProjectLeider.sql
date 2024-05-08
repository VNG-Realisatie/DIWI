DELETE FROM diwi_testset.organization_to_project WHERE project_rol = 'PROJECT_LEIDER';

ALTER TABLE diwi_testset.organization_to_project
    DROP COLUMN project_rol;

DROP TYPE diwi_testset.project_rol;
