import { List, ListItem, ListItemText, Typography } from "@mui/material";
import { Project } from "../api/projectsServices";
import { OrganizationUserAvatars } from "./OrganizationUserAvatars";
import { useTranslation } from "react-i18next";

type Props = {
    project: Project | null;
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
                    if (property !== "projectId" && property !== "projectStateId" && property !== "organization_state_id") {
                        return (
                            <>
                                <ListItem
                                    key={property}
                                    sx={{
                                        backgroundColor: "#738092",
                                        color: "#FFFFFF",
                                        border: "solid 1px #ddd",
                                    }}
                                >
                                    <ListItemText primary={property} />
                                </ListItem>
                                <ListItem
                                    sx={{
                                        border: "solid 1px #ddd",
                                    }}
                                >
                                    {/* Temporary hack, should show all data when we start working on map */}
                                    {typeof value === "string" && <ListItemText primary={getTranslatedText(property, value)} />}
                                    {typeof value === "object" &&
                                        (property === "projectOwners" || property === "projectLeaders"
                                            ? //@ts-ignore
                                              value.length > 0 && <OrganizationUserAvatars organizations={value} />
                                            : value.length > 0 && (
                                                  <List>
                                                      {value.map((content) => {
                                                          if (typeof content === "string") {
                                                              return <Typography>{getTranslatedText(property, content)}</Typography>;
                                                          }
                                                          return <></>;
                                                      })}
                                                  </List>
                                              ))}
                                </ListItem>
                            </>
                        );
                    }
                    return <></>;
                })}
        </List>
    );
};
