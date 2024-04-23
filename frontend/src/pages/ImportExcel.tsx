import { Button, Stack, Table, TableBody, TableCell, TableRow, Typography } from "@mui/material";
import DownloadIcon from "@mui/icons-material/Download";
import { ReactComponent as UploadCloud } from "../assets/uploadCloud.svg";
import { useRef, useState } from "react";
import useAlert from "../hooks/useAlert";
import { UploadErrorType, importExcelProjects } from "../api/importServices";

type Props = {
    excelImport: boolean;
};

export const ImportExcel = ({ excelImport }: Props) => {
    const fileInputRef = useRef(null);
    const [uploaded, setUploaded] = useState(false);
    const [errors, setErrors] = useState<Array<UploadErrorType>>([]);

    const { setAlert } = useAlert();

    function handleUploadStackClick(): void {
        if (fileInputRef.current) {
            (fileInputRef.current as HTMLElement).click();
        }
    }

    return (
        <Stack border="solid 1px #ddd" py={3} px={15}>
            <Typography fontSize="20px" fontWeight="600" sx={{ mt: 2 }}>
                {excelImport ? "Importeren vanuit Excel" : "Importeren vanuit Squit"}
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
                {excelImport ? "Upload ingevulde Excel template." : "Upload ingevulde Squit template."}
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
                    Klik hier om terug te gaan om een nieuw bestand te uploaden
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
                                importExcelProjects(file as FileList)
                                    .then((res) => {
                                        setUploaded(true);
                                        if (res.ok) {
                                            setAlert("Excel-bestand succesvol geüpload.", "success");
                                        } else {
                                            // 400 errors contain relevant info in body, deal with here
                                            setErrors(res);
                                        }
                                    })
                                    .catch((error) => {
                                        console.error("error", error);
                                    });
                            }
                        }}
                    />
                    Klik hier om te uploaden
                </Stack>
            )}
            {errors.length > 0 && (
                <Stack>
                    <Typography fontSize="16px" mt={2}>
                        {"Errors"}
                    </Typography>
                    <Table>
                        <TableBody>
                            {/* Header row */}
                            <TableRow>
                                <TableCell>{"Row"}</TableCell>
                                <TableCell>{"Column"}</TableCell>
                                <TableCell>{"Value"}</TableCell>
                                <TableCell>{"Description"}</TableCell>
                            </TableRow>
                            {/* Data rows */}
                            {errors.map((error) => (
                                <TableRow>
                                    <TableCell>{error.row}</TableCell>
                                    <TableCell>{error.column}</TableCell>
                                    <TableCell>{error.cellValue}</TableCell>
                                    <TableCell>{error.errorMessage}</TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </Stack>
            )}
        </Stack>
    );
};
