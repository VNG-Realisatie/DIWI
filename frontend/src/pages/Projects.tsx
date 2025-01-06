import { Stack } from "@mui/material";

import { useLocation } from "react-router-dom";
import * as Paths from "../Paths";

import { useTranslation } from "react-i18next";
import ProjectOverviewMap from "../components/map/ProjectOverviewMap";
import BreadcrumbBar from "../components/header/BreadcrumbBar";
import ProjectsTableWrapper from "../components/project/ProjectTableWrapper";

export const Projects = () => {
    const location = useLocation();

    const { t } = useTranslation();

    return (
        <Stack direction="column" justifyContent="space-between" position="relative" mb={10}>
            <BreadcrumbBar
                pageTitle={t("projects.title")}
                links={[
                    { title: t("projects.map"), link: Paths.projects.path },
                    { title: t("projects.table"), link: Paths.projectsTable.path },
                ]}
            />
            {(location.pathname === Paths.projects.path || location.pathname === Paths.root.path) && <ProjectOverviewMap />}
            {location.pathname === Paths.projectsTable.path && <ProjectsTableWrapper redirectPath={Paths.projectsTable.toPath()} />}
        </Stack>
    );
};
