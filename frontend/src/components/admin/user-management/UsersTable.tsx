import { Box, Typography } from "@mui/material";
import { DataGrid, GridCellParams } from "@mui/x-data-grid";
import EditOutlinedIcon from "@mui/icons-material/EditOutlined";
import DeleteForeverOutlinedIcon from "@mui/icons-material/DeleteForeverOutlined";
import { useTranslation } from "react-i18next";
import { useState } from "react";
import useAlert from "../../../hooks/useAlert";
import UserDialog from "./UserDialog";
import DeleteDialogWithConfirmation from "./DeleteDialogWithConfirmation";
import useAllowedActions from "../../../hooks/useAllowedActions";
import { deleteUser, updateUser, User } from "../../../api/userServices";

type Props = {
    rows: User[];
    setUsers: (users: User[]) => void;
};

const UsersTable = ({ rows, setUsers }: Props) => {
    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
    const [userToDelete, setUserToDelete] = useState<string | null>(null);
    const [editUserOpen, setUserDialogOpen] = useState(false);
    const [userToEdit, setUserToEdit] = useState<User | null>(null);
    const { setAlert } = useAlert();
    const { allowedActions } = useAllowedActions();

    const { t } = useTranslation();
    const handleEdit = (user: User) => {
        setUserToEdit(user);
        setUserDialogOpen(true);
    };

    const handleUpdateUser = async () => {
        if (userToEdit && userToEdit.id) {
            try {
                const updatedUser = await updateUser(userToEdit.id, userToEdit);
                setAlert(t("admin.userManagement.userUpdateSuccess"), "success");
                setUsers(rows.map((user) => (user.id === userToEdit.id ? updatedUser : user)));
            } catch (error: unknown) {
                if (error instanceof Error) {
                    setAlert(error.message, "error");
                }
            } finally {
                setUserDialogOpen(false);
            }
        }
    };
    const handleDeleteDialogOpen = (id: string) => {
        setUserToDelete(id);
        setDeleteDialogOpen(true);
    };
    const handleDelete = async () => {
        if (userToDelete) {
            try {
                await deleteUser(userToDelete);
                setAlert(t("admin.userManagement.userDeleteSuccess"), "success");
                setUsers(rows.filter((user) => user.id !== userToDelete));
            } catch (error: unknown) {
                if (error instanceof Error) {
                    setAlert(error.message, "error");
                }
            } finally {
                setDeleteDialogOpen(false);
            }
        }
    };

    const columns = [
        { field: "firstName", headerName: t("admin.userManagement.tableHeader.name.firstName"), flex: 1.5, sortable: true },
        { field: "lastName", headerName: t("admin.userManagement.tableHeader.name.lastName"), flex: 1.5, sortable: true },
        { field: "phoneNumber", headerName: t("admin.userManagement.tableHeader.phone"), flex: 1.5, sortable: true },
        { field: "email", headerName: t("admin.userManagement.tableHeader.email"), flex: 1.5, sortable: true },
        { field: "organization", headerName: t("admin.userManagement.tableHeader.organization"), flex: 1.5, sortable: true },
        { field: "department", headerName: t("admin.userManagement.tableHeader.department"), flex: 1.5, sortable: true },
        {
            field: "role",
            headerName: t("admin.userManagement.tableHeader.role"),
            flex: 1,
            sortable: true,
            renderCell: (params: GridCellParams) => t(`admin.userManagement.roles.${params.value}`),
        },
        {
            field: "acties",
            headerName: t("admin.userManagement.tableHeader.actions"),
            flex: 0.5,
            sortable: false,
            renderCell: (params: GridCellParams) => (
                <Box display="flex" alignItems="center" justifyContent="center" style={{ height: "100%" }} gap="10px">
                    {allowedActions.includes("EDIT_USERS") && (
                        <>
                            <EditOutlinedIcon style={{ cursor: "pointer" }} color="primary" onClick={() => handleEdit(params.row as User)} />
                            <DeleteForeverOutlinedIcon
                                style={{ cursor: "pointer" }}
                                color="error"
                                onClick={() => handleDeleteDialogOpen(params.row.id as string)}
                            />
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
