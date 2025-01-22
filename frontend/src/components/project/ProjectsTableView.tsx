import {
    GridColDef,
    GridColumnResizeParams,
    GridFilterInputMultipleSingleSelect,
    GridFilterItem,
    GridFilterModel,
    GridFilterOperator,
    GridLocaleText,
    GridPreProcessEditCellProps,
    GridRenderCellParams,
    GridRowSelectionModel,
    GridSortModel,
    GridToolbarColumnsButton,
    GridToolbarContainer,
    GridToolbarFilterButton,
    getGridSingleSelectOperators,
    getGridStringOperators,
} from "@mui/x-data-grid";
import { useNavigate, useParams } from "react-router-dom";
import { Dispatch, SetStateAction, useEffect, useState } from "react";
import { Box, Stack, Tooltip, Typography } from "@mui/material";
import SaveAsIcon from "@mui/icons-material/SaveAs";
import UndoIcon from "@mui/icons-material/Undo";
import useAlert from "../../hooks/useAlert";
import { Project } from "../../api/projectsServices";
import { useTranslation } from "react-i18next";
import { CategoriesCell } from "../table/CategoriesCell";
import { confidentialityLevelOptions, planningPlanStatus, planTypeOptions, projectPhaseOptions } from "../table/constants";

import useCustomSearchParams from "../../hooks/useCustomSearchParams";

import dayjs from "dayjs";
import { dateFormats } from "../../localization";
import { capitalizeFirstLetters } from "../../utils/stringFunctions";
import { UserGroupSelect } from "../../widgets/UserGroupSelect";
import { confidentialityUpdate, configuredExport } from "../../Paths";
import useProperties from "../../hooks/useProperties";
import { ColumnConfig, ColumnField, initialColumnConfig, loadColumnConfig, saveColumnConfig } from "./projectTableConfig";
import TableComponent from "./TableComponent";
import { ConfidentialityLevel } from "../../types/enums";
import { getAllowedConfidentialityLevels } from "../../utils/exportUtils";

type Props = {
    redirectPath: string;
    setSelectedProjects?: Dispatch<SetStateAction<string[]>>;
    selectedProjects?: string[];
    confidentiality?: ConfidentialityLevel;
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

export const ProjectsTableView = ({ setSelectedProjects = () => {}, selectedProjects = [], redirectPath, confidentiality }: Props) => {
    const navigate = useNavigate();
    const { setAlert } = useAlert();
    const { t } = useTranslation();

    const [columnConfig, setColumnConfig] = useState<ColumnConfig>(loadColumnConfig() || initialColumnConfig);

    const { filterUrl, rows, sortModel, setSortModel, filterModel, setFilterModel, totalProjectCount, setPage, setPageSize } = useCustomSearchParams();
    const { exportId = "defaultExportId" } = useParams<{ exportId?: string }>();
    const [selectionModel, setSelectionModel] = useState<GridRowSelectionModel>([]);
    const { municipalityRolesOptions, districtOptions, neighbourhoodOptions, municipalityOptions } = useProperties();
    const [showCheckBox, setShowCheckBox] = useState(false);

    const configuredExportPath = configuredExport.toPath({ exportId });
    const confidentialityUpdatePath = confidentialityUpdate.toPath({ exportId });

    useEffect(() => {
        if (filterUrl !== "") {
            navigate(redirectPath + `${filterUrl}`);
        }
    }, [filterUrl, navigate, redirectPath]);

    useEffect(() => {
        if (redirectPath === configuredExportPath || redirectPath === confidentialityUpdatePath) {
            setShowCheckBox(true);
        }
    }, [redirectPath, configuredExportPath, confidentialityUpdatePath]);

    useEffect(() => {
        if (!confidentiality) return;
        if (redirectPath === configuredExportPath) {
            setFilterModel({
                items: [{ field: "confidentialityLevel", operator: "isAnyOf", value: getAllowedConfidentialityLevels(confidentiality) }],
            });
        }
    }, [redirectPath, configuredExportPath, setFilterModel, confidentiality]);

    useEffect(() => {
        if (selectedProjects.length === 0) {
            setSelectionModel([]);
        }
    }, [setSelectionModel, selectedProjects]);

    const handleSelectionChange = (newSelection: GridRowSelectionModel) => {
        setSelectionModel((prevSelection: GridRowSelectionModel) => {
            const isSelectAllActive = rows.every((row) => newSelection.includes(row.id));

            let newSelectionModel: GridRowSelectionModel;

            if (isSelectAllActive) {
                newSelectionModel = [...new Set([...prevSelection, ...newSelection])] as GridRowSelectionModel;
            } else {
                const filteredSelection = prevSelection.filter((id) => rows.every((row) => row.id !== id));
                newSelectionModel = [...new Set([...filteredSelection, ...newSelection])] as GridRowSelectionModel;
            }

            setSelectedProjects(newSelectionModel as string[]);
            return newSelectionModel;
        });
    };

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

    const planningFilterOperator: GridFilterOperator = {
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
                return row.some((item: []) => filterItem.value.includes(item));
            };
        },
        InputComponent: GridFilterInputMultipleSingleSelect,
    };
    const createErrorReport = (params: GridPreProcessEditCellProps) => {
        const hasError = params.props.value.length < 3;
        return { ...params.props, error: hasError };
    };

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
            valueOptions: planTypeOptions.map((pt) => {
                return { value: pt.name, label: t(`projectTable.planTypeOptions.${pt.name}`) };
            }),
            type: "singleSelect",
            filterOperators: [planningFilterOperator],
            renderCell: (cellValues: GridRenderCellParams<Project>) => {
                const defaultPlanTypes = cellValues.row.planType?.map((c) => ({ id: c, name: t(`projectTable.planTypeOptions.${c}`) })) || [];
                return <CategoriesCell cellValues={defaultPlanTypes} />;
            },
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
            type: "singleSelect",
            valueOptions: municipalityRolesOptions?.map((c) => {
                const option = { value: c.name, label: c.name };
                return option;
            }),
            filterOperators: [customAreaFilterOperator],
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
            valueOptions: planningPlanStatus.map((pt) => {
                return { value: pt.name, label: t(`projectTable.planningPlanStatus.${pt.name}`) };
            }),
            type: "singleSelect",
            filterOperators: [planningFilterOperator],
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
            valueOptions: municipalityOptions?.map((c) => {
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
            valueOptions: districtOptions?.map((c) => {
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
            valueOptions: neighbourhoodOptions?.map((c) => {
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
    return (
        <>
            <TableComponent
                showCheckBox={showCheckBox}
                rows={rows}
                columns={defaultColumns}
                setPaginationInfo={(info) => {
                    setPage(info.page);
                    setPageSize(info.pageSize);
                }}
                rowCount={totalProjectCount}
                paginationMode="server"
                onRowClick={showCheckBox ? () => {} : (params) => navigate(`/projects/${params.id}/characteristics`)}
                filterModel={filterModel}
                handleFilterModelChange={handleFilterModelChange}
                sortModel={sortModel}
                handleSortModelChange={handleSortModelChange}
                selectionModel={selectionModel}
                columnVisibilityModel={initialVisibilityModel}
                onColumnVisibilityModelChange={(newModel) => {
                    setColumnConfig((prevConfig: ColumnConfig) => {
                        const newConfig = { ...prevConfig };
                        Object.keys(newModel).forEach((key) => {
                            if (newConfig[key as ColumnField]) {
                                newConfig[key as ColumnField].show = !newModel[key as ColumnField];
                            }
                        });
                        return newConfig;
                    });
                }}
                onRowSelectionModelChange={handleSelectionChange}
                onColumnResize={handleColumnSizeChange}
                localeText={
                    {
                        toolbarColumns: t("projects.toolbarColumns"),
                        toolbarColumnsLabel: t("projects.toolbarColumnsLabel"),
                        toolbarFiltersLabel: t("projects.toolbarFiltersLabel"),
                    } as GridLocaleText
                }
                isRowSelectable={(params) => {
                    if (!confidentiality) return false;
                    return getAllowedConfidentialityLevels(confidentiality).includes(params.row.confidentialityLevel);
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
        </>
    );
};
