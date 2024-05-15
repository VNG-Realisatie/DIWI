import { Box, Typography } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import EditOutlinedIcon from "@mui/icons-material/EditOutlined";
import DeleteForeverOutlinedIcon from "@mui/icons-material/DeleteForeverOutlined";

type Props = {
    rows: any[];
};

const columns = [
    {
        field: "name",
        headerName: "Groupen",
        width: 130,
        sortable: true,
    },
    {
        field: "users",
        headerName: "Gebruikers",
        width: 130,
        sortable: true,
        renderCell: (cellValues: any) => {
            const users = cellValues?.row?.users.map((user: any) => user.initials).join(", ");
            return users; //how do we want to display them??
        },
    },
    {
        field: "acties",
        headerName: "Acties",
        width: 130,
        sortable: true,
        renderCell: (params: any) => (
            <Box display="flex">
                <EditOutlinedIcon style={{ cursor: "pointer" }} color="primary" onClick={() => handleEdit(params.row)} />
                <DeleteForeverOutlinedIcon style={{ cursor: "pointer" }} color="error" onClick={() => handleDelete(params.row)} />
            </Box>
        ),
    },
];

const rows = [
    { id: 1, 1: "Group 1", 2: "User 1", 3: "Action 1" },
    { id: 2, 1: "Group 2", 2: "User 2", 3: "Action 2" },
];

// Implement these functions to handle the edit and delete actions
function handleEdit(row: any) {
    // Handle the edit action
}

function handleDelete(row: any) {
    // Handle the delete action
}

const GroupUserTable = ({ rows }: Props) => {
    return (
        <>
            <Typography variant="h6" gutterBottom component="div">
                User management groupen
            </Typography>
            <DataGrid rows={rows} columns={columns} getRowId={(row) => row.uuid} />
        </>
    );
};

export default GroupUserTable;
