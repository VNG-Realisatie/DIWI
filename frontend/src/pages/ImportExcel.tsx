import { Button, Stack, Typography } from "@mui/material";
import DownloadIcon from "@mui/icons-material/Download";
import { ReactComponent as UploadCloud } from "../assets/uploadCloud.svg";
import { useState } from "react";
import useAlert from "../hooks/useAlert";
import { useNavigate } from "react-router-dom";
import * as Paths from "../Paths";

type Props={
    excelImport:boolean;
}
export const ImportExcel = ({excelImport}:Props) => {
    const [uploaded, setUploaded] = useState(false);
    const { setAlert } = useAlert();
    const navigate = useNavigate();
    return (
        <Stack border="solid 1px #ddd" py={3} px={15}>
            <Typography fontSize="20px" fontWeight="600" sx={{ mt: 2 }}>
              {excelImport?"Importeren vanuit Excel":"Importeren vanuit Squit"}
            </Typography>
            <Button
                variant="outlined"
                endIcon={<DownloadIcon />}
                sx={{ my: 3, width: "310px" }}
                href={require("../assets/Excel_Import.xlsx")}
                download="Excel Import.xlsx"
            >
                Download Excel template hier
            </Button>
            <Typography fontSize="16px" mt={2}>
               {excelImport?"Upload ingevulde Excel template.":"Upload ingevulde Squit template."}
            </Typography>
            {!uploaded && (
                <Stack
                    mt={2}
                    height={180}
                    width="100%"
                    border="dashed 2px #ddd"
                    direction="column"
                    alignItems="center"
                    justifyContent="space-evenly"
                >
                    <label htmlFor="file-input" style={{ cursor: "pointer" }}>
                        <UploadCloud />
                        <input
                            hidden
                            id="file-input"
                            type="file"
                            onChange={(e) => {
                                //ToDo Add upload endpoint
                                const file =
                                    e.target.files && e.target.files[0];
                                if (file) {
                                    // ToDo: Add upload logic here
                                    console.log("File uploaded:", file);
                                }
                                setTimeout(() => {
                                    setUploaded(true);
                                    setAlert(
                                        "Excel-bestand succesvol geüpload.",
                                        "success"
                                    );
                                    navigate(Paths.importExcelProjects.path);
                                }, 3000);
                            }}
                        />
                    </label>
                    Sleep bestanden hierheen of click om te uploaden
                </Stack>
            )}
        </Stack>
    );
};
