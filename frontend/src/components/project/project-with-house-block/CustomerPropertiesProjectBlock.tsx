import { useState, useEffect } from "react";
import { Property, getCustomPropertiesWithQuery } from "../../../api/adminSettingServices";
import Grid from "@mui/material/Grid";
import { CustomPropertyValue } from "../../../api/customPropServices";
import { CustomPropertyWidget } from "../../CustomPropertyWidget";
import { LabelComponent } from "../LabelComponent";
import { Typography } from "@mui/material";
import { t } from "i18next";
import { CellContainer } from "./CellContainer";

type Props = {
    readOnly: boolean;
    customValues: CustomPropertyValue[];
    setCustomValues: (updatedValues: CustomPropertyValue[]) => void;
};

export const CustomPropertiesProject = ({ readOnly, customValues, setCustomValues }: Props) => {
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
        <Grid container spacing={2} m={2}>
            {customDefinitions.length > 0 &&
                customDefinitions
                    .filter(
                        // Make sure to filter out default and no longer active properties
                        (property) =>
                            !(
                                property.name === "district" ||
                                property.name === "municipality" ||
                                property.name === "municipalityRole" ||
                                property.name === "neighbourhood" ||
                                property.name === "priority"
                            ),
                    )
                    .map((property) => {
                        const customValue = customValues?.find((cv) => cv.customPropertyId === property.id);
                        return (
                            <Grid item xs={12} key={property.id}>
                                <Grid container spacing={2}>
                                    <Grid item xs={6}>
                                        {/* Show name of prop */}
                                        <CellContainer>
                                            <LabelComponent
                                                required={false /* TODO make depend on type of customprop? */}
                                                readOnly={readOnly}
                                                text={property.name}
                                            />
                                        </CellContainer>
                                    </Grid>

                                    <Grid item xs={6}>
                                        {/* Display value based on what type it is */}
                                        <CustomPropertyWidget
                                            readOnly={readOnly}
                                            customValue={customValue}
                                            setCustomValue={(newValue) => {
                                                setCustomValue({ ...newValue, customPropertyId: property.id });
                                            }}
                                            customDefinition={property}
                                        />
                                    </Grid>
                                </Grid>
                            </Grid>
                        );
                    })}
            {/* No custom props */}
            {(!customDefinitions || customDefinitions.length <= 0) && (
                <Grid item xs={12}>
                    <Typography fontStyle={readOnly ? "italic" : "normal"}>{t("createProject.noCustomProps")}</Typography>
                </Grid>
            )}
        </Grid>
    );
};
