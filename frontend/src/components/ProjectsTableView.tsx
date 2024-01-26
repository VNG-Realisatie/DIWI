import { DataGrid, GridColDef, GridRenderCellParams } from "@mui/x-data-grid";
import { GridRowParams } from "@mui/x-data-grid";
import { useNavigate } from "react-router-dom";
import { useContext, useState } from "react";
import { Box, Button, Chip, Dialog, DialogActions, DialogTitle, Stack, Typography } from "@mui/material";
import useAlert from "../hooks/useAlert";
import ProjectContext from "../context/ProjectContext";
import { Project } from "../api/projectsServices";
//Todo Implement filterDataWithSelectedColumns here to get columns dynamic.
const columns: GridColDef[] = [
    {
        field: "projectName",
        headerName: "projectName",
        editable: true,
        width: 120,
        renderCell: (cellValues: GridRenderCellParams<Project>) => {
            return [
                <Stack direction="row" spacing={1} alignItems="center">
                    <Box width="15px" height="15px" borderRadius="50%" sx={{ background: cellValues.row.projectColor }} />
                    <Typography fontSize={14}>{cellValues.row.projectName}</Typography>
                </Stack>,
            ];
        },
    },
    {
        field: "startDate",
        headerName: "startDate",
        editable: true,
    },
    {
        field: "endDate",
        headerName: "endDate",
        editable: true,
    },
    {
        field: "priority",
        headerName: "priority",
        editable: true,
    },
    {
        field: "confidentialityLevel",
        headerName: "confidentialityLevel",
        editable: true,
        width: 120,
    },
    {
        field: "planType",
        headerName: "planType",
        editable: true,
        width: 400,
        align: "center",

        renderCell: (cellValues: GridRenderCellParams<Project>) => {
            return [
                <Stack direction="row" spacing={1}>
                    {cellValues.row.planType.map((pt: string) => {
                        return <Chip size="small" label={pt} color="primary" />;
                    })}
                </Stack>,
            ];
        },
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
                        return <Chip size="small" label={pt} color="primary" />;
                    })}
                </Stack>,
            ];
        },
    },

    {
        field: "organizationName",
        headerName: "organizationName",
        editable: true,
        width: 120,
    },
    {
        field: "projectPhase",
        headerName: "projectPhase",
        editable: true,
        width: 140,
    },
    {
        field: "planningPlanStatus",
        headerName: "planningPlanStatus",
        editable: true,
        width: 300,
        align: "center",

        renderCell: (cellValues: GridRenderCellParams<Project>) => {
            return [
                <Stack direction="row" spacing={1}>
                    {cellValues.row.planningPlanStatus.map((pt: string) => {
                        return <Chip size="small" label={pt} color="primary" />;
                    })}
                </Stack>,
            ];
        },
    },
];

interface RowData {
    id: number;
}

type Props = {
    showCheckBox?: boolean;
};

export const ProjectsTableView = ({ showCheckBox }: Props) => {
    const { projects } = useContext(ProjectContext);

    const rows = projects.map((p) => {
        return { ...p, id: p.projectId };
    });

    const navigate = useNavigate();
    const [selectedRows, setSelectedRows] = useState<any[]>([]);
    const [showDialog, setShowDialog] = useState(false);
    const { setAlert } = useAlert();

    const handleRowClick = (params: GridRowParams) => {
        const clickedRow: RowData = params.row as RowData;
        navigate(`/projects/${clickedRow.id}`);
    };

    const handleExport = (params: GridRowParams) => {
        const clickedRow: RowData = params.row as RowData;
        if (selectedRows.includes(clickedRow.id)) {
            setSelectedRows(selectedRows.filter((s) => s !== clickedRow.id));
        } else {
            setSelectedRows([...selectedRows, clickedRow.id]);
        }
    };

    const handleClose = () => setShowDialog(false);

    return (
        <Stack
            width="100%"
            sx={{
                maxWidth: showCheckBox ? "100%" : "920px", // Adjust this width as needed
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
                onRowClick={showCheckBox ? handleExport : handleRowClick}
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
