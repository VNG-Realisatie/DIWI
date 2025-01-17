import { useSearchParams } from "react-router-dom";
import { GridFilterModel, GridLogicOperator, GridPaginationModel, GridSortModel } from "@mui/x-data-grid";
import { useCallback, useEffect, useState } from "react";
import queryString from "query-string";
import { filterTable } from "../api/projectsTableServices";
import { getProjects, getProjectsSizeWithParameters } from "../api/projectsServices";
import { Project } from "../api/projectsServices";
import { set } from "lodash";

const useCustomSearchParams = (
    defaultSort: GridSortModel | undefined,
    defaultFilter: GridFilterModel | undefined,
    defaultPaginationInfo: GridPaginationModel,
) => {
    const [filterModel, setFilterModel] = useState<GridFilterModel | undefined>(defaultFilter);
    const [sortModel, setSortModel] = useState<GridSortModel | undefined>(defaultSort);
    const [projects, setProjects] = useState<Array<Project>>([]);
    // const location = useLocation();
    const [filterUrl, setFilterUrl] = useState("");

    const [params] = useSearchParams();

    const [filterColumn, setFilterColumn] = useState<string | null>(null);
    const [filterValues, setFilterValues] = useState<string[]>([]);
    const [filterCondition, setFilterCondition] = useState<string | null>(null);
    // const [filterTableParams, setFilterTableParams] = useState<string>(decodeURIComponent(location.search));

    const [page, setPage] = useState<number>(defaultPaginationInfo.page);
    const [pageSize, setPageSize] = useState<number>(defaultPaginationInfo.pageSize);

    const [totalProjectCount, setTotalProjectCount] = useState<number>(0);

    console.log(filterUrl);

    // useEffect(() => {
    //     getProjectsSizeWithParameters(filterUrl)
    //         .then((size) => {
    //             setTotalProjectCount(size.size);
    //         })
    //         .catch((err) => console.log(err));
    // }, [filterUrl]);

    useEffect(
        () => {
            const pageSize = params.get("pageSize") || "10";
            const page = params.get("pageNumber") || "1";
            const filterColumn = params.get("filterColumn");
            const filterValues = params.getAll("filterValue");
            const filterCondition = params.get("filterCondition");

            setPageSize(parseInt(pageSize));
            setPage(parseInt(page));
            setFilterColumn(filterColumn);
            setFilterValues(filterValues);
            setFilterCondition(filterCondition);
        },
        // eslint-disable-next-line react-hooks/exhaustive-deps
        // reactWhatChanged([location.search, params]),
        [params],
    );

    useEffect(
        () => {
            // if (filterModel || sortModel) {
            const url = createSearchString(sortModel, filterModel, page, pageSize);
            setFilterUrl(url);

            filterTable(url).then((res) => {
                setProjects(res);
            });

            getProjectsSizeWithParameters(url)
            .then((size) => {
                setTotalProjectCount(size.size);
            })
            // } else {
            //     getProjects(page, pageSize)
            //         .then((projects) => {
            //             setProjects(projects);
            //         })
            //         .catch((err) => console.log(err));
            // }
        },
        // eslint-disable-next-line react-hooks/exhaustive-deps
        // reactWhatChanged([page, pageSize, filterModel, sortModel]),
        [page, pageSize, filterModel, sortModel],
    );

    useEffect(() => {
        if (filterColumn && filterCondition && filterValues) {
            const operator = filterCondition === "ANY_OF" ? "isAnyOf" : "contains";
            const filter = {
                items: [
                    {
                        field: filterColumn as string,
                        value: filterValues.length > 1 ? filterValues : filterValues[0],
                        operator,
                    },
                ],
                logicOperator: GridLogicOperator.And,
            };
            setFilterModel(filter);
        }
    }, [filterValues, filterColumn, filterCondition]);

    // const updateUrl = useCallback(() => {
    //     const url = createSearchString(sortModel, filterModel, page, pageSize);
    //     setFilterUrl(url);
    // }, [filterModel, sortModel, page, pageSize]);

    // useEffect(() => {
    //     updateUrl();
    // }, [updateUrl, filterModel, sortModel]);

    const rows = projects.map((p) => {
        return { ...p, id: p.projectId };
    });

    return { filterUrl, rows, sortModel, setSortModel, filterModel, setFilterModel, totalProjectCount, setPage, setPageSize };
};

export default useCustomSearchParams;
function createSearchString(sortModel: GridSortModel | undefined, filterModel: GridFilterModel | undefined, page: number, pageSize: number) {
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
    return url;
}
