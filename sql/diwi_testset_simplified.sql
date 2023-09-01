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
-- Name: diwi_testset_simplified; Type: SCHEMA; Schema: -; Owner: vng
--

CREATE SCHEMA diwi_testset_simplified;


ALTER SCHEMA diwi_testset_simplified OWNER TO vng;

--
-- Name: conditie_type; Type: TYPE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TYPE diwi_testset_simplified.conditie_type AS ENUM (
    'plan_conditie',
    'doel_conditie'
);


ALTER TYPE diwi_testset_simplified.conditie_type OWNER TO vng;

--
-- Name: confidentiality; Type: TYPE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TYPE diwi_testset_simplified.confidentiality AS ENUM (
    'prive',
    'intern_uitvoering',
    'intern_rapportage',
    'extern_rapportage',
    'openbaar'
);


ALTER TYPE diwi_testset_simplified.confidentiality OWNER TO vng;

--
-- Name: doel_richting; Type: TYPE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TYPE diwi_testset_simplified.doel_richting AS ENUM (
    'minimaal',
    'maximaal'
);


ALTER TYPE diwi_testset_simplified.doel_richting OWNER TO vng;

--
-- Name: doel_soort; Type: TYPE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TYPE diwi_testset_simplified.doel_soort AS ENUM (
    'aantal',
    'percentage'
);


ALTER TYPE diwi_testset_simplified.doel_soort OWNER TO vng;

--
-- Name: doelgroep; Type: TYPE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TYPE diwi_testset_simplified.doelgroep AS ENUM (
    'regulier',
    'jongeren',
    'studenten',
    'ouderen',
    'gehandicapten_en_zorg',
    'grote_gezinnen'
);


ALTER TYPE diwi_testset_simplified.doelgroep OWNER TO vng;

--
-- Name: eigendom_soort; Type: TYPE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TYPE diwi_testset_simplified.eigendom_soort AS ENUM (
    'koopwoning',
    'huurwoning_particuliere_verhuurder',
    'huurwoning_woningcorporatie'
);


ALTER TYPE diwi_testset_simplified.eigendom_soort OWNER TO vng;

--
-- Name: fysiek_voorkomen; Type: TYPE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TYPE diwi_testset_simplified.fysiek_voorkomen AS ENUM (
    'tussenwoning',
    'hoekwoning',
    'twee_onder_een_kap',
    'vrijstaand',
    'portiekflat',
    'gallerijflat'
);


ALTER TYPE diwi_testset_simplified.fysiek_voorkomen OWNER TO vng;

--
-- Name: grondpositie; Type: TYPE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TYPE diwi_testset_simplified.grondpositie AS ENUM (
    'formele_toestemming_grondeigenaar',
    'intentie_medewerking_grondeigenaar',
    'geen_toestemming_grondeigenaar'
);


ALTER TYPE diwi_testset_simplified.grondpositie OWNER TO vng;

--
-- Name: maatwerk_eigenschap_type; Type: TYPE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TYPE diwi_testset_simplified.maatwerk_eigenschap_type AS ENUM (
    'boolean',
    'category',
    'ordinal',
    'numeric'
);


ALTER TYPE diwi_testset_simplified.maatwerk_eigenschap_type OWNER TO vng;

--
-- Name: maatwerk_object_soort; Type: TYPE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TYPE diwi_testset_simplified.maatwerk_object_soort AS ENUM (
    'project',
    'woningblok'
);


ALTER TYPE diwi_testset_simplified.maatwerk_object_soort OWNER TO vng;

--
-- Name: milestone_status; Type: TYPE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TYPE diwi_testset_simplified.milestone_status AS ENUM (
    'voorspeld',
    'gepland',
    'gerealiseerd'
);


ALTER TYPE diwi_testset_simplified.milestone_status OWNER TO vng;

--
-- Name: mutatie_soort; Type: TYPE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TYPE diwi_testset_simplified.mutatie_soort AS ENUM (
    'bouw',
    'sloop',
    'transformatie',
    'splitsing'
);


ALTER TYPE diwi_testset_simplified.mutatie_soort OWNER TO vng;

--
-- Name: plan_type; Type: TYPE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TYPE diwi_testset_simplified.plan_type AS ENUM (
    'pand_transformatie',
    'transformatiegebied',
    'herstructurering',
    'verdichting',
    'uitbreiding_uitleg',
    'uitbreiding_overig'
);


ALTER TYPE diwi_testset_simplified.plan_type OWNER TO vng;

--
-- Name: planologische_planstatus; Type: TYPE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TYPE diwi_testset_simplified.planologische_planstatus AS ENUM (
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


ALTER TYPE diwi_testset_simplified.planologische_planstatus OWNER TO vng;

--
-- Name: project_phase; Type: TYPE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TYPE diwi_testset_simplified.project_phase AS ENUM (
    '1_Initiatieffase',
    '2_projectfase',
    '3_vergunningsfase',
    '4_realisatiefase',
    '5_opleveringsfase'
);


ALTER TYPE diwi_testset_simplified.project_phase OWNER TO vng;

--
-- Name: software_module; Type: TYPE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TYPE diwi_testset_simplified.software_module AS ENUM (
    'admin_panel',
    'beheer_portaal',
    'dashboard_publiek',
    'dashboard_provinciale_planmonitor',
    'dashboard_gemeenteraad',
    'dashboard_interne_uitvoering'
);


ALTER TYPE diwi_testset_simplified.software_module OWNER TO vng;

--
-- Name: software_rights; Type: TYPE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TYPE diwi_testset_simplified.software_rights AS ENUM (
    'admin',
    'crud_to_own',
    'crud_to_all',
    'view_only'
);


ALTER TYPE diwi_testset_simplified.software_rights OWNER TO vng;

--
-- Name: woning_type; Type: TYPE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TYPE diwi_testset_simplified.woning_type AS ENUM (
    'eengezinswoning',
    'meergezinswoning'
);


ALTER TYPE diwi_testset_simplified.woning_type OWNER TO vng;

--
-- Name: set_end_date_now(); Type: FUNCTION; Schema: diwi_testset_simplified; Owner: vng
--

CREATE FUNCTION diwi_testset_simplified.set_end_date_now() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
	 DECLARE BEGIN
	 IF OLD.change_end_date IS NULL THEN
        OLD.change_end_date = NOW();
        NEW."ID" = 10;
        INSERT INTO diwi.project_priorisering_value_state SELECT NEW.*;
	 END If;
	 RETURN OLD;
	END;
	$$;


ALTER FUNCTION diwi_testset_simplified.set_end_date_now() OWNER TO vng;

--
-- Name: set_start_date_now(); Type: FUNCTION; Schema: diwi_testset_simplified; Owner: vng
--

CREATE FUNCTION diwi_testset_simplified.set_start_date_now() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
	 DECLARE BEGIN
	 NEW.change_start_date = NOW();
	 RETURN NEW;
	END;
	$$;


ALTER FUNCTION diwi_testset_simplified.set_start_date_now() OWNER TO vng;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: actor; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.actor (
    "ID" integer NOT NULL
);


ALTER TABLE diwi_testset_simplified.actor OWNER TO vng;

--
-- Name: actor_state; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.actor_state (
    "ID" integer NOT NULL,
    "actor_ID" integer NOT NULL,
    name text NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    "organization_ID" integer
);


ALTER TABLE diwi_testset_simplified.actor_state OWNER TO vng;

--
-- Name: buurt; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.buurt (
    "ID" integer NOT NULL
);


ALTER TABLE diwi_testset_simplified.buurt OWNER TO vng;

--
-- Name: buurt_state; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.buurt_state (
    "ID" integer NOT NULL,
    "buurt_ID" integer NOT NULL,
    "wijk_ID" integer NOT NULL,
    waarde_label text NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset_simplified.buurt_state OWNER TO vng;

--
-- Name: document; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.document (
    "ID" integer NOT NULL
);


ALTER TABLE diwi_testset_simplified.document OWNER TO vng;

--
-- Name: document_soort; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.document_soort (
    "ID" integer NOT NULL
);


ALTER TABLE diwi_testset_simplified.document_soort OWNER TO vng;

--
-- Name: document_soort_state; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.document_soort_state (
    "ID" integer NOT NULL,
    "document_soort_ID" integer NOT NULL,
    waarde_label text NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset_simplified.document_soort_state OWNER TO vng;

--
-- Name: document_state; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.document_state (
    "ID" integer NOT NULL,
    "document_ID" integer NOT NULL,
    "milestone_ID" integer NOT NULL,
    "document_soort_ID" integer,
    naam text NOT NULL,
    notitie text,
    file_path text,
    "owner_organization_ID" integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    confidentiality_level diwi_testset_simplified.confidentiality NOT NULL
);


ALTER TABLE diwi_testset_simplified.document_state OWNER TO vng;

--
-- Name: gemeente; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.gemeente (
    "ID" integer NOT NULL
);


ALTER TABLE diwi_testset_simplified.gemeente OWNER TO vng;

--
-- Name: gemeente_state; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.gemeente_state (
    "ID" integer NOT NULL,
    "gemeente_ID" integer NOT NULL,
    waarde_label text NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset_simplified.gemeente_state OWNER TO vng;

--
-- Name: maatwerk_categorie_waarde; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.maatwerk_categorie_waarde (
    "ID" integer NOT NULL
);


ALTER TABLE diwi_testset_simplified.maatwerk_categorie_waarde OWNER TO vng;

--
-- Name: maatwerk_categorie_waarde_state; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.maatwerk_categorie_waarde_state (
    "ID" integer NOT NULL,
    "categorie_waarde_ID" integer NOT NULL,
    "maatwerk_eigenschap_ID" integer NOT NULL,
    waarde_label text NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset_simplified.maatwerk_categorie_waarde_state OWNER TO vng;

--
-- Name: maatwerk_eigenschap; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.maatwerk_eigenschap (
    "ID" integer NOT NULL
);


ALTER TABLE diwi_testset_simplified.maatwerk_eigenschap OWNER TO vng;

--
-- Name: maatwerk_eigenschap_state; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.maatwerk_eigenschap_state (
    "ID" integer NOT NULL,
    eigenschap_naam text NOT NULL,
    eigenschap_type diwi_testset_simplified.maatwerk_eigenschap_type NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    "eigenschap_ID" integer NOT NULL,
    eigenschap_object_soort diwi_testset_simplified.maatwerk_object_soort NOT NULL
);


ALTER TABLE diwi_testset_simplified.maatwerk_eigenschap_state OWNER TO vng;

--
-- Name: maatwerk_ordinaal_waarde; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.maatwerk_ordinaal_waarde (
    "ID" integer NOT NULL
);


ALTER TABLE diwi_testset_simplified.maatwerk_ordinaal_waarde OWNER TO vng;

--
-- Name: maatwerk_ordinaal_waarde_state; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.maatwerk_ordinaal_waarde_state (
    "ID" integer NOT NULL,
    "ordinaal_waarde_ID" integer NOT NULL,
    "maatwerk_eigenschap_ID" integer NOT NULL,
    waarde_label text NOT NULL,
    ordinaal_niveau integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset_simplified.maatwerk_ordinaal_waarde_state OWNER TO vng;

--
-- Name: milestone; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.milestone (
    "ID" integer NOT NULL
);


ALTER TABLE diwi_testset_simplified.milestone OWNER TO vng;

--
-- Name: milestone_state; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.milestone_state (
    "ID" integer NOT NULL,
    "milestone_ID" integer NOT NULL,
    date date NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    status diwi_testset_simplified.milestone_status NOT NULL,
    omschrijving text
);


ALTER TABLE diwi_testset_simplified.milestone_state OWNER TO vng;

--
-- Name: organization; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.organization (
    "ID" integer NOT NULL
);


ALTER TABLE diwi_testset_simplified.organization OWNER TO vng;

--
-- Name: organization_state; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.organization_state (
    "ID" integer NOT NULL,
    "organization_ID" integer NOT NULL,
    "parent_organization_ID" integer,
    naam text NOT NULL,
    change_end_date timestamp with time zone,
    change_start_date timestamp with time zone NOT NULL,
    "change_user_ID" integer NOT NULL
);


ALTER TABLE diwi_testset_simplified.organization_state OWNER TO vng;

--
-- Name: plan; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.plan (
    "ID" integer NOT NULL
);


ALTER TABLE diwi_testset_simplified.plan OWNER TO vng;

--
-- Name: plan_conditie; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.plan_conditie (
    "ID" integer NOT NULL
);


ALTER TABLE diwi_testset_simplified.plan_conditie OWNER TO vng;

--
-- Name: plan_conditie_buurt; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.plan_conditie_buurt (
    "ID" integer NOT NULL,
    "plan_conditie_ID" integer NOT NULL,
    "buurt_ID" integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset_simplified.plan_conditie_buurt OWNER TO vng;

--
-- Name: plan_conditie_doelgroep; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.plan_conditie_doelgroep (
    "ID" integer NOT NULL,
    "plan_conditie_ID" integer NOT NULL,
    doelgroep diwi_testset_simplified.doelgroep NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset_simplified.plan_conditie_doelgroep OWNER TO vng;

--
-- Name: plan_conditie_eigendom_en_waarde; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.plan_conditie_eigendom_en_waarde (
    "ID" integer NOT NULL,
    "plan_conditie_ID" integer NOT NULL,
    eigendom_soort diwi_testset_simplified.eigendom_soort,
    waarde money,
    huurbedrag money,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset_simplified.plan_conditie_eigendom_en_waarde OWNER TO vng;

--
-- Name: plan_conditie_gemeente; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.plan_conditie_gemeente (
    "ID" integer NOT NULL,
    "plan_conditie_ID" integer NOT NULL,
    "gemeente_ID" integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset_simplified.plan_conditie_gemeente OWNER TO vng;

--
-- Name: plan_conditie_geografie; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.plan_conditie_geografie (
    "ID" integer NOT NULL,
    "plan_conditie_ID" integer NOT NULL,
    brk_gemeente_code text NOT NULL,
    brk_sctie text,
    brk_perceelnummer integer,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset_simplified.plan_conditie_geografie OWNER TO vng;

--
-- Name: plan_conditie_grondpositie; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.plan_conditie_grondpositie (
    "ID" integer NOT NULL,
    "plan_conditie_ID" integer NOT NULL,
    grondpositie diwi_testset_simplified.grondpositie NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset_simplified.plan_conditie_grondpositie OWNER TO vng;

--
-- Name: plan_conditie_grootte; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.plan_conditie_grootte (
    "ID" integer NOT NULL,
    "plan_conditie_ID" integer NOT NULL,
    grootte integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset_simplified.plan_conditie_grootte OWNER TO vng;

--
-- Name: plan_conditie_maatwerk_boolean; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.plan_conditie_maatwerk_boolean (
    "ID" integer NOT NULL,
    "plan_conditie_ID" integer NOT NULL,
    eigenschap_waarde boolean NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset_simplified.plan_conditie_maatwerk_boolean OWNER TO vng;

--
-- Name: plan_conditie_maatwerk_categorie; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.plan_conditie_maatwerk_categorie (
    "ID" integer NOT NULL,
    "plan_conditie_ID" integer NOT NULL,
    "eigenschap_waarde_ID" integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset_simplified.plan_conditie_maatwerk_categorie OWNER TO vng;

--
-- Name: plan_conditie_maatwerk_numeriek; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.plan_conditie_maatwerk_numeriek (
    "ID" integer NOT NULL,
    "plan_conditie_ID" integer NOT NULL,
    eigenschap_waarde double precision NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset_simplified.plan_conditie_maatwerk_numeriek OWNER TO vng;

--
-- Name: plan_conditie_maatwerk_ordinaal; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.plan_conditie_maatwerk_ordinaal (
    "ID" integer NOT NULL,
    "plan_conditie_ID" integer NOT NULL,
    "eigenschap_waarde_ID" integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset_simplified.plan_conditie_maatwerk_ordinaal OWNER TO vng;

--
-- Name: plan_conditie_programmering; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.plan_conditie_programmering (
    "ID" integer NOT NULL,
    "plan_conditie_ID" integer NOT NULL,
    programmering boolean NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset_simplified.plan_conditie_programmering OWNER TO vng;

--
-- Name: plan_conditie_state; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.plan_conditie_state (
    "ID" integer NOT NULL,
    "plan_conditie_ID" integer NOT NULL,
    "plan_ID" integer NOT NULL,
    conditie_type diwi_testset_simplified.conditie_type NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset_simplified.plan_conditie_state OWNER TO vng;

--
-- Name: plan_conditie_type_en_fysiek_voorkomen; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.plan_conditie_type_en_fysiek_voorkomen (
    "ID" integer NOT NULL,
    "plan_conditie_ID" integer NOT NULL,
    woning_type diwi_testset_simplified.woning_type NOT NULL,
    fysiek_voorkomen diwi_testset_simplified.fysiek_voorkomen,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset_simplified.plan_conditie_type_en_fysiek_voorkomen OWNER TO vng;

--
-- Name: plan_conditie_wijk; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.plan_conditie_wijk (
    "ID" integer NOT NULL,
    "plan_conditie_ID" integer NOT NULL,
    "wijk_ID" integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset_simplified.plan_conditie_wijk OWNER TO vng;

--
-- Name: plan_soort; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.plan_soort (
    "ID" integer NOT NULL
);


ALTER TABLE diwi_testset_simplified.plan_soort OWNER TO vng;

--
-- Name: plan_soort_state; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.plan_soort_state (
    "ID" integer NOT NULL,
    "plan_soort_ID" integer NOT NULL,
    waarde_label text NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset_simplified.plan_soort_state OWNER TO vng;

--
-- Name: plan_state; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.plan_state (
    "ID" integer NOT NULL,
    "plan_ID" integer NOT NULL,
    "plan_soort_ID" integer,
    naam text NOT NULL,
    deadline date NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    confidentiality_level diwi_testset_simplified.confidentiality NOT NULL,
    "owner_organization_ID" integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    doel_soort diwi_testset_simplified.doel_soort NOT NULL,
    doel_richting diwi_testset_simplified.doel_richting NOT NULL,
    doel_waarde double precision NOT NULL,
    start_datum date NOT NULL
);


ALTER TABLE diwi_testset_simplified.plan_state OWNER TO vng;

--
-- Name: project; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.project (
    "ID" integer NOT NULL
);


ALTER TABLE diwi_testset_simplified.project OWNER TO vng;

--
-- Name: project_actor_rol_changelog; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.project_actor_rol_changelog (
    "ID" integer NOT NULL,
    "start_milestone_ID" integer NOT NULL,
    "end_milestone_ID" integer NOT NULL,
    "project_ID" integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    "actor_ID" integer NOT NULL,
    "project_actor_rol_ID" integer NOT NULL
);


ALTER TABLE diwi_testset_simplified.project_actor_rol_changelog OWNER TO vng;

--
-- Name: project_actor_rol_value; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.project_actor_rol_value (
    "ID" integer NOT NULL
);


ALTER TABLE diwi_testset_simplified.project_actor_rol_value OWNER TO vng;

--
-- Name: project_actor_rol_value_state; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.project_actor_rol_value_state (
    "ID" integer NOT NULL,
    "project_actor_rol_value_ID" integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    value_label text NOT NULL
);


ALTER TABLE diwi_testset_simplified.project_actor_rol_value_state OWNER TO vng;

--
-- Name: project_duration_changelog; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.project_duration_changelog (
    "ID" integer NOT NULL,
    "start_milestone_ID" integer NOT NULL,
    "end_milestone_ID" integer NOT NULL,
    "project_ID" integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset_simplified.project_duration_changelog OWNER TO vng;

--
-- Name: project_fase_changelog; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.project_fase_changelog (
    "ID" integer NOT NULL,
    "start_milestone_ID" integer NOT NULL,
    "end_milestone_ID" integer NOT NULL,
    "project_ID" integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    project_fase diwi_testset_simplified.project_phase NOT NULL
);


ALTER TABLE diwi_testset_simplified.project_fase_changelog OWNER TO vng;

--
-- Name: project_gemeenterol_changelog; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.project_gemeenterol_changelog (
    "ID" integer NOT NULL,
    "start_milestone_ID" integer NOT NULL,
    "end_milestone_ID" integer NOT NULL,
    "project_ID" integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    "project_gemeenterol_value_ID" integer NOT NULL
);


ALTER TABLE diwi_testset_simplified.project_gemeenterol_changelog OWNER TO vng;

--
-- Name: project_gemeenterol_value; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.project_gemeenterol_value (
    "ID" integer NOT NULL
);


ALTER TABLE diwi_testset_simplified.project_gemeenterol_value OWNER TO vng;

--
-- Name: project_gemeenterol_value_state; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.project_gemeenterol_value_state (
    "ID" integer NOT NULL,
    value_label text NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    "project_gemeenterol_value_ID" integer NOT NULL
);


ALTER TABLE diwi_testset_simplified.project_gemeenterol_value_state OWNER TO vng;

--
-- Name: project_maatwerk_boolean_eigenschap_changelog; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.project_maatwerk_boolean_eigenschap_changelog (
    "ID" integer NOT NULL,
    "start_milestone_ID" integer NOT NULL,
    "end_milestone_ID" integer NOT NULL,
    "project_ID" integer NOT NULL,
    eigenschap_waarde boolean,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    "eigenschap_ID" integer NOT NULL
);


ALTER TABLE diwi_testset_simplified.project_maatwerk_boolean_eigenschap_changelog OWNER TO vng;

--
-- Name: project_maatwerk_categorie_eigenschap_changelog; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.project_maatwerk_categorie_eigenschap_changelog (
    "ID" integer NOT NULL,
    "start_milestone_ID" integer NOT NULL,
    "end_milestone_ID" integer NOT NULL,
    "project_ID" integer NOT NULL,
    "eigenschap_waarde_ID" integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset_simplified.project_maatwerk_categorie_eigenschap_changelog OWNER TO vng;

--
-- Name: project_maatwerk_numeriek_eigenschap_changelog; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.project_maatwerk_numeriek_eigenschap_changelog (
    "ID" integer NOT NULL,
    "start_milestone_ID" integer NOT NULL,
    "end_milestone_ID" integer NOT NULL,
    "project_ID" integer NOT NULL,
    eigenschap_waarde double precision,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    "eigenschap_ID" integer NOT NULL
);


ALTER TABLE diwi_testset_simplified.project_maatwerk_numeriek_eigenschap_changelog OWNER TO vng;

--
-- Name: project_maatwerk_ordinaal_eigenschap_changelog; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.project_maatwerk_ordinaal_eigenschap_changelog (
    "ID" integer NOT NULL,
    "start_milestone_ID" integer NOT NULL,
    "end_milestone_ID" integer NOT NULL,
    "project_ID" integer NOT NULL,
    "eigenschap_waarde_ID" integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset_simplified.project_maatwerk_ordinaal_eigenschap_changelog OWNER TO vng;

--
-- Name: project_name_changelog; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.project_name_changelog (
    "ID" integer NOT NULL,
    "start_milestone_ID" integer NOT NULL,
    "end_milestone_ID" integer NOT NULL,
    "project_ID" integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    name text
);


ALTER TABLE diwi_testset_simplified.project_name_changelog OWNER TO vng;

--
-- Name: project_plan_type_changelog; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.project_plan_type_changelog (
    "ID" integer NOT NULL,
    "start_milestone_ID" integer NOT NULL,
    "end_milestone_ID" integer NOT NULL,
    "project_ID" integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    plan_type diwi_testset_simplified.plan_type
);


ALTER TABLE diwi_testset_simplified.project_plan_type_changelog OWNER TO vng;

--
-- Name: project_planologische_planstatus_changelog; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.project_planologische_planstatus_changelog (
    "ID" integer NOT NULL,
    "start_milestone_ID" integer NOT NULL,
    "end_milestone_ID" integer NOT NULL,
    "project_ID" integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    planologische_planstatus diwi_testset_simplified.planologische_planstatus NOT NULL
);


ALTER TABLE diwi_testset_simplified.project_planologische_planstatus_changelog OWNER TO vng;

--
-- Name: project_priorisering_changelog; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.project_priorisering_changelog (
    "ID" integer NOT NULL,
    "start_milestone_ID" integer NOT NULL,
    "end_milestone_ID" integer NOT NULL,
    "project_ID" integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    "project_priorisering_value_ID" integer NOT NULL
);


ALTER TABLE diwi_testset_simplified.project_priorisering_changelog OWNER TO vng;

--
-- Name: project_priorisering_value; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.project_priorisering_value (
    "ID" integer NOT NULL
);


ALTER TABLE diwi_testset_simplified.project_priorisering_value OWNER TO vng;

--
-- Name: project_priorisering_value_state; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.project_priorisering_value_state (
    "ID" integer NOT NULL,
    value_label text NOT NULL,
    ordinal_level integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    "project_priorisering_value_ID" integer NOT NULL
);


ALTER TABLE diwi_testset_simplified.project_priorisering_value_state OWNER TO vng;

--
-- Name: project_programmering_changelog; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.project_programmering_changelog (
    "ID" integer NOT NULL,
    "project_ID" integer NOT NULL,
    "start_milestone_ID" integer NOT NULL,
    "end_milestone_ID" integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    programmering boolean
);


ALTER TABLE diwi_testset_simplified.project_programmering_changelog OWNER TO vng;

--
-- Name: project_state; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.project_state (
    "ID" integer NOT NULL,
    "project_ID" integer NOT NULL,
    "owner_organization_ID" integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    confidentiality_level diwi_testset_simplified.confidentiality NOT NULL,
    project_colour text NOT NULL
);


ALTER TABLE diwi_testset_simplified.project_state OWNER TO vng;

--
-- Name: software_module_rights; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.software_module_rights (
    "ID" integer NOT NULL,
    "organization_ID" integer NOT NULL,
    software_module diwi_testset_simplified.software_module NOT NULL,
    confidentiality_level diwi_testset_simplified.confidentiality NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    software_rights diwi_testset_simplified.software_rights NOT NULL
);


ALTER TABLE diwi_testset_simplified.software_module_rights OWNER TO vng;

--
-- Name: user; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified."user" (
    "ID" integer NOT NULL
);


ALTER TABLE diwi_testset_simplified."user" OWNER TO vng;

--
-- Name: user_state; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.user_state (
    "ID" integer NOT NULL,
    "user_ID" integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    "identity_provider_ID" text NOT NULL
);


ALTER TABLE diwi_testset_simplified.user_state OWNER TO vng;

--
-- Name: user_to_organization; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.user_to_organization (
    "ID" integer NOT NULL,
    "user_ID" integer NOT NULL,
    "organization_ID" integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset_simplified.user_to_organization OWNER TO vng;

--
-- Name: wijk; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.wijk (
    "ID" integer NOT NULL
);


ALTER TABLE diwi_testset_simplified.wijk OWNER TO vng;

--
-- Name: wijk_state; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.wijk_state (
    "ID" integer NOT NULL,
    "wijk_ID" integer NOT NULL,
    "gemeente_ID" integer NOT NULL,
    waarde_label text NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset_simplified.wijk_state OWNER TO vng;

--
-- Name: woningblok; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.woningblok (
    "ID" integer NOT NULL
);


ALTER TABLE diwi_testset_simplified.woningblok OWNER TO vng;

--
-- Name: woningblok_buurt_changelog; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.woningblok_buurt_changelog (
    "ID" integer NOT NULL,
    "woningblok_ID" integer NOT NULL,
    "start_milestone_ID" integer NOT NULL,
    "end_milestone_ID" integer NOT NULL,
    "buurt_ID" integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset_simplified.woningblok_buurt_changelog OWNER TO vng;

--
-- Name: woningblok_doelgroep_changelog; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.woningblok_doelgroep_changelog (
    "ID" integer NOT NULL,
    "woningblok_ID" integer NOT NULL,
    "start_milestone_ID" integer NOT NULL,
    "end_milestone_ID" integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    doelgroep diwi_testset_simplified.doelgroep
);


ALTER TABLE diwi_testset_simplified.woningblok_doelgroep_changelog OWNER TO vng;

--
-- Name: woningblok_duration_changelog; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.woningblok_duration_changelog (
    "ID" integer NOT NULL,
    "woningblok_ID" integer NOT NULL,
    "start_milestone_ID" integer NOT NULL,
    "end_milestone_ID" integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset_simplified.woningblok_duration_changelog OWNER TO vng;

--
-- Name: woningblok_eigendom_en_waarde_changelog; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.woningblok_eigendom_en_waarde_changelog (
    "ID" integer NOT NULL,
    "woningblok_ID" integer NOT NULL,
    "start_milestone_ID" integer NOT NULL,
    "end_milestone_ID" integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    eigendom_soort diwi_testset_simplified.eigendom_soort,
    waarde money,
    huurbedrag money
);


ALTER TABLE diwi_testset_simplified.woningblok_eigendom_en_waarde_changelog OWNER TO vng;

--
-- Name: woningblok_gemeente_changelog; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.woningblok_gemeente_changelog (
    "ID" integer NOT NULL,
    "woningblok_ID" integer NOT NULL,
    "start_milestone_ID" integer NOT NULL,
    "end_milestone_ID" integer NOT NULL,
    "gemeente_ID" integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset_simplified.woningblok_gemeente_changelog OWNER TO vng;

--
-- Name: woningblok_grondpositie_changelog; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.woningblok_grondpositie_changelog (
    "ID" integer NOT NULL,
    "woningblok_ID" integer NOT NULL,
    "start_milestone_ID" integer NOT NULL,
    "end_milestone_ID" integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    grondpositie diwi_testset_simplified.grondpositie
);


ALTER TABLE diwi_testset_simplified.woningblok_grondpositie_changelog OWNER TO vng;

--
-- Name: woningblok_grootte_changelog; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.woningblok_grootte_changelog (
    "ID" integer NOT NULL,
    "woningblok_ID" integer NOT NULL,
    "start_milestone_ID" integer NOT NULL,
    "end_milestone_ID" integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    grootte integer
);


ALTER TABLE diwi_testset_simplified.woningblok_grootte_changelog OWNER TO vng;

--
-- Name: woningblok_kadastrale_koppeling_changelog; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.woningblok_kadastrale_koppeling_changelog (
    "ID" integer NOT NULL,
    "woningblok_ID" integer NOT NULL,
    "start_milestone_ID" integer NOT NULL,
    "end_milestone_ID" integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    brk_gemeente_code text NOT NULL,
    brk_sectie text,
    brk_perceelnummer integer
);


ALTER TABLE diwi_testset_simplified.woningblok_kadastrale_koppeling_changelog OWNER TO vng;

--
-- Name: woningblok_maatwerk_boolean_eigenschap_changelog; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.woningblok_maatwerk_boolean_eigenschap_changelog (
    "ID" integer NOT NULL,
    "start_milestone_ID" integer NOT NULL,
    "end_milestone_ID" integer NOT NULL,
    "woningblok_ID" integer NOT NULL,
    eigenschap_waarde boolean,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    "eigenschap_ID" integer NOT NULL
);


ALTER TABLE diwi_testset_simplified.woningblok_maatwerk_boolean_eigenschap_changelog OWNER TO vng;

--
-- Name: woningblok_maatwerk_categorie_eigenschap_changelog; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.woningblok_maatwerk_categorie_eigenschap_changelog (
    "ID" integer NOT NULL,
    "start_milestone_ID" integer NOT NULL,
    "end_milestone_ID" integer NOT NULL,
    "woningblok_ID" integer NOT NULL,
    "eigenschap_waarde_ID" integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset_simplified.woningblok_maatwerk_categorie_eigenschap_changelog OWNER TO vng;

--
-- Name: woningblok_maatwerk_numeriek_eigenschap_changelog; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.woningblok_maatwerk_numeriek_eigenschap_changelog (
    "ID" integer NOT NULL,
    "start_milestone_ID" integer NOT NULL,
    "end_milestone_ID" integer NOT NULL,
    "woningblok_ID" integer NOT NULL,
    eigenschap_waarde double precision,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    "eigenschap_ID" integer NOT NULL
);


ALTER TABLE diwi_testset_simplified.woningblok_maatwerk_numeriek_eigenschap_changelog OWNER TO vng;

--
-- Name: woningblok_maatwerk_ordinaal_eigenschap_changelog; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.woningblok_maatwerk_ordinaal_eigenschap_changelog (
    "ID" integer NOT NULL,
    "start_milestone_ID" integer NOT NULL,
    "end_milestone_ID" integer NOT NULL,
    "woningblok_ID" integer NOT NULL,
    "eigenschap_waarde_ID" integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset_simplified.woningblok_maatwerk_ordinaal_eigenschap_changelog OWNER TO vng;

--
-- Name: woningblok_mutatie_changelog; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.woningblok_mutatie_changelog (
    "ID" integer NOT NULL,
    "woningblok_ID" integer NOT NULL,
    "start_milestone_ID" integer NOT NULL,
    "end_milestone_ID" integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    mutatie_soort diwi_testset_simplified.mutatie_soort NOT NULL,
    bruto_plancapaciteit integer,
    sloop integer,
    netto_plancapaciteit integer
);


ALTER TABLE diwi_testset_simplified.woningblok_mutatie_changelog OWNER TO vng;

--
-- Name: woningblok_naam_changelog; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.woningblok_naam_changelog (
    "ID" integer NOT NULL,
    "woningblok_ID" integer NOT NULL,
    "start_milestone_ID" integer NOT NULL,
    "end_milestone_ID" integer NOT NULL,
    naam text NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset_simplified.woningblok_naam_changelog OWNER TO vng;

--
-- Name: woningblok_state; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.woningblok_state (
    "ID" integer NOT NULL,
    "woningblok_ID" integer NOT NULL,
    "project_ID" integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset_simplified.woningblok_state OWNER TO vng;

--
-- Name: woningblok_type_en_fysiek_voorkomen_changelog; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.woningblok_type_en_fysiek_voorkomen_changelog (
    "ID" integer NOT NULL,
    "woningblok_ID" integer NOT NULL,
    "start_milestone_ID" integer NOT NULL,
    "end_milestone_ID" integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    woning_type diwi_testset_simplified.woning_type,
    fysiek_voorkomen diwi_testset_simplified.fysiek_voorkomen
);


ALTER TABLE diwi_testset_simplified.woningblok_type_en_fysiek_voorkomen_changelog OWNER TO vng;

--
-- Name: woningblok_wijk_changelog; Type: TABLE; Schema: diwi_testset_simplified; Owner: vng
--

CREATE TABLE diwi_testset_simplified.woningblok_wijk_changelog (
    "ID" integer NOT NULL,
    "woningblok_ID" integer NOT NULL,
    "start_milestone_ID" integer NOT NULL,
    "end_milestone_ID" integer NOT NULL,
    "wijk_ID" integer NOT NULL,
    "change_user_ID" integer NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);


ALTER TABLE diwi_testset_simplified.woningblok_wijk_changelog OWNER TO vng;

--
-- Data for Name: actor; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.actor VALUES
	(1),
	(2),
	(3),
	(4),
	(5);


--
-- Data for Name: actor_state; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.actor_state VALUES
	(1, 1, 'projectleider_1', 11, '2023-08-21 12:55:19+02', NULL, 7),
	(2, 2, 'projectleider_2', 11, '2023-08-21 12:55:19+02', NULL, 8),
	(3, 3, 'projectleider_3', 11, '2023-08-21 12:55:19+02', NULL, 9),
	(4, 4, 'woonbedrijf', 11, '2023-08-21 12:55:19+02', NULL, 5),
	(5, 5, 'Truienbrijers Vastgoed', 9, '2023-08-25 11:51:37+02', NULL, NULL);


--
-- Data for Name: buurt; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--



--
-- Data for Name: buurt_state; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--



--
-- Data for Name: document; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--



--
-- Data for Name: document_soort; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--



--
-- Data for Name: document_soort_state; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--



--
-- Data for Name: document_state; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--



--
-- Data for Name: gemeente; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.gemeente VALUES
	(1);


--
-- Data for Name: gemeente_state; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.gemeente_state VALUES
	(1, 1, 'westvoorne', 11, '2023-08-21 12:55:19+02', NULL);


--
-- Data for Name: maatwerk_categorie_waarde; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.maatwerk_categorie_waarde VALUES
	(1),
	(2),
	(3),
	(4),
	(5),
	(6),
	(7),
	(8),
	(9),
	(10),
	(11),
	(12),
	(13),
	(14),
	(15);


--
-- Data for Name: maatwerk_categorie_waarde_state; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.maatwerk_categorie_waarde_state VALUES
	(1, 1, 1, '1_centrum_stedelijk', 11, '2023-08-21 12:55:19+02', NULL),
	(2, 2, 1, '2_stedelijk_buiten_centrum', 11, '2023-08-21 12:55:19+02', NULL),
	(3, 3, 1, '3_groen_stedelijk', 11, '2023-08-21 12:55:19+02', NULL),
	(4, 4, 1, '4_centrum_dorps', 11, '2023-08-21 12:55:19+02', NULL),
	(5, 5, 1, '5_landelijk_wonen', 11, '2023-08-21 12:55:19+02', NULL),
	(6, 6, 1, '6_werkgebied', 11, '2023-08-21 12:55:19+02', NULL),
	(7, 7, 3, 'ja', 11, '2023-08-21 12:55:19+02', NULL),
	(8, 8, 3, 'nee', 11, '2023-08-21 12:55:19+02', NULL),
	(9, 9, 3, 'ruimte_voor_ruimte', 11, '2023-08-21 12:55:19+02', NULL),
	(10, 10, 4, 'gemeente', 11, '2023-08-24 17:11:57+02', NULL),
	(11, 11, 4, 'particulieren', 11, '2023-08-24 17:11:57+02', NULL),
	(12, 12, 4, 'projectontwikkelaar', 11, '2023-08-24 17:11:57+02', NULL),
	(13, 13, 4, 'woningbouwcorporatie', 11, '2023-08-24 17:11:57+02', NULL),
	(14, 14, 4, 'meerdere', 11, '2023-08-24 17:11:57+02', NULL),
	(15, 15, 4, 'anders', 11, '2023-08-24 17:11:57+02', NULL);


--
-- Data for Name: maatwerk_eigenschap; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.maatwerk_eigenschap VALUES
	(1),
	(2),
	(3),
	(4);


--
-- Data for Name: maatwerk_eigenschap_state; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.maatwerk_eigenschap_state VALUES
	(1, 'woonmilieu_abf-6', 'category', 11, '2023-08-21 12:55:19+02', NULL, 1, 'project'),
	(2, 'categorie_woonvisie', 'ordinal', 11, '2023-08-21 12:55:19+02', NULL, 2, 'project'),
	(3, 'regionale_planlijst', 'category', 11, '2023-08-21 12:55:19+02', NULL, 3, 'project'),
	(4, 'opdrachtgever_type', 'category', 11, '2023-08-24 17:11:57+02', NULL, 4, 'project');


--
-- Data for Name: maatwerk_ordinaal_waarde; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.maatwerk_ordinaal_waarde VALUES
	(1),
	(2),
	(3),
	(4);


--
-- Data for Name: maatwerk_ordinaal_waarde_state; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.maatwerk_ordinaal_waarde_state VALUES
	(1, 1, 2, '1', 1, 11, '2023-08-21 12:55:19+02', NULL),
	(2, 2, 2, '2', 2, 11, '2023-08-21 12:55:19+02', NULL),
	(3, 3, 2, '3', 3, 11, '2023-08-21 12:55:19+02', NULL),
	(4, 4, 2, '4', 4, 11, '2023-08-21 12:55:19+02', NULL);


--
-- Data for Name: milestone; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.milestone VALUES
	(1),
	(2),
	(3),
	(4),
	(5),
	(6),
	(7),
	(8),
	(9),
	(10),
	(11),
	(12),
	(13),
	(14),
	(15),
	(16),
	(17),
	(18),
	(19),
	(20),
	(21),
	(22),
	(23),
	(24),
	(25),
	(26),
	(27),
	(28),
	(29),
	(30),
	(31),
	(32),
	(33),
	(34),
	(35),
	(36),
	(37),
	(38),
	(39),
	(40),
	(41),
	(42),
	(43),
	(44);


--
-- Data for Name: milestone_state; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.milestone_state VALUES
	(35, 35, '2023-11-01', 9, '2023-08-25 11:51:37+02', NULL, 'voorspeld', 'Van realisatie naar oplevering met 1e oplevering'),
	(36, 36, '2025-07-01', 9, '2023-08-25 11:51:37+02', NULL, 'voorspeld', '2e oplevering'),
	(34, 34, '2023-07-01', 9, '2023-08-25 11:51:37+02', NULL, 'gepland', 'Overgang van vergunning naar realisatie fase'),
	(15, 15, '2020-01-01', 9, '2023-08-25 11:51:37+02', NULL, 'gerealiseerd', 'Startdatum project'),
	(37, 37, '2027-07-01', 9, '2023-08-25 11:51:37+02', NULL, 'voorspeld', 'Eind project met 3e oplevering'),
	(17, 17, '2020-07-01', 9, '2023-08-25 11:51:37+02', NULL, 'gerealiseerd', 'Overgang van initiatief naar project fase'),
	(38, 38, '2020-01-01', 9, '2023-08-27 17:11:16+02', NULL, 'gerealiseerd', 'Start project'),
	(18, 18, '2021-01-01', 9, '2023-08-25 11:51:37+02', NULL, 'gerealiseerd', 'Overgang van project naar vergunning fase'),
	(19, 19, '2021-07-01', 9, '2023-08-25 11:51:37+02', NULL, 'gerealiseerd', 'Overgang van project naar realisatie fase'),
	(20, 20, '2022-01-01', 9, '2023-08-25 11:51:37+02', NULL, 'gerealiseerd', 'Realisatie van sloopwerkzaamheden'),
	(16, 16, '2022-07-01', 9, '2023-08-25 11:51:37+02', NULL, 'gerealiseerd', 'Einddatum project met oplevering bouwwerkzaamheden'),
	(8, 8, '2022-07-01', 8, '2023-08-25 11:51:37+02', NULL, 'gerealiseerd', 'Startdatum project'),
	(9, 9, '2030-07-01', 8, '2023-08-25 11:51:37+02', NULL, 'voorspeld', 'Einddatum project met 3e oplevermoment'),
	(10, 10, '2023-07-01', 8, '2023-08-25 11:51:37+02', NULL, 'gerealiseerd', 'Overgang van initiatief naar project fase'),
	(11, 11, '2024-07-01', 8, '2023-08-25 11:51:37+02', NULL, 'gepland', 'Overgang van project naar vergunning fase'),
	(39, 39, '0202-07-01', 9, '2023-08-27 17:11:16+02', NULL, 'gerealiseerd', 'Overgang van initiatief naar project fase'),
	(13, 13, '2028-07-01', 8, '2023-08-25 11:51:37+02', NULL, 'voorspeld', 'Overgang van realisatie naar opleverfase fase met 1e oplevermoment'),
	(14, 14, '2029-07-01', 8, '2023-08-25 11:51:37+02', NULL, 'voorspeld', 'Tussentijdse 2e oplevermoment'),
	(1, 1, '2022-01-01', 7, '2023-08-21 12:55:19+02', NULL, 'gerealiseerd', 'Startdatum project'),
	(2, 2, '2030-01-01', 7, '2023-08-21 12:55:19+02', NULL, 'voorspeld', 'Einddatum project met 3e oplevermoment'),
	(3, 3, '2023-01-01', 7, '2023-08-21 12:55:19+02', NULL, 'gerealiseerd', 'Overgang van initiatief naar project fase'),
	(4, 4, '2024-01-01', 7, '2023-08-21 12:55:19+02', NULL, 'gepland', 'Overgang van project naar vergunning fase'),
	(5, 5, '2026-01-01', 7, '2023-08-21 12:55:19+02', NULL, 'gepland', 'Overgang van project naar realisatie fase'),
	(6, 6, '2028-01-01', 7, '2023-08-21 12:55:19+02', NULL, 'voorspeld', 'Overgang van realisatie naar opleverfase fase met 1e oplevermoment'),
	(7, 7, '2029-01-01', 7, '2023-08-21 12:55:19+02', NULL, 'voorspeld', 'Tussentijdse 2e oplevermoment'),
	(21, 21, '2021-03-01', 9, '2023-08-25 11:51:37+02', NULL, 'gerealiseerd', 'Planstatus van 3 in voorbereiding naar 2a vastgesteld'),
	(22, 22, '2021-05-01', 9, '2023-08-25 11:51:37+02', NULL, 'gerealiseerd', 'Planstatus van 2a vastgesteld naar 1a onherroepelijk'),
	(40, 40, '2021-01-01', 9, '2023-08-27 17:11:16+02', NULL, 'gerealiseerd', 'Overgang van project naar vergunning fase'),
	(41, 41, '2022-07-01', 9, '2023-08-27 17:11:16+02', NULL, 'gerealiseerd', 'Aanpassing vergunningaanvraag: Duurdere woningen'),
	(12, 12, '2026-07-01', 8, '2023-08-25 11:51:37+02', NULL, 'voorspeld', 'Overgang van vergunning naar realisatie fase'),
	(42, 42, '2024-01-01', 9, '2023-08-27 17:11:16+02', NULL, 'gepland', 'Overgang van vergunning naar realisatie fase'),
	(43, 43, '2026-01-01', 9, '2023-08-27 17:11:16+02', NULL, 'voorspeld', 'Naar oplever fase en start BWT procedures'),
	(44, 44, '2026-07-01', 9, '2023-08-27 17:11:16+02', NULL, 'voorspeld', 'Eind project'),
	(23, 23, '2020-01-01', 7, '2023-08-25 11:51:37+02', NULL, 'gerealiseerd', 'Startdatum project'),
	(24, 24, '2020-07-01', 7, '2023-08-25 11:51:37+02', NULL, 'gerealiseerd', 'Overgang van initiatief naar project fase'),
	(25, 25, '2021-01-01', 7, '2023-08-25 11:51:37+02', NULL, 'gerealiseerd', 'Overgang van project naar vergunning fase'),
	(26, 26, '2022-01-01', 7, '2023-08-25 11:51:37+02', NULL, 'gerealiseerd', 'Planstatus van 3 in voorbereiding naar 2a vastgesteld'),
	(27, 27, '2022-07-10', 7, '2023-08-25 11:51:37+02', NULL, 'gerealiseerd', 'Planstatus van 2a vastgesteld naar 1a onherroepelijk'),
	(28, 28, '2023-01-01', 7, '2023-08-25 11:51:37+02', NULL, 'gerealiseerd', 'Overgang van project naar realisatie fase'),
	(29, 29, '2024-01-01', 7, '2023-08-25 11:51:37+02', NULL, 'gepland', 'Naar oplever fase met oplevering corporatiewoningen'),
	(30, 30, '2024-07-01', 7, '2023-08-25 11:51:37+02', NULL, 'voorspeld', 'Eind project met oplevering particuliere huurwoningen'),
	(31, 31, '2020-01-01', 9, '2023-08-25 11:51:37+02', NULL, 'gerealiseerd', 'Startdatum project'),
	(32, 32, '2020-07-01', 9, '2023-08-25 11:51:37+02', NULL, 'gerealiseerd', 'Overgang van initiatief naar project fase'),
	(33, 33, '2021-01-01', 9, '2023-08-25 11:51:37+02', NULL, 'gerealiseerd', 'Overgang van project naar vergunning fase');


--
-- Data for Name: organization; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.organization VALUES
	(1),
	(2),
	(3),
	(4),
	(5),
	(6),
	(7),
	(8),
	(9),
	(10);


--
-- Data for Name: organization_state; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.organization_state VALUES
	(1, 1, NULL, 'root', NULL, '2023-08-21 12:55:19+02', 1),
	(2, 2, 1, 'admin_groep', NULL, '2023-08-21 12:55:19+02', 1),
	(3, 3, 1, 'publiek', NULL, '2023-08-21 12:55:19+02', 1),
	(4, 4, 3, 'provincie', NULL, '2023-08-21 12:55:19+02', 1),
	(5, 5, 3, 'gemeente', NULL, '2023-08-21 12:55:19+02', 1),
	(6, 6, 5, 'uitvoering_afdeling', NULL, '2023-08-21 12:55:19+02', 1),
	(7, 7, 6, 'projectleider_1', NULL, '2023-08-21 12:55:19+02', 1),
	(8, 8, 6, 'projectleider_2', NULL, '2023-08-21 12:55:19+02', 1),
	(9, 9, 6, 'projectleider_3', NULL, '2023-08-21 12:55:19+02', 1),
	(10, 10, 6, 'uitvoering_management', NULL, '2023-08-21 12:55:19+02', 1);


--
-- Data for Name: plan; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.plan VALUES
	(1),
	(2),
	(3),
	(4),
	(5),
	(6),
	(7),
	(8),
	(9),
	(10);


--
-- Data for Name: plan_conditie; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.plan_conditie VALUES
	(1),
	(2),
	(3),
	(4),
	(5),
	(6),
	(7),
	(8),
	(9),
	(10),
	(11),
	(12),
	(13),
	(14),
	(15);


--
-- Data for Name: plan_conditie_buurt; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--



--
-- Data for Name: plan_conditie_doelgroep; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--



--
-- Data for Name: plan_conditie_eigendom_en_waarde; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.plan_conditie_eigendom_en_waarde VALUES
	(1, 5, 'huurwoning_woningcorporatie', NULL, NULL, 11, '2023-08-28 11:53:13+02', NULL),
	(3, 7, 'huurwoning_woningcorporatie', NULL, NULL, 11, '2023-08-28 11:53:13+02', NULL),
	(4, 8, NULL, NULL, '€ 1.000,00', 11, '2023-08-28 11:53:13+02', NULL),
	(5, 9, 'huurwoning_particuliere_verhuurder', NULL, NULL, 11, '2023-08-28 11:53:13+02', NULL),
	(6, 10, NULL, '€ 190.000,00', NULL, 11, '2023-08-28 11:53:13+02', NULL),
	(7, 11, 'koopwoning', NULL, NULL, 11, '2023-08-28 11:53:13+02', NULL),
	(8, 12, NULL, '€ 250.000,00', NULL, 11, '2023-08-28 11:53:13+02', NULL),
	(9, 13, 'koopwoning', NULL, NULL, 11, '2023-08-28 11:53:13+02', NULL),
	(10, 14, NULL, '€ 500.000,00', NULL, 11, '2023-08-28 11:53:13+02', NULL),
	(11, 15, 'koopwoning', NULL, NULL, 11, '2023-08-28 11:53:13+02', NULL),
	(2, 6, NULL, NULL, '€ 647,19', 11, '2023-08-28 11:53:13+02', NULL);


--
-- Data for Name: plan_conditie_gemeente; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--



--
-- Data for Name: plan_conditie_geografie; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--



--
-- Data for Name: plan_conditie_grondpositie; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--



--
-- Data for Name: plan_conditie_grootte; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--



--
-- Data for Name: plan_conditie_maatwerk_boolean; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--



--
-- Data for Name: plan_conditie_maatwerk_categorie; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.plan_conditie_maatwerk_categorie VALUES
	(1, 1, 4, 11, '2023-08-28 11:53:13+02', NULL),
	(2, 3, 4, 11, '2023-08-28 11:53:13+02', NULL),
	(3, 4, 7, 11, '2023-08-28 11:53:13+02', NULL);


--
-- Data for Name: plan_conditie_maatwerk_numeriek; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--



--
-- Data for Name: plan_conditie_maatwerk_ordinaal; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--



--
-- Data for Name: plan_conditie_programmering; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--



--
-- Data for Name: plan_conditie_state; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.plan_conditie_state VALUES
	(1, 1, 1, 'doel_conditie', 11, '2023-08-28 11:53:13+02', NULL),
	(2, 2, 3, 'doel_conditie', 11, '2023-08-28 11:53:13+02', NULL),
	(3, 3, 3, 'plan_conditie', 11, '2023-08-28 11:53:13+02', NULL),
	(4, 4, 4, 'doel_conditie', 11, '2023-08-28 11:53:13+02', NULL),
	(5, 5, 5, 'doel_conditie', 11, '2023-08-28 11:53:13+02', NULL),
	(6, 6, 6, 'doel_conditie', 11, '2023-08-28 11:53:13+02', NULL),
	(7, 7, 6, 'plan_conditie', 11, '2023-08-28 11:53:13+02', NULL),
	(8, 8, 7, 'doel_conditie', 11, '2023-08-28 11:53:13+02', NULL),
	(9, 9, 7, 'plan_conditie', 11, '2023-08-28 11:53:13+02', NULL),
	(10, 10, 8, 'doel_conditie', 11, '2023-08-28 11:53:13+02', NULL),
	(11, 11, 8, 'plan_conditie', 11, '2023-08-28 11:53:13+02', NULL),
	(12, 12, 9, 'doel_conditie', 11, '2023-08-28 11:53:13+02', NULL),
	(13, 13, 9, 'plan_conditie', 11, '2023-08-28 11:53:13+02', NULL),
	(14, 14, 10, 'doel_conditie', 11, '2023-08-28 11:53:13+02', NULL),
	(15, 15, 10, 'plan_conditie', 11, '2023-08-28 11:53:13+02', NULL);


--
-- Data for Name: plan_conditie_type_en_fysiek_voorkomen; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.plan_conditie_type_en_fysiek_voorkomen VALUES
	(1, 2, 'meergezinswoning', NULL, 11, '2023-08-28 11:53:13+02', NULL);


--
-- Data for Name: plan_conditie_wijk; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--



--
-- Data for Name: plan_soort; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.plan_soort VALUES
	(1),
	(2),
	(3),
	(4),
	(5),
	(6),
	(7),
	(8),
	(9);


--
-- Data for Name: plan_soort_state; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.plan_soort_state VALUES
	(1, 1, '1 Basiseisen', 11, '2023-08-28 11:53:13+02', NULL),
	(2, 2, '2 Kwantitatieve woningbehoefte', 11, '2023-08-28 11:53:13+02', NULL),
	(3, 3, '3 Kwalitatieve behoefte', 11, '2023-08-28 11:53:13+02', NULL),
	(4, 4, '4 Geintegreerde mobiliteitsoplossingen', 11, '2023-08-28 11:53:13+02', NULL),
	(5, 5, '5 Leegstand', 11, '2023-08-28 11:53:13+02', NULL),
	(6, 6, '6 Provinciaal beleid', 11, '2023-08-28 11:53:13+02', NULL),
	(7, 7, '7 Een vernieuwend woonconcept', 11, '2023-08-28 11:53:13+02', NULL),
	(8, 8, '8 Duurzame en groene initiatieven', 11, '2023-08-28 11:53:13+02', NULL),
	(9, 9, '9 Minimaal 10% groen en water', 11, '2023-08-28 11:53:13+02', NULL);


--
-- Data for Name: plan_state; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.plan_state VALUES
	(3, 3, 3, 'Minimaal 80% meergezinswoningen in het centrum', '2030-01-01', '2023-08-28 11:53:13+02', NULL, 'openbaar', 10, 11, 'percentage', 'minimaal', 0.8, '2022-01-01'),
	(1, 1, 1, 'Basiseis dorpse kwaliteit', '2030-01-01', '2023-08-28 11:53:13+02', NULL, 'openbaar', 10, 11, 'percentage', 'minimaal', 0.9, '2022-01-01'),
	(2, 2, 2, 'Totaal aantal woningen', '2030-01-01', '2023-08-28 11:53:13+02', NULL, 'openbaar', 10, 11, 'aantal', 'minimaal', 500, '2022-01-01'),
	(4, 4, 6, 'Binnen de provinciale BSD grens of ruimte voor ruimte', '2030-01-01', '2023-08-28 11:53:13+02', NULL, 'openbaar', 10, 11, 'percentage', 'minimaal', 1, '2022-01-01'),
	(5, 5, 3, 'Minimaal 35% sociale huurwoningen', '2030-01-01', '2023-08-28 11:53:13+02', NULL, 'openbaar', 10, 11, 'percentage', 'minimaal', 0.35, '2022-01-01'),
	(7, 7, 3, 'Minimaal 15% middeldure huurwoningen tot 1000 euro huur', '2030-01-01', '2023-08-28 11:53:13+02', NULL, 'openbaar', 10, 11, 'percentage', 'minimaal', 0.15, '2022-01-01'),
	(6, 6, 3, 'Minimaal 65% van de sociale huurwoningen tot aftoppingsgrens', '2030-01-01', '2023-08-28 11:53:13+02', NULL, 'openbaar', 10, 11, 'percentage', 'minimaal', 0.65, '2022-01-01'),
	(8, 8, 3, 'Minimaal 10% goedkope koopwoningen tot 190000 met antispeculatie en zelfbewoningsplicht', '2030-01-01', '2023-08-28 11:53:13+02', NULL, 'openbaar', 10, 11, 'percentage', 'minimaal', 0.1, '2022-01-01'),
	(9, 9, 3, 'Minimaal 10% midddeldure koopwoningen tot 250000 met antispeculatie en zelfbewoningsplicht', '2030-01-01', '2023-08-28 11:53:13+02', NULL, 'openbaar', 10, 11, 'percentage', 'minimaal', 0.1, '2022-01-01');


--
-- Data for Name: project; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.project VALUES
	(1),
	(2),
	(3),
	(4),
	(5),
	(6);


--
-- Data for Name: project_actor_rol_changelog; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.project_actor_rol_changelog VALUES
	(1, 3, 2, 1, 7, '2023-08-21 12:55:19+02', NULL, 1, 1),
	(2, 1, 2, 1, 7, '2023-08-21 12:55:19+02', NULL, 4, 2),
	(3, 8, 9, 2, 8, '2023-08-25 11:51:37+02', NULL, 4, 2),
	(4, 10, 9, 2, 8, '2023-08-25 11:51:37+02', NULL, 2, 1),
	(5, 17, 16, 3, 9, '2023-08-25 11:51:37+02', NULL, 3, 1),
	(6, 15, 16, 3, 9, '2023-08-25 11:51:37+02', NULL, 5, 2),
	(7, 24, 30, 4, 7, '2023-08-25 11:51:37+02', NULL, 1, 1),
	(8, 23, 30, 4, 7, '2023-08-25 11:51:37+02', NULL, 4, 2),
	(9, 32, 37, 5, 9, '2023-08-25 11:51:37+02', NULL, 3, 1),
	(10, 38, 44, 6, 9, '2023-08-27 17:11:16+02', NULL, 3, 1);


--
-- Data for Name: project_actor_rol_value; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.project_actor_rol_value VALUES
	(1),
	(2);


--
-- Data for Name: project_actor_rol_value_state; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.project_actor_rol_value_state VALUES
	(1, 1, 11, '2023-08-21 12:55:19+02', NULL, 'projectleider'),
	(2, 2, 11, '2023-08-21 12:55:19+02', NULL, 'opdrachtgever');


--
-- Data for Name: project_duration_changelog; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.project_duration_changelog VALUES
	(1, 1, 2, 1, 7, '2023-08-21 12:55:19+02', NULL),
	(2, 8, 9, 2, 8, '2023-08-25 11:51:37+02', NULL),
	(3, 15, 16, 3, 9, '2023-08-25 11:51:37+02', NULL),
	(4, 23, 30, 4, 7, '2023-08-25 11:51:37+02', NULL),
	(5, 31, 37, 5, 9, '2023-08-25 11:51:37+02', NULL),
	(6, 38, 44, 6, 9, '2023-08-27 17:11:16+02', NULL);


--
-- Data for Name: project_fase_changelog; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.project_fase_changelog VALUES
	(1, 1, 3, 1, 7, '2023-08-21 12:55:19+02', NULL, '1_Initiatieffase'),
	(3, 4, 5, 1, 7, '2023-08-21 12:55:19+02', NULL, '3_vergunningsfase'),
	(2, 3, 4, 1, 7, '2023-08-21 12:55:19+02', NULL, '2_projectfase'),
	(4, 5, 6, 1, 7, '2023-08-21 12:55:19+02', NULL, '4_realisatiefase'),
	(5, 6, 2, 1, 7, '2023-08-21 12:55:19+02', NULL, '5_opleveringsfase'),
	(6, 8, 10, 2, 8, '2023-08-25 11:51:37+02', NULL, '1_Initiatieffase'),
	(7, 10, 11, 2, 8, '2023-08-25 11:51:37+02', NULL, '2_projectfase'),
	(8, 11, 12, 2, 8, '2023-08-25 11:51:37+02', NULL, '3_vergunningsfase'),
	(9, 12, 13, 2, 8, '2023-08-25 11:51:37+02', NULL, '4_realisatiefase'),
	(10, 13, 9, 2, 8, '2023-08-25 11:51:37+02', NULL, '5_opleveringsfase'),
	(11, 15, 17, 3, 9, '2023-08-25 11:51:37+02', NULL, '1_Initiatieffase'),
	(12, 17, 18, 3, 9, '2023-08-25 11:51:37+02', NULL, '2_projectfase'),
	(13, 18, 19, 3, 9, '2023-08-25 11:51:37+02', NULL, '3_vergunningsfase'),
	(14, 19, 20, 3, 9, '2023-08-25 11:51:37+02', NULL, '4_realisatiefase'),
	(15, 20, 16, 3, 9, '2023-08-25 11:51:37+02', NULL, '5_opleveringsfase'),
	(16, 23, 24, 4, 7, '2023-08-25 11:51:37+02', NULL, '1_Initiatieffase'),
	(17, 24, 25, 4, 7, '2023-08-25 11:51:37+02', NULL, '2_projectfase'),
	(18, 25, 28, 4, 7, '2023-08-25 11:51:37+02', NULL, '3_vergunningsfase'),
	(19, 28, 29, 4, 7, '2023-08-25 11:51:37+02', NULL, '4_realisatiefase'),
	(20, 29, 30, 4, 7, '2023-08-25 11:51:37+02', NULL, '5_opleveringsfase'),
	(21, 31, 32, 5, 9, '2023-08-25 11:51:37+02', NULL, '1_Initiatieffase'),
	(22, 32, 33, 5, 9, '2023-08-25 11:51:37+02', NULL, '2_projectfase'),
	(23, 33, 34, 5, 9, '2023-08-25 11:51:37+02', NULL, '3_vergunningsfase'),
	(24, 34, 35, 5, 9, '2023-08-25 11:51:37+02', NULL, '4_realisatiefase'),
	(25, 35, 37, 5, 9, '2023-08-25 11:51:37+02', NULL, '5_opleveringsfase'),
	(26, 38, 39, 6, 9, '2023-08-27 17:11:16+02', NULL, '1_Initiatieffase'),
	(27, 39, 40, 6, 9, '2023-08-27 17:11:16+02', NULL, '2_projectfase'),
	(28, 40, 42, 6, 9, '2023-08-27 17:11:16+02', NULL, '3_vergunningsfase'),
	(29, 42, 43, 6, 9, '2023-08-27 17:11:16+02', NULL, '4_realisatiefase'),
	(30, 43, 44, 6, 9, '2023-08-27 17:11:16+02', NULL, '5_opleveringsfase');


--
-- Data for Name: project_gemeenterol_changelog; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.project_gemeenterol_changelog VALUES
	(1, 1, 2, 1, 7, '2023-08-21 12:55:19+02', NULL, 1),
	(2, 8, 9, 2, 8, '2023-08-25 11:51:37+02', NULL, 1),
	(3, 15, 16, 3, 9, '2023-08-25 11:51:37+02', NULL, 2),
	(4, 23, 30, 4, 7, '2023-08-25 11:51:37+02', NULL, 1),
	(5, 31, 37, 5, 9, '2023-08-25 11:51:37+02', NULL, 2),
	(6, 38, 44, 6, 9, '2023-08-27 17:11:16+02', NULL, 2);


--
-- Data for Name: project_gemeenterol_value; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.project_gemeenterol_value VALUES
	(1),
	(2);


--
-- Data for Name: project_gemeenterol_value_state; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.project_gemeenterol_value_state VALUES
	(1, 'initiatiefnemer', 11, '2023-08-21 12:55:19+02', NULL, 1),
	(2, 'kaderstellend', 11, '2023-08-25 11:51:37+02', NULL, 2);


--
-- Data for Name: project_maatwerk_boolean_eigenschap_changelog; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--



--
-- Data for Name: project_maatwerk_categorie_eigenschap_changelog; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.project_maatwerk_categorie_eigenschap_changelog VALUES
	(1, 1, 2, 1, 4, 7, '2023-08-24 17:11:57+02', NULL),
	(2, 1, 2, 1, 8, 7, '2023-08-24 17:11:57+02', NULL),
	(3, 1, 2, 1, 10, 7, '2023-08-24 17:11:57+02', NULL),
	(4, 8, 9, 2, 10, 8, '2023-08-25 11:51:37+02', NULL),
	(5, 8, 9, 2, 4, 8, '2023-08-25 11:51:37+02', NULL),
	(6, 8, 9, 2, 7, 8, '2023-08-25 11:51:37+02', NULL),
	(7, 15, 16, 3, 12, 9, '2023-08-25 11:51:37+02', NULL),
	(8, 15, 16, 3, 5, 9, '2023-08-25 11:51:37+02', NULL),
	(9, 23, 30, 4, 10, 7, '2023-08-25 11:51:37+02', NULL),
	(10, 23, 30, 4, 4, 7, '2023-08-25 11:51:37+02', NULL),
	(11, 23, 30, 4, 7, 7, '2023-08-25 11:51:37+02', NULL),
	(12, 31, 37, 5, 11, 9, '2023-08-25 11:51:37+02', NULL),
	(13, 31, 37, 5, 5, 9, '2023-08-25 11:51:37+02', NULL),
	(14, 31, 37, 5, 9, 9, '2023-08-25 11:51:37+02', NULL),
	(15, 38, 44, 6, 11, 9, '2023-08-27 17:11:16+02', NULL),
	(16, 38, 44, 6, 5, 9, '2023-08-27 17:11:16+02', NULL),
	(17, 38, 44, 6, 9, 9, '2023-08-27 17:11:16+02', NULL);


--
-- Data for Name: project_maatwerk_numeriek_eigenschap_changelog; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--



--
-- Data for Name: project_maatwerk_ordinaal_eigenschap_changelog; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.project_maatwerk_ordinaal_eigenschap_changelog VALUES
	(1, 1, 2, 1, 3, 7, '2023-08-24 17:11:57+02', NULL),
	(2, 8, 9, 2, 3, 8, '2023-08-25 11:51:37+02', NULL),
	(3, 15, 16, 3, 1, 9, '2023-08-25 11:51:37+02', NULL),
	(4, 23, 30, 4, 1, 7, '2023-08-25 11:51:37+02', NULL),
	(5, 31, 37, 5, 2, 9, '2023-08-25 11:51:37+02', NULL),
	(6, 38, 44, 6, 2, 9, '2023-08-27 17:11:16+02', NULL);


--
-- Data for Name: project_name_changelog; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.project_name_changelog VALUES
	(1, 1, 2, 1, 7, '2023-08-21 12:55:19+02', NULL, 'Achterweg'),
	(2, 8, 9, 2, 8, '2023-08-25 11:51:37+02', NULL, 'Brielseweg'),
	(3, 15, 16, 3, 9, '2023-08-25 11:51:37+02', NULL, 'Carte Blanche Exclusief en Unique'),
	(4, 23, 30, 4, 7, '2023-08-25 11:51:37+02', NULL, 'De Ruy'),
	(5, 31, 37, 5, 9, '2023-08-25 11:51:37+02', NULL, 'Doornweg 6 /Blindeweg 15'),
	(6, 38, 44, 6, 9, '2023-08-27 17:11:16+02', NULL, 'Dorpsweg 58');


--
-- Data for Name: project_plan_type_changelog; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.project_plan_type_changelog VALUES
	(1, 1, 2, 1, 7, '2023-08-21 12:55:19+02', NULL, 'herstructurering'),
	(2, 8, 9, 2, 8, '2023-08-25 11:51:37+02', NULL, 'verdichting'),
	(3, 23, 30, 4, 7, '2023-08-25 11:51:37+02', NULL, 'herstructurering'),
	(4, 31, 37, 5, 9, '2023-08-25 11:51:37+02', NULL, 'herstructurering'),
	(5, 38, 44, 6, 9, '2023-08-27 17:11:16+02', NULL, 'herstructurering');


--
-- Data for Name: project_planologische_planstatus_changelog; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.project_planologische_planstatus_changelog VALUES
	(1, 1, 3, 1, 7, '2023-08-21 12:55:19+02', NULL, '4b_niet_opgenomen_in_visie'),
	(2, 3, 4, 1, 7, '2023-08-21 12:55:19+02', NULL, '4a_opgenomen_in_visie'),
	(3, 8, 10, 2, 8, '2023-08-25 11:51:37+02', NULL, '4b_niet_opgenomen_in_visie'),
	(4, 10, 9, 2, 8, '2023-08-25 11:51:37+02', NULL, '4a_opgenomen_in_visie'),
	(5, 15, 17, 3, 9, '2023-08-25 11:51:37+02', NULL, '4b_niet_opgenomen_in_visie'),
	(6, 17, 18, 3, 9, '2023-08-25 11:51:37+02', NULL, '4a_opgenomen_in_visie'),
	(7, 18, 21, 3, 9, '2023-08-25 11:51:37+02', NULL, '3_in_voorbereiding'),
	(8, 21, 22, 3, 9, '2023-08-25 11:51:37+02', NULL, '2a_vastgesteld'),
	(9, 22, 16, 3, 9, '2023-08-25 11:51:37+02', NULL, '1a_onherroepelijk'),
	(10, 23, 24, 4, 7, '2023-08-25 11:51:37+02', NULL, '4b_niet_opgenomen_in_visie'),
	(11, 24, 25, 4, 7, '2023-08-25 11:51:37+02', NULL, '4a_opgenomen_in_visie'),
	(12, 25, 26, 4, 7, '2023-08-25 11:51:37+02', NULL, '3_in_voorbereiding'),
	(13, 26, 27, 4, 7, '2023-08-25 11:51:37+02', NULL, '2a_vastgesteld'),
	(14, 27, 30, 4, 7, '2023-08-25 11:51:37+02', NULL, '1a_onherroepelijk'),
	(15, 31, 32, 5, 9, '2023-08-25 11:51:37+02', NULL, '4b_niet_opgenomen_in_visie'),
	(16, 32, 33, 5, 9, '2023-08-25 11:51:37+02', NULL, '4a_opgenomen_in_visie'),
	(17, 33, 34, 5, 9, '2023-08-25 11:51:37+02', NULL, '3_in_voorbereiding'),
	(18, 38, 39, 6, 9, '2023-08-27 17:11:16+02', NULL, '4b_niet_opgenomen_in_visie'),
	(19, 39, 40, 6, 9, '2023-08-27 17:11:16+02', NULL, '4a_opgenomen_in_visie'),
	(20, 40, 42, 6, 9, '2023-08-27 17:11:16+02', NULL, '3_in_voorbereiding');


--
-- Data for Name: project_priorisering_changelog; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--



--
-- Data for Name: project_priorisering_value; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.project_priorisering_value VALUES
	(1),
	(2),
	(3);


--
-- Data for Name: project_priorisering_value_state; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.project_priorisering_value_state VALUES
	(2, 'Low', 1, 1, '2023-07-28 00:00:00+02', NULL, 1),
	(4, 'High', 3, 1, '2023-07-28 00:00:00+02', '2023-07-28 00:00:00+02', 3),
	(10, 'Medium', 2, 1, '2023-07-28 00:00:00+02', NULL, 2),
	(3, 'Med', 2, 1, '2023-07-28 00:00:00+02', '2023-07-28 00:00:00+02', 2);


--
-- Data for Name: project_programmering_changelog; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.project_programmering_changelog VALUES
	(1, 1, 1, 3, 7, '2023-08-21 12:55:19+02', NULL, false),
	(2, 1, 3, 2, 7, '2023-08-21 12:55:19+02', NULL, true),
	(3, 2, 8, 10, 8, '2023-08-25 11:51:37+02', NULL, false),
	(4, 2, 10, 9, 8, '2023-08-25 11:51:37+02', NULL, true),
	(5, 3, 15, 17, 9, '2023-08-25 11:51:37+02', NULL, false),
	(6, 3, 17, 16, 9, '2023-08-25 11:51:37+02', NULL, true),
	(7, 4, 23, 24, 7, '2023-08-25 11:51:37+02', NULL, false),
	(8, 4, 24, 30, 7, '2023-08-25 11:51:37+02', NULL, true),
	(9, 5, 31, 32, 9, '2023-08-25 11:51:37+02', NULL, false),
	(10, 5, 32, 37, 9, '2023-08-25 11:51:37+02', NULL, true),
	(11, 6, 38, 39, 9, '2023-08-27 17:11:16+02', NULL, false),
	(12, 6, 39, 44, 9, '2023-08-27 17:11:16+02', NULL, true);


--
-- Data for Name: project_state; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.project_state VALUES
	(1, 1, 7, 7, '2023-08-21 12:55:19+02', NULL, 'extern_rapportage', 'FF3333'),
	(2, 2, 8, 8, '2023-08-25 11:51:37+02', NULL, 'extern_rapportage', 'FF8000'),
	(3, 3, 9, 9, '2023-08-25 11:51:37+02', NULL, 'openbaar', 'FFFF00'),
	(4, 4, 7, 7, '2023-08-25 11:51:37+02', NULL, 'openbaar', '80FF00'),
	(5, 5, 9, 9, '2023-08-25 11:51:37+02', NULL, 'extern_rapportage', '00FF00'),
	(6, 6, 9, 9, '2023-08-27 17:11:16+02', NULL, 'extern_rapportage', '00FF80');


--
-- Data for Name: software_module_rights; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.software_module_rights VALUES
	(1, 2, 'admin_panel', 'prive', 1, '2023-08-21 12:55:19+02', NULL, 'admin'),
	(2, 3, 'dashboard_publiek', 'openbaar', 1, '2023-08-21 12:55:19+02', NULL, 'view_only'),
	(3, 4, 'dashboard_provinciale_planmonitor', 'openbaar', 1, '2023-08-21 12:55:19+02', NULL, 'view_only'),
	(4, 5, 'dashboard_gemeenteraad', 'intern_uitvoering', 1, '2023-08-21 12:55:19+02', NULL, 'view_only'),
	(5, 5, 'dashboard_provinciale_planmonitor', 'intern_uitvoering', 1, '2023-08-21 12:55:19+02', NULL, 'view_only'),
	(6, 6, 'dashboard_interne_uitvoering', 'intern_rapportage', 1, '2023-08-21 12:55:19+02', NULL, 'view_only'),
	(7, 6, 'beheer_portaal', 'intern_uitvoering', 1, '2023-08-21 12:55:19+02', NULL, 'view_only'),
	(8, 7, 'beheer_portaal', 'intern_uitvoering', 1, '2023-08-21 12:55:19+02', NULL, 'crud_to_own'),
	(9, 8, 'beheer_portaal', 'intern_uitvoering', 1, '2023-08-21 12:55:19+02', NULL, 'crud_to_own'),
	(10, 9, 'beheer_portaal', 'intern_uitvoering', 1, '2023-08-21 12:55:19+02', NULL, 'crud_to_own'),
	(11, 10, 'beheer_portaal', 'intern_uitvoering', 1, '2023-08-21 12:55:19+02', NULL, 'crud_to_all');


--
-- Data for Name: user; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified."user" VALUES
	(1),
	(2),
	(3),
	(4),
	(5),
	(6),
	(7),
	(8),
	(9),
	(10),
	(11);


--
-- Data for Name: user_state; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.user_state VALUES
	(1, 1, 1, '2023-08-21 12:55:19+02', NULL, 'admin_1'),
	(2, 2, 1, '2023-08-21 12:55:19+02', NULL, 'publiek_1'),
	(3, 3, 1, '2023-08-21 12:55:19+02', NULL, 'provincie_1'),
	(4, 4, 1, '2023-08-21 12:55:19+02', NULL, 'algemene_gemeente_medewerker_1'),
	(5, 5, 1, '2023-08-21 12:55:19+02', NULL, 'raadslid_1'),
	(7, 7, 1, '2023-08-21 12:55:19+02', NULL, 'projectleider_1'),
	(8, 8, 1, '2023-08-21 12:55:19+02', NULL, 'projectleider_2'),
	(9, 9, 1, '2023-08-21 12:55:19+02', NULL, 'projectleider_3'),
	(10, 10, 1, '2023-08-21 12:55:19+02', NULL, 'assistent_1_projectleider_3'),
	(6, 6, 1, '2023-08-21 12:55:19+02', NULL, 'algemene_medewerker_uitvoering_1'),
	(11, 11, 1, '2023-08-21 12:55:19+02', NULL, 'manager_uitvoering_1');


--
-- Data for Name: user_to_organization; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.user_to_organization VALUES
	(1, 1, 2, 1, '2023-08-21 12:55:19+02', NULL),
	(2, 2, 3, 1, '2023-08-21 12:55:19+02', NULL),
	(3, 3, 4, 1, '2023-08-21 12:55:19+02', NULL),
	(4, 4, 5, 1, '2023-08-21 12:55:19+02', NULL),
	(5, 5, 5, 1, '2023-08-21 12:55:19+02', NULL),
	(6, 6, 6, 1, '2023-08-21 12:55:19+02', NULL),
	(7, 7, 7, 1, '2023-08-21 12:55:19+02', NULL),
	(8, 8, 8, 1, '2023-08-21 12:55:19+02', NULL),
	(10, 10, 9, 1, '2023-08-21 12:55:19+02', NULL),
	(9, 8, 9, 1, '2023-08-21 12:55:19+02', NULL),
	(11, 11, 10, 1, '2023-08-21 12:55:19+02', NULL);


--
-- Data for Name: wijk; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.wijk VALUES
	(1),
	(2);


--
-- Data for Name: wijk_state; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.wijk_state VALUES
	(1, 1, 1, 'rockanje', 11, '2023-08-21 12:55:19+02', NULL),
	(2, 2, 1, 'oostvoorne', 11, '2023-08-25 11:51:37+02', NULL);


--
-- Data for Name: woningblok; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.woningblok VALUES
	(1),
	(2),
	(51),
	(76),
	(77),
	(78),
	(79),
	(80),
	(81),
	(82),
	(83),
	(84),
	(85);


--
-- Data for Name: woningblok_buurt_changelog; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--



--
-- Data for Name: woningblok_doelgroep_changelog; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--



--
-- Data for Name: woningblok_duration_changelog; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.woningblok_duration_changelog VALUES
	(1, 1, 1, 2, 7, '2023-08-21 12:55:19+02', NULL),
	(2, 2, 1, 2, 7, '2023-08-21 12:55:19+02', NULL),
	(51, 51, 8, 9, 8, '2023-08-25 11:51:37+02', NULL),
	(76, 76, 15, 20, 9, '2023-08-25 11:51:37+02', NULL),
	(77, 77, 15, 16, 9, '2023-08-25 11:51:37+02', NULL),
	(78, 78, 15, 20, 9, '2023-08-25 11:51:37+02', NULL),
	(79, 79, 23, 29, 7, '2023-08-25 11:51:37+02', NULL),
	(80, 80, 23, 30, 7, '2023-08-25 11:51:37+02', NULL),
	(81, 81, 23, 30, 7, '2023-08-25 11:51:37+02', NULL),
	(82, 82, 31, 35, 9, '2023-08-25 11:51:37+02', NULL),
	(83, 83, 31, 36, 9, '2023-08-25 11:51:37+02', NULL),
	(84, 84, 31, 37, 9, '2023-08-25 11:51:37+02', NULL),
	(85, 85, 38, 44, 9, '2023-08-27 17:11:16+02', NULL);


--
-- Data for Name: woningblok_eigendom_en_waarde_changelog; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.woningblok_eigendom_en_waarde_changelog VALUES
	(76, 76, 15, 20, 9, '2023-08-25 11:51:37+02', NULL, 'koopwoning', '€ 500.000,00', NULL),
	(77, 77, 15, 16, 9, '2023-08-25 11:51:37+02', NULL, 'koopwoning', '€ 500.000,00', NULL),
	(78, 78, 15, 20, 9, '2023-08-25 11:51:37+02', NULL, 'koopwoning', '€ 500.000,00', NULL),
	(79, 79, 23, 29, 7, '2023-08-25 11:51:37+02', NULL, 'huurwoning_woningcorporatie', NULL, '€ 673,00'),
	(80, 80, 23, 30, 7, '2023-08-25 11:51:37+02', NULL, 'huurwoning_particuliere_verhuurder', NULL, '€ 850,00'),
	(81, 81, 23, 30, 7, '2023-08-25 11:51:37+02', NULL, 'huurwoning_particuliere_verhuurder', NULL, '€ 1.075,00'),
	(82, 82, 31, 35, 9, '2023-08-25 11:51:37+02', NULL, 'koopwoning', '€ 500.000,00', NULL),
	(83, 83, 31, 36, 9, '2023-08-25 11:51:37+02', NULL, 'koopwoning', '€ 500.000,00', NULL),
	(84, 84, 31, 37, 9, '2023-08-25 11:51:37+02', NULL, 'koopwoning', '€ 500.000,00', NULL),
	(85, 85, 38, 41, 9, '2023-08-27 17:11:16+02', NULL, 'koopwoning', '€ 355.000,00', NULL),
	(86, 85, 41, 44, 9, '2023-08-27 17:11:16+02', NULL, 'koopwoning', '€ 500.000,00', NULL);


--
-- Data for Name: woningblok_gemeente_changelog; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--



--
-- Data for Name: woningblok_grondpositie_changelog; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--



--
-- Data for Name: woningblok_grootte_changelog; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--



--
-- Data for Name: woningblok_kadastrale_koppeling_changelog; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--



--
-- Data for Name: woningblok_maatwerk_boolean_eigenschap_changelog; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--



--
-- Data for Name: woningblok_maatwerk_categorie_eigenschap_changelog; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--



--
-- Data for Name: woningblok_maatwerk_numeriek_eigenschap_changelog; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--



--
-- Data for Name: woningblok_maatwerk_ordinaal_eigenschap_changelog; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.woningblok_maatwerk_ordinaal_eigenschap_changelog VALUES
	(1, 1, 3, 1, 3, 7, '2023-08-21 12:55:19+02', NULL),
	(2, 1, 3, 2, 3, 7, '2023-08-21 12:55:19+02', NULL);


--
-- Data for Name: woningblok_mutatie_changelog; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.woningblok_mutatie_changelog VALUES
	(1, 1, 1, 2, 7, '2023-08-21 12:55:19+02', NULL, 'bouw', 50, 0, 50),
	(2, 2, 1, 2, 7, '2023-08-21 12:55:19+02', NULL, 'bouw', 200, 0, 200),
	(51, 51, 8, 9, 8, '2023-08-25 11:51:37+02', NULL, 'bouw', 70, 0, 70),
	(76, 76, 15, 20, 9, '2023-08-25 11:51:37+02', NULL, 'sloop', 0, 1, -1),
	(78, 78, 15, 20, 9, '2023-08-25 11:51:37+02', NULL, 'bouw', 2, 0, 2),
	(84, 84, 31, 37, 9, '2023-08-25 11:51:37+02', NULL, 'bouw', 3, 0, 3),
	(77, 77, 15, 16, 9, '2023-08-25 11:51:37+02', NULL, 'bouw', 2, 0, 2),
	(79, 79, 23, 29, 7, '2023-08-25 11:51:37+02', NULL, 'bouw', 16, 0, 16),
	(80, 80, 23, 30, 7, '2023-08-25 11:51:37+02', NULL, 'bouw', 12, 0, 12),
	(81, 81, 23, 30, 7, '2023-08-25 11:51:37+02', NULL, 'bouw', 12, 0, 12),
	(82, 82, 31, 35, 9, '2023-08-25 11:51:37+02', NULL, 'bouw', 2, 0, 2),
	(83, 83, 31, 36, 9, '2023-08-25 11:51:37+02', NULL, 'bouw', 2, 0, 2),
	(85, 85, 38, 44, 9, '2023-08-27 17:11:16+02', NULL, 'bouw', 2, 0, 2);


--
-- Data for Name: woningblok_naam_changelog; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.woningblok_naam_changelog VALUES
	(1, 1, 1, 2, '50 meergezinswoningen', 7, '2023-08-21 12:55:19+02', NULL),
	(2, 2, 1, 2, '200 eengezinswoningen', 7, '2023-08-21 12:55:19+02', NULL),
	(3, 51, 8, 9, '70 woningen', 8, '2023-08-25 11:51:37+02', NULL),
	(6, 78, 15, 20, 'Carte Blanche Unique: Bouw 2 woningen ', 9, '2023-08-25 11:51:37+02', NULL),
	(5, 77, 15, 16, 'Carte Blanche Exclusief: Bouw 2 woningen', 9, '2023-08-25 11:51:37+02', NULL),
	(4, 76, 15, 20, 'Carte Blanche Exclusief: Sloop woning', 9, '2023-08-25 11:51:37+02', NULL);


--
-- Data for Name: woningblok_state; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.woningblok_state VALUES
	(1, 1, 1, 7, '2023-08-21 12:55:19+02', NULL),
	(2, 2, 1, 7, '2023-08-21 12:55:19+02', NULL),
	(51, 51, 2, 8, '2023-08-25 11:51:37+02', NULL),
	(76, 76, 3, 9, '2023-08-25 11:51:37+02', NULL),
	(77, 77, 3, 9, '2023-08-25 11:51:37+02', NULL),
	(78, 78, 3, 9, '2023-08-25 11:51:37+02', NULL),
	(79, 79, 4, 7, '2023-08-25 11:51:37+02', NULL),
	(80, 80, 4, 7, '2023-08-25 11:51:37+02', NULL),
	(81, 81, 4, 7, '2023-08-25 11:51:37+02', NULL),
	(82, 82, 5, 9, '2023-08-25 11:51:37+02', NULL),
	(83, 83, 5, 9, '2023-08-25 11:51:37+02', NULL),
	(84, 84, 5, 9, '2023-08-25 11:51:37+02', NULL),
	(85, 85, 6, 9, '2023-08-27 17:11:16+02', NULL);


--
-- Data for Name: woningblok_type_en_fysiek_voorkomen_changelog; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.woningblok_type_en_fysiek_voorkomen_changelog VALUES
	(1, 1, 1, 2, 7, '2023-08-21 12:55:19+02', NULL, 'meergezinswoning', NULL),
	(2, 2, 1, 2, 7, '2023-08-21 12:55:19+02', NULL, 'eengezinswoning', NULL),
	(76, 76, 15, 20, 9, '2023-08-25 11:51:37+02', NULL, 'eengezinswoning', NULL),
	(77, 77, 15, 16, 9, '2023-08-25 11:51:37+02', NULL, 'eengezinswoning', NULL),
	(78, 78, 15, 20, 9, '2023-08-25 11:51:37+02', NULL, 'eengezinswoning', NULL),
	(79, 79, 23, 29, 7, '2023-08-25 11:51:37+02', NULL, 'meergezinswoning', NULL),
	(80, 80, 23, 30, 7, '2023-08-25 11:51:37+02', NULL, 'meergezinswoning', NULL),
	(81, 81, 23, 30, 7, '2023-08-25 11:51:37+02', NULL, 'meergezinswoning', NULL),
	(82, 82, 31, 35, 9, '2023-08-25 11:51:37+02', NULL, 'eengezinswoning', NULL),
	(83, 83, 31, 36, 9, '2023-08-25 11:51:37+02', NULL, 'eengezinswoning', NULL),
	(84, 84, 31, 37, 9, '2023-08-25 11:51:37+02', NULL, 'eengezinswoning', NULL),
	(85, 85, 38, 44, 9, '2023-08-27 17:11:16+02', NULL, 'eengezinswoning', NULL);


--
-- Data for Name: woningblok_wijk_changelog; Type: TABLE DATA; Schema: diwi_testset_simplified; Owner: vng
--

INSERT INTO diwi_testset_simplified.woningblok_wijk_changelog VALUES
	(1, 1, 1, 2, 1, 7, '2023-08-21 12:55:19+02', NULL),
	(2, 2, 1, 2, 1, 7, '2023-08-21 12:55:19+02', NULL),
	(51, 51, 8, 9, 2, 8, '2023-08-25 11:51:37+02', NULL),
	(76, 76, 15, 20, 1, 9, '2023-08-25 11:51:37+02', NULL),
	(77, 77, 15, 16, 1, 9, '2023-08-25 11:51:37+02', NULL),
	(78, 78, 15, 20, 1, 9, '2023-08-25 11:51:37+02', NULL),
	(79, 79, 23, 29, 2, 7, '2023-08-25 11:51:37+02', NULL),
	(80, 80, 23, 30, 2, 7, '2023-08-25 11:51:37+02', NULL),
	(81, 81, 23, 30, 2, 7, '2023-08-25 11:51:37+02', NULL),
	(82, 82, 31, 35, 1, 9, '2023-08-25 11:51:37+02', NULL),
	(83, 83, 31, 36, 1, 9, '2023-08-25 11:51:37+02', NULL),
	(84, 84, 31, 37, 1, 9, '2023-08-25 11:51:37+02', NULL),
	(85, 85, 38, 44, 1, 9, '2023-08-27 17:11:16+02', NULL);


--
-- Name: gemeente Gemeente_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.gemeente
    ADD CONSTRAINT "Gemeente_pkey" PRIMARY KEY ("ID");


--
-- Name: buurt buurt_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.buurt
    ADD CONSTRAINT buurt_pkey PRIMARY KEY ("ID");


--
-- Name: buurt_state buurt_state_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.buurt_state
    ADD CONSTRAINT buurt_state_pkey PRIMARY KEY ("ID");


--
-- Name: document document_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.document
    ADD CONSTRAINT document_pkey PRIMARY KEY ("ID");


--
-- Name: document_soort document_soort_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.document_soort
    ADD CONSTRAINT document_soort_pkey PRIMARY KEY ("ID");


--
-- Name: document_soort_state document_soort_state_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.document_soort_state
    ADD CONSTRAINT document_soort_state_pkey PRIMARY KEY ("ID");


--
-- Name: document_state document_state_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.document_state
    ADD CONSTRAINT document_state_pkey PRIMARY KEY ("ID");


--
-- Name: actor externe_partij_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.actor
    ADD CONSTRAINT externe_partij_pkey PRIMARY KEY ("ID");


--
-- Name: actor_state externe_partij_state_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.actor_state
    ADD CONSTRAINT externe_partij_state_pkey PRIMARY KEY ("ID");


--
-- Name: gemeente_state gemeente_state_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.gemeente_state
    ADD CONSTRAINT gemeente_state_pkey PRIMARY KEY ("ID");


--
-- Name: milestone milestone_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.milestone
    ADD CONSTRAINT milestone_pkey PRIMARY KEY ("ID");


--
-- Name: milestone_state milestone_state_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.milestone_state
    ADD CONSTRAINT milestone_state_pkey PRIMARY KEY ("ID");


--
-- Name: organization organization_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.organization
    ADD CONSTRAINT organization_pkey PRIMARY KEY ("ID");


--
-- Name: organization_state organization_state_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.organization_state
    ADD CONSTRAINT organization_state_pkey PRIMARY KEY ("ID");


--
-- Name: plan_conditie_buurt plan_conditie_buurt_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_buurt
    ADD CONSTRAINT plan_conditie_buurt_pkey PRIMARY KEY ("ID");


--
-- Name: plan_conditie_maatwerk_categorie plan_conditie_categorie_maatwerk_eigenschap_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_maatwerk_categorie
    ADD CONSTRAINT plan_conditie_categorie_maatwerk_eigenschap_pkey PRIMARY KEY ("ID");


--
-- Name: plan_conditie_doelgroep plan_conditie_doelgroep_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_doelgroep
    ADD CONSTRAINT plan_conditie_doelgroep_pkey PRIMARY KEY ("ID");


--
-- Name: plan_conditie_eigendom_en_waarde plan_conditie_eigendom_en_waarde_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_eigendom_en_waarde
    ADD CONSTRAINT plan_conditie_eigendom_en_waarde_pkey PRIMARY KEY ("ID");


--
-- Name: plan_conditie_gemeente plan_conditie_gemeente_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_gemeente
    ADD CONSTRAINT plan_conditie_gemeente_pkey PRIMARY KEY ("ID");


--
-- Name: plan_conditie_geografie plan_conditie_geografie_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_geografie
    ADD CONSTRAINT plan_conditie_geografie_pkey PRIMARY KEY ("ID");


--
-- Name: plan_conditie_grondpositie plan_conditie_grondpositie_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_grondpositie
    ADD CONSTRAINT plan_conditie_grondpositie_pkey PRIMARY KEY ("ID");


--
-- Name: plan_conditie_grootte plan_conditie_grootte_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_grootte
    ADD CONSTRAINT plan_conditie_grootte_pkey PRIMARY KEY ("ID");


--
-- Name: plan_conditie_maatwerk_boolean plan_conditie_maatwerk_boolean_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_maatwerk_boolean
    ADD CONSTRAINT plan_conditie_maatwerk_boolean_pkey PRIMARY KEY ("ID");


--
-- Name: plan_conditie_maatwerk_numeriek plan_conditie_maatwerk_numeriek_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_maatwerk_numeriek
    ADD CONSTRAINT plan_conditie_maatwerk_numeriek_pkey PRIMARY KEY ("ID");


--
-- Name: plan_conditie_maatwerk_ordinaal plan_conditie_maatwerk_ordinaal_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_maatwerk_ordinaal
    ADD CONSTRAINT plan_conditie_maatwerk_ordinaal_pkey PRIMARY KEY ("ID");


--
-- Name: plan_conditie plan_conditie_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie
    ADD CONSTRAINT plan_conditie_pkey PRIMARY KEY ("ID");


--
-- Name: plan_conditie_programmering plan_conditie_programmering_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_programmering
    ADD CONSTRAINT plan_conditie_programmering_pkey PRIMARY KEY ("ID");


--
-- Name: plan_conditie_state plan_conditie_state_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_state
    ADD CONSTRAINT plan_conditie_state_pkey PRIMARY KEY ("ID");


--
-- Name: plan_conditie_type_en_fysiek_voorkomen plan_conditie_type_en_fysiek_voorkomen_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_type_en_fysiek_voorkomen
    ADD CONSTRAINT plan_conditie_type_en_fysiek_voorkomen_pkey PRIMARY KEY ("ID");


--
-- Name: plan_conditie_wijk plan_conditie_wijk_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_wijk
    ADD CONSTRAINT plan_conditie_wijk_pkey PRIMARY KEY ("ID");


--
-- Name: plan plan_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan
    ADD CONSTRAINT plan_pkey PRIMARY KEY ("ID");


--
-- Name: plan_soort plan_soort_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_soort
    ADD CONSTRAINT plan_soort_pkey PRIMARY KEY ("ID");


--
-- Name: plan_soort_state plan_soort_state_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_soort_state
    ADD CONSTRAINT plan_soort_state_pkey PRIMARY KEY ("ID");


--
-- Name: plan_state plan_state_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_state
    ADD CONSTRAINT plan_state_pkey PRIMARY KEY ("ID");


--
-- Name: project_duration_changelog project_duration_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_duration_changelog
    ADD CONSTRAINT project_duration_changelog_pkey PRIMARY KEY ("ID");


--
-- Name: project_actor_rol_changelog project_externe_partij_rol_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_actor_rol_changelog
    ADD CONSTRAINT project_externe_partij_rol_changelog_pkey PRIMARY KEY ("ID");


--
-- Name: project_actor_rol_value project_externe_rol_value_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_actor_rol_value
    ADD CONSTRAINT project_externe_rol_value_pkey PRIMARY KEY ("ID");


--
-- Name: project_actor_rol_value_state project_externe_rol_value_state_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_actor_rol_value_state
    ADD CONSTRAINT project_externe_rol_value_state_pkey PRIMARY KEY ("ID");


--
-- Name: project_fase_changelog project_fase_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_fase_changelog
    ADD CONSTRAINT project_fase_changelog_pkey PRIMARY KEY ("ID");


--
-- Name: project_gemeenterol_changelog project_gemeenterol_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_gemeenterol_changelog
    ADD CONSTRAINT project_gemeenterol_changelog_pkey PRIMARY KEY ("ID");


--
-- Name: project_gemeenterol_value project_gemeenterol_value_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_gemeenterol_value
    ADD CONSTRAINT project_gemeenterol_value_pkey PRIMARY KEY ("ID");


--
-- Name: project_gemeenterol_value_state project_gemeenterol_value_state_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_gemeenterol_value_state
    ADD CONSTRAINT project_gemeenterol_value_state_pkey PRIMARY KEY ("ID");


--
-- Name: project_maatwerk_boolean_eigenschap_changelog project_maatwerk_boolean_eigenschap_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_maatwerk_boolean_eigenschap_changelog
    ADD CONSTRAINT project_maatwerk_boolean_eigenschap_changelog_pkey PRIMARY KEY ("ID");


--
-- Name: project_maatwerk_categorie_eigenschap_changelog project_maatwerk_categorie_eigenschap_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_maatwerk_categorie_eigenschap_changelog
    ADD CONSTRAINT project_maatwerk_categorie_eigenschap_changelog_pkey PRIMARY KEY ("ID");


--
-- Name: project_maatwerk_numeriek_eigenschap_changelog project_maatwerk_numeriek_eigenschap_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_maatwerk_numeriek_eigenschap_changelog
    ADD CONSTRAINT project_maatwerk_numeriek_eigenschap_changelog_pkey PRIMARY KEY ("ID");


--
-- Name: project_maatwerk_ordinaal_eigenschap_changelog project_maatwerk_ordinaal_eigenschap_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_maatwerk_ordinaal_eigenschap_changelog
    ADD CONSTRAINT project_maatwerk_ordinaal_eigenschap_changelog_pkey PRIMARY KEY ("ID");


--
-- Name: project_name_changelog project_name_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_name_changelog
    ADD CONSTRAINT project_name_changelog_pkey PRIMARY KEY ("ID");


--
-- Name: project project_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project
    ADD CONSTRAINT project_pkey PRIMARY KEY ("ID");


--
-- Name: project_plan_type_changelog project_plan_type_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_plan_type_changelog
    ADD CONSTRAINT project_plan_type_changelog_pkey PRIMARY KEY ("ID");


--
-- Name: project_planologische_planstatus_changelog project_planologische_planstatus_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_planologische_planstatus_changelog
    ADD CONSTRAINT project_planologische_planstatus_changelog_pkey PRIMARY KEY ("ID");


--
-- Name: project_priorisering_changelog project_priorisering_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_priorisering_changelog
    ADD CONSTRAINT project_priorisering_changelog_pkey PRIMARY KEY ("ID");


--
-- Name: project_priorisering_value project_priorisering_value_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_priorisering_value
    ADD CONSTRAINT project_priorisering_value_pkey PRIMARY KEY ("ID");


--
-- Name: project_priorisering_value_state project_priorisering_values_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_priorisering_value_state
    ADD CONSTRAINT project_priorisering_values_pkey PRIMARY KEY ("ID");


--
-- Name: project_state project_state_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_state
    ADD CONSTRAINT project_state_pkey PRIMARY KEY ("ID");


--
-- Name: software_module_rights software_module_rights_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.software_module_rights
    ADD CONSTRAINT software_module_rights_pkey PRIMARY KEY ("ID");


--
-- Name: milestone unique_milestone_id; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.milestone
    ADD CONSTRAINT unique_milestone_id UNIQUE ("ID");


--
-- Name: milestone_state unique_milestone_state_ID; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.milestone_state
    ADD CONSTRAINT "unique_milestone_state_ID" UNIQUE ("ID");


--
-- Name: user unique_user_ID; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified."user"
    ADD CONSTRAINT "unique_user_ID" UNIQUE ("ID");


--
-- Name: user_state unique_user_state_ID; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.user_state
    ADD CONSTRAINT "unique_user_state_ID" UNIQUE ("ID");


--
-- Name: user user_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified."user"
    ADD CONSTRAINT user_pkey PRIMARY KEY ("ID");


--
-- Name: user_state user_state_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.user_state
    ADD CONSTRAINT user_state_pkey PRIMARY KEY ("ID");


--
-- Name: user_to_organization user_to_organization_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.user_to_organization
    ADD CONSTRAINT user_to_organization_pkey PRIMARY KEY ("ID");


--
-- Name: wijk wijk_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.wijk
    ADD CONSTRAINT wijk_pkey PRIMARY KEY ("ID");


--
-- Name: wijk_state wijk_state_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.wijk_state
    ADD CONSTRAINT wijk_state_pkey PRIMARY KEY ("ID");


--
-- Name: woningblok_buurt_changelog woningblok_buurt_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_buurt_changelog
    ADD CONSTRAINT woningblok_buurt_changelog_pkey PRIMARY KEY ("ID");


--
-- Name: woningblok_doelgroep_changelog woningblok_doelgroep_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_doelgroep_changelog
    ADD CONSTRAINT woningblok_doelgroep_changelog_pkey PRIMARY KEY ("ID");


--
-- Name: woningblok_duration_changelog woningblok_duration_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_duration_changelog
    ADD CONSTRAINT woningblok_duration_changelog_pkey PRIMARY KEY ("ID");


--
-- Name: woningblok_eigendom_en_waarde_changelog woningblok_eigendom_en_waarde_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_eigendom_en_waarde_changelog
    ADD CONSTRAINT woningblok_eigendom_en_waarde_changelog_pkey PRIMARY KEY ("ID");


--
-- Name: woningblok_gemeente_changelog woningblok_gemeente_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_gemeente_changelog
    ADD CONSTRAINT woningblok_gemeente_changelog_pkey PRIMARY KEY ("ID");


--
-- Name: woningblok_grondpositie_changelog woningblok_grondpositie_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_grondpositie_changelog
    ADD CONSTRAINT woningblok_grondpositie_changelog_pkey PRIMARY KEY ("ID");


--
-- Name: woningblok_grootte_changelog woningblok_grootte_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_grootte_changelog
    ADD CONSTRAINT woningblok_grootte_changelog_pkey PRIMARY KEY ("ID");


--
-- Name: woningblok_kadastrale_koppeling_changelog woningblok_kadastrale_koppeling_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_kadastrale_koppeling_changelog
    ADD CONSTRAINT woningblok_kadastrale_koppeling_changelog_pkey PRIMARY KEY ("ID");


--
-- Name: woningblok_maatwerk_boolean_eigenschap_changelog woningblok_maatwerk_boolean_eigenschap_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_maatwerk_boolean_eigenschap_changelog
    ADD CONSTRAINT woningblok_maatwerk_boolean_eigenschap_changelog_pkey PRIMARY KEY ("ID");


--
-- Name: woningblok_maatwerk_categorie_eigenschap_changelog woningblok_maatwerk_categorie_eigenschap_changelos_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_maatwerk_categorie_eigenschap_changelog
    ADD CONSTRAINT woningblok_maatwerk_categorie_eigenschap_changelos_pkey PRIMARY KEY ("ID");


--
-- Name: maatwerk_categorie_waarde woningblok_maatwerk_categorie_waarde_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.maatwerk_categorie_waarde
    ADD CONSTRAINT woningblok_maatwerk_categorie_waarde_pkey PRIMARY KEY ("ID");


--
-- Name: maatwerk_categorie_waarde_state woningblok_maatwerk_categorie_waarde_state_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.maatwerk_categorie_waarde_state
    ADD CONSTRAINT woningblok_maatwerk_categorie_waarde_state_pkey PRIMARY KEY ("ID");


--
-- Name: maatwerk_eigenschap woningblok_maatwerk_eigenschap_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.maatwerk_eigenschap
    ADD CONSTRAINT woningblok_maatwerk_eigenschap_pkey PRIMARY KEY ("ID");


--
-- Name: maatwerk_eigenschap_state woningblok_maatwerk_eigenschap_state_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.maatwerk_eigenschap_state
    ADD CONSTRAINT woningblok_maatwerk_eigenschap_state_pkey PRIMARY KEY ("ID");


--
-- Name: woningblok_maatwerk_numeriek_eigenschap_changelog woningblok_maatwerk_numeriek_eigenschap_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_maatwerk_numeriek_eigenschap_changelog
    ADD CONSTRAINT woningblok_maatwerk_numeriek_eigenschap_changelog_pkey PRIMARY KEY ("ID");


--
-- Name: woningblok_maatwerk_ordinaal_eigenschap_changelog woningblok_maatwerk_ordinaal_eigenschap_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_maatwerk_ordinaal_eigenschap_changelog
    ADD CONSTRAINT woningblok_maatwerk_ordinaal_eigenschap_changelog_pkey PRIMARY KEY ("ID");


--
-- Name: maatwerk_ordinaal_waarde woningblok_maatwerk_ordinale_waarde_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.maatwerk_ordinaal_waarde
    ADD CONSTRAINT woningblok_maatwerk_ordinale_waarde_pkey PRIMARY KEY ("ID");


--
-- Name: maatwerk_ordinaal_waarde_state woningblok_maatwerk_ordinale_waarde_state_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.maatwerk_ordinaal_waarde_state
    ADD CONSTRAINT woningblok_maatwerk_ordinale_waarde_state_pkey PRIMARY KEY ("ID");


--
-- Name: woningblok_mutatie_changelog woningblok_mutatie_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_mutatie_changelog
    ADD CONSTRAINT woningblok_mutatie_changelog_pkey PRIMARY KEY ("ID");


--
-- Name: woningblok_naam_changelog woningblok_naam_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_naam_changelog
    ADD CONSTRAINT woningblok_naam_changelog_pkey PRIMARY KEY ("ID");


--
-- Name: woningblok woningblok_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok
    ADD CONSTRAINT woningblok_pkey PRIMARY KEY ("ID");


--
-- Name: project_programmering_changelog woningblok_programmering_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_programmering_changelog
    ADD CONSTRAINT woningblok_programmering_changelog_pkey PRIMARY KEY ("ID");


--
-- Name: woningblok_state woningblok_state_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_state
    ADD CONSTRAINT woningblok_state_pkey PRIMARY KEY ("ID");


--
-- Name: woningblok_wijk_changelog woningblok_wijk_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_wijk_changelog
    ADD CONSTRAINT woningblok_wijk_changelog_pkey PRIMARY KEY ("ID");


--
-- Name: woningblok_type_en_fysiek_voorkomen_changelog woningblok_woning_type_en_fysiek_voorkomen_changelog_pkey; Type: CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_type_en_fysiek_voorkomen_changelog
    ADD CONSTRAINT woningblok_woning_type_en_fysiek_voorkomen_changelog_pkey PRIMARY KEY ("ID");


--
-- Name: actor_state fk_actor_state__actor; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.actor_state
    ADD CONSTRAINT fk_actor_state__actor FOREIGN KEY ("actor_ID") REFERENCES diwi_testset_simplified.actor("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: actor_state fk_actor_state__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.actor_state
    ADD CONSTRAINT fk_actor_state__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: actor_state fk_actor_state__organization; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.actor_state
    ADD CONSTRAINT fk_actor_state__organization FOREIGN KEY ("organization_ID") REFERENCES diwi_testset_simplified.organization("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: buurt_state fk_buurt_state__buurt; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.buurt_state
    ADD CONSTRAINT fk_buurt_state__buurt FOREIGN KEY ("buurt_ID") REFERENCES diwi_testset_simplified.buurt("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: buurt_state fk_buurt_state__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.buurt_state
    ADD CONSTRAINT fk_buurt_state__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: buurt_state fk_buurt_state__wijk; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.buurt_state
    ADD CONSTRAINT fk_buurt_state__wijk FOREIGN KEY ("wijk_ID") REFERENCES diwi_testset_simplified.wijk("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: document_soort_state fk_document_soort_state__document_soort; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.document_soort_state
    ADD CONSTRAINT fk_document_soort_state__document_soort FOREIGN KEY ("document_soort_ID") REFERENCES diwi_testset_simplified.document_soort("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: document_soort_state fk_document_soort_state__user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.document_soort_state
    ADD CONSTRAINT fk_document_soort_state__user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: document_state fk_document_state__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.document_state
    ADD CONSTRAINT fk_document_state__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: document_state fk_document_state__document; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.document_state
    ADD CONSTRAINT fk_document_state__document FOREIGN KEY ("document_ID") REFERENCES diwi_testset_simplified.document("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: document_state fk_document_state__document_soort; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.document_state
    ADD CONSTRAINT fk_document_state__document_soort FOREIGN KEY ("document_soort_ID") REFERENCES diwi_testset_simplified.document_soort("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: document_state fk_document_state__milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.document_state
    ADD CONSTRAINT fk_document_state__milestone FOREIGN KEY ("milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: document_state fk_document_state__owner_organisation; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.document_state
    ADD CONSTRAINT fk_document_state__owner_organisation FOREIGN KEY ("owner_organization_ID") REFERENCES diwi_testset_simplified.organization("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gemeente_state fk_gemeente_state__gchange_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.gemeente_state
    ADD CONSTRAINT fk_gemeente_state__gchange_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gemeente_state fk_gemeente_state__gemeente; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.gemeente_state
    ADD CONSTRAINT fk_gemeente_state__gemeente FOREIGN KEY ("gemeente_ID") REFERENCES diwi_testset_simplified.gemeente("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: milestone_state fk_milestone_state__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.milestone_state
    ADD CONSTRAINT fk_milestone_state__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: milestone_state fk_milestone_state__milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.milestone_state
    ADD CONSTRAINT fk_milestone_state__milestone FOREIGN KEY ("milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: organization_state fk_organization_state__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.organization_state
    ADD CONSTRAINT fk_organization_state__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: organization_state fk_organization_state__organization; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.organization_state
    ADD CONSTRAINT fk_organization_state__organization FOREIGN KEY ("organization_ID") REFERENCES diwi_testset_simplified.organization("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: organization_state fk_organization_state__parent_organization; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.organization_state
    ADD CONSTRAINT fk_organization_state__parent_organization FOREIGN KEY ("parent_organization_ID") REFERENCES diwi_testset_simplified.organization("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_buurt fk_plan_conditie_buurt__buurt; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_buurt
    ADD CONSTRAINT fk_plan_conditie_buurt__buurt FOREIGN KEY ("buurt_ID") REFERENCES diwi_testset_simplified.buurt("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_buurt fk_plan_conditie_buurt__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_buurt
    ADD CONSTRAINT fk_plan_conditie_buurt__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_buurt fk_plan_conditie_buurt__plan_conditie; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_buurt
    ADD CONSTRAINT fk_plan_conditie_buurt__plan_conditie FOREIGN KEY ("plan_conditie_ID") REFERENCES diwi_testset_simplified.plan_conditie("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_doelgroep fk_plan_conditie_doelgroep__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_doelgroep
    ADD CONSTRAINT fk_plan_conditie_doelgroep__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_doelgroep fk_plan_conditie_doelgroep__plan_conditie; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_doelgroep
    ADD CONSTRAINT fk_plan_conditie_doelgroep__plan_conditie FOREIGN KEY ("plan_conditie_ID") REFERENCES diwi_testset_simplified.plan_conditie("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_eigendom_en_waarde fk_plan_conditie_eigendom_en_waarde__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_eigendom_en_waarde
    ADD CONSTRAINT fk_plan_conditie_eigendom_en_waarde__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_eigendom_en_waarde fk_plan_conditie_eigendom_en_waarde__plan_conditie; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_eigendom_en_waarde
    ADD CONSTRAINT fk_plan_conditie_eigendom_en_waarde__plan_conditie FOREIGN KEY ("plan_conditie_ID") REFERENCES diwi_testset_simplified.plan_conditie("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_gemeente fk_plan_conditie_gemeente__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_gemeente
    ADD CONSTRAINT fk_plan_conditie_gemeente__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_gemeente fk_plan_conditie_gemeente__gemeente; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_gemeente
    ADD CONSTRAINT fk_plan_conditie_gemeente__gemeente FOREIGN KEY ("gemeente_ID") REFERENCES diwi_testset_simplified.gemeente("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_gemeente fk_plan_conditie_gemeente__plan_conditie; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_gemeente
    ADD CONSTRAINT fk_plan_conditie_gemeente__plan_conditie FOREIGN KEY ("plan_conditie_ID") REFERENCES diwi_testset_simplified.plan_conditie("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_geografie fk_plan_conditie_geografie__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_geografie
    ADD CONSTRAINT fk_plan_conditie_geografie__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_geografie fk_plan_conditie_geografie__plan_conditie; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_geografie
    ADD CONSTRAINT fk_plan_conditie_geografie__plan_conditie FOREIGN KEY ("plan_conditie_ID") REFERENCES diwi_testset_simplified.plan_conditie("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_grondpositie fk_plan_conditie_grondpositie__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_grondpositie
    ADD CONSTRAINT fk_plan_conditie_grondpositie__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_grondpositie fk_plan_conditie_grondpositie__plan_conditie; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_grondpositie
    ADD CONSTRAINT fk_plan_conditie_grondpositie__plan_conditie FOREIGN KEY ("plan_conditie_ID") REFERENCES diwi_testset_simplified.plan_conditie("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_grootte fk_plan_conditie_grootte__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_grootte
    ADD CONSTRAINT fk_plan_conditie_grootte__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_grootte fk_plan_conditie_grootte__plan_conditie; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_grootte
    ADD CONSTRAINT fk_plan_conditie_grootte__plan_conditie FOREIGN KEY ("plan_conditie_ID") REFERENCES diwi_testset_simplified.plan_conditie("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_maatwerk_boolean fk_plan_conditie_maatwerk_boolean__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_maatwerk_boolean
    ADD CONSTRAINT fk_plan_conditie_maatwerk_boolean__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_maatwerk_boolean fk_plan_conditie_maatwerk_boolean__plan_conditie; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_maatwerk_boolean
    ADD CONSTRAINT fk_plan_conditie_maatwerk_boolean__plan_conditie FOREIGN KEY ("plan_conditie_ID") REFERENCES diwi_testset_simplified.plan_conditie("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_maatwerk_categorie fk_plan_conditie_maatwerk_categorie__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_maatwerk_categorie
    ADD CONSTRAINT fk_plan_conditie_maatwerk_categorie__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_maatwerk_categorie fk_plan_conditie_maatwerk_categorie__plan_conditie; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_maatwerk_categorie
    ADD CONSTRAINT fk_plan_conditie_maatwerk_categorie__plan_conditie FOREIGN KEY ("plan_conditie_ID") REFERENCES diwi_testset_simplified.plan_conditie("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_maatwerk_categorie fk_plan_conditie_maatwerk_categorie__waarde; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_maatwerk_categorie
    ADD CONSTRAINT fk_plan_conditie_maatwerk_categorie__waarde FOREIGN KEY ("eigenschap_waarde_ID") REFERENCES diwi_testset_simplified.maatwerk_categorie_waarde("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_maatwerk_numeriek fk_plan_conditie_maatwerk_numeriek__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_maatwerk_numeriek
    ADD CONSTRAINT fk_plan_conditie_maatwerk_numeriek__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_maatwerk_numeriek fk_plan_conditie_maatwerk_numeriek__plan_conditie; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_maatwerk_numeriek
    ADD CONSTRAINT fk_plan_conditie_maatwerk_numeriek__plan_conditie FOREIGN KEY ("plan_conditie_ID") REFERENCES diwi_testset_simplified.plan_conditie("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_maatwerk_ordinaal fk_plan_conditie_maatwerk_ordinaal__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_maatwerk_ordinaal
    ADD CONSTRAINT fk_plan_conditie_maatwerk_ordinaal__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_maatwerk_ordinaal fk_plan_conditie_maatwerk_ordinaal__plan_conditie; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_maatwerk_ordinaal
    ADD CONSTRAINT fk_plan_conditie_maatwerk_ordinaal__plan_conditie FOREIGN KEY ("plan_conditie_ID") REFERENCES diwi_testset_simplified.plan_conditie("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_maatwerk_ordinaal fk_plan_conditie_maatwerk_ordinaal__waarde; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_maatwerk_ordinaal
    ADD CONSTRAINT fk_plan_conditie_maatwerk_ordinaal__waarde FOREIGN KEY ("eigenschap_waarde_ID") REFERENCES diwi_testset_simplified.maatwerk_ordinaal_waarde("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_programmering fk_plan_conditie_programmering__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_programmering
    ADD CONSTRAINT fk_plan_conditie_programmering__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_programmering fk_plan_conditie_programmering__plan_conditie; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_programmering
    ADD CONSTRAINT fk_plan_conditie_programmering__plan_conditie FOREIGN KEY ("plan_conditie_ID") REFERENCES diwi_testset_simplified.plan_conditie("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_state fk_plan_conditie_state__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_state
    ADD CONSTRAINT fk_plan_conditie_state__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_state fk_plan_conditie_state__plan; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_state
    ADD CONSTRAINT fk_plan_conditie_state__plan FOREIGN KEY ("plan_ID") REFERENCES diwi_testset_simplified.plan("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_state fk_plan_conditie_state__plan_conditie; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_state
    ADD CONSTRAINT fk_plan_conditie_state__plan_conditie FOREIGN KEY ("plan_conditie_ID") REFERENCES diwi_testset_simplified.plan_conditie("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_type_en_fysiek_voorkomen fk_plan_conditie_type_en_fysiek_voorkomen__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_type_en_fysiek_voorkomen
    ADD CONSTRAINT fk_plan_conditie_type_en_fysiek_voorkomen__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_type_en_fysiek_voorkomen fk_plan_conditie_type_en_fysiek_voorkomen__plan_conditie; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_type_en_fysiek_voorkomen
    ADD CONSTRAINT fk_plan_conditie_type_en_fysiek_voorkomen__plan_conditie FOREIGN KEY ("plan_conditie_ID") REFERENCES diwi_testset_simplified.plan_conditie("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_wijk fk_plan_conditie_wijk__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_wijk
    ADD CONSTRAINT fk_plan_conditie_wijk__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_wijk fk_plan_conditie_wijk__plan_conditie; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_wijk
    ADD CONSTRAINT fk_plan_conditie_wijk__plan_conditie FOREIGN KEY ("plan_conditie_ID") REFERENCES diwi_testset_simplified.plan_conditie("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_conditie_wijk fk_plan_conditie_wijk__wijk; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_conditie_wijk
    ADD CONSTRAINT fk_plan_conditie_wijk__wijk FOREIGN KEY ("wijk_ID") REFERENCES diwi_testset_simplified.wijk("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_soort_state fk_plan_soort_state__plan_soort; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_soort_state
    ADD CONSTRAINT fk_plan_soort_state__plan_soort FOREIGN KEY ("plan_soort_ID") REFERENCES diwi_testset_simplified.plan_soort("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_soort_state fk_plan_soort_state__user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_soort_state
    ADD CONSTRAINT fk_plan_soort_state__user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_state fk_plan_state__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_state
    ADD CONSTRAINT fk_plan_state__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_state fk_plan_state__owner_organization; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_state
    ADD CONSTRAINT fk_plan_state__owner_organization FOREIGN KEY ("owner_organization_ID") REFERENCES diwi_testset_simplified.organization("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_state fk_plan_state__plan; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_state
    ADD CONSTRAINT fk_plan_state__plan FOREIGN KEY ("plan_ID") REFERENCES diwi_testset_simplified.plan("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: plan_state fk_plan_state__plan_soort; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.plan_state
    ADD CONSTRAINT fk_plan_state__plan_soort FOREIGN KEY ("plan_soort_ID") REFERENCES diwi_testset_simplified.plan_soort("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_actor_rol_changelog fk_project_actor_rol_changelog__actor; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_actor_rol_changelog
    ADD CONSTRAINT fk_project_actor_rol_changelog__actor FOREIGN KEY ("actor_ID") REFERENCES diwi_testset_simplified.actor("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_actor_rol_changelog fk_project_actor_rol_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_actor_rol_changelog
    ADD CONSTRAINT fk_project_actor_rol_changelog__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_actor_rol_changelog fk_project_actor_rol_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_actor_rol_changelog
    ADD CONSTRAINT fk_project_actor_rol_changelog__end_milestone FOREIGN KEY ("end_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_actor_rol_changelog fk_project_actor_rol_changelog__project; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_actor_rol_changelog
    ADD CONSTRAINT fk_project_actor_rol_changelog__project FOREIGN KEY ("project_ID") REFERENCES diwi_testset_simplified.project("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_actor_rol_changelog fk_project_actor_rol_changelog__rol; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_actor_rol_changelog
    ADD CONSTRAINT fk_project_actor_rol_changelog__rol FOREIGN KEY ("project_actor_rol_ID") REFERENCES diwi_testset_simplified.project_actor_rol_value("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_actor_rol_changelog fk_project_actor_rol_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_actor_rol_changelog
    ADD CONSTRAINT fk_project_actor_rol_changelog__start_milestone FOREIGN KEY ("start_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_actor_rol_value_state fk_project_actor_rol_value__actor_rol; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_actor_rol_value_state
    ADD CONSTRAINT fk_project_actor_rol_value__actor_rol FOREIGN KEY ("project_actor_rol_value_ID") REFERENCES diwi_testset_simplified.project_actor_rol_value("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_actor_rol_value_state fk_project_actor_rol_value__user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_actor_rol_value_state
    ADD CONSTRAINT fk_project_actor_rol_value__user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_duration_changelog fk_project_duration_chagelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_duration_changelog
    ADD CONSTRAINT fk_project_duration_chagelog__start_milestone FOREIGN KEY ("start_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_duration_changelog fk_project_duration_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_duration_changelog
    ADD CONSTRAINT fk_project_duration_changelog__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_duration_changelog fk_project_duration_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_duration_changelog
    ADD CONSTRAINT fk_project_duration_changelog__end_milestone FOREIGN KEY ("end_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_duration_changelog fk_project_duration_changelog__project; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_duration_changelog
    ADD CONSTRAINT fk_project_duration_changelog__project FOREIGN KEY ("project_ID") REFERENCES diwi_testset_simplified.project("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_fase_changelog fk_project_fase_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_fase_changelog
    ADD CONSTRAINT fk_project_fase_changelog__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_fase_changelog fk_project_fase_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_fase_changelog
    ADD CONSTRAINT fk_project_fase_changelog__end_milestone FOREIGN KEY ("end_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_fase_changelog fk_project_fase_changelog__project; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_fase_changelog
    ADD CONSTRAINT fk_project_fase_changelog__project FOREIGN KEY ("project_ID") REFERENCES diwi_testset_simplified.project("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_fase_changelog fk_project_fase_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_fase_changelog
    ADD CONSTRAINT fk_project_fase_changelog__start_milestone FOREIGN KEY ("start_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_gemeenterol_changelog fk_project_gemeenterol_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_gemeenterol_changelog
    ADD CONSTRAINT fk_project_gemeenterol_changelog__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_gemeenterol_changelog fk_project_gemeenterol_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_gemeenterol_changelog
    ADD CONSTRAINT fk_project_gemeenterol_changelog__end_milestone FOREIGN KEY ("end_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_gemeenterol_changelog fk_project_gemeenterol_changelog__gemeenterol; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_gemeenterol_changelog
    ADD CONSTRAINT fk_project_gemeenterol_changelog__gemeenterol FOREIGN KEY ("project_gemeenterol_value_ID") REFERENCES diwi_testset_simplified.project_gemeenterol_value("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_gemeenterol_changelog fk_project_gemeenterol_changelog__project; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_gemeenterol_changelog
    ADD CONSTRAINT fk_project_gemeenterol_changelog__project FOREIGN KEY ("project_ID") REFERENCES diwi_testset_simplified.project("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_priorisering_changelog fk_project_gemeenterol_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_priorisering_changelog
    ADD CONSTRAINT fk_project_gemeenterol_changelog__start_milestone FOREIGN KEY ("start_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_gemeenterol_value_state fk_project_gemeenterol_value_state__gemeenterol; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_gemeenterol_value_state
    ADD CONSTRAINT fk_project_gemeenterol_value_state__gemeenterol FOREIGN KEY ("project_gemeenterol_value_ID") REFERENCES diwi_testset_simplified.project_gemeenterol_value("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_gemeenterol_value_state fk_project_gemeenterol_value_state__user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_gemeenterol_value_state
    ADD CONSTRAINT fk_project_gemeenterol_value_state__user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_boolean_eigenschap_changelog fk_project_maatwerk_boolean_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_maatwerk_boolean_eigenschap_changelog
    ADD CONSTRAINT fk_project_maatwerk_boolean_changelog__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_boolean_eigenschap_changelog fk_project_maatwerk_boolean_changelog__eigenschap; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_maatwerk_boolean_eigenschap_changelog
    ADD CONSTRAINT fk_project_maatwerk_boolean_changelog__eigenschap FOREIGN KEY ("eigenschap_ID") REFERENCES diwi_testset_simplified.maatwerk_eigenschap("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_boolean_eigenschap_changelog fk_project_maatwerk_boolean_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_maatwerk_boolean_eigenschap_changelog
    ADD CONSTRAINT fk_project_maatwerk_boolean_changelog__end_milestone FOREIGN KEY ("end_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_boolean_eigenschap_changelog fk_project_maatwerk_boolean_changelog__project; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_maatwerk_boolean_eigenschap_changelog
    ADD CONSTRAINT fk_project_maatwerk_boolean_changelog__project FOREIGN KEY ("project_ID") REFERENCES diwi_testset_simplified.project("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_boolean_eigenschap_changelog fk_project_maatwerk_boolean_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_maatwerk_boolean_eigenschap_changelog
    ADD CONSTRAINT fk_project_maatwerk_boolean_changelog__start_milestone FOREIGN KEY ("start_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_categorie_eigenschap_changelog fk_project_maatwerk_categorie_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_maatwerk_categorie_eigenschap_changelog
    ADD CONSTRAINT fk_project_maatwerk_categorie_changelog__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_categorie_eigenschap_changelog fk_project_maatwerk_categorie_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_maatwerk_categorie_eigenschap_changelog
    ADD CONSTRAINT fk_project_maatwerk_categorie_changelog__end_milestone FOREIGN KEY ("end_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_categorie_eigenschap_changelog fk_project_maatwerk_categorie_changelog__project; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_maatwerk_categorie_eigenschap_changelog
    ADD CONSTRAINT fk_project_maatwerk_categorie_changelog__project FOREIGN KEY ("project_ID") REFERENCES diwi_testset_simplified.project("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_categorie_eigenschap_changelog fk_project_maatwerk_categorie_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_maatwerk_categorie_eigenschap_changelog
    ADD CONSTRAINT fk_project_maatwerk_categorie_changelog__start_milestone FOREIGN KEY ("start_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_categorie_eigenschap_changelog fk_project_maatwerk_categorie_changelog__waarde; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_maatwerk_categorie_eigenschap_changelog
    ADD CONSTRAINT fk_project_maatwerk_categorie_changelog__waarde FOREIGN KEY ("eigenschap_waarde_ID") REFERENCES diwi_testset_simplified.maatwerk_categorie_waarde("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_numeriek_eigenschap_changelog fk_project_maatwerk_numeriek_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_maatwerk_numeriek_eigenschap_changelog
    ADD CONSTRAINT fk_project_maatwerk_numeriek_changelog__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_numeriek_eigenschap_changelog fk_project_maatwerk_numeriek_changelog__eigenschap; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_maatwerk_numeriek_eigenschap_changelog
    ADD CONSTRAINT fk_project_maatwerk_numeriek_changelog__eigenschap FOREIGN KEY ("eigenschap_ID") REFERENCES diwi_testset_simplified.maatwerk_eigenschap("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_numeriek_eigenschap_changelog fk_project_maatwerk_numeriek_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_maatwerk_numeriek_eigenschap_changelog
    ADD CONSTRAINT fk_project_maatwerk_numeriek_changelog__end_milestone FOREIGN KEY ("end_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_numeriek_eigenschap_changelog fk_project_maatwerk_numeriek_changelog__project; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_maatwerk_numeriek_eigenschap_changelog
    ADD CONSTRAINT fk_project_maatwerk_numeriek_changelog__project FOREIGN KEY ("project_ID") REFERENCES diwi_testset_simplified.project("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_numeriek_eigenschap_changelog fk_project_maatwerk_numeriek_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_maatwerk_numeriek_eigenschap_changelog
    ADD CONSTRAINT fk_project_maatwerk_numeriek_changelog__start_milestone FOREIGN KEY ("start_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_ordinaal_eigenschap_changelog fk_project_maatwerk_ordinaal_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_maatwerk_ordinaal_eigenschap_changelog
    ADD CONSTRAINT fk_project_maatwerk_ordinaal_changelog__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_ordinaal_eigenschap_changelog fk_project_maatwerk_ordinaal_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_maatwerk_ordinaal_eigenschap_changelog
    ADD CONSTRAINT fk_project_maatwerk_ordinaal_changelog__end_milestone FOREIGN KEY ("end_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_ordinaal_eigenschap_changelog fk_project_maatwerk_ordinaal_changelog__project; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_maatwerk_ordinaal_eigenschap_changelog
    ADD CONSTRAINT fk_project_maatwerk_ordinaal_changelog__project FOREIGN KEY ("project_ID") REFERENCES diwi_testset_simplified.project("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_ordinaal_eigenschap_changelog fk_project_maatwerk_ordinaal_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_maatwerk_ordinaal_eigenschap_changelog
    ADD CONSTRAINT fk_project_maatwerk_ordinaal_changelog__start_milestone FOREIGN KEY ("start_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_maatwerk_ordinaal_eigenschap_changelog fk_project_maatwerk_ordinaal_changelog__waarde; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_maatwerk_ordinaal_eigenschap_changelog
    ADD CONSTRAINT fk_project_maatwerk_ordinaal_changelog__waarde FOREIGN KEY ("eigenschap_waarde_ID") REFERENCES diwi_testset_simplified.maatwerk_ordinaal_waarde("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_name_changelog fk_project_name_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_name_changelog
    ADD CONSTRAINT fk_project_name_changelog__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_name_changelog fk_project_name_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_name_changelog
    ADD CONSTRAINT fk_project_name_changelog__end_milestone FOREIGN KEY ("end_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_name_changelog fk_project_name_changelog__project; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_name_changelog
    ADD CONSTRAINT fk_project_name_changelog__project FOREIGN KEY ("project_ID") REFERENCES diwi_testset_simplified.project("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_name_changelog fk_project_name_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_name_changelog
    ADD CONSTRAINT fk_project_name_changelog__start_milestone FOREIGN KEY ("start_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_plan_type_changelog fk_project_plan_type_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_plan_type_changelog
    ADD CONSTRAINT fk_project_plan_type_changelog__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_plan_type_changelog fk_project_plan_type_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_plan_type_changelog
    ADD CONSTRAINT fk_project_plan_type_changelog__end_milestone FOREIGN KEY ("end_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_plan_type_changelog fk_project_plan_type_changelog__project; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_plan_type_changelog
    ADD CONSTRAINT fk_project_plan_type_changelog__project FOREIGN KEY ("project_ID") REFERENCES diwi_testset_simplified.project("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_plan_type_changelog fk_project_plan_type_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_plan_type_changelog
    ADD CONSTRAINT fk_project_plan_type_changelog__start_milestone FOREIGN KEY ("start_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_planologische_planstatus_changelog fk_project_planologische_planstatus_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_planologische_planstatus_changelog
    ADD CONSTRAINT fk_project_planologische_planstatus_changelog__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_planologische_planstatus_changelog fk_project_planologische_planstatus_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_planologische_planstatus_changelog
    ADD CONSTRAINT fk_project_planologische_planstatus_changelog__end_milestone FOREIGN KEY ("end_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_planologische_planstatus_changelog fk_project_planologische_planstatus_changelog__project; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_planologische_planstatus_changelog
    ADD CONSTRAINT fk_project_planologische_planstatus_changelog__project FOREIGN KEY ("project_ID") REFERENCES diwi_testset_simplified.project("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_planologische_planstatus_changelog fk_project_planologische_planstatus_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_planologische_planstatus_changelog
    ADD CONSTRAINT fk_project_planologische_planstatus_changelog__start_milestone FOREIGN KEY ("start_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_priorisering_changelog fk_project_priorisering_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_priorisering_changelog
    ADD CONSTRAINT fk_project_priorisering_changelog__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_priorisering_changelog fk_project_priorisering_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_priorisering_changelog
    ADD CONSTRAINT fk_project_priorisering_changelog__end_milestone FOREIGN KEY ("end_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_priorisering_changelog fk_project_priorisering_changelog__priorisering; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_priorisering_changelog
    ADD CONSTRAINT fk_project_priorisering_changelog__priorisering FOREIGN KEY ("project_priorisering_value_ID") REFERENCES diwi_testset_simplified.project_priorisering_value("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_priorisering_changelog fk_project_priorisering_changelog__project; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_priorisering_changelog
    ADD CONSTRAINT fk_project_priorisering_changelog__project FOREIGN KEY ("project_ID") REFERENCES diwi_testset_simplified.project("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_priorisering_changelog fk_project_priorisering_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_priorisering_changelog
    ADD CONSTRAINT fk_project_priorisering_changelog__start_milestone FOREIGN KEY ("start_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_priorisering_value_state fk_project_priorisering_value_state__priorisering; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_priorisering_value_state
    ADD CONSTRAINT fk_project_priorisering_value_state__priorisering FOREIGN KEY ("project_priorisering_value_ID") REFERENCES diwi_testset_simplified.project_priorisering_value("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_priorisering_value_state fk_project_priorisering_value_state__user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_priorisering_value_state
    ADD CONSTRAINT fk_project_priorisering_value_state__user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_programmering_changelog fk_project_programmering_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_programmering_changelog
    ADD CONSTRAINT fk_project_programmering_changelog__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_programmering_changelog fk_project_programmering_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_programmering_changelog
    ADD CONSTRAINT fk_project_programmering_changelog__end_milestone FOREIGN KEY ("end_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_programmering_changelog fk_project_programmering_changelog__project; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_programmering_changelog
    ADD CONSTRAINT fk_project_programmering_changelog__project FOREIGN KEY ("project_ID") REFERENCES diwi_testset_simplified.project("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_programmering_changelog fk_project_programmering_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_programmering_changelog
    ADD CONSTRAINT fk_project_programmering_changelog__start_milestone FOREIGN KEY ("start_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_state fk_project_state__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_state
    ADD CONSTRAINT fk_project_state__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_state fk_project_state__owner_organization; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_state
    ADD CONSTRAINT fk_project_state__owner_organization FOREIGN KEY ("owner_organization_ID") REFERENCES diwi_testset_simplified.organization("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: project_state fk_project_state__project; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.project_state
    ADD CONSTRAINT fk_project_state__project FOREIGN KEY ("project_ID") REFERENCES diwi_testset_simplified.project("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: software_module_rights fk_software_module_rights__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.software_module_rights
    ADD CONSTRAINT fk_software_module_rights__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: software_module_rights fk_software_module_rights__organization; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.software_module_rights
    ADD CONSTRAINT fk_software_module_rights__organization FOREIGN KEY ("organization_ID") REFERENCES diwi_testset_simplified.organization("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: user_state fk_user_state__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.user_state
    ADD CONSTRAINT fk_user_state__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: user_state fk_user_state__user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.user_state
    ADD CONSTRAINT fk_user_state__user FOREIGN KEY ("user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: user_to_organization fk_user_to_organization__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.user_to_organization
    ADD CONSTRAINT fk_user_to_organization__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: user_to_organization fk_user_to_organization__organization; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.user_to_organization
    ADD CONSTRAINT fk_user_to_organization__organization FOREIGN KEY ("organization_ID") REFERENCES diwi_testset_simplified.organization("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: user_to_organization fk_user_to_organization__user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.user_to_organization
    ADD CONSTRAINT fk_user_to_organization__user FOREIGN KEY ("user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: wijk_state fk_wijk_state__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.wijk_state
    ADD CONSTRAINT fk_wijk_state__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: wijk_state fk_wijk_state__gemeente; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.wijk_state
    ADD CONSTRAINT fk_wijk_state__gemeente FOREIGN KEY ("gemeente_ID") REFERENCES diwi_testset_simplified.gemeente("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: wijk_state fk_wijk_state__wijk; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.wijk_state
    ADD CONSTRAINT fk_wijk_state__wijk FOREIGN KEY ("wijk_ID") REFERENCES diwi_testset_simplified.wijk("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_doelgroep_changelog fk_woningblok_doelgroep_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_doelgroep_changelog
    ADD CONSTRAINT fk_woningblok_doelgroep_changelog__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_doelgroep_changelog fk_woningblok_doelgroep_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_doelgroep_changelog
    ADD CONSTRAINT fk_woningblok_doelgroep_changelog__end_milestone FOREIGN KEY ("end_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_doelgroep_changelog fk_woningblok_doelgroep_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_doelgroep_changelog
    ADD CONSTRAINT fk_woningblok_doelgroep_changelog__start_milestone FOREIGN KEY ("start_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_doelgroep_changelog fk_woningblok_doelgroep_changelog__woningblok; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_doelgroep_changelog
    ADD CONSTRAINT fk_woningblok_doelgroep_changelog__woningblok FOREIGN KEY ("woningblok_ID") REFERENCES diwi_testset_simplified.woningblok("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_duration_changelog fk_woningblok_duration_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_duration_changelog
    ADD CONSTRAINT fk_woningblok_duration_changelog__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_duration_changelog fk_woningblok_duration_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_duration_changelog
    ADD CONSTRAINT fk_woningblok_duration_changelog__end_milestone FOREIGN KEY ("end_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_duration_changelog fk_woningblok_duration_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_duration_changelog
    ADD CONSTRAINT fk_woningblok_duration_changelog__start_milestone FOREIGN KEY ("start_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_duration_changelog fk_woningblok_duration_changelog__woningblok; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_duration_changelog
    ADD CONSTRAINT fk_woningblok_duration_changelog__woningblok FOREIGN KEY ("woningblok_ID") REFERENCES diwi_testset_simplified.woningblok("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_eigendom_en_waarde_changelog fk_woningblok_eigendom_en_waarde_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_eigendom_en_waarde_changelog
    ADD CONSTRAINT fk_woningblok_eigendom_en_waarde_changelog__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_eigendom_en_waarde_changelog fk_woningblok_eigendom_en_waarde_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_eigendom_en_waarde_changelog
    ADD CONSTRAINT fk_woningblok_eigendom_en_waarde_changelog__end_milestone FOREIGN KEY ("end_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_eigendom_en_waarde_changelog fk_woningblok_eigendom_en_waarde_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_eigendom_en_waarde_changelog
    ADD CONSTRAINT fk_woningblok_eigendom_en_waarde_changelog__start_milestone FOREIGN KEY ("start_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_eigendom_en_waarde_changelog fk_woningblok_eigendom_en_waarde_changelog__woningblok; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_eigendom_en_waarde_changelog
    ADD CONSTRAINT fk_woningblok_eigendom_en_waarde_changelog__woningblok FOREIGN KEY ("woningblok_ID") REFERENCES diwi_testset_simplified.woningblok("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_grondpositie_changelog fk_woningblok_grondpositie_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_grondpositie_changelog
    ADD CONSTRAINT fk_woningblok_grondpositie_changelog__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_grondpositie_changelog fk_woningblok_grondpositie_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_grondpositie_changelog
    ADD CONSTRAINT fk_woningblok_grondpositie_changelog__end_milestone FOREIGN KEY ("end_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_grondpositie_changelog fk_woningblok_grondpositie_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_grondpositie_changelog
    ADD CONSTRAINT fk_woningblok_grondpositie_changelog__start_milestone FOREIGN KEY ("start_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_grondpositie_changelog fk_woningblok_grondpositie_changelog__woningblok; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_grondpositie_changelog
    ADD CONSTRAINT fk_woningblok_grondpositie_changelog__woningblok FOREIGN KEY ("woningblok_ID") REFERENCES diwi_testset_simplified.woningblok("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_grootte_changelog fk_woningblok_grootte_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_grootte_changelog
    ADD CONSTRAINT fk_woningblok_grootte_changelog__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_grootte_changelog fk_woningblok_grootte_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_grootte_changelog
    ADD CONSTRAINT fk_woningblok_grootte_changelog__end_milestone FOREIGN KEY ("end_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_grootte_changelog fk_woningblok_grootte_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_grootte_changelog
    ADD CONSTRAINT fk_woningblok_grootte_changelog__start_milestone FOREIGN KEY ("start_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_grootte_changelog fk_woningblok_grootte_changelog__woningblok; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_grootte_changelog
    ADD CONSTRAINT fk_woningblok_grootte_changelog__woningblok FOREIGN KEY ("woningblok_ID") REFERENCES diwi_testset_simplified.woningblok("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_kadastrale_koppeling_changelog fk_woningblok_kadastrale_koppeling_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_kadastrale_koppeling_changelog
    ADD CONSTRAINT fk_woningblok_kadastrale_koppeling_changelog__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_kadastrale_koppeling_changelog fk_woningblok_kadastrale_koppeling_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_kadastrale_koppeling_changelog
    ADD CONSTRAINT fk_woningblok_kadastrale_koppeling_changelog__end_milestone FOREIGN KEY ("end_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_kadastrale_koppeling_changelog fk_woningblok_kadastrale_koppeling_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_kadastrale_koppeling_changelog
    ADD CONSTRAINT fk_woningblok_kadastrale_koppeling_changelog__start_milestone FOREIGN KEY ("start_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_kadastrale_koppeling_changelog fk_woningblok_kadastrale_koppeling_changelog__woningblok; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_kadastrale_koppeling_changelog
    ADD CONSTRAINT fk_woningblok_kadastrale_koppeling_changelog__woningblok FOREIGN KEY ("woningblok_ID") REFERENCES diwi_testset_simplified.woningblok("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_boolean_eigenschap_changelog fk_woningblok_maatwerk_boolean_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_maatwerk_boolean_eigenschap_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_boolean_changelog__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_boolean_eigenschap_changelog fk_woningblok_maatwerk_boolean_changelog__eigenschap; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_maatwerk_boolean_eigenschap_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_boolean_changelog__eigenschap FOREIGN KEY ("eigenschap_ID") REFERENCES diwi_testset_simplified.maatwerk_eigenschap("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_boolean_eigenschap_changelog fk_woningblok_maatwerk_boolean_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_maatwerk_boolean_eigenschap_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_boolean_changelog__end_milestone FOREIGN KEY ("end_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_boolean_eigenschap_changelog fk_woningblok_maatwerk_boolean_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_maatwerk_boolean_eigenschap_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_boolean_changelog__start_milestone FOREIGN KEY ("start_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_boolean_eigenschap_changelog fk_woningblok_maatwerk_boolean_changelog__woningblok; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_maatwerk_boolean_eigenschap_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_boolean_changelog__woningblok FOREIGN KEY ("woningblok_ID") REFERENCES diwi_testset_simplified.woningblok("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_categorie_eigenschap_changelog fk_woningblok_maatwerk_categorie_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_maatwerk_categorie_eigenschap_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_categorie_changelog__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_categorie_eigenschap_changelog fk_woningblok_maatwerk_categorie_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_maatwerk_categorie_eigenschap_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_categorie_changelog__end_milestone FOREIGN KEY ("end_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_categorie_eigenschap_changelog fk_woningblok_maatwerk_categorie_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_maatwerk_categorie_eigenschap_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_categorie_changelog__start_milestone FOREIGN KEY ("start_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_categorie_eigenschap_changelog fk_woningblok_maatwerk_categorie_changelog__waarde; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_maatwerk_categorie_eigenschap_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_categorie_changelog__waarde FOREIGN KEY ("eigenschap_waarde_ID") REFERENCES diwi_testset_simplified.maatwerk_categorie_waarde("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_categorie_eigenschap_changelog fk_woningblok_maatwerk_categorie_changelog__woningblok; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_maatwerk_categorie_eigenschap_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_categorie_changelog__woningblok FOREIGN KEY ("woningblok_ID") REFERENCES diwi_testset_simplified.woningblok("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: maatwerk_categorie_waarde_state fk_woningblok_maatwerk_categorie_waarde_state__categorie_waarde; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.maatwerk_categorie_waarde_state
    ADD CONSTRAINT fk_woningblok_maatwerk_categorie_waarde_state__categorie_waarde FOREIGN KEY ("categorie_waarde_ID") REFERENCES diwi_testset_simplified.maatwerk_categorie_waarde("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: maatwerk_categorie_waarde_state fk_woningblok_maatwerk_categorie_waarde_state__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.maatwerk_categorie_waarde_state
    ADD CONSTRAINT fk_woningblok_maatwerk_categorie_waarde_state__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: maatwerk_categorie_waarde_state fk_woningblok_maatwerk_categorie_waarde_state__eigenschap; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.maatwerk_categorie_waarde_state
    ADD CONSTRAINT fk_woningblok_maatwerk_categorie_waarde_state__eigenschap FOREIGN KEY ("maatwerk_eigenschap_ID") REFERENCES diwi_testset_simplified.maatwerk_eigenschap("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: maatwerk_eigenschap_state fk_woningblok_maatwerk_eigenschap_state__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.maatwerk_eigenschap_state
    ADD CONSTRAINT fk_woningblok_maatwerk_eigenschap_state__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: maatwerk_eigenschap_state fk_woningblok_maatwerk_eigenschap_state__eigenschap; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.maatwerk_eigenschap_state
    ADD CONSTRAINT fk_woningblok_maatwerk_eigenschap_state__eigenschap FOREIGN KEY ("eigenschap_ID") REFERENCES diwi_testset_simplified.maatwerk_eigenschap("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_numeriek_eigenschap_changelog fk_woningblok_maatwerk_numeriek_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_maatwerk_numeriek_eigenschap_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_numeriek_changelog__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_numeriek_eigenschap_changelog fk_woningblok_maatwerk_numeriek_changelog__eigenschap; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_maatwerk_numeriek_eigenschap_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_numeriek_changelog__eigenschap FOREIGN KEY ("eigenschap_ID") REFERENCES diwi_testset_simplified.maatwerk_eigenschap("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_numeriek_eigenschap_changelog fk_woningblok_maatwerk_numeriek_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_maatwerk_numeriek_eigenschap_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_numeriek_changelog__end_milestone FOREIGN KEY ("end_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_numeriek_eigenschap_changelog fk_woningblok_maatwerk_numeriek_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_maatwerk_numeriek_eigenschap_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_numeriek_changelog__start_milestone FOREIGN KEY ("start_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_numeriek_eigenschap_changelog fk_woningblok_maatwerk_numeriek_changelog__woningblok; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_maatwerk_numeriek_eigenschap_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_numeriek_changelog__woningblok FOREIGN KEY ("woningblok_ID") REFERENCES diwi_testset_simplified.woningblok("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_ordinaal_eigenschap_changelog fk_woningblok_maatwerk_ordinaal_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_maatwerk_ordinaal_eigenschap_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_ordinaal_changelog__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_ordinaal_eigenschap_changelog fk_woningblok_maatwerk_ordinaal_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_maatwerk_ordinaal_eigenschap_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_ordinaal_changelog__end_milestone FOREIGN KEY ("end_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_ordinaal_eigenschap_changelog fk_woningblok_maatwerk_ordinaal_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_maatwerk_ordinaal_eigenschap_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_ordinaal_changelog__start_milestone FOREIGN KEY ("start_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_ordinaal_eigenschap_changelog fk_woningblok_maatwerk_ordinaal_changelog__waarde; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_maatwerk_ordinaal_eigenschap_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_ordinaal_changelog__waarde FOREIGN KEY ("eigenschap_waarde_ID") REFERENCES diwi_testset_simplified.maatwerk_ordinaal_waarde("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_maatwerk_ordinaal_eigenschap_changelog fk_woningblok_maatwerk_ordinaal_changelog__woningblok; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_maatwerk_ordinaal_eigenschap_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_ordinaal_changelog__woningblok FOREIGN KEY ("woningblok_ID") REFERENCES diwi_testset_simplified.woningblok("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: maatwerk_ordinaal_waarde_state fk_woningblok_maatwerk_ordinale_waarde_state__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.maatwerk_ordinaal_waarde_state
    ADD CONSTRAINT fk_woningblok_maatwerk_ordinale_waarde_state__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: maatwerk_ordinaal_waarde_state fk_woningblok_maatwerk_ordinale_waarde_state__eigenschap; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.maatwerk_ordinaal_waarde_state
    ADD CONSTRAINT fk_woningblok_maatwerk_ordinale_waarde_state__eigenschap FOREIGN KEY ("maatwerk_eigenschap_ID") REFERENCES diwi_testset_simplified.maatwerk_eigenschap("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: maatwerk_ordinaal_waarde_state fk_woningblok_maatwerk_ordinale_waarde_state__ordinale_waarde; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.maatwerk_ordinaal_waarde_state
    ADD CONSTRAINT fk_woningblok_maatwerk_ordinale_waarde_state__ordinale_waarde FOREIGN KEY ("ordinaal_waarde_ID") REFERENCES diwi_testset_simplified.maatwerk_ordinaal_waarde("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_mutatie_changelog fk_woningblok_mutatie_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_mutatie_changelog
    ADD CONSTRAINT fk_woningblok_mutatie_changelog__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_mutatie_changelog fk_woningblok_mutatie_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_mutatie_changelog
    ADD CONSTRAINT fk_woningblok_mutatie_changelog__end_milestone FOREIGN KEY ("end_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_mutatie_changelog fk_woningblok_mutatie_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_mutatie_changelog
    ADD CONSTRAINT fk_woningblok_mutatie_changelog__start_milestone FOREIGN KEY ("start_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_mutatie_changelog fk_woningblok_mutatie_changelog__woningblok; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_mutatie_changelog
    ADD CONSTRAINT fk_woningblok_mutatie_changelog__woningblok FOREIGN KEY ("woningblok_ID") REFERENCES diwi_testset_simplified.woningblok("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_naam_changelog fk_woningblok_naam_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_naam_changelog
    ADD CONSTRAINT fk_woningblok_naam_changelog__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_naam_changelog fk_woningblok_naam_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_naam_changelog
    ADD CONSTRAINT fk_woningblok_naam_changelog__end_milestone FOREIGN KEY ("end_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_naam_changelog fk_woningblok_naam_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_naam_changelog
    ADD CONSTRAINT fk_woningblok_naam_changelog__start_milestone FOREIGN KEY ("start_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_naam_changelog fk_woningblok_naam_changelog__woningblok; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_naam_changelog
    ADD CONSTRAINT fk_woningblok_naam_changelog__woningblok FOREIGN KEY ("woningblok_ID") REFERENCES diwi_testset_simplified.woningblok("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_state fk_woningblok_state__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_state
    ADD CONSTRAINT fk_woningblok_state__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_state fk_woningblok_state__project; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_state
    ADD CONSTRAINT fk_woningblok_state__project FOREIGN KEY ("project_ID") REFERENCES diwi_testset_simplified.project("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_state fk_woningblok_state__woningblok; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_state
    ADD CONSTRAINT fk_woningblok_state__woningblok FOREIGN KEY ("woningblok_ID") REFERENCES diwi_testset_simplified.woningblok("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_type_en_fysiek_voorkomen_changelog fk_woningblok_type_en_voorkomen_changelog__change_user; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_type_en_fysiek_voorkomen_changelog
    ADD CONSTRAINT fk_woningblok_type_en_voorkomen_changelog__change_user FOREIGN KEY ("change_user_ID") REFERENCES diwi_testset_simplified."user"("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_type_en_fysiek_voorkomen_changelog fk_woningblok_type_en_voorkomen_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_type_en_fysiek_voorkomen_changelog
    ADD CONSTRAINT fk_woningblok_type_en_voorkomen_changelog__end_milestone FOREIGN KEY ("end_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_type_en_fysiek_voorkomen_changelog fk_woningblok_type_en_voorkomen_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_type_en_fysiek_voorkomen_changelog
    ADD CONSTRAINT fk_woningblok_type_en_voorkomen_changelog__start_milestone FOREIGN KEY ("start_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_type_en_fysiek_voorkomen_changelog fk_woningblok_type_en_voorkomen_changelog__woningblok; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_type_en_fysiek_voorkomen_changelog
    ADD CONSTRAINT fk_woningblok_type_en_voorkomen_changelog__woningblok FOREIGN KEY ("woningblok_ID") REFERENCES diwi_testset_simplified.woningblok("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_buurt_changelog woningblok_buurt_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_buurt_changelog
    ADD CONSTRAINT woningblok_buurt_changelog__end_milestone FOREIGN KEY ("end_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_buurt_changelog woningblok_buurt_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_buurt_changelog
    ADD CONSTRAINT woningblok_buurt_changelog__start_milestone FOREIGN KEY ("start_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_buurt_changelog woningblok_buurt_changelog__wijk; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_buurt_changelog
    ADD CONSTRAINT woningblok_buurt_changelog__wijk FOREIGN KEY ("buurt_ID") REFERENCES diwi_testset_simplified.buurt("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_buurt_changelog woningblok_buurt_changelog__woningblok; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_buurt_changelog
    ADD CONSTRAINT woningblok_buurt_changelog__woningblok FOREIGN KEY ("woningblok_ID") REFERENCES diwi_testset_simplified.woningblok("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_gemeente_changelog woningblok_gemeente_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_gemeente_changelog
    ADD CONSTRAINT woningblok_gemeente_changelog__end_milestone FOREIGN KEY ("end_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_gemeente_changelog woningblok_gemeente_changelog__gemeente; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_gemeente_changelog
    ADD CONSTRAINT woningblok_gemeente_changelog__gemeente FOREIGN KEY ("gemeente_ID") REFERENCES diwi_testset_simplified.gemeente("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_gemeente_changelog woningblok_gemeente_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_gemeente_changelog
    ADD CONSTRAINT woningblok_gemeente_changelog__start_milestone FOREIGN KEY ("start_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_gemeente_changelog woningblok_gemeente_changelog__woningblok; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_gemeente_changelog
    ADD CONSTRAINT woningblok_gemeente_changelog__woningblok FOREIGN KEY ("woningblok_ID") REFERENCES diwi_testset_simplified.woningblok("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_wijk_changelog woningblok_wijk_changelog__end_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_wijk_changelog
    ADD CONSTRAINT woningblok_wijk_changelog__end_milestone FOREIGN KEY ("end_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_wijk_changelog woningblok_wijk_changelog__start_milestone; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_wijk_changelog
    ADD CONSTRAINT woningblok_wijk_changelog__start_milestone FOREIGN KEY ("start_milestone_ID") REFERENCES diwi_testset_simplified.milestone("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_wijk_changelog woningblok_wijk_changelog__wijk; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_wijk_changelog
    ADD CONSTRAINT woningblok_wijk_changelog__wijk FOREIGN KEY ("wijk_ID") REFERENCES diwi_testset_simplified.wijk("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: woningblok_wijk_changelog woningblok_wijk_changelog__woningblok; Type: FK CONSTRAINT; Schema: diwi_testset_simplified; Owner: vng
--

ALTER TABLE ONLY diwi_testset_simplified.woningblok_wijk_changelog
    ADD CONSTRAINT woningblok_wijk_changelog__woningblok FOREIGN KEY ("woningblok_ID") REFERENCES diwi_testset_simplified.woningblok("ID") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

