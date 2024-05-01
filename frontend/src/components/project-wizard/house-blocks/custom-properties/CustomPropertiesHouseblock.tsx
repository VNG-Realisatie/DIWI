import { Stack, Typography } from "@mui/material";
import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Property, getCustomPropertiesWithQuery } from "../../../../api/adminSettingServices";
import { LabelComponent } from "../../../project/LabelComponent";
import { WizardCard } from "../../WizardCard";
import { CustomPropertyValue } from "../../../../api/customPropServices";
import { CustomPropertyWidget } from "../../../CustomPropertyWidget";

type Props = {
    readOnly: boolean;
    customPropertyValues: CustomPropertyValue[];
    setCustomPropertyValues: (updatedValues: CustomPropertyValue[]) => void;
};

export const CustomPropertiesHouseblock = ({ readOnly, customPropertyValues, setCustomPropertyValues }: Props) => {
    const [customDefinitions, setCustomDefinitions] = useState<Property[]>([]);

    const { t } = useTranslation();

    const translationPath = "customProperties";

    useEffect(() => {
        getCustomPropertiesWithQuery("WONINGBLOK").then((properties) => {
            // Make sure to filter out no longer active properties
            setCustomDefinitions(properties.filter((property) => !property.disabled));
        });
    }, []);

    const setCustomValue = (newValue: CustomPropertyValue) => {
        const newCustomValues = customPropertyValues.filter((val) => val.customPropertyId !== newValue.customPropertyId);
        setCustomPropertyValues([...newCustomValues, newValue]);
    };

    return (
        <WizardCard>
            <Typography fontWeight={600} mb={2}>
                {t(`${translationPath}.title`)}
            </Typography>
            {customDefinitions.length > 0 &&
                customDefinitions
                    // Make sure to not display 'default' houseblock props
                    .filter((property) => !(property.name === "physicalAppearance" || property.name === "targetGroup"))
                    .map((property) => {
                        const customValue = customPropertyValues?.find((cv) => cv.customPropertyId === property.id);
                        return (
                            <Stack key={property.id} width="100%">
                                <LabelComponent required text={property.name} />{" "}
                                <CustomPropertyWidget
                                    readOnly={readOnly}
                                    customValue={customValue}
                                    setCustomValue={(newValue) => {
                                        setCustomValue({ ...newValue, customPropertyId: property.id });
                                    }}
                                    customDefinition={property}
                                />
                            </Stack>
                        );
                    })}
            {/* No custom props */}
            {(!customDefinitions || customDefinitions.length <= 0) && (
                <Stack>
                    <Typography fontStyle={readOnly ? "italic" : "normal"}>{t("createProject.noCustomProps")}</Typography>
                </Stack>
            )}
        </WizardCard>
    );
};
