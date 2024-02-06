import { Box, FormControl, FormControlLabel, Popover, Stack, Switch, Typography } from "@mui/material";
import Search from "../components/Search";
import { ProjectList } from "../components/ProjectList";
import { useContext, useState } from "react";
import { Stack } from "@mui/material";
import { ReactComponent as Map } from "../assets/temp/map.svg";
import { ProjectsTableView } from "../components/ProjectsTableView";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import { useLocation, useNavigate } from "react-router-dom";
import * as Paths from "../Paths";
import { DateCalendar } from "@mui/x-date-pickers";
import dayjs from "dayjs";
import NetherlandsMap from "../components/map/NetherlandsMap";
import { dummyMapData } from "./ProjectDetail";
import BreadcrumbBar from "../components/header/BreadcrumbBar";
import { useTranslation } from "react-i18next";

export const Projects = () => {
    const navigate = useNavigate();
    const location = useLocation();

    const { t } = useTranslation();

    return (
        <Stack direction="column" justifyContent="space-between" position="relative" border="solid 1px #ddd" mb={10}>
            <BreadcrumbBar
                pageTitle={t("projects.title")}
                links={[
                    { title: t("projects.map"), link: Paths.projects.path },
                    { title: t("projects.table"), link: Paths.projectsTable.path },
                ]}
            />
            <Stack direction="row" justifyContent="flex-end" alignItems="center" border="solid 1px #ddd" p={0.5}>
                <AddCircleIcon color="info" sx={{ fontSize: "45px", cursor: "pointer" }} onClick={() => navigate(Paths.projectAdd.path)} />
            </Stack>
            {(location.pathname === Paths.projects.path || location.pathname === Paths.root.path) && <Map style={{ width: "100%" }} />}
            {location.pathname === Paths.projectsTable.path && <ProjectsTableView />}
        </Stack>
    );
};
