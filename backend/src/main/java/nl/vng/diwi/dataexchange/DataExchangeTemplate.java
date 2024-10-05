package nl.vng.diwi.dataexchange;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.entities.DataExchangeType;
import nl.vng.diwi.dal.entities.enums.ObjectType;
import nl.vng.diwi.dal.entities.enums.PropertyType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DataExchangeTemplate {

    private List<TemplateProperty> properties = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode
    @AllArgsConstructor
    public static class TemplateProperty {
        private String name;
        private ObjectType objectType;
        private List<PropertyType> propertyTypes;
        private Boolean mandatory;
        private Boolean singleSelect;
        private List<String> options;
    }

    public static Map<DataExchangeType, DataExchangeTemplate> templates = new HashMap<>();

    static {
        DataExchangeTemplate zuidHollandTemplate = new DataExchangeTemplate();

        zuidHollandTemplate.properties.add(new TemplateProperty("opdrachtgever_type", ObjectType.PROJECT, List.of(PropertyType.CATEGORY), true, true,
            List.of("Gemeente", "Woningcorporatie", "Projectontwikkelaar", "Particulieren", "Meerdere namelijk", "Anders", "Onbekend")));
        zuidHollandTemplate.properties.add(new TemplateProperty("opdrachtgever_naam", ObjectType.PROJECT, List.of(PropertyType.TEXT), false, null, null));
        zuidHollandTemplate.properties.add(new TemplateProperty("opmerkingen_basis", ObjectType.PROJECT, List.of(PropertyType.TEXT), false, null, null));
        zuidHollandTemplate.properties.add(new TemplateProperty("masterplan", ObjectType.PROJECT, List.of(PropertyType.TEXT), false,  null, null));
        zuidHollandTemplate.properties.add(new TemplateProperty("bestemmingsplan", ObjectType.PROJECT, List.of(PropertyType.TEXT), false,  null, null));
        zuidHollandTemplate.properties.add(new TemplateProperty("opmerkingen_status", ObjectType.PROJECT, List.of(PropertyType.TEXT), false,  null, null));
        zuidHollandTemplate.properties.add(new TemplateProperty("beoogd_woonmilieu_ABF5", ObjectType.PROJECT, List.of(PropertyType.CATEGORY), false, true,
            List.of("Centrum-stedelijk", "Buitencentrum", "Groen-stedelijk", "Dorps", "Landelijk", "Onbekend")));
        zuidHollandTemplate.properties.add(new TemplateProperty("beoogd_woonmilieu_ABF13", ObjectType.PROJECT, List.of(PropertyType.CATEGORY), false, true,
            List.of("Centrum-stedelijk-plus", "Centrum-stedel", "Centrum-stedelijk", "Centrum-kleinstedelijk", "Stedelijk vooroorlogs",
                "Stedelijk naoorlogs compact", "Stedelijk naoorlogs grondgebonden", "Kleinstedelijk", "Groen-stedelijk", "Groen-kleinstedelijk",
                "Centrum-dorps", "Dorps", "Landelijk bereikbaar", "Landelijk perifeer", "Onbekend")));
        zuidHollandTemplate.properties.add(new TemplateProperty("knelpunten_meerkeuze", ObjectType.PROJECT, List.of(PropertyType.CATEGORY), true, false,
            List.of("Aansluiting op nutsvoorzieningen", "Bereikbaarheid", "Capaciteit bouwsector", "Capaciteit overheid", "Compensatie bedrijventerreinen",
                "Geluidhinder – industrie en bedrijven", "Geluidhinder – vliegverkeer", "Geluidhinder – weg- en railverkeer", "Geluidhinder – nestgeluid",
                "Maatschappelijk draagvlak", "Natuurbescherming", "Netcongestie", "Onrendabele top", "Parkeren", "Procedures - Bezwaarprocedures",
                "Procedures - Beroepsprocedures", "Stikstof", "Vervuilde grond", "Anders", "Geen", "Onbekend")));
        zuidHollandTemplate.properties.add(new TemplateProperty("toelichting_knelpunten", ObjectType.PROJECT, List.of(PropertyType.TEXT), false, null, null));
        zuidHollandTemplate.properties.add(new TemplateProperty("verhuurder_type", ObjectType.PROJECT, List.of(PropertyType.CATEGORY), false, true,
            List.of("Woningcorporatie", "Overig", "Combinatie", "Onbekend")));
        zuidHollandTemplate.properties.add(new TemplateProperty("opmerkingen_kwalitatief", ObjectType.PROJECT, List.of(PropertyType.TEXT), false, null, null));
        zuidHollandTemplate.properties.add(new TemplateProperty("ph_text3", ObjectType.PROJECT, List.of(PropertyType.BOOLEAN), true, null, null));
        zuidHollandTemplate.properties.add(new TemplateProperty("ph_short2", ObjectType.PROJECT, List.of(PropertyType.NUMERIC), true, null, null));
        zuidHollandTemplate.properties.add(new TemplateProperty("ph_text4", ObjectType.PROJECT, List.of(PropertyType.BOOLEAN), true, null, null));
        zuidHollandTemplate.properties.add(new TemplateProperty("ph_text5", ObjectType.PROJECT, List.of(PropertyType.CATEGORY), false, true,
            List.of("20%", "40%", "60%", "80%", "100%", "Onbekend")));
        zuidHollandTemplate.properties.add(new TemplateProperty("ph_text6", ObjectType.PROJECT, List.of(PropertyType.CATEGORY), true, true,
            List.of("Onbekend", "Wettelijke norm", "Boven wettelijk", "Volledig energieneutraal")));
        zuidHollandTemplate.properties.add(new TemplateProperty("ph_text7", ObjectType.PROJECT, List.of(PropertyType.CATEGORY), true, true,
            List.of("Onbekend", "Nee", "Ja, minimale aandacht", "Ja, ruime aandacht")));
        zuidHollandTemplate.properties.add(new TemplateProperty("ph_text8", ObjectType.PROJECT, List.of(PropertyType.CATEGORY), true, true,
            List.of("Warmtenet", "Elektra", "Overig", "Onbekend")));
        zuidHollandTemplate.properties.add(new TemplateProperty("ph_text10", ObjectType.PROJECT, List.of(PropertyType.TEXT), false, null, null));
        zuidHollandTemplate.properties.add(new TemplateProperty("ph_short7", ObjectType.PROJECT, List.of(PropertyType.NUMERIC), true, null, null));
        zuidHollandTemplate.properties.add(new TemplateProperty("ph_short8", ObjectType.PROJECT, List.of(PropertyType.NUMERIC), true, null, null));
        zuidHollandTemplate.properties.add(new TemplateProperty("ph_short9", ObjectType.PROJECT, List.of(PropertyType.NUMERIC), true, null, null));

        templates.put(DataExchangeType.ESRI_ZUID_HOLLAND, zuidHollandTemplate);
    }
}
