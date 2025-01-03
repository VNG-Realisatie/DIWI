import { useLocation } from "react-router-dom";
import { GridFilterModel, GridLogicOperator, GridPaginationModel, GridSortModel } from "@mui/x-data-grid";
import { useCallback, useEffect, useState } from "react";
import queryString from "query-string";
import { filterTable } from "../api/projectsTableServices";
import { getProjects, getProjectsSizeWithParameters } from "../api/projectsServices";
import { Project } from "../api/projectsServices";

const useCustomSearchParams = (sort: GridSortModel | undefined, filter: GridFilterModel | undefined, paginationInfo: GridPaginationModel) => {
    const [filterModel, setFilterModel] = useState<GridFilterModel | undefined>();
    const [sortModel, setSortModel] = useState<GridSortModel | undefined>();
    const [projects, setProjects] = useState<Array<Project>>([]);
    const location = useLocation();
    const [filterUrl, setFilterUrl] = useState("");
    const [filteredProjectsSize, setFilteredProjectsSize] = useState<number>(0);
    const [isFiltered, setIsFiltered] = useState(false);
    const [isSorted, setIsSorted] = useState(false);

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

    useEffect(() => {
        const querySortParams = ["pageNumber", "pageSize", "sortColumn", "sortDirection"];
        setIsSorted(querySortParams.every((e) => location.search.includes(e)));

        const queryFilterParams = ["pageNumber", "pageSize", "filterColumn", "filterValue", "filterCondition"];
        setIsFiltered(queryFilterParams.every((e) => location.search.includes(e)));
    }, [location.search]);

    useEffect(() => {
        getProjectsSizeWithParameters(filterUrl)
            .then((size) => {
                setFilteredProjectsSize(size.size);
            })
            .catch((err) => console.log(err));
    }, [filterUrl]);

    useEffect(() => {
        if (isSorted || isFiltered) {
            filterTable(decodeURIComponent(location.search)).then((res) => {
                setProjects(res);
            });
        } else {
            getProjects(paginationInfo.page, paginationInfo.pageSize)
                .then((projects) => {
                    setProjects(projects);
                })
                .catch((err) => console.log(err));
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isFiltered, isSorted, paginationInfo.page, paginationInfo.pageSize]);

    useEffect(() => {
        if (isFiltered) {
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
    }, [isFiltered, location.search]);

    const updateUrl = useCallback(() => {
        let query = "";

        if (sortModel && sortModel.length > 0) {
            const sortQuery = sortModel.map((sortItem) => `sortColumn=${sortItem.field}&sortDirection=${sortItem.sort?.toUpperCase()}`);
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

    return { filterUrl, rows, filteredProjectsSize };
};

export default useCustomSearchParams;
