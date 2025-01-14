import { ThemeProvider } from "@mui/material";
import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import "dayjs/locale/nl";
import { PropsWithChildren } from "react";
import { MemoryRouter } from "react-router-dom";
import { dateFormats } from "../localization";
import { theme } from "../theme";
import { CustomPropertyStoreProvider } from "../context/CustomPropertiesProvider";

// This file is used to wrap components in the TestComponentWrapper to provide the correct context for the component to function correctly in a test environment.
const TestComponentWrapper = ({ children }: PropsWithChildren) => {
    return (
        <ThemeProvider theme={theme}>
            <CustomPropertyStoreProvider>
                <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="nl" dateFormats={dateFormats}>
                    <MemoryRouter>{children}</MemoryRouter>
                </LocalizationProvider>
            </CustomPropertyStoreProvider>
        </ThemeProvider>
    );
};

export default TestComponentWrapper;
