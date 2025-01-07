import { Stack, Typography } from "@mui/material";
import { useTranslation } from "react-i18next";
import { LabelComponent } from "../../../project/LabelComponent";
import { WizardCard } from "../../WizardCard";
import { CustomPropertyValue } from "../../../../api/customPropServices";
import { CustomPropertyWidget } from "../../../CustomPropertyWidget";
import { useCustomPropertyStore } from "../../../../hooks/useCustomPropertyStore";

type Props = {
    readOnly: boolean;
    customPropertyValues: CustomPropertyValue[];
    setCustomPropertyValues: (updatedValues: CustomPropertyValue[]) => void;
};

export const CustomPropertiesHouseblock = ({ readOnly, customPropertyValues, setCustomPropertyValues }: Props) => {
    const { customProperties } = useCustomPropertyStore();
    const { t } = useTranslation();
    const translationPath = "customProperties";

    const customDefinitions = customProperties.filter(
        (property) =>
            !property.disabled &&
            property.objectType === "WONINGBLOK" &&
            property.name !== "physicalAppearance" &&
            property.name !== "targetGroup" &&
            property.name !== "priceRangeBuy" &&
            property.name !== "priceRangeRent",
    );

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
                customDefinitions.map((property) => {
                    const customValue = customPropertyValues?.find((cv) => cv.customPropertyId === property.id);
                    return (
                        property.propertyType !== "RANGE_CATEGORY" && (
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
                        )
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
