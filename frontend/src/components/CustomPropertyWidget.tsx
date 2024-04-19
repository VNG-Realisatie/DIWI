import { Autocomplete, TextField } from "@mui/material";
import { Property } from "../api/adminSettingServices";
import { CustomPropertyValue } from "../api/customPropServices";
import { CellContainer } from "./project/project-with-house-block/CellContainer";
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
        if (!readOnly) {
            return (
                <Autocomplete
                    size="small"
                    options={[trueishLabel, falsyLabel]}
                    value={customValue?.booleanValue !== undefined ? booleanToLabel(customValue.booleanValue) : ""}
                    onChange={(_, newValue) => {
                        const booleanValue = newValue === trueishLabel ? true : newValue === falsyLabel ? false : undefined;
                        setCustomValue({ ...customValue, booleanValue });
                    }}
                    renderInput={(params) => <TextField {...params} size="small" />}
                />
            );
        } else {
            return <CellContainer>{customValue?.booleanValue !== undefined ? booleanToLabel(customValue.booleanValue) : ""}</CellContainer>;
        }
    } else if (customDefinition.propertyType === "CATEGORY") {
        if (!readOnly) {
            const values = customValue?.categories?.map((val) => customDefinition.categories?.find((d) => val === d.id));
            return (
                <Autocomplete
                    size="small"
                    options={customDefinition.categories?.filter((c) => !c.disabled) || []}
                    getOptionLabel={(option) => option?.name || ""}
                    value={values}
                    multiple
                    onChange={(_, newValue) => setCustomValue({ ...customValue, categories: newValue.map((c) => c?.id as string) })}
                    renderInput={(params) => <TextField {...params} size="small" />}
                />
            );
        } else {
            return (
                <CellContainer>
                    {(() => {
                        const categoryId = customValue?.categories;
                        if (!categoryId) return null;
                        const selectedCategoryIds = customDefinition?.categories?.filter((cat: any) => categoryId.includes(cat.id));
                        const categoryValues = selectedCategoryIds?.map((c) => c.name);
                        return categoryValues ? categoryValues.join(", ") : null;
                    })()}
                </CellContainer>
            );
        }
    } else if (customDefinition.propertyType === "ORDINAL") {
        const ordinalCategoryId = customValue?.ordinals?.value;
        if (!readOnly) {
            console.log("ordinalCategoryId", ordinalCategoryId);
            const value = customDefinition.ordinals?.find((d: any) => ordinalCategoryId?.includes(d.id));
            return (
                <Autocomplete
                    size="small"
                    options={customDefinition.ordinals?.filter((oc) => !oc.disabled).sort((a, b) => a.level - b.level) || []}
                    getOptionLabel={(option) => option?.name || ""}
                    value={value}
                    onChange={(_, newValue) => setCustomValue({ ...customValue, ordinals: { value: newValue?.id as string } })}
                    renderInput={(params) => <TextField {...params} size="small" sx={{ minWidth: "200px" }} />}
                />
            );
        } else {
            return (
                <CellContainer>
                    {(() => {
                        if (!ordinalCategoryId) return null;
                        const selectedOrdinalCategoryIds = customDefinition?.ordinals?.filter((ordCat: any) => ordinalCategoryId.includes(ordCat.id));
                        const ordinalCategoryValue = selectedOrdinalCategoryIds?.map((oc) => oc.name);
                        return ordinalCategoryValue ? ordinalCategoryValue : null;
                    })()}
                </CellContainer>
            );
        }
    } else if (customDefinition.propertyType === "NUMERIC") {
        if (!readOnly) {
            return (
                <TextField
                    variant="outlined"
                    size="small"
                    type="number"
                    value={customValue?.numericValue?.value || 0}
                    onChange={(e) => setCustomValue({ ...customValue, numericValue: { value: parseFloat(e.target.value) } })}
                />
            );
        } else {
            return <CellContainer>{customValue?.numericValue?.value || 0}</CellContainer>;
        }
    } else if (customDefinition.propertyType === "TEXT") {
        if (!readOnly) {
            return (
                <TextField
                    variant="outlined"
                    size="small"
                    value={customValue?.textValue || ""}
                    onChange={(e) => setCustomValue({ ...customValue, textValue: e.target.value })}
                />
            );
        } else {
            return <CellContainer>{customValue?.textValue || ""}</CellContainer>;
        }
    } else {
        return null;
    }
};
