import { Autocomplete, TextField } from "@mui/material";
import { Property } from "../api/adminSettingServices";
import { CustomPropertyValue } from "../api/customPropServices";
import { useTranslation } from "react-i18next";
import { getCustomValue } from "../utils/formValidation";

type Props = {
    readOnly: boolean;
    customValue: CustomPropertyValue | undefined;
    customDefinition: Property;
    setCustomValue: (newValue: CustomPropertyValue) => void;
};

function hasError(customValue: CustomPropertyValue | undefined, readOnly: boolean, mandatory: boolean): boolean {
    if (readOnly || !mandatory) {
        return false;
    }

    const value = getCustomValue(customValue);

    if (!customValue || value === undefined || value === null || value === "" || value === 0) {
        return true;
    }

    return false;
}

export const CustomPropertyWidget = ({ readOnly, customValue, setCustomValue, customDefinition }: Props) => {
    const { t } = useTranslation();
    const trueishLabel = t("generic.true");
    const falsyLabel = t("generic.false");
    const errorText = t("generic.thisFieldIsRequired");
    const mandatory = customDefinition.mandatory || false;

    function booleanToLabel(value: boolean) {
        return value === true ? trueishLabel : falsyLabel;
    }

    const error = hasError(customValue, readOnly, mandatory);

    if (customDefinition.propertyType === "BOOLEAN") {
        return (
            <Autocomplete
                id="boolean-custom-property"
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
                renderInput={(params) => <TextField {...params} size="small" error={error} helperText={error ? errorText : ""} />}
                isOptionEqualToValue={(option, value) => option === value}
            />
        );
    } else if (customDefinition.propertyType === "CATEGORY") {
        const values = customValue?.categories?.map((val) => customDefinition.categories?.find((d) => val === d.id));
        return (
            <>
                {customDefinition.singleSelect ? (
                    <Autocomplete
                        id="category-custom-property-single"
                        size="small"
                        disabled={readOnly}
                        sx={{
                            "& .MuiInputBase-input.Mui-disabled": {
                                backgroundColor: "#0000", // set 0 opacity when disabled
                            },
                        }}
                        options={customDefinition.categories || []}
                        getOptionLabel={(option) => option?.name || ""}
                        value={values ? values[0] : null}
                        onChange={(_, newValue) =>
                            setCustomValue({ ...customValue, categories: newValue ? [newValue.id].filter((id): id is string => id !== undefined) : [] })
                        }
                        renderInput={(params) => <TextField {...params} size="small" error={error} helperText={error ? errorText : ""} />}
                        isOptionEqualToValue={(option, value) => !!value && !!option && option.id === value.id}
                    />
                ) : (
                    <Autocomplete
                        id="category-custom-property-multiple"
                        size="small"
                        disabled={readOnly}
                        sx={{
                            "& .MuiInputBase-input.Mui-disabled": {
                                backgroundColor: "#0000", // set 0 opacity when disabled
                            },
                        }}
                        options={customDefinition.categories || []}
                        getOptionLabel={(option) => option?.name || ""}
                        value={values ? values : []}
                        multiple
                        onChange={(_, newValue) => setCustomValue({ ...customValue, categories: newValue.map((c) => c?.id as string) })}
                        renderInput={(params) => <TextField {...params} size="small" error={error} helperText={error ? errorText : ""} />}
                        isOptionEqualToValue={(option, value) => !!value && !!option && option.id === value.id}
                    />
                )}
            </>
        );
    } else if (customDefinition.propertyType === "ORDINAL") {
        const value = customDefinition.ordinals?.find((d) => customValue?.ordinals?.value?.includes(d.id as string));
        return (
            <Autocomplete
                id="ordinal-custom-property"
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
                renderInput={(params) => <TextField {...params} size="small" error={error} helperText={error ? errorText : ""} sx={{ minWidth: "200px" }} />}
                isOptionEqualToValue={(option, value) => !!value && !!option && option.id === value.id}
            />
        );
    } else if (customDefinition.propertyType === "NUMERIC") {
        return (
            <TextField
                required={customDefinition?.mandatory}
                id="numeric-custom-property"
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
                error={error}
                helperText={error ? errorText : ""}
            />
        );
    } else if (customDefinition.propertyType === "TEXT") {
        return (
            <TextField
                required={customDefinition?.mandatory}
                id="text-custom-property"
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
                error={error}
                helperText={error ? errorText : ""}
            />
        );
    } else {
        return null;
    }
};
