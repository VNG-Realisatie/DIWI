import { useSearchParams } from "react-router-dom";
import { GridFilterModel, GridSortModel } from "@mui/x-data-grid";
import { useEffect, useState } from "react";
import queryString from "query-string";
import { filterTable } from "../api/projectsTableServices";
import { getProjectsSizeWithParameters } from "../api/projectsServices";
import { Project } from "../api/projectsServices";
import { debounce } from "lodash";

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
    const [isLoading, setIsLoading] = useState(false);

    const [params] = useSearchParams();

    const [page, setPage] = useState<number | undefined>(undefined);
    const [pageSize, setPageSize] = useState<number | undefined>(undefined);

    const [totalProjectCount, setTotalProjectCount] = useState<number>(0);

    useEffect(() => {
        const pageSize = params.get("pageSize") || "10";
        const page = params.get("pageNumber") || "1";

        setPageSize(parseInt(pageSize));
        setPage(parseInt(page));
    }, [params]);

    useEffect(() => {
        const debouncedFetchData = debounce(
            (page: number | undefined, pageSize: number | undefined, sortModel: GridSortModel | undefined, filterModel: GridFilterModel | undefined) => {
                if (!page || !pageSize) return;
                setIsLoading(true);
                const url = createSearchString(sortModel, filterModel, page, pageSize);
                setFilterUrl(url);

                filterTable(url).then((res) => {
                    setProjects(res);
                });

                getProjectsSizeWithParameters(url).then((size) => {
                    setTotalProjectCount(size.size);
                    setIsLoading(false);
                });
            },
            300,
        );

        debouncedFetchData(page, pageSize, sortModel, filterModel);

        return () => {
            debouncedFetchData.cancel();
        };
    }, [page, pageSize, filterModel, sortModel]);

    const rows = projects.map((p) => {
        return { ...p, id: p.projectId };
    });

    return { filterUrl, rows, sortModel, setSortModel, filterModel, setFilterModel, totalProjectCount, setPage, setPageSize, isLoading, page, pageSize };
};

export default useCustomSearchParams;
