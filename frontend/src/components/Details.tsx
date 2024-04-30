import { Box, List, ListItem, ListItemText, Stack, Typography } from "@mui/material";
import { Fragment, ReactNode, useContext } from "react";
import { useTranslation } from "react-i18next";
import { Project } from "../api/projectsServices";
import { OrganizationUserAvatars } from "./OrganizationUserAvatars";
import { HouseBlock } from "../types/houseBlockTypes";
import HouseBlockContext from "../context/HouseBlockContext";

type Props = {
    project: Project | null;
};

const DetailListItem = ({ children, property }: { children: ReactNode; property: string }) => {
    const { t } = useTranslation();
    return (
        <>
            <ListItem
                sx={{
                    backgroundColor: "#738092",
                    color: "#FFFFFF",
                    border: "solid 1px #ddd",
                }}
            >
                <ListItemText primary={t(property)} />
            </ListItem>
            <ListItem
                sx={{
                    minHeight: "50px",
                    border: "solid 1px #ddd",
                }}
            >
                {children}
            </ListItem>
        </>
    );
};

export const Details = ({ project }: Props) => {
    const { t } = useTranslation();
    const { houseBlocks } = useContext(HouseBlockContext);

    const getTranslatedText = (property: string, content: string) => {
        if (property === "confidentialityLevel") {
            return t(`projectTable.confidentialityLevelOptions.${content}`);
        }
        if (property === "planType") {
            return t(`projectTable.planTypeOptions.${content}`);
        }
        if (property === "projectPhase") {
            return t(`projectTable.projectPhaseOptions.${content}`);
        }
        if (property === "planningPlanStatus") {
            return t(`projectTable.planningPlanStatus.${content}`);
        } else {
            return content;
        }
    };

    return (
        <List
            sx={{
                padding: 0,
                bgcolor: "background.paper",
                width: "100%",
            }}
        >
            {project &&
                Object.entries(project).map(([property, value]) => {
                    if (property === "totalValue" || property === "projectPhase" || property === "planType") {
                        return (
                            <Fragment key={property}>
                                <ListItem
                                    sx={{
                                        width: "100%",
                                        backgroundColor: "#738092",
                                        color: "#FFFFFF",
                                        border: "solid 1px #ddd",
                                    }}
                                >
                                    <ListItemText primary={t(property)} />
                                </ListItem>
                                <ListItem
                                    sx={{
                                        minHeight: "50px",
                                        border: "solid 1px #ddd",
                                    }}
                                >
                                    {value !== null && typeof value === "number" && <ListItemText primary={getTranslatedText(property, value.toString())} />}
                                    {value !== null && typeof value === "string" && <ListItemText primary={getTranslatedText(property, value)} />}
                                    {value !== null && typeof value === "object" && <Typography>{value.toString().split(",").join(", ")}</Typography>}
                                </ListItem>
                            </Fragment>
                        );
                    }
                    return <Fragment key={property} />;
                })}
            <DetailListItem property="projectOwners">
                <OrganizationUserAvatars organizations={project?.projectOwners} />
            </DetailListItem>
            {houseBlocks &&
                houseBlocks.map((hb: HouseBlock) => {
                    return (
                        <Stack key={hb.houseblockId}>
                            <Typography sx={{ color: "#FFFFFF", backgroundColor: "#00A9F3", px: 2, py: 1.5 }}>{hb.houseblockName}</Typography>
                            <Box border="solid 1px #DDD" px={2} py={1.5}>
                                <Typography>{hb.mutation.amount}</Typography>
                            </Box>
                        </Stack>
                    );
                })}
        </List>
    );
};
