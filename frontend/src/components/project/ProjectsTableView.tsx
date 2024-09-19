import {
    DataGrid,
    GridColDef,
    GridFilterModel,
    GridPreProcessEditCellProps,
    GridRenderCellParams,
    GridRowParams,
    GridRowSelectionModel,
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
import useAllowedActions from "../../hooks/useAllowedActions";
import { UserGroupSelect } from "../../widgets/UserGroupSelect";

interface RowData {
    id: number;
}

type Props = {
    showCheckBox?: boolean;
    isExportPage?: boolean;
    handleProjectSelection?: (projectId: string | null | string[]) => void;
    selectedProjects?: string[];
    handleBack?: () => void;
    exportProjects?: () => void;
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

const confidentialityLevelComparator = (v1: string, v2: string): number => {
    const label1 = confidentialityLevelOptions.find((option) => option.id === v1)?.name || "";
    const label2 = confidentialityLevelOptions.find((option) => option.id === v2)?.name || "";
    return label1.localeCompare(label2);
};

export const ProjectsTableView = ({
    showCheckBox,
    isExportPage = false,
    handleProjectSelection = () => {},
    selectedProjects = [],
    handleBack = () => {},
    exportProjects = () => {},
}: Props) => {
    const { paginationInfo, setPaginationInfo, totalProjectCount } = useContext(ProjectContext);

    const navigate = useNavigate();
    const { setAlert } = useAlert();
    const { t } = useTranslation();

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const [selectedRows, setSelectedRows] = useState<any[]>([]);
    const [showDialog, setShowDialog] = useState(false);
    const [filterModel, setFilterModel] = useState<GridFilterModel | undefined>();
    const [sortModel, setSortModel] = useState<GridSortModel | undefined>();
    const [previousSelection, setPreviousSelection] = useState<GridRowSelectionModel>([]);

    const { allowedActions } = useAllowedActions();
    const { filterUrl, rows, filteredProjectsSize } = useCustomSearchParams(sortModel, filterModel, paginationInfo);

    useEffect(() => {
        if (filterUrl !== "") {
            if (!isExportPage) {
                navigate(`/projects/table${filterUrl}`);
            } else {
                navigate(`/exchangedata/export${filterUrl}`);
            }
        }
    }, [filterUrl, navigate, filterModel, sortModel, isExportPage]);

    useEffect(() => {
        if (isExportPage) {
            setFilterModel({ items: [{ field: "confidentialityLevel", operator: "isAnyOf", value: ["PUBLIC", "EXTERNAL_GOVERNMENTAL"] }] });
        }
    }, [isExportPage]);

    const handleExport = (params: GridRowParams) => {
        const clickedRow: RowData = params.row as RowData;
        if (selectedRows.includes(clickedRow.id)) {
            setSelectedRows(selectedRows.filter((s) => s !== clickedRow.id));
        } else {
            setSelectedRows([...selectedRows, clickedRow.id]);
        }
    };

    const createErrorReport = (params: GridPreProcessEditCellProps) => {
        const hasError = params.props.value.length < 3;
        return { ...params.props, error: hasError };
    };

    const handleSelectionChange = (newSelection: GridRowSelectionModel) => {
        const added = newSelection.filter((id) => !previousSelection.includes(id));
        const removed = previousSelection.filter((id) => !newSelection.includes(id));

        if (newSelection.length === 0) {
            handleProjectSelection(null);
        } else if (newSelection.length === rows.length) {
            handleProjectSelection(rows.map((row) => row.id));
        } else {
            if (added.length > 0) {
                handleProjectSelection(added[0] as string);
            } else if (removed.length > 0) {
                handleProjectSelection(removed[0] as string);
            }
        }

        setPreviousSelection(newSelection);
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
            sortComparator: (v1: string, v2: string): number => {
                const num1 = parseInt(v1.replace(/\D/g, ""));
                const num2 = parseInt(v2.replace(/\D/g, ""));
                if (!isNaN(num1) && !isNaN(num2)) {
                    return num1 - num2;
                }
                return v1.localeCompare(v2);
            },
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
                        <UserGroupSelect
                            checkIsOwnerValidWithConfidentialityLevel={() => true}
                            mandatory={false}
                            errorText=""
                            readOnly={true}
                            userGroup={cellValues.row.projectOwners}
                            setUserGroup={() => {}}
                        />
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
            sortComparator: confidentialityLevelComparator,
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
            field: "district",
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
            field: "neighbourhood",
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
        if (isExportPage) return;

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

    const handleProjectsExport = () => {
        exportProjects();
        setShowDialog(false);
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
                checkboxSelection={showCheckBox || isExportPage}
                rows={rows}
                columns={columns}
                rowHeight={70}
                initialState={{
                    pagination: {
                        paginationModel: { page: 0, pageSize: 10 },
                    },
                }}
                pageSizeOptions={[5, 10, 25, 50, 100]}
                onPaginationModelChange={(model) => {
                    setPaginationInfo({ page: model.page + 1, pageSize: model.pageSize });
                }}
                rowCount={filterModel?.items.some((item) => item.value) ? filteredProjectsSize : totalProjectCount}
                paginationMode="server"
                onRowClick={
                    isExportPage
                        ? () => {}
                        : showCheckBox
                          ? handleExport
                          : (params: GridRowParams) => {
                                navigate(`/projects/${params.id}/characteristics`);
                            }
                }
                processRowUpdate={
                    (updatedRow) => console.log(updatedRow)
                    //todo add update endpoint later
                }
                filterModel={filterModel}
                onFilterModelChange={handleFilterModelChange}
                sortModel={sortModel}
                onSortModelChange={handleSortModelChange}
                rowSelectionModel={selectedProjects}
                onRowSelectionModelChange={handleSelectionChange}
            />
            <Dialog open={showDialog} onClose={() => setShowDialog(false)} aria-labelledby="alert-dialog-title" aria-describedby="alert-dialog-description">
                <DialogTitle id="alert-dialog-title">{t("projects.confirmExport")}</DialogTitle>
                <DialogActions sx={{ px: 5, py: 3, ml: 15 }}>
                    <Button onClick={() => setShowDialog(false)}>{t("projects.cancelExport")}</Button>
                    <Button
                        variant="contained"
                        onClick={() => {
                            handleProjectsExport();
                            setAlert(t("projects.successExport"), "success");
                        }}
                        autoFocus
                    >
                        {t("projects.exportIt")}
                    </Button>
                </DialogActions>
            </Dialog>
            <Box sx={{ display: "flex", justifyContent: "right", gap: "3px" }}>
                {isExportPage && (
                    <Button sx={{ width: "130px", my: 2 }} variant="contained" color="primary" onClick={handleBack}>
                        {t("generic.previousStep")}
                    </Button>
                )}
                {(showCheckBox || isExportPage) && allowedActions.includes("EXPORT_PROJECTS") && (
                    <Button
                        sx={{ width: "130px", my: 2 }}
                        variant="contained"
                        onClick={() => {
                            setShowDialog(true);
                        }}
                    >
                        {t("projects.export")}
                    </Button>
                )}
            </Box>
            <Box sx={{ height: 100 }}></Box>
            {!isExportPage && <AddProjectButton />}
        </Stack>
    );
};
