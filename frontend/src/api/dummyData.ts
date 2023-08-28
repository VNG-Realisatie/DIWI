export type SearchItem = { id: number; name: string; color: string } | null;
export const projects = [
    {
        id: 1,
        name: "Project One",
        color: "#FF5733",
        geo: "geo",
        organization: "organisatie",
        houseblocks: [
            {
                id: 1,
                name: "Huizenblok-1",
                stardate: "10/02/2023",
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
                stardate: "10/02/2023",
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
        name: "Project Two",
        color: "#E91E63",
        geo: "geo",
        organization: "organisatie",
        houseblocks: [
            {
                id: 2,
                name: "Huizenblok-1",
                stardate: "01/12/2023",
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
        name: "Project Three",
        color: "#9C27B0",
        geo: "geo",
        organization: "organisatie",
    },
    {
        id: 4,
        name: "Project Four",
        color: "#673AB7",
        geo: "geo",
        organization: "organisatie",
    },
    {
        id: 5,
        name: "Project Five",
        color: "#3F51B5",
        geo: "geo",
        organization: "organisatie",
    },
    {
        id: 6,
        name: "Project Six",
        color: "#2196F3",
        geo: "geo",
        organization: "organisatie",
    },
    {
        id: 7,
        name: "Project Seven",
        color: "#00BCD4",
        geo: "geo",
        organization: "organisatie",
    },
    {
        id: 8,
        name: "Project Eight",
        color: "#009688",
        geo: "geo",
        organization: "organisatie",
    },
    {
        id: 9,
        name: "Project Nine",
        color: "#4CAF50",
        geo: "geo",
        organization: "organisatie",
    },
    {
        id: 10,
        name: "Project Ten",
        color: "#CDDC39",
        geo: "geo",
        organization: "organisatie",
    },
    {
        id: 11,
        name: "Project Eleven",
        color: "#FFC107",
        geo: "geo",
        organization: "organisatie",
    },
    {
        id: 12,
        name: "Project Twelve",
        color: "#FF9800",
        geo: "geo",
        organization: "organisatie",
    },
    {
        id: 13,
        name: "Project Thirteen",
        color: "#FF5722",
        geo: "geo",
        organization: "organisatie",
    },
    {
        id: 14,
        name: "Project Fourteen",
        color: "#795548",
        geo: "geo",
        organization: "organisatie",
    },
    {
        id: 15,
        name: "Project Fifteen",
        color: "#607D8B",
        geo: "geo",
        organization: "organisatie",
    },
    {
        id: 16,
        name: "Project Sixteen",
        color: "#9E9E9E",
        geo: "geo",
        organization: "organisatie",
    },
    {
        id: 17,
        name: "Project Seventeen",
        color: "#F44336",
        geo: "geo",
        organization: "organisatie",
    },
    {
        id: 18,
        name: "Project Eighteen",
        color: "#9C27B0",
        geo: "geo",
        organization: "organisatie",
    },
    {
        id: 19,
        name: "Project Nineteen",
        color: "#673AB7",
        geo: "geo",
        organization: "organisatie",
    },
    {
        id: 20,
        name: "Project Twenty",
        color: "#3F51B5",
        geo: "geo",
        organization: "organisatie",
    },
    {
        id: 21,
        name: "Project Twenty-One",
        color: "#2196F3",
        geo: "geo",
        organization: "organisatie",
    },
    {
        id: 22,
        name: "Project Twenty-Two",
        color: "#00BCD4",
        geo: "geo",
        organization: "organisatie",
    },
    {
        id: 23,
        name: "Project Twenty-Three",
        color: "#009688",
        geo: "geo",
        organization: "organisatie",
    },
    {
        id: 24,
        name: "Project Twenty-Four",
        color: "#4CAF50",
        geo: "geo",
        organization: "organisatie",
    },
    {
        id: 25,
        name: "Project Twenty-Five",
        color: "#CDDC39",
        geo: "geo",
        organization: "organisatie",
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
        };
    };
};
export const policyGoals = [
    {
        id: 1,
        name: "Totaal woningen",
        data: {
            characteristic: "Test-1",
            goal: 12,
            time: "01/01/2023",
            geo: "Voorbeeld-1",
            category: "Cat-1",
        },
    },
    {
        id: 2,
        name: "Woningen voor starters",
        data: {
            characteristic: "Test-2",
            goal: 2,
            time: "03/06/2023",
            geo: "Voorbeeld-2",
            category: "Cat-2",
        },
    },
    {
        id: 3,
        name: "Houtbouw woningen in Delfshaven",
        data: {
            characteristic: "Test-3",
            goal: 3,
            time: "30/04/2023",
            geo: "Voorbeeld-3",
            category: "Cat-3",
        },
    },
];
