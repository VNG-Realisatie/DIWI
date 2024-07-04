import React, { ChangeEvent, useCallback, useContext, useEffect, useState } from "react";
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
    setId?: (id: string) => void;
}

const PropertyDialog: React.FC<Props> = ({ openDialog, setOpenDialog, id, setCustomProperties, setId }) => {
    const [selectedObjectType, setSelectedObjectType] = useState<ObjectType>("PROJECT");
    const [selectedPropertyType, setSelectedPropertyType] = useState<PropertyType>("TEXT");
    const [active, setActive] = useState(false);
    const [untranslatedName, setUntranslatedName] = useState<string>("");
    const [displayedName, setDisplayedName] = useState<string>("");
    const [categories, setCategories] = useState<CategoryType[]>([]);
    const [ordinals, setOrdinalCategories] = useState<OrdinalCategoryType[]>([]);
    const [activeProperty, setActiveProperty] = useState<Property>();
    const { setAlert } = useContext(AlertContext);
    const { t } = useTranslation();

    const updateDialog = useCallback(
        (property: Property): void => {
            setActiveProperty(property);
            setUntranslatedName(property.name);
            if (property.type === "FIXED") {
                setDisplayedName(t(`admin.settings.fixedPropertyType.${property.name}`));
            } else {
                setDisplayedName(property.name);
            }
            property.categories && setCategories(property.categories);
            property.ordinals && setOrdinalCategories(property.ordinals.sort((a, b) => a.level - b.level));
            setActive(property.disabled);
            setSelectedObjectType(property.objectType);
            setSelectedPropertyType(property.propertyType);
        },
        [
            setActiveProperty,
            setUntranslatedName,
            setDisplayedName,
            setCategories,
            setOrdinalCategories,
            setActive,
            setSelectedObjectType,
            setSelectedPropertyType,
            t,
        ],
    );

    useEffect(() => {
        if (id) {
            getCustomProperty(id).then(updateDialog);
        }
    }, [id, updateDialog]);

    const clearFields = () => {
        setDisplayedName("");
        setUntranslatedName("");
        setCategories([]);
        setOrdinalCategories([]);
        setSelectedObjectType("PROJECT");
        setSelectedPropertyType("TEXT");
    };

    const handleClose = () => {
        setId && setId("");
        clearFields();
        setOpenDialog(false);
    };

    const saveAction = async (newProperty: Property) => {
        try {
            if (!id) {
                clearFields();
                setActive(false);
            }
            const savedProperty = await (id ? updateCustomProperty(id, newProperty) : addCustomProperty(newProperty));
            setAlert(t("admin.settings.notifications.successfullySaved"), "success");
            updateDialog(savedProperty);
            const customProperties = await getCustomProperties();
            setCustomProperties(customProperties);
        } catch (error: unknown) {
            if (error instanceof Error) setAlert(error.message, "warning");
        } finally {
            handleClose();
        }
    };

    const handleSave = () => {
        const newProperty: Property = {
            id,
            name: untranslatedName,
            type: "CUSTOM",
            objectType: selectedObjectType,
            propertyType: selectedPropertyType,
            disabled: active,
            categories: selectedPropertyType === "CATEGORY" && categories !== null ? categories : undefined,
            ordinals:
                selectedPropertyType === "ORDINAL" && ordinals.length > 0
                    ? ordinals.map(({ id, level, name, disabled }) => ({ id, level, name, disabled }))
                    : undefined,
        };

        saveAction(newProperty);
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
                        value={displayedName}
                        onChange={(e: ChangeEvent<HTMLInputElement>) => {
                            setDisplayedName(e.target.value);
                            setUntranslatedName(e.target.value);
                        }}
                        disabled={activeProperty?.type === "FIXED"}
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
                                const refinedCategoryValue = value.map((item, index) => ({ ...item, level: index }));
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
