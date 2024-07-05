import { Button, Dialog, DialogActions, DialogContent, DialogTitle } from "@mui/material";
import TextInput from "../project/inputs/TextInput";
import { Property, updateCustomProperty } from "../../api/adminSettingServices";
import useAlert from "../../hooks/useAlert";
import { t } from "i18next";
import { useState } from "react";
import RangeNumberInput from "../project/inputs/RangeNumberInput";

type RangeNumber = {
    value: number | null;
    min: number | null;
    max: number | null;
};

type Props = {
    open: boolean;
    setOpen: (open: boolean) => void;
    id: string | undefined;
    propertyName: string;
    setRangeCategories: (rangeCategories: Property[]) => void;
};

const PriceCategoriesDialog = ({ open, setOpen, id, propertyName, setRangeCategories }: Props) => {
    const [name, setName] = useState<string>("");
    const [rangeValue, setRangeValue] = useState<RangeNumber>({ value: null, min: null, max: null });
    const { setAlert } = useAlert();

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
            setAlert(t("admin.settings.notifications.successfullySaved"), "success");
        } catch (error: unknown) {
            if (error instanceof Error) setAlert(error.message, "warning");
        } finally {
            handleClose();
        }
    };

    const handleSave = () => {
        const newProperty: any = {
            name: propertyName,
            type: "FIXED",
            objectType: "WONINGBLOK",
            propertyType: "RANGE_CATEGORY",
            ranges: [
                {
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
        setName("");
        setRangeValue({ value: null, min: null, max: null });
        setOpen(false);
    };

    return (
        <Dialog open={open} onClose={() => setOpen(false)} maxWidth="sm" fullWidth>
            <DialogTitle>Add Price Category</DialogTitle>
            <DialogContent>
                {/* <TextInput/> */}
                <TextInput
                    value={name}
                    setValue={(e: React.ChangeEvent<HTMLInputElement>) => setName(e.target.value)}
                    readOnly={false}
                    mandatory={true}
                    errorText="This field is required"
                    title="Name"
                />
                <RangeNumberInput
                    value={rangeValue}
                    updateCallBack={handleRangeValueUpdate}
                    isMonetary={true}
                    readOnly={false}
                    mandatory={true}
                    title="Enter the price range"
                    errorText="This field is required"
                />
            </DialogContent>
            <DialogActions>
                <Button onClick={handleClose} color="primary">
                    Cancel
                </Button>
                <Button onClick={handleSave} disabled={!name} color="primary">
                    Save
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default PriceCategoriesDialog;
