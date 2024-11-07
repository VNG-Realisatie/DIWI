package nl.vng.diwi.services.export.zuidholland;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static nl.vng.diwi.services.export.zuidholland.EsriZuidHollandExport.EsriZuidHollandHouseblockProps;
@Data
public class EsriZuidHollandHouseblockModel {

    private int totalHouses = 0;

    private PercentageModel houseTypeEeng = new PercentageModel();
    private PercentageModel houseTypeMeer = new PercentageModel();
    private PercentageModel houseTypeUnknown = new PercentageModel();

    private PercentageModel ownershipKoop1 = new PercentageModel();
    private PercentageModel ownershipKoop2 = new PercentageModel();
    private PercentageModel ownershipKoop3 = new PercentageModel();
    private PercentageModel ownershipKoop4 = new PercentageModel();
    private PercentageModel ownershipKoopUnknown = new PercentageModel();
    private PercentageModel ownershipHuur1 = new PercentageModel();
    private PercentageModel ownershipHuur2 = new PercentageModel();
    private PercentageModel ownershipHuur3 = new PercentageModel();
    private PercentageModel ownershipHuur4 = new PercentageModel();
    private PercentageModel ownershipHuurUnknown = new PercentageModel();
    private PercentageModel ownershipUnknown = new PercentageModel();

    @Data
    @NoArgsConstructor
    public static class PercentageModel {
        private int value = 0;
        private double percentage;
        private double roundingDiff = 0;

        public PercentageModel(double percentage) {
            this.percentage = percentage;
        }

        public void addValue(Integer valueToAdd) {
            this.value += valueToAdd;
        }

        public void calculatePercentage(int total) {
            this.percentage = (double) this.value / total;
        }

        public void calculateValueAndRoundingDiff(int total) {
            this.value = (int) (this.percentage * total);
            this.roundingDiff = this.percentage * total - this.value;
        }
    }

    public void addHouseblockData(EsriZuidHollandHouseblockExportModel houseblock) {

            this.totalHouses += houseblock.getMutationAmount();

            if (houseblock.getEengezinswoning() != null) {
                this.houseTypeEeng.addValue(houseblock.getEengezinswoning());
            }
            if (houseblock.getMeergezinswoning() != null) {
                this.houseTypeMeer.addValue(houseblock.getMeergezinswoning());
            }

            for (var ownership : houseblock.getOwnershipValueList()) {
                switch (ownership.getOwnershipCategory()) {
                    case koop1 -> this.ownershipKoop1.addValue(ownership.getAmount());
                    case koop2 -> this.ownershipKoop2.addValue(ownership.getAmount());
                    case koop3 -> this.ownershipKoop3.addValue(ownership.getAmount());
                    case koop4 -> this.ownershipKoop4.addValue(ownership.getAmount());
                    case koop_onb -> this.ownershipKoopUnknown.addValue(ownership.getAmount());
                    case huur1 -> this.ownershipHuur1.addValue(ownership.getAmount());
                    case huur2 -> this.ownershipHuur2.addValue(ownership.getAmount());
                    case huur3 -> this.ownershipHuur3.addValue(ownership.getAmount());
                    case huur4 -> this.ownershipHuur4.addValue(ownership.getAmount());
                    case huur_onb -> this.ownershipHuurUnknown.addValue(ownership.getAmount());
                }
            }
    }

    public Map<EsriZuidHollandHouseblockProps, Integer> calculateHouseTypeOwnershipValuesMap() {
        this.houseTypeUnknown.setValue(this.totalHouses - (this.houseTypeEeng.value + this.houseTypeMeer.value));
        this.ownershipUnknown.setValue(this.totalHouses - (this.ownershipKoop1.value + this.ownershipKoop2.value + this.ownershipKoop3.value + this.ownershipKoop4.value
            + this.ownershipKoopUnknown.value + this.ownershipHuur1.value + this.ownershipHuur2.value + this.ownershipHuur3.value + this.ownershipHuur4.value + this.ownershipHuurUnknown.value));

        this.houseTypeEeng.calculatePercentage(this.totalHouses);
        this.houseTypeMeer.calculatePercentage(this.totalHouses);
        this.houseTypeUnknown.calculatePercentage(this.totalHouses);

        this.ownershipHuur1.calculatePercentage(this.totalHouses);
        this.ownershipHuur2.calculatePercentage(this.totalHouses);
        this.ownershipHuur3.calculatePercentage(this.totalHouses);
        this.ownershipHuur4.calculatePercentage(this.totalHouses);
        this.ownershipHuurUnknown.calculatePercentage(this.totalHouses);

        this.ownershipKoop1.calculatePercentage(this.totalHouses);
        this.ownershipKoop2.calculatePercentage(this.totalHouses);
        this.ownershipKoop3.calculatePercentage(this.totalHouses);
        this.ownershipKoop4.calculatePercentage(this.totalHouses);
        this.ownershipKoopUnknown.calculatePercentage(this.totalHouses);

        this.ownershipUnknown.calculatePercentage(this.totalHouses);

        Map<EsriZuidHollandExport.EsriZuidHollandHouseblockProps, PercentageModel> houseTypeOwnershipMap = new HashMap<>();

        houseTypeOwnershipMap.put(EsriZuidHollandHouseblockProps.meergezins_koop1, new PercentageModel(this.houseTypeMeer.percentage * this.ownershipKoop1.percentage));
        houseTypeOwnershipMap.put(EsriZuidHollandHouseblockProps.meergezins_koop2, new PercentageModel(this.houseTypeMeer.percentage * this.ownershipKoop2.percentage));
        houseTypeOwnershipMap.put(EsriZuidHollandHouseblockProps.meergezins_koop3, new PercentageModel(this.houseTypeMeer.percentage * this.ownershipKoop3.percentage));
        houseTypeOwnershipMap.put(EsriZuidHollandHouseblockProps.meergezins_koop4, new PercentageModel(this.houseTypeMeer.percentage * this.ownershipKoop4.percentage));
        houseTypeOwnershipMap.put(EsriZuidHollandHouseblockProps.meergezins_koop_onb, new PercentageModel(this.houseTypeMeer.percentage * this.ownershipKoopUnknown.percentage));
        houseTypeOwnershipMap.put(EsriZuidHollandHouseblockProps.meergezins_huur1, new PercentageModel(this.houseTypeMeer.percentage * this.ownershipHuur1.percentage));
        houseTypeOwnershipMap.put(EsriZuidHollandHouseblockProps.meergezins_huur2, new PercentageModel(this.houseTypeMeer.percentage * this.ownershipHuur2.percentage));
        houseTypeOwnershipMap.put(EsriZuidHollandHouseblockProps.meergezins_huur3, new PercentageModel(this.houseTypeMeer.percentage * this.ownershipHuur3.percentage));
        houseTypeOwnershipMap.put(EsriZuidHollandHouseblockProps.meergezins_huur4, new PercentageModel(this.houseTypeMeer.percentage * this.ownershipHuur4.percentage));
        houseTypeOwnershipMap.put(EsriZuidHollandHouseblockProps.meergezins_huur_onb, new PercentageModel(this.houseTypeMeer.percentage * this.ownershipHuurUnknown.percentage));
        houseTypeOwnershipMap.put(EsriZuidHollandHouseblockProps.meergezins_onbekend, new PercentageModel(this.houseTypeMeer.percentage * this.ownershipUnknown.percentage));

        houseTypeOwnershipMap.put(EsriZuidHollandHouseblockProps.eengezins_koop1, new PercentageModel(this.houseTypeEeng.percentage * this.ownershipKoop1.percentage));
        houseTypeOwnershipMap.put(EsriZuidHollandHouseblockProps.eengezins_koop2, new PercentageModel(this.houseTypeEeng.percentage * this.ownershipKoop2.percentage));
        houseTypeOwnershipMap.put(EsriZuidHollandHouseblockProps.eengezins_koop3, new PercentageModel(this.houseTypeEeng.percentage * this.ownershipKoop3.percentage));
        houseTypeOwnershipMap.put(EsriZuidHollandHouseblockProps.eengezins_koop4, new PercentageModel(this.houseTypeEeng.percentage * this.ownershipKoop4.percentage));
        houseTypeOwnershipMap.put(EsriZuidHollandHouseblockProps.eengezins_koop_onb, new PercentageModel(this.houseTypeEeng.percentage * this.ownershipKoopUnknown.percentage));
        houseTypeOwnershipMap.put(EsriZuidHollandHouseblockProps.eengezins_huur1, new PercentageModel(this.houseTypeEeng.percentage * this.ownershipHuur1.percentage));
        houseTypeOwnershipMap.put(EsriZuidHollandHouseblockProps.eengezins_huur2, new PercentageModel(this.houseTypeEeng.percentage * this.ownershipHuur2.percentage));
        houseTypeOwnershipMap.put(EsriZuidHollandHouseblockProps.eengezins_huur3, new PercentageModel(this.houseTypeEeng.percentage * this.ownershipHuur3.percentage));
        houseTypeOwnershipMap.put(EsriZuidHollandHouseblockProps.eengezins_huur4, new PercentageModel(this.houseTypeEeng.percentage * this.ownershipHuur4.percentage));
        houseTypeOwnershipMap.put(EsriZuidHollandHouseblockProps.eengezins_huur_onb, new PercentageModel(this.houseTypeEeng.percentage * this.ownershipHuurUnknown.percentage));
        houseTypeOwnershipMap.put(EsriZuidHollandHouseblockProps.eengezins_onbekend, new PercentageModel(this.houseTypeEeng.percentage * this.ownershipUnknown.percentage));

        houseTypeOwnershipMap.put(EsriZuidHollandHouseblockProps.onbekend_koop1, new PercentageModel(this.houseTypeUnknown.percentage * this.ownershipKoop1.percentage));
        houseTypeOwnershipMap.put(EsriZuidHollandHouseblockProps.onbekend_koop2, new PercentageModel(this.houseTypeUnknown.percentage * this.ownershipKoop2.percentage));
        houseTypeOwnershipMap.put(EsriZuidHollandHouseblockProps.onbekend_koop3, new PercentageModel(this.houseTypeUnknown.percentage * this.ownershipKoop3.percentage));
        houseTypeOwnershipMap.put(EsriZuidHollandHouseblockProps.onbekend_koop4, new PercentageModel(this.houseTypeUnknown.percentage * this.ownershipKoop4.percentage));
        houseTypeOwnershipMap.put(EsriZuidHollandHouseblockProps.onbekend_koop_onb, new PercentageModel(this.houseTypeUnknown.percentage * this.ownershipKoopUnknown.percentage));
        houseTypeOwnershipMap.put(EsriZuidHollandHouseblockProps.onbekend_huur1, new PercentageModel(this.houseTypeUnknown.percentage * this.ownershipHuur1.percentage));
        houseTypeOwnershipMap.put(EsriZuidHollandHouseblockProps.onbekend_huur2, new PercentageModel(this.houseTypeUnknown.percentage * this.ownershipHuur2.percentage));
        houseTypeOwnershipMap.put(EsriZuidHollandHouseblockProps.onbekend_huur3, new PercentageModel(this.houseTypeUnknown.percentage * this.ownershipHuur3.percentage));
        houseTypeOwnershipMap.put(EsriZuidHollandHouseblockProps.onbekend_huur4, new PercentageModel(this.houseTypeUnknown.percentage * this.ownershipHuur4.percentage));
        houseTypeOwnershipMap.put(EsriZuidHollandHouseblockProps.onbekend_huur_onb, new PercentageModel(this.houseTypeUnknown.percentage * this.ownershipHuurUnknown.percentage));
        houseTypeOwnershipMap.put(EsriZuidHollandHouseblockProps.onbekend_onbekend, new PercentageModel(this.houseTypeUnknown.percentage * this.ownershipUnknown.percentage));

        houseTypeOwnershipMap.values().forEach(pv -> pv.calculateValueAndRoundingDiff(this.totalHouses));

        int totalMappedHouses = houseTypeOwnershipMap.values().stream().mapToInt(PercentageModel::getValue).sum();
        int housesLostThroughRounding = this.totalHouses - totalMappedHouses;

        Map<EsriZuidHollandExport.EsriZuidHollandHouseblockProps, PercentageModel> sortedMap =
            houseTypeOwnershipMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue((v1, v2) -> Double.compare(v2.roundingDiff, v1.roundingDiff)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        Iterator<Map.Entry<EsriZuidHollandHouseblockProps, PercentageModel>> it = sortedMap.entrySet().iterator();
        while (housesLostThroughRounding > 0) {
            housesLostThroughRounding--;
            it.next().getValue().value++;
        }

        return sortedMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().value));
    }


}
