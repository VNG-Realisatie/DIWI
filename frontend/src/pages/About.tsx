import { TableContainer, Table, TableBody, TableRow, TableCell } from "@mui/material";
import { useTranslation } from "react-i18next";

export const About = () => {
    const { t } = useTranslation();
    console.log(process.env);

    return (
        <>
            <h1>{t("about.heading")}</h1>
            <TableContainer>
                <Table sx={{ minWidth: 650 }} aria-label="simple table">
                    <TableBody>
                        <TableRow>
                            <TableCell>{t("about.version")}</TableCell>
                            <TableCell>{process.env.REACT_APP_GIT_SHA}</TableCell>
                        </TableRow>
                        <TableRow>
                            <TableCell>{t("about.deployedOn")}</TableCell>
                            <TableCell>{process.env.REACT_APP_DEPLOY_DATE}</TableCell>
                        </TableRow>
                    </TableBody>
                </Table>
            </TableContainer>
        </>
    );
};
