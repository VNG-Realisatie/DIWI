import {
    DataGrid,
    GridCallbackDetails,
    GridColDef,
    GridFilterModel,
    GridPaginationModel,
    GridPreProcessEditCellProps,
    GridRenderCellParams,
    GridRowParams,
    getGridSingleSelectOperators,
    getGridStringOperators,
} from "@mui/x-data-grid";
import { useNavigate } from "react-router-dom";
import { useContext, useEffect, useState } from "react";
import { AvatarGroup, Box, Button, Dialog, DialogActions, DialogTitle, Stack, Typography } from "@mui/material";
import useAlert from "../../hooks/useAlert";
import { Project } from "../../api/projectsServices";
import { useTranslation } from "react-i18next";
import { PlanTypeCell } from "../table/PlanTypeCell";
import { MunicipalityRoleCell } from "../table/MunicipalityRoleCell";
import { PlanningPlanStatusCell } from "../table/PlanningPlanStatusCell";
// import { WijkCell } from "../table/WijkCell";
// import { BuurtCell } from "../table/NeighbourhoodCell";
// import { MunicipalityCell } from "../table/MunicipalityCell";
import { confidentialityLevelOptions, planTypeOptions, projectPhaseOptions } from "../table/constants";
import { OrganizationUserAvatars } from "../OrganizationUserAvatars";
import ProjectContext from "../../context/ProjectContext";
import useCustomSearchParams from "../../hooks/useCustomSearchParams";
import { AddProjectButton } from "../PlusButton";
import dayjs from "dayjs";
import { dateFormats } from "../../localization";

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
    // const [selectedPlanTypes, setSelectedPlanTypes] = useState<SelectedOptionWithId[]>([]);
    // const [selectedMunicipality, setSelectedMunicipality] = useState<SelectedOptionWithId[]>([]);
    // const [selectedMunicipalityRole, setSelectedMunicipalityRole] = useState<SelectedOptionWithId[]>([]);
    // const [selectedPlanStatus, setSelectedPlanStatus] = useState<SelectedOptionWithId[]>([]);
    // const [selectedWijk, setSelectedWijk] = useState<SelectedOptionWithId[]>([]);
    // const [selectedBuurt, setSelectedBuurt] = useState<SelectedOptionWithId[]>([]);
    const [showDialog, setShowDialog] = useState(false);
    const [filterModel, setFilterModel] = useState<GridFilterModel | undefined>();

    const { filterUrl, rows } = useCustomSearchParams(filterModel, paginationInfo);

    useEffect(() => {
        navigate(`/projects/table${filterUrl}`);
    }, [filterUrl, navigate, filterModel]);

    const handleExport = (params: GridRowParams) => {
        const clickedRow: RowData = params.row as RowData;
        if (selectedRows.includes(clickedRow.id)) {
            setSelectedRows(selectedRows.filter((s) => s !== clickedRow.id));
        } else {
            setSelectedRows([...selectedRows, clickedRow.id]);
        }
    };

    const handleClose = () => setShowDialog(false);

    // const handlePlanTypeChange = (_: React.ChangeEvent<{}>, values: OptionType[], id: string) => {
    //     const existingRecordIndex = selectedPlanTypes.findIndex((item) => item.id === id);

    //     if (existingRecordIndex !== -1) {
    //         const updatedSelectedPlanTypes = [...selectedPlanTypes];
    //         updatedSelectedPlanTypes[existingRecordIndex] = { id, option: values };
    //         setSelectedPlanTypes(updatedSelectedPlanTypes);
    //     } else {
    //         // If not exists, add a new record
    //         setSelectedPlanTypes([...selectedPlanTypes, { id, option: values }]);
    //     }
    //     //Add update endpoint later
    // };

    // const handleMunicipalityRoleChange = (_: React.ChangeEvent<{}>, values: OptionType[], id: string) => {
    //     const existingRecordIndex = selectedMunicipalityRole.findIndex((item) => item.id === id);

    //     if (existingRecordIndex !== -1) {
    //         const updatedMunicipality = [...selectedMunicipalityRole];
    //         updatedMunicipality[existingRecordIndex] = { id, option: values };
    //         setSelectedMunicipalityRole(updatedMunicipality);
    //     } else {
    //         // If not exists, add a new record
    //         setSelectedMunicipalityRole([...selectedMunicipalityRole, { id, option: values }]);
    //     }
    //     //Add update endpoint later
    // };

    // const handleStatusChange = (_: React.ChangeEvent<{}>, values: OptionType[], id: string) => {
    //     const existingRecordIndex = selectedPlanStatus.findIndex((item) => item.id === id);

    //     if (existingRecordIndex !== -1) {
    //         const updatedPlanStatus = [...selectedPlanStatus];
    //         updatedPlanStatus[existingRecordIndex] = { id, option: values };
    //         setSelectedPlanStatus(updatedPlanStatus);
    //     } else {
    //         // If not exists, add a new record
    //         setSelectedPlanStatus([...selectedPlanStatus, { id, option: values }]);
    //     }
    //     //Add update endpoint later
    // };

    // const handleMunicipalityChange = (_: React.ChangeEvent<{}>, values: OptionType[], id: string) => {
    //     const existingRecordIndex = selectedMunicipality.findIndex((item) => item.id === id);

    //     if (existingRecordIndex !== -1) {
    //         const updateMunicipality = [...selectedMunicipality];
    //         updateMunicipality[existingRecordIndex] = { id, option: values };
    //         setSelectedMunicipality(updateMunicipality);
    //     } else {
    //         // If not exists, add a new record
    //         setSelectedMunicipality([...selectedMunicipality, { id, option: values }]);
    //     }
    //     //Add update endpoint later
    // };

    // const handleWijkChange = (_: React.ChangeEvent<{}>, values: OptionType[], id: string) => {
    //     const existingRecordIndex = selectedWijk.findIndex((item) => item.id === id);

    //     if (existingRecordIndex !== -1) {
    //         const updateWijk = [...selectedWijk];
    //         updateWijk[existingRecordIndex] = { id, option: values };
    //         setSelectedWijk(updateWijk);
    //     } else {
    //         // If not exists, add a new record
    //         setSelectedWijk([...selectedWijk, { id, option: values }]);
    //     }
    //     //Add update endpoint later
    // };

    // const handleBuurtChange = (_: React.ChangeEvent<{}>, values: OptionType[], id: string) => {
    //     const existingRecordIndex = selectedBuurt.findIndex((item) => item.id === id);

    //     if (existingRecordIndex !== -1) {
    //         const updateBuurt = [...selectedBuurt];
    //         updateBuurt[existingRecordIndex] = { id, option: values };
    //         setSelectedBuurt(updateBuurt);
    //     } else {
    //         // If not exists, add a new record
    //         setSelectedBuurt([...selectedBuurt, { id, option: values }]);
    //     }
    //     //Add update endpoint later
    // };

    const createErrorReport = (params: GridPreProcessEditCellProps) => {
        const hasError = params.props.value.length < 3;
        return { ...params.props, error: hasError };
    };

    const columns: GridColDef[] = [
        {
            field: "projectName",
            headerName: t("projects.tableColumns.projectName"),
            width: 120,
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
            headerName: t("projects.tableColumns.totalValue"),
            width: 120,
            filterable: false,
            preProcessEditCellProps: createErrorReport,
        },

        {
            field: "projectOwners",
            headerName: t("projects.tableColumns.organizationName"),
            width: 160,
            filterOperators: getGridStringOperators().filter((o) => o.value === "contains"),
            renderCell: (cellValues: GridRenderCellParams<Project>) => {
                return (
                    <AvatarGroup max={3}>
                        <OrganizationUserAvatars organizations={cellValues?.row.projectOwners} />
                    </AvatarGroup>
                );
            },
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "confidentialityLevel",
            headerName: t("projects.tableColumns.confidentialityLevel"),
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
            headerName: t("projects.tableColumns.startDate"),
            type: "dateTime",
            valueFormatter: (p) => dayjs(p.value).format(dateFormats.keyboardDate),
            filterOperators: getGridStringOperators().filter((o) => o.value === "contains"),
            valueGetter: ({ value }) => value && new Date(value),
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "endDate",
            headerName: t("projects.tableColumns.endDate"),
            type: "dateTime",
            valueFormatter: (p) => dayjs(p.value).format(dateFormats.keyboardDate),
            filterOperators: getGridStringOperators().filter((o) => o.value === "contains"),
            valueGetter: ({ value }) => value && new Date(value),
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "planType",
            headerName: t("projects.tableColumns.planType"),
            width: 500,
            valueOptions: planTypeOptions.map((pt) => pt.id),
            type: "singleSelect",
            renderCell: (cellValues: GridRenderCellParams<Project>) => {
                return <PlanTypeCell cellValues={cellValues} />;
            },
            filterOperators: getGridStringOperators().filter((o) => o.value === "contains"),
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "priority",
            headerName: t("projects.tableColumns.priority"),
            renderCell: (cellValues: GridRenderCellParams<Project>) => {
                return <>{cellValues.row?.priority?.value?.name}</>; //TODO FIX AFTER MIN MAX INTEGRATED
            },
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "municipalityRole",
            headerName: t("projects.tableColumns.municipalityRole"),
            width: 320,
            renderCell: (cellValues: GridRenderCellParams<Project>) => {
                return <MunicipalityRoleCell cellValues={cellValues} />;
            },
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "projectLeaders",
            headerName: t("projects.tableColumns.projectLeader"),
            width: 160,
            filterOperators: getGridStringOperators().filter((o) => o.value === "contains"),
            renderCell: (cellValues: GridRenderCellParams<Project>) => {
                return (
                    <AvatarGroup max={3}>
                        <OrganizationUserAvatars organizations={cellValues?.row.projectLeaders} />
                    </AvatarGroup>
                );
            },
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "projectPhase",
            headerName: t("projects.tableColumns.projectPhase"),
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
            headerName: t("projects.tableColumns.planningPlanStatus"),
            width: 500,
            preProcessEditCellProps: createErrorReport,
            renderCell: (cellValues: GridRenderCellParams<Project>) => {
                return <PlanningPlanStatusCell cellValues={cellValues} />;
            },
        },
        // {
        //     field: "municipality",
        //     headerName: t("projects.tableColumns.municipality"),
        //     width: 320,
        //     renderCell: (cellValues: GridRenderCellParams<Project>) => {
        //         return (
        //             <MunicipalityCell cellValues={cellValues} selectedMunicipality={selectedMunicipality} handleMunicipalityChange={handleMunicipalityChange} />
        //         );
        //     },
        //     preProcessEditCellProps: createErrorReport,
        // },
        // {
        //     field: "wijk",
        //     headerName: t("projects.tableColumns.wijk"),
        //     width: 320,
        //     renderCell: (cellValues: GridRenderCellParams<Project>) => {
        //         return <WijkCell cellValues={cellValues} selectedWijk={selectedWijk} handleWijkChange={handleWijkChange} />;
        //     },
        //     preProcessEditCellProps: createErrorReport,
        // },
        // {
        //     field: "buurt",
        //     headerName: t("projects.tableColumns.neighbourhood"),
        //     width: 320,
        //     renderCell: (cellValues: GridRenderCellParams<Project>) => {
        //         return <BuurtCell cellValues={cellValues} selectedNeighbourhood={selectedBuurt} handleNeighbourhoodChange={handleBuurtChange} />;
        //     },
        //     preProcessEditCellProps: createErrorReport,
        // },
    ];

    const handleFilterModelChange = (newModel: GridFilterModel, details: GridCallbackDetails) => {
        if (details.reason === "deleteFilterItem") {
            if (newModel.items.some((item) => item.value == null)) {
                const updatedFilterModel = {
                    items: newModel.items.map((item) => ({
                        ...item,
                        value: item.value == null ? "" : item.value,
                    })),
                };
                setFilterModel(updatedFilterModel);
            }
        }
        if (newModel.items.length > 0) {
            setFilterModel(newModel);
        }
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
                rowCount={totalProjectCount}
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
            {showCheckBox && (
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
