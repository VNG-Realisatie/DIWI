import { Stack, Typography } from "@mui/material";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import { useTranslation } from "react-i18next";
import { useContext, useState } from "react";
import { CustomPropertyStoreType } from "../../api/adminSettingServices";
import { CustomPropertiesTable } from "./CustomPropertiesTable";
import PropertyDialog from "./PropertyDialog";
import UserContext from "../../context/UserContext";
import { useCustomPropertyStore } from "../../context/CustomPropertiesContext";

// eslint-disable-next-line react-refresh/only-export-components
export const rowStyle = {
    p: 1,
    border: "solid 1px #BDBDBD",
};
export const Settings = () => {
    const [openDialog, setOpenDialog] = useState(false);
    const { t } = useTranslation();

    const { allowedActions } = useContext(UserContext);
    // const { customProperties, fetchCustomProperties, addCustomProperty }: CustomPropertyStoreType = useCustomPropertyStore();

    return (
        <Stack mt={2} mb={5} mx={2} pb={3}>
            <Typography fontWeight={600}>{t("admin.settings.title")}</Typography>
            <CustomPropertiesTable />
            <Stack direction="row" alignItems="center" mt={1}>
                {allowedActions.includes("EDIT_CUSTOM_PROPERTIES") && (
                    <>
                        <AddCircleIcon color="info" sx={{ fontSize: "40px", cursor: "pointer" }} onClick={() => setOpenDialog(true)} />
                        {t("admin.settings.add")}
                    </>
                )}
            </Stack>
            <PropertyDialog openDialog={openDialog} setOpenDialog={setOpenDialog} />
        </Stack>
    );
};
