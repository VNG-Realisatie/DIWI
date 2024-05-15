import { Box, Typography } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import EditOutlinedIcon from "@mui/icons-material/EditOutlined";
import DeleteForeverOutlinedIcon from "@mui/icons-material/DeleteForeverOutlined";
import { useTranslation } from "react-i18next";

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
    const { t } = useTranslation();

    const columns = [
        { field: "firstName", headerName: t("admin.userManagement.tableHeader.name.firstName"), width: 130, sortable: true },
        { field: "lastName", headerName: t("admin.userManagement.tableHeader.name.lastName"), width: 130, sortable: true },
        { field: "email", headerName: t("admin.userManagement.tableHeader.email"), width: 200, sortable: true },
        { field: "role", headerName: t("admin.userManagement.tableHeader.role"), width: 200, sortable: true },
        {
            field: "acties",
            headerName: t("admin.userManagement.tableHeader.actions"),
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
