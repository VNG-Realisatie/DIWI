import React, { ChangeEvent, useCallback, useContext, useEffect, useState } from "react";
import { Alert, Button, Dialog, DialogActions, DialogContent, DialogTitle, InputLabel, MenuItem, Select, Stack, TextField, Tooltip } from "@mui/material";
import InfoIcon from "@mui/icons-material/Info";
import { useTranslation } from "react-i18next";
import AlertContext from "../../context/AlertContext";
import { getCustomProperty, updateCustomProperty, CategoryType, OrdinalCategoryType, Property, CustomPropertyStoreType } from "../../api/adminSettingServices";
import { ObjectType, PropertyType } from "../../types/enums";
import { objectType } from "../../types/enums";
import { CategoryCreateOption } from "./CategoryCreateOption";
import { getDuplicatedPropertyInfo } from "../../utils/getDuplicatedPropertyInfo";
import PropertyTypeSelect from "./PropertyTypeSelect";
import PropertyCheckboxGroup from "./PropertyCheckboxGroup";
import { observer } from "mobx-react-lite";
import { useCustomPropertyStore } from "../../context/CustomPropertiesContext";

interface Props {
    openDialog: boolean;
    setOpenDialog: (openDialog: boolean) => void;
    id?: string;
    setId?: (id: string) => void;
}

const PropertyDialog: React.FC<Props> = observer(({ openDialog, setOpenDialog, id, setId }) => {
    const [selectedObjectType, setSelectedObjectType] = useState<ObjectType>("PROJECT");
    const [selectedPropertyType, setSelectedPropertyType] = useState<PropertyType>("TEXT");
    const [active, setActive] = useState(false);
    const [untranslatedName, setUntranslatedName] = useState<string>("");
    const [displayedName, setDisplayedName] = useState<string>("");
    const [categories, setCategories] = useState<CategoryType[]>([]);
    const [ordinals, setOrdinalCategories] = useState<OrdinalCategoryType[]>([]);
    const [propertyDuplicationInfo, setPropertyDuplicationInfo] = useState<{ duplicatedStatus: boolean; duplicatedName: string }>();
    const { setAlert } = useContext(AlertContext);
    const { t } = useTranslation();
    const [activeProperty, setActiveProperty] = useState<Property>();
    const [mandatory, setMandatory] = useState(false);
    const [singleSelect, setSingleSelect] = useState(false);
    const { addCustomProperty }: CustomPropertyStoreType = useCustomPropertyStore();

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
            property.mandatory && setMandatory(property.mandatory);
            property.singleSelect && setSingleSelect(property.singleSelect);
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
            setMandatory,
            setSingleSelect,
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
        setActive(false);
        setMandatory(false);
        setSingleSelect(false);
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
            }
            const savedProperty = await (id ? updateCustomProperty(id, newProperty) : addCustomProperty(newProperty));
            setAlert(t("admin.settings.notifications.successfullySaved"), "success");
            updateDialog(savedProperty);
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
            mandatory,
            singleSelect: selectedPropertyType === "CATEGORY" ? singleSelect : undefined,
        };

        saveAction(newProperty);
    };

    useEffect(() => {
        const filteredCategories = categories.filter((category) => !category.disabled);
        const duplicated = getDuplicatedPropertyInfo(filteredCategories);
        setPropertyDuplicationInfo(duplicated);
    }, [categories]);

    useEffect(() => {
        const filteredOrdinals = ordinals.filter((ordinal) => !ordinal.disabled);
        const duplicated = getDuplicatedPropertyInfo(filteredOrdinals);
        setPropertyDuplicationInfo(duplicated);
    }, [ordinals]);

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
                    <PropertyTypeSelect selectedPropertyType={selectedPropertyType} setSelectedPropertyType={setSelectedPropertyType} disabled={!!id} />

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
                    <PropertyCheckboxGroup
                        mandatory={mandatory}
                        setMandatory={setMandatory}
                        singleSelect={singleSelect}
                        setSingleSelect={setSingleSelect}
                        selectedPropertyType={selectedPropertyType}
                    />
                    {propertyDuplicationInfo?.duplicatedStatus && (
                        <Alert severity="error">{propertyDuplicationInfo?.duplicatedName + " " + t("admin.settings.duplicatedOption")}</Alert>
                    )}
                </Stack>
            </DialogContent>
            <DialogActions>
                <Button id="cancel-custom-property" variant="contained" color="error" onClick={handleClose}>
                    {t("generic.cancel")}
                </Button>
                <Button
                    id="save-custom-property"
                    variant="contained"
                    color="success"
                    onClick={handleSave}
                    autoFocus
                    disabled={propertyDuplicationInfo?.duplicatedStatus}
                >
                    {t("generic.save")}
                </Button>
            </DialogActions>
        </Dialog>
    );
});

export default PropertyDialog;
