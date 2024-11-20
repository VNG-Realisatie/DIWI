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
import UserContext from "../context/UserContext";

export const Goals = () => {
    const [goals, setGoals] = useState<Goal[]>([]);
    const navigate = useNavigate();
    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
    const { setAlert } = useContext(AlertContext);
    const [idToDelete, setIdToDelete] = useState<string>("");
    const { allowedActions } = useContext(UserContext);
    useEffect(() => {
        getAllGoals().then((goals) => {
            setGoals(goals);
        });
    }, []);

    const columns: GridColDef[] = [
        { field: "name", headerName: t("goals.table.property"), flex: 1 },
        { field: "goalDirection", headerName: t("goals.table.goal"), flex: 1 },
        { field: "startDate", headerName: t("goals.table.startDate"), flex: 1 },
        { field: "endDate", headerName: t("goals.table.endDate"), flex: 1 },
        // { field: "geography", headerName: t("goals.table.geography"), flex: 1 }, Will be implemented later
        { field: "category", headerName: t("goals.table.category"), flex: 1 },
        {
            field: "actions",
            headerName: t("goals.table.actions"),
            flex: 1,
            sortable: false,
            renderCell: (params: GridCellParams) => (
                <Box display="flex" alignItems="center" justifyContent="center" style={{ height: "100%" }} gap="10px">
                    {allowedActions.includes("EDIT_GOALS") && (
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
                    )}
                </Box>
            ),
        },
    ];
    const rows = goals.map((goal) => ({
        id: goal.id,
        name: goal.name,
        goalDirection: goal.goalType === "NUMBER" ? goal.goalValue : `${t(`goals.goalType.direction.${goal.goalDirection}`)} | ${goal.goalValue}%`,
        startDate: goal.startDate,
        endDate: goal.endDate,
        // geography: goal.geography?.conditionId, //???
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
            <Box sx={{ width: "100%", overflowY: "auto", paddingBottom: "150px" }}>
                <DataGrid
                    rows={rows}
                    columns={columns}
                    autoHeight
                    initialState={{
                        pagination: {
                            paginationModel: { page: 0, pageSize: 10 },
                        },
                    }}
                    pageSizeOptions={[5, 10, 25]}
                    disableRowSelectionOnClick
                />
                {allowedActions.includes("EDIT_GOALS") && (
                    <Box sx={{ position: "relative", top: "80px" }}>
                        <AddGoalButton />
                    </Box>
                )}
            </Box>

            <DeleteDialogWithConfirmation
                open={deleteDialogOpen}
                onClose={() => setDeleteDialogOpen(false)}
                onConfirm={() => handleDelete(idToDelete)}
                dialogContentText="goals.deleteConfirmation"
            />
        </>
    );
};
