import { Alert, Typography, Table, TableBody, TableRow, TableCell } from "@mui/material";
import { t } from "i18next";

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

type ImportErrorProps = { errors: Array<ImportErrorType> };

export const ImportErrors = ({ errors }: ImportErrorProps) => {
    return (
        <>
            {/* This INFO text can be removed later or kept if valuable */}
            <Alert severity="info">
                <Typography>{t("import.description.intro")}</Typography>
                <Typography>{t("import.description.level1")}</Typography>
                <Typography>{t("import.description.level2&3")}</Typography>
                <Typography>{t("import.description.disabling")}</Typography>
            </Alert>
            <Alert severity="error">
                <Typography fontSize="16px" mt={2}>
                    {t("import.title")}
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
                                <TableCell>{error.value}</TableCell>
                                <TableCell>{error.errorMessage}</TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </Alert>
        </>
    );
};
