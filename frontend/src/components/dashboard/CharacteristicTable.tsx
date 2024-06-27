import { Stack, Typography } from "@mui/material";
import { useContext } from "react";
import HouseBlockContext from "../../context/HouseBlockContext";
import ProjectContext from "../../context/ProjectContext";
import { useTranslation } from "react-i18next";
import { calculateTotalYear } from "../../utils/calculateTotalYear";

export const CharacteristicTable = () => {
    const { houseBlocks } = useContext(HouseBlockContext);
    const { selectedProject } = useContext(ProjectContext);
    const { t } = useTranslation();

    const constructionAmount = houseBlocks
        .filter((hb) => hb.mutation.kind === "CONSTRUCTION")
        .map((hb) => hb.mutation.amount ?? 0)
        .reduce((a, b) => a + b, 0);

    const demolitionAmount = houseBlocks
        .filter((hb) => hb.mutation.kind === "DEMOLITION")
        .map((hb) => hb.mutation.amount ?? 0)
        .reduce((a, b) => a + b, 0);

    const netAmount = constructionAmount - demolitionAmount;
    const totalYear = calculateTotalYear(selectedProject?.startDate, selectedProject?.endDate);

    const propertyStyle = { border: "solid 1px #ddd", padding: 1, backgroundColor: "#738092", color: "#FFFFFF", fontWeight: "bold", flex: 1 };
    const valueStyle = { border: "solid 1px #ddd", padding: 1, flex: 1 };

    const characteristicData = [
        {
            property: "constructionAmount",
            value: constructionAmount,
        },
        {
            property: "demolitionAmount",
            value: demolitionAmount,
        },
        {
            property: "netAmount",
            value: netAmount,
        },
        {
            property: "projectTotalTime",
            value: totalYear + " " + t("dashboard.properties.year"),
        },
        {
            property: "projectPhase",
            value: selectedProject?.projectPhase ?? "N/A",
        },
        {
            property: "projectOwners",
            value: selectedProject?.projectOwners.map((po) => po.name).join(", ") ?? "N/A",
        },
    ];
    return (
        <>
            {characteristicData.map((data, i) => {
                return (
                    <Stack key={i + data.property} flexDirection="row" alignItems="center" justifyContent="flex-start">
                        <Typography sx={propertyStyle}>{t(`dashboard.properties.${data.property}`)}</Typography>
                        <Typography sx={valueStyle}>
                            {data.property === "projectPhase" ? t(`projectTable.projectPhaseOptions.${data.value}`) : data.value}
                        </Typography>
                    </Stack>
                );
            })}
        </>
    );
};
