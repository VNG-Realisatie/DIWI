import { GridColDef } from "@mui/x-data-grid";

export type SearchItem = { id: number; name: string } | null;
export const colorArray = [
    "#FF5733",
    "#FFC300",
    "#36DBCA",
    "#5E35B1",
    "#F4511E",
    "#8E24AA",
    "#D81B60",
    "#3949AB",
    "#039BE5",
    "#00ACC1",
    "#43A047",
    "#C0CA33",
    "#FB8C00",
    "#F06292",
    "#8BC34A",
    "#03A9F4",
    "#673AB7",
    "#FF5252",
    "#009688",
    "#FFD600",
    "#607D8B",
    "#FBC02D",
    "#795548",
    "#9C27B0",
    "#4CAF50",
];
export const projects = [
    {
        id: 1,
        name: "Gemeentelijke ontwikkeling 1",
        color: colorArray[1],
        eigenaar: "projectleider_1",
        "plan type": "herstructurering",
        "eind datum": "2030-01-01",
        "start datum": "2022-01-01",
        priorisering: null,
        "project fase": "2_projectfase",
        "rol gemeente": "initiatiefnemer",
        programmering: true,
        "project leider": "projectleider_1",
        organization_id: 7,
        project_state_id: 1,
        organization_state_id: 7,
        vertrouwlijkheidsniveau: "extern_rapportage",
        "planologische plan status": "4a_opgenomen_in_visie",
        houseblocks: [
            {
                id: 1,
                name: "Huizenblok-1",
                startdate: "10/02/2023",
                finishDate: "10/04/2030",
                mutationType: "bouw",
                grossPlanCapacity: 100,
                demolition: 20,
                netPlanCapacity: 80,
                size: 80,
                ownerOccupied: 3,
                rentalPrivateHouse: 4,
                rentalHousingCompany: 3,
                price: 300000,
                rentingPrice: 600,
                houseType: "Single Family House",
            },
            {
                id: 1,
                name: "Huizenblok-2",
                startdate: "10/02/2023",
                finishDate: "02/01/2035",
                mutationType: "bouw",
                grossPlanCapacity: 25,
                demolition: 0,
                netPlanCapacity: 60,
                size: 50,
                ownerOccupied: 2,
                rentalPrivateHouse: 1,
                rentalHousingCompany: 1,
                price: 275000,
                rentingPrice: 490,
                houseType: "Single Family House",
            },
        ],
    },
    {
        id: 2,
        color: colorArray[2],
        name: "Gemeentelijke ontwikkeling 2",
        eigenaar: "projectleider_2",
        "plan type": "verdichting",
        "eind datum": "2030-07-01",
        "start datum": "2022-07-01",
        priorisering: null,
        "project fase": "2_projectfase",
        "rol gemeente": "initiatiefnemer",
        programmering: true,
        "project leider": "projectleider_2",
        organization_id: 8,
        project_state_id: 2,
        organization_state_id: 8,
        vertrouwlijkheidsniveau: "extern_rapportage",
        "planologische plan status": "4a_opgenomen_in_visie",
        houseblocks: [
            {
                id: 2,
                name: "Huizenblok-1",
                startdate: "01/12/2023",
                finishDate: "10/04/2030",
                mutationType: "sloop",
                grossPlanCapacity: 100,
                demolition: 20,
                netPlanCapacity: 80,
                size: 57,
                ownerOccupied: 2,
                rentalPrivateHouse: 5,
                rentalHousingCompany: 2,
                price: 200000,
                rentingPrice: 570,
                houseType: "Single Family House",
            },
        ],
    },
    {
        id: 3,
        color: colorArray[3],
        name: "Gemeentelijke ontwikkeling 3",
        eigenaar: "projectleider_1",
        "plan type": "herstructurering",
        "eind datum": "2024-07-01",
        "start datum": "2020-01-01",
        priorisering: null,
        "project fase": "4_realisatiefase",
        "rol gemeente": "initiatiefnemer",
        programmering: true,
        "project leider": "projectleider_1",
        organization_id: 7,
        project_state_id: 4,
        organization_state_id: 7,
        vertrouwlijkheidsniveau: "openbaar",
        "planologische plan status": "1a_onherroepelijk",
        houseblocks: [
            {
                id: 3,
                name: "Huizenblok-1",
                startdate: "01/12/2023",
                finishDate: "10/04/2030",
                mutationType: "sloop",
                grossPlanCapacity: 100,
                demolition: 20,
                netPlanCapacity: 80,
                size: 57,
                ownerOccupied: 2,
                rentalPrivateHouse: 5,
                rentalHousingCompany: 2,
                price: 200000,
                rentingPrice: 570,
                houseType: "Single Family House",
            },
        ],
    },
];
export type HouseBlockItem = { id: number; name: string };
export const houseBlocks = [
    { id: 1, name: "Huizenblok-1" },
    { id: 2, name: "Huizenblok-2" },
    { id: 3, name: "Huizenblok-3" },
    { id: 4, name: "Huizenblok-4" },
];
export type StatusType = { id: number; status: string };
export const statuses = [
    { id: 1, status: "Milestone" },
    { id: 2, status: "Document" },
];
export type Policy = {
    policy: {
        id: number;
        name: string;
        data: {
            characteristic: string;
            goal: number | string;
            time: string;
            geo: string;
            category: string;
            doel_richting?: string;
        };
    };
};
export const policyGoals = [
    {
        id: 1,
        name: "Totaal woningen gerealiseerd",
        data: {
            characteristic: "Totaal woningen",
            goal: 500,
            time: "01/01/2025",
            geo: "Voorne aan Zee",
            category: "Regionale doelstelling",
            doel_ricting: "minimaal",
        },
    },
    {
        id: 2,
        name: "Minimaal percentage ouderen woningen",
        data: {
            characteristic: "Doelgroep: Ouderen",
            goal: "Minimaal 35%",
            time: "01/01/2025",
            geo: "NVT",
            category: "Doelstelling van het Rijk",
            doel_ricting: "minimaal",
        },
    },
    {
        id: 3,
        name: "Koopwoningen in Voorne aan Zee",
        data: {
            characteristic: "Koopwoning",
            goal: 100,
            time: "01/01/2030",
            geo: "Voorne aan Zee",
            category: "Doelstelling Gemeente",
            doel_ricting: "minimaal",
        },
    },
];
export const projectColumns: GridColDef[] = [
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
export const houseBlockColumns: GridColDef[] = [
    { field: "id", headerName: "ID", width: 90 },
    {
        field: "name",
        headerName: "Huizen Groep Naam",
        width: 140,
        editable: false,
    },
    {
        field: "startdate",
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
export const projectLead = ["Ali", "Dirk", "Emiel", "Laurens"];
export const vertrouwlijkheidsniveau = ["Prive", "Intern voor uitvoering", "Intern rapportage", "Extern rapportage", "Openbaar"];
