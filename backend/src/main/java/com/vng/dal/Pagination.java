package com.vng.dal;

import javax.ws.rs.QueryParam;

public class Pagination {

    @QueryParam("pageNumber")
    private int pageNumber;

    @QueryParam("pageSize")
    private int pageSize;

    public Pagination() {
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
}
