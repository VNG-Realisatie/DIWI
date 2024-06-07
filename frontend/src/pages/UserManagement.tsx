import { useEffect, useState } from "react";
import UsersTable from "../components/admin/user-management/UsersTable";
import { getGroups, getUsers } from "../api/userServices";
import GroupUserTable from "../components/admin/user-management/GroupUserTable";
import { Box, Stack } from "@mui/material";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import { t } from "i18next";
import { addGroup } from "../api/userServices";
import useAlert from "../hooks/useAlert";
import GroupDialog from "../components/admin/user-management/GroupDialog";
import UserDialog from "../components/admin/user-management/UserDialog";
import useAllowedActions from "../hooks/useAllowedActions";
import { addUser } from "../api/userServices";
import { RoleType } from "../types/enums";

const emptyGroupForm: Group = {
    uuid: "",
    name: "",
    users: [],
};

const emptyUserForm: User = {
    firstName: "",
    lastName: "",
    email: "",
    role: undefined,
    organization: "",
    phoneNumber: "",
    department: "",
    contactPerson: "",
    prefixes: "",
};

export type User = {
    id?: string;
    firstName?: string;
    lastName?: string;
    email?: string;
    role?: RoleType;
    organization?: string;
    phoneNumber?: string;
    department?: string;
    contactPerson?: string;
    prefixes?: string;
};

export type Group = {
    uuid: string;
    name: string;
    users: User[];
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
            setAlert(t("admin.userManagement.groupAddSuccess"), "success");
        } catch (error: unknown) {
            if (error instanceof Error) {
                setAlert(error.message, "error");
            }
        } finally {
            setIsGroupDialogOpen(false);
        }
    };

    const handleAddUser = async () => {
        try {
            const data = await addUser(newUser);
            console.log(data);
            setUsers([...users, data]);
            setNewUser(emptyUserForm);
            setAlert(t("admin.userManagement.userAddSuccess"), "success");
        } catch (error: unknown) {
            if (error instanceof Error) {
                setAlert(error.message, "error");
            }
        } finally {
            setIsUserDialogOpen(false);
        }
    };
    return (
        <Box sx={{ marginBottom: "90px" }}>
            <UsersTable rows={users} setUsers={setUsers} />

            {allowedActions.includes("EDIT_USERS") && (
                <Stack direction="row" alignItems="center" mt={1} sx={{ cursor: "pointer" }} onClick={() => setIsUserDialogOpen(true)}>
                    <AddCircleIcon color="primary" sx={{ fontSize: "40px" }} />
                    {t("admin.userManagement.addUser")}
                </Stack>
            )}

            <GroupUserTable rows={userGroups} users={users} setUserGroups={setUserGroups} />

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
