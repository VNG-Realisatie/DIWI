import { DataGrid, GridColDef, GridRenderCellParams } from "@mui/x-data-grid";
import { GridRowParams } from "@mui/x-data-grid";
import { useNavigate } from "react-router-dom";
import { useContext, useState } from "react";
import { Box, Button, Chip, Dialog, DialogActions, DialogTitle, MenuItem, Select, Stack, Typography } from "@mui/material";
import useAlert from "../hooks/useAlert";
import ProjectContext from "../context/ProjectContext";
import { Project } from "../api/projectsServices";
//Todo Implement filterDataWithSelectedColumns here to get columns dynamic.
const confidentialityLevelOptions = ["prive", "intern_uitvoering", "intern_rapportage", "extern_rapportage", "openbaar"];
const planTypeOptions = ["pand_transformatie", "transformatiegebied", "herstructurering", "verdichting", "uitbreiding_uitleg", "uitbreiding_overig"];

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
    const[planTypeEdit,setPlanTypeEdit]=useState(false);
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
            field: "organizationName",
            headerName: "organizationName",
            editable: true,
            width: 120,
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
            width: 200,
            renderCell: (cellValues: GridRenderCellParams<Project>) => {
                return [
                    <Stack direction="row" spacing={1}>
                        <Select
                            id="confidentialityLevel"
                            value={cellValues.row.confidentialityLevel}
                            onChange={() => console.log("TODO Later after endpoint")}
                            size="small"
                        >
                            {confidentialityLevelOptions.map((c) => {
                                return (
                                    <MenuItem key={c} value={c}>
                                        {c}
                                    </MenuItem>
                                );
                            })}
                        </Select>
                    </Stack>,
                ];
            },
        },
        {
            field: "planType",
            headerName: "planType",
            // editable: true,
            width: 400,
            align: "center",

            renderCell: (cellValues: GridRenderCellParams<Project>) => {
                return [
                    <Stack direction="row" spacing={1} >
                        {!planTypeEdit&&cellValues.row.planType.map((pt: string) => {
                            return <Chip size="small" label={pt} color="primary"  onClick={()=>setPlanTypeEdit(true)}/>;
                        })}
                        {planTypeEdit&&<>Edit me </>}
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
                onRowClick={showCheckBox ? handleExport : () => {}}
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
