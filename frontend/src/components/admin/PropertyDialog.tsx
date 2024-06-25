import React, { ChangeEvent, useContext, useEffect, useState } from "react";
import { Button, Dialog, DialogActions, DialogContent, DialogTitle, InputLabel, MenuItem, Select, Stack, TextField, Tooltip } from "@mui/material";
import InfoIcon from "@mui/icons-material/Info";
import { useTranslation } from "react-i18next";
import AlertContext from "../../context/AlertContext";
import {
    getCustomProperty,
    updateCustomProperty,
    addCustomProperty,
    CategoryType,
    OrdinalCategoryType,
    Property,
    getCustomProperties,
} from "../../api/adminSettingServices";
import { ObjectType, PropertyType } from "../../types/enums";
import { objectType } from "../../types/enums";
import { propertyType } from "../../types/enums";
import { CategoryCreateOption } from "./CategoryCreateOption";

interface Props {
    openDialog: boolean;
    setOpenDialog: (openDialog: boolean) => void;
    id?: string;
    setCustomProperties: (cp: Property[]) => void;
}

const PropertyDialog: React.FC<Props> = ({ openDialog, setOpenDialog, id, setCustomProperties }) => {
    const [selectedObjectType, setSelectedObjectType] = useState<ObjectType>("PROJECT");
    const [selectedPropertyType, setSelectedPropertyType] = useState<PropertyType>("TEXT");
    const [active, setActive] = useState(false);
    const [name, setName] = useState<string>("");
    const [categories, setCategories] = useState<CategoryType[]>([]);
    const [ordinals, setOrdinalCategories] = useState<OrdinalCategoryType[]>([]);
    const { setAlert } = useContext(AlertContext);
    const { t } = useTranslation();

    const updateDialog = (property: Property): void => {
        setName(property.name);
        property.categories && setCategories(property.categories);
        property.ordinals && setOrdinalCategories(property.ordinals);
        setActive(property.disabled);
        setSelectedObjectType(property.objectType);
        setSelectedPropertyType(property.propertyType);
    };

    useEffect(() => {
        if (id) {
            getCustomProperty(id).then(updateDialog);
        }
    }, [id]);

    const resetForm = () => {
        setName("");
        setSelectedObjectType("PROJECT");
        setSelectedPropertyType("TEXT");
        setActive(false);
        setCategories([]);
        setOrdinalCategories([]);
    };

    const saveAction = async (newProperty: Property) => {
        try {
            if (!id) resetForm();
            const savedProperty = await (id ? updateCustomProperty(id, newProperty) : addCustomProperty(newProperty));
            setAlert(t("admin.settings.notifications.successfullySaved"), "success");
            updateDialog(savedProperty);
            const customProperties = await getCustomProperties();
            setCustomProperties(customProperties);
            setOpenDialog(false);
        } catch (error: unknown) {
            if (error instanceof Error) setAlert(error.message, "warning");
        } finally {
            setCategories([]);
            setOrdinalCategories([]);
        }
    };

    const handleSave = () => {
        const newProperty: Property = {
            id,
            name,
            type: "CUSTOM",
            objectType: selectedObjectType,
            propertyType: selectedPropertyType,
            disabled: active,
            categories: selectedPropertyType === "CATEGORY" && categories !== null ? categories : undefined,
            ordinals: selectedPropertyType === "ORDINAL" && ordinals !== null ? ordinals : undefined,
        };

        saveAction(newProperty);
    };

    const handleClose = () => {
        setCategories([]);
        setOrdinalCategories([]);
        setOpenDialog(false);
    };

    return (
        <Dialog open={openDialog} onClose={handleClose} fullWidth>
            <DialogTitle id="alert-dialog-title">{id ? t("admin.settings.edit") : t("admin.settings.add")}</DialogTitle>
            <DialogContent>
                <Stack spacing={1.5}>
                    <InputLabel variant="standard" id="name">
                        {t("admin.settings.tableHeader.name")}
                    </InputLabel>
                    <TextField
                        size="small"
                        label={t("admin.settings.tableHeader.name")}
                        value={name}
                        onChange={(e: ChangeEvent<HTMLInputElement>) => setName(e.target.value)}
                    />
                    <InputLabel variant="standard" id="objectType">
                        {t("admin.settings.tableHeader.objectType")}
                    </InputLabel>
                    <Select
                        size="small"
                        disabled={!!id}
                        value={selectedObjectType}
                        labelId="objectType"
                        onChange={(e) => setSelectedObjectType(e.target.value as ObjectType)}
                    >
                        {objectType.map((object) => (
                            <MenuItem key={object} value={object}>
                                {object}
                            </MenuItem>
                        ))}
                    </Select>
                    <Stack direction="row" alignItems="center">
                        <InputLabel variant="standard" id="propertyType">
                            {t("admin.settings.tableHeader.propertyType")}
                        </InputLabel>
                        <Tooltip title={t("admin.settings.propertyTypeInfo")}>
                            <InfoIcon sx={{ fontSize: "20px", color: "#394048" }} />
                        </Tooltip>
                    </Stack>
                    <Select
                        size="small"
                        disabled={!!id}
                        value={selectedPropertyType}
                        labelId="propertyType"
                        onChange={(e) => setSelectedPropertyType(e.target.value as PropertyType)}
                    >
                        {propertyType.map((property) => (
                            <MenuItem key={property} value={property}>
                                {t(`admin.settings.propertyType.${property}`)}
                            </MenuItem>
                        ))}
                    </Select>
                    {selectedPropertyType === "CATEGORY" && <CategoryCreateOption categoryValue={categories} setCategoryValue={setCategories} />}
                    {selectedPropertyType === "ORDINAL" && (
                        <CategoryCreateOption
                            categoryValue={ordinals ? ordinals : []}
                            setCategoryValue={(value) => {
                                const refinedCategoryValue = value.map((item) => ("level" in item ? item : { ...item, level: 1 }));
                                setOrdinalCategories(refinedCategoryValue);
                            }}
                            ordered={true}
                        />
                    )}
                </Stack>
            </DialogContent>
            <DialogActions>
                <Button variant="contained" color="error" onClick={handleClose}>
                    {t("generic.cancel")}
                </Button>
                <Button variant="contained" color="success" onClick={handleSave} autoFocus>
                    {t("generic.save")}
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default PropertyDialog;
