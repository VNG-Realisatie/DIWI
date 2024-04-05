import { Stack, Typography } from "@mui/material";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import { useTranslation } from "react-i18next";
import { useCallback, useContext, useEffect, useState } from "react";
import { CustomPropertyType, addCustomProperty, getCustomProperties } from "../../api/adminSettingServices";
import { ObjectType, PropertyType } from "../../types/enums";
import AlertContext from "../../context/AlertContext";
import { CreatePropertyDialog } from "./CreatePropertyDialog";
import { CustomPropertiesTable } from "./CustomPropertiesTable";
import { components } from "../../types/schema";

export const rowStyle = {
    p: 1,
    border: "solid 1px #BDBDBD",
};
export const Settings = () => {
    const [openDialog, setOpenDialog] = useState(false);
    const [customProperties, setCustomProperties] = useState<CustomPropertyType[]>([]);
    const [selectedObjectType, setSelectedObjectType] = useState<ObjectType>("PROJECT");
    const [selectedPropertyType, setSelectedPropertyType] = useState<PropertyType>("TEXT");
    const [active, setActive] = useState(false);
    const [name, setName] = useState<string>("");
    const [categories, setCategories] = useState<components["schemas"]["SelectDisabledModel"][]>([]);

    const { t } = useTranslation();
    const { setAlert } = useContext(AlertContext);

    const handleSave = useCallback(() => {
        const newProperty = {
            name,
            objectType: selectedObjectType,
            propertyType: selectedPropertyType,
            disabled: !active,
            categories:
                categories !== null
                    ? categories.map((c) => {
                          return c;
                      })
                    : undefined,
        };
        addCustomProperty(newProperty).then(() => {
            setAlert(t("admin.settings.notifications.successfullySaved"), "success");
            setOpenDialog(false);
            getCustomProperties().then((customProperties) => setCustomProperties(customProperties));

            setName("");
            setSelectedObjectType("PROJECT");
            setSelectedPropertyType("TEXT");
            setActive(false);
            setCategories([]);
        });
    }, [active, categories, name, selectedObjectType, selectedPropertyType, setAlert, t]);

    useEffect(() => {
        getCustomProperties().then((customProperties) => setCustomProperties(customProperties));
    }, []);

    useEffect(() => {
        selectedPropertyType !== "CATEGORY" && setCategories([]);
    }, [selectedPropertyType]);
    return (
        <Stack mt={2} mb={5} mx={2} pb={3}>
            <Typography fontWeight={600}>{t("admin.settings.title")}</Typography>
            <CustomPropertiesTable customProperties={customProperties} setCustomProperties={setCustomProperties} />
            <Stack direction="row" alignItems="center" mt={1} sx={{ cursor: "pointer" }} onClick={() => setOpenDialog(true)}>
                <AddCircleIcon color="info" sx={{ fontSize: "40px" }} />
                {t("admin.settings.add")}
            </Stack>
            <CreatePropertyDialog
                openDialog={openDialog}
                setOpenDialog={setOpenDialog}
                name={name}
                setName={setName}
                selectedObjectType={selectedObjectType}
                setSelectedObjectType={setSelectedObjectType}
                selectedPropertyType={selectedPropertyType}
                setSelectedPropertyType={setSelectedPropertyType}
                active={active}
                setActive={setActive}
                handleSave={handleSave}
                categories={categories ? categories : []}
                setCategories={setCategories}
            />
        </Stack>
    );
};
