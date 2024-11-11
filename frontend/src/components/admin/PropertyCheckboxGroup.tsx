import { FormControlLabel, Checkbox } from "@mui/material";
import { useTranslation } from "react-i18next";

type Props = {
    mandatory: boolean;
    setMandatory: (checked: boolean) => void;
    singleSelect: boolean | undefined;
    setSingleSelect: (checked: boolean) => void;
    selectedPropertyType: string;
};

const PropertyCheckboxGroup = ({ mandatory, setMandatory, singleSelect, setSingleSelect, selectedPropertyType }: Props) => {
    const { t } = useTranslation();

    return (
        <>
            <FormControlLabel
                control={<Checkbox checked={mandatory} onChange={(e) => setMandatory(e.target.checked)} />}
                label={t("admin.settings.mandatory")}
            />
            {selectedPropertyType === "CATEGORY" && (
                <FormControlLabel
                    control={<Checkbox checked={singleSelect} onChange={(e) => setSingleSelect(e.target.checked)} />}
                    label={t("admin.settings.singleSelect")}
                />
            )}
        </>
    );
};

export default PropertyCheckboxGroup;
