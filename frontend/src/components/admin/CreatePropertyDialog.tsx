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
import { ChangeEvent } from "react";
import { ObjectType, PropertyType } from "../../types/enums";
import { CategoryCreateOption } from "./CategoryCreateOption";
import { propertyType } from "../../types/enums";
import { objectType } from "../../types/enums";
import { components } from "../../types/schema";
import InfoIcon from "@mui/icons-material/Info";
import { OrdinalCategoryCreateOption } from "./OrdinalCategoryCreateOption";

type Props = {
    openDialog: boolean;
    setOpenDialog: (openDialog: boolean) => void;
    name: string;
    setName: (name: string) => void;
    selectedObjectType: ObjectType;
    setSelectedObjectType: (selectedObjectType: ObjectType) => void;
    selectedPropertyType: PropertyType;
    setSelectedPropertyType: (selectedPropertyType: PropertyType) => void;
    active: boolean;
    setActive: (active: boolean) => void;
    handleSave: () => void;
    categories: components["schemas"]["SelectDisabledModel"][];
    setCategories: (categories: components["schemas"]["SelectDisabledModel"][]) => void;
    ordinals: components["schemas"]["OrdinalSelectDisabledModel"][];
    setOrdinalCategories: (ordinals: components["schemas"]["OrdinalSelectDisabledModel"][]) => void;
};
export const CreatePropertyDialog = ({
    openDialog,
    setOpenDialog,
    name,
    setName,
    selectedObjectType,
    setSelectedObjectType,
    selectedPropertyType,
    setSelectedPropertyType,
    active,
    setActive,
    handleSave,
    categories,
    setCategories,
    ordinals,
    setOrdinalCategories,
}: Props) => {
    return (
        <Dialog open={openDialog} onClose={() => setOpenDialog(false)} fullWidth>
            <DialogTitle id="alert-dialog-title"> {t("admin.settings.add")}</DialogTitle>
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
                    {selectedPropertyType === "CATEGORY" && <OrdinalCategoryCreateOption categoryValue={categories} setCategoryValue={setCategories} />}
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
