import { useState, useEffect, useContext } from "react";
import { Grid, Box, Typography } from "@mui/material";
import { ExportData, getExportData } from "../api/exportServices";
import ExportTable from "../components/export/ExportTable";
import { t } from "i18next";
import ActionNotAllowed from "./ActionNotAllowed";
import { AddExportButton } from "../components/PlusButton";
import UserContext from "../context/UserContext";

const ExportSettings = () => {
    const [exportData, setExportData] = useState<ExportData[]>([]);
    const [selectedExport, setSelectedExport] = useState<ExportData | null>(null);
    const { allowedActions } = useContext(UserContext);

    useEffect(() => {
        const fetchData = async () => {
            const exportdata = await getExportData();
            setExportData(exportdata);
        };
        fetchData();
    }, []);

    if (!allowedActions.includes("VIEW_DATA_EXCHANGES")) {
        return <ActionNotAllowed errorMessage={t("admin.export.actionNotAllowed")} />;
    }

    return (
        <Box p={2}>
            <Grid container spacing={2}>
                <Grid item xs={12}>
                    <Typography variant="h6">{t("sidebar.exportSettings")}</Typography>
                </Grid>
                <Grid item xs={12}>
                    <ExportTable exportData={exportData} selectedExport={selectedExport} setSelectedExport={setSelectedExport} setExportData={setExportData} />
                    {allowedActions.includes("EDIT_DATA_EXCHANGES") && (
                        <Box sx={{ position: "relative", top: "80px" }}>
                            <AddExportButton />
                        </Box>
                    )}
                </Grid>
            </Grid>
        </Box>
    );
};

export default ExportSettings;
