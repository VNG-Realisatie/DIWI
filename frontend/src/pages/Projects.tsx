import { Stack } from "@mui/material";

import { ProjectsTableView } from "../components/project/ProjectsTableView";
import { useLocation } from "react-router-dom";
import * as Paths from "../Paths";
import { dummyMapData } from "./ProjectDetail";

import BreadcrumbBar from "../components/header/BreadcrumbBar";
import { useTranslation } from "react-i18next";
import NetherlandsMap from "../components/map/NetherlandsMap";

export const Projects = () => {
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
            {(location.pathname === Paths.projects.path || location.pathname === Paths.root.path) && (
                <NetherlandsMap height="70vh" width="100%" mapData={dummyMapData} plusButton={true} />
            )}
            {location.pathname === Paths.projectsTable.path && <ProjectsTableView />}
        </Stack>
    );
};
