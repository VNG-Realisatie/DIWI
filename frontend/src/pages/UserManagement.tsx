import { useEffect, useState } from "react";
import UsersTable from "../components/admin/user-management/UsersTable";
import { getGroups, getUsers } from "../api/userSerivces";
import GroupUserTable from "../components/admin/user-management/GroupUserTable";
import { Box, Stack } from "@mui/material";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import { t } from "i18next";
import { addGroup } from "../api/userSerivces";
import useAlert from "../hooks/useAlert";
import GroupDialog from "../components/admin/user-management/GroupDialog";
import UserDialog from "../components/admin/user-management/UserDialog";
import useAllowedActions from "../hooks/useAllowedActions";

const emptyGroupForm: Group = {
    name: "",
    users: [],
};

const emptyUserForm: User = {
    firstName: "",
    lastName: "",
    email: "",
    role: "",
    initials: "",
    phone: "",
    organization: "",
    department: "",
};

export type User = {
    uuid?: string;
    firstName?: string;
    lastName?: string;
    email?: string;
    role?: string;
    initials?: string;
    phone?: string;
    organization?: string;
    department?: string;
};

export type Group = {
    uuid?: string;
    name?: string;
    users?: User[];
};

const UserManagement = () => {
    const [users, setUsers] = useState<User[]>([]);
    const [userGroups, setUserGroups] = useState<Group[]>([]);
    const [isGroupDialogOpen, setIsGroupDialogOpen] = useState<boolean>(false);
    const [isUserDialogOpen, setIsUserDialogOpen] = useState<boolean>(false);
    const [newGroup, setNewGroup] = useState<Group>(emptyGroupForm);
    const [newUser, setNewUser] = useState<User>(emptyUserForm);
    const { setAlert } = useAlert();
    const allowedActions = useAllowedActions();
    useEffect(() => {
        getUsers().then((data) => setUsers(data));
    }, []);

    useEffect(() => {
        getGroups().then((data) => setUserGroups(data));
    }, []);

    const handleAddGroup = async () => {
        try {
            const data = await addGroup(newGroup);
            setUserGroups([...userGroups, data]);
            setNewGroup(emptyGroupForm);
        } catch (error: any) {
            setAlert(error.message, "warning");
        } finally {
            setIsGroupDialogOpen(false);
        }
    };

    const handleAddUser = () => {
        // Implement this function to add a user
    };
    return (
        <Box sx={{ marginBottom: "90px" }}>
            <UsersTable rows={users} />

            {allowedActions.includes("EDIT_USERS") && (
                <Stack direction="row" alignItems="center" mt={1} sx={{ cursor: "pointer" }} onClick={() => setIsGroupDialogOpen(true)}>
                    <AddCircleIcon color="primary" sx={{ fontSize: "40px" }} />
                    {t("admin.userManagement.addGroup")}
                </Stack>
            )}

            <GroupUserTable rows={userGroups} users={users} setUserGroups={setUserGroups} userGroups={userGroups} />

            {allowedActions.includes("EDIT_USERS") && (
                <Stack direction="row" alignItems="center" mt={1} sx={{ cursor: "pointer" }} onClick={() => setIsGroupDialogOpen(true)}>
                    <AddCircleIcon color="primary" sx={{ fontSize: "40px" }} />
                    {t("admin.userManagement.addGroup")}
                </Stack>
            )}

            <GroupDialog
                open={isGroupDialogOpen}
                onClose={() => setIsGroupDialogOpen(false)}
                newGroup={newGroup}
                setNewGroup={setNewGroup}
                handleAddGroup={handleAddGroup}
                users={users}
                title={t("admin.userManagement.addGroup")}
            />
            <UserDialog
                open={isUserDialogOpen}
                onClose={() => setIsUserDialogOpen(false)}
                newUser={newUser}
                setNewUser={setNewUser}
                handleAddUser={handleAddUser}
                title={t("admin.userManagement.addUser")}
            />
        </Box>
    );
};

export default UserManagement;
