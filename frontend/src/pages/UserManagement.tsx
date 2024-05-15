import { useEffect, useState } from "react";
import UsersTable from "../components/admin/user-management/UsersTable";
import { getGroups, getUsers } from "../api/userSerivces";
import GroupUserTable from "../components/admin/user-management/GroupUserTable";
import { Stack } from "@mui/material";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import { t } from "i18next";
import { addGroup } from "../api/userSerivces";
import useAlert from "../hooks/useAlert";
import GroupDialog from "../components/admin/user-management/GroupDialog";

const UserManagement = () => {
    const [users, setUsers] = useState<any[]>([]);
    const [userGroups, setUserGroups] = useState<any[]>([]);
    const [isGroupDialogOpen, setIsGroupDialogOpen] = useState<boolean>(false);
    const [isUserDialogOpen, setIsUserDialogOpen] = useState<boolean>(false);
    const [newGroup, setNewGroup] = useState<any>({});
    const { setAlert } = useAlert();
    useEffect(() => {
        getUsers().then((data) => setUsers(data));
    }, []);

    useEffect(() => {
        getGroups().then((data) => setUserGroups(data));
    }, []);

    let testGroup: any;
    if (users.length > 0) {
        testGroup = {
            name: "test1",
            users: [
                { uuid: users[1].id, firstName: users[1].firstName, lastName: users[1].lastName },
                { uuid: users[0].id, firstName: users[0].firstName, lastName: users[0].lastName },
            ],
        };
    }

    const handleAddGroup = async () => {
        try {
            const data = await addGroup(newGroup);
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
            <GroupDialog
                open={isGroupDialogOpen}
                onClose={() => setIsGroupDialogOpen(false)}
                newGroup={newGroup}
                setNewGroup={setNewGroup}
                handleAddGroup={handleAddGroup}
                users={users}
            />
        </>
    );
};

export default UserManagement;
