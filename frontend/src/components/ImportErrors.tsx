import { Alert, Typography, Stack, Accordion, AccordionSummary, AccordionDetails, List } from "@mui/material";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import { t } from "i18next";
import CustomPropertiesCreateButton from "./CustomPropertiesCreateButton";
import { useState } from "react";
import { PropertyListItem } from "./PropertyListItem";

export type ImportErrorObject = {
    error: Array<ImportErrorType>;
};

export type ImportErrorType = {
    // only two guaranteed props
    errorCode: string; // short string indicating what error occured, see errorCodes in translation file(s)
    errorMessage: string; // English description = always same for errorCode, we need to use translation instead
    // optional props
    row: number | undefined;
    column: string | undefined;
    value: string | undefined;
    propertyName: string | undefined;
    houseblockName: string | undefined;
    identificationNumber: number | undefined;
    customPropertyId: string | undefined; // UUID
};

type ImportErrorProps = { errors: ImportErrorType[]; isGeoJson?: boolean };

export const ImportErrors = ({ errors, isGeoJson = false }: ImportErrorProps) => {
    const [isButtonDisabledMap, setIsButtonDisabledMap] = useState<{ [key: string]: boolean }>({});
    return (
        <>
            {/* This INFO text can be removed later or kept if valuable */}
            <Alert severity="info">
                <Typography>{t("import.description.intro")}</Typography>
                <Typography>{t("import.description.level1")}</Typography>
                <Typography>{t("import.description.level2&3")}</Typography>
                {!isGeoJson && <Typography>{t("import.description.disabling")}</Typography>}
            </Alert>
            <Alert severity="error" sx={{ "& .MuiAlert-message": { width: "100%" } }}>
                <Typography fontSize="16px" mb={2}>
                    {t("import.errorsTitle")}
                </Typography>
                <Stack>
                    {errors.map((error) => {
                        return (
                            <Accordion defaultExpanded>
                                <AccordionSummary
                                    expandIcon={<ExpandMoreIcon />}
                                    sx={{
                                        backgroundColor: "lightgray",
                                    }}
                                >
                                    <Typography className="import-error">{t(`import.errorCodes.${error.errorCode}`)}</Typography>
                                </AccordionSummary>
                                <AccordionDetails>
                                    <>
                                        <List
                                            dense
                                            sx={{
                                                listStyleType: "disc",
                                                pl: 1,
                                                "& .MuiListItem-root": {
                                                    display: "list-item",
                                                    padding: 0,
                                                },
                                            }}
                                        >
                                            <PropertyListItem label={t("import.errorProperties.row")} value={error.row} />
                                            <PropertyListItem label={t("import.errorProperties.column")} value={error.column} />
                                            <PropertyListItem label={t("import.errorProperties.value")} value={error.value} />
                                            <PropertyListItem label={t("import.errorProperties.propertyName")} value={error.propertyName} />
                                            <PropertyListItem label={t("import.errorProperties.houseblockName")} value={error.houseblockName} />
                                            <PropertyListItem label={t("import.errorProperties.identificationNumber")} value={error.identificationNumber} />
                                            <PropertyListItem label={t("import.errorProperties.customPropertyId")} value={error.customPropertyId} />
                                        </List>
                                        <CustomPropertiesCreateButton
                                            error={error}
                                            isButtonDisabledMap={isButtonDisabledMap}
                                            setIsButtonDisabledMap={setIsButtonDisabledMap}
                                        />
                                    </>
                                </AccordionDetails>
                            </Accordion>
                        );
                    })}
                </Stack>
            </Alert>
        </>
    );
};
