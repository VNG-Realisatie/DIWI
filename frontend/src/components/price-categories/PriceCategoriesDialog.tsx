import { Alert, Button, Dialog, DialogActions, DialogContent, DialogTitle } from "@mui/material";
import TextInput from "../project/inputs/TextInput";
import { CategoryType, Property, updateCustomProperty } from "../../api/adminSettingServices";
import useAlert from "../../hooks/useAlert";
import { t } from "i18next";
import { useContext, useEffect, useState } from "react";
import RangeNumberInput from "../project/inputs/RangeNumberInput";
import { getDuplicatedPropertyInfo } from "../../utils/getDuplicatedPropertyInfo";
import { MAX_INT_LARGER } from "../../utils/houseblocks/houseBlocksFunctions";
import UserContext from "../../context/UserContext";

type RangeNumber = {
    value: number | null;
    min: number | null;
    max: number | null;
};

type Category = {
    id: string;
    name: string;
    min: number | null;
    max?: number;
    disabled: boolean;
};

type Props = {
    open: boolean;
    setOpen: (open: boolean) => void;
    id: string | undefined;
    propertyName: string;
    setRangeCategories: (rangeCategories: Property[]) => void;
    categoryToEdit?: Category | null;
    setCategoryToEdit: (category: Category | null) => void;
    title: string;
    categories: CategoryType[];
};

const PriceCategoriesDialog = ({ open, setOpen, id, propertyName, setRangeCategories, categoryToEdit, setCategoryToEdit, title, categories }: Props) => {
    const [name, setName] = useState<string>("");
    const [rangeValue, setRangeValue] = useState<RangeNumber>({ value: null, min: null, max: null });
    const { setAlert } = useAlert();
    const [propertyDuplicationInfo, setPropertyDuplicationInfo] = useState<{ duplicatedStatus: boolean; duplicatedName: string }>();
    const [isRangeValid, setIsRangeValid] = useState<boolean>(true);
    const { allowedActions } = useContext(UserContext);

    useEffect(() => {
        if (categoryToEdit) {
            setName(categoryToEdit.name);
            setRangeValue({ value: null, min: categoryToEdit.min ?? null, max: categoryToEdit.max ?? null });
        }
    }, [categoryToEdit]);

    useEffect(() => {
        const duplicated = getDuplicatedPropertyInfo(categories);
        setPropertyDuplicationInfo(duplicated);
    }, [categories]);

    useEffect(() => {
        // If categoryToEdit is not null, we should exclude the current category from the list to check for duplication
        const listExcludingCurrent = categoryToEdit ? categories.filter((category) => category.id !== categoryToEdit.id) : categories;

        const listWithCurrentInput = [...listExcludingCurrent, { name: name, id: undefined, disabled: false }];
        const { duplicatedStatus, duplicatedName } = getDuplicatedPropertyInfo(listWithCurrentInput);
        setPropertyDuplicationInfo({ duplicatedStatus, duplicatedName });
    }, [name, categories, categoryToEdit]);

    const handleRangeValueUpdate = (newValue: RangeNumber) => {
        setRangeValue(newValue);
        if (newValue.value) {
            setRangeValue((prevState) => ({
                ...prevState,
                min: newValue.value,
            }));
        }
    };

    const saveAction = async (newProperty: Property) => {
        if (!id || !allowedActions.includes("EDIT_CUSTOM_PROPERTIES")) {
            return;
        }
        try {
            const savedProperty = await updateCustomProperty(id, newProperty);
            setRangeCategories([savedProperty]);
            setAlert(t("admin.priceCategories.successfullySaved"), "success");
        } catch (error: unknown) {
            if (error instanceof Error) setAlert(error.message, "warning");
        } finally {
            handleClose();
        }
    };

    const handleSave = () => {
        const newProperty: Property = {
            id,
            name: propertyName,
            type: "FIXED",
            objectType: "WONINGBLOK",
            propertyType: "RANGE_CATEGORY",
            ranges: [
                {
                    id: categoryToEdit ? categoryToEdit.id : "",
                    name,
                    min: rangeValue.min ?? 0,
                    max: rangeValue.max ?? undefined,
                    disabled: false,
                },
            ],
            disabled: false,
            mandatory: false,
            singleSelect: true,
        };

        saveAction(newProperty);
    };

    const handleClose = () => {
        setOpen(false);
        setName("");
        setRangeValue({ value: null, min: null, max: null });
        setCategoryToEdit(null);
    };

    return (
        <Dialog open={open} onClose={() => setOpen(false)} maxWidth="sm" fullWidth>
            <DialogTitle>{title}</DialogTitle>
            <DialogContent>
                <TextInput
                    value={name}
                    setValue={(e: React.ChangeEvent<HTMLInputElement>) => setName(e.target.value)}
                    readOnly={false}
                    mandatory={true}
                    errorText={t("admin.priceCategories.nameError")}
                    title={t("admin.priceCategories.name")}
                />
                <RangeNumberInput
                    isMonetary={true}
                    setIsRangeValid={setIsRangeValid}
                    value={rangeValue}
                    updateCallBack={handleRangeValueUpdate}
                    readOnly={false}
                    mandatory={true}
                    title={t("admin.priceCategories.amount")}
                    errorText={t("admin.priceCategories.amountError")}
                    maxValue={MAX_INT_LARGER}
                />
            </DialogContent>
            <DialogActions>
                <Button onClick={handleClose} variant="outlined">
                    {t("generic.cancel")}
                </Button>
                <Button onClick={handleSave} disabled={!name || propertyDuplicationInfo?.duplicatedStatus || !isRangeValid} variant="contained">
                    {t("generic.save")}
                </Button>
            </DialogActions>
            {propertyDuplicationInfo?.duplicatedStatus && (
                <Alert severity="error">{propertyDuplicationInfo?.duplicatedName + " " + t("admin.settings.duplicatedOption")}</Alert>
            )}
        </Dialog>
    );
};

export default PriceCategoriesDialog;
