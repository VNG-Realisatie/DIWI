import { Box, Stack } from "@mui/material";
import { PolicyGoal } from "../../api/dashboardServices";
import { t } from "i18next";

type Props = {
    goal: PolicyGoal;
};

const getStyles = (equalGoalandAmount: boolean, isSurplus: boolean, isMaximalPercentageGoal: boolean) => ({
    goalBox: {
        backgroundColor: "#738092",
        color: "#FFFFFF",
        p: 1,
        fontSize: "12px",
        boxShadow: "0px 1px 5px rgb(12, 12, 12)",
        zIndex: 1000,
        marginBottom: "17px",
    },
    filledBox: {
        backgroundColor: "#00A9F3",
        p: 2,
        textAlign: "center",
        color: "#FFFFFF",
        borderRadius: equalGoalandAmount ? "8px" : "8px 0 0 8px",
    },
    remainingBox: {
        backgroundColor: "#EDEDED",
        p: 2,
        textAlign: "center",
        color: isMaximalPercentageGoal ? (isSurplus ? "#AB3636" : "#04BD00") : isSurplus ? "#04BD00" : "#AB3636",
        borderRadius: "0 8px 8px 0",
    },
});

export const PolicyGoalChart = ({ goal }: Props) => {
    const isNumericGoal = goal.goalType === "NUMBER";
    const isMaximalPercentageGoal = goal.goalDirection === "MAXIMAL" && goal.goalType === "PERCENTAGE";
    const amount = isNumericGoal ? goal.amount : goal.percentage;
    const equalGoalandAmount = amount === goal.goal;

    const isSurplus = amount > goal.goal;
    const filledWidth = Math.min(isSurplus ? (goal.goal / amount) * 100 : (amount / goal.goal) * 100, equalGoalandAmount || isNumericGoal ? 100 : 90);
    const remainingWidth = 100 - filledWidth;

    const difference = Math.abs(goal.goal - amount);
    const formattedDifference = goal.goalType === "NUMBER" ? difference : difference.toFixed(2);

    const styles = getStyles(equalGoalandAmount, isSurplus, isMaximalPercentageGoal);

    const text = isNumericGoal
        ? !isSurplus
            ? `-${formattedDifference}`
            : formattedDifference
        : `${formattedDifference}% ${
              isSurplus ? t("goals.dashboard.surplus") : isMaximalPercentageGoal ? t("goals.dashboard.moreSpace") : t("goals.dashboard.deficit")
          }`;

    return (
        <Stack width="100%">
            <Box sx={styles.goalBox}>{goal.name}</Box>
            <Stack direction="row" alignItems="center" width="100%" marginBottom="17px">
                <Box sx={{ ...styles.filledBox, width: `${filledWidth}%` }}>{isNumericGoal ? amount : `${amount}%`}</Box>
                {!equalGoalandAmount && <Box sx={{ ...styles.remainingBox, width: `${remainingWidth}%` }}>{text}</Box>}
            </Stack>
        </Stack>
    );
};
