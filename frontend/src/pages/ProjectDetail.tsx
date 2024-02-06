import { Box, Stack, Typography } from "@mui/material";
import { useContext } from "react";
import { Details } from "../components/Details";
import { ReactComponent as Map } from "../assets/temp/map.svg";
import ProjectContext from "../context/ProjectContext";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import { ReactComponent as TimeLineImg } from "../assets/temp/timeline.svg";
import { useLocation, useNavigate } from "react-router-dom";
import * as Paths from "../Paths";
import { colorArray } from "../api/dummyData";
import BreadcrumbBar from "../components/header/BreadcrumbBar";
import { useTranslation } from "react-i18next";

export const ProjectDetail = () => {
    const { selectedProject, id } = useContext(ProjectContext);
    const navigate = useNavigate();
    const location = useLocation();
    const { t } = useTranslation();

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
            {location.pathname === Paths.projectDetail.path.replace(":id", id ?? "1") && (
                <Stack direction="row" alignItems="center" justifyContent="space-between">
                    <Stack overflow="auto" height="70vh">
                        {<Details project={selectedProject} />}
                    </Stack>
                    <Map style={{ width: "100%" }} />
                </Stack>
            )}
            {location.pathname === Paths.projectDetailTimeline.path.replace(":id", id ?? "1") && <TimeLineImg style={{ width: "100%" }} />}

        {/* TO DO add house blocks here later */}
        </Stack>
    );
};
