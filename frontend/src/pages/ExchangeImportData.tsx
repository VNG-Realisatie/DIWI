import { Stack, Typography } from "@mui/material";
import { DataCardItem } from "../components/DataCardItem";
import excelIcon from "../assets/excel.svg";
import geojsonIcon from "../assets/geojson.svg";

import * as Paths from "../Paths";
import { useTranslation } from "react-i18next";
import { useContext } from "react";
import UserContext from "../context/UserContext";

export const ExchangeImportData = () => {
    const { t } = useTranslation();
    const { allowedActions } = useContext(UserContext);

    return (
        <Stack border="solid 1px #ddd" py={5} px={8} mb={10}>
            <Typography fontSize="20px" fontWeight="600">
                {t("exchangeData.title")}
            </Typography>
            {allowedActions.includes("IMPORT_PROJECTS") && (
                <>
                    <Typography fontSize="16px" mt={2}>
                        {t("exchangeData.import")}
                    </Typography>
                    <Stack mt={1} direction="row" alignItems="flex-start" justifyContent="flex-start" flexWrap="wrap">
                        <DataCardItem text={t("exchangeData.excel")} link={Paths.importExcel.path} isImport>
                            <img src={excelIcon} alt="excel" />
                        </DataCardItem>
                        <DataCardItem text={t("exchangeData.geojson")} link={Paths.importGeoJson.path} isImport>
                            <img src={geojsonIcon} alt="geojson" />
                        </DataCardItem>
                    </Stack>
                </>
            )}
        </Stack>
    );
};
