import { Divider, Drawer, List, ListItemButton, ListItemText, Typography } from "@mui/material";
import { styled, useTheme } from "@mui/material/styles";
import IconButton from "@mui/material/IconButton";
import CloseIcon from "@mui/icons-material/Close";
import ChevronRightIcon from "@mui/icons-material/ChevronRight";
import { projectMenuItems } from "../widgets/constants";
import { Link } from "react-router-dom";

import * as Paths from "../Paths";
import { useTranslation } from "react-i18next";
import { useContext } from "react";
import ConfigContext from "../context/ConfigContext";
import useAllowedActions from "../hooks/useAllowedActions";

type SideBarProps = {
    open: boolean;
    handleDrawerClose: () => void;
};

const DrawerHeader = styled("div")(({ theme }) => ({
    display: "flex",
    alignItems: "center",
    padding: theme.spacing(0, 1),
    // necessary for content to be below app bar
    ...theme.mixins.toolbar,
    justifyContent: "space-between",
}));

export const SideBar = ({ open, handleDrawerClose }: SideBarProps) => {
    const theme = useTheme();
    const { t } = useTranslation();
    const { municipalityName } = useContext(ConfigContext);
    const allowedActions = useAllowedActions();

    return (
        <Drawer variant="persistent" anchor="left" open={open}>
            <DrawerHeader>
                <Typography sx={{ fontSize: "20px", fontWeight: "700" }} ml={1}>
                    {/* This will come from backend ignore for now  */}
                    {municipalityName}
                </Typography>
                <IconButton onClick={handleDrawerClose}>
                    {theme.direction === "ltr" ? <CloseIcon sx={{ color: "#FFFFFF" }} /> : <ChevronRightIcon sx={{ color: "#FFFFFF" }} />}
                </IconButton>
            </DrawerHeader>
            <Divider />

            <List sx={{ ml: 3 }}>
                <Typography sx={{ fontSize: "20px", fontWeight: "600" }}>{t("sidebar.projects")}</Typography>
                <Link key="Overzicht projecten" to="/projects/table" style={{ color: "#FFFFFF", textDecoration: "none" }}>
                    <ListItemButton onClick={handleDrawerClose}>
                        <ListItemText primary="Overzicht projecten" />
                    </ListItemButton>
                </Link>
                {allowedActions.includes("CREATE_NEW_PROJECT") && (
                    <Link key="Project toevoegen" to="/project/create" style={{ color: "#FFFFFF", textDecoration: "none" }}>
                        <ListItemButton onClick={handleDrawerClose}>
                            <ListItemText primary="Project toevoegen" />
                        </ListItemButton>
                    </Link>
                )}
            </List>
            {/* <List sx={{ ml: 3 }}>
                <Typography sx={{ fontSize: "20px", fontWeight: "600" }}>{t("sidebar.dashboards")}</Typography>

                <Link to={Paths.policygoal.path} style={{ color: "#FFFFFF", textDecoration: "none" }}>
                    <ListItemButton>
                        <ListItemText primary="Beleidsdoelen" />
                    </ListItemButton>
                </Link>
                <Link to={Paths.dashboard.path} style={{ color: "#FFFFFF", textDecoration: "none" }}>
                    <ListItemButton>
                        <ListItemText primary="Dashboard projecten" />
                    </ListItemButton>
                </Link>
            </List>
            <List sx={{ ml: 3 }}>
                <Typography sx={{ fontSize: "20px", fontWeight: "600" }}>{t("sidebar.users")}</Typography>
            </List> */}
            {/* <List sx={{ ml: 3 }}>
                <Typography sx={{ fontSize: "20px", fontWeight: "600" }}>{t("sidebar.dataExchange")}</Typography>
                <Link to={Paths.exchangedata.path} style={{ color: "#FFFFFF", textDecoration: "none" }}>
                    <ListItemButton>
                        <ListItemText primary="Data uitwisselen" />
                    </ListItemButton>
                </Link>
            </List> */}
            <List sx={{ ml: 3 }}>
                <Typography sx={{ fontSize: "20px", fontWeight: "600" }}>{t("sidebar.settings")}</Typography>
                <Link to={Paths.userSettings.path} style={{ color: "#FFFFFF", textDecoration: "none" }}>
                    <ListItemButton>
                        <ListItemText primary={t("customProperties.title")} />
                    </ListItemButton>
                </Link>
            </List>
        </Drawer>
    );
};
