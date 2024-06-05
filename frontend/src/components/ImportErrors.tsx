import { Alert, Typography, Table, TableBody, TableRow, TableCell } from "@mui/material";

export type UploadErrorType = {
    errorCode: string;
    row: number;
    column: string;
    cellValue: string;
    errorMessage: string;
};

type ImportErrorProps = { errors: Array<UploadErrorType> };

export const ImportErrors = ({ errors }: ImportErrorProps) => {
    return (
        <>
            {/* This INFO text can be removed later or kept if valuable */}
            <Alert severity="info">
                <Typography> There are three levels of errors. So fixing one type could lead to others when uploading again.</Typography>
                <Typography>Level 1 relates to reading headers: Missing custom props: stops any data/businesslogic validation for this column.</Typography>
                <Typography>Levels 2 & 3 relate to reading rows: Missing/invalid data: stops business logic validation for this row/cell.</Typography>
                <Typography>
                    An easy way to (temporarily) disable a row is to remove the ids in column B, these numbers do not mean anything but are required for this
                    row to be included.
                </Typography>
            </Alert>
            <Alert severity="error">
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
            </Alert>
        </>
    );
};
