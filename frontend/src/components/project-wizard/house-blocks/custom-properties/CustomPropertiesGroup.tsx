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

export const CustomPropertiesGroup = ({ readOnly, customPropertyValues, setCustomPropertyValues }: Props) => {
    const [customDefinitions, setCustomDefinitions] = useState<Property[]>([]);

    const { t } = useTranslation();

    const translationPath = "customProperties";

    useEffect(() => {
        getCustomPropertiesWithQuery("WONINGBLOK").then((customProperties) => setCustomDefinitions(customProperties));
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
            {customDefinitions
                .filter((p) => !p.disabled)
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
        </WizardCard>
    );
};
