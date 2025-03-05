package nl.vng.diwi.dataexchange;

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
import nl.vng.diwi.services.export.zuidholland.EsriZuidHollandExport.EsriZuidHollandProjectProps;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DataExchangeTemplate {

    private List<TemplateProperty> properties = new ArrayList<>();

    /**
     * // Default to an external level, can be overriden for exports meant for internal use.
     */
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
        private Boolean mandatory;
        private Boolean singleSelect;
        private List<String> options;
    }

    public static final ImmutableMap<DataExchangeType, DataExchangeTemplate> templates;

    static {
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
        zuidHollandTemplate.properties.add(new TemplateProperty(EsriZuidHollandProjectProps.masterplan.name(), ObjectType.PROJECT, List.of(PropertyType.TEXT), false,  null, null));
        zuidHollandTemplate.properties.add(new TemplateProperty(EsriZuidHollandProjectProps.bestemmingsplan.name(), ObjectType.PROJECT, List.of(PropertyType.TEXT), false,  null, null));
        zuidHollandTemplate.properties.add(new TemplateProperty(EsriZuidHollandProjectProps.opmerkingen_status.name(), ObjectType.PROJECT, List.of(PropertyType.TEXT), false,  null, null));
        zuidHollandTemplate.properties.add(new TemplateProperty(EsriZuidHollandProjectProps.beoogd_woonmilieu_ABF5.name(), ObjectType.PROJECT, List.of(PropertyType.CATEGORY), false, true,
            List.of("Centrum-stedelijk", "Buitencentrum", "Groen-stedelijk", "Dorps", "Landelijk", "Onbekend")));
        zuidHollandTemplate.properties.add(new TemplateProperty(EsriZuidHollandProjectProps.beoogd_woonmilieu_ABF13.name(), ObjectType.PROJECT, List.of(PropertyType.CATEGORY), false, true,
            List.of("Centrum-stedelijk-plus", "Centrum-stedelijk", "Centrum-kleinstedelijk", "Stedelijk vooroorlogs",
                "Stedelijk naoorlogs compact", "Stedelijk naoorlogs grondgebonden", "Kleinstedelijk", "Groen-stedelijk", "Groen-kleinstedelijk",
                "Centrum-dorps", "Dorps", "Landelijk bereikbaar", "Landelijk perifeer", "Onbekend")));
        zuidHollandTemplate.properties.add(new TemplateProperty(EsriZuidHollandProjectProps.knelpunten_meerkeuze.name(), ObjectType.PROJECT, List.of(PropertyType.CATEGORY), false, false,
            List.of("Aansluiting op nutsvoorzieningen", "Bereikbaarheid", "Capaciteit bouwsector", "Capaciteit overheid", "Compensatie bedrijventerreinen",
                "Geluidhinder – industrie en bedrijven", "Geluidhinder – vliegverkeer", "Geluidhinder – weg- en railverkeer", "Geluidhinder – nestgeluid",
                "Maatschappelijk draagvlak", "Natuurbescherming", "Netcongestie", "Onrendabele top", "Parkeren", "Procedures - Bezwaarprocedures",
                "Procedures - Beroepsprocedures", "Stikstof", "Vervuilde grond", "Anders", "Geen", "Onbekend")));
        zuidHollandTemplate.properties.add(new TemplateProperty(EsriZuidHollandProjectProps.toelichting_knelpunten.name(), ObjectType.PROJECT, List.of(PropertyType.TEXT), false, null, null));
        zuidHollandTemplate.properties.add(new TemplateProperty("verhuurder_type", ObjectType.PROJECT, List.of(PropertyType.CATEGORY), false, true,
            List.of("Woningcorporatie", "Overig", "Combinatie", "Onbekend")));
        zuidHollandTemplate.properties.add(new TemplateProperty(EsriZuidHollandProjectProps.opmerkingen_kwalitatief.name(), ObjectType.PROJECT, List.of(PropertyType.TEXT), false, null, null));
        zuidHollandTemplate.properties.add(new TemplateProperty(EsriZuidHollandProjectProps.ph_text1.name(), ObjectType.PROJECT, List.of(PropertyType.CATEGORY), true, true,
            List.of("1a harde plannen, in voorbereiding of in uitvoering", "1b kansrijke plannen", "1c bestuurlijke afspraken", "2 reserveplannen", "Onbekend")));
        zuidHollandTemplate.properties.add(new TemplateProperty(
                EsriZuidHollandProjectProps.ph_text3.name(),
                ObjectType.PROJECT,
                List.of(PropertyType.CATEGORY),
                false,
                true,
                List.of("Ja", "Nee", "Onbekend")));
        zuidHollandTemplate.properties.add(new TemplateProperty(EsriZuidHollandProjectProps.ph_short2.name(), ObjectType.PROJECT, List.of(PropertyType.NUMERIC), true, null, null));
        zuidHollandTemplate.properties.add(new TemplateProperty(
                EsriZuidHollandProjectProps.ph_text4.name(),
                ObjectType.PROJECT,
                List.of(PropertyType.CATEGORY),
                false,
                true,
                List.of("Ja", "Nee", "Onbekend")));
        zuidHollandTemplate.properties.add(new TemplateProperty(EsriZuidHollandProjectProps.ph_text5.name(), ObjectType.PROJECT, List.of(PropertyType.CATEGORY), false, true,
            List.of("20%", "40%", "60%", "80%", "100%", "Onbekend")));
        zuidHollandTemplate.properties.add(new TemplateProperty(EsriZuidHollandProjectProps.ph_text6.name(), ObjectType.PROJECT, List.of(PropertyType.CATEGORY), false, true,
            List.of("Onbekend", "Wettelijke norm", "Boven wettelijk", "Volledig energieneutraal")));
        zuidHollandTemplate.properties.add(new TemplateProperty(EsriZuidHollandProjectProps.ph_text7.name(), ObjectType.PROJECT, List.of(PropertyType.CATEGORY), false, true,
            List.of("Onbekend", "Nee", "Ja, minimale aandacht", "Ja, ruime aandacht")));
        zuidHollandTemplate.properties.add(new TemplateProperty(EsriZuidHollandProjectProps.ph_text8.name(), ObjectType.PROJECT, List.of(PropertyType.CATEGORY), false, true,
            List.of("Warmtenet", "Elektra", "Overig", "Onbekend")));
        zuidHollandTemplate.properties.add(new TemplateProperty(EsriZuidHollandProjectProps.ph_short7.name(), ObjectType.PROJECT, List.of(PropertyType.NUMERIC), false, null, null));
        zuidHollandTemplate.properties.add(new TemplateProperty(EsriZuidHollandProjectProps.ph_short8.name(), ObjectType.PROJECT, List.of(PropertyType.NUMERIC), false, null, null));
        zuidHollandTemplate.properties.add(new TemplateProperty(EsriZuidHollandProjectProps.ph_short9.name(), ObjectType.PROJECT, List.of(PropertyType.NUMERIC), false, null, null));

        var gelderlandTemplate = new DataExchangeTemplate();

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
}
