import { Box, Stack, useMediaQuery } from "@mui/material";
import { PolicyGoal } from "../../api/dashboardServices";
import { t } from "i18next";

type Props = {
    goal: PolicyGoal;
    isPDF?: boolean;
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
        backgroundColor: "#D3D3D3",
        p: 2,
        textAlign: "center",
        color: isMaximalPercentageGoal ? (isSurplus ? "#AB3636" : "#04BD00") : isSurplus ? "#04BD00" : "#AB3636",
        borderRadius: "0 8px 8px 0",
    },
});

const calculateWidths = (isNumericGoal: boolean, isSurplus: boolean, amount: number, goal: number, isPDF: boolean) => {
    let filledWidth, remainingWidth;
    if (isNumericGoal) {
        filledWidth = isSurplus ? (goal / amount) * 100 : (amount / goal) * 100;
        remainingWidth = 100 - filledWidth;
    } else {
        filledWidth = Math.max(amount, isPDF ? 15 : 4);
        filledWidth = Math.min(filledWidth, isPDF ? 75 : 90);
        remainingWidth = Math.max(goal - amount, isPDF ? 25 : 10);
    }
    return { filledWidth, remainingWidth };
};

const formatDifference = (goalType: string, difference: number) => {
    return goalType === "NUMBER" ? difference : difference.toFixed(2);
};

const getText = (isNumericGoal: boolean, isSurplus: boolean, formattedDifference: string, isMaximalPercentageGoal: boolean, isBigScreen: boolean) => {
    if (!isBigScreen) {
        return isNumericGoal ? (!isSurplus ? `-${formattedDifference}` : formattedDifference) : `${formattedDifference}%`;
    }
    return isNumericGoal
        ? !isSurplus
            ? `-${formattedDifference}`
            : formattedDifference
        : `${formattedDifference}% ${
              isSurplus ? t("goals.dashboard.surplus") : isMaximalPercentageGoal ? t("goals.dashboard.moreSpace") : t("goals.dashboard.deficit")
          }`;
};

export const PolicyGoalChart = ({ goal, isPDF = false }: Props) => {
    const isNumericGoal = goal.goalType === "NUMBER";
    const isMaximalPercentageGoal = goal.goalDirection === "MAXIMAL" && goal.goalType === "PERCENTAGE";
    const amount = isNumericGoal ? goal.amount ?? 0 : goal.percentage ?? 0;
    const equalGoalandAmount = amount === goal.goal;
    const isSurplus = amount > goal.goal;
    const isBigScreen = useMediaQuery("(min-width:1800px)");
    const { filledWidth, remainingWidth } = calculateWidths(isNumericGoal, isSurplus, amount, goal.goal, isPDF);
    const difference = Math.abs(goal.goal - amount);
    const formattedDifference = formatDifference(goal.goalType, difference);
    const styles = getStyles(equalGoalandAmount, isSurplus, isMaximalPercentageGoal);
    const text = getText(isNumericGoal, isSurplus, String(formattedDifference), isMaximalPercentageGoal, isBigScreen);

    return (
        <Stack width="100%">
            <Box sx={styles.goalBox}>{`${goal.name} ${t(`goals.goalType.direction.${goal.goalDirection}`)} ${goal.goal}${isNumericGoal ? "" : "%"}`}</Box>
            <Stack direction="row" alignItems="center" width="100%" marginBottom="17px">
                <Box sx={{ ...styles.filledBox, width: `${filledWidth}%` }}>{isNumericGoal ? amount : `${amount.toFixed(2)}%`}</Box>
                {!equalGoalandAmount && <Box sx={{ ...styles.remainingBox, width: `${remainingWidth}%` }}>{text}</Box>}
            </Stack>
        </Stack>
    );
};
