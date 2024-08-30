ALTER TABLE diwi.plan_state RENAME COLUMN naam TO name;
ALTER TABLE diwi.plan_state RENAME COLUMN start_datum TO start_date;
ALTER TABLE diwi.plan_state RENAME COLUMN doel_waarde TO goal_value;
ALTER TABLE diwi.plan_state RENAME COLUMN doel_richting TO goal_direction;
ALTER TABLE diwi.plan_state RENAME COLUMN doel_soort TO goal_type;
ALTER TABLE diwi.plan_state DROP COLUMN confidentiality_level;
ALTER TABLE diwi.plan_state ALTER COLUMN goal_value TYPE NUMERIC(16,2);

ALTER TABLE diwi.plan_conditie_state RENAME COLUMN conditie_type TO condition_type;

ALTER TYPE diwi.doel_richting RENAME TO goal_direction;
ALTER TYPE diwi.goal_direction RENAME VALUE 'MINIMAAL' TO 'MINIMAL';
ALTER TYPE diwi.goal_direction RENAME VALUE 'MAXIMAAL' TO 'MAXIMAL';

ALTER TYPE diwi.doel_soort RENAME TO goal_type;
ALTER TYPE diwi.goal_type RENAME VALUE 'AANTAL' TO 'NUMBER';

ALTER TYPE diwi.conditie_type RENAME TO condition_type;
ALTER TYPE diwi.condition_type RENAME VALUE 'PLAN_CONDITIE' TO 'PLAN_CONDITION';
ALTER TYPE diwi.condition_type RENAME VALUE 'DOEL_CONDITIE' TO 'GOAL_CONDITION';


ALTER TABLE diwi.plan_conditie_maatwerk_boolean RENAME TO plan_conditie_property_boolean;
ALTER TABLE diwi.plan_conditie_property_boolean
    ADD COLUMN IF NOT EXISTS property_id UUID NOT NULL;
ALTER TABLE ONLY diwi.plan_conditie_property_boolean
    ADD CONSTRAINT fk_plan_conditie_property_boolean__property FOREIGN KEY ("property_id") REFERENCES diwi.property("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE diwi.plan_conditie_property_boolean RENAME COLUMN eigenschap_waarde TO value;

ALTER TABLE diwi.plan_conditie_maatwerk_categorie RENAME TO plan_conditie_property_category;
ALTER TABLE diwi.plan_conditie_property_category
    ADD COLUMN IF NOT EXISTS property_id UUID NOT NULL;
ALTER TABLE ONLY diwi.plan_conditie_property_category
    ADD CONSTRAINT fk_plan_conditie_property_category__property FOREIGN KEY ("property_id") REFERENCES diwi.property("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi.plan_conditie_maatwerk_categorie_value RENAME TO plan_conditie_property_category_value;
ALTER TABLE diwi.plan_conditie_property_category_value
    RENAME COLUMN eigenschap_waarde_id TO property_value_id;
ALTER TABLE diwi.plan_conditie_property_category_value
    RENAME COLUMN plan_conditie_maatwerk_categorie_id TO plan_conditie_property_category_id;

ALTER TABLE diwi.plan_conditie_maatwerk_numeriek RENAME TO plan_conditie_property_numeric;
ALTER TABLE diwi.plan_conditie_property_numeric
    ADD COLUMN IF NOT EXISTS property_id UUID NOT NULL;
ALTER TABLE ONLY diwi.plan_conditie_property_numeric
    ADD CONSTRAINT fk_plan_conditie_property_numeric__property FOREIGN KEY ("property_id") REFERENCES diwi.property("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi.plan_conditie_maatwerk_ordinaal RENAME TO plan_conditie_property_ordinal;
ALTER TABLE diwi.plan_conditie_property_ordinal
    ADD COLUMN IF NOT EXISTS property_id UUID NOT NULL;
ALTER TABLE ONLY diwi.plan_conditie_property_ordinal
    ADD CONSTRAINT fk_plan_conditie_property_ordinal__property FOREIGN KEY ("property_id") REFERENCES diwi.property("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
