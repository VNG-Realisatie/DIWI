import { Stack, Typography } from "@mui/material";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import { useTranslation } from "react-i18next";
import { useEffect, useState } from "react";
import { Property, getCustomProperties } from "../../api/adminSettingServices";
import { CustomPropertiesTable } from "./CustomPropertiesTable";
import PropertyDialog from "./PropertyDialog";

export const rowStyle = {
    p: 1,
    border: "solid 1px #BDBDBD",
};
export const Settings = () => {
    const [openDialog, setOpenDialog] = useState(false);
    const [customProperties, setCustomProperties] = useState<Property[]>([]);
    const { t } = useTranslation();

    useEffect(() => {
        getCustomProperties().then((customProperties) => setCustomProperties(customProperties));
    }, []);

    return (
        <Stack mt={2} mb={5} mx={2} pb={3}>
            <Typography fontWeight={600}>{t("admin.settings.title")}</Typography>
            <CustomPropertiesTable customProperties={customProperties} setCustomProperties={setCustomProperties} />
            <Stack direction="row" alignItems="center" mt={1}>
                <AddCircleIcon color="info" sx={{ fontSize: "40px", cursor: "pointer" }} onClick={() => setOpenDialog(true)} />
                {t("admin.settings.add")}
            </Stack>
            <PropertyDialog openDialog={openDialog} setOpenDialog={setOpenDialog} setCustomProperties={setCustomProperties} />
        </Stack>
    );
};
