import { Avatar, IconButton, Menu, MenuItem, Stack, Toolbar } from "@mui/material";
import MenuIcon from "@mui/icons-material/Menu";
import { styled } from "@mui/material/styles";
import MuiAppBar, { AppBarProps as MuiAppBarProps } from "@mui/material/AppBar";

import { drawerWidth } from "../theme";
import { useState } from "react";

import { diwiFetch } from "../utils/requests";
import * as Paths from "../Paths";
import { useNavigate } from "react-router-dom";

type Props = {
    open: boolean;
    handleDrawerOpen: () => void;
};
interface AppBarProps extends MuiAppBarProps {
    open?: boolean;
}

const AppBar = styled(MuiAppBar, {
    shouldForwardProp: (prop) => prop !== "open",
})<AppBarProps>(({ theme, open }) => ({
    transition: theme.transitions.create(["margin", "width"], {
        easing: theme.transitions.easing.sharp,
        duration: theme.transitions.duration.leavingScreen,
    }),
    ...(open && {
        width: `calc(100% - ${drawerWidth}px)`,
        marginLeft: `${drawerWidth}px`,
        transition: theme.transitions.create(["margin", "width"], {
            easing: theme.transitions.easing.easeOut,
            duration: theme.transitions.duration.enteringScreen,
        }),
    }),
}));
export const Header = ({ open, handleDrawerOpen }: Props) => {
    const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);

    const navigate = useNavigate();
    const handleClick = (event: React.MouseEvent<HTMLElement>) => {
        setAnchorEl(event.currentTarget);
    };

    const handleClose = () => {
        setAnchorEl(null);
    };

    const handleLogout = () => {
        setAnchorEl(null);
        diwiFetch(Paths.logout.path)
            .then((res) => navigate(Paths.logout.path))
            .catch((err) => console.log(err));
    };
    const openProfile = Boolean(anchorEl);
    return (
        <AppBar open={open}>
            <Toolbar>
                <IconButton color="default" aria-label="open drawer" onClick={handleDrawerOpen} edge="start" sx={{ mr: 2, ...(open && { display: "none" }) }}>
                    <MenuIcon />
                </IconButton>
                <Stack width="100%" direction="row" alignItems="center" justifyContent="flex-end">
                    <IconButton
                        onClick={handleClick}
                        size="small"
                        aria-controls={openProfile ? "profile-menu" : undefined}
                        aria-haspopup="true"
                        aria-expanded={openProfile ? "true" : undefined}
                    >
                        <Avatar sx={{ width: 35, height: 35, cursor: "pointer" }}>AD</Avatar>
                    </IconButton>
                    <Menu
                        id="profile-menu"
                        anchorEl={anchorEl}
                        open={openProfile}
                        onClose={handleClose}
                        anchorOrigin={{
                            vertical: "bottom",
                            horizontal: "right",
                        }}
                        transformOrigin={{
                            vertical: "top",
                            horizontal: "right",
                        }}
                    >
                        <MenuItem onClick={handleLogout}>Logout</MenuItem>
                    </Menu>
                </Stack>
            </Toolbar>
        </AppBar>
    );
};
