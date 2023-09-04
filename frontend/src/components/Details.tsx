import { List, ListItem, ListItemText } from "@mui/material";
import { ProjectType } from "../context/ProjectContext";

type Props = {
    project: ProjectType|undefined;
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
                    if(property==="start datum"){
                        console.log(value)
                    }
                    if (
                        property !== "organization_id" &&
                        property !== "project_state_id" &&
                        property !== "organization_state_id" &&
                        property !== "project_fase_changelog_id" &&
                        property !== "project_gemeenterol_value_id"&&
                        property !== "project_name_changelog_id"&&
                        property !== "project_priorisering_value_id"&&
                        property !== "project_gemeenterol_changelog_id"&&
                        property !== "project_priorisering_changelog_id" &&
                        property !== "project_gemeenterol_value_state_id" &&
                        property !== "project_priorisering_value_state_id"
                    ) {
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
                                    <ListItemText primary={property==="start datum"||property==="eind datum"?"":value} />
                                </ListItem>
                            </>
                        );
                    }
                    return <></>;
                })}
        </List>
    );
};
