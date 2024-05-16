import { Box, Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, Typography } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import EditOutlinedIcon from "@mui/icons-material/EditOutlined";
import DeleteForeverOutlinedIcon from "@mui/icons-material/DeleteForeverOutlined";
import { deleteGroup, updateGroup } from "../../../api/userSerivces";
import { useState } from "react";
import useAlert from "../../../hooks/useAlert";
import GroupDialog from "./GroupDialog";

type Props = {
    rows: any[];
    users: any[];
    userGroups: any[];
    setUserGroups: any;
};

const rows = [
    { id: 1, 1: "Group 1", 2: "User 1", 3: "Action 1" },
    { id: 2, 1: "Group 2", 2: "User 2", 3: "Action 2" },
];

const GroupUserTable = ({ rows, users, userGroups, setUserGroups }: Props) => {
    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
    const [groupToDelete, setGroupToDelete] = useState<any>(null);
    const [editDialogOpen, setEditDialogOpen] = useState(false);
    const [groupToEdit, setGroupToEdit] = useState<any>(null);
    const { setAlert } = useAlert();
    const columns = [
        {
            field: "name",
            headerName: "Groupen",
            sortable: true,
        },
        {
            field: "users",
            headerName: "Gebruikers",
            sortable: true,
            renderCell: (cellValues: any) => {
                const users = cellValues?.row?.users.map((user: any) => user.initials).join(", ");
                return users; //how do we want to display them??
            },
        },
        {
            field: "acties",
            headerName: "Acties",
            sortable: true,
            renderCell: (params: any) => (
                <Box display="flex">
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
            } catch (error: any) {
                setAlert(error.message, "warning");
            } finally {
                setDeleteDialogOpen(false);
            }
        }
    };

    return (
        <>
            <Typography variant="h6" gutterBottom component="div">
                User management groupen
            </Typography>
            <DataGrid rows={rows} columns={columns} getRowId={(row) => row.uuid} />
            <Dialog open={deleteDialogOpen} onClose={() => setDeleteDialogOpen(false)}>
                <DialogContent>
                    <DialogContentText>Are you sure you want to delete this group?</DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setDeleteDialogOpen(false)} color="primary">
                        No
                    </Button>
                    <Button onClick={handleDelete} color="primary">
                        Yes
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
        </>
    );
};

export default GroupUserTable;
