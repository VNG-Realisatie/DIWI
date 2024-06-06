import { Paper, Typography } from "@mui/material";
import { t } from "i18next";

const ImportNotAllowed = () => {
    return (
        <Paper style={{ padding: "20px", margin: "20px" }}>
            <Typography variant="h6">{t("exchangeData.importForbidden")}.</Typography>
        </Paper>
    );
};

export default ImportNotAllowed;
