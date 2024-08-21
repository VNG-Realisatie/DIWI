import { Box } from "@mui/material";
import { deleteGoal, getAllGoals, Goal } from "../api/goalsServices";
import { useContext, useEffect, useState } from "react";
import { DataGrid, GridCellParams, GridColDef } from "@mui/x-data-grid";
import { AddGoalButton } from "../components/PlusButton";
import { useNavigate } from "react-router-dom";
import EditOutlinedIcon from "@mui/icons-material/EditOutlined";
import DeleteForeverOutlinedIcon from "@mui/icons-material/DeleteForeverOutlined";
import DeleteDialogWithConfirmation from "../components/admin/user-management/DeleteDialogWithConfirmation";
import AlertContext from "../context/AlertContext";
import { t } from "i18next";

export const Goals = () => {
    const [goals, setGoals] = useState<Goal[]>([]);
    const navigate = useNavigate();
    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
    const { setAlert } = useContext(AlertContext);
    const [idToDelete, setIdToDelete] = useState<string>("");
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
        {
            field: "actions",
            headerName: "Actions",
            flex: 1,
            sortable: false,
            renderCell: (params: GridCellParams) => (
                <Box display="flex" alignItems="center" justifyContent="center" style={{ height: "100%" }} gap="10px">
                    <>
                        <EditOutlinedIcon style={{ cursor: "pointer" }} color="primary" onClick={() => navigate(`/goals/${params.row.id}`)} />
                        <DeleteForeverOutlinedIcon
                            style={{ cursor: "pointer" }}
                            color="error"
                            onClick={() => {
                                setIdToDelete(params.row.id);
                                setDeleteDialogOpen(true);
                            }}
                        />
                    </>
                </Box>
            ),
        },
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

    const handleDelete = async (goalId: string) => {
        try {
            await deleteGoal(goalId);
            setGoals(goals.filter((goal) => goal.id !== goalId));
            setAlert(t("goals.notifications.deleted"), "success");
            setDeleteDialogOpen(false);
            setIdToDelete("");
        } catch (error) {
            if (error instanceof Error) {
                setAlert(error.message, "error");
            }
        }
    };

    return (
        <>
            <Box sx={{ width: "100%" }}>
                <DataGrid rows={rows} columns={columns} />
            </Box>
            <Box position="relative" top={350}>
                <AddGoalButton />
            </Box>
            <DeleteDialogWithConfirmation
                open={deleteDialogOpen}
                onClose={() => setDeleteDialogOpen(false)}
                onConfirm={() => handleDelete(idToDelete)}
                dialogContentText="admin.userManagement.groupDeleteConfirmation"
            />
        </>
    );
};
