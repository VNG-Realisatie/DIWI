import { List, ListItem, ListItemText } from "@mui/material";
import { Project } from "../api/projectsServices";

type Props = {
    project: Project | null;
};
export const Details = ({ project }: Props) => {
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
                                    {typeof value === "string" ? <ListItemText primary={value} /> : null}
                                </ListItem>
                            </>
                        );
                    }
                    return <></>;
                })}
        </List>
    );
};
