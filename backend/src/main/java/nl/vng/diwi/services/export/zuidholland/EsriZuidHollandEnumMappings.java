package nl.vng.diwi.services.export.zuidholland;

import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;

import java.time.LocalDate;

public class EsriZuidHollandEnumMappings {

    public static EsriZuidHollandConfidentiality getEsriZuidHollandConfidentiality(Confidentiality diwiConfidentiality) {
        return switch (diwiConfidentiality) {
            case PRIVATE, INTERNAL_CIVIL, INTERNAL_MANAGEMENT, INTERNAL_COUNCIL -> EsriZuidHollandConfidentiality.Gemeente;
            case EXTERNAL_REGIONAL -> EsriZuidHollandConfidentiality.Regio;
            case EXTERNAL_GOVERNMENTAL -> EsriZuidHollandConfidentiality.Provincie;
            case PUBLIC -> EsriZuidHollandConfidentiality.Openbaar;
        };
    }

    public static String getEsriZuidHollandPlanType(PlanType diwiPlanType) {
        EsriZuidHollandPlanType ezhPlanType = switch (diwiPlanType) {
            case PAND_TRANSFORMATIE -> EsriZuidHollandPlanType.Pand_Transformatie;
            case TRANSFORMATIEGEBIED -> EsriZuidHollandPlanType.Transformatiegebied;
            case HERSTRUCTURERING -> EsriZuidHollandPlanType.Herstructurering;
            case VERDICHTING -> EsriZuidHollandPlanType.Verdichting;
            case UITBREIDING_UITLEG -> EsriZuidHollandPlanType.Uitbreiding_uitleg;
            case UITBREIDING_OVERIG -> EsriZuidHollandPlanType.Uitbreiding_overig;
        };

        return ezhPlanType.toString().replaceAll("_", " ");
    }

    public static String getEsriZuidHollandProjectPhase(ProjectPhase diwiProjectPhase, LocalDate projectEndDate) {
        if (projectEndDate.isBefore(LocalDate.now())) {
            return "7. Afgerond";
        }
        return switch (diwiProjectPhase) {
            case _1_CONCEPT -> "0. Studie";
            case _2_INITIATIVE -> "1. Initiatief";
            case _3_DEFINITION -> "2. Definitie";
            case _4_DESIGN -> "3. Ontwerp";
            case _5_PREPARATION -> "4. Voorbereiding";
            case _6_REALIZATION -> "5. Realisatie";
            case _7_AFTERCARE -> "6. Nazorg";
        };
    }

    public static String getEsriZuidHollandPlanningStatus(PlanStatus diwiPlanStatus) {

        if (diwiPlanStatus == null) {
            return "Onbekend";
        }
        return switch (diwiPlanStatus) {
            case _4A_OPGENOMEN_IN_VISIE -> "4A. Visie";
            case _4B_NIET_OPGENOMEN_IN_VISIE -> "4B. Idee";
            case _3_IN_VOORBEREIDING -> "3. In voorbereiding";
            case _2A_VASTGESTELD -> "2A. Vastgesteld";
            case _2B_VASTGESTELD_MET_UITWERKING_NODIG -> "2B. Vastgesteld, uitwerkingsplicht";
            case _2C_VASTGESTELD_MET_BW_NODIG -> "2C. Vastgesteld, wijzigingsbevoegdheid";
            case _1A_ONHERROEPELIJK -> "1A. Onherroepelijk";
            case _1B_ONHERROEPELIJK_MET_UITWERKING_NODIG -> "1B. Onherroepelijk, uitwerkingsplicht";
            case _1C_ONHERROEPELIJK_MET_BW_NODIG -> "1C. Onherroepelijk, wijzigingsbevoegdheid";
        };
    }

    public enum EsriZuidHollandConfidentiality {
        Gemeente,
        Regio,
        Provincie,
        Openbaar;
    }

    public enum EsriZuidHollandPlanType {
        Pand_Transformatie,
        Transformatiegebied,
        Herstructurering,
        Verdichting,
        Uitbreiding_uitleg,
        Uitbreiding_overig,
        Onbekend;
    }

}
