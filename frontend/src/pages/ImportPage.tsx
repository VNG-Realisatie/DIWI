import { Button, Stack, Typography } from "@mui/material";
import DownloadIcon from "@mui/icons-material/Download";
import { useRef, useState } from "react";
import { importExcelProjects, importGeoJsonProjects } from "../api/importServices";
import UploadCloud from "../assets/uploadCloud.svg";
import useAlert from "../hooks/useAlert";
import { useNavigate } from "react-router-dom";
import * as Paths from "../Paths";
import { ImportErrorType, ImportErrors } from "../components/ImportErrors";
import { t } from "i18next";

type FunctionalityType = "excel" | "squit" | "geojson";

type Props = {
    functionality: FunctionalityType;
};

export const ImportPage = ({ functionality }: Props) => {
    const fileInputRef = useRef(null);
    const navigate = useNavigate();
    const [uploaded, setUploaded] = useState(false);
    const [errors, setErrors] = useState<Array<ImportErrorType>>([]);

    const { setAlert } = useAlert();

    function handleUploadStackClick(): void {
        if (fileInputRef.current) {
            (fileInputRef.current as HTMLElement).click();
        }
    }

    const importFunction = functionality === "geojson" ? importGeoJsonProjects : importExcelProjects;

    return (
        <Stack border="solid 1px #ddd" py={3} px={15} marginBottom={"2em"}>
            <Typography fontSize="20px" fontWeight="600" sx={{ mt: 2 }}>
                {t(`exchangeData.importName.${functionality}`)}
            </Typography>
            <Button
                variant="outlined"
                endIcon={<DownloadIcon />}
                sx={{ my: 3, width: "310px" }}
                href={require(functionality === "geojson" ? "../assets/geojson_template.geojson" : "../assets/Excel_Import.xlsx")}
                download={functionality === "geojson" ? "geojson_template.geojson" : "Excel Import.xlsx"}
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
                        setErrors([]);
                        handleUploadStackClick();
                    }}
                >
                    {t("exchangeData.notifications.importFailedGoBack")}
                </Stack>
            )}
            {!uploaded && (
                <Stack
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
                    <UploadCloud />
                    <input
                        hidden
                        ref={fileInputRef}
                        type="file"
                        onChange={(e) => {
                            const file = e.target.files;
                            if (file) {
                                setErrors([]);
                                importFunction(file as FileList)
                                    .then(async (res) => {
                                        setUploaded(true);
                                        if (res.ok) {
                                            setAlert(t("exchangeData.notifications.importSuccess"), "success");
                                            navigate(Paths.projectsTable.path);
                                        } else {
                                            // 400 errors contain relevant info in body, deal with here
                                            const newErrors = (await res.json()) as Array<ImportErrorType>;
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
            {errors.length > 0 && <ImportErrors errors={errors} isGeoJson={true} />}
        </Stack>
    );
};
