import { Stack, Typography } from "@mui/material";
import { Footer } from "../components/Footer";
import { t } from "i18next";

export const Unauthorized = () => {
    return (
        <Stack justifyContent={"space-around"} height={"100vh"}>
            <Typography variant="h3" align="center">
                {t("generic.unauthorized")}
            </Typography>
            <Footer />
        </Stack>
    );
};
