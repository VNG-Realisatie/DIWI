import { Button, Stack, Typography } from "@mui/material";
import DownloadIcon from "@mui/icons-material/Download";
import { useRef, useState } from "react";
import { importExcelProjects, importGeoJsonProjects } from "../api/importServices";
import useAlert from "../hooks/useAlert";
import { useNavigate } from "react-router-dom";
import * as Paths from "../Paths";
import { ImportErrorObject, ImportErrors } from "../components/ImportErrors";
import { t } from "i18next";
import useAllowedActions from "../hooks/useAllowedActions";
import ActionNotAllowed from "./ActionNotAllowed";

type FunctionalityType = "excel" | "squit" | "geojson";

type Props = {
    functionality: FunctionalityType;
};

export const ImportPage = ({ functionality }: Props) => {
    const fileInputRef = useRef(null);
    const navigate = useNavigate();
    const [uploaded, setUploaded] = useState(false);
    const [errors, setErrors] = useState<ImportErrorObject>({ error: [] });

    const { setAlert } = useAlert();
    const { allowedActions } = useAllowedActions();

    if (!allowedActions.includes("IMPORT_PROJECTS")) {
        return <ActionNotAllowed errorMessage={t("dashboard.forbidden")} />;
    }

    function handleUploadStackClick(): void {
        if (fileInputRef.current) {
            (fileInputRef.current as HTMLElement).click();
        }
    }
    const isGeoJson = functionality === "geojson";
    const importFunction = isGeoJson ? importGeoJsonProjects : importExcelProjects;
    return (
        <>
            <Stack border="solid 1px #ddd" py={3} px={15} marginBottom={"2em"}>
                <Typography fontSize="20px" fontWeight="600" sx={{ mt: 2 }}>
                    {t(`exchangeData.importName.${functionality}`)}
                </Typography>
                <Button
                    variant="outlined"
                    endIcon={<DownloadIcon />}
                    sx={{ my: 3, width: "310px" }}
                    href={
                        isGeoJson
                            ? new URL("../assets/geojson_template.geojson", import.meta.url).toString()
                            : new URL("../assets/Excel_Import.xlsx", import.meta.url).toString()
                    }
                    download={isGeoJson ? "geojson_template.geojson" : "Excel Import.xlsx"}
                >
                    {t(`exchangeData.download.${functionality}`)}
                </Button>
                <Typography fontSize="16px" mt={2}>
                    {t(`exchangeData.upload.${functionality}`)}
                </Typography>
                {uploaded && (
                    <Stack
                        height={180}
                        width="100%"
                        border="dashed 2px #ddd"
                        alignItems="center"
                        justifyContent="space-evenly"
                        style={{ cursor: "pointer" }}
                        onClick={() => {
                            setUploaded(false);
                            setErrors({ error: [] });
                            handleUploadStackClick();
                        }}
                    >
                        {t("exchangeData.notifications.importFailedGoBack")}
                    </Stack>
                )}
                {!uploaded && (
                    <Stack
                        id="upload-stack"
                        mt={2}
                        height={180}
                        width="100%"
                        border="dashed 2px #ddd"
                        direction="column"
                        alignItems="center"
                        justifyContent="space-evenly"
                        style={{ cursor: "pointer" }}
                        onClick={handleUploadStackClick}
                    >
                        <img src={"/upload-cloud.svg"} alt="Upload Cloud" />
                        <input
                            id="import-file"
                            hidden={true}
                            ref={fileInputRef}
                            type="file"
                            onChange={(e) => {
                                const file = e.target.files;
                                if (file) {
                                    setErrors({ error: [] });
                                    importFunction(file as FileList)
                                        .then(async (res) => {
                                            setUploaded(true);
                                            if (res.ok) {
                                                setAlert(t("exchangeData.notifications.importSuccess"), "success");
                                                navigate(Paths.projectsTable.path);
                                            } else {
                                                // 400 errors contain relevant info in body, deal with here
                                                const newErrors = (await res.json()) as ImportErrorObject;
                                                setErrors(newErrors);
                                                setAlert(t("exchangeData.notifications.importFailed"), "error");
                                            }
                                        })
                                        .catch((error) => {
                                            console.error("Failed to import due to error", error);
                                            setAlert(t("exchangeData.notifications.importFailed"), "error");
                                        });
                                }
                            }}
                        />
                        {t("exchangeData.upload.hint")}
                    </Stack>
                )}
                {errors.error.length > 0 && <ImportErrors errors={errors.error} isGeoJson={true} />}
            </Stack>
            {isGeoJson && (
                <Typography fontSize="16px" mt={2} fontWeight="600">
                    {t("exchangeData.geojsonHint")}
                </Typography>
            )}
        </>
    );
};
