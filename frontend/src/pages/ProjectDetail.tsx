import { Box, Stack, Tooltip, Typography } from "@mui/material";
import { useContext, useState, createContext, PropsWithChildren, useEffect } from "react";
import ProjectContext from "../context/ProjectContext";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import { useNavigate } from "react-router-dom";
import * as Paths from "../Paths";
import BreadcrumbBar from "../components/header/BreadcrumbBar";
import { useTranslation } from "react-i18next";
import DeleteForeverOutlinedIcon from "@mui/icons-material/DeleteForeverOutlined";
import DeleteProjectDialog from "../components/project/DeleteProjectDialog";
import { getProjectTimeline } from "../api/projectTimeLine";

export const dummyMapData = [
    {
        projectColor: "orange",
        projectName: "test-01",
        coordinate: [52.1434, 5.0013],
    },
    {
        projectColor: "tomato",
        projectName: "test-02",
        coordinate: [52.2434, 5.2013],
    },
    {
        projectColor: "green",
        projectName: "test-03",
        coordinate: [52.5434, 5.5013],
    },
];

const ProjectColorContext = createContext({
    selectedProjectColor: "",
    setSelectedProjectColor: (color: string) => {},
});

export const ProjectDetail = ({ children }: PropsWithChildren) => {
    const { selectedProject, id } = useContext(ProjectContext);
    const navigate = useNavigate();
    const { t } = useTranslation();
    const [projectData, setProjectData] = useState(null);
    const [error, setError] = useState(null);
    const [selectedProjectColor, setSelectedProjectColor] = useState<string>("");

    const [isDeleteConfirmationOpen, setDeteleConfirmationOpen] = useState<boolean>(false);

    useEffect(() => {
        if (id) {
            getProjectTimeline(id)
                .then((timelineData) => setProjectData(timelineData))
                .catch((error) => {
                    setError(error);
                });
        }
    }, [id]);

    console.log(projectData);
    return (
        <Stack direction="column" justifyContent="space-between" position="relative" border="solid 1px #ddd" mb={10}>
            <BreadcrumbBar
                pageTitle={t("projectDetail.title")}
                links={[
                    { title: t("projectDetail.map"), link: Paths.projectDetail.path.replace(":id", id ?? "1") },
                    { title: t("projectDetail.characteristics"), link: Paths.projectDetailCharacteristics.path.replace(":id", id ?? "1") },
                    { title: t("projectDetail.timeline"), link: Paths.projectDetailTimeline.path.replace(":id", id ?? "1") },
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
            <Stack direction="row" justifyContent="flex-end" border="solid 1px #ddd" p={0.5}>
                <Box sx={{ cursor: "pointer" }} onClick={() => navigate(Paths.projectAdd.path)}>
                    <AddCircleIcon color="info" sx={{ fontSize: "45px" }} />
                </Box>
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
