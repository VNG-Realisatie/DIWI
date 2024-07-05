import { Button, Dialog, DialogActions, DialogContent, DialogTitle } from "@mui/material";
import TextInput from "../project/inputs/TextInput";
import { Property, updateCustomProperty } from "../../api/adminSettingServices";
import useAlert from "../../hooks/useAlert";
import { t } from "i18next";
import { useEffect, useState } from "react";
import RangeNumberInput from "../project/inputs/RangeNumberInput";

type RangeNumber = {
    value: number | null;
    min: number | null;
    max: number | null;
};

type Category = {
    id: string;
    name: string;
    min: number | null;
    max: number | null;
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
};

const PriceCategoriesDialog = ({ open, setOpen, id, propertyName, setRangeCategories, categoryToEdit, setCategoryToEdit, title }: Props) => {
    const [name, setName] = useState<string>("");
    const [rangeValue, setRangeValue] = useState<RangeNumber>({ value: null, min: null, max: null });
    const { setAlert } = useAlert();

    useEffect(() => {
        if (categoryToEdit) {
            setName(categoryToEdit.name);
            setRangeValue({ value: null, min: categoryToEdit.min, max: categoryToEdit.max });
        }
    }, [categoryToEdit]);

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
        if (!id) {
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
        const newProperty: any = {
            id,
            name: propertyName,
            type: "FIXED",
            objectType: "WONINGBLOK",
            propertyType: "RANGE_CATEGORY",
            ranges: [
                {
                    id: categoryToEdit ? categoryToEdit.id : undefined,
                    name,
                    min: rangeValue.min,
                    max: rangeValue.max,
                    disabled: false,
                },
            ],
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
                {/* <TextInput/> */}
                <TextInput
                    value={name}
                    setValue={(e: React.ChangeEvent<HTMLInputElement>) => setName(e.target.value)}
                    readOnly={false}
                    mandatory={true}
                    errorText={t("admin.priceCategories.nameError")}
                    title={t("admin.priceCategories.name")}
                />
                <RangeNumberInput
                    value={rangeValue}
                    updateCallBack={handleRangeValueUpdate}
                    readOnly={false}
                    mandatory={true}
                    title={t("admin.priceCategories.amount")}
                    errorText={t("admin.priceCategories.amountError")}
                />
            </DialogContent>
            <DialogActions>
                <Button onClick={handleClose} variant="outlined">
                    {t("generic.cancel")}
                </Button>
                <Button onClick={handleSave} disabled={!name || !(rangeValue.value || rangeValue.min)} variant="contained">
                    {t("generic.save")}
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default PriceCategoriesDialog;
