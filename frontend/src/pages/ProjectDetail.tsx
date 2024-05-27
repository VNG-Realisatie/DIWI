import { Accordion, AccordionDetails, AccordionSummary, Box, Stack, Typography } from "@mui/material";
import { useContext, useState, createContext, PropsWithChildren } from "react";
import ProjectContext from "../context/ProjectContext";

import * as Paths from "../Paths";
import BreadcrumbBar from "../components/header/BreadcrumbBar";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import { useTranslation } from "react-i18next";
import { DeleteButtonWithConfirm } from "../components/DeleteButtonWithConfirm";
import { deleteProject } from "../api/projectsServices";
import { useNavigate } from "react-router-dom";
import useAllowedActions from "../hooks/useAllowedActions";

const ProjectColorContext = createContext({
    selectedProjectColor: "",
    setSelectedProjectColor: (color: string) => {},
});

export const ProjectDetail = ({ children }: PropsWithChildren) => {
    const { selectedProject, projectId, updateProjects } = useContext(ProjectContext);
    const { t } = useTranslation();
    const navigate = useNavigate();
    const [selectedProjectColor, setSelectedProjectColor] = useState<string>("");
    const allowedActions = useAllowedActions();

    return (
        <Stack direction="column" justifyContent="space-between" position="relative" border="solid 1px #ddd" mb={10}>
            <BreadcrumbBar
                pageTitle={t("projectDetail.title")}
                links={[
                    { title: t("projectDetail.map"), link: Paths.projectDetail.toPath({ projectId: projectId || "" }) },
                    { title: t("projectDetail.characteristics"), link: Paths.projectDetailCharacteristics.toPath({ projectId: projectId || "" }) },
                    // { title: t("projectDetail.timeline"), link: Paths.projectDetailTimeline..toPath({ ":projectId": projectId || "" }) },
                ]}
            />
            <Accordion sx={{ width: "100%" }} key={projectId} disableGutters defaultExpanded>
                <AccordionSummary
                    sx={{ backgroundColor: selectedProjectColor ? selectedProjectColor : selectedProject?.projectColor, color: "#ffffff" }}
                    expandIcon={<ExpandMoreIcon sx={{ color: "#ffffff" }} />}
                    aria-controls="panel1-content"
                    id="panel1-header"
                >
                    <Typography variant="h5">{selectedProject?.projectName}</Typography>
                    <Box sx={{ marginLeft: "auto", marginTop: "5px", marginRight: "20px" }}>
                        {selectedProject && allowedActions.includes("CREATE_NEW_PROJECT") && (
                            <DeleteButtonWithConfirm
                                typeAndName={`${t("generic.project")} ${selectedProject.projectName}`}
                                iconColor={"#FFFFFF"}
                                deleteFunction={() => deleteProject(projectId ?? null)}
                                afterDelete={() => {
                                    updateProjects();
                                    navigate("/projects/table");
                                }}
                            />
                        )}
                    </Box>
                </AccordionSummary>
                <AccordionDetails sx={{ padding: 0 }}>
                    <ProjectColorContext.Provider
                        value={{
                            selectedProjectColor,
                            setSelectedProjectColor,
                        }}
                    >
                        {children}
                    </ProjectColorContext.Provider>
                </AccordionDetails>
            </Accordion>
        </Stack>
    );
};

export default ProjectColorContext;
