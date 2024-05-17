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
        { field: "firstName", headerName: t("admin.userManagement.tableHeader.name.firstName"), flex: 1.5, sortable: true },
        { field: "lastName", headerName: t("admin.userManagement.tableHeader.name.lastName"), flex: 1.5, sortable: true },
        { field: "email", headerName: t("admin.userManagement.tableHeader.email"), flex: 1.5, sortable: true },
        { field: "role", headerName: t("admin.userManagement.tableHeader.role"), flex: 1, sortable: true },
        {
            field: "acties",
            headerName: t("admin.userManagement.tableHeader.actions"),
            flex: 0.5,
            sortable: true,
            renderCell: (params: any) => (
                <Box display="flex" alignItems="center" justifyContent="center" style={{ height: "100%" }} gap="10px">
                    <EditOutlinedIcon style={{ cursor: "pointer" }} color="primary" onClick={() => handleEdit(params.row)} />
                    <DeleteForeverOutlinedIcon style={{ cursor: "pointer" }} color="error" onClick={() => handleDelete(params.row)} />
                </Box>
            ),
        },
    ];
    return (
        <>
            <Typography variant="h6" gutterBottom component="div">
                {t("admin.userManagement.titleUser")}
            </Typography>
            <DataGrid
                autoHeight
                rows={rows}
                columns={columns}
                initialState={{
                    pagination: {
                        paginationModel: { page: 0, pageSize: 10 },
                    },
                }}
                pageSizeOptions={[5, 10, 25]}
            />
        </>
    );
};

export default UsersTable;
