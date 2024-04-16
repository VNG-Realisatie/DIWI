import { useState, useEffect } from "react";
import { Property, getCustomPropertiesWithQuery } from "../../../api/adminSettingServices";
import Grid from "@mui/material/Grid";
import { CustomPropertyValue } from "../../../api/customPropServices";
import Typography from "@mui/material/Typography";
import { CustomPropertyWidget } from "../../CustomPropertyWidget";
import { SxProps, Theme } from "@mui/material";

type Props = {
    readOnly: boolean;
    customValues: CustomPropertyValue[];
    setCustomValues: (updatedValues: CustomPropertyValue[]) => void;
    columnTitleStyle: SxProps<Theme> | undefined;
};

export const CustomerPropertiesProjectBlock = ({ readOnly, customValues, setCustomValues, columnTitleStyle }: Props) => {
    const [customDefinitions, setCustomDefinitions] = useState<Property[]>([]);

    useEffect(() => {
        getCustomPropertiesWithQuery("PROJECT").then((properties) => {
            setCustomDefinitions(properties.filter((property) => !property.disabled));
        });
    }, []);

    const setCustomValue = (newValue: CustomPropertyValue) => {
        const newCustomValues = customValues.filter((val) => val.customPropertyId !== newValue.customPropertyId);
        setCustomValues([...newCustomValues, newValue]);
    };
    return (
        <Grid container my={2}>
            {customDefinitions.map((property) => {
                const customValue = customValues?.find((cv) => cv.customPropertyId === property.id);
                return (
                    <Grid item xs={6} md={"auto"} key={property.id}>
                        <Typography sx={columnTitleStyle}>{property.name}</Typography>

                        <CustomPropertyWidget
                            readOnly={readOnly}
                            customValue={customValue}
                            setCustomValue={(newValue) => {
                                setCustomValue({ ...newValue, customPropertyId: property.id });
                            }}
                            customDefinition={property}
                        />
                    </Grid>
                );
            })}
        </Grid>
    );
};
