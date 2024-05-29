import { Link, Stack, Typography } from "@mui/material";
import { Footer } from "../components/Footer";
import { t } from "i18next";
import * as Paths from "../Paths";

export const Forbidden = () => {
    return (
        <Stack justifyContent={"space-evenly"} height={"100vh"}>
            <Typography variant="h3" align="center">
                {t("generic.forbidden")}
            </Typography>
            <Typography variant="h4" align="center">
                {t("generic.nodiwiaccount")}
            </Typography>
            <Link href={Paths.logout.path} alignSelf={"center"}>
                <Typography variant="h3">{t("generic.logout")}</Typography>
            </Link>
            <Footer />
        </Stack>
    );
};
