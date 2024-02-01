import {
    DataGrid,
    GridCellEditStopParams,
    GridCellEditStopReasons,
    GridColDef,
    GridPreProcessEditCellProps,
    GridRenderCellParams,
    MuiEvent,
} from "@mui/x-data-grid";
import { GridRowParams } from "@mui/x-data-grid";
import { useNavigate } from "react-router-dom";
import { useContext, useState } from "react";
import { Autocomplete, Box, Button, Chip, Dialog, DialogActions, DialogTitle, MenuItem, Select, Stack, TextField, Typography } from "@mui/material";
import useAlert from "../hooks/useAlert";
import ProjectContext from "../context/ProjectContext";
import { Project } from "../api/projectsServices";

interface RowData {
    id: number;
}

type Props = {
    showCheckBox?: boolean;
};
interface OptionType {
    id: string;
    title: string;
}

const confidentialityLevelOptions = ["PRIVE", "INTERN_UITVOERING", "INTERN_RAPPORTAGE", "EXTERN_RAPPORTAGE", "OPENBAAR"];
const planTypeOptions: OptionType[] = [
    { id: "PAND_TRANSFORMATIE", title: "PAND_TRANSFORMATIE" },
    { id: "TRANSFORMATIEGEBIED", title: "TRANSFORMATIEGEBIED" },
    { id: "HERSTRUCTURERING", title: "HERSTRUCTURERING" },
    { id: "VERDICHTING", title: "VERDICHTING" },
    { id: "UITBREIDING_UITLEG", title: "UITBREIDING_UITLEG" },
    { id: "UITBREIDING_OVERIG", title: "UITBREIDING_OVERIG" },
];

export const ProjectsTableView = ({ showCheckBox }: Props) => {
    const { projects } = useContext(ProjectContext);

    const rows = projects.map((p) => {
        return { ...p, id: p.projectId };
    });

    const navigate = useNavigate();
    const [selectedRows, setSelectedRows] = useState<any[]>([]);
    const [selectedPlanTypes, setSelectedPlanTypes] = useState<OptionType[]>([]);
    const [showDialog, setShowDialog] = useState(false);
    const { setAlert } = useAlert();


    const handleExport = (params: GridRowParams) => {
        const clickedRow: RowData = params.row as RowData;
        if (selectedRows.includes(clickedRow.id)) {
            setSelectedRows(selectedRows.filter((s) => s !== clickedRow.id));
        } else {
            setSelectedRows([...selectedRows, clickedRow.id]);
        }
    };

    const handleClose = () => setShowDialog(false);
    const handlePlanTypeChange = (_: React.ChangeEvent<{}>, values: OptionType[]) => {
        setSelectedPlanTypes(values);
        //Add later update endpoint
    };
    const createErrorReport = (params: GridPreProcessEditCellProps) => {
        const hasError = params.props.value.length < 3;
        return { ...params.props, error: hasError };
    };
    const columns: GridColDef[] = [
        {
            field: "projectName",
            headerName: "projectName",
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
            field: "organizationName",
            headerName: "organizationName",
            editable: true,
            width: 120,
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "startDate",
            headerName: "startDate",
            editable: true,
            type: "dateTime",
            valueGetter: ({ value }) => value && new Date(value),
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "endDate",
            headerName: "endDate",
            editable: true,
            type: "dateTime",
            valueGetter: ({ value }) => value && new Date(value),
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "priority",
            headerName: "priority",
            editable: true,
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "confidentialityLevel",
            headerName: "confidentialityLevel",
            valueOptions: confidentialityLevelOptions,
            type: "singleSelect",
            editable: true,
            width: 250,
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "planType",
            headerName: "planType",
            // editable: true,
            width: 500,
            align: "center",

            renderCell: (cellValues: GridRenderCellParams<Project>) => {
                const defaultPlanTypes = cellValues.row.planType.map((c) => ({ id: c, title: c }));
                return [
                    <Stack direction="row" spacing={1}>
                        <Autocomplete
                            size="small"
                            multiple
                            limitTags={2}
                            id="multiple-limit-tags"
                            options={planTypeOptions}
                            getOptionLabel={(option) => option.title}
                            isOptionEqualToValue={(option, value) => option.id === value.id}
                            value={selectedPlanTypes.length > 1 ? selectedPlanTypes : defaultPlanTypes}
                            renderInput={(params) => <TextField {...params} label="Plan Type" placeholder="select a plan type" />}
                            sx={{ width: "500px" }}
                            onChange={handlePlanTypeChange}
                        />
                    </Stack>,
                ];
            },
            preProcessEditCellProps: createErrorReport,
        },

        {
            field: "municipalityRole",
            headerName: "municipalityRole",
            editable: true,
            width: 200,
            renderCell: (cellValues: GridRenderCellParams<Project>) => {
                return [
                    <Stack direction="row" spacing={1}>
                        {cellValues.row.municipalityRole.map((pt: string) => {
                            return <Chip key={pt} size="small" label={pt} color="primary" />;
                        })}
                    </Stack>,
                ];
            },
            preProcessEditCellProps: createErrorReport,
        },

        {
            field: "projectPhase",
            headerName: "projectPhase",
            editable: true,
            width: 140,
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "planningPlanStatus",
            headerName: "planningPlanStatus",
            editable: true,
            width: 300,
            align: "center",
            preProcessEditCellProps: createErrorReport,
            renderCell: (cellValues: GridRenderCellParams<Project>) => {
                return [
                    <Stack direction="row" spacing={1}>
                        {cellValues.row.planningPlanStatus.map((pt: string) => {
                            return <Chip key={pt} size="small" label={pt} color="primary" />;
                        })}
                    </Stack>,
                ];
            },
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
