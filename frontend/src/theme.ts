import { createTheme } from "@mui/material/styles";
import { nlNL } from "@mui/material/locale";
import type {} from "@mui/x-data-grid/themeAugmentation";

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
            MuiOutlinedInput: {
                styleOverrides: {
                    root: {
                        "& .MuiInputBase-input.Mui-disabled": {
                            WebkitTextFillColor: "#000000",
                            backgroundColor: "#F0F0F0",
                        },
                    },
                },
            },
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
            MuiChip: {
                styleOverrides: {
                    root: {
                        "&.Mui-disabled": {
                            opacity: 1,
                        },
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
                        backgroundColor: "#ffffff",
                        height: "30px",
                    },
                },
            },
            MuiDataGrid: {
                styleOverrides: {
                    cell: {
                        borderLeft: "2px solid #ccc",
                        borderTop: "2px solid #ccc",
                    },
                    columnHeader: {
                        borderLeft: "2px solid #ccc",
                        borderTop: "2px solid #ccc",
                        backgroundColor: "#738092",
                        color: "#ffffff",
                        "& svg": {
                            color: "#ffffff",
                        },
                    },
                    sortIcon: {
                        color: "#ffffff",
                    },
                },
            },
            MuiTooltip: {
                styleOverrides: {
                    tooltip: {
                        maxWidth: 500,
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
