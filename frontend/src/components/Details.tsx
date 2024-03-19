import { List, ListItem, ListItemText, Typography } from "@mui/material";
import { Fragment, ReactNode } from "react";
import { useTranslation } from "react-i18next";
import { Project } from "../api/projectsServices";
import { OrganizationUserAvatars } from "./OrganizationUserAvatars";

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
                bgcolor: "background.paper",
                width: "100%",
            }}
        >
            {project &&
                Object.entries(project).map(([property, value]) => {
                    //To avoid mixed data type bug keep this. it will be removed after all data defined
                    if (property === "start datum") {
                        console.log(value);
                    }
                    if (property === "totalValue" || property === "projectPhase" || property === "planType") {
                        console.log(value);
                        return (
                            <Fragment key={property}>
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
                                        border: "solid 1px #ddd",
                                    }}
                                >
                                    {/* Temporary hack, should show all data when we start working on map */}
                                    {typeof value === "number" && <ListItemText primary={getTranslatedText(property, value.toString())} />}
                                    {typeof value === "string" && <ListItemText primary={getTranslatedText(property, value)} />}
                                    {typeof value === "object" && <Typography>{value.toString().split(",").join(", ")}</Typography>}
                                </ListItem>
                            </Fragment>
                        );
                    }
                    return <Fragment key={property} />;
                })}
            <DetailListItem property="projectOwners">
                <OrganizationUserAvatars organizations={project?.projectOwners} />
            </DetailListItem>
        </List>
    );
};
