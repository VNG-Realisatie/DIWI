import { Box, Typography } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import EditOutlinedIcon from "@mui/icons-material/EditOutlined";
import DeleteForeverOutlinedIcon from "@mui/icons-material/DeleteForeverOutlined";

const columns = [
    { field: "firstName", headerName: "Naam", width: 130, sortable: true },
    { field: "lastName", headerName: "Achternaam", width: 130, sortable: true },
    // { field: "tel", headerName: "Tel.", width: 130, sortable: true },
    { field: "email", headerName: "E-mail", width: 200, sortable: true },
    // { field: "organisatie", headerName: "Organisatie", width: 200, sortable: true },
    { field: "role", headerName: "rol", width: 200, sortable: true },
    // { field: "type", headerName: "Type", width: 130, sortable: true },
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

// Implement these functions to handle the edit and delete actions
function handleEdit(row: any) {
    // Handle the edit action
}

function handleDelete(row: any) {
    // Handle the delete action
}

type Props = {
    rows: any[];
};

const UsersTable = ({ rows }: Props) => {
    return (
        <>
            <Typography variant="h6" gutterBottom component="div">
                User management Gebruikers
            </Typography>
            <DataGrid rows={rows} columns={columns} />
        </>
    );
};

export default UsersTable;
