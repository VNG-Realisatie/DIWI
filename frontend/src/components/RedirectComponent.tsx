import { Box, CircularProgress, Typography } from "@mui/material";
import { t } from "i18next";

const RedirectComponent = () => {
    return (
        <Box display="flex" flexDirection="column" alignItems="center" justifyContent="center" height="100vh">
            <CircularProgress />
            <Typography variant="h6" mt={2}>
               {t("exchangeData.arcgis.authIsHappening")}
            </Typography>
        </Box>
    );
};

export default RedirectComponent;
