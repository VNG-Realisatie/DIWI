import { DataGrid, GridColDef, GridPreProcessEditCellProps, GridRenderCellParams } from "@mui/x-data-grid";
import { GridRowParams } from "@mui/x-data-grid";
import { useNavigate } from "react-router-dom";
import { useContext, useState } from "react";
import { Box, Button, Dialog, DialogActions, DialogTitle, Stack, Typography } from "@mui/material";
import useAlert from "../hooks/useAlert";
import ProjectContext from "../context/ProjectContext";
import { Project } from "../api/projectsServices";
import { useTranslation } from "react-i18next";
import { PlanTypeCell } from "./table/PlanTypeCell";
import { MunicipalityRoleCell } from "./table/MunicipalityRoleCell";
import { PlanningPlanStatusCell } from "./table/PlanningPlanStatusCell";
import { WijkCell } from "./table/WijkCell";
import { BuurtCell } from "./table/BuurtCell";

interface RowData {
    id: number;
}

type Props = {
    showCheckBox?: boolean;
};

export interface OptionType {
    id: string;
    title: string;
}
export type SelectedOptionWithId = {
    id: string;
    option: OptionType[];
};

const confidentialityLevelOptions = ["PRIVE", "INTERN_UITVOERING", "INTERN_RAPPORTAGE", "EXTERN_RAPPORTAGE", "OPENBAAR"];

export const ProjectsTableView = ({ showCheckBox }: Props) => {
    const { projects } = useContext(ProjectContext);

    const rows = projects.map((p) => {
        return { ...p, id: p.projectId };
    });

    const navigate = useNavigate();
    const { setAlert } = useAlert();
    const { t } = useTranslation();
    const [selectedRows, setSelectedRows] = useState<any[]>([]);
    const [selectedPlanTypes, setSelectedPlanTypes] = useState<SelectedOptionWithId[]>([]);
    const [selectedMunicipality, setSelectedMunicipality] = useState<SelectedOptionWithId[]>([]);
    const [selectedPlanStatus, setSelectedPlanStatus] = useState<SelectedOptionWithId[]>([]);
    const [selectedWijk, setSelectedWijk] = useState<SelectedOptionWithId[]>([]);
    const [selectedBuurt, setSelectedBuurt] = useState<SelectedOptionWithId[]>([]);
    const [showDialog, setShowDialog] = useState(false);

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

    const handleMunicipalityChange = (_: React.ChangeEvent<{}>, values: OptionType[], id: string) => {
        const existingRecordIndex = selectedMunicipality.findIndex((item) => item.id === id);

        if (existingRecordIndex !== -1) {
            const updatedMunicipality = [...selectedMunicipality];
            updatedMunicipality[existingRecordIndex] = { id, option: values };
            setSelectedMunicipality(updatedMunicipality);
        } else {
            // If not exists, add a new record
            setSelectedMunicipality([...selectedMunicipality, { id, option: values }]);
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
            preProcessEditCellProps: createErrorReport,
        },

        {
            field: "organizationName",
            headerName: t("projects.tableColumns.organizationName"),
            editable: true,
            width: 120,
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "confidentialityLevel",
            headerName: t("projects.tableColumns.confidentialityLevel"),
            valueOptions: confidentialityLevelOptions,
            type: "singleSelect",
            editable: true,
            width: 250,
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "startDate",
            headerName: t("projects.tableColumns.startDate"),
            editable: true,
            type: "dateTime",
            valueGetter: ({ value }) => value && new Date(value),
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "endDate",
            headerName: t("projects.tableColumns.endDate"),
            editable: true,
            type: "dateTime",
            valueGetter: ({ value }) => value && new Date(value),
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "planType",
            headerName: t("projects.tableColumns.planType"),
            width: 500,
            align: "center",

            renderCell: (cellValues: GridRenderCellParams<Project>) => {
                return [<PlanTypeCell cellValues={cellValues} selectedPlanTypes={selectedPlanTypes} handlePlanTypeChange={handlePlanTypeChange} />];
            },
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
                        selectedMunicipality={selectedMunicipality}
                        handleMunicipalityChange={handleMunicipalityChange}
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
            width: 140,
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
                initialState={{
                    pagination: {
                        paginationModel: {
                            pageSize: 10,
                        },
                    },
                }}
                pageSizeOptions={[5]}
                onRowClick={showCheckBox ? handleExport : () => {}}
                processRowUpdate={
                    (updatedRow, originalRow) => console.log(updatedRow)
                    //todo add update endpoint later
                }
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
