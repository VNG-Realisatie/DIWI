import { Divider, Drawer, List, ListItemButton, ListItemText, Typography } from "@mui/material";
import { styled, useTheme } from "@mui/material/styles";
import IconButton from "@mui/material/IconButton";
import CloseIcon from "@mui/icons-material/Close";
import ChevronRightIcon from "@mui/icons-material/ChevronRight";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useContext } from "react";
import ConfigContext from "../context/ConfigContext";
import useAllowedActions from "../hooks/useAllowedActions";
import { menu, MenuSection } from "./SidebarMenu";

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

const typographyStyles = { fontSize: "20px", fontWeight: "600" };
const linkStyles = { color: "#FFFFFF", textDecoration: "none" };

const filterMenuByPermissions = (menu: MenuSection[], allowedActions: string[]): MenuSection[] => {
    return menu
        .map((section: MenuSection) => {
            const filteredMenuItems = section.menuItems.filter(
                (item) => item.required_permission.length === 0 || item.required_permission.some((perm) => allowedActions.includes(perm)),
            );
            return filteredMenuItems.length > 0 ? { ...section, menuItems: filteredMenuItems } : null;
        })
        .filter((section): section is MenuSection => section !== null);
};

export const SideBar = ({ open, handleDrawerClose }: SideBarProps) => {
    const theme = useTheme();
    const { t } = useTranslation();
    const { municipalityName } = useContext(ConfigContext);
    const { allowedActions } = useAllowedActions();

    const filteredMenu = filterMenuByPermissions(menu, allowedActions);

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

            {filteredMenu.map((section, index) => (
                <List key={index} sx={{ ml: 3 }}>
                    <Typography sx={typographyStyles}>{t(section.header)}</Typography>
                    {section.menuItems.map((item, index) => (
                        <Link key={index} to={item.link} style={linkStyles} onClick={handleDrawerClose}>
                            <ListItemButton>
                                <ListItemText primary={t(item.name)} />
                            </ListItemButton>
                        </Link>
                    ))}
                </List>
            ))}

            <List sx={{ ml: 3, marginTop: "auto", marginBottom: "20px" }}>
                <Link to="https://support.diwi.vng.client.phinion.com/help/nl-nl" target="_blank" style={linkStyles} onClick={handleDrawerClose}>
                    <Typography style={typographyStyles}>{t("sidebar.knowledgeBase")}</Typography>
                </Link>
            </List>
        </Drawer>
    );
};
