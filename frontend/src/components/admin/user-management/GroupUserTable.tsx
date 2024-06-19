import { Box, Typography } from "@mui/material";
import { DataGrid, GridCellParams } from "@mui/x-data-grid";
import EditOutlinedIcon from "@mui/icons-material/EditOutlined";
import DeleteForeverOutlinedIcon from "@mui/icons-material/DeleteForeverOutlined";
import { deleteGroup, updateGroup } from "../../../api/userServices";
import { useState } from "react";
import useAlert from "../../../hooks/useAlert";
import GroupDialog from "./GroupDialog";
import { Group, User } from "../../../pages/UserManagement";
import { useTranslation } from "react-i18next";
import DeleteDialogWithConfirmation from "./DeleteDialogWithConfirmation";
import useAllowedActions from "../../../hooks/useAllowedActions";

type Props = {
    rows: Group[];
    users: User[];
    setUserGroups: (groups: Group[]) => void;
};

const GroupUserTable = ({ rows, users, setUserGroups }: Props) => {
    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
    const [groupToDelete, setGroupToDelete] = useState<string | null>(null);
    const [editDialogOpen, setEditDialogOpen] = useState(false);
    const [groupToEdit, setGroupToEdit] = useState<Group | null>(null);
    const { setAlert } = useAlert();
    const { t } = useTranslation();
    const allowedActions = useAllowedActions();
    const columns = [
        {
            field: "name",
            headerName: t("admin.userManagement.tableHeader.groupes"),
            sortable: true,
            flex: 2,
        },
        {
            field: "users",
            headerName: t("admin.userManagement.tableHeader.users"),
            sortable: true,
            flex: 4,
            renderCell: (params: GridCellParams) => {
                const users = params.row.users.map((user: User) => user.firstName + " " + user.lastName).join(", ");
                return users;
            },
        },
        {
            field: "acties",
            headerName: t("admin.userManagement.tableHeader.actions"),
            sortable: false,
            flex: 1,
            renderCell: (params: GridCellParams) => (
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

    const handleEdit = (group: Group) => {
        setGroupToEdit(group);
        setEditDialogOpen(true);
    };

    const handleUpdateGroup = async () => {
        if (groupToEdit && groupToEdit.uuid) {
            try {
                const updatedGroup = await updateGroup(groupToEdit.uuid, groupToEdit);
                setUserGroups(rows.map((group) => (group.uuid === updatedGroup.uuid ? updatedGroup : group)));
                setAlert(t("admin.userManagement.groupUpdateSuccess"), "success");
            } catch (error: unknown) {
                if (error instanceof Error) {
                    setAlert(error.message, "error");
                }
            } finally {
                setEditDialogOpen(false);
            }
        }
    };

    const handleDeleteDialogOpen = (id: string) => {
        setGroupToDelete(id);
        setDeleteDialogOpen(true);
    };
    const handleDelete = async () => {
        if (groupToDelete) {
            try {
                await deleteGroup(groupToDelete);
                setUserGroups(rows.filter((group) => group.uuid !== groupToDelete));
                setAlert(t("admin.userManagement.groupDeleteSuccess"), "success");
            } catch (error: unknown) {
                if (error instanceof Error) {
                    setAlert(error.message, "error");
                }
            } finally {
                setDeleteDialogOpen(false);
            }
        }
    };

    return (
        <Box sx={{ marginTop: "20px" }}>
            <Typography variant="h6" gutterBottom component="div">
                {t("admin.userManagement.titleGroup")}
            </Typography>
            <DataGrid
                autoHeight
                rows={rows}
                columns={columns}
                getRowId={(row) => row.uuid}
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
                dialogContentText="admin.userManagement.groupDeleteConfirmation"
            />
            {groupToEdit && (
                <GroupDialog
                    open={editDialogOpen}
                    onClose={() => setEditDialogOpen(false)}
                    newGroup={groupToEdit}
                    setNewGroup={setGroupToEdit}
                    handleAddGroup={() => {
                        handleUpdateGroup();
                        setEditDialogOpen(false);
                    }}
                    users={users}
                    title={t("admin.userManagement.editGroup")}
                />
            )}
        </Box>
    );
};

export default GroupUserTable;
