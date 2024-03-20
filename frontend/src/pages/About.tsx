import { TableContainer, Table, TableBody, TableRow, TableCell } from "@mui/material";
import { useTranslation } from "react-i18next";
import { diwiFetch } from "../utils/requests";
import { useEffect, useState } from "react";

type VersionType = {
    hash: string;
    date: string;
};

export const About = () => {
    const { t } = useTranslation();
    const [version, setVersion] = useState<VersionType | null>(null);

    useEffect(() => {
        diwiFetch("/version.json")
            .then((res) => {
                if (!res.ok) {
                    console.warn("Problem getting version info!");
                    return null;
                }
                return res.json();
            })
            .then((data) => {
                if (data) {
                    const versionInfo = { hash: data["git hash"], date: data["build date"] };
                    setVersion(versionInfo);
                } else {
                    setVersion(null);
                }
            });
    }, []);

    return (
        <>
            <h1>{t("about.heading")}</h1>
            <TableContainer>
                <Table sx={{ minWidth: 650 }} aria-label="simple table">
                    <TableBody>
                        <TableRow>
                            <TableCell>{t("about.version")}</TableCell>
                            <TableCell>{version?.hash ?? "No info available"}</TableCell>
                        </TableRow>
                        <TableRow>
                            <TableCell>{t("about.deployedOn")}</TableCell>
                            <TableCell>{version?.date ?? "No info available"}</TableCell>
                        </TableRow>
                    </TableBody>
                </Table>
            </TableContainer>
        </>
    );
};
