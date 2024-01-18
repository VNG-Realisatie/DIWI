package com.vng.dal;

import jakarta.ws.rs.QueryParam;

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

    public FilterPaginationSorting() {
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isValid() {
        return (this.pageNumber > 0 && this.pageSize > 0);
    }

    public int getFirstResultIndex() {
        return (pageNumber - 1) * pageSize;
    }

    public String getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    public String getFilterColumn() {
        return filterColumn;
    }

    public void setFilterColumn(String filterColumn) {
        this.filterColumn = filterColumn;
    }

    public String getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }
}
