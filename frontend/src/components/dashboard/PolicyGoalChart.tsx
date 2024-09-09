import { Box, Stack } from "@mui/material";
import { PolicyGoal } from "../../api/dashboardServices";

type Props = {
    goal: PolicyGoal;
};

export const PolicyGoalChart = ({ goal }: Props) => {
    const equalGoalandAmount = goal.amount === goal.goal;
    const isSurplus = goal.amount > goal.goal;
    const filledWidth = isSurplus ? (goal.goal / goal.amount) * 100 : (goal.amount / goal.goal) * 100;
    const remainingWidth = 100 - filledWidth;

    return (
        <Stack width="100%">
            <Box
                sx={{
                    backgroundColor: "#738092",
                    color: "#FFFFFF",
                    p: 1,
                    fontSize: "12px",
                    boxShadow: "0px 1px 5px rgb(12, 12, 12)",
                    zIndex: 1000,
                    marginBottom: "17px",
                }}
            >
                {goal.name}
            </Box>
            <Stack direction="row" alignItems="center" width="100%" marginBottom="17px">
                <Box
                    sx={{
                        backgroundColor: "#00A9F3",
                        width: `${filledWidth}%`,
                        p: 2,
                        textAlign: "center",
                        color: "#FFFFFF",
                        borderRadius: equalGoalandAmount ? "8px" : "8px 0 0 8px",
                    }}
                >
                    {goal.amount}
                </Box>
                {!equalGoalandAmount && (
                    <Box
                        sx={{
                            backgroundColor: "#EDEDED",
                            width: `${remainingWidth}%`,
                            p: 2,
                            textAlign: "center",
                            color: isSurplus ? "#04BD00" : "#AB3636",
                            borderRadius: "0 8px 8px 0",
                        }}
                    >
                        {!isSurplus ? `-${Math.abs(goal.goal - goal.amount)}` : Math.abs(goal.goal - goal.amount)}
                    </Box>
                )}
            </Stack>
        </Stack>
    );
};
