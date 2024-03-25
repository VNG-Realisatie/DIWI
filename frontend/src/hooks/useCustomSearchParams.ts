import { useLocation } from "react-router-dom";
import { GridFilterModel, GridLogicOperator, GridPaginationModel } from "@mui/x-data-grid";
import { useCallback, useEffect, useState } from "react";
import queryString from "query-string";
import { filterTable } from "../api/projectsTableServices";
import { getProjects } from "../api/projectsServices";
import { Project } from "../api/projectsServices";

const useCustomSearchParams = (filter: GridFilterModel | undefined, paginationInfo: GridPaginationModel) => {
    const [filterModel, setFilterModel] = useState<GridFilterModel | undefined>();
    const [projects, setProjects] = useState<Array<Project>>([]);
    const location = useLocation();
    const [filterUrl, setFilterUrl] = useState("");

    useEffect(() => {
        setFilterModel(filter);
    }, [filter]);

    useEffect(() => {
        localStorage.setItem("filterModel", JSON.stringify(filterModel));
    }, [filterModel]);

    const isFilteredUrl = useCallback(() => {
        const queryParams = ["pageNumber", "pageSize", "filterColumn", "filterCondition", "filterValue"];
        return queryParams.every((e) => location.search.includes(e));
    }, [location.search]);

    useEffect(() => {
        if (isFilteredUrl()) {
            filterTable(location.search).then((res) => setProjects(res));
        } else {
            getProjects(paginationInfo.page, paginationInfo.pageSize)
                .then((projects) => {
                    setProjects(projects);
                })
                .catch((err) => console.log(err));
        }
    }, [isFilteredUrl, location.search, paginationInfo.page, paginationInfo.pageSize]);

    useEffect(() => {
        if (isFilteredUrl()) {
            const filterValues = queryString.parse(location.search);

            const filterItems = Object.values(filterValues);
            const values = filterItems.slice(2, -2); //because 2 first are related to column and operator, two last are page number and size
            //when I refactor it to value: values.length === 1 ? (values[0] as string) : (values as string[]), it doesnt work anymore. to consider later
            if (filterItems.length >= 3) {
                let filter;
                if (values.length === 1) {
                    filter = {
                        items: [
                            {
                                field: filterItems[0] as string,
                                operator: filterItems[1] === "ANY_OF" ? "isAnyOf" : "contains",
                                value: values[0] as string,
                            },
                        ],
                        logicOperator: GridLogicOperator.And,
                    };
                } else {
                    filter = {
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
        }
    }, [isFilteredUrl, location.search]);

    const updateUrl = useCallback(() => {
        let query = "";
        if (filterModel && filterModel.items) {
            query = queryString.stringify({
                filterColumn: filterModel.items[0].field,
                filterCondition: filterModel.items[0].operator === "isAnyOf" ? "ANY_OF" : "CONTAINS",
                filterValue: filterModel.items[0].value,
            });
        }
        const url = `?pageNumber=${paginationInfo.page}&pageSize=${paginationInfo.pageSize}&${query}`;
        setFilterUrl(url);
    }, [filterModel, paginationInfo.page, paginationInfo.pageSize]);

    useEffect(() => {
        updateUrl();
    }, [updateUrl, filterModel]);

    const rows = projects.map((p) => {
        return { ...p, id: p.projectId };
    });

    return { filterUrl, rows };
};

export default useCustomSearchParams;
