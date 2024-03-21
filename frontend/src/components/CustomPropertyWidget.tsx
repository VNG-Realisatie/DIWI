import { Autocomplete, Grid, TextField, Typography } from "@mui/material";
import { useEffect, useState } from "react";
import { CustomPropertyType, getCustomPropertiesWithQuery } from "../api/adminSettingServices";
import { columnTitleStyle } from "./project/project-with-house-block/ProjectWithHouseBlock";

const CustomPropertyWidget = ({ projectEditable, customValues, setCustomValues }: any) => {
    const [customProperties, setCustomProperties] = useState<CustomPropertyType[]>([]);

    useEffect(() => {
        getCustomPropertiesWithQuery("PROJECT").then((properties) => {
            setCustomProperties(properties.filter((property) => !!property.id));
        });
    }, []);

    const handleValueChange = (customPropertyId: string, newValue: string | number | boolean) => {
        const updatedValues = customValues.map((value: any) => {
            if (value.customPropertyId === customPropertyId) {
                return {
                    ...value,
                    textValue: typeof newValue === "string" ? newValue : null,
                    numericValue: typeof newValue === "number" ? { value: newValue } : null,
                    booleanValue: typeof newValue === "boolean" ? newValue : null,
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
                    propertyType: typeof newValue === "string" ? "TEXT" : typeof newValue === "number" ? "NUMERIC" : "BOOLEAN",
                    textValue: typeof newValue === "string" ? newValue : null,
                    numericValue: typeof newValue === "number" ? { value: newValue } : null,
                    booleanValue: typeof newValue === "boolean" ? newValue : null,
                },
            ]);
        }
    };
    console.log(customProperties);
    console.log(customValues);

    return (
        <Grid container my={2}>
            {customProperties.map((property: any) => (
                <Grid item xs={6} md={1} key={property.id}>
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
                                    <Typography sx={columnTitleStyle}>
                                        {customValues.find((value: any) => value.customPropertyId === property.id)?.numericValue?.value || 0}
                                    </Typography>
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
                                    <Typography sx={columnTitleStyle}>
                                        {customValues.find((value: any) => value.customPropertyId === property.id)?.textValue || ""}
                                    </Typography>
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
