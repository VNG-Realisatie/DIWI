package nl.vng.diwi.dal;

import jakarta.ws.rs.QueryParam;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class FilterPaginationSorting {

    public enum FilterCondition {
        CONTAINS,
        ANY_OF;
    }

    public enum SortDirection {
        ASC,
        DESC;
    }

    @QueryParam("pageNumber")
    private int pageNumber;

    @QueryParam("pageSize")
    private int pageSize;

    @QueryParam("sortColumn")
    private String sortColumn;

    @QueryParam("sortDirection")
    private SortDirection sortDirection;

    @QueryParam("filterColumn")
    private String filterColumn;

    @QueryParam("filterValue")
    private List<String> filterValue;

    @QueryParam("filterCondition")
    private FilterCondition filterCondition;

    public boolean isValid() {
        return (this.pageNumber > 0 && this.pageSize > 0);
    }

    public int getFirstResultIndex() {
        return (pageNumber - 1) * pageSize;
    }

    public SortDirection getSortDirection() {
        return (sortDirection == null) ? SortDirection.ASC : sortDirection;
    }
}
