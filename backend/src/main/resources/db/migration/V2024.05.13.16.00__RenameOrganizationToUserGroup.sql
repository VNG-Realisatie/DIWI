ALTER TABLE diwi_testset.organization RENAME TO usergroup;
ALTER TABLE diwi_testset.organization_state RENAME TO usergroup_state;

ALTER TABLE diwi_testset.usergroup_state
    RENAME COLUMN organization_id TO usergroup_id;
ALTER TABLE diwi_testset.usergroup_state
    RENAME COLUMN parent_organization_id TO parent_usergroup_id;

ALTER TABLE diwi_testset.organization_to_project RENAME TO usergroup_to_project;
ALTER TABLE diwi_testset.usergroup_to_project
    RENAME COLUMN organization_id TO usergroup_id;

ALTER TABLE diwi_testset.organization_to_document RENAME TO usergroup_to_document;
ALTER TABLE diwi_testset.usergroup_to_document
    RENAME COLUMN organization_id TO usergroup_id;

ALTER TABLE diwi_testset.organization_to_plan RENAME TO usergroup_to_plan;
ALTER TABLE diwi_testset.usergroup_to_plan
    RENAME COLUMN organization_id TO usergroup_id;

ALTER TABLE diwi_testset.user_to_organization RENAME TO user_to_usergroup;
ALTER TABLE diwi_testset.user_to_usergroup
    RENAME COLUMN organization_id TO usergroup_id;




