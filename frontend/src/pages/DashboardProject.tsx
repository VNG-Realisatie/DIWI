import { Grid, Stack, Typography } from "@mui/material";
import BreadcrumbBar from "../components/header/BreadcrumbBar";
import * as Paths from "../Paths";
import { useContext } from "react";
import { useTranslation } from "react-i18next";
import ProjectContext from "../context/ProjectContext";

export const DashboardProject = () => {
    const { t } = useTranslation();
    const { selectedProject } = useContext(ProjectContext);
    return (
        <Stack>
            <BreadcrumbBar pageTitle={t("dashboard.projectTitle")} links={[{ title: `${selectedProject?.projectName}`, link: Paths.dashboard.path }]} />
            <Grid container border="solid 1px #DDD">
                <Grid item xs={12} md={6}>
                    <Typography variant="h6" fontSize={16}>
                        Koop
                    </Typography>
                    {/* ToDo:Add chart here later */}
                </Grid>
                <Grid item xs={12} md={6}>
                    <Typography variant="h6" fontSize={16}>
                        Huur
                    </Typography>
                    {/* ToDo:Add chart here later */}
                </Grid>
            </Grid>
            <Grid container border="solid 1px #DDD">
                <Grid item xs={12} md={6}>
                    <Typography variant="h6" fontSize={16}>
                        Woon Producten
                    </Typography>
                    {/* ToDo:Add chart here later */}
                </Grid>
                <Grid item xs={12} md={6}>
                    <Typography variant="h6" fontSize={16}>
                        Opleveringen
                    </Typography>
                    {/* ToDo:Add chart here later */}
                </Grid>
            </Grid>
            <Grid container border="solid 1px #DDD">
                <Grid item xs={12} md={6}>
                    <Typography variant="h6" fontSize={16}>
                        Vertraagde projecten
                    </Typography>
                </Grid>
                <Grid item xs={12} md={6}>
                    <Typography variant="h6" fontSize={16}>
                        Woonkenmerken
                    </Typography>
                    {/* ToDo:Add chart here later */}
                </Grid>
            </Grid>
            <Grid container border="solid 1px #DDD">
                <Grid item xs={12} md={6}>
                    <Typography variant="h6" fontSize={16}>
                        Verleende vergunningen
                    </Typography>
                    {/* ToDo:Add chart here later */}
                </Grid>
                <Grid item xs={12} md={6}>
                    <Typography variant="h6" fontSize={16}>
                        Doelgroepen
                    </Typography>
                    {/* ToDo:Add chart here later */}
                </Grid>
            </Grid>
        </Stack>
    );
};
