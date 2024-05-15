import { useEffect, useState } from "react";
import UsersTable from "../components/admin/user-management/UsersTable";
import { getGroups, getUsers } from "../api/userSerivces";
import GroupUserTable from "../components/admin/user-management/GroupUserTable";
import { Box, Button, Dialog, DialogActions, DialogTitle, Stack } from "@mui/material";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import { t } from "i18next";
import { addGroup } from "../api/userSerivces";
import useAlert from "../hooks/useAlert";
import TextInput from "../components/project/inputs/TextInput";

const UserManagement = () => {
    const [users, setUsers] = useState<any[]>([]);
    const [userGroups, setUserGroups] = useState<any[]>([]);
    const [isGroupDialogOpen, setIsGroupDialogOpen] = useState<boolean>(false);
    const [isUserDialogOpen, setIsUserDialogOpen] = useState<boolean>(false);
    const [newGroup, setNewGroup] = useState<any>({});
    const { setAlert } = useAlert();
    useEffect(() => {
        getUsers().then((data) => setUsers(data));
        getGroups().then((data) => setUserGroups(data));
    }, []);

    let testGroup: any;
    if (users.length > 0) {
        testGroup = {
            name: "fdfd",
            users: [
                { uuid: users[1].id, firstName: users[1].firstName, lastName: users[1].lastName },
                { uuid: users[0].id, firstName: users[0].firstName, lastName: users[0].lastName },
            ],
        };
    }

    const handleAddGroup = async () => {
        try {
            const data = await addGroup(testGroup);
            console.log(data);
            setUserGroups([...userGroups, data]);
        } catch (error: any) {
            setAlert(error.message, "warning");
        }
    };

    const handleAddUser = () => {
        // Implement this function to add a user
    };

    return (
        <>
            <UsersTable rows={users} />

            <Stack direction="row" alignItems="center" mt={1} sx={{ cursor: "pointer" }} onClick={handleAddUser}>
                <AddCircleIcon color="primary" sx={{ fontSize: "40px" }} />
                {t("createProject.addAnotherHouseBlock")} {/* needs translation */}
            </Stack>

            <GroupUserTable rows={userGroups} />

            <Stack direction="row" alignItems="center" mt={1} sx={{ cursor: "pointer" }} onClick={() => setIsGroupDialogOpen(true)}>
                <AddCircleIcon color="primary" sx={{ fontSize: "40px" }} />
                {t("createProject.addAnotherHouseBlock")} {/* needs translation */}
            </Stack>
            {isGroupDialogOpen && (
                <Dialog open={isGroupDialogOpen} onClose={() => setIsGroupDialogOpen(false)}>
                    <DialogTitle>Voeg een groep toe</DialogTitle>
                    <DialogActions>
                        <TextInput
                            readOnly={false}
                            value={newGroup.name}
                            setValue={(value: any) => setNewGroup({ ...newGroup, name: value })}
                            mandatory={true}
                            title={t("createProject.informationForm.nameLabel")}
                            errorText={t("createProject.hasMissingRequiredAreas.name")}
                        />
                        <Box sx={{ display: "flex", gap: "10px" }}>
                            <Button
                                onClick={(event) => {
                                    event.stopPropagation();
                                    setIsGroupDialogOpen(false);
                                }}
                                variant="outlined"
                            >
                                {t("generic.no")}
                            </Button>
                            <Button onClick={handleAddGroup} variant="contained">
                                {t("generic.yes")}
                            </Button>
                        </Box>
                    </DialogActions>
                </Dialog>
            )}
        </>
    );
};

export default UserManagement;
