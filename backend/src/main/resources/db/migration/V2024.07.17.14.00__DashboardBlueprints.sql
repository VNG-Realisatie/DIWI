CREATE TABLE diwi.blueprint (
    id UUID NOT NULL
);

ALTER TABLE ONLY diwi.blueprint
    ADD CONSTRAINT blueprint_pkey PRIMARY KEY (id);

CREATE TABLE diwi.blueprint_state (
    id UUID NOT NULL,
    blueprint_id UUID NOT NULL,
    name TEXT NOT NULL,
    create_user_id UUID NOT NULL,
    change_user_id UUID,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);

ALTER TABLE ONLY diwi.blueprint_state
    ADD CONSTRAINT blueprint_state_pkey PRIMARY KEY (id);

ALTER TABLE ONLY diwi.blueprint_state
    ADD CONSTRAINT fk_blueprint_state__blueprint FOREIGN KEY ("blueprint_id") REFERENCES diwi."blueprint"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi.blueprint_state
    ADD CONSTRAINT fk_blueprint_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi.blueprint_state
    ADD CONSTRAINT fk_blueprint_state__create_user FOREIGN KEY ("create_user_id") REFERENCES diwi."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


CREATE TYPE diwi.blueprint_element AS ENUM (
    'MUTATION',
    'PROJECT_PHASE',
    'TARGET_GROUP',
    'PHYSICAL_APPEARANCE',
    'OWNERSHIP_BUY',
    'OWNERSHIP_RENT',
    'PROJECT_MAP',
    'RESIDENTIAL_PROJECTS',
    'DELIVERABLES',
    'DELAYED_PROJECTS'
);

CREATE TABLE diwi.blueprint_to_element (
    id UUID NOT NULL,
    blueprint_state_id UUID NOT NULL,
    element diwi.blueprint_element
);

ALTER TABLE ONLY diwi.blueprint_to_element
    ADD CONSTRAINT blueprint_to_element_pkey PRIMARY KEY (id);

ALTER TABLE ONLY diwi.blueprint_to_element
    ADD CONSTRAINT fk_blueprint_to_element__blueprint_state FOREIGN KEY ("blueprint_state_id") REFERENCES diwi."blueprint_state"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


CREATE TABLE diwi.blueprint_to_usergroup (
    id UUID NOT NULL,
    blueprint_state_id UUID NOT NULL,
    usergroup_id UUID NOT NULL
);

ALTER TABLE ONLY diwi.blueprint_to_usergroup
    ADD CONSTRAINT blueprint_to_usergroup_pkey PRIMARY KEY (id);

ALTER TABLE ONLY diwi.blueprint_to_usergroup
    ADD CONSTRAINT fk_blueprint_to_usergroup__blueprint_state FOREIGN KEY ("blueprint_state_id") REFERENCES diwi."blueprint_state"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi.blueprint_to_usergroup
    ADD CONSTRAINT fk_blueprint_to_usergroup__usergroup FOREIGN KEY ("usergroup_id") REFERENCES diwi."usergroup"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
