import { DataGrid, GridColDef, GridPreProcessEditCellProps, GridRenderCellParams } from "@mui/x-data-grid";
import { GridRowParams } from "@mui/x-data-grid";
import { useNavigate } from "react-router-dom";
import { useContext, useState } from "react";
import { Box, Button, Dialog, DialogActions, DialogTitle, Stack, Typography } from "@mui/material";
import useAlert from "../hooks/useAlert";
import ProjectContext from "../context/ProjectContext";
import { Project } from "../api/projectsServices";
import { MultiSelect } from "./table/MultiSelect";

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
const municipalityRolesOptions: OptionType[] = [
    { id: "ACTIVE", title: "ACTIVE" },
    { id: "PASSIVE", title: "PASSIVE" },
    { id: "NOTHING", title: "NOTHING" },
];
const planningPlanStatus = [
    { id: "_1A_ONHERROEPELIJK", title: "_1A_ONHERROEPELIJK" },
    { id: "_1B_ONHERROEPELIJK_MET_UITWERKING_NODIG", title: "_1B_ONHERROEPELIJK_MET_UITWERKING_NODIG" },
    { id: "_1C_ONHERROEPELIJK_MET_BW_NODIG", title: "_1C_ONHERROEPELIJK_MET_BW_NODIG" },
    { id: "_2A_VASTGESTELD", title: "_2A_VASTGESTELD" },
    { id: "_2B_VASTGESTELD_MET_UITWERKING_NODIG", title: "_2B_VASTGESTELD_MET_UITWERKING_NODIG" },
    { id: "_2C_VASTGESTELD_MET_BW_NODIG", title: "_2C_VASTGESTELD_MET_BW_NODIG" },
    { id: "_3_IN_VOORBEREIDING", title: "_3_IN_VOORBEREIDING" },
    { id: "_4A_OPGENOMEN_IN_VISIE", title: "_4A_OPGENOMEN_IN_VISIE" },
    { id: "_4B_NIET_OPGENOMEN_IN_VISIE", title: "_4B_NIET_OPGENOMEN_IN_VISIE" },
];
const wijkOptions=[
    {id:"Centrum",title:"Centrum"},
    {id:"Castricum-Noord",title:"Castricum-Noord"},
    {id:"Castricum-Oost",title:"Castricum-Oost"},
    {id:"Castricum-Zuid",title:"Castricum-Zuid"},
    {id:"Bakkum",title:"Bakkum"},
    {id:"Akersloot",title:"Akersloot"},
    {id:"De Woude",title:"De Woude"},
    {id:"Limmen",title:"Limmen"}
]
const buurtOptions=[
    {id:"Centrum-Noord",title:"Centrum-Noord"},
    {id:"Centrum-Zuid",title:"Centrum-Zuid"},
    {id:"Oranjebuurt",title:"Oranjebuurt"},
    {id:"Kooiweg",title:"Kooiweg"},
    {id:"Noord-End",title:"Noord-End"},
    {id:"Albert’s Hoeve",title:"Albert’s Hoeve"},
    {id:"Beverwijkerstraat",title:"Beverwijkerstraat"},
    {id:"Buitengebied",title:"Buitengebied"},
    {id:"Bakkum-Noord",title:"Bakkum-Noord"}
]

export const ProjectsTableView = ({ showCheckBox }: Props) => {
    const { projects } = useContext(ProjectContext);

    const rows = projects.map((p) => {
        return { ...p, id: p.projectId };
    });

    const navigate = useNavigate();
    const [selectedRows, setSelectedRows] = useState<any[]>([]);
    const [selectedPlanTypes, setSelectedPlanTypes] = useState<OptionType[]>([]);
    const [selectedMunicipality, setSelectedMunicipality] = useState<OptionType[]>([]);
    const [selectedPlanStatus, setSelectedPlanStatus] = useState<OptionType[]>([]);
    const [selectedWijk, setSelectedWijk] = useState<OptionType[]>([]);
    const [selectedBuurt, setSelectedBuurt] = useState<OptionType[]>([]);
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
    const handleMunicipalityChange = (_: React.ChangeEvent<{}>, values: OptionType[]) => {
        setSelectedMunicipality(values);
    };
    const handleStatusChange = (_: React.ChangeEvent<{}>, values: OptionType[]) => {
        setSelectedPlanStatus(values);
    };
    const handleWijkChange = (_: React.ChangeEvent<{}>, values: OptionType[]) => {
        setSelectedWijk(values);
    };
    const handleBuurtChange = (_: React.ChangeEvent<{}>, values: OptionType[]) => {
        setSelectedBuurt(values);
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
            field: "totalValue",
            headerName: "totalValue",
            editable: true,
            width: 120,
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
            field: "confidentialityLevel",
            headerName: "confidentialityLevel",
            valueOptions: confidentialityLevelOptions,
            type: "singleSelect",
            editable: true,
            width: 250,
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
            field: "planType",
            headerName: "planType",
            // editable: true,
            width: 500,
            align: "center",

            renderCell: (cellValues: GridRenderCellParams<Project>) => {
                const defaultPlanTypes = cellValues.row.planType.map((c) => ({ id: c, title: c }));
                return [
                    <MultiSelect
                        currentRow={cellValues.row}
                        selected={selectedPlanTypes}
                        options={planTypeOptions}
                        tagLimit={2}
                        defaultOptionValues={defaultPlanTypes}
                        inputLabel="Plan Type"
                        placeHolder="Select type"
                        handleChange={handlePlanTypeChange}
                        width="500px"
                    />,
                ];
            },
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "priority",
            headerName: "priority",
            editable: true,
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "municipalityRole",
            headerName: "municipalityRole",
            editable: true,
            width: 320,
            renderCell: (cellValues: GridRenderCellParams<Project>) => {
                const defaultPlanTypes = cellValues.row.municipalityRole.map((c) => ({ id: c, title: c }));
                return [
                    <MultiSelect
                        currentRow={cellValues.row}
                        selected={selectedMunicipality}
                        options={municipalityRolesOptions}
                        tagLimit={2}
                        defaultOptionValues={defaultPlanTypes}
                        inputLabel="Municipality Roles"
                        placeHolder="Select role"
                        handleChange={handleMunicipalityChange}
                        width="300px"
                    />,
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
            width: 500,
            align: "center",
            preProcessEditCellProps: createErrorReport,
            renderCell: (cellValues: GridRenderCellParams<Project>) => {
                const defaultPlanTypes = cellValues.row.planningPlanStatus.map((c) => ({ id: c, title: c }));
                return [
                    <MultiSelect
                        currentRow={cellValues.row}
                        selected={selectedPlanStatus}
                        options={planningPlanStatus}
                        tagLimit={2}
                        defaultOptionValues={defaultPlanTypes}
                        inputLabel="PlanningPlan Status"
                        placeHolder="Select status"
                        handleChange={handleStatusChange}
                        width="500px"
                    />,
                ];
            },
        },
        {
            field: "municipality",
            headerName: "municipality",
            editable: true,
            width: 140,
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "wijk",
            headerName: "wijk",
            editable: true,
            width: 320,
            renderCell: (cellValues: GridRenderCellParams<Project>) => {
                const defaultPlanTypes = cellValues.row.wijk?.map((c) => ({ id: c, title: c }));
                return [
                    <MultiSelect
                        currentRow={cellValues.row}
                        selected={selectedWijk}
                        options={wijkOptions}
                        tagLimit={2}
                        defaultOptionValues={defaultPlanTypes}
                        inputLabel="Wijk"
                        placeHolder="Select wijk"
                        handleChange={handleWijkChange}
                        width="300px"
                    />,
                ];
            },
            preProcessEditCellProps: createErrorReport,
        },
        {
            field: "buurt",
            headerName: "buurt",
            editable: true,
            width: 320,
            renderCell: (cellValues: GridRenderCellParams<Project>) => {
                const defaultPlanTypes = cellValues.row.buurt?.map((c) => ({ id: c, title: c }));
                return [
                    <MultiSelect
                        currentRow={cellValues.row}
                        selected={selectedBuurt}
                        options={buurtOptions}
                        tagLimit={2}
                        defaultOptionValues={defaultPlanTypes}
                        inputLabel="Buurt"
                        placeHolder="Select buurt"
                        handleChange={handleBuurtChange}
                        width="300px"
                    />,
                ];
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
