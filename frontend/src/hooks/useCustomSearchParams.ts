import { useSearchParams } from "react-router-dom";
import { GridFilterModel, GridLogicOperator, GridSortModel } from "@mui/x-data-grid";
import { useEffect, useState } from "react";
import queryString from "query-string";
import { filterTable } from "../api/projectsTableServices";
import { getProjectsSizeWithParameters } from "../api/projectsServices";
import { Project } from "../api/projectsServices";

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

const useCustomSearchParams = () => {
    const [filterModel, setFilterModel] = useState<GridFilterModel | undefined>(undefined);
    const [sortModel, setSortModel] = useState<GridSortModel | undefined>(undefined);
    const [projects, setProjects] = useState<Array<Project>>([]);
    const [filterUrl, setFilterUrl] = useState("");

    const [params] = useSearchParams();

    const [filterColumn, setFilterColumn] = useState<string | null>(null);
    const [filterValues, setFilterValues] = useState<string[]>([]);
    const [filterCondition, setFilterCondition] = useState<string | null>(null);

    const [page, setPage] = useState<number | undefined>(undefined);
    const [pageSize, setPageSize] = useState<number | undefined>(undefined);

    const [totalProjectCount, setTotalProjectCount] = useState<number>(0);

    useEffect(() => {
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
    }, [params]);

    useEffect(() => {
        if (!page || !pageSize) return;
        const url = createSearchString(sortModel, filterModel, page, pageSize);
        setFilterUrl(url);

        filterTable(url).then((res) => {
            setProjects(res);
        });

        getProjectsSizeWithParameters(url).then((size) => {
            setTotalProjectCount(size.size);
        });
    }, [page, pageSize, filterModel, sortModel]);

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

    const rows = projects.map((p) => {
        return { ...p, id: p.projectId };
    });

    return { filterUrl, rows, sortModel, setSortModel, filterModel, setFilterModel, totalProjectCount, setPage, setPageSize };
};

export default useCustomSearchParams;
