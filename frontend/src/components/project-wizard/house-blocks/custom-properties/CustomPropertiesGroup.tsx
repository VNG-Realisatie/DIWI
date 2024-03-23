import { Stack, SxProps, Theme, Typography } from "@mui/material";
import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { CustomPropertyType, getCustomPropertiesWithQuery } from "../../../../api/adminSettingServices";
import { LabelComponent } from "../../../project/LabelComponent";
import { WizardCard } from "../../WizardCard";
import { CustomPropertyValue } from "../../../../api/customPropServices";
import { CustomPropertyWidget } from "../../../CustomPropertyWidget";

type Props = {
    projectEditable: boolean;
    customValues: CustomPropertyValue[];
    setCustomValues: (updatedValues: CustomPropertyValue[]) => void;
    columnTitleStyle: SxProps<Theme> | undefined;
};

export const CustomPropertiesGroup = ({ projectEditable, customValues, setCustomValues, columnTitleStyle }: Props) => {
    const [customDefinitions, setCustomDefinitions] = useState<CustomPropertyType[]>([]);

    const { t } = useTranslation();

    const translationPath = "customProperties";

    useEffect(() => {
        getCustomPropertiesWithQuery("WONINGBLOK").then((customProperties) => setCustomDefinitions(customProperties));
    }, []);

    const setCustomValue = (newValue: CustomPropertyValue) => {
        const newCustomValues = customValues.filter((val) => val.customPropertyId !== newValue.customPropertyId);
        setCustomValues([...newCustomValues, newValue]);
    };

    return (
        <WizardCard>
            <Typography fontWeight={600} mb={2}>
                {t(`${translationPath}.title`)}
            </Typography>
            {customDefinitions
                .filter((p) => !p.disabled)
                .map((property) => {
                    const customValue = customValues?.find((cv) => cv.customPropertyId === property.id);

                    return (
                        <Stack width="100%">
                            <LabelComponent required text={property.name} />{" "}
                            <CustomPropertyWidget
                                projectEditable={projectEditable}
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
