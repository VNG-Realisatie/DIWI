import { Box, List, ListItem, ListItemText } from "@mui/material";
import * as Paths from "../Paths";
import { Link } from "react-router-dom";
type Props = {
    projectList: Array<{ id: number; name: string; color: string }>;
};
export const ProjectList = ({ projectList }: Props) => {
    return (
        <List
            sx={{
                bgcolor: "background.paper",
                width: "100%",
            }}
        >
            {projectList.map((project) => {
                return (
                    <Link
                        to={`${Paths.projects.path}/${project.id}`}
                        key={project.id}
                        style={{ textDecoration: "none", color: "black" }}
                    >
                        <ListItem
                            sx={{
                                border: "solid 1px #ddd",
                                transition: "background-color 0.3s", // Adding a smooth transition effect
                                "&:hover": {
                                    backgroundColor: "#f5f5f5", // Background color when hovering
                                },
                            }}
                        >
                            <Box
                                sx={{
                                    width: "12px",
                                    height: "12px",
                                    backgroundColor: project.color,
                                    borderRadius: "50%",
                                }}
                                mr={1}
                            />

                            <ListItemText primary={project.name} />
                        </ListItem>
                    </Link>
                );
            })}
        </List>
    );
};
