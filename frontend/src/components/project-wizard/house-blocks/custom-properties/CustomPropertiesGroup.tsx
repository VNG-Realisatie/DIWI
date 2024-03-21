import { Grid, Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { useTranslation } from "react-i18next";
import { useEffect, useState } from "react";
import { CustomPropertyType, getCustomPropertiesWithQuery } from "../../../../api/adminSettingServices";

export const CustomPropertiesGroup = () => {
    const [customProperties, setCustomProperties] = useState<CustomPropertyType[]>();

    const { t } = useTranslation();

    const translationPath = "customProperties";

    useEffect(() => {
        getCustomPropertiesWithQuery("WONINGBLOK").then((customProperties) => setCustomProperties(customProperties));
    }, []);
    return (
        <WizardCard>
            <Typography fontWeight={600} mb={2}>
                {t(`${translationPath}.title`)}
            </Typography>
            {customProperties &&
                customProperties
                    .filter((p) => !p.disabled)
                    .map((cp, i) => {
                        return (
                            <Grid item xs={6} md={1} key={i} my={2} spacing={2}>
                                <Typography variant="subtitle1" fontWeight="500" border="solid 1px #ddd" borderRadius="5px" p={0.6}>
                                    {cp.name}
                                </Typography>
                            </Grid>
                        );
                    })}
        </WizardCard>
    );
};
