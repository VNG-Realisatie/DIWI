import { Box, Button, Dialog, DialogActions, DialogContent, DialogContentText, Typography } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import EditOutlinedIcon from "@mui/icons-material/EditOutlined";
import DeleteForeverOutlinedIcon from "@mui/icons-material/DeleteForeverOutlined";
import { deleteGroup, updateGroup } from "../../../api/userSerivces";
import { useState } from "react";
import useAlert from "../../../hooks/useAlert";
import GroupDialog from "./GroupDialog";
import { Group, User } from "../../../pages/UserManagement";
import { useTranslation } from "react-i18next";

type Props = {
    rows: any[];
    users: User[];
    userGroups: Group[];
    setUserGroups: any;
};

const GroupUserTable = ({ rows, users, userGroups, setUserGroups }: Props) => {
    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
    const [groupToDelete, setGroupToDelete] = useState<any>(null);
    const [editDialogOpen, setEditDialogOpen] = useState(false);
    const [groupToEdit, setGroupToEdit] = useState<any>(null);
    const { setAlert } = useAlert();
    const { t } = useTranslation();
    const columns = [
        {
            field: "name",
            headerName: t("admin.userManagement.tableHeader.groupeName"),
            sortable: true,
            flex: 2,
        },
        {
            field: "users",
            headerName: t("admin.userManagement.tableHeader.users"),
            sortable: true,
            flex: 4,
            renderCell: (cellValues: any) => {
                const users = cellValues?.row?.users.map((user: User) => user.initials).join(", ");
                return users; //how do we want to display them??
            },
        },
        {
            field: "acties",
            headerName: t("admin.userManagement.tableHeader.actions"),
            sortable: true,
            flex: 1,
            renderCell: (params: any) => (
                <Box display="flex" alignItems="center" justifyContent="center" style={{ height: "100%" }} gap="10px">
                    <EditOutlinedIcon style={{ cursor: "pointer" }} color="primary" onClick={() => handleEdit(params.row)} />
                    <DeleteForeverOutlinedIcon style={{ cursor: "pointer" }} color="error" onClick={() => handleDeleteDialogOpen(params.row.uuid)} />
                </Box>
            ),
        },
    ];

    const handleEdit = (group: any) => {
        setGroupToEdit(group);
        setEditDialogOpen(true);
    };

    const handleUpdateGroup = async () => {
        if (groupToEdit) {
            try {
                const updatedGroup = await updateGroup(groupToEdit.uuid, groupToEdit);
                setUserGroups(userGroups.map((group) => (group.uuid === updatedGroup.uuid ? updatedGroup : group)));
                setAlert(t("admin.userManagement.groupUpdateSuccess"), "success");
            } catch (error: any) {
                setAlert(error.message, "warning");
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
                setUserGroups(userGroups.filter((group) => group.uuid !== groupToDelete));
                setAlert(t("admin.userManagement.groupDeleteSuccess"), "success");
            } catch (error: any) {
                setAlert(error.message, "warning");
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
            />
            <Dialog open={deleteDialogOpen} onClose={() => setDeleteDialogOpen(false)}>
                <DialogContent>
                    <DialogContentText>{t("admin.userManagement.groupDeleteConfirmation")}</DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setDeleteDialogOpen(false)} variant="outlined">
                        {t("generic.no")}
                    </Button>
                    <Button onClick={handleDelete} variant="contained">
                        {t("generic.yes")}
                    </Button>
                </DialogActions>
            </Dialog>
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
                />
            )}
        </Box>
    );
};

export default GroupUserTable;
