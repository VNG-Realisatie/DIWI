import {
    Dialog,
    DialogTitle,
    DialogContent,
    Stack,
    TextField,
    InputLabel,
    Select,
    SelectChangeEvent,
    MenuItem,
    DialogActions,
    Button,
    Tooltip,
} from "@mui/material";
import { t } from "i18next";
import { ChangeEvent, useContext, useEffect, useState } from "react";
import { ObjectType, PropertyType } from "../../types/enums";
import { CategoryCreateOption } from "./CategoryCreateOption";
import { propertyType } from "../../types/enums";
import { objectType } from "../../types/enums";
import { CategoryType, OrdinalCategoryType, Property, getCustomProperties, getCustomProperty, updateCustomProperty } from "../../api/adminSettingServices";
import AlertContext from "../../context/AlertContext";
import InfoIcon from "@mui/icons-material/Info";
import { OrdinalCategoryCreateOption } from "./OrdinalCategoryCreateOption";

type Props = {
    openDialog: boolean;
    setOpenDialog: (openDialog: boolean) => void;
    id: string;
    setCustomProperties: (cp: Property[]) => void;
};
export const EditPropertyDialog = ({ openDialog, setOpenDialog, id, setCustomProperties }: Props) => {
    const [selectedObjectType, setSelectedObjectType] = useState<ObjectType>("PROJECT");
    const [selectedPropertyType, setSelectedPropertyType] = useState<PropertyType>("TEXT");
    const [active, setActive] = useState(false);
    const [name, setName] = useState<string>("");
    const [categories, setCategories] = useState<CategoryType[]>([]);
    const [ordinals, setOrdinalCategories] = useState<OrdinalCategoryType[]>([]);
    const { setAlert } = useContext(AlertContext);
    useEffect(() => {
        if (id) {
            getCustomProperty(id).then((property) => {
                console.log("property", property);
                console.log("property.ordinals", property.ordinals);
                setName(property.name);
                property.categories && setCategories(property.categories);
                property.ordinals && setOrdinalCategories(property.ordinals);
                setActive(!property.disabled);
                setSelectedObjectType(property.objectType);
                setSelectedPropertyType(property.propertyType);
            });
        }
    }, [id]);

    const handleSave = () => {
        const newProperty: Property = {
            id,
            name,
            type: "CUSTOM",
            objectType: selectedObjectType,
            propertyType: selectedPropertyType,
            disabled: !active,
            categories:
                categories !== null
                    ? categories.map((c) => {
                          return c;
                      })
                    : undefined,
            ordinals:
                ordinals !== null
                    ? ordinals.map((oc) => {
                          return oc;
                      })
                    : undefined,
        };
        console.log("newProperty", newProperty);
        updateCustomProperty(id, newProperty).then(() => {
            setAlert(t("admin.settings.notifications.successfullySaved"), "success");
            getCustomProperties().then((customProperties) => setCustomProperties(customProperties));
            setOpenDialog(false);
        });
    };

    return (
        <Dialog open={openDialog} onClose={() => setOpenDialog(false)} fullWidth>
            <DialogTitle id="alert-dialog-title"> {t("admin.settings.edit")}</DialogTitle>
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
                        disabled
                        value={selectedObjectType}
                        labelId="objectType"
                        onChange={(e: SelectChangeEvent<typeof selectedObjectType>) => setSelectedObjectType(e.target.value as ObjectType)}
                    >
                        {objectType.map((object) => {
                            return (
                                <MenuItem key={object} value={object}>
                                    {object}
                                </MenuItem>
                            );
                        })}
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
                        disabled
                        value={selectedPropertyType}
                        labelId="propertyType"
                        onChange={(e: SelectChangeEvent<typeof selectedPropertyType>) => setSelectedPropertyType(e.target.value as PropertyType)}
                    >
                        {propertyType.map((property) => {
                            return (
                                <MenuItem key={property} value={property}>
                                    {t(`admin.settings.propertyType.${property}`)}
                                </MenuItem>
                            );
                        })}
                    </Select>
                    {selectedPropertyType === "CATEGORY" && (
                        <OrdinalCategoryCreateOption categoryValue={categories ? categories : []} setCategoryValue={setCategories} />
                    )}
                    {selectedPropertyType === "ORDINAL" && (
                        <OrdinalCategoryCreateOption
                            categoryValue={ordinals ? ordinals : []}
                            setCategoryValue={(value) => {
                                const refinedCategoryValue = value.map((item) => ("level" in item ? item : { ...item, level: 0 }));
                                setOrdinalCategories(refinedCategoryValue);
                            }}
                        />
                    )}
                </Stack>
            </DialogContent>
            <DialogActions>
                <Button variant="contained" color="error" onClick={() => setOpenDialog(false)}>
                    {t("generic.cancel")}
                </Button>
                <Button variant="contained" color="success" onClick={handleSave} autoFocus>
                    {t("generic.save")}
                </Button>
            </DialogActions>
        </Dialog>
    );
};
