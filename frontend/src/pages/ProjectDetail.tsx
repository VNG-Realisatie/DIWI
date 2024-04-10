import { Stack, Tooltip, Typography } from "@mui/material";
import { useContext, useState, createContext, PropsWithChildren } from "react";
import ProjectContext from "../context/ProjectContext";

import * as Paths from "../Paths";
import BreadcrumbBar from "../components/header/BreadcrumbBar";
import { useTranslation } from "react-i18next";
import DeleteForeverOutlinedIcon from "@mui/icons-material/DeleteForeverOutlined";
import DeleteProjectDialog from "../components/project/DeleteProjectDialog";

const ProjectColorContext = createContext({
    selectedProjectColor: "",
    setSelectedProjectColor: (color: string) => {},
});

export const ProjectDetail = ({ children }: PropsWithChildren) => {
    const { selectedProject, projectId } = useContext(ProjectContext);
    const { t } = useTranslation();
    const [selectedProjectColor, setSelectedProjectColor] = useState<string>("");

    const [isDeleteConfirmationOpen, setDeteleConfirmationOpen] = useState<boolean>(false);

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
            <Stack
                direction="row"
                alignItems="center"
                pl={2}
                sx={{
                    backgroundColor: selectedProjectColor ? selectedProjectColor : selectedProject?.projectColor,
                    color: "#FFFFFF",
                    minHeight: "53px",
                }}
            >
                <Typography variant="h5">{selectedProject?.projectName}</Typography>
                {selectedProject && (
                    <Tooltip placement="top" title={t("generic.delete")}>
                        <DeleteForeverOutlinedIcon
                            sx={{ ml: 3, color: "#FFFFFF", cursor: "pointer" }}
                            onClick={() => {
                                setDeteleConfirmationOpen(!isDeleteConfirmationOpen);
                            }}
                        />
                    </Tooltip>
                )}
                {isDeleteConfirmationOpen && selectedProject && (
                    <DeleteProjectDialog
                        setIsOpen={setDeteleConfirmationOpen}
                        isOpen={isDeleteConfirmationOpen}
                        projectName={selectedProject.projectName}
                        projectId={selectedProject.projectId}
                    />
                )}
            </Stack>

            <ProjectColorContext.Provider
                value={{
                    selectedProjectColor,
                    setSelectedProjectColor,
                }}
            >
                {children}
            </ProjectColorContext.Provider>
        </Stack>
    );
};

export default ProjectColorContext;
