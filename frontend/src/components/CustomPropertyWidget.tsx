import { Autocomplete, TextField } from "@mui/material";
import { Property } from "../api/adminSettingServices";
import { CustomPropertyValue } from "../api/customPropServices";
import { useTranslation } from "react-i18next";

type Props = {
    readOnly: boolean;
    customValue: CustomPropertyValue | undefined;
    customDefinition: Property;
    setCustomValue: (newValue: CustomPropertyValue) => void;
};

export const CustomPropertyWidget = ({ readOnly, customValue, setCustomValue, customDefinition }: Props) => {
    const { t } = useTranslation();
    const trueishLabel = t("generic.true");
    const falsyLabel = t("generic.false");

    function booleanToLabel(value: boolean) {
        return value === true ? trueishLabel : falsyLabel;
    }

    if (customDefinition.propertyType === "BOOLEAN") {
        return (
            <Autocomplete
                size="small"
                disabled={readOnly}
                sx={{
                    "& .MuiInputBase-input.Mui-disabled": {
                        backgroundColor: "#0000", // set 0 opacity when disabled
                    },
                }}
                options={[trueishLabel, falsyLabel]}
                value={customValue?.booleanValue !== undefined ? booleanToLabel(customValue.booleanValue) : ""}
                onChange={(_, newValue) => {
                    const booleanValue = newValue === trueishLabel ? true : newValue === falsyLabel ? false : undefined;
                    setCustomValue({ ...customValue, booleanValue });
                }}
                renderInput={(params) => <TextField {...params} size="small" />}
            />
        );
    } else if (customDefinition.propertyType === "CATEGORY") {
        const values = customValue?.categories?.map((val) => customDefinition.categories?.find((d) => val === d.id));
        return (
            <Autocomplete
                size="small"
                disabled={readOnly}
                sx={{
                    "& .MuiInputBase-input.Mui-disabled": {
                        backgroundColor: "#0000", // set 0 opacity when disabled
                    },
                }}
                options={customDefinition.categories || []}
                getOptionLabel={(option) => option?.name || ""}
                value={values}
                multiple
                onChange={(_, newValue) => setCustomValue({ ...customValue, categories: newValue.map((c) => c?.id as string) })}
                renderInput={(params) => <TextField {...params} size="small" />}
            />
        );
    } else if (customDefinition.propertyType === "ORDINAL") {
        const value = customDefinition.ordinals?.find((d) => customValue?.ordinals?.value?.includes(d.id as string));
        return (
            <Autocomplete
                size="small"
                disabled={readOnly}
                sx={{
                    "& .MuiInputBase-input.Mui-disabled": {
                        backgroundColor: "#0000", // set 0 opacity when disabled
                    },
                }}
                options={customDefinition.ordinals?.filter((oc) => !oc.disabled).sort((a, b) => a.level - b.level) || []}
                getOptionLabel={(option) => option?.name || ""}
                value={value}
                onChange={(_, newValue) => setCustomValue({ ...customValue, ordinals: { value: newValue?.id as string } })}
                renderInput={(params) => <TextField {...params} size="small" sx={{ minWidth: "200px" }} />}
            />
        );
    } else if (customDefinition.propertyType === "NUMERIC") {
        return (
            <TextField
                fullWidth
                variant="outlined"
                size="small"
                disabled={readOnly}
                sx={{
                    "& .MuiInputBase-input.Mui-disabled": {
                        backgroundColor: "#0000", // set 0 opacity when disabled
                    },
                }}
                type="number"
                value={customValue?.numericValue?.value || 0}
                onChange={(e) => setCustomValue({ ...customValue, numericValue: { value: parseFloat(e.target.value) } })}
            />
        );
    } else if (customDefinition.propertyType === "TEXT") {
        return (
            <TextField
                fullWidth
                variant="outlined"
                size="small"
                disabled={readOnly}
                sx={{
                    "& .MuiInputBase-input.Mui-disabled": {
                        backgroundColor: "#0000", // set 0 opacity when disabled
                    },
                }}
                value={customValue?.textValue || ""}
                onChange={(e) => setCustomValue({ ...customValue, textValue: e.target.value })}
            />
        );
        // TODO add ORDINAL!
    } else {
        return null;
    }
};
