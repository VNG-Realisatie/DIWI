import { DataGrid, GridColDef } from "@mui/x-data-grid";
import { projects } from "../api/dummyData";
import { Typography } from "@mui/material";
import { ImportProjectCardItem } from "../components/ImportProjectCardItem";

const projectColumns: GridColDef[] = [
    { field: "id", headerName: "ID", width: 90 },
    {
        field: "name",
        headerName: "Project name",
        width: 200,
        editable: true,
    },
    {
        field: "geo",
        headerName: "Geography",
        width: 200,
        editable: true,
    },
    {
        field: "organization",
        headerName: "Organisatie",
        width: 200,
        editable: true,
    },
    {
        field: "color",
        headerName: "Color",
        width: 200,
        editable: true,
    },
];
const houseBlockColumns: GridColDef[] = [
    { field: "id", headerName: "ID", width: 90 },
    {
        field: "name",
        headerName: "Huizen Groep Naam",
        width: 140,
        editable: false,
    },
    {
        field: "stardate",
        headerName: "Startdatum",
        width: 100,
        editable: false,
    },
    {
        field: "finishDate",
        headerName: "Einddatum",
        width: 100,
        editable: false,
    },
    {
        field: "mutationType",
        headerName: "Mutatiesoort",
        width: 100,
        editable: false,
    },
    {
        field: "grossPlanCapacity",
        headerName: "Bruto plancapaciteit",
        width: 150,
        editable: false,
    },
    {
        field: "demolition",
        headerName: "Sloop",
        width: 100,
        editable: false,
    },
    {
        field: "netPlanCapacity",
        headerName: "Netto Plancapaciteit",
        width: 100,
        editable: false,
    },
    {
        field: "size",
        headerName: "Grootte",
        width: 100,
        editable: false,
    },
    {
        field: "ownerOccupied",
        headerName: "Koopwoning",
        width: 100,
        editable: false,
    },
    {
        field: "rentalPrivateHouse",
        headerName: "Huurwoning Particuliere verhuurder",
        width: 200,
        editable: false,
    },
    {
        field: "rentalHousingCompany",
        headerName: "Huurwoning Woningcorporatie",
        width: 200,
        editable: false,
    },
    {
        field: "price",
        headerName: "Waarde",
        width: 100,
        editable: false,
    },
    {
        field: "rentingPrice",
        headerName: "Huurbedrag",
        width: 100,
        editable: false,
    },
    {
        field: "houseType",
        headerName: "Eengezinswoning",
        width: 150,
        editable: false,
    },
];
export const ImportedProjects = () => {
    const importedDummyProjects = projects.filter((a, i) => i < 2);
    const dummyHouseBlocks = importedDummyProjects
        .map((a) => a.houseblocks)
        .flat(1);
    console.log(importedDummyProjects);
    console.log(dummyHouseBlocks.flat(1));
    return (
        <>
            <Typography fontSize="20px" fontWeight="600" sx={{ mt: 2 }}>
                Importeren vanuit Excel
            </Typography>
           {importedDummyProjects.map(p=> {
            return <ImportProjectCardItem project={p}/>
           })}
        </>
    );
};
