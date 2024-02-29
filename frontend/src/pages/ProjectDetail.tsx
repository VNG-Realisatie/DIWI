import { Box, Stack, Tooltip, Typography } from "@mui/material";
import { useContext, useState } from "react";
import { Details } from "../components/Details";
import ProjectContext from "../context/ProjectContext";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import { ReactComponent as TimeLineImg } from "../assets/temp/timeline.svg";
import { useLocation, useNavigate } from "react-router-dom";
import * as Paths from "../Paths";
import BreadcrumbBar from "../components/header/BreadcrumbBar";
import { useTranslation } from "react-i18next";
import NetherlandsMap from "../components/map/NetherlandsMap";
import { ProjectsWithHouseBlock } from "../components/project/project-with-house-block/ProjectWithHouseBlock";
import DeleteForeverOutlinedIcon from "@mui/icons-material/DeleteForeverOutlined";
import DeleteProjectDialog from "../components/project/DeleteProjectDialog";

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

export const ProjectDetail = () => {
    const { selectedProject, id } = useContext(ProjectContext);
    const navigate = useNavigate();
    const location = useLocation();
    const { t } = useTranslation();
    const [selectedProjectColor, setSelectedProjectColor] = useState<string>();

    const [isDeleteConfirmationOpen, setDeteleConfirmationOpen] = useState<boolean>(false);

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
                    <Tooltip placement="top" title={t("Delete project")}>
                        {/* change tooltip title */}
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
            {location.pathname === Paths.projectDetail.path.replace(":id", id ?? "1") && (
                <Stack direction="row" alignItems="center" justifyContent="space-between">
                    <Stack overflow="auto" height="70vh">
                        {<Details project={selectedProject} />}
                    </Stack>
                    <NetherlandsMap height="66vh" width="100%" mapData={dummyMapData} />
                </Stack>
            )}
            {location.pathname === Paths.projectDetailTimeline.path.replace(":id", id ?? "1") && <TimeLineImg style={{ width: "100%" }} />}

            {/* TO DO add house blocks here later */}
            {location.pathname === Paths.projectDetailCharacteristics.path.replace(":id", id ?? "1") && (
                <ProjectsWithHouseBlock
                    selectedProjectColor={selectedProjectColor ? selectedProjectColor : ""}
                    setSelectedProjectColor={setSelectedProjectColor}
                    // houseblocks={projects.filter((p) => selectedProject && p.project && p.project.id === selectedProject.id)[0].woningblokken}
                />
            )}
        </Stack>
    );
};
