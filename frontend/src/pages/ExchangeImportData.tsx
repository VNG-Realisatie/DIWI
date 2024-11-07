import { Button, Stack, Typography } from "@mui/material";
import { DataCardItem } from "../components/DataCardItem";
import excelIcon from "../assets/excel.svg";
import geojsonIcon from "../assets/geojson.svg";
import zuidHollandIcon from "../assets/zuid-holland.png";

import * as Paths from "../Paths";
import { useTranslation } from "react-i18next";
import { useContext } from "react";
import UserContext from "../context/UserContext";
import { ExportData, getExportData } from "../api/exportServices";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

export const ExchangeImportData = () => {
    const { t } = useTranslation();
    const { allowedActions } = useContext(UserContext);

    useEffect(() => {
        const fetchData = async () => {
            const exportdata = await getExportData();
            setExportData(exportdata);
        };
        fetchData();
    }, []);

    return (
        <>
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
                {allowedActions.includes("EXPORT_PROJECTS") && (
                    <>
                        <Typography fontSize="16px" mt={2}>
                            {t("exchangeData.export")}
                        </Typography>
                        <Stack mt={1} direction="row" alignItems="flex-start" justifyContent="flex-start" flexWrap="wrap">
                            {exportData.map((exportItem) => (
                                <DataCardItem key={exportItem.id} text={exportItem.name} link={Paths.configuredExport.path + `/${exportItem.id}`}>
                                    <img src={zuidHollandIcon} height="125" width="125" alt="zuid-holland" />
                                </DataCardItem>
                            ))}
                        </Stack>
                    </>
                )}
                {allowedActions.includes("VIEW_DATA_EXCHANGES") && (
                    <Stack mt={5} direction="row" alignItems="flex-start" justifyContent="flex-start" flexWrap="wrap">
                        <Button
                            onClick={() => {
                                navigate(Paths.exportSettings.toPath());
                            }}
                            variant="outlined"
                        >
                            {t("exchangeData.newData")}
                        </Button>
                    </Stack>
                )}
            </Stack>
        </>
    );
};
