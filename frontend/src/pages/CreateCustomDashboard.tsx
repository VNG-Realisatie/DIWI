import { useEffect, useState } from "react";
import { CustomDashboardForm } from "../components/dashboard/CustomDashboardForm";
import { DashboardCharts } from "../components/dashboard/DashboardCharts";
import { User } from "./UserManagement";
import { getUsers } from "../api/userServices";

export const CreateCustomDashboard = () => {
    const [users, setUsers] = useState<User[]>([]);
    const [newBlueprint, setNewBlueprint] = useState({
        name: "",
        users: [],
    });
    useEffect(() => {
        getUsers().then((data) => setUsers(data));
    }, []);
    return (
        <>
            <CustomDashboardForm newBlueprint={newBlueprint} setNewBlueprint={setNewBlueprint} users={users} />
            <DashboardCharts customizable={true} />
        </>
    );
};
