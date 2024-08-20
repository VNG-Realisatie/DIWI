import { Box } from "@mui/material";
import { getAllGoals, Goal } from "../api/goalsServices";
import { useEffect, useState } from "react";
import { DataGrid, GridColDef } from "@mui/x-data-grid";
import { AddGoalButton } from "../components/PlusButton";
import { useNavigate } from "react-router-dom";
export const Goals = () => {
    const [goals, setGoals] = useState<Goal[]>([]);
    const navigate = useNavigate();
    useEffect(() => {
        getAllGoals().then((goals) => {
            setGoals(goals);
        });
    }, []);

    const columns: GridColDef[] = [
        { field: "name", headerName: "Property", flex: 1 },
        { field: "goalDirection", headerName: "Goal", flex: 1 },
        { field: "startDate", headerName: "Start Date", flex: 1 },
        { field: "endDate", headerName: "End Date", flex: 1 },
        { field: "geography", headerName: "Geography", flex: 1 },
        { field: "category", headerName: "Category", flex: 1 },
    ];
    const rows = goals.map((goal) => ({
        id: goal.id,
        name: goal.name,
        goalDirection: goal.goalDirection,
        startDate: goal.startDate,
        endDate: goal.endDate,
        geography: goal.geography?.conditionId, //???
        category: goal.category?.name,
    }));

    const handleRowClick = (params: any) => {
        navigate(`/goals/${params.id}`);
    };

    return (
        <>
            <Box sx={{ width: "100%" }}>
                <DataGrid rows={rows} columns={columns} onRowClick={handleRowClick} />
            </Box>
            <Box position="relative" top={350}>
                <AddGoalButton />
            </Box>
        </>
    );
};
