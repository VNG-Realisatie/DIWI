package nl.vng.diwi.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RangeCategoryPieChartModel extends PieChartModel {

    private UUID id;

    private BigDecimal min;

    private BigDecimal max;

}
