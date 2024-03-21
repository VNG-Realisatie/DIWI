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
    Typography,
    Checkbox,
    DialogActions,
    Button,
} from "@mui/material";
import { t } from "i18next";
import { ChangeEvent, useContext, useEffect, useState } from "react";
import { ObjectType, PropertyType } from "../../types/enums";
import { CategoryCreateOption } from "./CategoryCreateOption";
import { objectType, propertyType } from "./constants";
import { CategoryType, CustomPropertyType, getCustomProperties, getCustomProperty, updateCustomProperty } from "../../api/adminSettingServices";
import AlertContext from "../../context/AlertContext";

type Props = {
    openDialog: boolean;
    setOpenDialog: (openDialog: boolean) => void;
    id: string;
    setCustomProperties: (cp: CustomPropertyType[]) => void;
};
export const EditPropertyDialog = ({ openDialog, setOpenDialog, id, setCustomProperties }: Props) => {
    const [selectedObjectType, setSelectedObjectType] = useState<ObjectType>("PROJECT");
    const [selectedPropertyType, setSelectedPropertyType] = useState<PropertyType>("TEXT");
    const [active, setActive] = useState(false);
    const [name, setName] = useState<string>("");
    const [categories, setCategories] = useState<CategoryType[]>([]);
    const { setAlert } = useContext(AlertContext);
    useEffect(() => {
        if (id) {
            getCustomProperty(id).then((property) => {
                setName(property.name);
                property.categories && setCategories(property.categories);
                setActive(property.disabled);
                setSelectedObjectType(property.objectType);
                setSelectedPropertyType(property.propertyType);
            });
        }
    }, [id]);

    const handleSave = () => {
        const newProperty = {
            id,
            name,
            objectType: selectedObjectType,
            propertyType: selectedPropertyType,
            disabled: !active,
            categories: selectedPropertyType === "CATEGORY" ? categories : undefined,
        };
        updateCustomProperty(id, newProperty).then(() => {
            setAlert(t("admin.settings.notifications.successfullySaved"), "success");
            getCustomProperties().then((customProperties) => setCustomProperties(customProperties));
            setOpenDialog(false);
        });
    };
    return (
        <Dialog open={openDialog} onClose={() => setOpenDialog(false)} fullWidth>
            <DialogTitle id="alert-dialog-title"> {t("admin.settings.add")}</DialogTitle>
            <DialogContent>
                <Stack spacing={2}>
                    <TextField
                        label={t("admin.settings.tableHeader.name")}
                        value={name}
                        onChange={(e: ChangeEvent<HTMLInputElement>) => setName(e.target.value)}
                    />
                    <InputLabel variant="standard" id="objectType">
                        {t("admin.settings.tableHeader.objectType")}
                    </InputLabel>
                    <Select
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
                    <InputLabel variant="standard" id="propertyType">
                        {t("admin.settings.tableHeader.propertyType")}
                    </InputLabel>
                    <Select
                        disabled
                        value={selectedPropertyType}
                        labelId="propertyType"
                        onChange={(e: SelectChangeEvent<typeof selectedPropertyType>) => setSelectedPropertyType(e.target.value as PropertyType)}
                    >
                        {propertyType.map((property) => {
                            return (
                                <MenuItem key={property} value={property}>
                                    {property}
                                </MenuItem>
                            );
                        })}
                    </Select>
                    {selectedPropertyType === "CATEGORY" && (
                        <CategoryCreateOption categoryValue={categories ? categories : []} setCategoryValue={setCategories} />
                    )}
                    <Stack>
                        <Typography>
                            {t("admin.settings.tableHeader.active")}
                            <Checkbox checked={active} onChange={(event: ChangeEvent<HTMLInputElement>) => setActive(event.target.checked)} />
                        </Typography>
                    </Stack>
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
