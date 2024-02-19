import { Box, Stack, Typography } from "@mui/material";
import { useContext } from "react";
import ProjectContext from "../context/ProjectContext";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import { useLocation, useNavigate } from "react-router-dom";
import * as Paths from "../Paths";
import { colorArray } from "../api/dummyData";
import BreadcrumbBar from "../components/header/BreadcrumbBar";
import { useTranslation } from "react-i18next";
import ProjectTimeline from "../components/ProjectTimeline";
import { useState, useEffect } from "react";
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

export const ProjectDetail = () => {
    const { selectedProject, id } = useContext(ProjectContext);
    const navigate = useNavigate();
    const location = useLocation();
    const { t } = useTranslation();
    const [projectData, setProjectData] = useState(null);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (id) {
            getProjectTimeline(id)
                .then((timelineData) => setProjectData(timelineData))
                .catch((error) => {
                    setError(error);
                });
        }
    }, [id]);

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
                    backgroundColor: id && colorArray[parseInt(id) - 1],
                    color: "#FFFFFF",
                    minHeight: "53px",
                }}
            >
                <Typography variant="h5">{selectedProject?.projectName}</Typography>
            </Stack>
            <Stack direction="row" justifyContent="flex-end" border="solid 1px #ddd" p={0.5}>
                <Box sx={{ cursor: "pointer" }} onClick={() => navigate(Paths.projectAdd.path)}>
                    <AddCircleIcon color="info" sx={{ fontSize: "45px" }} />
                </Box>
            </Stack>
            <ProjectTimeline />
        </Stack>
    );
};
