import {
    DataGrid,
    GridColDef,
    GridColumnResizeParams,
    GridFilterInputMultipleSingleSelect,
    GridFilterItem,
    GridFilterModel,
    GridFilterOperator,
    GridPreProcessEditCellProps,
    GridRenderCellParams,
    GridRowParams,
    GridRowSelectionModel,
    GridSortModel,
    GridToolbarColumnsButton,
    GridToolbarContainer,
    GridToolbarFilterButton,
    getGridSingleSelectOperators,
    getGridStringOperators,
} from "@mui/x-data-grid";
import { useNavigate } from "react-router-dom";
import { useContext, useEffect, useState } from "react";
import { Box, Button, Dialog, DialogActions, DialogTitle, Stack, Tooltip, Typography } from "@mui/material";
import SaveAsIcon from "@mui/icons-material/SaveAs";
import UndoIcon from "@mui/icons-material/Undo";
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
import { getCustomProperties } from "../../api/adminSettingServices";

interface RowData {
    id: number;
}

type ColumnField =
    | "projectName"
    | "totalValue"
    | "projectOwners"
    | "confidentialityLevel"
    | "startDate"
    | "endDate"
    | "planType"
    | "priority"
    | "municipalityRole"
    | "projectPhase"
    | "planningPlanStatus"
    | "municipality"
    | "district"
    | "neighbourhood";

type ColumnConfig = {
    [key in ColumnField]?: {
        width?: number;
        show?: boolean;
    };
};

const initialColumnConfig: ColumnConfig = {
    projectName: {},
    totalValue: {},
    projectOwners: {},
    confidentialityLevel: {},
    startDate: {},
    endDate: {},
    planType: {},
    priority: {},
    municipalityRole: {},
    projectPhase: {},
    planningPlanStatus: {},
    municipality: {},
    district: {},
    neighbourhood: {},
};

type Props = {
    showCheckBox?: boolean;
    isExportPage?: boolean;
    handleProjectSelection?: (projectId: string | null | string[]) => void;
    selectedProjects?: string[];
    handleBack?: () => void;
    exportProjects?: () => void;
    handleDownload?: () => void;
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

type Category = {
    id: string;
    name: string;
};

type AreaProperties = {
    district: Category[];
    municipality: Category[];
    neighbourhood: Category[];
};

const confidentialityLevelComparator = (v1: string, v2: string): number => {
    const label1 = confidentialityLevelOptions.find((option) => option.id === v1)?.name || "";
    const label2 = confidentialityLevelOptions.find((option) => option.id === v2)?.name || "";
    return label1.localeCompare(label2);
};

const saveColumnConfig = (config: ColumnConfig) => {
    localStorage.setItem("projectsTableColumnConfig", JSON.stringify(config));
};

const loadColumnConfig = (): ColumnConfig | null => {
    const config = localStorage.getItem("projectsTableColumnConfig");
    return config ? JSON.parse(config) : null;
};

export const ProjectsTableView = ({
    showCheckBox,
    isExportPage = false,
    handleProjectSelection = () => {},
    selectedProjects = [],
    handleBack = () => {},
    exportProjects = () => {},
    handleDownload = () => {},
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
    const [areaProperties, setAreaProperties] = useState<AreaProperties | null>(null);
    const [columnConfig, setColumnConfig] = useState<ColumnConfig>(loadColumnConfig() || initialColumnConfig);

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

    useEffect(() => {
        getCustomProperties().then((customProperties) => {
            const filteredProperties = customProperties.filter((property) => ["district", "municipality", "neighbourhood"].includes(property.name));

            const areaProperties: AreaProperties = filteredProperties.reduce((acc, property) => {
                acc[property.name as keyof AreaProperties] =
                    property.categories?.map((category) => ({
                        ...category,
                        id: category.id || "",
                    })) || [];
                return acc;
            }, {} as AreaProperties);

            setAreaProperties(areaProperties);
        });
    }, []);

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

    const customAreaFilterOperator: GridFilterOperator = {
        label: "isAnyOf",
        value: "isAnyOf",
        getApplyFilterFn: (filterItem: GridFilterItem) => {
            if (!filterItem.value || !Array.isArray(filterItem.value) || filterItem.value.length === 0) {
                return () => true;
            }
            return (row) => {
                if (!row || row.length === 0) {
                    return () => true;
                }
                return row.some((item: { name: string }) => filterItem.value.includes(item.name));
            };
        },
        InputComponent: GridFilterInputMultipleSingleSelect,
    };

    const defaultColumnsWithSize: GridColDef[] = [
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
            width: 100,
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
            width: 100,
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
            width: 100,
            renderCell: (cellValues: GridRenderCellParams<Project>) => {
                return <>{cellValues.row?.priority?.value?.name}</>; //TODO FIX AFTER MIN MAX INTEGRATED
            },
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "municipalityRole",
            headerName: capitalizeFirstLetters(t("projects.tableColumns.municipalityRole")),
            display: "flex",
            width: 150,
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
            type: "singleSelect",
            valueOptions: areaProperties?.municipality.map((c) => {
                const option = { value: c.name, label: c.name };
                return option;
            }),
            filterOperators: [customAreaFilterOperator],
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
            type: "singleSelect",
            valueOptions: areaProperties?.district.map((c) => {
                const option = { value: c.name, label: c.name };
                return option;
            }),
            filterOperators: [customAreaFilterOperator],
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
            type: "singleSelect",
            valueOptions: areaProperties?.neighbourhood.map((c) => {
                const option = { value: c.name, label: c.name };
                return option;
            }),
            filterOperators: [customAreaFilterOperator],
            renderCell: (cellValues: GridRenderCellParams<Project>) => {
                const fixedProperties = cellValues?.row?.neighbourhood || [];
                return <CategoriesCell cellValues={fixedProperties} />;
            },
            preProcessEditCellProps: createErrorReport,
        },
    ];

    const defaultColumns: GridColDef[] = [
        {
            field: "projectName",
            headerName: capitalizeFirstLetters(t("projects.tableColumns.projectName")),
            display: "flex",
            width: columnConfig?.projectName?.width || 300,
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
            width: columnConfig?.totalValue?.width || 120,
            filterable: false,
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "projectOwners",
            headerName: capitalizeFirstLetters(t("projects.tableColumns.projectOwners")),
            display: "flex",
            width: columnConfig?.projectOwners?.width || 270,
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
            width: columnConfig?.confidentialityLevel?.width || 250,
            filterOperators: getGridSingleSelectOperators().filter((o) => o.value === "isAnyOf"),
            preProcessEditCellProps: createErrorReport,
            sortComparator: confidentialityLevelComparator,
        },
        {
            field: "startDate",
            headerName: capitalizeFirstLetters(t("projects.tableColumns.startDate")),
            display: "flex",
            type: "dateTime",
            width: columnConfig?.startDate?.width || 100,
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
            width: columnConfig?.endDate?.width || 100,
            valueFormatter: (p) => dayjs(p).format(dateFormats.keyboardDate),
            filterOperators: getGridStringOperators().filter((o) => o.value === "contains"),
            valueGetter: (value) => value && new Date(value),
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "planType",
            headerName: capitalizeFirstLetters(t("projects.tableColumns.planType")),
            display: "flex",
            width: columnConfig?.planType?.width || 500,
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
            width: columnConfig?.priority?.width || 100,
            renderCell: (cellValues: GridRenderCellParams<Project>) => {
                return <>{cellValues.row?.priority?.value?.name}</>; //TODO FIX AFTER MIN MAX INTEGRATED
            },
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "municipalityRole",
            headerName: capitalizeFirstLetters(t("projects.tableColumns.municipalityRole")),
            display: "flex",
            width: columnConfig?.municipalityRole?.width || 150,
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
            width: columnConfig?.projectPhase?.width || 250,
            filterOperators: getGridSingleSelectOperators().filter((o) => o.value === "isAnyOf"),
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "planningPlanStatus",
            headerName: capitalizeFirstLetters(t("projects.tableColumns.planningPlanStatus")),
            display: "flex",
            width: columnConfig?.planningPlanStatus?.width || 500,
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
            width: columnConfig?.municipality?.width || 320,
            type: "singleSelect",
            valueOptions: areaProperties?.municipality.map((c) => {
                const option = { value: c.name, label: c.name };
                return option;
            }),
            filterOperators: [customAreaFilterOperator],
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
            width: columnConfig?.district?.width || 320,
            type: "singleSelect",
            valueOptions: areaProperties?.district.map((c) => {
                const option = { value: c.name, label: c.name };
                return option;
            }),
            filterOperators: [customAreaFilterOperator],
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
            width: columnConfig?.neighbourhood?.width || 320,
            type: "singleSelect",
            valueOptions: areaProperties?.neighbourhood.map((c) => {
                const option = { value: c.name, label: c.name };
                return option;
            }),
            filterOperators: [customAreaFilterOperator],
            renderCell: (cellValues: GridRenderCellParams<Project>) => {
                const fixedProperties = cellValues?.row?.neighbourhood || [];
                return <CategoriesCell cellValues={fixedProperties} />;
            },
            preProcessEditCellProps: createErrorReport,
        },
    ];
    const [columns, setColumns] = useState<GridColDef[]>(defaultColumns);

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

    const handleColumnSizeChange = (params: GridColumnResizeParams) => {
        const field = params.colDef.field as ColumnField;
        const newColumnConfig = { ...columnConfig, [field]: { ...columnConfig[field], width: params.colDef.width } };
        setColumnConfig(newColumnConfig);
    };

    const initialVisibilityModel = Object.keys(columnConfig).reduce(
        (model, key) => {
            model[key as ColumnField] = !columnConfig[key as ColumnField]?.show;
            return model;
        },
        {} as { [key in ColumnField]: boolean },
    );
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
                columnVisibilityModel={initialVisibilityModel}
                onColumnVisibilityModelChange={(newModel) => {
                    setColumnConfig((prevConfig: ColumnConfig) => {
                        const newConfig = { ...prevConfig };
                        Object.keys(newModel).forEach((key) => {
                            if (newConfig[key as ColumnField]) {
                                newConfig[key as ColumnField]!.show = !newModel[key as ColumnField];
                            }
                        });
                        return newConfig;
                    });
                }}
                onRowSelectionModelChange={handleSelectionChange}
                onColumnResize={handleColumnSizeChange}
                localeText={{
                    toolbarColumns: t("projects.toolbarColumns"),
                    toolbarColumnsLabel: t("projects.toolbarColumnsLabel"),
                    toolbarFiltersLabel: t("projects.toolbarFiltersLabel"),
                }}
                slots={{
                    toolbar: () => (
                        <GridToolbarContainer>
                            <GridToolbarColumnsButton />
                            <GridToolbarFilterButton />
                            <Tooltip title={t("projects.saveColumnConfig")}>
                                <Stack
                                    direction="row"
                                    alignItems="center"
                                    onClick={() => {
                                        saveColumnConfig(columnConfig);
                                        setAlert(t("projects.successSaveColumnConfig"), "success");
                                    }}
                                    p={0.5}
                                    sx={{
                                        cursor: "pointer",
                                        borderRadius: 1,
                                        "&:hover": {
                                            backgroundColor: "#f8f8f8",
                                        },
                                    }}
                                >
                                    <SaveAsIcon color="primary" />
                                    <Typography color="primary" variant="caption" ml={1}>
                                        {" "}
                                        {t("projects.saveSetting")}
                                    </Typography>
                                </Stack>
                            </Tooltip>
                            <Tooltip title={t("projects.resetColumnConfig")}>
                                <Stack
                                    direction="row"
                                    alignItems="center"
                                    onClick={() => {
                                        saveColumnConfig(initialColumnConfig);
                                        localStorage.removeItem("projectsTableColumnConfig");
                                        setAlert(t("projects.successResetColumnConfig"), "success");
                                        setColumns(defaultColumnsWithSize);
                                        setColumnConfig(initialColumnConfig);
                                    }}
                                    p={0.5}
                                    sx={{
                                        cursor: "pointer",
                                        borderRadius: 1,
                                        "&:hover": {
                                            backgroundColor: "#f8f8f8",
                                        },
                                    }}
                                >
                                    <UndoIcon color="primary" />
                                    <Typography color="primary" variant="caption" ml={1}>
                                        {" "}
                                        {t("projects.resetSetting")}
                                    </Typography>
                                </Stack>
                            </Tooltip>
                        </GridToolbarContainer>
                    ),
                }}
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
                    <>
                        <Button
                            disabled={true}
                            sx={{ width: "130px", my: 2 }}
                            variant="contained"
                            onClick={() => {
                                setShowDialog(true);
                            }}
                        >
                            {t("projects.export")}
                        </Button>
                        <Button sx={{ width: "130px", my: 2 }} variant="contained" onClick={handleDownload}>
                            {t("projects.download")}
                        </Button>
                    </>
                )}
            </Box>
            <Box sx={{ height: 100 }}></Box>
            {!isExportPage && <AddProjectButton />}
        </Stack>
    );
};
