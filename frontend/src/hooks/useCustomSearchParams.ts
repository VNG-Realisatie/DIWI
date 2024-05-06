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

    const isFilteredUrl = useCallback(() => {
        const queryParams = ["pageNumber", "pageSize", "sortColumn", "sortDirection", "filterColumn", "filterCondition", "filterValue"];
        return queryParams.every((e) => location.search.includes(e));
    }, [location.search]);

    useEffect(() => {
        if (isFilteredUrl()) {
            filterTable(location.search).then((res) => setProjects(res));
            console.log("filtered");
        } else {
            getProjects(paginationInfo.page, paginationInfo.pageSize)
                .then((projects) => {
                    setProjects(projects);
                })
                .catch((err) => console.log(err));
        }
    }, [isFilteredUrl, location.search, paginationInfo.page, paginationInfo.pageSize]);

    useEffect(() => {
        console.log(isFilteredUrl());
        if (isFilteredUrl()) {
            const filterValues = queryString.parse(location.search);

            const filterItems = Object.values(filterValues);
            console.log(filterItems);
            const values = filterItems.slice(2, -2);

            if (filterItems.length >= 3 && filterItems.length < 6) {
                const filter = {
                    items: [
                        {
                            field: filterItems[0] as string,
                            operator: filterItems[1] === "ANY_OF" ? "isAnyOf" : "contains",
                            value: filterItems[2] as string,
                        },
                    ],
                    logicOperator: GridLogicOperator.And,
                };

                setFilterModel(filter);
            } else {
                const filter = {
                    items: [
                        {
                            field: filterItems[0] as string,
                            operator: filterItems[1] === "ANY_OF" ? "isAnyOf" : "contains",
                            value: values as string[],
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

        if (filterModel && filterModel.items) {
            const filterQuery = queryString.stringify({
                filterColumn: filterModel.items[0].field,
                filterCondition: filterModel.items[0].operator === "isAnyOf" ? "ANY_OF" : "CONTAINS",
                filterValue: filterModel.items[0].value,
            });
            query += filterQuery;
        }

        if (sortModel && sortModel.length > 0) {
            const sortQuery = sortModel.map((sortItem) => `${sortItem.field}:${sortItem.sort}`).join(",");
            query += query ? `&sort=${sortQuery}` : `sort=${sortQuery}`;
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
