import { useLocation } from "react-router-dom";
import { GridFilterModel, GridLogicOperator, GridPaginationModel, GridSortModel } from "@mui/x-data-grid";
import { useCallback, useEffect, useState } from "react";
import queryString from "query-string";
import { filterTable } from "../api/projectsTableServices";
import { getProjects, getProjectsSizeWithParameters } from "../api/projectsServices";
import { Project } from "../api/projectsServices";
import { reactWhatChanged } from "react-what-changed";
const useCustomSearchParams = (
    defaultSort: GridSortModel | undefined,
    defaultFilter: GridFilterModel | undefined,
    defaultPaginationInfo: GridPaginationModel,
) => {
    const [filterModel, setFilterModel] = useState<GridFilterModel | undefined>(defaultFilter);
    const [sortModel, setSortModel] = useState<GridSortModel | undefined>(defaultSort);
    const [projects, setProjects] = useState<Array<Project>>([]);
    const location = useLocation();
    const [filterUrl, setFilterUrl] = useState("");
    const [filteredProjectsSize, setFilteredProjectsSize] = useState<number>(0);

    // const [params, setParams] = useSearchParams();
    const [isSorted, setSorted] = useState<boolean>(false);
    const [isFiltered, setFiltered] = useState<boolean>(false);
    const [filterValues, setFilterValues] = useState<queryString.ParsedQuery<string>>();
    const [filterTableParams, setFilterTableParams] = useState<string>(decodeURIComponent(location.search));
    const [page, setPage] = useState<number>(defaultPaginationInfo.page);
    const [pageSize, setPageSize] = useState<number>(defaultPaginationInfo.pageSize);

    useEffect(() => {
        getProjectsSizeWithParameters(filterUrl)
            .then((size) => {
                setFilteredProjectsSize(size.size);
            })
            .catch((err) => console.log(err));
    }, [filterUrl]);

    useEffect(() => {
        const querySortParams = ["pageNumber", "pageSize", "sortColumn", "sortDirection"];
        const isSorted = querySortParams.every((e) => location.search.includes(e));

        const queryFilterParams = ["pageNumber", "pageSize", "filterColumn", "filterValue", "filterCondition"];
        const isFiltered = queryFilterParams.every((e) => location.search.includes(e));

        const filterValues = queryString.parse(location.search);

        setSorted(isSorted);
        setFiltered(isFiltered);
        setFilterValues(filterValues);
        setFilterTableParams(decodeURIComponent(location.search));
        const pageSize = filterValues["pageSize"];
        const page = filterValues["pageNumber"];
        if (typeof page === "string" && typeof pageSize === "string") {
            setPageSize(parseInt(pageSize));
            setPage(parseInt(page));
        }
    }, [location.search]);

    useEffect(
        () => {
            console.log(filterTableParams);
            if (isFiltered || isSorted) {
                filterTable(filterTableParams).then((res) => {
                    setProjects(res);
                });
            } else {
                getProjects(page, pageSize)
                    .then((projects) => {
                        setProjects(projects);
                    })
                    .catch((err) => console.log(err));
            }
        },
        // eslint-disable-next-line react-hooks/exhaustive-deps
        reactWhatChanged([page, pageSize, isFiltered, isSorted, filterTableParams]),
    );

    useEffect(() => {
        if (isFiltered && filterValues) {
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
    }, [filterValues, isFiltered]);

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

        const url = `?pageNumber=${page}&pageSize=${pageSize}&${query}`;
        setFilterUrl(url);
    }, [filterModel, sortModel, page, pageSize]);

    useEffect(() => {
        updateUrl();
    }, [updateUrl, filterModel, sortModel]);

    const rows = projects.map((p) => {
        return { ...p, id: p.projectId };
    });

    return { filterUrl, rows, filteredProjectsSize, sortModel, setSortModel, filterModel, setFilterModel };
};

export default useCustomSearchParams;
