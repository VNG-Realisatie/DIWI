--
-- PostgreSQL database dump
--

-- Dumped from database version 12.15 (Ubuntu 12.15-0ubuntu0.20.04.1)
-- Dumped by pg_dump version 14.0

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: diwi_testset; Type: SCHEMA; Schema: -; Owner: vng
--

CREATE SCHEMA diwi_testset;


ALTER SCHEMA diwi_testset OWNER TO vng;

--
-- Name: conditie_type; Type: TYPE; Schema: diwi_testset; Owner: vng
--

CREATE TYPE diwi_testset.conditie_type AS ENUM (
    'plan_conditie',
    'doel_conditie'
);


ALTER TYPE diwi_testset.conditie_type OWNER TO vng;

--
-- Name: confidentiality; Type: TYPE; Schema: diwi_testset; Owner: vng
--

CREATE TYPE diwi_testset.confidentiality AS ENUM (
    'prive',
    'intern_uitvoering',
    'intern_rapportage',
    'extern_rapportage',
    'openbaar'
);


ALTER TYPE diwi_testset.confidentiality OWNER TO vng;

--
-- Name: doel_richting; Type: TYPE; Schema: diwi_testset; Owner: vng
--

CREATE TYPE diwi_testset.doel_richting AS ENUM (
    'minimaal',
    'maximaal'
);


ALTER TYPE diwi_testset.doel_richting OWNER TO vng;

--
-- Name: doel_soort; Type: TYPE; Schema: diwi_testset; Owner: vng
--

CREATE TYPE diwi_testset.doel_soort AS ENUM (
    'aantal',
    'percentage'
);


ALTER TYPE diwi_testset.doel_soort OWNER TO vng;

--
-- Name: doelgroep; Type: TYPE; Schema: diwi_testset; Owner: vng
--

CREATE TYPE diwi_testset.doelgroep AS ENUM (
    'regulier',
    'jongeren',
    'studenten',
    'ouderen',
    'gehandicapten_en_zorg',
    'grote_gezinnen'
);


ALTER TYPE diwi_testset.doelgroep OWNER TO vng;

--
-- Name: eigendom_soort; Type: TYPE; Schema: diwi_testset; Owner: vng
--

CREATE TYPE diwi_testset.eigendom_soort AS ENUM (
    'koopwoning',
    'huurwoning_particuliere_verhuurder',
    'huurwoning_woningcorporatie'
);


ALTER TYPE diwi_testset.eigendom_soort OWNER TO vng;

--
-- Name: fysiek_voorkomen; Type: TYPE; Schema: diwi_testset; Owner: vng
--

CREATE TYPE diwi_testset.fysiek_voorkomen AS ENUM (
    'tussenwoning',
    'hoekwoning',
    'twee_onder_een_kap',
    'vrijstaand',
    'portiekflat',
    'gallerijflat'
);


ALTER TYPE diwi_testset.fysiek_voorkomen OWNER TO vng;

--
-- Name: grondpositie; Type: TYPE; Schema: diwi_testset; Owner: vng
--

CREATE TYPE diwi_testset.grondpositie AS ENUM (
    'formele_toestemming_grondeigenaar',
    'intentie_medewerking_grondeigenaar',
    'geen_toestemming_grondeigenaar'
);


ALTER TYPE diwi_testset.grondpositie OWNER TO vng;

--
-- Name: maatwerk_eigenschap_type; Type: TYPE; Schema: diwi_testset; Owner: vng
--

CREATE TYPE diwi_testset.maatwerk_eigenschap_type AS ENUM (
    'boolean',
    'category',
    'ordinal',
    'numeric'
);


ALTER TYPE diwi_testset.maatwerk_eigenschap_type OWNER TO vng;

--
-- Name: maatwerk_object_soort; Type: TYPE; Schema: diwi_testset; Owner: vng
--

CREATE TYPE diwi_testset.maatwerk_object_soort AS ENUM (
    'project',
    'woningblok'
);


ALTER TYPE diwi_testset.maatwerk_object_soort OWNER TO vng;

--
-- Name: milestone_status; Type: TYPE; Schema: diwi_testset; Owner: vng
--

CREATE TYPE diwi_testset.milestone_status AS ENUM (
    'voorspeld',
    'gepland',
    'gerealiseerd'
);


ALTER TYPE diwi_testset.milestone_status OWNER TO vng;

--
-- Name: mutatie_soort; Type: TYPE; Schema: diwi_testset; Owner: vng
--

CREATE TYPE diwi_testset.mutatie_soort AS ENUM (
    'bouw',
    'sloop',
    'transformatie',
    'splitsing'
);


ALTER TYPE diwi_testset.mutatie_soort OWNER TO vng;

--
-- Name: plan_type; Type: TYPE; Schema: diwi_testset; Owner: vng
--

CREATE TYPE diwi_testset.plan_type AS ENUM (
    'pand_transformatie',
    'transformatiegebied',
    'herstructurering',
    'verdichting',
    'uitbreiding_uitleg',
    'uitbreiding_overig'
);


ALTER TYPE diwi_testset.plan_type OWNER TO vng;

--
-- Name: planologische_planstatus; Type: TYPE; Schema: diwi_testset; Owner: vng
--

CREATE TYPE diwi_testset.planologische_planstatus AS ENUM (
    '1a_onherroepelijk',
    '1b_onherroepelijk_met_uitwerking_nodig',
    '1c_onherroepelijk_met_b&w_nodig',
    '2a_vastgesteld',
    '2b_vastgesteld_met_uitwerking_nodig',
    '2c_vastgesteld_met_b&w_nodig',
    '3_in_voorbereiding',
    '4a_opgenomen_in_visie',
    '4b_niet_opgenomen_in_visie'
);


ALTER TYPE diwi_testset.planologische_planstatus OWNER TO vng;

--
-- Name: project_phase; Type: TYPE; Schema: diwi_testset; Owner: vng
--

CREATE TYPE diwi_testset.project_phase AS ENUM (
    '1_Initiatieffase',
    '2_projectfase',
    '3_vergunningsfase',
    '4_realisatiefase',
    '5_opleveringsfase'
);


ALTER TYPE diwi_testset.project_phase OWNER TO vng;

--
-- Name: software_module; Type: TYPE; Schema: diwi_testset; Owner: vng
--

CREATE TYPE diwi_testset.software_module AS ENUM (
    'admin_panel',
    'beheer_portaal',
    'dashboard_publiek',
    'dashboard_provinciale_planmonitor',
    'dashboard_gemeenteraad',
    'dashboard_interne_uitvoering'
);


ALTER TYPE diwi_testset.software_module OWNER TO vng;

--
-- Name: software_rights; Type: TYPE; Schema: diwi_testset; Owner: vng
--

CREATE TYPE diwi_testset.software_rights AS ENUM (
    'admin',
    'crud_to_own',
    'crud_to_all',
    'view_only'
);


ALTER TYPE diwi_testset.software_rights OWNER TO vng;

--
-- Name: value_type; Type: TYPE; Schema: diwi_testset; Owner: vng
--

CREATE TYPE diwi_testset.value_type AS ENUM (
    'single_value',
    'range'
);


ALTER TYPE diwi_testset.value_type OWNER TO vng;

--
-- Name: woning_type; Type: TYPE; Schema: diwi_testset; Owner: vng
--

CREATE TYPE diwi_testset.woning_type AS ENUM (
    'eengezinswoning',
    'meergezinswoning'
);


ALTER TYPE diwi_testset.woning_type OWNER TO vng;

--
-- Name: set_end_date_now(); Type: FUNCTION; Schema: diwi_testset; Owner: vng
--

CREATE FUNCTION diwi_testset.set_end_date_now() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
	 DECLARE BEGIN
	 IF OLD.change_end_date IS NULL THEN
        OLD.change_end_date = NOW();
        NEW."id" = 10;
        INSERT INTO diwi.project_priorisering_value_state SELECT NEW.*;
	 END If;
	 RETURN OLD;
	END;
	$$;


ALTER FUNCTION diwi_testset.set_end_date_now() OWNER TO vng;

--
-- Name: set_start_date_now(); Type: FUNCTION; Schema: diwi_testset; Owner: vng
--

CREATE FUNCTION diwi_testset.set_start_date_now() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
	 DECLARE BEGIN
	 NEW.change_start_date = NOW();
	 RETURN NEW;
	END;
	$$;


ALTER FUNCTION diwi_testset.set_start_date_now() OWNER TO vng;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: actor; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.actor (
    "id" UUID NOT NULL
);


ALTER TABLE diwi_testset.actor OWNER TO vng;

--
-- Name: actor_state; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.actor_state (
    "id" UUID NOT NULL,
    "actor_id" UUID NOT NULL,
    name text NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    "organization_id" UUID
);


ALTER TABLE diwi_testset.actor_state OWNER TO vng;

--
-- Name: buurt; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.buurt (
    "id" UUID NOT NULL
);


ALTER TABLE diwi_testset.buurt OWNER TO vng;

--
-- Name: buurt_state; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.buurt_state (
    "id" UUID NOT NULL,
    "buurt_id" UUID NOT NULL,
    "wijk_id" UUID NOT NULL,
    waarde_label text NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset.buurt_state OWNER TO vng;

--
-- Name: document; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.document (
    "id" UUID NOT NULL
);


ALTER TABLE diwi_testset.document OWNER TO vng;

--
-- Name: document_soort; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.document_soort (
    "id" UUID NOT NULL
);


ALTER TABLE diwi_testset.document_soort OWNER TO vng;

--
-- Name: document_soort_state; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.document_soort_state (
    "id" UUID NOT NULL,
    "document_soort_id" UUID NOT NULL,
    waarde_label text NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset.document_soort_state OWNER TO vng;

--
-- Name: document_state; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.document_state (
    "id" UUID NOT NULL,
    "document_id" UUID NOT NULL,
    "milestone_id" UUID NOT NULL,
    naam text NOT NULL,
    notitie text,
    file_path text,
    "owner_organization_id" UUID NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    confidentiality_level diwi_testset.confidentiality NOT NULL
);


ALTER TABLE diwi_testset.document_state OWNER TO vng;

--
-- Name: document_state_soort_value; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.document_state_soort_value (
    "id" UUID NOT NULL,
    "document_state_id" UUID NOT NULL,
    "document_soort_id" UUID NOT NULL
);


ALTER TABLE diwi_testset.document_state_soort_value OWNER TO vng;

--
-- Name: gemeente; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.gemeente (
    "id" UUID NOT NULL
);


ALTER TABLE diwi_testset.gemeente OWNER TO vng;

--
-- Name: gemeente_state; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.gemeente_state (
    "id" UUID NOT NULL,
    "gemeente_id" UUID NOT NULL,
    waarde_label text NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset.gemeente_state OWNER TO vng;

--
-- Name: maatwerk_categorie_waarde; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.maatwerk_categorie_waarde (
    "id" UUID NOT NULL
);


ALTER TABLE diwi_testset.maatwerk_categorie_waarde OWNER TO vng;

--
-- Name: maatwerk_categorie_waarde_state; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.maatwerk_categorie_waarde_state (
    "id" UUID NOT NULL,
    "categorie_waarde_id" UUID NOT NULL,
    "maatwerk_eigenschap_id" UUID NOT NULL,
    waarde_label text NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset.maatwerk_categorie_waarde_state OWNER TO vng;

--
-- Name: maatwerk_eigenschap; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.maatwerk_eigenschap (
    "id" UUID NOT NULL
);


ALTER TABLE diwi_testset.maatwerk_eigenschap OWNER TO vng;

--
-- Name: maatwerk_eigenschap_state; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.maatwerk_eigenschap_state (
    "id" UUID NOT NULL,
    eigenschap_naam text NOT NULL,
    eigenschap_type diwi_testset.maatwerk_eigenschap_type NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    "eigenschap_id" UUID NOT NULL,
    eigenschap_object_soort diwi_testset.maatwerk_object_soort NOT NULL
);


ALTER TABLE diwi_testset.maatwerk_eigenschap_state OWNER TO vng;

--
-- Name: maatwerk_ordinaal_waarde; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.maatwerk_ordinaal_waarde (
    "id" UUID NOT NULL
);


ALTER TABLE diwi_testset.maatwerk_ordinaal_waarde OWNER TO vng;

--
-- Name: maatwerk_ordinaal_waarde_state; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.maatwerk_ordinaal_waarde_state (
    "id" UUID NOT NULL,
    "ordinaal_waarde_id" UUID NOT NULL,
    "maatwerk_eigenschap_id" UUID NOT NULL,
    waarde_label text NOT NULL,
    ordinaal_niveau integer NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset.maatwerk_ordinaal_waarde_state OWNER TO vng;

--
-- Name: milestone; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.milestone (
    "id" UUID NOT NULL
);


ALTER TABLE diwi_testset.milestone OWNER TO vng;

--
-- Name: milestone_state; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.milestone_state (
    "id" UUID NOT NULL,
    "milestone_id" UUID NOT NULL,
    date date NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    status diwi_testset.milestone_status NOT NULL,
    omschrijving text
);


ALTER TABLE diwi_testset.milestone_state OWNER TO vng;

--
-- Name: organization; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.organization (
    "id" UUID NOT NULL
);


ALTER TABLE diwi_testset.organization OWNER TO vng;

--
-- Name: organization_state; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.organization_state (
    "id" UUID NOT NULL,
    "organization_id" UUID NOT NULL,
    "parent_organization_id" UUID,
    naam text NOT NULL,
    change_end_date timestamp with time zone,
    change_start_date timestamp with time zone NOT NULL,
    "change_user_id" UUID NOT NULL
);


ALTER TABLE diwi_testset.organization_state OWNER TO vng;

--
-- Name: plan; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.plan (
    "id" UUID NOT NULL
);


ALTER TABLE diwi_testset.plan OWNER TO vng;

--
-- Name: plan_conditie; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.plan_conditie (
    "id" UUID NOT NULL
);


ALTER TABLE diwi_testset.plan_conditie OWNER TO vng;

--
-- Name: plan_conditie_doelgroep; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.plan_conditie_doelgroep (
    "id" UUID NOT NULL,
    "plan_conditie_id" UUID NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset.plan_conditie_doelgroep OWNER TO vng;

--
-- Name: plan_conditie_doelgroep_value; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.plan_conditie_doelgroep_value (
    "id" UUID NOT NULL,
    "plan_conditie_doelgroep_id" UUID NOT NULL,
    doelgroep diwi_testset.doelgroep NOT NULL
);


ALTER TABLE diwi_testset.plan_conditie_doelgroep_value OWNER TO vng;

--
-- Name: plan_conditie_eigendom_en_waarde; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.plan_conditie_eigendom_en_waarde (
    "id" UUID NOT NULL,
    "plan_conditie_id" UUID NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    waarde_value_type diwi_testset.value_type,
    waarde_value integer,
    huurbedrag_value_type diwi_testset.value_type,
    huurbedrag_value integer,
    waarde_value_range int8range,
    huurbedrag_value_range int8range
);


ALTER TABLE diwi_testset.plan_conditie_eigendom_en_waarde OWNER TO vng;

--
-- Name: plan_conditie_eigendom_en_waarde_soort_value; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.plan_conditie_eigendom_en_waarde_soort_value (
    "id" UUID NOT NULL,
    "plan_conditie_eigendom_en_waarde_id" UUID NOT NULL,
    eigendom_soort diwi_testset.eigendom_soort NOT NULL
);


ALTER TABLE diwi_testset.plan_conditie_eigendom_en_waarde_soort_value OWNER TO vng;

--
-- Name: plan_conditie_gemeente_indeling; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.plan_conditie_gemeente_indeling (
    "id" UUID NOT NULL,
    "plan_conditie_id" UUID NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset.plan_conditie_gemeente_indeling OWNER TO vng;

--
-- Name: plan_conditie_gemeente_indeling_buurt_value; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.plan_conditie_gemeente_indeling_buurt_value (
    "id" UUID NOT NULL,
    "plan_conditie_gemeente_indeling_id" UUID NOT NULL,
    "wijk_id" UUID NOT NULL
);


ALTER TABLE diwi_testset.plan_conditie_gemeente_indeling_buurt_value OWNER TO vng;

--
-- Name: plan_conditie_gemeente_indeling_gemeente_value; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.plan_conditie_gemeente_indeling_gemeente_value (
    "id" UUID NOT NULL,
    "plan_conditie_gemeente_indeling_id" UUID NOT NULL,
    "gemeente_id" UUID NOT NULL
);


ALTER TABLE diwi_testset.plan_conditie_gemeente_indeling_gemeente_value OWNER TO vng;

--
-- Name: plan_conditie_gemeente_indeling_wijk_value; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.plan_conditie_gemeente_indeling_wijk_value (
    "id" UUID NOT NULL,
    "plan_conditie_gemeente_indeling_id" UUID NOT NULL,
    "wijk_id" UUID NOT NULL
);


ALTER TABLE diwi_testset.plan_conditie_gemeente_indeling_wijk_value OWNER TO vng;

--
-- Name: plan_conditie_grondpositie; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.plan_conditie_grondpositie (
    "id" UUID NOT NULL,
    "plan_conditie_id" UUID NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset.plan_conditie_grondpositie OWNER TO vng;

--
-- Name: plan_conditie_grondpositie_value; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.plan_conditie_grondpositie_value (
    "id" UUID NOT NULL,
    "plan_conditie_grondpositie_id" UUID NOT NULL,
    grondpositie diwi_testset.grondpositie NOT NULL
);


ALTER TABLE diwi_testset.plan_conditie_grondpositie_value OWNER TO vng;

--
-- Name: plan_conditie_grootte; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.plan_conditie_grootte (
    "id" UUID NOT NULL,
    "plan_conditie_id" UUID NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    value_type diwi_testset.value_type NOT NULL,
    value double precision,
    value_range numrange
);


ALTER TABLE diwi_testset.plan_conditie_grootte OWNER TO vng;

--
-- Name: plan_conditie_kadastraal; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.plan_conditie_kadastraal (
    "id" UUID NOT NULL,
    "plan_conditie_id" UUID NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset.plan_conditie_kadastraal OWNER TO vng;

--
-- Name: plan_conditie_kadastraal_value; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.plan_conditie_kadastraal_value (
    "id" UUID NOT NULL,
    "plan_conditie_kadastraal_id" UUID NOT NULL,
    brk_gemeente_code text NOT NULL,
    brk_sectie text,
    brk_perceelnummer integer,
    brk_selectie text
);


ALTER TABLE diwi_testset.plan_conditie_kadastraal_value OWNER TO vng;

--
-- Name: plan_conditie_maatwerk_boolean; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.plan_conditie_maatwerk_boolean (
    "id" UUID NOT NULL,
    "plan_conditie_id" UUID NOT NULL,
    eigenschap_waarde boolean NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset.plan_conditie_maatwerk_boolean OWNER TO vng;

--
-- Name: plan_conditie_maatwerk_categorie; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.plan_conditie_maatwerk_categorie (
    "id" UUID NOT NULL,
    "plan_conditie_id" UUID NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset.plan_conditie_maatwerk_categorie OWNER TO vng;

--
-- Name: plan_conditie_maatwerk_categorie_value; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.plan_conditie_maatwerk_categorie_value (
    "id" UUID NOT NULL,
    "plan_conditie_maatwerk_categorie_id" UUID NOT NULL,
    "eigenschap_waarde_id" UUID NOT NULL
);


ALTER TABLE diwi_testset.plan_conditie_maatwerk_categorie_value OWNER TO vng;

--
-- Name: plan_conditie_maatwerk_numeriek; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.plan_conditie_maatwerk_numeriek (
    "id" UUID NOT NULL,
    "plan_conditie_id" UUID NOT NULL,
    value double precision,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    value_type diwi_testset.value_type NOT NULL,
    value_range numrange
);


ALTER TABLE diwi_testset.plan_conditie_maatwerk_numeriek OWNER TO vng;

--
-- Name: plan_conditie_maatwerk_ordinaal; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.plan_conditie_maatwerk_ordinaal (
    "id" UUID NOT NULL,
    "plan_conditie_id" UUID NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    value_type diwi_testset.value_type NOT NULL,
    "value_id" UUID,
    "min_value_id" UUID,
    "max_value_id" UUID
);


ALTER TABLE diwi_testset.plan_conditie_maatwerk_ordinaal OWNER TO vng;

--
-- Name: plan_conditie_programmering; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.plan_conditie_programmering (
    "id" UUID NOT NULL,
    "plan_conditie_id" UUID NOT NULL,
    programmering boolean NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset.plan_conditie_programmering OWNER TO vng;

--
-- Name: plan_conditie_state; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.plan_conditie_state (
    "id" UUID NOT NULL,
    "plan_conditie_id" UUID NOT NULL,
    "plan_id" UUID NOT NULL,
    conditie_type diwi_testset.conditie_type NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset.plan_conditie_state OWNER TO vng;

--
-- Name: plan_conditie_type_en_fysiek; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.plan_conditie_type_en_fysiek (
    "id" UUID NOT NULL,
    "plan_conditie_id" UUID NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset.plan_conditie_type_en_fysiek OWNER TO vng;

--
-- Name: plan_conditie_type_en_fysiek_fysiek_value; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.plan_conditie_type_en_fysiek_fysiek_value (
    "id" UUID NOT NULL,
    "plan_conditie_type_en_fysiek_id" UUID NOT NULL,
    fysiek_voorkomen diwi_testset.fysiek_voorkomen NOT NULL
);


ALTER TABLE diwi_testset.plan_conditie_type_en_fysiek_fysiek_value OWNER TO vng;

--
-- Name: plan_conditie_type_en_fysiek_type_value; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.plan_conditie_type_en_fysiek_type_value (
    "id" UUID NOT NULL,
    "plan_conditie_type_en_fysiek_id" UUID NOT NULL,
    woning_type diwi_testset.woning_type NOT NULL
);


ALTER TABLE diwi_testset.plan_conditie_type_en_fysiek_type_value OWNER TO vng;

--
-- Name: plan_soort; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.plan_soort (
    "id" UUID NOT NULL
);


ALTER TABLE diwi_testset.plan_soort OWNER TO vng;

--
-- Name: plan_soort_state; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.plan_soort_state (
    "id" UUID NOT NULL,
    "plan_soort_id" UUID NOT NULL,
    waarde_label text NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset.plan_soort_state OWNER TO vng;

--
-- Name: plan_state; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.plan_state (
    "id" UUID NOT NULL,
    "plan_id" UUID NOT NULL,
    naam text NOT NULL,
    deadline date NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    confidentiality_level diwi_testset.confidentiality NOT NULL,
    "owner_organization_id" UUID NOT NULL,
    "change_user_id" UUID NOT NULL,
    doel_soort diwi_testset.doel_soort NOT NULL,
    doel_richting diwi_testset.doel_richting NOT NULL,
    doel_waarde double precision NOT NULL,
    start_datum date NOT NULL
);


ALTER TABLE diwi_testset.plan_state OWNER TO vng;

--
-- Name: plan_state_soort_value; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.plan_state_soort_value (
    "id" UUID NOT NULL,
    "plan_state_id" UUID NOT NULL,
    "plan_soort_id" UUID NOT NULL
);


ALTER TABLE diwi_testset.plan_state_soort_value OWNER TO vng;

--
-- Name: project; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.project (
    "id" UUID NOT NULL
);


ALTER TABLE diwi_testset.project OWNER TO vng;

--
-- Name: project_actor_rol_changelog; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.project_actor_rol_changelog (
    "id" UUID NOT NULL,
    "start_milestone_id" UUID NOT NULL,
    "end_milestone_id" UUID NOT NULL,
    "project_id" UUID NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    "actor_id" UUID NOT NULL,
    "project_actor_rol_id" UUID NOT NULL
);


ALTER TABLE diwi_testset.project_actor_rol_changelog OWNER TO vng;

--
-- Name: project_actor_rol_value; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.project_actor_rol_value (
    "id" UUID NOT NULL
);


ALTER TABLE diwi_testset.project_actor_rol_value OWNER TO vng;

--
-- Name: project_actor_rol_value_state; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.project_actor_rol_value_state (
    "id" UUID NOT NULL,
    "project_actor_rol_value_id" UUID NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    value_label text NOT NULL
);


ALTER TABLE diwi_testset.project_actor_rol_value_state OWNER TO vng;

--
-- Name: project_duration_changelog; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.project_duration_changelog (
    "id" UUID NOT NULL,
    "start_milestone_id" UUID NOT NULL,
    "end_milestone_id" UUID NOT NULL,
    "project_id" UUID NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset.project_duration_changelog OWNER TO vng;

--
-- Name: project_fase_changelog; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.project_fase_changelog (
    "id" UUID NOT NULL,
    "start_milestone_id" UUID NOT NULL,
    "end_milestone_id" UUID NOT NULL,
    "project_id" UUID NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    project_fase diwi_testset.project_phase NOT NULL
);


ALTER TABLE diwi_testset.project_fase_changelog OWNER TO vng;

--
-- Name: project_filiatie; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.project_filiatie (
    "id" UUID NOT NULL,
    "old_project_id" UUID NOT NULL,
    "new_project_id" UUID NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset.project_filiatie OWNER TO vng;

--
-- Name: project_gemeenterol_changelog; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.project_gemeenterol_changelog (
    "id" UUID NOT NULL,
    "start_milestone_id" UUID NOT NULL,
    "end_milestone_id" UUID NOT NULL,
    "project_id" UUID NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    "project_gemeenterol_value_id" UUID NOT NULL
);


ALTER TABLE diwi_testset.project_gemeenterol_changelog OWNER TO vng;

--
-- Name: project_gemeenterol_value; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.project_gemeenterol_value (
    "id" UUID NOT NULL
);


ALTER TABLE diwi_testset.project_gemeenterol_value OWNER TO vng;

--
-- Name: project_gemeenterol_value_state; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.project_gemeenterol_value_state (
    "id" UUID NOT NULL,
    value_label text NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    "project_gemeenterol_value_id" UUID NOT NULL
);


ALTER TABLE diwi_testset.project_gemeenterol_value_state OWNER TO vng;

--
-- Name: project_maatwerk_boolean_changelog; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.project_maatwerk_boolean_changelog (
    "id" UUID NOT NULL,
    "start_milestone_id" UUID NOT NULL,
    "end_milestone_id" UUID NOT NULL,
    "project_id" UUID NOT NULL,
    value boolean NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    "eigenschap_id" UUID NOT NULL
);


ALTER TABLE diwi_testset.project_maatwerk_boolean_changelog OWNER TO vng;

--
-- Name: project_maatwerk_categorie_changelog; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.project_maatwerk_categorie_changelog (
    "id" UUID NOT NULL,
    "start_milestone_id" UUID NOT NULL,
    "end_milestone_id" UUID NOT NULL,
    "project_id" UUID NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset.project_maatwerk_categorie_changelog OWNER TO vng;

--
-- Name: project_maatwerk_categorie_changelog_value; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.project_maatwerk_categorie_changelog_value (
    "id" UUID NOT NULL,
    "project_maatwerk_categorie_changelog_id" UUID NOT NULL,
    "eigenschap_waarde_id" UUID NOT NULL
);


ALTER TABLE diwi_testset.project_maatwerk_categorie_changelog_value OWNER TO vng;

--
-- Name: project_maatwerk_numeriek_changelog; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.project_maatwerk_numeriek_changelog (
    "id" UUID NOT NULL,
    "start_milestone_id" UUID NOT NULL,
    "end_milestone_id" UUID NOT NULL,
    "project_id" UUID NOT NULL,
    value double precision,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    "eigenschap_id" UUID NOT NULL,
    value_type diwi_testset.value_type NOT NULL,
    value_range numrange
);


ALTER TABLE diwi_testset.project_maatwerk_numeriek_changelog OWNER TO vng;

--
-- Name: project_maatwerk_ordinaal_changelog; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.project_maatwerk_ordinaal_changelog (
    "id" UUID NOT NULL,
    "start_milestone_id" UUID NOT NULL,
    "end_milestone_id" UUID NOT NULL,
    "project_id" UUID NOT NULL,
    "value_id" UUID,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    value_type diwi_testset.value_type NOT NULL,
    "min_value_id" UUID,
    "max_value_id" UUID
);


ALTER TABLE diwi_testset.project_maatwerk_ordinaal_changelog OWNER TO vng;

--
-- Name: project_name_changelog; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.project_name_changelog (
    "id" UUID NOT NULL,
    "start_milestone_id" UUID NOT NULL,
    "end_milestone_id" UUID NOT NULL,
    "project_id" UUID NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    name text NOT NULL
);


ALTER TABLE diwi_testset.project_name_changelog OWNER TO vng;

--
-- Name: project_plan_type_changelog; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.project_plan_type_changelog (
    "id" UUID NOT NULL,
    "start_milestone_id" UUID NOT NULL,
    "end_milestone_id" UUID NOT NULL,
    "project_id" UUID NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset.project_plan_type_changelog OWNER TO vng;

--
-- Name: project_plan_type_changelog_value; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.project_plan_type_changelog_value (
    "id" UUID NOT NULL,
    "changelog_id" UUID NOT NULL,
    plan_type diwi_testset.plan_type NOT NULL
);


ALTER TABLE diwi_testset.project_plan_type_changelog_value OWNER TO vng;

--
-- Name: project_planologische_planstatus_changelog; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.project_planologische_planstatus_changelog (
    "id" UUID NOT NULL,
    "start_milestone_id" UUID NOT NULL,
    "end_milestone_id" UUID NOT NULL,
    "project_id" UUID NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset.project_planologische_planstatus_changelog OWNER TO vng;

--
-- Name: project_planologische_planstatus_changelog_value; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.project_planologische_planstatus_changelog_value (
    "id" UUID NOT NULL,
    "planologische_planstatus_changelog_id" UUID NOT NULL,
    planologische_planstatus diwi_testset.planologische_planstatus NOT NULL
);


ALTER TABLE diwi_testset.project_planologische_planstatus_changelog_value OWNER TO vng;

--
-- Name: project_priorisering_changelog; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.project_priorisering_changelog (
    "id" UUID NOT NULL,
    "start_milestone_id" UUID NOT NULL,
    "end_milestone_id" UUID NOT NULL,
    "project_id" UUID NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    "project_priorisering_value_id" UUID,
    value_type diwi_testset.value_type NOT NULL,
    "project_priorisering_min_value_id" UUID,
    "project_priorisering_max_value_id" UUID
);


ALTER TABLE diwi_testset.project_priorisering_changelog OWNER TO vng;

--
-- Name: project_priorisering_value; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.project_priorisering_value (
    "id" UUID NOT NULL
);


ALTER TABLE diwi_testset.project_priorisering_value OWNER TO vng;

--
-- Name: project_priorisering_value_state; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.project_priorisering_value_state (
    "id" UUID NOT NULL,
    value_label text NOT NULL,
    ordinal_level integer NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    "project_priorisering_value_id" UUID NOT NULL
);


ALTER TABLE diwi_testset.project_priorisering_value_state OWNER TO vng;

--
-- Name: project_state; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.project_state (
    "id" UUID NOT NULL,
    "project_id" UUID NOT NULL,
    "owner_organization_id" UUID NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    confidentiality_level diwi_testset.confidentiality NOT NULL,
    project_colour text NOT NULL
);


ALTER TABLE diwi_testset.project_state OWNER TO vng;

--
-- Name: software_module_rights; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.software_module_rights (
    "id" UUID NOT NULL,
    "organization_id" UUID NOT NULL,
    software_module diwi_testset.software_module NOT NULL,
    confidentiality_level diwi_testset.confidentiality NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    software_rights diwi_testset.software_rights NOT NULL
);


ALTER TABLE diwi_testset.software_module_rights OWNER TO vng;

--
-- Name: user; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset."user" (
    "id" UUID NOT NULL
);


ALTER TABLE diwi_testset."user" OWNER TO vng;

--
-- Name: user_state; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.user_state (
    "id" UUID NOT NULL,
    "user_id" UUID NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    "identity_provider_id" text NOT NULL
);


ALTER TABLE diwi_testset.user_state OWNER TO vng;

--
-- Name: user_to_organization; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.user_to_organization (
    "id" UUID NOT NULL,
    "user_id" UUID NOT NULL,
    "organization_id" UUID NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset.user_to_organization OWNER TO vng;

--
-- Name: wijk; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.wijk (
    "id" UUID NOT NULL
);


ALTER TABLE diwi_testset.wijk OWNER TO vng;

--
-- Name: wijk_state; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.wijk_state (
    "id" UUID NOT NULL,
    "wijk_id" UUID NOT NULL,
    "gemeente_id" UUID NOT NULL,
    waarde_label text NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset.wijk_state OWNER TO vng;

--
-- Name: woningblok; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.woningblok (
    "id" UUID NOT NULL
);


ALTER TABLE diwi_testset.woningblok OWNER TO vng;

--
-- Name: woningblok_doelgroep_changelog; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.woningblok_doelgroep_changelog (
    "id" UUID NOT NULL,
    "woningblok_id" UUID NOT NULL,
    "start_milestone_id" UUID NOT NULL,
    "end_milestone_id" UUID NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset.woningblok_doelgroep_changelog OWNER TO vng;

--
-- Name: woningblok_doelgroep_changelog_value; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.woningblok_doelgroep_changelog_value (
    "id" UUID NOT NULL,
    "woningblok_doelgroep_changelog_id" UUID NOT NULL,
    doelgroep diwi_testset.doelgroep NOT NULL
);


ALTER TABLE diwi_testset.woningblok_doelgroep_changelog_value OWNER TO vng;

--
-- Name: woningblok_duration_changelog; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.woningblok_duration_changelog (
    "id" UUID NOT NULL,
    "woningblok_id" UUID NOT NULL,
    "start_milestone_id" UUID NOT NULL,
    "end_milestone_id" UUID NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset.woningblok_duration_changelog OWNER TO vng;

--
-- Name: woningblok_eigendom_en_waarde_changelog; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.woningblok_eigendom_en_waarde_changelog (
    "id" UUID NOT NULL,
    "woningblok_id" UUID NOT NULL,
    "start_milestone_id" UUID NOT NULL,
    "end_milestone_id" UUID NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    waarde_value_type diwi_testset.value_type,
    waarde_value integer,
    huurbedrag_value_type diwi_testset.value_type,
    huurbedrag_value integer,
    waarde_value_range int8range,
    huurbedrag_value_range int8range
);


ALTER TABLE diwi_testset.woningblok_eigendom_en_waarde_changelog OWNER TO vng;

--
-- Name: woningblok_eigendom_en_waarde_changelog_soort_value; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.woningblok_eigendom_en_waarde_changelog_soort_value (
    "id" UUID NOT NULL,
    "woningblok_eigendom_en_waarde_changelog_id" UUID NOT NULL,
    eigendom_soort diwi_testset.eigendom_soort NOT NULL
);


ALTER TABLE diwi_testset.woningblok_eigendom_en_waarde_changelog_soort_value OWNER TO vng;

--
-- Name: woningblok_gemeente_indeling_changelog; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.woningblok_gemeente_indeling_changelog (
    "id" UUID NOT NULL,
    "woningblok_id" UUID NOT NULL,
    "start_milestone_id" UUID NOT NULL,
    "end_milestone_id" UUID NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset.woningblok_gemeente_indeling_changelog OWNER TO vng;

--
-- Name: woningblok_gemeente_indeling_changelog_buurt; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.woningblok_gemeente_indeling_changelog_buurt (
    "id" UUID NOT NULL,
    "woningblok_gemeente_indeling_changelog_id" UUID NOT NULL,
    "buurt_id" UUID NOT NULL
);


ALTER TABLE diwi_testset.woningblok_gemeente_indeling_changelog_buurt OWNER TO vng;

--
-- Name: woningblok_gemeente_indeling_changelog_gemeente; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.woningblok_gemeente_indeling_changelog_gemeente (
    "id" UUID NOT NULL,
    "woningblok_gemeente_indeling_changelog_id" UUID NOT NULL,
    "gemeente_id" UUID NOT NULL
);


ALTER TABLE diwi_testset.woningblok_gemeente_indeling_changelog_gemeente OWNER TO vng;

--
-- Name: woningblok_gemeente_indeling_changelog_wijk; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.woningblok_gemeente_indeling_changelog_wijk (
    "id" UUID NOT NULL,
    "woningblok_gemeente_indeling_changelog_id" UUID NOT NULL,
    "wijk_id" UUID NOT NULL
);


ALTER TABLE diwi_testset.woningblok_gemeente_indeling_changelog_wijk OWNER TO vng;

--
-- Name: woningblok_grondpositie_changelog; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.woningblok_grondpositie_changelog (
    "id" UUID NOT NULL,
    "woningblok_id" UUID NOT NULL,
    "start_milestone_id" UUID NOT NULL,
    "end_milestone_id" UUID NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset.woningblok_grondpositie_changelog OWNER TO vng;

--
-- Name: woningblok_grondpositie_changelog_value; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.woningblok_grondpositie_changelog_value (
    "id" UUID NOT NULL,
    "woningblok_grondpositie_changelog_id" UUID NOT NULL,
    grondpositie diwi_testset.grondpositie NOT NULL
);


ALTER TABLE diwi_testset.woningblok_grondpositie_changelog_value OWNER TO vng;

--
-- Name: woningblok_grootte_changelog; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.woningblok_grootte_changelog (
    "id" UUID NOT NULL,
    "woningblok_id" UUID NOT NULL,
    "start_milestone_id" UUID NOT NULL,
    "end_milestone_id" UUID NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    value double precision,
    value_range numrange,
    value_type diwi_testset.value_type NOT NULL
);


ALTER TABLE diwi_testset.woningblok_grootte_changelog OWNER TO vng;

--
-- Name: woningblok_kadastrale_koppeling_changelog; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.woningblok_kadastrale_koppeling_changelog (
    "id" UUID NOT NULL,
    "woningblok_id" UUID NOT NULL,
    "start_milestone_id" UUID NOT NULL,
    "end_milestone_id" UUID NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    brk_gemeente_code text NOT NULL,
    brk_sectie text,
    brk_perceelnummer integer
);


ALTER TABLE diwi_testset.woningblok_kadastrale_koppeling_changelog OWNER TO vng;

--
-- Name: woningblok_kadastrale_koppeling_changelog_value; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.woningblok_kadastrale_koppeling_changelog_value (
    "id" UUID NOT NULL,
    "woningblok_kadastrale_koppeling_changelog_id" UUID NOT NULL,
    brk_gemeente_code text NOT NULL,
    brk_sectie text,
    brk_perceelnummer integer,
    brk_selectie text
);


ALTER TABLE diwi_testset.woningblok_kadastrale_koppeling_changelog_value OWNER TO vng;

--
-- Name: woningblok_maatwerk_boolean_changelog; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.woningblok_maatwerk_boolean_changelog (
    "id" UUID NOT NULL,
    "start_milestone_id" UUID NOT NULL,
    "end_milestone_id" UUID NOT NULL,
    "woningblok_id" UUID NOT NULL,
    value boolean NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    "eigenschap_id" UUID NOT NULL
);


ALTER TABLE diwi_testset.woningblok_maatwerk_boolean_changelog OWNER TO vng;

--
-- Name: woningblok_maatwerk_categorie_changelog; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.woningblok_maatwerk_categorie_changelog (
    "id" UUID NOT NULL,
    "start_milestone_id" UUID NOT NULL,
    "end_milestone_id" UUID NOT NULL,
    "woningblok_id" UUID NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset.woningblok_maatwerk_categorie_changelog OWNER TO vng;

--
-- Name: woningblok_maatwerk_categorie_changelog_value; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.woningblok_maatwerk_categorie_changelog_value (
    "id" UUID NOT NULL,
    "woningblok_maatwerk_categorie_changelog_id" UUID NOT NULL,
    "eigenschap_waarde_id" UUID NOT NULL
);


ALTER TABLE diwi_testset.woningblok_maatwerk_categorie_changelog_value OWNER TO vng;

--
-- Name: woningblok_maatwerk_numeriek_changelog; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.woningblok_maatwerk_numeriek_changelog (
    "id" UUID NOT NULL,
    "start_milestone_id" UUID NOT NULL,
    "end_milestone_id" UUID NOT NULL,
    "woningblok_id" UUID NOT NULL,
    value double precision,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    "eigenschap_id" UUID NOT NULL,
    value_type diwi_testset.value_type NOT NULL,
    value_range numrange
);


ALTER TABLE diwi_testset.woningblok_maatwerk_numeriek_changelog OWNER TO vng;

--
-- Name: woningblok_maatwerk_ordinaal_changelog; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.woningblok_maatwerk_ordinaal_changelog (
    "id" UUID NOT NULL,
    "start_milestone_id" UUID NOT NULL,
    "end_milestone_id" UUID NOT NULL,
    "woningblok_id" UUID NOT NULL,
    "value_id" UUID,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    value_type diwi_testset.value_type NOT NULL,
    "min_value_id" UUID,
    "max_value_id" UUID
);


ALTER TABLE diwi_testset.woningblok_maatwerk_ordinaal_changelog OWNER TO vng;

--
-- Name: woningblok_mutatie_changelog; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.woningblok_mutatie_changelog (
    "id" UUID NOT NULL,
    "woningblok_id" UUID NOT NULL,
    "start_milestone_id" UUID NOT NULL,
    "end_milestone_id" UUID NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    bruto_plancapaciteit integer,
    sloop integer,
    netto_plancapaciteit integer
);


ALTER TABLE diwi_testset.woningblok_mutatie_changelog OWNER TO vng;

--
-- Name: woningblok_mutatie_changelog_soort_value; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.woningblok_mutatie_changelog_soort_value (
    "id" UUID NOT NULL,
    "woningblok_mutatie_changelog_id" UUID NOT NULL,
    mutatie_soort diwi_testset.mutatie_soort NOT NULL
);


ALTER TABLE diwi_testset.woningblok_mutatie_changelog_soort_value OWNER TO vng;

--
-- Name: woningblok_naam_changelog; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.woningblok_naam_changelog (
    "id" UUID NOT NULL,
    "woningblok_id" UUID NOT NULL,
    "start_milestone_id" UUID NOT NULL,
    "end_milestone_id" UUID NOT NULL,
    naam text NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset.woningblok_naam_changelog OWNER TO vng;

--
-- Name: woningblok_programmering_changelog; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.woningblok_programmering_changelog (
    "id" UUID NOT NULL,
    "woningblok_id" UUID NOT NULL,
    "start_milestone_id" UUID NOT NULL,
    "end_milestone_id" UUID NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    programmering boolean NOT NULL
);


ALTER TABLE diwi_testset.woningblok_programmering_changelog OWNER TO vng;

--
-- Name: woningblok_state; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.woningblok_state (
    "id" UUID NOT NULL,
    "woningblok_id" UUID NOT NULL,
    "project_id" UUID NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset.woningblok_state OWNER TO vng;

--
-- Name: woningblok_type_en_fysiek_changelog; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.woningblok_type_en_fysiek_changelog (
    "id" UUID NOT NULL,
    "woningblok_id" UUID NOT NULL,
    "start_milestone_id" UUID NOT NULL,
    "end_milestone_id" UUID NOT NULL,
    "change_user_id" UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset.woningblok_type_en_fysiek_changelog OWNER TO vng;

--
-- Name: woningblok_type_en_fysiek_changelog_fysiek_value; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.woningblok_type_en_fysiek_changelog_fysiek_value (
    "id" UUID NOT NULL,
    "woningblok_type_en_fysiek_voorkomen_changelog_id" UUID NOT NULL,
    fysiek_voorkomen diwi_testset.fysiek_voorkomen NOT NULL
);


ALTER TABLE diwi_testset.woningblok_type_en_fysiek_changelog_fysiek_value OWNER TO vng;

--
-- Name: woningblok_type_en_fysiek_changelog_type_value; Type: TABLE; Schema: diwi_testset; Owner: vng
--

CREATE TABLE diwi_testset.woningblok_type_en_fysiek_changelog_type_value (
    "id" UUID NOT NULL,
    "woningblok_type_en_fysiek_voorkomen_changelog_id" UUID NOT NULL,
    woning_type diwi_testset.woning_type NOT NULL
);


ALTER TABLE diwi_testset.woningblok_type_en_fysiek_changelog_type_value OWNER TO vng;

--
-- Name: gemeente Gemeente_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.gemeente
    ADD CONSTRAINT "Gemeente_pkey" PRIMARY KEY ("id");


--
-- Name: buurt buurt_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.buurt
    ADD CONSTRAINT buurt_pkey PRIMARY KEY ("id");


--
-- Name: buurt_state buurt_state_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.buurt_state
    ADD CONSTRAINT buurt_state_pkey PRIMARY KEY ("id");


--
-- Name: document document_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.document
    ADD CONSTRAINT document_pkey PRIMARY KEY ("id");


--
-- Name: document_soort document_soort_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.document_soort
    ADD CONSTRAINT document_soort_pkey PRIMARY KEY ("id");


--
-- Name: document_soort_state document_soort_state_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.document_soort_state
    ADD CONSTRAINT document_soort_state_pkey PRIMARY KEY ("id");


--
-- Name: document_state document_state_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.document_state
    ADD CONSTRAINT document_state_pkey PRIMARY KEY ("id");


--
-- Name: document_state_soort_value document_state_soort_value_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.document_state_soort_value
    ADD CONSTRAINT document_state_soort_value_pkey PRIMARY KEY ("id");


--
-- Name: actor externe_partij_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.actor
    ADD CONSTRAINT externe_partij_pkey PRIMARY KEY ("id");


--
-- Name: actor_state externe_partij_state_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.actor_state
    ADD CONSTRAINT externe_partij_state_pkey PRIMARY KEY ("id");


--
-- Name: gemeente_state gemeente_state_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.gemeente_state
    ADD CONSTRAINT gemeente_state_pkey PRIMARY KEY ("id");


--
-- Name: milestone milestone_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.milestone
    ADD CONSTRAINT milestone_pkey PRIMARY KEY ("id");


--
-- Name: milestone_state milestone_state_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.milestone_state
    ADD CONSTRAINT milestone_state_pkey PRIMARY KEY ("id");


--
-- Name: organization organization_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.organization
    ADD CONSTRAINT organization_pkey PRIMARY KEY ("id");


--
-- Name: organization_state organization_state_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.organization_state
    ADD CONSTRAINT organization_state_pkey PRIMARY KEY ("id");


--
-- Name: plan_conditie_maatwerk_categorie plan_conditie_categorie_maatwerk_eigenschap_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_maatwerk_categorie
    ADD CONSTRAINT plan_conditie_categorie_maatwerk_eigenschap_pkey PRIMARY KEY ("id");


--
-- Name: plan_conditie_doelgroep plan_conditie_doelgroep_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_doelgroep
    ADD CONSTRAINT plan_conditie_doelgroep_pkey PRIMARY KEY ("id");


--
-- Name: plan_conditie_doelgroep_value plan_conditie_doelgroep_value_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_doelgroep_value
    ADD CONSTRAINT plan_conditie_doelgroep_value_pkey PRIMARY KEY ("id");


--
-- Name: plan_conditie_eigendom_en_waarde plan_conditie_eigendom_en_waarde_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_eigendom_en_waarde
    ADD CONSTRAINT plan_conditie_eigendom_en_waarde_pkey PRIMARY KEY ("id");


--
-- Name: plan_conditie_eigendom_en_waarde_soort_value plan_conditie_eigendom_en_waarde_soort_value_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_eigendom_en_waarde_soort_value
    ADD CONSTRAINT plan_conditie_eigendom_en_waarde_soort_value_pkey PRIMARY KEY ("id");


--
-- Name: plan_conditie_gemeente_indeling_buurt_value plan_conditie_gemeente_indeling_buurt_value_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_gemeente_indeling_buurt_value
    ADD CONSTRAINT plan_conditie_gemeente_indeling_buurt_value_pkey PRIMARY KEY ("id");


--
-- Name: plan_conditie_gemeente_indeling_gemeente_value plan_conditie_gemeente_indeling_gemeente_value_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_gemeente_indeling_gemeente_value
    ADD CONSTRAINT plan_conditie_gemeente_indeling_gemeente_value_pkey PRIMARY KEY ("id");


--
-- Name: plan_conditie_gemeente_indeling plan_conditie_gemeente_indeling_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_gemeente_indeling
    ADD CONSTRAINT plan_conditie_gemeente_indeling_pkey PRIMARY KEY ("id");


--
-- Name: plan_conditie_gemeente_indeling_wijk_value plan_conditie_gemeente_indeling_wijk_value_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_gemeente_indeling_wijk_value
    ADD CONSTRAINT plan_conditie_gemeente_indeling_wijk_value_pkey PRIMARY KEY ("id");


--
-- Name: plan_conditie_kadastraal plan_conditie_geografie_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_kadastraal
    ADD CONSTRAINT plan_conditie_geografie_pkey PRIMARY KEY ("id");


--
-- Name: plan_conditie_grondpositie plan_conditie_grondpositie_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_grondpositie
    ADD CONSTRAINT plan_conditie_grondpositie_pkey PRIMARY KEY ("id");


--
-- Name: plan_conditie_grondpositie_value plan_conditie_grondpositie_value_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_grondpositie_value
    ADD CONSTRAINT plan_conditie_grondpositie_value_pkey PRIMARY KEY ("id");


--
-- Name: plan_conditie_grootte plan_conditie_grootte_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_grootte
    ADD CONSTRAINT plan_conditie_grootte_pkey PRIMARY KEY ("id");


--
-- Name: plan_conditie_kadastraal_value plan_conditie_kadastraal_value_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_kadastraal_value
    ADD CONSTRAINT plan_conditie_kadastraal_value_pkey PRIMARY KEY ("id");


--
-- Name: plan_conditie_maatwerk_boolean plan_conditie_maatwerk_boolean_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_maatwerk_boolean
    ADD CONSTRAINT plan_conditie_maatwerk_boolean_pkey PRIMARY KEY ("id");


--
-- Name: plan_conditie_maatwerk_categorie_value plan_conditie_maatwerk_categorie_value_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_maatwerk_categorie_value
    ADD CONSTRAINT plan_conditie_maatwerk_categorie_value_pkey PRIMARY KEY ("id");


--
-- Name: plan_conditie_maatwerk_numeriek plan_conditie_maatwerk_numeriek_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_maatwerk_numeriek
    ADD CONSTRAINT plan_conditie_maatwerk_numeriek_pkey PRIMARY KEY ("id");


--
-- Name: plan_conditie_maatwerk_ordinaal plan_conditie_maatwerk_ordinaal_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_maatwerk_ordinaal
    ADD CONSTRAINT plan_conditie_maatwerk_ordinaal_pkey PRIMARY KEY ("id");


--
-- Name: plan_conditie plan_conditie_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie
    ADD CONSTRAINT plan_conditie_pkey PRIMARY KEY ("id");


--
-- Name: plan_conditie_programmering plan_conditie_programmering_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_programmering
    ADD CONSTRAINT plan_conditie_programmering_pkey PRIMARY KEY ("id");


--
-- Name: plan_conditie_state plan_conditie_state_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_state
    ADD CONSTRAINT plan_conditie_state_pkey PRIMARY KEY ("id");


--
-- Name: plan_conditie_type_en_fysiek_fysiek_value plan_conditie_type_en_fysiek_fysiek_value_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_type_en_fysiek_fysiek_value
    ADD CONSTRAINT plan_conditie_type_en_fysiek_fysiek_value_pkey PRIMARY KEY ("id");


--
-- Name: plan_conditie_type_en_fysiek_type_value plan_conditie_type_en_fysiek_type_value_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_type_en_fysiek_type_value
    ADD CONSTRAINT plan_conditie_type_en_fysiek_type_value_pkey PRIMARY KEY ("id");


--
-- Name: plan_conditie_type_en_fysiek plan_conditie_type_en_fysiek_voorkomen_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_type_en_fysiek
    ADD CONSTRAINT plan_conditie_type_en_fysiek_voorkomen_pkey PRIMARY KEY ("id");


--
-- Name: plan plan_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan
    ADD CONSTRAINT plan_pkey PRIMARY KEY ("id");


--
-- Name: plan_soort plan_soort_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_soort
    ADD CONSTRAINT plan_soort_pkey PRIMARY KEY ("id");


--
-- Name: plan_soort_state plan_soort_state_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_soort_state
    ADD CONSTRAINT plan_soort_state_pkey PRIMARY KEY ("id");


--
-- Name: plan_state plan_state_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_state
    ADD CONSTRAINT plan_state_pkey PRIMARY KEY ("id");


--
-- Name: plan_state_soort_value plan_state_soort_value_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_state_soort_value
    ADD CONSTRAINT plan_state_soort_value_pkey PRIMARY KEY ("id");


--
-- Name: project_duration_changelog project_duration_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_duration_changelog
    ADD CONSTRAINT project_duration_changelog_pkey PRIMARY KEY ("id");


--
-- Name: project_actor_rol_changelog project_externe_partij_rol_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_actor_rol_changelog
    ADD CONSTRAINT project_externe_partij_rol_changelog_pkey PRIMARY KEY ("id");


--
-- Name: project_actor_rol_value project_externe_rol_value_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_actor_rol_value
    ADD CONSTRAINT project_externe_rol_value_pkey PRIMARY KEY ("id");


--
-- Name: project_actor_rol_value_state project_externe_rol_value_state_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_actor_rol_value_state
    ADD CONSTRAINT project_externe_rol_value_state_pkey PRIMARY KEY ("id");


--
-- Name: project_fase_changelog project_fase_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_fase_changelog
    ADD CONSTRAINT project_fase_changelog_pkey PRIMARY KEY ("id");


--
-- Name: project_filiatie project_filiatie_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_filiatie
    ADD CONSTRAINT project_filiatie_pkey PRIMARY KEY ("id");


--
-- Name: project_gemeenterol_changelog project_gemeenterol_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_gemeenterol_changelog
    ADD CONSTRAINT project_gemeenterol_changelog_pkey PRIMARY KEY ("id");


--
-- Name: project_gemeenterol_value project_gemeenterol_value_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_gemeenterol_value
    ADD CONSTRAINT project_gemeenterol_value_pkey PRIMARY KEY ("id");


--
-- Name: project_gemeenterol_value_state project_gemeenterol_value_state_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_gemeenterol_value_state
    ADD CONSTRAINT project_gemeenterol_value_state_pkey PRIMARY KEY ("id");


--
-- Name: project_maatwerk_boolean_changelog project_maatwerk_boolean_eigenschap_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_maatwerk_boolean_changelog
    ADD CONSTRAINT project_maatwerk_boolean_eigenschap_changelog_pkey PRIMARY KEY ("id");


--
-- Name: project_maatwerk_categorie_changelog project_maatwerk_categorie_eigenschap_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_maatwerk_categorie_changelog
    ADD CONSTRAINT project_maatwerk_categorie_eigenschap_changelog_pkey PRIMARY KEY ("id");


--
-- Name: project_maatwerk_categorie_changelog_value project_maatwerk_categorie_eigenschap_changelog_value_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_maatwerk_categorie_changelog_value
    ADD CONSTRAINT project_maatwerk_categorie_eigenschap_changelog_value_pkey PRIMARY KEY ("id");


--
-- Name: project_maatwerk_numeriek_changelog project_maatwerk_numeriek_eigenschap_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_maatwerk_numeriek_changelog
    ADD CONSTRAINT project_maatwerk_numeriek_eigenschap_changelog_pkey PRIMARY KEY ("id");


--
-- Name: project_maatwerk_ordinaal_changelog project_maatwerk_ordinaal_eigenschap_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_maatwerk_ordinaal_changelog
    ADD CONSTRAINT project_maatwerk_ordinaal_eigenschap_changelog_pkey PRIMARY KEY ("id");


--
-- Name: project_name_changelog project_name_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_name_changelog
    ADD CONSTRAINT project_name_changelog_pkey PRIMARY KEY ("id");


--
-- Name: project project_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project
    ADD CONSTRAINT project_pkey PRIMARY KEY ("id");


--
-- Name: project_plan_type_changelog project_plan_type_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_plan_type_changelog
    ADD CONSTRAINT project_plan_type_changelog_pkey PRIMARY KEY ("id");


--
-- Name: project_plan_type_changelog_value project_plan_type_changelog_value_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_plan_type_changelog_value
    ADD CONSTRAINT project_plan_type_changelog_value_pkey PRIMARY KEY ("id");


--
-- Name: project_planologische_planstatus_changelog project_planologische_planstatus_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_planologische_planstatus_changelog
    ADD CONSTRAINT project_planologische_planstatus_changelog_pkey PRIMARY KEY ("id");


--
-- Name: project_planologische_planstatus_changelog_value project_planologische_planstatus_changelog_value_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_planologische_planstatus_changelog_value
    ADD CONSTRAINT project_planologische_planstatus_changelog_value_pkey PRIMARY KEY ("id");


--
-- Name: project_priorisering_changelog project_priorisering_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_priorisering_changelog
    ADD CONSTRAINT project_priorisering_changelog_pkey PRIMARY KEY ("id");


--
-- Name: project_priorisering_value project_priorisering_value_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_priorisering_value
    ADD CONSTRAINT project_priorisering_value_pkey PRIMARY KEY ("id");


--
-- Name: project_priorisering_value_state project_priorisering_values_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_priorisering_value_state
    ADD CONSTRAINT project_priorisering_values_pkey PRIMARY KEY ("id");


--
-- Name: project_state project_state_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_state
    ADD CONSTRAINT project_state_pkey PRIMARY KEY ("id");


--
-- Name: software_module_rights software_module_rights_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.software_module_rights
    ADD CONSTRAINT software_module_rights_pkey PRIMARY KEY ("id");


--
-- Name: milestone unique_milestone_id; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.milestone
    ADD CONSTRAINT unique_milestone_id UNIQUE ("id");


--
-- Name: milestone_state unique_milestone_state_id; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.milestone_state
    ADD CONSTRAINT "unique_milestone_state_id" UNIQUE ("id");


--
-- Name: user unique_user_id; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset."user"
    ADD CONSTRAINT "unique_user_id" UNIQUE ("id");


--
-- Name: user_state unique_user_state_id; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.user_state
    ADD CONSTRAINT "unique_user_state_id" UNIQUE ("id");


--
-- Name: user user_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset."user"
    ADD CONSTRAINT user_pkey PRIMARY KEY ("id");


--
-- Name: user_state user_state_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.user_state
    ADD CONSTRAINT user_state_pkey PRIMARY KEY ("id");


--
-- Name: user_to_organization user_to_organization_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.user_to_organization
    ADD CONSTRAINT user_to_organization_pkey PRIMARY KEY ("id");


--
-- Name: wijk wijk_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.wijk
    ADD CONSTRAINT wijk_pkey PRIMARY KEY ("id");


--
-- Name: wijk_state wijk_state_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.wijk_state
    ADD CONSTRAINT wijk_state_pkey PRIMARY KEY ("id");


--
-- Name: woningblok_doelgroep_changelog woningblok_doelgroep_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_doelgroep_changelog
    ADD CONSTRAINT woningblok_doelgroep_changelog_pkey PRIMARY KEY ("id");


--
-- Name: woningblok_doelgroep_changelog_value woningblok_doelgroep_changelog_value_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_doelgroep_changelog_value
    ADD CONSTRAINT woningblok_doelgroep_changelog_value_pkey PRIMARY KEY ("id");


--
-- Name: woningblok_duration_changelog woningblok_duration_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_duration_changelog
    ADD CONSTRAINT woningblok_duration_changelog_pkey PRIMARY KEY ("id");


--
-- Name: woningblok_eigendom_en_waarde_changelog woningblok_eigendom_en_waarde_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_eigendom_en_waarde_changelog
    ADD CONSTRAINT woningblok_eigendom_en_waarde_changelog_pkey PRIMARY KEY ("id");


--
-- Name: woningblok_eigendom_en_waarde_changelog_soort_value woningblok_eigendom_en_waarde_changelog_soort_value_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_eigendom_en_waarde_changelog_soort_value
    ADD CONSTRAINT woningblok_eigendom_en_waarde_changelog_soort_value_pkey PRIMARY KEY ("id");


--
-- Name: woningblok_gemeente_indeling_changelog_buurt woningblok_gemeentelijke_indeling_changelog_buurt_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_gemeente_indeling_changelog_buurt
    ADD CONSTRAINT woningblok_gemeentelijke_indeling_changelog_buurt_pkey PRIMARY KEY ("id");


--
-- Name: woningblok_gemeente_indeling_changelog_gemeente woningblok_gemeentelijke_indeling_changelog_gemeente_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_gemeente_indeling_changelog_gemeente
    ADD CONSTRAINT woningblok_gemeentelijke_indeling_changelog_gemeente_pkey PRIMARY KEY ("id");


--
-- Name: woningblok_gemeente_indeling_changelog_wijk woningblok_gemeentelijke_indeling_changelog_wijk_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_gemeente_indeling_changelog_wijk
    ADD CONSTRAINT woningblok_gemeentelijke_indeling_changelog_wijk_pkey PRIMARY KEY ("id");


--
-- Name: woningblok_grondpositie_changelog woningblok_grondpositie_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_grondpositie_changelog
    ADD CONSTRAINT woningblok_grondpositie_changelog_pkey PRIMARY KEY ("id");


--
-- Name: woningblok_grondpositie_changelog_value woningblok_grondpositie_changelog_value_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_grondpositie_changelog_value
    ADD CONSTRAINT woningblok_grondpositie_changelog_value_pkey PRIMARY KEY ("id");


--
-- Name: woningblok_grootte_changelog woningblok_grootte_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_grootte_changelog
    ADD CONSTRAINT woningblok_grootte_changelog_pkey PRIMARY KEY ("id");


--
-- Name: woningblok_kadastrale_koppeling_changelog woningblok_kadastrale_koppeling_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_kadastrale_koppeling_changelog
    ADD CONSTRAINT woningblok_kadastrale_koppeling_changelog_pkey PRIMARY KEY ("id");


--
-- Name: woningblok_kadastrale_koppeling_changelog_value woningblok_kadastrale_koppeling_changelog_value_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_kadastrale_koppeling_changelog_value
    ADD CONSTRAINT woningblok_kadastrale_koppeling_changelog_value_pkey PRIMARY KEY ("id");


--
-- Name: woningblok_maatwerk_boolean_changelog woningblok_maatwerk_boolean_eigenschap_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_boolean_changelog
    ADD CONSTRAINT woningblok_maatwerk_boolean_eigenschap_changelog_pkey PRIMARY KEY ("id");


--
-- Name: woningblok_maatwerk_categorie_changelog_value woningblok_maatwerk_categorie_changelog_value_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_categorie_changelog_value
    ADD CONSTRAINT woningblok_maatwerk_categorie_changelog_value_pkey PRIMARY KEY ("id");


--
-- Name: woningblok_maatwerk_categorie_changelog woningblok_maatwerk_categorie_eigenschap_changelos_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_categorie_changelog
    ADD CONSTRAINT woningblok_maatwerk_categorie_eigenschap_changelos_pkey PRIMARY KEY ("id");


--
-- Name: maatwerk_categorie_waarde woningblok_maatwerk_categorie_waarde_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.maatwerk_categorie_waarde
    ADD CONSTRAINT woningblok_maatwerk_categorie_waarde_pkey PRIMARY KEY ("id");


--
-- Name: maatwerk_categorie_waarde_state woningblok_maatwerk_categorie_waarde_state_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.maatwerk_categorie_waarde_state
    ADD CONSTRAINT woningblok_maatwerk_categorie_waarde_state_pkey PRIMARY KEY ("id");


--
-- Name: maatwerk_eigenschap woningblok_maatwerk_eigenschap_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.maatwerk_eigenschap
    ADD CONSTRAINT woningblok_maatwerk_eigenschap_pkey PRIMARY KEY ("id");


--
-- Name: maatwerk_eigenschap_state woningblok_maatwerk_eigenschap_state_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.maatwerk_eigenschap_state
    ADD CONSTRAINT woningblok_maatwerk_eigenschap_state_pkey PRIMARY KEY ("id");


--
-- Name: woningblok_maatwerk_numeriek_changelog woningblok_maatwerk_numeriek_eigenschap_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_numeriek_changelog
    ADD CONSTRAINT woningblok_maatwerk_numeriek_eigenschap_changelog_pkey PRIMARY KEY ("id");


--
-- Name: woningblok_maatwerk_ordinaal_changelog woningblok_maatwerk_ordinaal_eigenschap_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_ordinaal_changelog
    ADD CONSTRAINT woningblok_maatwerk_ordinaal_eigenschap_changelog_pkey PRIMARY KEY ("id");


--
-- Name: maatwerk_ordinaal_waarde woningblok_maatwerk_ordinale_waarde_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.maatwerk_ordinaal_waarde
    ADD CONSTRAINT woningblok_maatwerk_ordinale_waarde_pkey PRIMARY KEY ("id");


--
-- Name: maatwerk_ordinaal_waarde_state woningblok_maatwerk_ordinale_waarde_state_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.maatwerk_ordinaal_waarde_state
    ADD CONSTRAINT woningblok_maatwerk_ordinale_waarde_state_pkey PRIMARY KEY ("id");


--
-- Name: woningblok_mutatie_changelog woningblok_mutatie_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_mutatie_changelog
    ADD CONSTRAINT woningblok_mutatie_changelog_pkey PRIMARY KEY ("id");


--
-- Name: woningblok_mutatie_changelog_soort_value woningblok_mutatie_changelog_soort_value_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_mutatie_changelog_soort_value
    ADD CONSTRAINT woningblok_mutatie_changelog_soort_value_pkey PRIMARY KEY ("id");


--
-- Name: woningblok_naam_changelog woningblok_naam_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_naam_changelog
    ADD CONSTRAINT woningblok_naam_changelog_pkey PRIMARY KEY ("id");


--
-- Name: woningblok woningblok_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok
    ADD CONSTRAINT woningblok_pkey PRIMARY KEY ("id");


--
-- Name: woningblok_programmering_changelog woningblok_programmering_changelog_pkey1; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_programmering_changelog
    ADD CONSTRAINT woningblok_programmering_changelog_pkey1 PRIMARY KEY ("id");


--
-- Name: woningblok_state woningblok_state_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_state
    ADD CONSTRAINT woningblok_state_pkey PRIMARY KEY ("id");


--
-- Name: woningblok_type_en_fysiek_changelog_fysiek_value woningblok_type_en_fysiek_voorkomen_changelog_fysiek_voork_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_type_en_fysiek_changelog_fysiek_value
    ADD CONSTRAINT woningblok_type_en_fysiek_voorkomen_changelog_fysiek_voork_pkey PRIMARY KEY ("id");


--
-- Name: woningblok_type_en_fysiek_changelog_type_value woningblok_type_en_fysiek_voorkomen_changelog_type_value_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_type_en_fysiek_changelog_type_value
    ADD CONSTRAINT woningblok_type_en_fysiek_voorkomen_changelog_type_value_pkey PRIMARY KEY ("id");


--
-- Name: woningblok_gemeente_indeling_changelog woningblok_wijk_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_gemeente_indeling_changelog
    ADD CONSTRAINT woningblok_wijk_changelog_pkey PRIMARY KEY ("id");


--
-- Name: woningblok_type_en_fysiek_changelog woningblok_woning_type_en_fysiek_voorkomen_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_type_en_fysiek_changelog
    ADD CONSTRAINT woningblok_woning_type_en_fysiek_voorkomen_changelog_pkey PRIMARY KEY ("id");


--
-- Name: actor_state fk_actor_state__actor; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.actor_state
    ADD CONSTRAINT fk_actor_state__actor FOREIGN KEY ("actor_id") REFERENCES diwi_testset.actor("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: actor_state fk_actor_state__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.actor_state
    ADD CONSTRAINT fk_actor_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: actor_state fk_actor_state__organization; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.actor_state
    ADD CONSTRAINT fk_actor_state__organization FOREIGN KEY ("organization_id") REFERENCES diwi_testset.organization("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: buurt_state fk_buurt_state__buurt; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.buurt_state
    ADD CONSTRAINT fk_buurt_state__buurt FOREIGN KEY ("buurt_id") REFERENCES diwi_testset.buurt("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: buurt_state fk_buurt_state__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.buurt_state
    ADD CONSTRAINT fk_buurt_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: buurt_state fk_buurt_state__wijk; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.buurt_state
    ADD CONSTRAINT fk_buurt_state__wijk FOREIGN KEY ("wijk_id") REFERENCES diwi_testset.wijk("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: document_soort_state fk_document_soort_state__document_soort; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.document_soort_state
    ADD CONSTRAINT fk_document_soort_state__document_soort FOREIGN KEY ("document_soort_id") REFERENCES diwi_testset.document_soort("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: document_soort_state fk_document_soort_state__user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.document_soort_state
    ADD CONSTRAINT fk_document_soort_state__user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: document_state fk_document_state__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.document_state
    ADD CONSTRAINT fk_document_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: document_state fk_document_state__document; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.document_state
    ADD CONSTRAINT fk_document_state__document FOREIGN KEY ("document_id") REFERENCES diwi_testset.document("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: document_state fk_document_state__milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.document_state
    ADD CONSTRAINT fk_document_state__milestone FOREIGN KEY ("milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: document_state fk_document_state__owner_organisation; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.document_state
    ADD CONSTRAINT fk_document_state__owner_organisation FOREIGN KEY ("owner_organization_id") REFERENCES diwi_testset.organization("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: document_state_soort_value fk_document_state_soort_value__document_state; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.document_state_soort_value
    ADD CONSTRAINT fk_document_state_soort_value__document_state FOREIGN KEY ("document_state_id") REFERENCES diwi_testset.document_state("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: document_state_soort_value fk_document_state_soort_value__soort_value; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.document_state_soort_value
    ADD CONSTRAINT fk_document_state_soort_value__soort_value FOREIGN KEY ("document_soort_id") REFERENCES diwi_testset.document_soort("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gemeente_state fk_gemeente_state__gchange_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.gemeente_state
    ADD CONSTRAINT fk_gemeente_state__gchange_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gemeente_state fk_gemeente_state__gemeente; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.gemeente_state
    ADD CONSTRAINT fk_gemeente_state__gemeente FOREIGN KEY ("gemeente_id") REFERENCES diwi_testset.gemeente("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: milestone_state fk_milestone_state__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.milestone_state
    ADD CONSTRAINT fk_milestone_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: milestone_state fk_milestone_state__milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.milestone_state
    ADD CONSTRAINT fk_milestone_state__milestone FOREIGN KEY ("milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: organization_state fk_organization_state__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.organization_state
    ADD CONSTRAINT fk_organization_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: organization_state fk_organization_state__organization; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.organization_state
    ADD CONSTRAINT fk_organization_state__organization FOREIGN KEY ("organization_id") REFERENCES diwi_testset.organization("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: organization_state fk_organization_state__parent_organization; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.organization_state
    ADD CONSTRAINT fk_organization_state__parent_organization FOREIGN KEY ("parent_organization_id") REFERENCES diwi_testset.organization("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_doelgroep fk_plan_conditie_doelgroep__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_doelgroep
    ADD CONSTRAINT fk_plan_conditie_doelgroep__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_doelgroep fk_plan_conditie_doelgroep__plan_conditie; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_doelgroep
    ADD CONSTRAINT fk_plan_conditie_doelgroep__plan_conditie FOREIGN KEY ("plan_conditie_id") REFERENCES diwi_testset.plan_conditie("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_doelgroep_value fk_plan_conditie_doelgroep_value__conditie; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_doelgroep_value
    ADD CONSTRAINT fk_plan_conditie_doelgroep_value__conditie FOREIGN KEY ("plan_conditie_doelgroep_id") REFERENCES diwi_testset.plan_conditie_doelgroep("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_eigendom_en_waarde fk_plan_conditie_eigendom_en_waarde__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_eigendom_en_waarde
    ADD CONSTRAINT fk_plan_conditie_eigendom_en_waarde__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_eigendom_en_waarde fk_plan_conditie_eigendom_en_waarde__plan_conditie; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_eigendom_en_waarde
    ADD CONSTRAINT fk_plan_conditie_eigendom_en_waarde__plan_conditie FOREIGN KEY ("plan_conditie_id") REFERENCES diwi_testset.plan_conditie("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_eigendom_en_waarde_soort_value fk_plan_conditie_eigendom_en_waarde_soort_value__conditie; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_eigendom_en_waarde_soort_value
    ADD CONSTRAINT fk_plan_conditie_eigendom_en_waarde_soort_value__conditie FOREIGN KEY ("plan_conditie_eigendom_en_waarde_id") REFERENCES diwi_testset.plan_conditie_eigendom_en_waarde("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_gemeente_indeling fk_plan_conditie_gemeente_indeling__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_gemeente_indeling
    ADD CONSTRAINT fk_plan_conditie_gemeente_indeling__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_gemeente_indeling fk_plan_conditie_gemeente_indeling__plan_conditie; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_gemeente_indeling
    ADD CONSTRAINT fk_plan_conditie_gemeente_indeling__plan_conditie FOREIGN KEY ("plan_conditie_id") REFERENCES diwi_testset.plan_conditie("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_gemeente_indeling_buurt_value fk_plan_conditie_gemeente_indeling_buurt_value__buurt; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_gemeente_indeling_buurt_value
    ADD CONSTRAINT fk_plan_conditie_gemeente_indeling_buurt_value__buurt FOREIGN KEY ("wijk_id") REFERENCES diwi_testset.buurt("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_gemeente_indeling_buurt_value fk_plan_conditie_gemeente_indeling_buurt_value__conditie; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_gemeente_indeling_buurt_value
    ADD CONSTRAINT fk_plan_conditie_gemeente_indeling_buurt_value__conditie FOREIGN KEY ("plan_conditie_gemeente_indeling_id") REFERENCES diwi_testset.plan_conditie_gemeente_indeling("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_gemeente_indeling_gemeente_value fk_plan_conditie_gemeente_indeling_gemeente_value__conditie; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_gemeente_indeling_gemeente_value
    ADD CONSTRAINT fk_plan_conditie_gemeente_indeling_gemeente_value__conditie FOREIGN KEY ("plan_conditie_gemeente_indeling_id") REFERENCES diwi_testset.plan_conditie_gemeente_indeling("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_gemeente_indeling_gemeente_value fk_plan_conditie_gemeente_indeling_gemeente_value__gemeente; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_gemeente_indeling_gemeente_value
    ADD CONSTRAINT fk_plan_conditie_gemeente_indeling_gemeente_value__gemeente FOREIGN KEY ("gemeente_id") REFERENCES diwi_testset.gemeente("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_gemeente_indeling_wijk_value fk_plan_conditie_gemeente_indeling_wijk_value__conditie; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_gemeente_indeling_wijk_value
    ADD CONSTRAINT fk_plan_conditie_gemeente_indeling_wijk_value__conditie FOREIGN KEY ("plan_conditie_gemeente_indeling_id") REFERENCES diwi_testset.plan_conditie_gemeente_indeling("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_gemeente_indeling_wijk_value fk_plan_conditie_gemeente_indeling_wijk_value__wijk; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_gemeente_indeling_wijk_value
    ADD CONSTRAINT fk_plan_conditie_gemeente_indeling_wijk_value__wijk FOREIGN KEY ("wijk_id") REFERENCES diwi_testset.wijk("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_kadastraal fk_plan_conditie_geografie__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_kadastraal
    ADD CONSTRAINT fk_plan_conditie_geografie__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_kadastraal fk_plan_conditie_geografie__plan_conditie; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_kadastraal
    ADD CONSTRAINT fk_plan_conditie_geografie__plan_conditie FOREIGN KEY ("plan_conditie_id") REFERENCES diwi_testset.plan_conditie("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_grondpositie fk_plan_conditie_grondpositie__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_grondpositie
    ADD CONSTRAINT fk_plan_conditie_grondpositie__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_grondpositie fk_plan_conditie_grondpositie__plan_conditie; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_grondpositie
    ADD CONSTRAINT fk_plan_conditie_grondpositie__plan_conditie FOREIGN KEY ("plan_conditie_id") REFERENCES diwi_testset.plan_conditie("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_grondpositie_value fk_plan_conditie_grondpositie_value__conditie; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_grondpositie_value
    ADD CONSTRAINT fk_plan_conditie_grondpositie_value__conditie FOREIGN KEY ("plan_conditie_grondpositie_id") REFERENCES diwi_testset.plan_conditie_grondpositie("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_grootte fk_plan_conditie_grootte__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_grootte
    ADD CONSTRAINT fk_plan_conditie_grootte__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_grootte fk_plan_conditie_grootte__plan_conditie; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_grootte
    ADD CONSTRAINT fk_plan_conditie_grootte__plan_conditie FOREIGN KEY ("plan_conditie_id") REFERENCES diwi_testset.plan_conditie("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_kadastraal_value fk_plan_conditie_kadastraal_value__conditie; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_kadastraal_value
    ADD CONSTRAINT fk_plan_conditie_kadastraal_value__conditie FOREIGN KEY ("plan_conditie_kadastraal_id") REFERENCES diwi_testset.plan_conditie_kadastraal("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_maatwerk_boolean fk_plan_conditie_maatwerk_boolean__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_maatwerk_boolean
    ADD CONSTRAINT fk_plan_conditie_maatwerk_boolean__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_maatwerk_boolean fk_plan_conditie_maatwerk_boolean__plan_conditie; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_maatwerk_boolean
    ADD CONSTRAINT fk_plan_conditie_maatwerk_boolean__plan_conditie FOREIGN KEY ("plan_conditie_id") REFERENCES diwi_testset.plan_conditie("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_maatwerk_categorie fk_plan_conditie_maatwerk_categorie__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_maatwerk_categorie
    ADD CONSTRAINT fk_plan_conditie_maatwerk_categorie__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_maatwerk_categorie fk_plan_conditie_maatwerk_categorie__plan_conditie; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_maatwerk_categorie
    ADD CONSTRAINT fk_plan_conditie_maatwerk_categorie__plan_conditie FOREIGN KEY ("plan_conditie_id") REFERENCES diwi_testset.plan_conditie("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_maatwerk_categorie_value fk_plan_conditie_maatwerk_categorie_value__conditie; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_maatwerk_categorie_value
    ADD CONSTRAINT fk_plan_conditie_maatwerk_categorie_value__conditie FOREIGN KEY ("plan_conditie_maatwerk_categorie_id") REFERENCES diwi_testset.plan_conditie_maatwerk_categorie("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_maatwerk_categorie_value fk_plan_conditie_maatwerk_categorie_value__waarde; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_maatwerk_categorie_value
    ADD CONSTRAINT fk_plan_conditie_maatwerk_categorie_value__waarde FOREIGN KEY ("eigenschap_waarde_id") REFERENCES diwi_testset.maatwerk_categorie_waarde("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_maatwerk_numeriek fk_plan_conditie_maatwerk_numeriek__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_maatwerk_numeriek
    ADD CONSTRAINT fk_plan_conditie_maatwerk_numeriek__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_maatwerk_numeriek fk_plan_conditie_maatwerk_numeriek__plan_conditie; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_maatwerk_numeriek
    ADD CONSTRAINT fk_plan_conditie_maatwerk_numeriek__plan_conditie FOREIGN KEY ("plan_conditie_id") REFERENCES diwi_testset.plan_conditie("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_maatwerk_ordinaal fk_plan_conditie_maatwerk_ordinaal__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_maatwerk_ordinaal
    ADD CONSTRAINT fk_plan_conditie_maatwerk_ordinaal__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_maatwerk_ordinaal fk_plan_conditie_maatwerk_ordinaal__max_value; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_maatwerk_ordinaal
    ADD CONSTRAINT fk_plan_conditie_maatwerk_ordinaal__max_value FOREIGN KEY ("max_value_id") REFERENCES diwi_testset.maatwerk_ordinaal_waarde("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_maatwerk_ordinaal fk_plan_conditie_maatwerk_ordinaal__min_value; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_maatwerk_ordinaal
    ADD CONSTRAINT fk_plan_conditie_maatwerk_ordinaal__min_value FOREIGN KEY ("min_value_id") REFERENCES diwi_testset.maatwerk_ordinaal_waarde("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_maatwerk_ordinaal fk_plan_conditie_maatwerk_ordinaal__plan_conditie; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_maatwerk_ordinaal
    ADD CONSTRAINT fk_plan_conditie_maatwerk_ordinaal__plan_conditie FOREIGN KEY ("plan_conditie_id") REFERENCES diwi_testset.plan_conditie("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_maatwerk_ordinaal fk_plan_conditie_maatwerk_ordinaal__value; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_maatwerk_ordinaal
    ADD CONSTRAINT fk_plan_conditie_maatwerk_ordinaal__value FOREIGN KEY ("value_id") REFERENCES diwi_testset.maatwerk_ordinaal_waarde("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_programmering fk_plan_conditie_programmering__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_programmering
    ADD CONSTRAINT fk_plan_conditie_programmering__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_programmering fk_plan_conditie_programmering__plan_conditie; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_programmering
    ADD CONSTRAINT fk_plan_conditie_programmering__plan_conditie FOREIGN KEY ("plan_conditie_id") REFERENCES diwi_testset.plan_conditie("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_state fk_plan_conditie_state__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_state
    ADD CONSTRAINT fk_plan_conditie_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_state fk_plan_conditie_state__plan; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_state
    ADD CONSTRAINT fk_plan_conditie_state__plan FOREIGN KEY ("plan_id") REFERENCES diwi_testset.plan("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_state fk_plan_conditie_state__plan_conditie; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_state
    ADD CONSTRAINT fk_plan_conditie_state__plan_conditie FOREIGN KEY ("plan_conditie_id") REFERENCES diwi_testset.plan_conditie("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_type_en_fysiek_fysiek_value fk_plan_conditie_type_en_fysiek_fysiek_value__conditie; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_type_en_fysiek_fysiek_value
    ADD CONSTRAINT fk_plan_conditie_type_en_fysiek_fysiek_value__conditie FOREIGN KEY ("plan_conditie_type_en_fysiek_id") REFERENCES diwi_testset.plan_conditie_type_en_fysiek("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_type_en_fysiek_type_value fk_plan_conditie_type_en_fysiek_type_value__conditie; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_type_en_fysiek_type_value
    ADD CONSTRAINT fk_plan_conditie_type_en_fysiek_type_value__conditie FOREIGN KEY ("plan_conditie_type_en_fysiek_id") REFERENCES diwi_testset.plan_conditie_type_en_fysiek("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_type_en_fysiek fk_plan_conditie_type_en_fysiek_voorkomen__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_type_en_fysiek
    ADD CONSTRAINT fk_plan_conditie_type_en_fysiek_voorkomen__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_type_en_fysiek fk_plan_conditie_type_en_fysiek_voorkomen__plan_conditie; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_conditie_type_en_fysiek
    ADD CONSTRAINT fk_plan_conditie_type_en_fysiek_voorkomen__plan_conditie FOREIGN KEY ("plan_conditie_id") REFERENCES diwi_testset.plan_conditie("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_soort_state fk_plan_soort_state__plan_soort; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_soort_state
    ADD CONSTRAINT fk_plan_soort_state__plan_soort FOREIGN KEY ("plan_soort_id") REFERENCES diwi_testset.plan_soort("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_soort_state fk_plan_soort_state__user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_soort_state
    ADD CONSTRAINT fk_plan_soort_state__user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_state fk_plan_state__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_state
    ADD CONSTRAINT fk_plan_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_state fk_plan_state__owner_organization; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_state
    ADD CONSTRAINT fk_plan_state__owner_organization FOREIGN KEY ("owner_organization_id") REFERENCES diwi_testset.organization("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_state fk_plan_state__plan; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_state
    ADD CONSTRAINT fk_plan_state__plan FOREIGN KEY ("plan_id") REFERENCES diwi_testset.plan("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_state_soort_value fk_plan_state_soort_value__plan_state; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_state_soort_value
    ADD CONSTRAINT fk_plan_state_soort_value__plan_state FOREIGN KEY ("plan_state_id") REFERENCES diwi_testset.plan_state("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_state_soort_value fk_plan_state_soort_value__soort_value; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.plan_state_soort_value
    ADD CONSTRAINT fk_plan_state_soort_value__soort_value FOREIGN KEY ("plan_soort_id") REFERENCES diwi_testset.plan_soort("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_actor_rol_changelog fk_project_actor_rol_changelog__actor; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_actor_rol_changelog
    ADD CONSTRAINT fk_project_actor_rol_changelog__actor FOREIGN KEY ("actor_id") REFERENCES diwi_testset.actor("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_actor_rol_changelog fk_project_actor_rol_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_actor_rol_changelog
    ADD CONSTRAINT fk_project_actor_rol_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_actor_rol_changelog fk_project_actor_rol_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_actor_rol_changelog
    ADD CONSTRAINT fk_project_actor_rol_changelog__end_milestone FOREIGN KEY ("end_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_actor_rol_changelog fk_project_actor_rol_changelog__project; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_actor_rol_changelog
    ADD CONSTRAINT fk_project_actor_rol_changelog__project FOREIGN KEY ("project_id") REFERENCES diwi_testset.project("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_actor_rol_changelog fk_project_actor_rol_changelog__rol; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_actor_rol_changelog
    ADD CONSTRAINT fk_project_actor_rol_changelog__rol FOREIGN KEY ("project_actor_rol_id") REFERENCES diwi_testset.project_actor_rol_value("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_actor_rol_changelog fk_project_actor_rol_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_actor_rol_changelog
    ADD CONSTRAINT fk_project_actor_rol_changelog__start_milestone FOREIGN KEY ("start_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_actor_rol_value_state fk_project_actor_rol_value__actor_rol; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_actor_rol_value_state
    ADD CONSTRAINT fk_project_actor_rol_value__actor_rol FOREIGN KEY ("project_actor_rol_value_id") REFERENCES diwi_testset.project_actor_rol_value("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_actor_rol_value_state fk_project_actor_rol_value__user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_actor_rol_value_state
    ADD CONSTRAINT fk_project_actor_rol_value__user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_duration_changelog fk_project_duration_chagelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_duration_changelog
    ADD CONSTRAINT fk_project_duration_chagelog__start_milestone FOREIGN KEY ("start_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_duration_changelog fk_project_duration_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_duration_changelog
    ADD CONSTRAINT fk_project_duration_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_duration_changelog fk_project_duration_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_duration_changelog
    ADD CONSTRAINT fk_project_duration_changelog__end_milestone FOREIGN KEY ("end_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_duration_changelog fk_project_duration_changelog__project; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_duration_changelog
    ADD CONSTRAINT fk_project_duration_changelog__project FOREIGN KEY ("project_id") REFERENCES diwi_testset.project("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_fase_changelog fk_project_fase_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_fase_changelog
    ADD CONSTRAINT fk_project_fase_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_fase_changelog fk_project_fase_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_fase_changelog
    ADD CONSTRAINT fk_project_fase_changelog__end_milestone FOREIGN KEY ("end_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_fase_changelog fk_project_fase_changelog__project; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_fase_changelog
    ADD CONSTRAINT fk_project_fase_changelog__project FOREIGN KEY ("project_id") REFERENCES diwi_testset.project("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_fase_changelog fk_project_fase_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_fase_changelog
    ADD CONSTRAINT fk_project_fase_changelog__start_milestone FOREIGN KEY ("start_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_filiatie fk_project_filiatie__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_filiatie
    ADD CONSTRAINT fk_project_filiatie__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_filiatie fk_project_filiatie__new_project; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_filiatie
    ADD CONSTRAINT fk_project_filiatie__new_project FOREIGN KEY ("new_project_id") REFERENCES diwi_testset.project("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_filiatie fk_project_filiatie__old_project; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_filiatie
    ADD CONSTRAINT fk_project_filiatie__old_project FOREIGN KEY ("old_project_id") REFERENCES diwi_testset.project("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_gemeenterol_changelog fk_project_gemeenterol_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_gemeenterol_changelog
    ADD CONSTRAINT fk_project_gemeenterol_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_gemeenterol_changelog fk_project_gemeenterol_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_gemeenterol_changelog
    ADD CONSTRAINT fk_project_gemeenterol_changelog__end_milestone FOREIGN KEY ("end_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_gemeenterol_changelog fk_project_gemeenterol_changelog__gemeenterol; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_gemeenterol_changelog
    ADD CONSTRAINT fk_project_gemeenterol_changelog__gemeenterol FOREIGN KEY ("project_gemeenterol_value_id") REFERENCES diwi_testset.project_gemeenterol_value("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_gemeenterol_changelog fk_project_gemeenterol_changelog__project; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_gemeenterol_changelog
    ADD CONSTRAINT fk_project_gemeenterol_changelog__project FOREIGN KEY ("project_id") REFERENCES diwi_testset.project("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_gemeenterol_value_state fk_project_gemeenterol_value_state__gemeenterol; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_gemeenterol_value_state
    ADD CONSTRAINT fk_project_gemeenterol_value_state__gemeenterol FOREIGN KEY ("project_gemeenterol_value_id") REFERENCES diwi_testset.project_gemeenterol_value("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_gemeenterol_value_state fk_project_gemeenterol_value_state__user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_gemeenterol_value_state
    ADD CONSTRAINT fk_project_gemeenterol_value_state__user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_boolean_changelog fk_project_maatwerk_boolean_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_maatwerk_boolean_changelog
    ADD CONSTRAINT fk_project_maatwerk_boolean_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_boolean_changelog fk_project_maatwerk_boolean_changelog__eigenschap; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_maatwerk_boolean_changelog
    ADD CONSTRAINT fk_project_maatwerk_boolean_changelog__eigenschap FOREIGN KEY ("eigenschap_id") REFERENCES diwi_testset.maatwerk_eigenschap("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_boolean_changelog fk_project_maatwerk_boolean_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_maatwerk_boolean_changelog
    ADD CONSTRAINT fk_project_maatwerk_boolean_changelog__end_milestone FOREIGN KEY ("end_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_boolean_changelog fk_project_maatwerk_boolean_changelog__project; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_maatwerk_boolean_changelog
    ADD CONSTRAINT fk_project_maatwerk_boolean_changelog__project FOREIGN KEY ("project_id") REFERENCES diwi_testset.project("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_boolean_changelog fk_project_maatwerk_boolean_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_maatwerk_boolean_changelog
    ADD CONSTRAINT fk_project_maatwerk_boolean_changelog__start_milestone FOREIGN KEY ("start_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_categorie_changelog fk_project_maatwerk_categorie_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_maatwerk_categorie_changelog
    ADD CONSTRAINT fk_project_maatwerk_categorie_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_categorie_changelog fk_project_maatwerk_categorie_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_maatwerk_categorie_changelog
    ADD CONSTRAINT fk_project_maatwerk_categorie_changelog__end_milestone FOREIGN KEY ("end_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_categorie_changelog fk_project_maatwerk_categorie_changelog__project; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_maatwerk_categorie_changelog
    ADD CONSTRAINT fk_project_maatwerk_categorie_changelog__project FOREIGN KEY ("project_id") REFERENCES diwi_testset.project("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_categorie_changelog fk_project_maatwerk_categorie_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_maatwerk_categorie_changelog
    ADD CONSTRAINT fk_project_maatwerk_categorie_changelog__start_milestone FOREIGN KEY ("start_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_categorie_changelog_value fk_project_maatwerk_categorie_changelog_value__changelog; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_maatwerk_categorie_changelog_value
    ADD CONSTRAINT fk_project_maatwerk_categorie_changelog_value__changelog FOREIGN KEY ("project_maatwerk_categorie_changelog_id") REFERENCES diwi_testset.project_maatwerk_categorie_changelog("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_categorie_changelog_value fk_project_maatwerk_categorie_changelog_value__waarde; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_maatwerk_categorie_changelog_value
    ADD CONSTRAINT fk_project_maatwerk_categorie_changelog_value__waarde FOREIGN KEY ("eigenschap_waarde_id") REFERENCES diwi_testset.maatwerk_categorie_waarde("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_numeriek_changelog fk_project_maatwerk_numeriek_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_maatwerk_numeriek_changelog
    ADD CONSTRAINT fk_project_maatwerk_numeriek_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_numeriek_changelog fk_project_maatwerk_numeriek_changelog__eigenschap; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_maatwerk_numeriek_changelog
    ADD CONSTRAINT fk_project_maatwerk_numeriek_changelog__eigenschap FOREIGN KEY ("eigenschap_id") REFERENCES diwi_testset.maatwerk_eigenschap("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_numeriek_changelog fk_project_maatwerk_numeriek_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_maatwerk_numeriek_changelog
    ADD CONSTRAINT fk_project_maatwerk_numeriek_changelog__end_milestone FOREIGN KEY ("end_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_numeriek_changelog fk_project_maatwerk_numeriek_changelog__project; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_maatwerk_numeriek_changelog
    ADD CONSTRAINT fk_project_maatwerk_numeriek_changelog__project FOREIGN KEY ("project_id") REFERENCES diwi_testset.project("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_numeriek_changelog fk_project_maatwerk_numeriek_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_maatwerk_numeriek_changelog
    ADD CONSTRAINT fk_project_maatwerk_numeriek_changelog__start_milestone FOREIGN KEY ("start_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_ordinaal_changelog fk_project_maatwerk_ordinaal_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_maatwerk_ordinaal_changelog
    ADD CONSTRAINT fk_project_maatwerk_ordinaal_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_ordinaal_changelog fk_project_maatwerk_ordinaal_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_maatwerk_ordinaal_changelog
    ADD CONSTRAINT fk_project_maatwerk_ordinaal_changelog__end_milestone FOREIGN KEY ("end_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_ordinaal_changelog fk_project_maatwerk_ordinaal_changelog__max_value; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_maatwerk_ordinaal_changelog
    ADD CONSTRAINT fk_project_maatwerk_ordinaal_changelog__max_value FOREIGN KEY ("max_value_id") REFERENCES diwi_testset.maatwerk_ordinaal_waarde("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_ordinaal_changelog fk_project_maatwerk_ordinaal_changelog__min_value; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_maatwerk_ordinaal_changelog
    ADD CONSTRAINT fk_project_maatwerk_ordinaal_changelog__min_value FOREIGN KEY ("max_value_id") REFERENCES diwi_testset.maatwerk_ordinaal_waarde("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_ordinaal_changelog fk_project_maatwerk_ordinaal_changelog__project; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_maatwerk_ordinaal_changelog
    ADD CONSTRAINT fk_project_maatwerk_ordinaal_changelog__project FOREIGN KEY ("project_id") REFERENCES diwi_testset.project("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_ordinaal_changelog fk_project_maatwerk_ordinaal_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_maatwerk_ordinaal_changelog
    ADD CONSTRAINT fk_project_maatwerk_ordinaal_changelog__start_milestone FOREIGN KEY ("start_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_ordinaal_changelog fk_project_maatwerk_ordinaal_changelog__value; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_maatwerk_ordinaal_changelog
    ADD CONSTRAINT fk_project_maatwerk_ordinaal_changelog__value FOREIGN KEY ("value_id") REFERENCES diwi_testset.maatwerk_ordinaal_waarde("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_name_changelog fk_project_name_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_name_changelog
    ADD CONSTRAINT fk_project_name_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_name_changelog fk_project_name_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_name_changelog
    ADD CONSTRAINT fk_project_name_changelog__end_milestone FOREIGN KEY ("end_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_name_changelog fk_project_name_changelog__project; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_name_changelog
    ADD CONSTRAINT fk_project_name_changelog__project FOREIGN KEY ("project_id") REFERENCES diwi_testset.project("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_name_changelog fk_project_name_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_name_changelog
    ADD CONSTRAINT fk_project_name_changelog__start_milestone FOREIGN KEY ("start_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_plan_type_changelog fk_project_plan_type_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_plan_type_changelog
    ADD CONSTRAINT fk_project_plan_type_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_plan_type_changelog fk_project_plan_type_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_plan_type_changelog
    ADD CONSTRAINT fk_project_plan_type_changelog__end_milestone FOREIGN KEY ("end_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_plan_type_changelog fk_project_plan_type_changelog__project; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_plan_type_changelog
    ADD CONSTRAINT fk_project_plan_type_changelog__project FOREIGN KEY ("project_id") REFERENCES diwi_testset.project("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_plan_type_changelog fk_project_plan_type_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_plan_type_changelog
    ADD CONSTRAINT fk_project_plan_type_changelog__start_milestone FOREIGN KEY ("start_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_plan_type_changelog_value fk_project_plan_type_changelog_value__changelog; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_plan_type_changelog_value
    ADD CONSTRAINT fk_project_plan_type_changelog_value__changelog FOREIGN KEY ("changelog_id") REFERENCES diwi_testset.project_plan_type_changelog("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_planologische_planstatus_changelog fk_project_planologische_planstatus_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_planologische_planstatus_changelog
    ADD CONSTRAINT fk_project_planologische_planstatus_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_planologische_planstatus_changelog fk_project_planologische_planstatus_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_planologische_planstatus_changelog
    ADD CONSTRAINT fk_project_planologische_planstatus_changelog__end_milestone FOREIGN KEY ("end_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_planologische_planstatus_changelog fk_project_planologische_planstatus_changelog__project; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_planologische_planstatus_changelog
    ADD CONSTRAINT fk_project_planologische_planstatus_changelog__project FOREIGN KEY ("project_id") REFERENCES diwi_testset.project("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_planologische_planstatus_changelog fk_project_planologische_planstatus_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_planologische_planstatus_changelog
    ADD CONSTRAINT fk_project_planologische_planstatus_changelog__start_milestone FOREIGN KEY ("start_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_planologische_planstatus_changelog_value fk_project_planologische_planstatus_changelog_value__changelog; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_planologische_planstatus_changelog_value
    ADD CONSTRAINT fk_project_planologische_planstatus_changelog_value__changelog FOREIGN KEY ("planologische_planstatus_changelog_id") REFERENCES diwi_testset.project_planologische_planstatus_changelog("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_priorisering_changelog fk_project_priorisering_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_priorisering_changelog
    ADD CONSTRAINT fk_project_priorisering_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_priorisering_changelog fk_project_priorisering_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_priorisering_changelog
    ADD CONSTRAINT fk_project_priorisering_changelog__end_milestone FOREIGN KEY ("end_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_priorisering_changelog fk_project_priorisering_changelog__max_priorisering; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_priorisering_changelog
    ADD CONSTRAINT fk_project_priorisering_changelog__max_priorisering FOREIGN KEY ("project_priorisering_max_value_id") REFERENCES diwi_testset.project_priorisering_value("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_priorisering_changelog fk_project_priorisering_changelog__min_priorisering; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_priorisering_changelog
    ADD CONSTRAINT fk_project_priorisering_changelog__min_priorisering FOREIGN KEY ("project_priorisering_min_value_id") REFERENCES diwi_testset.project_priorisering_value("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_priorisering_changelog fk_project_priorisering_changelog__priorisering; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_priorisering_changelog
    ADD CONSTRAINT fk_project_priorisering_changelog__priorisering FOREIGN KEY ("project_priorisering_value_id") REFERENCES diwi_testset.project_priorisering_value("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_priorisering_changelog fk_project_priorisering_changelog__project; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_priorisering_changelog
    ADD CONSTRAINT fk_project_priorisering_changelog__project FOREIGN KEY ("project_id") REFERENCES diwi_testset.project("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_priorisering_changelog fk_project_priorisering_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_priorisering_changelog
    ADD CONSTRAINT fk_project_priorisering_changelog__start_milestone FOREIGN KEY ("start_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_priorisering_value_state fk_project_priorisering_value_state__priorisering; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_priorisering_value_state
    ADD CONSTRAINT fk_project_priorisering_value_state__priorisering FOREIGN KEY ("project_priorisering_value_id") REFERENCES diwi_testset.project_priorisering_value("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_priorisering_value_state fk_project_priorisering_value_state__user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_priorisering_value_state
    ADD CONSTRAINT fk_project_priorisering_value_state__user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_state fk_project_state__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_state
    ADD CONSTRAINT fk_project_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_state fk_project_state__owner_organization; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_state
    ADD CONSTRAINT fk_project_state__owner_organization FOREIGN KEY ("owner_organization_id") REFERENCES diwi_testset.organization("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_state fk_project_state__project; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.project_state
    ADD CONSTRAINT fk_project_state__project FOREIGN KEY ("project_id") REFERENCES diwi_testset.project("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: software_module_rights fk_software_module_rights__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.software_module_rights
    ADD CONSTRAINT fk_software_module_rights__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: software_module_rights fk_software_module_rights__organization; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.software_module_rights
    ADD CONSTRAINT fk_software_module_rights__organization FOREIGN KEY ("organization_id") REFERENCES diwi_testset.organization("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: user_state fk_user_state__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.user_state
    ADD CONSTRAINT fk_user_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: user_state fk_user_state__user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.user_state
    ADD CONSTRAINT fk_user_state__user FOREIGN KEY ("user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: user_to_organization fk_user_to_organization__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.user_to_organization
    ADD CONSTRAINT fk_user_to_organization__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: user_to_organization fk_user_to_organization__organization; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.user_to_organization
    ADD CONSTRAINT fk_user_to_organization__organization FOREIGN KEY ("organization_id") REFERENCES diwi_testset.organization("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: user_to_organization fk_user_to_organization__user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.user_to_organization
    ADD CONSTRAINT fk_user_to_organization__user FOREIGN KEY ("user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: wijk_state fk_wijk_state__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.wijk_state
    ADD CONSTRAINT fk_wijk_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: wijk_state fk_wijk_state__gemeente; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.wijk_state
    ADD CONSTRAINT fk_wijk_state__gemeente FOREIGN KEY ("gemeente_id") REFERENCES diwi_testset.gemeente("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: wijk_state fk_wijk_state__wijk; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.wijk_state
    ADD CONSTRAINT fk_wijk_state__wijk FOREIGN KEY ("wijk_id") REFERENCES diwi_testset.wijk("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_doelgroep_changelog fk_woningblok_doelgroep_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_doelgroep_changelog
    ADD CONSTRAINT fk_woningblok_doelgroep_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_doelgroep_changelog fk_woningblok_doelgroep_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_doelgroep_changelog
    ADD CONSTRAINT fk_woningblok_doelgroep_changelog__end_milestone FOREIGN KEY ("end_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_doelgroep_changelog fk_woningblok_doelgroep_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_doelgroep_changelog
    ADD CONSTRAINT fk_woningblok_doelgroep_changelog__start_milestone FOREIGN KEY ("start_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_doelgroep_changelog fk_woningblok_doelgroep_changelog__woningblok; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_doelgroep_changelog
    ADD CONSTRAINT fk_woningblok_doelgroep_changelog__woningblok FOREIGN KEY ("woningblok_id") REFERENCES diwi_testset.woningblok("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_doelgroep_changelog_value fk_woningblok_doelgroep_changelog_value__changelog; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_doelgroep_changelog_value
    ADD CONSTRAINT fk_woningblok_doelgroep_changelog_value__changelog FOREIGN KEY ("woningblok_doelgroep_changelog_id") REFERENCES diwi_testset.woningblok_doelgroep_changelog("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_duration_changelog fk_woningblok_duration_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_duration_changelog
    ADD CONSTRAINT fk_woningblok_duration_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_duration_changelog fk_woningblok_duration_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_duration_changelog
    ADD CONSTRAINT fk_woningblok_duration_changelog__end_milestone FOREIGN KEY ("end_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_duration_changelog fk_woningblok_duration_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_duration_changelog
    ADD CONSTRAINT fk_woningblok_duration_changelog__start_milestone FOREIGN KEY ("start_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_duration_changelog fk_woningblok_duration_changelog__woningblok; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_duration_changelog
    ADD CONSTRAINT fk_woningblok_duration_changelog__woningblok FOREIGN KEY ("woningblok_id") REFERENCES diwi_testset.woningblok("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_eigendom_en_waarde_changelog fk_woningblok_eigendom_en_waarde_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_eigendom_en_waarde_changelog
    ADD CONSTRAINT fk_woningblok_eigendom_en_waarde_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_eigendom_en_waarde_changelog fk_woningblok_eigendom_en_waarde_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_eigendom_en_waarde_changelog
    ADD CONSTRAINT fk_woningblok_eigendom_en_waarde_changelog__end_milestone FOREIGN KEY ("end_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_eigendom_en_waarde_changelog fk_woningblok_eigendom_en_waarde_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_eigendom_en_waarde_changelog
    ADD CONSTRAINT fk_woningblok_eigendom_en_waarde_changelog__start_milestone FOREIGN KEY ("start_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_eigendom_en_waarde_changelog fk_woningblok_eigendom_en_waarde_changelog__woningblok; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_eigendom_en_waarde_changelog
    ADD CONSTRAINT fk_woningblok_eigendom_en_waarde_changelog__woningblok FOREIGN KEY ("woningblok_id") REFERENCES diwi_testset.woningblok("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_eigendom_en_waarde_changelog_soort_value fk_woningblok_eigendom_en_waarde_changelog_value__changelog; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_eigendom_en_waarde_changelog_soort_value
    ADD CONSTRAINT fk_woningblok_eigendom_en_waarde_changelog_value__changelog FOREIGN KEY ("woningblok_eigendom_en_waarde_changelog_id") REFERENCES diwi_testset.woningblok_eigendom_en_waarde_changelog("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_gemeente_indeling_changelog fk_woningblok_gemeente_indeling_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_gemeente_indeling_changelog
    ADD CONSTRAINT fk_woningblok_gemeente_indeling_changelog__end_milestone FOREIGN KEY ("end_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_gemeente_indeling_changelog fk_woningblok_gemeente_indeling_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_gemeente_indeling_changelog
    ADD CONSTRAINT fk_woningblok_gemeente_indeling_changelog__start_milestone FOREIGN KEY ("start_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_gemeente_indeling_changelog fk_woningblok_gemeente_indeling_changelog__woningblok; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_gemeente_indeling_changelog
    ADD CONSTRAINT fk_woningblok_gemeente_indeling_changelog__woningblok FOREIGN KEY ("woningblok_id") REFERENCES diwi_testset.woningblok("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_gemeente_indeling_changelog_buurt fk_woningblok_gemeente_indeling_changelog_buurt__changelog; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_gemeente_indeling_changelog_buurt
    ADD CONSTRAINT fk_woningblok_gemeente_indeling_changelog_buurt__changelog FOREIGN KEY ("woningblok_gemeente_indeling_changelog_id") REFERENCES diwi_testset.woningblok_gemeente_indeling_changelog("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_gemeente_indeling_changelog_buurt fk_woningblok_gemeente_indeling_changelog_buurt__waarde; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_gemeente_indeling_changelog_buurt
    ADD CONSTRAINT fk_woningblok_gemeente_indeling_changelog_buurt__waarde FOREIGN KEY ("buurt_id") REFERENCES diwi_testset.buurt("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_gemeente_indeling_changelog_gemeente fk_woningblok_gemeente_indeling_changelog_gemeente__changelog; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_gemeente_indeling_changelog_gemeente
    ADD CONSTRAINT fk_woningblok_gemeente_indeling_changelog_gemeente__changelog FOREIGN KEY ("woningblok_gemeente_indeling_changelog_id") REFERENCES diwi_testset.woningblok_gemeente_indeling_changelog("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_gemeente_indeling_changelog_gemeente fk_woningblok_gemeente_indeling_changelog_gemeente__waarde; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_gemeente_indeling_changelog_gemeente
    ADD CONSTRAINT fk_woningblok_gemeente_indeling_changelog_gemeente__waarde FOREIGN KEY ("gemeente_id") REFERENCES diwi_testset.gemeente("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_gemeente_indeling_changelog_wijk fk_woningblok_gemeente_indeling_changelog_wijk__changelog; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_gemeente_indeling_changelog_wijk
    ADD CONSTRAINT fk_woningblok_gemeente_indeling_changelog_wijk__changelog FOREIGN KEY ("woningblok_gemeente_indeling_changelog_id") REFERENCES diwi_testset.woningblok_gemeente_indeling_changelog("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_gemeente_indeling_changelog_wijk fk_woningblok_gemeente_indeling_changelog_wijk__waarde; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_gemeente_indeling_changelog_wijk
    ADD CONSTRAINT fk_woningblok_gemeente_indeling_changelog_wijk__waarde FOREIGN KEY ("wijk_id") REFERENCES diwi_testset.wijk("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_grondpositie_changelog fk_woningblok_grondpositie_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_grondpositie_changelog
    ADD CONSTRAINT fk_woningblok_grondpositie_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_grondpositie_changelog fk_woningblok_grondpositie_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_grondpositie_changelog
    ADD CONSTRAINT fk_woningblok_grondpositie_changelog__end_milestone FOREIGN KEY ("end_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_grondpositie_changelog fk_woningblok_grondpositie_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_grondpositie_changelog
    ADD CONSTRAINT fk_woningblok_grondpositie_changelog__start_milestone FOREIGN KEY ("start_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_grondpositie_changelog fk_woningblok_grondpositie_changelog__woningblok; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_grondpositie_changelog
    ADD CONSTRAINT fk_woningblok_grondpositie_changelog__woningblok FOREIGN KEY ("woningblok_id") REFERENCES diwi_testset.woningblok("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_grondpositie_changelog_value fk_woningblok_grondpositie_changelog_value__changelog; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_grondpositie_changelog_value
    ADD CONSTRAINT fk_woningblok_grondpositie_changelog_value__changelog FOREIGN KEY ("woningblok_grondpositie_changelog_id") REFERENCES diwi_testset.woningblok_grondpositie_changelog("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_grootte_changelog fk_woningblok_grootte_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_grootte_changelog
    ADD CONSTRAINT fk_woningblok_grootte_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_grootte_changelog fk_woningblok_grootte_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_grootte_changelog
    ADD CONSTRAINT fk_woningblok_grootte_changelog__end_milestone FOREIGN KEY ("end_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_grootte_changelog fk_woningblok_grootte_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_grootte_changelog
    ADD CONSTRAINT fk_woningblok_grootte_changelog__start_milestone FOREIGN KEY ("start_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_grootte_changelog fk_woningblok_grootte_changelog__woningblok; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_grootte_changelog
    ADD CONSTRAINT fk_woningblok_grootte_changelog__woningblok FOREIGN KEY ("woningblok_id") REFERENCES diwi_testset.woningblok("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_kadastrale_koppeling_changelog fk_woningblok_kadastrale_koppeling_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_kadastrale_koppeling_changelog
    ADD CONSTRAINT fk_woningblok_kadastrale_koppeling_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_kadastrale_koppeling_changelog fk_woningblok_kadastrale_koppeling_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_kadastrale_koppeling_changelog
    ADD CONSTRAINT fk_woningblok_kadastrale_koppeling_changelog__end_milestone FOREIGN KEY ("end_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_kadastrale_koppeling_changelog fk_woningblok_kadastrale_koppeling_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_kadastrale_koppeling_changelog
    ADD CONSTRAINT fk_woningblok_kadastrale_koppeling_changelog__start_milestone FOREIGN KEY ("start_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_kadastrale_koppeling_changelog fk_woningblok_kadastrale_koppeling_changelog__woningblok; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_kadastrale_koppeling_changelog
    ADD CONSTRAINT fk_woningblok_kadastrale_koppeling_changelog__woningblok FOREIGN KEY ("woningblok_id") REFERENCES diwi_testset.woningblok("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_kadastrale_koppeling_changelog_value fk_woningblok_kadastrale_koppeling_changelog_value__changelog; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_kadastrale_koppeling_changelog_value
    ADD CONSTRAINT fk_woningblok_kadastrale_koppeling_changelog_value__changelog FOREIGN KEY ("woningblok_kadastrale_koppeling_changelog_id") REFERENCES diwi_testset.woningblok_kadastrale_koppeling_changelog("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_boolean_changelog fk_woningblok_maatwerk_boolean_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_boolean_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_boolean_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_boolean_changelog fk_woningblok_maatwerk_boolean_changelog__eigenschap; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_boolean_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_boolean_changelog__eigenschap FOREIGN KEY ("eigenschap_id") REFERENCES diwi_testset.maatwerk_eigenschap("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_boolean_changelog fk_woningblok_maatwerk_boolean_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_boolean_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_boolean_changelog__end_milestone FOREIGN KEY ("end_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_boolean_changelog fk_woningblok_maatwerk_boolean_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_boolean_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_boolean_changelog__start_milestone FOREIGN KEY ("start_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_boolean_changelog fk_woningblok_maatwerk_boolean_changelog__woningblok; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_boolean_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_boolean_changelog__woningblok FOREIGN KEY ("woningblok_id") REFERENCES diwi_testset.woningblok("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_categorie_changelog fk_woningblok_maatwerk_categorie_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_categorie_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_categorie_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_categorie_changelog fk_woningblok_maatwerk_categorie_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_categorie_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_categorie_changelog__end_milestone FOREIGN KEY ("end_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_categorie_changelog fk_woningblok_maatwerk_categorie_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_categorie_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_categorie_changelog__start_milestone FOREIGN KEY ("start_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_categorie_changelog fk_woningblok_maatwerk_categorie_changelog__woningblok; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_categorie_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_categorie_changelog__woningblok FOREIGN KEY ("woningblok_id") REFERENCES diwi_testset.woningblok("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_categorie_changelog_value fk_woningblok_maatwerk_categorie_changelog_value__changelog; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_categorie_changelog_value
    ADD CONSTRAINT fk_woningblok_maatwerk_categorie_changelog_value__changelog FOREIGN KEY ("woningblok_maatwerk_categorie_changelog_id") REFERENCES diwi_testset.woningblok_maatwerk_categorie_changelog("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_categorie_changelog_value fk_woningblok_maatwerk_categorie_changelog_value__waarde; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_categorie_changelog_value
    ADD CONSTRAINT fk_woningblok_maatwerk_categorie_changelog_value__waarde FOREIGN KEY ("eigenschap_waarde_id") REFERENCES diwi_testset.maatwerk_categorie_waarde("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: maatwerk_categorie_waarde_state fk_woningblok_maatwerk_categorie_waarde_state__categorie_waarde; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.maatwerk_categorie_waarde_state
    ADD CONSTRAINT fk_woningblok_maatwerk_categorie_waarde_state__categorie_waarde FOREIGN KEY ("categorie_waarde_id") REFERENCES diwi_testset.maatwerk_categorie_waarde("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: maatwerk_categorie_waarde_state fk_woningblok_maatwerk_categorie_waarde_state__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.maatwerk_categorie_waarde_state
    ADD CONSTRAINT fk_woningblok_maatwerk_categorie_waarde_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: maatwerk_categorie_waarde_state fk_woningblok_maatwerk_categorie_waarde_state__eigenschap; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.maatwerk_categorie_waarde_state
    ADD CONSTRAINT fk_woningblok_maatwerk_categorie_waarde_state__eigenschap FOREIGN KEY ("maatwerk_eigenschap_id") REFERENCES diwi_testset.maatwerk_eigenschap("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: maatwerk_eigenschap_state fk_woningblok_maatwerk_eigenschap_state__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.maatwerk_eigenschap_state
    ADD CONSTRAINT fk_woningblok_maatwerk_eigenschap_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: maatwerk_eigenschap_state fk_woningblok_maatwerk_eigenschap_state__eigenschap; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.maatwerk_eigenschap_state
    ADD CONSTRAINT fk_woningblok_maatwerk_eigenschap_state__eigenschap FOREIGN KEY ("eigenschap_id") REFERENCES diwi_testset.maatwerk_eigenschap("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_numeriek_changelog fk_woningblok_maatwerk_numeriek_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_numeriek_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_numeriek_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_numeriek_changelog fk_woningblok_maatwerk_numeriek_changelog__eigenschap; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_numeriek_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_numeriek_changelog__eigenschap FOREIGN KEY ("eigenschap_id") REFERENCES diwi_testset.maatwerk_eigenschap("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_numeriek_changelog fk_woningblok_maatwerk_numeriek_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_numeriek_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_numeriek_changelog__end_milestone FOREIGN KEY ("end_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_numeriek_changelog fk_woningblok_maatwerk_numeriek_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_numeriek_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_numeriek_changelog__start_milestone FOREIGN KEY ("start_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_numeriek_changelog fk_woningblok_maatwerk_numeriek_changelog__woningblok; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_numeriek_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_numeriek_changelog__woningblok FOREIGN KEY ("woningblok_id") REFERENCES diwi_testset.woningblok("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_ordinaal_changelog fk_woningblok_maatwerk_ordinaal_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_ordinaal_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_ordinaal_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_ordinaal_changelog fk_woningblok_maatwerk_ordinaal_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_ordinaal_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_ordinaal_changelog__end_milestone FOREIGN KEY ("end_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_ordinaal_changelog fk_woningblok_maatwerk_ordinaal_changelog__max_value; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_ordinaal_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_ordinaal_changelog__max_value FOREIGN KEY ("max_value_id") REFERENCES diwi_testset.maatwerk_ordinaal_waarde("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_ordinaal_changelog fk_woningblok_maatwerk_ordinaal_changelog__min_value; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_ordinaal_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_ordinaal_changelog__min_value FOREIGN KEY ("min_value_id") REFERENCES diwi_testset.maatwerk_ordinaal_waarde("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_ordinaal_changelog fk_woningblok_maatwerk_ordinaal_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_ordinaal_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_ordinaal_changelog__start_milestone FOREIGN KEY ("start_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_ordinaal_changelog fk_woningblok_maatwerk_ordinaal_changelog__value; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_ordinaal_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_ordinaal_changelog__value FOREIGN KEY ("value_id") REFERENCES diwi_testset.maatwerk_ordinaal_waarde("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_ordinaal_changelog fk_woningblok_maatwerk_ordinaal_changelog__woningblok; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_ordinaal_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_ordinaal_changelog__woningblok FOREIGN KEY ("woningblok_id") REFERENCES diwi_testset.woningblok("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: maatwerk_ordinaal_waarde_state fk_woningblok_maatwerk_ordinale_waarde_state__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.maatwerk_ordinaal_waarde_state
    ADD CONSTRAINT fk_woningblok_maatwerk_ordinale_waarde_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: maatwerk_ordinaal_waarde_state fk_woningblok_maatwerk_ordinale_waarde_state__eigenschap; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.maatwerk_ordinaal_waarde_state
    ADD CONSTRAINT fk_woningblok_maatwerk_ordinale_waarde_state__eigenschap FOREIGN KEY ("maatwerk_eigenschap_id") REFERENCES diwi_testset.maatwerk_eigenschap("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: maatwerk_ordinaal_waarde_state fk_woningblok_maatwerk_ordinale_waarde_state__ordinale_waarde; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.maatwerk_ordinaal_waarde_state
    ADD CONSTRAINT fk_woningblok_maatwerk_ordinale_waarde_state__ordinale_waarde FOREIGN KEY ("ordinaal_waarde_id") REFERENCES diwi_testset.maatwerk_ordinaal_waarde("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_mutatie_changelog fk_woningblok_mutatie_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_mutatie_changelog
    ADD CONSTRAINT fk_woningblok_mutatie_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_mutatie_changelog fk_woningblok_mutatie_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_mutatie_changelog
    ADD CONSTRAINT fk_woningblok_mutatie_changelog__end_milestone FOREIGN KEY ("end_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_mutatie_changelog fk_woningblok_mutatie_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_mutatie_changelog
    ADD CONSTRAINT fk_woningblok_mutatie_changelog__start_milestone FOREIGN KEY ("start_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_mutatie_changelog fk_woningblok_mutatie_changelog__woningblok; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_mutatie_changelog
    ADD CONSTRAINT fk_woningblok_mutatie_changelog__woningblok FOREIGN KEY ("woningblok_id") REFERENCES diwi_testset.woningblok("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_mutatie_changelog_soort_value fk_woningblok_mutatie_changelog_soort_value__soort; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_mutatie_changelog_soort_value
    ADD CONSTRAINT fk_woningblok_mutatie_changelog_soort_value__soort FOREIGN KEY ("woningblok_mutatie_changelog_id") REFERENCES diwi_testset.woningblok_mutatie_changelog("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_naam_changelog fk_woningblok_naam_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_naam_changelog
    ADD CONSTRAINT fk_woningblok_naam_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_naam_changelog fk_woningblok_naam_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_naam_changelog
    ADD CONSTRAINT fk_woningblok_naam_changelog__end_milestone FOREIGN KEY ("end_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_naam_changelog fk_woningblok_naam_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_naam_changelog
    ADD CONSTRAINT fk_woningblok_naam_changelog__start_milestone FOREIGN KEY ("start_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_naam_changelog fk_woningblok_naam_changelog__woningblok; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_naam_changelog
    ADD CONSTRAINT fk_woningblok_naam_changelog__woningblok FOREIGN KEY ("woningblok_id") REFERENCES diwi_testset.woningblok("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_programmering_changelog fk_woningblok_programmering_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_programmering_changelog
    ADD CONSTRAINT fk_woningblok_programmering_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_programmering_changelog fk_woningblok_programmering_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_programmering_changelog
    ADD CONSTRAINT fk_woningblok_programmering_changelog__end_milestone FOREIGN KEY ("end_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_programmering_changelog fk_woningblok_programmering_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_programmering_changelog
    ADD CONSTRAINT fk_woningblok_programmering_changelog__start_milestone FOREIGN KEY ("start_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_programmering_changelog fk_woningblok_programmering_changelog__woningblok; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_programmering_changelog
    ADD CONSTRAINT fk_woningblok_programmering_changelog__woningblok FOREIGN KEY ("woningblok_id") REFERENCES diwi_testset.woningblok("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_state fk_woningblok_state__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_state
    ADD CONSTRAINT fk_woningblok_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_state fk_woningblok_state__project; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_state
    ADD CONSTRAINT fk_woningblok_state__project FOREIGN KEY ("project_id") REFERENCES diwi_testset.project("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_state fk_woningblok_state__woningblok; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_state
    ADD CONSTRAINT fk_woningblok_state__woningblok FOREIGN KEY ("woningblok_id") REFERENCES diwi_testset.woningblok("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_type_en_fysiek_changelog fk_woningblok_type_en_fysiek_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_type_en_fysiek_changelog
    ADD CONSTRAINT fk_woningblok_type_en_fysiek_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_type_en_fysiek_changelog fk_woningblok_type_en_fysiek_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_type_en_fysiek_changelog
    ADD CONSTRAINT fk_woningblok_type_en_fysiek_changelog__end_milestone FOREIGN KEY ("end_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_type_en_fysiek_changelog fk_woningblok_type_en_fysiek_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_type_en_fysiek_changelog
    ADD CONSTRAINT fk_woningblok_type_en_fysiek_changelog__start_milestone FOREIGN KEY ("start_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_type_en_fysiek_changelog fk_woningblok_type_en_fysiek_changelog__woningblok; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_type_en_fysiek_changelog
    ADD CONSTRAINT fk_woningblok_type_en_fysiek_changelog__woningblok FOREIGN KEY ("woningblok_id") REFERENCES diwi_testset.woningblok("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_type_en_fysiek_changelog_fysiek_value fk_woningblok_type_en_fysiek_changelog_fysiek_value__changelog; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_type_en_fysiek_changelog_fysiek_value
    ADD CONSTRAINT fk_woningblok_type_en_fysiek_changelog_fysiek_value__changelog FOREIGN KEY ("woningblok_type_en_fysiek_voorkomen_changelog_id") REFERENCES diwi_testset.woningblok_type_en_fysiek_changelog("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_type_en_fysiek_changelog_type_value fk_woningblok_type_en_fysiek_changelog_type_value__changelog; Type: FK CONSTRAINT; Schema: diwi_testset; Owner: vng
--

ALTER TABLE ONLY diwi_testset.woningblok_type_en_fysiek_changelog_type_value
    ADD CONSTRAINT fk_woningblok_type_en_fysiek_changelog_type_value__changelog FOREIGN KEY ("woningblok_type_en_fysiek_voorkomen_changelog_id") REFERENCES diwi_testset.woningblok_type_en_fysiek_changelog("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

