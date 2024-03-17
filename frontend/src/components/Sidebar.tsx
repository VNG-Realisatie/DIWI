import { Divider, Drawer, List, ListItemButton, ListItemText, Typography } from "@mui/material";
import { styled, useTheme } from "@mui/material/styles";
import IconButton from "@mui/material/IconButton";
import CloseIcon from "@mui/icons-material/Close";
import ChevronRightIcon from "@mui/icons-material/ChevronRight";
import { menuProjects } from "../widgets/constants";
import { Link } from "react-router-dom";

import * as Paths from "../Paths";
import { useTranslation } from "react-i18next";

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
    return (
        <Drawer variant="persistent" anchor="left" open={open}>
            <DrawerHeader>
                <Typography sx={{ fontSize: "20px", fontWeight: "700" }} ml={1}>
                    {/* This will come from backend ignore for now  */}
                    Voorne aan Zee
                </Typography>
                <IconButton onClick={handleDrawerClose}>
                    {theme.direction === "ltr" ? <CloseIcon sx={{ color: "#FFFFFF" }} /> : <ChevronRightIcon sx={{ color: "#FFFFFF" }} />}
                </IconButton>
            </DrawerHeader>
            <Divider />

            <List sx={{ ml: 3 }}>
                <Typography sx={{ fontSize: "20px", fontWeight: "600" }}>{t("sidebar.projects")}</Typography>
                {menuProjects.map((text, index) => (
                    <Link key={index} to={`/${text.url}`} style={{ color: "#FFFFFF", textDecoration: "none" }}>
                        <ListItemButton onClick={handleDrawerClose}>
                            <ListItemText primary={text.text} />
                        </ListItemButton>
                    </Link>
                ))}
            </List>
            <List sx={{ ml: 3 }}>
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
            </List>
            <List sx={{ ml: 3 }}>
                <Typography sx={{ fontSize: "20px", fontWeight: "600" }}>{t("sidebar.dataExchange")}</Typography>
                <Link to={Paths.exchangedata.path} style={{ color: "#FFFFFF", textDecoration: "none" }}>
                    <ListItemButton>
                        <ListItemText primary="Data uitwisselen" />
                    </ListItemButton>
                </Link>
            </List>
            <List sx={{ ml: 3 }}>
                <Typography sx={{ fontSize: "20px", fontWeight: "600" }}>{t("sidebar.settings")}</Typography>
                <Link to={Paths.userSettings.path} style={{ color: "#FFFFFF", textDecoration: "none" }}>
                    <ListItemButton>
                        <ListItemText primary="Gebruiker Instellingen " />
                    </ListItemButton>
                </Link>
            </List>
        </Drawer>
    );
};
