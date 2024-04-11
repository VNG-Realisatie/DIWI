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
    if (customDefinition.propertyType === "BOOLEAN") {
        if (!readOnly) {
            return (
                <Autocomplete
                    size="small"
                    options={[t("generic.true"), t("generic.false")]}
                    value={customValue?.booleanValue === true ? t("generic.true") : customValue?.booleanValue === false ? t("generic.false") : ""}
                    onChange={(_, newValue) => {
                        const booleanValue = newValue === t("generic.true") ? true : newValue === t("generic.false") ? false : undefined;
                        setCustomValue({ ...customValue, booleanValue });
                    }}
                    renderInput={(params) => <TextField {...params} size="small" />}
                />
            );
        } else {
            return (
                <CellContainer>
                    {customValue?.booleanValue === true ? t("generic.true") : customValue?.booleanValue === false ? t("generic.false") : ""}
                </CellContainer>
            );
        }
    } else if (customDefinition.propertyType === "CATEGORY") {
        if (!readOnly) {
            const values = customValue?.categories?.map((val) => customDefinition.categories?.find((d) => val === d.id));
            return (
                <Autocomplete
                    size="small"
                    options={customDefinition.categories || []}
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
