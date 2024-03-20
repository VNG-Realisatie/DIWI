import { createTheme } from "@mui/material/styles";
import { nlNL } from "@mui/material/locale";

declare module "@mui/material/styles" {
    interface PaletteColor {
        customDarkBlue?: string;
        customLightBlue?: string;
    }
    interface SimplePaletteColorOptions {
        customDarkBlue?: string;
        customLightBlue?: string;
    }
}

export const drawerWidth = 290;

export const theme = createTheme(
    {
        typography: {
            fontFamily: "Inter, Arial",
            h6: {
                fontWeight: 600,
            },
            caption: {
                fontWeight: 500,
                fontSize: "13px",
                lineHeight: "120%",
            },
        },
        components: {
            MuiDrawer: {
                styleOverrides: {
                    paper: {
                        width: drawerWidth,
                        backgroundColor: "#002C64",
                        color: "#FFFFFF",
                        boxSizing: "border-box",
                    },
                },
            },
            MuiAppBar: {
                styleOverrides: {
                    root: {
                        backgroundColor: "#FFFFFF",
                        position: "fixed",
                    },
                },
            },
            MuiListItemIcon: {
                styleOverrides: {
                    root: {
                        color: "#FFFFFF",
                        minWidth: "40px",
                    },
                },
            },
            MuiInputBase: {
                styleOverrides: {
                    input: {
                        backgroundColor: "white",
                    },
                },
            },
        },
        palette: {
            primary: {
                main: "#002C64",
                customDarkBlue: "#002C64",
                customLightBlue: "#00A9F3",
            },
            secondary: {
                main: "#900A0A",
            },
        },
    },
    nlNL,
);
