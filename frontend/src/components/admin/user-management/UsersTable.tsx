import { Box, Typography } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import EditOutlinedIcon from "@mui/icons-material/EditOutlined";
import DeleteForeverOutlinedIcon from "@mui/icons-material/DeleteForeverOutlined";
import { useTranslation } from "react-i18next";
import { useState } from "react";
import { User } from "../../../pages/UserManagement";
import useAlert from "../../../hooks/useAlert";
import UserDialog from "./UserDialog";
import DeleteDialogWithConfirmation from "./DeleteDialogWithConfirmation";
import useAllowedActions from "../../../hooks/useAllowedActions";

type Props = {
    rows: any[];
};

const UsersTable = ({ rows }: Props) => {
    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
    const [userToDelete, setUserToDelete] = useState<string | null>(null);
    const [editUserOpen, setUserDialogOpen] = useState(false);
    const [userToEdit, setUserToEdit] = useState<User | null>(null);
    const { setAlert } = useAlert();
    const allowedActions = useAllowedActions();

    const { t } = useTranslation();
    const handleEdit = (user: User) => {
        setUserToEdit(user);
        setUserDialogOpen(true);
    };

    const handleUpdateUser = async (user: User) => {
        // Update the user
    };
    const handleDeleteDialogOpen = (id: string) => {
        setUserToDelete(id);
        setDeleteDialogOpen(true);
    };
    const handleDelete = async () => {
        if (userToDelete === null) return;
        setAlert("user deleted", "success");
        // Delete the user
    };

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
                    {allowedActions.includes("EDIT_USERS") && (
                        <>
                            <EditOutlinedIcon style={{ cursor: "pointer" }} color="primary" onClick={() => handleEdit(params.row)} />
                            <DeleteForeverOutlinedIcon style={{ cursor: "pointer" }} color="error" onClick={() => handleDeleteDialogOpen(params.row.uuid)} />
                        </>
                    )}
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
                disableRowSelectionOnClick
            />
            <DeleteDialogWithConfirmation
                open={deleteDialogOpen}
                onClose={() => setDeleteDialogOpen(false)}
                onConfirm={handleDelete}
                dialogContentText="admin.userManagement.userDeleteConfirmation"
            />
            {userToEdit && (
                <UserDialog
                    open={editUserOpen}
                    onClose={() => setUserDialogOpen(false)}
                    newUser={userToEdit}
                    setNewUser={setUserToEdit}
                    handleAddUser={handleUpdateUser}
                    title={t("admin.userManagement.editUser")}
                />
            )}
        </>
    );
};

export default UsersTable;
