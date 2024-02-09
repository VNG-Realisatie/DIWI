import {
    DataGrid,
    GridCallbackDetails,
    GridColDef,
    GridFilterModel,
    GridPaginationModel,
    GridPreProcessEditCellProps,
    GridRenderCellParams,
    getGridSingleSelectOperators,
    getGridStringOperators,
} from "@mui/x-data-grid";
import { GridRowParams } from "@mui/x-data-grid";
import { useLocation, useNavigate } from "react-router-dom";
import { useCallback,  useEffect, useState } from "react";
import { Box, Button, Dialog, DialogActions, DialogTitle, Stack, Typography } from "@mui/material";
import useAlert from "../hooks/useAlert";
import { Project, getProjects } from "../api/projectsServices";
import { useTranslation } from "react-i18next";
import { PlanTypeCell } from "./table/PlanTypeCell";
import { MunicipalityRoleCell } from "./table/MunicipalityRoleCell";
import { PlanningPlanStatusCell } from "./table/PlanningPlanStatusCell";
import { WijkCell } from "./table/WijkCell";
import { BuurtCell } from "./table/BuurtCell";
import { MunicipalityCell } from "./table/MunicipalityCell";
import { planTypeOptions } from "./table/constants";
import { filterTable } from "../api/projectsTableServices";

interface RowData {
    id: number;
}

type Props = {
    showCheckBox?: boolean;
};

export interface OptionType {
    id: string;
    name: string;
}
export type SelectedOptionWithId = {
    id: string;
    option: OptionType[];
};

const confidentialityLevelOptions = ["PRIVE", "INTERN_UITVOERING", "INTERN_RAPPORTAGE", "EXTERN_RAPPORTAGE", "OPENBAAR"];

export const ProjectsTableView = ({ showCheckBox }: Props) => {
    // const { projects } = useContext(ProjectContext);
    const location = useLocation();
    const navigate = useNavigate();
    const { setAlert } = useAlert();
    const { t } = useTranslation();

    const [projects, setProjects] = useState<Array<Project>>([]);
    const [selectedRows, setSelectedRows] = useState<any[]>([]);
    const [selectedPlanTypes, setSelectedPlanTypes] = useState<SelectedOptionWithId[]>([]);
    const [selectedMunicipality, setSelectedMunicipality] = useState<SelectedOptionWithId[]>([]);
    const [selectedMunicipalityRole, setSelectedMunicipalityRole] = useState<SelectedOptionWithId[]>([]);
    const [selectedPlanStatus, setSelectedPlanStatus] = useState<SelectedOptionWithId[]>([]);
    const [selectedWijk, setSelectedWijk] = useState<SelectedOptionWithId[]>([]);
    const [selectedBuurt, setSelectedBuurt] = useState<SelectedOptionWithId[]>([]);
    const [showDialog, setShowDialog] = useState(false);
    const [filterModel, setFilterModel] = useState<GridFilterModel>({
        items: [],
    });

    const [paginationInfo, setPaginationInfo] = useState<GridPaginationModel>({ page: 1, pageSize: 10 });

    useEffect(() => {
        getProjects(paginationInfo.page, paginationInfo.pageSize)
            .then((projects) => setProjects(projects))
            .catch((err) => console.log(err));
    }, [paginationInfo.page, paginationInfo.pageSize]);

    const rows = projects.map((p) => {
        return { ...p, id: p.projectId };
    });

    const handleExport = (params: GridRowParams) => {
        const clickedRow: RowData = params.row as RowData;
        if (selectedRows.includes(clickedRow.id)) {
            setSelectedRows(selectedRows.filter((s) => s !== clickedRow.id));
        } else {
            setSelectedRows([...selectedRows, clickedRow.id]);
        }
    };

    const handleClose = () => setShowDialog(false);

    const handlePlanTypeChange = (_: React.ChangeEvent<{}>, values: OptionType[], id: string) => {
        const existingRecordIndex = selectedPlanTypes.findIndex((item) => item.id === id);

        if (existingRecordIndex !== -1) {
            const updatedSelectedPlanTypes = [...selectedPlanTypes];
            updatedSelectedPlanTypes[existingRecordIndex] = { id, option: values };
            setSelectedPlanTypes(updatedSelectedPlanTypes);
        } else {
            // If not exists, add a new record
            setSelectedPlanTypes([...selectedPlanTypes, { id, option: values }]);
        }
        //Add update endpoint later
    };

    const handleMunicipalityRoleChange = (_: React.ChangeEvent<{}>, values: OptionType[], id: string) => {
        const existingRecordIndex = selectedMunicipalityRole.findIndex((item) => item.id === id);

        if (existingRecordIndex !== -1) {
            const updatedMunicipality = [...selectedMunicipalityRole];
            updatedMunicipality[existingRecordIndex] = { id, option: values };
            setSelectedMunicipalityRole(updatedMunicipality);
        } else {
            // If not exists, add a new record
            setSelectedMunicipalityRole([...selectedMunicipalityRole, { id, option: values }]);
        }
        //Add update endpoint later
    };

    const handleStatusChange = (_: React.ChangeEvent<{}>, values: OptionType[], id: string) => {
        const existingRecordIndex = selectedPlanStatus.findIndex((item) => item.id === id);

        if (existingRecordIndex !== -1) {
            const updatedPlanStatus = [...selectedPlanStatus];
            updatedPlanStatus[existingRecordIndex] = { id, option: values };
            setSelectedPlanStatus(updatedPlanStatus);
        } else {
            // If not exists, add a new record
            setSelectedPlanStatus([...selectedPlanStatus, { id, option: values }]);
        }
        //Add update endpoint later
    };

    const handleMunicipalityChange = (_: React.ChangeEvent<{}>, values: OptionType[], id: string) => {
        const existingRecordIndex = selectedMunicipality.findIndex((item) => item.id === id);

        if (existingRecordIndex !== -1) {
            const updateMunicipality = [...selectedMunicipality];
            updateMunicipality[existingRecordIndex] = { id, option: values };
            setSelectedMunicipality(updateMunicipality);
        } else {
            // If not exists, add a new record
            setSelectedMunicipality([...selectedMunicipality, { id, option: values }]);
        }
        //Add update endpoint later
    };

    const handleWijkChange = (_: React.ChangeEvent<{}>, values: OptionType[], id: string) => {
        const existingRecordIndex = selectedWijk.findIndex((item) => item.id === id);

        if (existingRecordIndex !== -1) {
            const updateWijk = [...selectedWijk];
            updateWijk[existingRecordIndex] = { id, option: values };
            setSelectedWijk(updateWijk);
        } else {
            // If not exists, add a new record
            setSelectedWijk([...selectedWijk, { id, option: values }]);
        }
        //Add update endpoint later
    };

    const handleBuurtChange = (_: React.ChangeEvent<{}>, values: OptionType[], id: string) => {
        const existingRecordIndex = selectedBuurt.findIndex((item) => item.id === id);

        if (existingRecordIndex !== -1) {
            const updateBuurt = [...selectedBuurt];
            updateBuurt[existingRecordIndex] = { id, option: values };
            setSelectedBuurt(updateBuurt);
        } else {
            // If not exists, add a new record
            setSelectedBuurt([...selectedBuurt, { id, option: values }]);
        }
        //Add update endpoint later
    };

    const createErrorReport = (params: GridPreProcessEditCellProps) => {
        const hasError = params.props.value.length < 3;
        return { ...params.props, error: hasError };
    };

    const columns: GridColDef[] = [
        {
            field: "projectName",
            headerName: t("projects.tableColumns.projectName"),
            editable: true,
            width: 120,
            filterOperators: getGridStringOperators().filter((o) => o.value === "contains"),
            renderCell: (cellValues: GridRenderCellParams<Project>) => {
                return [
                    <Stack direction="row" spacing={1} alignItems="center" onDoubleClick={() => navigate(`/projects/${cellValues.row.projectId}`)}>
                        <Box width="15px" height="15px" borderRadius="50%" sx={{ background: cellValues.row.projectColor }} />
                        <Typography fontSize={14}>{cellValues.row.projectName}</Typography>
                    </Stack>,
                ];
            },
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "totalValue",
            headerName: t("projects.tableColumns.totalValue"),
            editable: true,
            width: 120,
            filterable: false,
            preProcessEditCellProps: createErrorReport,
        },

        {
            field: "organizationName",
            headerName: t("projects.tableColumns.organizationName"),
            editable: true,
            width: 120,
            filterOperators: getGridStringOperators().filter((o) => o.value === "contains"),
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "confidentialityLevel",
            headerName: t("projects.tableColumns.confidentialityLevel"),
            valueOptions: confidentialityLevelOptions,
            type: "singleSelect",
            editable: true,
            width: 250,
            filterOperators: getGridSingleSelectOperators().filter((o) => o.value === "isAnyOf"),
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "startDate",
            headerName: t("projects.tableColumns.startDate"),
            editable: true,
            type: "dateTime",
            filterOperators: getGridStringOperators().filter((o) => o.value === "contains"),
            valueGetter: ({ value }) => value && new Date(value),
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "endDate",
            headerName: t("projects.tableColumns.endDate"),
            editable: true,
            type: "dateTime",
            filterOperators: getGridStringOperators().filter((o) => o.value === "contains"),
            valueGetter: ({ value }) => value && new Date(value),
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "planType",
            headerName: t("projects.tableColumns.planType"),
            width: 500,
            align: "center",
            valueOptions: planTypeOptions.map((pt) => pt.id),
            type: "singleSelect",
            renderCell: (cellValues: GridRenderCellParams<Project>) => {
                return [<PlanTypeCell cellValues={cellValues} selectedPlanTypes={selectedPlanTypes} handlePlanTypeChange={handlePlanTypeChange} />];
            },
            filterOperators: getGridStringOperators().filter((o) => o.value === "contains"),
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "priority",
            headerName: t("projects.tableColumns.priority"),
            editable: true,
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "municipalityRole",
            headerName: t("projects.tableColumns.municipalityRole"),
            editable: true,
            width: 320,
            renderCell: (cellValues: GridRenderCellParams<Project>) => {
                return [
                    <MunicipalityRoleCell
                        cellValues={cellValues}
                        selectedMunicipalityRole={selectedMunicipalityRole}
                        handleMunicipalityRoleChange={handleMunicipalityRoleChange}
                    />,
                ];
            },
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "projectPhase",
            headerName: t("projects.tableColumns.projectPhase"),
            editable: true,
            width: 140,
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "planningPlanStatus",
            headerName: t("projects.tableColumns.planningPlanStatus"),
            editable: true,
            width: 500,
            align: "center",
            preProcessEditCellProps: createErrorReport,
            renderCell: (cellValues: GridRenderCellParams<Project>) => {
                return [<PlanningPlanStatusCell cellValues={cellValues} selectedPlanStatus={selectedPlanStatus} handleStatusChange={handleStatusChange} />];
            },
        },
        {
            field: "municipality",
            headerName: t("projects.tableColumns.municipality"),
            editable: true,
            width: 320,
            renderCell: (cellValues: GridRenderCellParams<Project>) => {
                return [
                    <MunicipalityCell
                        cellValues={cellValues}
                        selectedMunicipality={selectedMunicipality}
                        handleMunicipalityChange={handleMunicipalityChange}
                    />,
                ];
            },
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "wijk",
            headerName: t("projects.tableColumns.wijk"),
            editable: true,
            width: 320,
            renderCell: (cellValues: GridRenderCellParams<Project>) => {
                return [<WijkCell cellValues={cellValues} selectedWijk={selectedWijk} handleWijkChange={handleWijkChange} />];
            },
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "buurt",
            headerName: t("projects.tableColumns.buurt"),
            editable: true,
            width: 320,
            renderCell: (cellValues: GridRenderCellParams<Project>) => {
                return [<BuurtCell cellValues={cellValues} selectedBuurt={selectedBuurt} handleBuurtChange={handleBuurtChange} />];
            },
            preProcessEditCellProps: createErrorReport,
        },
    ];

    const handleFilterModelChange = (newModel: GridFilterModel) => {
        if (newModel.items.length > 0) {
            const converFilterType = newModel.items[0].operator === "isAnyOf" ? "ANY_OF" : "CONTAINS";
            const filterValues = newModel.items[0].operator === "isAnyOf"? newModel.items[0].value?.map((v: string) => `filterValue=${v}`).join("&"):`filterValue=${newModel.items[0].value}`;

            if (converFilterType && filterValues!=="filterValue=undefined") {
                const query = `?pageNumber=${paginationInfo.page === 0 ? 1 : paginationInfo.page}&pageSize=${paginationInfo.pageSize}&filterColumn=${
                    newModel.items[0].field
                }&filterCondition=${converFilterType}&${filterValues}`;
                filterTable(query).then((res) => setProjects(res));
            }
            setFilterModel(newModel);
            filterValues!=="filterValue=undefined"&&  navigate(
                `/projects/table?pageNumber=${paginationInfo.page === 0 ? 1 : paginationInfo.page}&pageSize=${paginationInfo.pageSize}&filterColumn=${
                    newModel.items[0].field
                }&filterCondition=${converFilterType}&${filterValues}`
            );
        }
    };
    // TODO Update filter model when there is a search param in url
    console.log(filterModel)


    const isFilteredUrl = useCallback(() => {
        const queryParams = ["pageNumber", "pageSize", "filterColumn", "filterCondition", "filterValue"];
        return queryParams.every((e)=>location.search.includes(e));
    }, [location.search]);

    useEffect(() => {
        if (isFilteredUrl()) {
            filterTable(location.search).then((res) => setProjects(res));
        }
    }, [isFilteredUrl, location.search]);

    return (
        <Stack
            width="100%"
            sx={{
                margin: "0 auto",
                overflowX: "auto",
            }}
        >
            <DataGrid
                checkboxSelection={showCheckBox}
                rows={rows}
                columns={columns}
                pageSizeOptions={[5, 10, 25, 50, 100]}
                onPaginationModelChange={(model: GridPaginationModel, _: GridCallbackDetails) => {
                    if (model.page === 0) {
                        setPaginationInfo({ page: 1, pageSize: model.pageSize });
                    }
                    setPaginationInfo(model);
                }}
                paginationModel={paginationInfo}
                onRowClick={showCheckBox ? handleExport : () => {}}
                processRowUpdate={
                    (updatedRow, originalRow) => console.log(updatedRow)
                    //todo add update endpoint later
                }
                onFilterModelChange={handleFilterModelChange}
            />
            <Dialog open={showDialog} onClose={handleClose} aria-labelledby="alert-dialog-title" aria-describedby="alert-dialog-description">
                <DialogTitle id="alert-dialog-title">Weet je het zeker?</DialogTitle>
                <DialogActions sx={{ px: 5, py: 3, ml: 15 }}>
                    <Button onClick={handleClose}>Annuleer</Button>
                    <Button
                        variant="contained"
                        onClick={() => {
                            handleClose();
                            setAlert("Gelukt! Je ontvangt de bevestiging via de mail.", "success");
                        }}
                        autoFocus
                    >
                        Exporteer
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
                    Exporteren
                </Button>
            )}
        </Stack>
    );
};
