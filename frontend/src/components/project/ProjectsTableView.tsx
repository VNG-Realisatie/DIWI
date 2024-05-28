import {
    DataGrid,
    GridCallbackDetails,
    GridColDef,
    GridFilterModel,
    GridPaginationModel,
    GridPreProcessEditCellProps,
    GridRenderCellParams,
    GridRowParams,
    GridSortModel,
    getGridSingleSelectOperators,
    getGridStringOperators,
} from "@mui/x-data-grid";
import { useNavigate } from "react-router-dom";
import { useContext, useEffect, useState } from "react";
import { Box, Button, Dialog, DialogActions, DialogTitle, Stack, Typography } from "@mui/material";
import useAlert from "../../hooks/useAlert";
import { Project } from "../../api/projectsServices";
import { useTranslation } from "react-i18next";
import { CategoriesCell } from "../table/CategoriesCell";
import { confidentialityLevelOptions, planTypeOptions, projectPhaseOptions } from "../table/constants";
import ProjectContext from "../../context/ProjectContext";
import useCustomSearchParams from "../../hooks/useCustomSearchParams";
import { AddProjectButton } from "../PlusButton";
import dayjs from "dayjs";
import { dateFormats } from "../../localization";
import { capitalizeFirstLetters } from "../../utils/stringFunctions";
<<<<<<< frontend/src/components/project/ProjectsTableView.tsx
import useAllowedActions from "../../hooks/useAllowedActions";
=======
import { UserGroupSelect } from "../../widgets/UserGroupSelect";
>>>>>>> frontend/src/components/project/ProjectsTableView.tsx

interface RowData {
    id: number;
}

type Props = {
    showCheckBox?: boolean;
};

export interface GenericOptionType<Type> {
    id: Type;
    name: string;
}

export type OptionType = GenericOptionType<string>;

export type SelectedOptionWithId = {
    id: string;
    option: OptionType[];
};

export const ProjectsTableView = ({ showCheckBox }: Props) => {
    const { paginationInfo, setPaginationInfo, totalProjectCount } = useContext(ProjectContext);

    const navigate = useNavigate();
    const { setAlert } = useAlert();
    const { t } = useTranslation();

    const [selectedRows, setSelectedRows] = useState<any[]>([]);
    const [showDialog, setShowDialog] = useState(false);
    const [filterModel, setFilterModel] = useState<GridFilterModel | undefined>();
    const [sortModel, setSortModel] = useState<GridSortModel | undefined>();

    const allowedActions = useAllowedActions();
    const { filterUrl, rows } = useCustomSearchParams(sortModel, filterModel, paginationInfo);

    useEffect(() => {
        if (filterUrl !== "") {
            navigate(`/projects/table${filterUrl}`);
        }
    }, [filterUrl, navigate, filterModel, sortModel]);

    const handleExport = (params: GridRowParams) => {
        const clickedRow: RowData = params.row as RowData;
        if (selectedRows.includes(clickedRow.id)) {
            setSelectedRows(selectedRows.filter((s) => s !== clickedRow.id));
        } else {
            setSelectedRows([...selectedRows, clickedRow.id]);
        }
    };

    const handleClose = () => setShowDialog(false);

    const createErrorReport = (params: GridPreProcessEditCellProps) => {
        const hasError = params.props.value.length < 3;
        return { ...params.props, error: hasError };
    };

    const columns: GridColDef[] = [
        {
            field: "projectName",
            headerName: capitalizeFirstLetters(t("projects.tableColumns.projectName")),
            display: "flex",
            width: 300,
            filterOperators: getGridStringOperators().filter((o) => o.value === "contains"),
            renderCell: (cellValues: GridRenderCellParams<Project>) => {
                return (
                    <Stack direction="row" spacing={1} alignItems="center">
                        <Box width="15px" height="15px" borderRadius="50%" sx={{ background: cellValues.row.projectColor }} />
                        <Typography fontSize={14}>{cellValues.row.projectName}</Typography>
                    </Stack>
                );
            },
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "totalValue",
            headerName: capitalizeFirstLetters(t("projects.tableColumns.totalValue")),
            display: "flex",
            width: 120,
            filterable: false,
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "projectOwners",
            headerName: capitalizeFirstLetters(t("projects.tableColumns.projectOwners")),
            display: "flex",
            width: 270,
            filterable: false,
            preProcessEditCellProps: createErrorReport,
            renderCell: (cellValues) => {
                return (
                    cellValues.row.projectOwners &&
                    cellValues.row.projectOwners.length > 0 && (
                        <UserGroupSelect readOnly={true} userGroup={cellValues.row.projectOwners} setUserGroup={() => {}} />
                    )
                );
            },
        },
        {
            field: "confidentialityLevel",
            headerName: capitalizeFirstLetters(t("projects.tableColumns.confidentialityLevel")),
            display: "flex",
            valueOptions: confidentialityLevelOptions.map((c) => {
                return { value: c.id, label: t(`projectTable.confidentialityLevelOptions.${c.name}`) };
            }),
            type: "singleSelect",
            width: 250,
            filterOperators: getGridSingleSelectOperators().filter((o) => o.value === "isAnyOf"),
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "startDate",
            headerName: capitalizeFirstLetters(t("projects.tableColumns.startDate")),
            display: "flex",
            type: "dateTime",
            valueFormatter: (p) => dayjs(p).format(dateFormats.keyboardDate),
            filterOperators: getGridStringOperators().filter((o) => o.value === "contains"),
            valueGetter: (value) => value && new Date(value),
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "endDate",
            headerName: capitalizeFirstLetters(t("projects.tableColumns.endDate")),
            display: "flex",
            type: "dateTime",
            valueFormatter: (p) => dayjs(p).format(dateFormats.keyboardDate),
            filterOperators: getGridStringOperators().filter((o) => o.value === "contains"),
            valueGetter: (value) => value && new Date(value),
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "planType",
            headerName: capitalizeFirstLetters(t("projects.tableColumns.planType")),
            display: "flex",
            width: 500,
            valueOptions: planTypeOptions.map((pt) => pt.id),
            type: "singleSelect",
            renderCell: (cellValues: GridRenderCellParams<Project>) => {
                const defaultPlanTypes = cellValues.row.planType?.map((c) => ({ id: c, name: t(`projectTable.planTypeOptions.${c}`) })) || [];
                return <CategoriesCell cellValues={defaultPlanTypes} />;
            },
            filterOperators: getGridStringOperators().filter((o) => o.value === "contains"),
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "priority",
            headerName: capitalizeFirstLetters(t("projects.tableColumns.priority")),
            display: "flex",
            renderCell: (cellValues: GridRenderCellParams<Project>) => {
                return <>{cellValues.row?.priority?.value?.name}</>; //TODO FIX AFTER MIN MAX INTEGRATED
            },
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "municipalityRole",
            headerName: capitalizeFirstLetters(t("projects.tableColumns.municipalityRole")),
            display: "flex",
            width: 320,
            renderCell: (cellValues: GridRenderCellParams<Project>) => {
                return <CategoriesCell cellValues={cellValues?.row?.municipalityRole || []} />;
            },
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "projectPhase",
            headerName: capitalizeFirstLetters(t("projects.tableColumns.projectPhase")),
            display: "flex",
            valueOptions: projectPhaseOptions.map((c) => {
                return { value: c.id, label: t(`projectTable.projectPhaseOptions.${c.name}`) };
            }),
            type: "singleSelect",
            width: 250,
            filterOperators: getGridSingleSelectOperators().filter((o) => o.value === "isAnyOf"),
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "planningPlanStatus",
            headerName: capitalizeFirstLetters(t("projects.tableColumns.planningPlanStatus")),
            display: "flex",
            width: 500,
            preProcessEditCellProps: createErrorReport,
            renderCell: (cellValues: GridRenderCellParams<Project>) => {
                const fixedProperties = cellValues?.row?.planningPlanStatus?.map((fp) => ({ id: fp, name: t(`projectTable.planningPlanStatus.${fp}`) })) || [];
                return <CategoriesCell cellValues={fixedProperties} />;
            },
        },
        {
            field: "municipality",
            headerName: capitalizeFirstLetters(t("projects.tableColumns.municipality")),
            display: "flex",
            width: 320,
            renderCell: (cellValues: GridRenderCellParams<Project>) => {
                const fixedProperties = cellValues?.row?.municipality || [];
                return <CategoriesCell cellValues={fixedProperties} />;
            },
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "wijk",
            headerName: capitalizeFirstLetters(t("projects.tableColumns.district")),
            display: "flex",
            width: 320,
            renderCell: (cellValues: GridRenderCellParams<Project>) => {
                const fixedProperties = cellValues?.row?.district || [];
                return <CategoriesCell cellValues={fixedProperties} />;
            },
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "buurt",
            headerName: capitalizeFirstLetters(t("projects.tableColumns.neighbourhood")),
            display: "flex",
            width: 320,
            renderCell: (cellValues: GridRenderCellParams<Project>) => {
                const fixedProperties = cellValues?.row?.neighbourhood || [];
                return <CategoriesCell cellValues={fixedProperties} />;
            },
            preProcessEditCellProps: createErrorReport,
        },
    ];

    const handleFilterModelChange = (newModel: GridFilterModel) => {
        if (newModel.items.some((item) => item.value)) {
            setFilterModel(newModel);
        } else {
            const updatedFilterModel: GridFilterModel = {
                items: newModel.items.map((item) => ({
                    ...item,
                    value: undefined,
                })),
            };
            setFilterModel(updatedFilterModel);
        }
    };

    const handleSortModelChange = (newSortModel: GridSortModel) => {
        setSortModel(newSortModel);
    };

    return (
        <Stack
            width="100%"
            sx={{
                margin: "0 auto",
                overflowX: "auto",
            }}
        >
            <DataGrid
                autoHeight
                sx={{
                    borderRadius: 0,
                }}
                checkboxSelection={showCheckBox}
                rows={rows}
                columns={columns}
                rowHeight={70}
                initialState={{
                    pagination: {
                        paginationModel: { page: 0, pageSize: 10 },
                    },
                }}
                pageSizeOptions={[5, 10, 25, 50, 100]}
                onPaginationModelChange={(model: GridPaginationModel, _: GridCallbackDetails) => {
                    setPaginationInfo({ page: model.page + 1, pageSize: model.pageSize });
                }}
                rowCount={filterModel?.items.some((item) => item.value) ? rows.length : totalProjectCount}
                paginationMode="server"
                onRowClick={
                    showCheckBox
                        ? handleExport
                        : (params: GridRowParams) => {
                              navigate(`/projects/${params.id}/characteristics`);
                          }
                }
                processRowUpdate={
                    (updatedRow, originalRow) => console.log(updatedRow)
                    //todo add update endpoint later
                }
                filterModel={filterModel}
                onFilterModelChange={handleFilterModelChange}
                sortModel={sortModel}
                onSortModelChange={handleSortModelChange}
            />
            <Dialog open={showDialog} onClose={handleClose} aria-labelledby="alert-dialog-title" aria-describedby="alert-dialog-description">
                <DialogTitle id="alert-dialog-title">{t("projects.confirmExport")}</DialogTitle>
                <DialogActions sx={{ px: 5, py: 3, ml: 15 }}>
                    <Button onClick={handleClose}>{t("projects.cancelExport")}</Button>
                    <Button
                        variant="contained"
                        onClick={() => {
                            handleClose();
                            setAlert(t("projects.successExport"), "success");
                        }}
                        autoFocus
                    >
                        {t("projects.exportIt")}
                    </Button>
                </DialogActions>
            </Dialog>
            {showCheckBox && allowedActions.includes("EXPORT_PROJECTS") && (
                <Button
                    sx={{ width: "130px", my: 2, ml: "auto" }}
                    variant="contained"
                    onClick={() => {
                        setShowDialog(true);
                    }}
                >
                    {t("projects.export")}
                </Button>
            )}
            <Box sx={{ height: 100 }}></Box>
            <AddProjectButton />
        </Stack>
    );
};
