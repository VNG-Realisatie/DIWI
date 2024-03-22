import { TableContainer, Table, TableBody, TableRow, TableCell } from "@mui/material";
import { useTranslation } from "react-i18next";
import { diwiFetch } from "../utils/requests";
import { useEffect, useState } from "react";
import useAlert from "../hooks/useAlert";

type VersionType = {
    hash: string;
    date: string;
};

export const About = () => {
    const { t } = useTranslation();
    const [version, setVersion] = useState<VersionType | null>(null);
    const { setAlert } = useAlert();

    const unavailableText = t("generic.noInfoAvailable");

    useEffect(() => {
        diwiFetch("/version.json")
            .then((res) => {
                if (!res.ok) {
                    setAlert("Problem getting version info!", "error");
                    return null;
                }
                return res.json();
            })
            .then((data) => {
                if (data) {
                    const hash = data["git hash"] && data["git hash"].trim().length !== 0 ? data["git hash"] : null;
                    const date = data["build date"] && data["build date"].trim().length !== 0 ? data["build date"] : null;
                    const versionInfo = { hash: hash, date: date };
                    setVersion(versionInfo);
                } else {
                    setVersion(null);
                }
            });
    }, [setAlert]);

    return (
        <>
            <h1>{t("about.heading")}</h1>
            <TableContainer>
                <Table sx={{ minWidth: 650 }} aria-label="simple table">
                    <TableBody>
                        <TableRow>
                            <TableCell>{t("about.version")}</TableCell>
                            <TableCell>{version?.hash ?? unavailableText}</TableCell>
                        </TableRow>
                        <TableRow>
                            <TableCell>{t("about.deployedOn")}</TableCell>
                            <TableCell>{version?.date ?? unavailableText}</TableCell>
                        </TableRow>
                    </TableBody>
                </Table>
            </TableContainer>
        </>
    );
};
