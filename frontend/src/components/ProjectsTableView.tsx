import { DataGrid, GridColDef } from "@mui/x-data-grid";
import { GridRowParams } from "@mui/x-data-grid";
// import { projects } from "../api/dummyData";
import { useNavigate } from "react-router-dom";
import { useContext, useState } from "react";
import {
    Button,
    Dialog,
    DialogActions,
    DialogTitle,
    Stack,
} from "@mui/material";
import useAlert from "../hooks/useAlert";
import ProjectContext from "../context/ProjectContext";
//Todo Implement filterDataWithSelectedColumns here to get columns dynamic.
const columns: GridColDef[] = [
    { field: "id", headerName: "ID", width: 20 },
    {
        field: "name",
        headerName: "Name",
        editable: true,
        width: 120
    },
    {
        field: "eigenaar",
        headerName: "Eigenaar",
        editable: true,
        width: 120
    },
    {
        field: "plan type",
        headerName: "Plan Type",
        editable: true,
        width: 120
    },
    {
        field: "start datum",
        headerName: "Start Datum",
        editable: true,
    },
    {
        field: "eind datum",
        headerName: "Eind Datum",
        editable: true,
    },
    {
        field: "priorisering",
        headerName: "Priorisering",
        editable: true,
    },
    {
        field: "project fase",
        headerName: "Project Fase",
        editable: true,
        width: 140
    },
    {
        field: "rol gemeente",
        headerName: "Rol Gemeente",
        editable: true,
        width: 120
    },
    {
        field: "programmering",
        headerName: "Programmering",
        editable: true,
    },
    {
        field: "project leider",
        headerName: "Project Leider",
        editable: true,
        width: 120
    },
    {
        field: "vertrouwlijkheidsniveau",
        headerName: "Vertrouwlijkheidsniveau",
        editable: true,
        width: 140
    },
    {
        field: "planologische plan status",
        headerName: "Planologische Plan Status",
        editable: true,
        width: 180
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
    const rows = projects.map((p) => p.project);

    const navigate = useNavigate();
    const [selectedRows, setSelectedRows] = useState<any[]>([]);
    const [showDialog, setShowDialog] = useState(false);
    const { setAlert } = useAlert();
    const handleRowClick = (params: GridRowParams) => {
        const clickedRow: RowData = params.row as RowData;
        navigate(`/projects/${clickedRow.id}`);
    };
    const handleExport = (params: any) => {
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
                maxWidth: showCheckBox?"100%":"920px", // Adjust this width as needed
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
            <Dialog
                open={showDialog}
                onClose={handleClose}
                aria-labelledby="alert-dialog-title"
                aria-describedby="alert-dialog-description"
            >
                <DialogTitle id="alert-dialog-title">
                    Weet je het zeker?
                </DialogTitle>

                <DialogActions sx={{ px: 5, py: 3, ml: 15 }}>
                    <Button onClick={handleClose}>Annuleer</Button>
                    <Button
                        variant="contained"
                        onClick={() => {
                            handleClose();
                            setAlert(
                                "Gelukt! Je ontvangt de bevestiging via de mail.",
                                "success"
                            );
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
