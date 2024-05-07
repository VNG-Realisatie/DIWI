import { useLocation } from "react-router-dom";
import { GridFilterModel, GridLogicOperator, GridPaginationModel, GridSortModel } from "@mui/x-data-grid";
import { useCallback, useEffect, useState } from "react";
import queryString from "query-string";
import { filterTable } from "../api/projectsTableServices";
import { getProjects } from "../api/projectsServices";
import { Project } from "../api/projectsServices";

const useCustomSearchParams = (sort: GridSortModel | undefined, filter: GridFilterModel | undefined, paginationInfo: GridPaginationModel) => {
    const [filterModel, setFilterModel] = useState<GridFilterModel | undefined>();
    const [sortModel, setSortModel] = useState<GridSortModel | undefined>();
    const [projects, setProjects] = useState<Array<Project>>([]);
    const location = useLocation();
    const [filterUrl, setFilterUrl] = useState("");

    useEffect(() => {
        if (filter) {
            setFilterModel(filter);
        }
    }, [filter]);

    useEffect(() => {
        if (sort) {
            setSortModel(sort);
        }
    }, [sort]);

    const isSortedUrl = useCallback(() => {
        const queryParams = ["pageNumber", "pageSize", "sortColumn", "sortDirection"];
        return queryParams.every((e) => location.search.includes(e));
    }, [location.search]);

    const isFilteredUrl = useCallback(() => {
        const queryParams = ["pageNumber", "pageSize", "filterColumn", "filterValue", "filterCondition"];
        return queryParams.every((e) => location.search.includes(e));
    }, [location.search]);

    useEffect(() => {
        if (isFilteredUrl() || isSortedUrl()) {
            filterTable(location.search).then((res) => {
                setProjects(res);
            });
        } else {
            getProjects(paginationInfo.page, paginationInfo.pageSize)
                .then((projects) => {
                    setProjects(projects);
                })
                .catch((err) => console.log(err));
        }
    }, [isFilteredUrl, isSortedUrl, location.search, paginationInfo.page, paginationInfo.pageSize]);

    useEffect(() => {
        if (isFilteredUrl()) {
            const filterValues = queryString.parse(location.search);

            const filterColumn = filterValues["filterColumn"];
            const filterValue = filterValues["filterValue"];
            const filterCondition = filterValues["filterCondition"];

            if (filterColumn && filterCondition && filterValue) {
                const operator = filterCondition === "ANY_OF" ? "isAnyOf" : "contains";
                const filter = {
                    items: [
                        {
                            field: filterColumn as string,
                            value: filterValue as string | string[],
                            operator,
                        },
                    ],
                    logicOperator: GridLogicOperator.And,
                };
                setFilterModel(filter);
            }
        }
    }, [isFilteredUrl, location.search]);

    const updateUrl = useCallback(() => {
        let query = "";

        if (sortModel && sortModel.length > 0) {
            let sortQuery;
            sortQuery = sortModel.map((sortItem) => `sortColumn=${sortItem.field}&sortDirection=${sortItem.sort?.toUpperCase()}`);
            query += sortQuery;
        }

        if (filterModel && filterModel.items && filterModel.items.length > 0) {
            const filterQuery = queryString.stringify({
                filterColumn: filterModel.items[0].field,
                filterValue: filterModel.items[0].value,
                filterCondition: filterModel.items[0].operator === "isAnyOf" ? "ANY_OF" : "CONTAINS",
            });
            query += query ? `&${filterQuery}` : `${filterQuery}`;
        }

        const url = `?pageNumber=${paginationInfo.page}&pageSize=${paginationInfo.pageSize}&${query}`;
        setFilterUrl(url);
    }, [filterModel, sortModel, paginationInfo.page, paginationInfo.pageSize]);

    useEffect(() => {
        updateUrl();
    }, [updateUrl, filterModel, sortModel]);

    const rows = projects.map((p) => {
        return { ...p, id: p.projectId };
    });

    return { filterUrl, rows };
};

export default useCustomSearchParams;
