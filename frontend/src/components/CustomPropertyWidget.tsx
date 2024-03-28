import { Autocomplete, TextField } from "@mui/material";
import { CustomPropertyType } from "../api/adminSettingServices";
import { CustomPropertyValue } from "../api/customPropServices";
import { CellContainer } from "./project/project-with-house-block/CellContainer";

type Props = {
    readOnly: boolean;
    customValue: CustomPropertyValue | undefined;
    customDefinition: CustomPropertyType;
    setCustomValue: (newValue: CustomPropertyValue) => void;
};

export const CustomPropertyWidget = ({ readOnly, customValue, setCustomValue, customDefinition }: Props) => {
    if (customDefinition.propertyType === "BOOLEAN") {
        if (!readOnly) {
            return (
                <Autocomplete
                    options={["true", "false"]}
                    value={customValue?.booleanValue?.toString() || ""}
                    onChange={(_, newValue) => setCustomValue({ ...customValue, booleanValue: newValue === "true" ? true : false })}
                    renderInput={(params) => <TextField {...params} size="small" />}
                />
            );
        } else {
            return <CellContainer>{customValue?.booleanValue?.toString() || ""}</CellContainer>;
        }
    } else if (customDefinition.propertyType === "CATEGORY") {
        if (!readOnly) {
            const values = customValue?.categories?.map((val) => customDefinition.categories?.find((d) => val === d.id));
            return (
                <Autocomplete
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
