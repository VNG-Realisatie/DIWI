package nl.vng.diwi.dataexchange;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.entities.DataExchangeType;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.ObjectType;
import nl.vng.diwi.dal.entities.enums.PropertyType;
import nl.vng.diwi.generic.ResourceUtil;
import nl.vng.diwi.services.export.zuidholland.EsriZuidHollandExport.EsriZuidHollandProjectProps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class DataExchangeTemplate {

    @Builder.Default
    private List<TemplateProperty> properties = new ArrayList<>();

    /**
     * // Default to an external level, can be overriden for exports meant for internal use.
     */
    @Builder.Default
    private Confidentiality minimumConfidentiality = Confidentiality.EXTERNAL_REGIONAL;

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode
    @AllArgsConstructor
    @Builder
    public static class TemplateProperty {
        private String name;
        private ObjectType objectType;
        private List<PropertyType> propertyTypes;

        @Builder.Default
        private Boolean mandatory = false;

        @Builder.Default
        private Boolean singleSelect = false;
        private List<String> options;
    }

    public static final ImmutableMap<DataExchangeType, DataExchangeTemplate> templates;

    static {
        var zuidHollandTemplate = createZuidHollandTemplate();

        var gelderlandTemplate = createGelderlandTemplate();

        var geoJSONTemplate = new DataExchangeTemplate();
        geoJSONTemplate.setMinimumConfidentiality(Confidentiality.PRIVATE);

        var excelTemplate = new DataExchangeTemplate();
        excelTemplate.setMinimumConfidentiality(Confidentiality.PRIVATE);

        templates = new ImmutableMap.Builder<DataExchangeType, DataExchangeTemplate>()
                .put(DataExchangeType.ESRI_ZUID_HOLLAND, zuidHollandTemplate)
                .put(DataExchangeType.GEO_JSON, geoJSONTemplate)
                .put(DataExchangeType.EXCEL, excelTemplate)
                .put(DataExchangeType.GDB_GELDERLAND, gelderlandTemplate)
                .build();
    }

    private static DataExchangeTemplate createZuidHollandTemplate() {
        var zuidHollandTemplate = new DataExchangeTemplate();

        zuidHollandTemplate.properties.add(new TemplateProperty(
                EsriZuidHollandProjectProps.opdrachtgever_type.name(),
                ObjectType.PROJECT, List.of(PropertyType.CATEGORY),
                true,
                true,
                List.of("Gemeente", "Woningcorporatie", "Projectontwikkelaar", "Particulieren", "Meerdere namelijk", "Anders", "Onbekend")));
        zuidHollandTemplate.properties.add(new TemplateProperty(
                EsriZuidHollandProjectProps.opdrachtgever_naam.name(),
                ObjectType.PROJECT,
                List.of(PropertyType.CATEGORY, PropertyType.TEXT),
                false,
                true,
                null));
        zuidHollandTemplate.properties.add(
                new TemplateProperty(EsriZuidHollandProjectProps.opmerkingen_basis.name(),
                        ObjectType.PROJECT,
                        List.of(PropertyType.TEXT),
                        false,
                        null,
                        null));
        zuidHollandTemplate.properties
                .add(new TemplateProperty(EsriZuidHollandProjectProps.masterplan.name(), ObjectType.PROJECT, List.of(PropertyType.TEXT), false, null, null));
        zuidHollandTemplate.properties.add(
                new TemplateProperty(EsriZuidHollandProjectProps.bestemmingsplan.name(), ObjectType.PROJECT, List.of(PropertyType.TEXT), false, null, null));
        zuidHollandTemplate.properties.add(
                new TemplateProperty(EsriZuidHollandProjectProps.opmerkingen_status.name(), ObjectType.PROJECT, List.of(PropertyType.TEXT), false, null, null));
        zuidHollandTemplate.properties.add(
                new TemplateProperty(EsriZuidHollandProjectProps.beoogd_woonmilieu_ABF5.name(), ObjectType.PROJECT, List.of(PropertyType.CATEGORY), false, true,
                        List.of("Centrum-stedelijk", "Buitencentrum", "Groen-stedelijk", "Dorps", "Landelijk", "Onbekend")));
        zuidHollandTemplate.properties.add(new TemplateProperty(EsriZuidHollandProjectProps.beoogd_woonmilieu_ABF13.name(), ObjectType.PROJECT,
                List.of(PropertyType.CATEGORY), false, true,
                List.of("Centrum-stedelijk-plus", "Centrum-stedelijk", "Centrum-kleinstedelijk", "Stedelijk vooroorlogs",
                        "Stedelijk naoorlogs compact", "Stedelijk naoorlogs grondgebonden", "Kleinstedelijk", "Groen-stedelijk", "Groen-kleinstedelijk",
                        "Centrum-dorps", "Dorps", "Landelijk bereikbaar", "Landelijk perifeer", "Onbekend")));
        zuidHollandTemplate.properties.add(new TemplateProperty(EsriZuidHollandProjectProps.knelpunten_meerkeuze.name(), ObjectType.PROJECT,
                List.of(PropertyType.CATEGORY), false, false,
                List.of("Aansluiting op nutsvoorzieningen", "Bereikbaarheid", "Capaciteit bouwsector", "Capaciteit overheid", "Compensatie bedrijventerreinen",
                        "Geluidhinder – industrie en bedrijven", "Geluidhinder – vliegverkeer", "Geluidhinder – weg- en railverkeer",
                        "Geluidhinder – nestgeluid",
                        "Maatschappelijk draagvlak", "Natuurbescherming", "Netcongestie", "Onrendabele top", "Parkeren", "Procedures - Bezwaarprocedures",
                        "Procedures - Beroepsprocedures", "Stikstof", "Vervuilde grond", "Anders", "Geen", "Onbekend")));
        zuidHollandTemplate.properties.add(new TemplateProperty(EsriZuidHollandProjectProps.toelichting_knelpunten.name(), ObjectType.PROJECT,
                List.of(PropertyType.TEXT), false, null, null));
        zuidHollandTemplate.properties.add(new TemplateProperty("verhuurder_type", ObjectType.PROJECT, List.of(PropertyType.CATEGORY), false, true,
                List.of("Woningcorporatie", "Overig", "Combinatie", "Onbekend")));
        zuidHollandTemplate.properties.add(new TemplateProperty(EsriZuidHollandProjectProps.opmerkingen_kwalitatief.name(), ObjectType.PROJECT,
                List.of(PropertyType.TEXT), false, null, null));
        zuidHollandTemplate.properties
                .add(new TemplateProperty(EsriZuidHollandProjectProps.ph_text1.name(), ObjectType.PROJECT, List.of(PropertyType.CATEGORY), true, true,
                        List.of("1a harde plannen, in voorbereiding of in uitvoering", "1b kansrijke plannen", "1c bestuurlijke afspraken", "2 reserveplannen",
                                "Onbekend")));
        zuidHollandTemplate.properties.add(new TemplateProperty(
                EsriZuidHollandProjectProps.ph_text3.name(),
                ObjectType.PROJECT,
                List.of(PropertyType.CATEGORY),
                false,
                true,
                List.of("Ja", "Nee", "Onbekend")));
        zuidHollandTemplate.properties
                .add(new TemplateProperty(EsriZuidHollandProjectProps.ph_short2.name(), ObjectType.PROJECT, List.of(PropertyType.NUMERIC), true, null, null));
        zuidHollandTemplate.properties.add(new TemplateProperty(
                EsriZuidHollandProjectProps.ph_text4.name(),
                ObjectType.PROJECT,
                List.of(PropertyType.CATEGORY),
                false,
                true,
                List.of("Ja", "Nee", "Onbekend")));
        zuidHollandTemplate.properties
                .add(new TemplateProperty(EsriZuidHollandProjectProps.ph_text5.name(), ObjectType.PROJECT, List.of(PropertyType.CATEGORY), false, true,
                        List.of("20%", "40%", "60%", "80%", "100%", "Onbekend")));
        zuidHollandTemplate.properties
                .add(new TemplateProperty(EsriZuidHollandProjectProps.ph_text6.name(), ObjectType.PROJECT, List.of(PropertyType.CATEGORY), false, true,
                        List.of("Onbekend", "Wettelijke norm", "Boven wettelijk", "Volledig energieneutraal")));
        zuidHollandTemplate.properties
                .add(new TemplateProperty(EsriZuidHollandProjectProps.ph_text7.name(), ObjectType.PROJECT, List.of(PropertyType.CATEGORY), false, true,
                        List.of("Onbekend", "Nee", "Ja, minimale aandacht", "Ja, ruime aandacht")));
        zuidHollandTemplate.properties
                .add(new TemplateProperty(EsriZuidHollandProjectProps.ph_text8.name(), ObjectType.PROJECT, List.of(PropertyType.CATEGORY), false, true,
                        List.of("Warmtenet", "Elektra", "Overig", "Onbekend")));
        zuidHollandTemplate.properties
                .add(new TemplateProperty(EsriZuidHollandProjectProps.ph_short7.name(), ObjectType.PROJECT, List.of(PropertyType.NUMERIC), false, null, null));
        zuidHollandTemplate.properties
                .add(new TemplateProperty(EsriZuidHollandProjectProps.ph_short8.name(), ObjectType.PROJECT, List.of(PropertyType.NUMERIC), false, null, null));
        zuidHollandTemplate.properties
                .add(new TemplateProperty(EsriZuidHollandProjectProps.ph_short9.name(), ObjectType.PROJECT, List.of(PropertyType.NUMERIC), false, null, null));
        return zuidHollandTemplate;
    }

    private static DataExchangeTemplate createGelderlandTemplate() {
        return DataExchangeTemplate.builder()
                .properties(ImmutableList.<TemplateProperty>builder()
                        .add(TemplateProperty.builder()
                                .name("gemeente")
                                .objectType(ObjectType.PROJECT)
                                .propertyTypes(List.of(PropertyType.TEXT, PropertyType.CATEGORY))
                                .mandatory(false)
                                .build())

                        .add(TemplateProperty.builder()
                                .name("woonplaats")
                                .objectType(ObjectType.PROJECT)
                                .propertyTypes(List.of(PropertyType.TEXT, PropertyType.CATEGORY))
                                .mandatory(false)
                                .build())

                        .add(TemplateProperty.builder()
                                .name("regio")
                                .objectType(ObjectType.PROJECT)
                                .propertyTypes(List.of(PropertyType.TEXT, PropertyType.CATEGORY))
                                .mandatory(false)
                                .build())

                        .add(TemplateProperty.builder()
                                .name("opdrachtgever_type")
                                .objectType(ObjectType.PROJECT)
                                .propertyTypes(List.of(PropertyType.CATEGORY))
                                .mandatory(false)
                                .options(List.of(
                                        "Gemeente",
                                        "Woningbouwcorporatie",
                                        "Projectontwikkelaar",
                                        "Particulieren",
                                        "Meerdere namelijk",
                                        "Anders",
                                        "Onbekend"))
                                .build())
                        .add(TemplateProperty.builder()
                                .name("opdrachtgever_naam")
                                .objectType(ObjectType.PROJECT)
                                .propertyTypes(List.of(PropertyType.TEXT))
                                .mandatory(false)
                                .build())
                        .add(TemplateProperty.builder()
                                .name("opmerkingen_basis")
                                .objectType(ObjectType.PROJECT)
                                .propertyTypes(List.of(PropertyType.TEXT))
                                .mandatory(false)
                                .build())
                        .add(TemplateProperty.builder()
                                .name("masterplan")
                                .objectType(ObjectType.PROJECT)
                                .propertyTypes(List.of(PropertyType.TEXT))
                                .mandatory(false)
                                .build())
                        .add(TemplateProperty.builder()
                                .name("bestemmingsplan")
                                .objectType(ObjectType.PROJECT)
                                .propertyTypes(List.of(PropertyType.TEXT))
                                .mandatory(false)
                                .build())
                        .add(TemplateProperty.builder()
                                .name("zoekgebied")
                                .objectType(ObjectType.PROJECT)
                                .propertyTypes(List.of(PropertyType.TEXT))
                                .mandatory(false)
                                .build())
                        .add(TemplateProperty.builder()
                                .name("opmerkingen_status")
                                .objectType(ObjectType.PROJECT)
                                .propertyTypes(List.of(PropertyType.TEXT))
                                .mandatory(false)
                                .build())

                        .add(TemplateProperty.builder()
                                .name("beoogd_woonmilieu_ABF5")
                                .objectType(ObjectType.PROJECT)
                                .propertyTypes(List.of(PropertyType.CATEGORY))
                                .mandatory(false)
                                .options(List.of(
                                        "Centrum-stedelijk",
                                        "Buitencentrum",
                                        "Groen-stedelijk",
                                        "Dorps",
                                        "Landelijk",
                                        "Onbekend"))
                                .build())
                        .add(TemplateProperty.builder()
                                .name("beoogd_woonmilieu_ABF13")
                                .objectType(ObjectType.PROJECT)
                                .propertyTypes(List.of(PropertyType.CATEGORY))
                                .mandatory(false)
                                .options(List.of(
                                        "Centrum-stedelijk-plus",
                                        "Centrum-stedelijk",
                                        "Centrum-kleinstedelijk",
                                        "Stedelijk vooroorlogs",
                                        "Stedelijk naoorlogs compact",
                                        "Stedelijk naoorlogs grondgebonden",
                                        "Kleinstedelijk",
                                        "Groen-stedelijk",
                                        "Groen-kleinstedelijk",
                                        "Centrum-dorps",
                                        "Dorps",
                                        "Landelijk bereikbaar",
                                        "Landelijk perifeer",
                                        "Onbekend"))
                                .build())
                        // Knelpunten meerkeuze

                        .add(TemplateProperty.builder()
                                .name("toelichting_knelpunten")
                                .objectType(ObjectType.PROJECT)
                                .propertyTypes(List.of(PropertyType.TEXT))
                                .mandatory(false)
                                .build())

                        .add(TemplateProperty.builder()
                                .name("verhuurder_type")
                                .objectType(ObjectType.PROJECT)
                                .propertyTypes(List.of(PropertyType.CATEGORY))
                                .mandatory(false)
                                .options(List.of("Woningcorporatie", "Overig", "Combinatie", "Onbekend"))
                                .build())

                        .add(TemplateProperty.builder()
                                .name("opmerkingen_kwalitatief")
                                .objectType(ObjectType.PROJECT)
                                .propertyTypes(List.of(PropertyType.TEXT))
                                .mandatory(false)
                                .build())

                        .add(TemplateProperty.builder()
                                .name("aantal_tijdelijke_woningen")
                                .objectType(ObjectType.PROJECT)
                                .propertyTypes(List.of(PropertyType.NUMERIC))
                                .mandatory(false)
                                .build())
                        .add(TemplateProperty.builder()
                                .name("aantal_nultreden_woningen")
                                .objectType(ObjectType.PROJECT)
                                .propertyTypes(List.of(PropertyType.NUMERIC))
                                .mandatory(false)
                                .build())
                        .add(TemplateProperty.builder()
                                .name("aantal_geclusterde_woningen")
                                .objectType(ObjectType.PROJECT)
                                .propertyTypes(List.of(PropertyType.NUMERIC))
                                .mandatory(false)
                                .build())
                        .add(TemplateProperty.builder()
                                .name("aantal_zorggeschikte_woningen")
                                .objectType(ObjectType.PROJECT)
                                .propertyTypes(List.of(PropertyType.NUMERIC))
                                .mandatory(false)
                                .build())

                        .add(TemplateProperty.builder()
                                .name("energieconcept")
                                .objectType(ObjectType.PROJECT)
                                .propertyTypes(List.of(PropertyType.CATEGORY))
                                .mandatory(false)
                                .options(List.of(
                                        "1. Individueel All-Electric",
                                        "2. Hybride verwarming",
                                        "3. Collectief Warmtenet zonder bijverwarming in woning",
                                        "4. Collectief Warmtenet met bijverwarming in woning",
                                        "5. Conventioneel aardgas verwarming",
                                        "6. Individueel NOM",
                                        "7. Duurzaam gasverwarming",
                                        "8. Onbekend"))
                                .build())
                        .add(TemplateProperty.builder()
                                .name("tapwatervoorziening")
                                .objectType(ObjectType.PROJECT)
                                .propertyTypes(List.of(PropertyType.CATEGORY))
                                .mandatory(false)
                                .options(List.of(
                                        "1. LT warmtepomp lucht-water",
                                        "1. LT warmtepomp grond-water",
                                        "2. Hybride warmtepomp met gas bijverwarming",
                                        "3. LT (<35 graden) met wijk bijverwarming (GV aansluiting)",
                                        "3. MT (35-60 graden) met wijk bijverwarming (GV aansluiting)",
                                        "3. HT (60-90 graden) warmtenet zonder bijverwarming",
                                        "4. LT (<35 graden) met warmtepomp",
                                        "4. MT (35-60 graden) met tapwater verwarming",
                                        "5. Aardgas",
                                        "6. LT warmtepomp lucht-water",
                                        "6. LT warmtepomp grond-water",
                                        "7. Groengas",
                                        "Onbekend"))
                                .build())
                        .add(TemplateProperty.builder()
                                .name("realisatiekans")
                                .objectType(ObjectType.PROJECT)
                                .propertyTypes(List.of(PropertyType.CATEGORY))
                                .mandatory(false)
                                .options(List.of(
                                        "10%",
                                        "20%",
                                        "30%",
                                        "40%",
                                        "50%",
                                        "60%",
                                        "70%",
                                        "80%",
                                        "90%",
                                        "100%"))
                                .build())
                        .add(TemplateProperty.builder()
                                .name("aandachtsgroepen")
                                .objectType(ObjectType.PROJECT)
                                .propertyTypes(List.of(PropertyType.CATEGORY))
                                .mandatory(false)
                                .options(List.of(
                                        "Studenten",
                                        "Ouderen",
                                        "Arbeidsmigranten",
                                        "Woonwagenbewoners",
                                        "Overig"))
                                .build())
                        .add(TemplateProperty.builder()
                                .name("sleutelproject")
                                .objectType(ObjectType.PROJECT)
                                .propertyTypes(List.of(PropertyType.CATEGORY))
                                .mandatory(false)
                                .options(List.of("Ja", "Nee", "Onbekend"))
                                .build())
                        .add(TemplateProperty.builder()
                                .name("onzelfstandige_wooneenheden")
                                .objectType(ObjectType.PROJECT)
                                .propertyTypes(List.of(PropertyType.NUMERIC))
                                .mandatory(false)
                                .build())
                        .build())
                .build();
    }

    /*
     * For e.g. loading a lot of options
     */
    private static List<String> loadStringListFromResource(String name) {
        try {
            return Arrays.asList(ResourceUtil.getResourceAsString(name).split("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
