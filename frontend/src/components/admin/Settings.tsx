import { Stack, Typography } from "@mui/material";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import { useTranslation } from "react-i18next";
import { useCallback, useContext, useEffect, useState } from "react";
import { Property, addCustomProperty, getCustomProperties } from "../../api/adminSettingServices";
import { ObjectType, PropertyType } from "../../types/enums";
import AlertContext from "../../context/AlertContext";
import { CustomPropertiesTable } from "./CustomPropertiesTable";
import { components } from "../../types/schema";
import PropertyDialog from "./PropertyDialog";

export const rowStyle = {
    p: 1,
    border: "solid 1px #BDBDBD",
};
export const Settings = () => {
    const [openDialog, setOpenDialog] = useState(false);
    const [customProperties, setCustomProperties] = useState<Property[]>([]);
    const [selectedObjectType, setSelectedObjectType] = useState<ObjectType>("PROJECT");
    const [selectedPropertyType, setSelectedPropertyType] = useState<PropertyType>("TEXT");
    const [active, setActive] = useState(false);
    const [name, setName] = useState<string>("");
    const [categories, setCategories] = useState<components["schemas"]["SelectDisabledModel"][]>([]);
    const [ordinals, setOrdinalCategories] = useState<components["schemas"]["OrdinalSelectDisabledModel"][]>([]);

    const { t } = useTranslation();
    const { setAlert } = useContext(AlertContext);

    const handleSave = useCallback(() => {
        const newProperty: Property = {
            name,
            type: "CUSTOM",
            objectType: selectedObjectType,
            propertyType: selectedPropertyType,
            disabled: active,
            categories:
                categories !== null
                    ? categories.map((c) => {
                          return c;
                      })
                    : undefined,
            ordinals:
                ordinals !== null
                    ? ordinals.map((oc) => {
                          console.log(oc);
                          return oc;
                      })
                    : undefined,
        };
        console.log("newPropertySettings", newProperty);
        addCustomProperty(newProperty)
            .then(() => {
                setAlert(t("admin.settings.notifications.successfullySaved"), "success");
                setOpenDialog(false);
                getCustomProperties().then((customProperties) => setCustomProperties(customProperties));

                setName("");
                setSelectedObjectType("PROJECT");
                setSelectedPropertyType("TEXT");
                setActive(false);
                setCategories([]);
                setOrdinalCategories([]);
            })
            .catch((error) => setAlert(error.message, "warning"));
    }, [active, categories, ordinals, name, selectedObjectType, selectedPropertyType, setAlert, t]);

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
