import { useState, useEffect, useCallback } from "react";
import { Box, Typography } from "@mui/material";
import { getProjectAudit, ProjectAudit } from "../api/projectsServices";
import TableComponent from "../components/project/TableComponent";
import DateInput from "../components/project/inputs/DateInput";
import dayjs, { Dayjs } from "dayjs";
import { useParams } from "react-router-dom";
import { t } from "i18next";
import { getGridStringOperators, GridCellParams, GridColDef, GridFilterItem, GridFilterOperator, GridSortCellParams } from "@mui/x-data-grid";

const ProjectAuditTable = () => {
    const [auditLogs, setAuditLogs] = useState<ProjectAudit[]>([]);
    const [startDate, setStartDate] = useState<Dayjs | null>(null);
    const [endDate, setEndDate] = useState<Dayjs | null>(null);

    const { projectId } = useParams<{ projectId: string }>();

    const fetchAuditLogs = useCallback(async () => {
        const formattedStartDate = startDate ? encodeURIComponent(startDate.format("YYYY-MM-DDTHH:mm:ss")) : "";
        const formattedEndDate = endDate ? encodeURIComponent(endDate.endOf("day").format("YYYY-MM-DDTHH:mm:ss")) : "";
        const query = `projectId=${projectId}&startTime=${formattedStartDate}&endTime=${formattedEndDate}`;
        const logs = await getProjectAudit(query);
        setAuditLogs(logs);
    }, [startDate, endDate, projectId]);

    useEffect(() => {
        if (!startDate || !endDate) return;
        fetchAuditLogs();
    }, [startDate, endDate, fetchAuditLogs]);

    const stringOperators = getGridStringOperators().filter((operator) => operator.value === "contains");

    const sortComparator = (cellParams1: GridSortCellParams, cellParams2: GridSortCellParams, translationKeyPrefix: string) => {
        const row1 = cellParams1.api.getRow(cellParams1.id);
        const row2 = cellParams2.api.getRow(cellParams2.id);
        const property1 = t(`${translationKeyPrefix}.${row1.property}`);
        const property2 = t(`${translationKeyPrefix}.${row2.property}`);
        return property1.localeCompare(property2);
    };

    const getFilterOperators = (translationKeyPrefix: string): GridFilterOperator[] => {
        return stringOperators.map((operator) => ({
            ...operator,
            getApplyFilterFn: (filterItem: GridFilterItem) => {
                return (params: GridCellParams) => {
                    const property = t(`${translationKeyPrefix}.${params}`).toLowerCase();
                    return property.includes(filterItem.value?.toLowerCase());
                };
            },
        }));
    };

    const valuesSortComparator = (v1: string, v2: string, cellParams1: GridSortCellParams, cellParams2: GridSortCellParams) => {
        const row1 = cellParams1.api.getRow(cellParams1.id);
        const row2 = cellParams2.api.getRow(cellParams2.id);
        const values1 = row1.oldValues?.join(", ") || "";
        const values2 = row2.oldValues?.join(", ") || "";
        return values1.localeCompare(values2);
    };
    const columns: GridColDef[] = [
        {
            field: "action",
            headerName: t("audit.tableHeader.action"),
            flex: 1,
            renderCell: (params: { row: { action: string } }) => <Typography>{t(`audit.action.${params.row.action}`)}</Typography>,
            sortComparator: (v1, v2, cellParams1, cellParams2) => sortComparator(cellParams1, cellParams2, "audit.action"),
            filterable: true,
            filterOperators: getFilterOperators("audit.action"),
        },
        {
            field: "property",
            headerName: t("audit.tableHeader.property"),
            flex: 1,
            renderCell: (params: { row: { property: string } }) => <Typography>{t(`audit.property.${params.row.property}`)}</Typography>,
            sortComparator: (v1, v2, cellParams1, cellParams2) => sortComparator(cellParams1, cellParams2, "audit.property"),
            filterable: true,
            filterOperators: getFilterOperators("audit.property"),
        },
        {
            field: "oldValues",
            headerName: t("audit.tableHeader.oldValue"),
            flex: 2,
            renderCell: (params: { row: { property: string; oldValues?: string[] } }) => {
                const { property, oldValues } = params.row;
                if (property === "projectConfidentiality" && oldValues) {
                    return <Typography>{oldValues.map((value) => t(`audit.confidentialityLevel.${value}`)).join(", ")}</Typography>;
                }
                return <Typography>{oldValues?.join(", ") || ""}</Typography>;
            },
            sortable: true,
            sortComparator: valuesSortComparator,
        },
        {
            field: "newValues",
            headerName: t("audit.tableHeader.newValue"),
            flex: 2,
            renderCell: (params: { row: { property: string; newValues?: string[] } }) => {
                const { property, newValues } = params.row;
                if (property === "projectConfidentiality" && newValues) {
                    return <Typography>{newValues.map((value) => t(`audit.confidentialityLevel.${value}`)).join(", ")}</Typography>;
                }
                return <Typography>{newValues?.join(", ") || ""}</Typography>;
            },
            sortable: true,
            sortComparator: valuesSortComparator,
        },
        { field: "changeUser", headerName: t("audit.tableHeader.user"), flex: 1, renderCell: (params) => <Typography>{params.row.changeUser}</Typography> },
        {
            field: "changeDate",
            headerName: t("audit.tableHeader.date"),
            flex: 1,
            renderCell: (params: { row: { changeDate: string } }) => {
                return <Typography>{dayjs(params.row.changeDate).format("YYYY-MM-DD HH:mm:ss")}</Typography>;
            },
        },
    ];

    return (
        <Box style={{ margin: "10px 10px 40px 10px" }}>
            <Box style={{ display: "flex", justifyContent: "space-between", marginBottom: "20px", gap: "40px" }}>
                <DateInput
                    value={startDate ? startDate.format("YYYY-MM-DD") : null}
                    setValue={setStartDate}
                    readOnly={false}
                    mandatory={true}
                    errorText={t("createProject.hasMissingRequiredAreas.startDate")}
                    title={t("createProject.informationForm.startDate")}
                />
                <DateInput
                    value={endDate ? endDate.format("YYYY-MM-DD") : null}
                    setValue={setEndDate}
                    readOnly={false}
                    mandatory={true}
                    errorText={t("createProject.hasMissingRequiredAreas.endDate")}
                    title={t("createProject.informationForm.endDate")}
                />
            </Box>

            <TableComponent
                showCheckBox={false}
                rows={auditLogs.map((log, index) => ({ ...log, id: log.changeDate ? log.changeDate + index : index }))}
                columns={columns}
            />
        </Box>
    );
};

export default ProjectAuditTable;
