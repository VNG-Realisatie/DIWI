import { Autocomplete, Grid, TextField, Typography } from "@mui/material";
import { useEffect, useState } from "react";
import { CustomPropertyType, getCustomPropertiesWithQuery } from "../api/adminSettingServices";
import { CellContainer } from "./project/project-with-house-block/CellContainer";

const columnTitleStyle = {
    border: "solid 1px #ddd",
    p: 0.6,
    color: "#FFFFFF",
    backgroundColor: "#738092",
};

const CustomPropertyWidget = ({ projectEditable, customValues, setCustomValues }: any) => {
    const [customProperties, setCustomProperties] = useState<CustomPropertyType[]>([]);

    useEffect(() => {
        getCustomPropertiesWithQuery("PROJECT").then((properties) => {
            setCustomProperties(properties.filter((property) => !!property.id));
        });
    }, []);

    const handleValueChange = (customPropertyId: string, newValue: any) => {
        const updatedValues = customValues.map((value: any) => {
            if (value.customPropertyId === customPropertyId) {
                return {
                    ...value,
                    textValue: typeof newValue === "string" ? newValue : null,
                    numericValue: typeof newValue === "number" ? { value: newValue } : null,
                    booleanValue: typeof newValue === "string" && (newValue === "true" || newValue === "false") ? newValue === "true" : null,
                    categories: Array.isArray(newValue) && newValue.length === 1 ? newValue : [],
                };
            }
            return value;
        });

        const existingValueIndex = updatedValues.findIndex((value: any) => value.customPropertyId === customPropertyId);
        if (existingValueIndex !== -1) {
            setCustomValues(updatedValues);
        } else {
            setCustomValues([
                ...updatedValues,
                {
                    customPropertyId,
                    propertyType:
                        typeof newValue === "string"
                            ? "TEXT"
                            : typeof newValue === "number"
                              ? "NUMERIC"
                              : typeof newValue === "boolean"
                                ? "BOOLEAN"
                                : "CATEGORY",
                    textValue: typeof newValue === "string" ? newValue : null,
                    numericValue: typeof newValue === "number" ? { value: newValue } : null,
                    booleanValue: typeof newValue === "string" && (newValue === "true" || newValue === "false") ? newValue === "true" : null,
                    categories: Array.isArray(newValue) && newValue.length === 1 ? newValue : [],
                },
            ]);
        }
    };

    return (
        <Grid container my={2}>
            {customProperties.map((property: any) => (
                <Grid item xs={6} md={1} key={property.id}>
                    {property.propertyType === "BOOLEAN" && (
                        <>
                            {projectEditable ? (
                                <>
                                    <Typography sx={columnTitleStyle}>{property.name}</Typography>
                                    <Autocomplete
                                        options={["true", "false"]}
                                        value={customValues.find((value: any) => value.customPropertyId === property.id)?.booleanValue?.toString() || ""}
                                        onChange={(_, newValue) => handleValueChange(property.id, newValue)}
                                        renderInput={(params) => <TextField {...params} size="small" />}
                                    />
                                </>
                            ) : (
                                <>
                                    <Typography sx={columnTitleStyle}>{property.name}</Typography>
                                    <CellContainer>
                                        {customValues.find((value: any) => value.customPropertyId === property.id)?.booleanValue?.toString() || ""}
                                    </CellContainer>
                                </>
                            )}
                        </>
                    )}
                    {property.propertyType === "CATEGORY" && (
                        <>
                            {projectEditable ? (
                                <>
                                    <Typography sx={columnTitleStyle}>{property.name}</Typography>
                                    <Autocomplete
                                        options={property.categories || []}
                                        getOptionLabel={(option: any) => option.name}
                                        value={(() => {
                                            const categoryId = customValues.find((value: any) => value.customPropertyId === property.id)?.categories?.[0];
                                            if (!categoryId) return null;
                                            const category = property.categories.find((cat: any) => cat.id === categoryId);
                                            return category ? category : null;
                                        })()}
                                        onChange={(_, newValue) => handleValueChange(property.id, newValue ? [newValue.id] : null)}
                                        renderInput={(params) => <TextField {...params} size="small" />}
                                    />
                                </>
                            ) : (
                                <>
                                    <Typography sx={columnTitleStyle}>{property.name}</Typography>
                                    <CellContainer>
                                        {(() => {
                                            const categoryId = customValues.find((value: any) => value.customPropertyId === property.id)?.categories?.[0];
                                            if (!categoryId) return null;
                                            const category = property.categories.find((cat: any) => cat.id === categoryId);
                                            return category ? category.name : null;
                                        })()}
                                    </CellContainer>
                                </>
                            )}
                        </>
                    )}
                    {property.propertyType === "NUMERIC" && (
                        <>
                            {projectEditable ? (
                                <>
                                    <Typography sx={columnTitleStyle}>{property.name}</Typography>
                                    <TextField
                                        variant="outlined"
                                        size="small"
                                        value={customValues.find((value: any) => value.customPropertyId === property.id)?.numericValue?.value || 0}
                                        onChange={(e) => {
                                            const numericValue: number = +e.target.value.replace(/[^0-9]/g, "");
                                            handleValueChange(property.id, numericValue);
                                        }}
                                    />
                                </>
                            ) : (
                                <>
                                    <Typography sx={columnTitleStyle}>{property.name}</Typography>
                                    <CellContainer>
                                        {customValues.find((value: any) => value.customPropertyId === property.id)?.numericValue?.value || 0}
                                    </CellContainer>
                                </>
                            )}
                        </>
                    )}
                    {property.propertyType === "TEXT" && (
                        <>
                            {projectEditable ? (
                                <>
                                    <Typography sx={columnTitleStyle}>{property.name}</Typography>
                                    <TextField
                                        variant="outlined"
                                        size="small"
                                        value={customValues.find((value: any) => value.customPropertyId === property.id)?.textValue || ""}
                                        onChange={(e) => handleValueChange(property.id, e.target.value)}
                                    />
                                </>
                            ) : (
                                <>
                                    <Typography sx={columnTitleStyle}>{property.name}</Typography>
                                    <CellContainer>{customValues.find((value: any) => value.customPropertyId === property.id)?.textValue || ""}</CellContainer>
                                </>
                            )}
                        </>
                    )}
                </Grid>
            ))}
        </Grid>
    );
};

export default CustomPropertyWidget;
