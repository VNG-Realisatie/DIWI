package com.vng.dal;

import jakarta.ws.rs.QueryParam;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FilterPaginationSorting {

    @QueryParam("pageNumber")
    private int pageNumber;

    @QueryParam("pageSize")
    private int pageSize;

    @QueryParam("sortColumn")
    private String sortColumn;

    @QueryParam("sortDirection")
    private String sortDirection;

    @QueryParam("filterColumn")
    private String filterColumn;

    @QueryParam("filterValue")
    private String filterValue;

    public boolean isValid() {
        return (this.pageNumber > 0 && this.pageSize > 0);
    }

    public int getFirstResultIndex() {
        return (pageNumber - 1) * pageSize;
    }


    public String getSortDirection() {
        return ("DESC".equalsIgnoreCase(sortDirection)) ? "DESC" : "ASC";
    }
}
