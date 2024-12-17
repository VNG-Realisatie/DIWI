import { ChangeEvent, useContext, useEffect } from "react";
import { Box, Button, Grid } from "@mui/material";
import TextInput from "../project/inputs/TextInput";

import UserGroupSelect from "../../widgets/UserGroupSelect";
import { UserGroup } from "../../api/projectsServices";
import { Blueprint, createBlueprint, updateBlueprint, VisibilityElement } from "../../api/dashboardServices";
import useAlert from "../../hooks/useAlert";
import { t } from "i18next";
import { Visibility } from "./DashboardCharts";
import { useNavigate, useParams } from "react-router-dom";
import { TooltipInfo } from "../../widgets/TooltipInfo";
import { FileDownload } from "@mui/icons-material";
import { exportPdf } from "../../utils/exportPDF";
import UserContext from "../../context/UserContext";

const emptyBlueprint: Blueprint = {
    name: "",
    userGroups: [],
    elements: [],
    categories: [],
};

type Props = {
    visibility: Visibility;
    newBlueprint: Blueprint;
    setNewBlueprint: (blueprint: Blueprint) => void;
    userGroups: UserGroup[];
    setUserGroups: (userGroups: UserGroup[]) => void;
    setPdfExport: (pdfExport: boolean) => void;
    pdfExport: boolean;
    categoriesVisibility: { [key: string]: boolean };
};

export const CustomDashboardForm = ({
    visibility,
    newBlueprint,
    setNewBlueprint,
    userGroups,
    setUserGroups,
    setPdfExport,
    pdfExport,
    categoriesVisibility,
}: Props) => {
    const { setAlert } = useAlert();
    const { id } = useParams();
    const navigate = useNavigate();
    const { user, allowedActions } = useContext(UserContext);

    const disabledForm = !allowedActions.includes("EDIT_ALL_BLUEPRINTS");

    useEffect(() => {
        if (id) {
            setUserGroups(newBlueprint.userGroups.map((group) => ({ ...group, name: "" })));
        }
    }, [id, newBlueprint.userGroups, setUserGroups]);

    useEffect(() => {
        pdfExport &&
            setTimeout(() => {
                exportPdf(t, setPdfExport);
            }, 500);
    }, [pdfExport, setPdfExport]);

    const buttonDisabled = !newBlueprint.name || userGroups.length === 0 || !Object.values(visibility).some((value) => value === true);

    const handleSave = async () => {
        const elementsToAdd = Object.entries(visibility)
            // eslint-disable-next-line @typescript-eslint/no-unused-vars
            .filter(([_, value]) => value === true)
            .map(([key]) => key);

        const categoriesToAdd = Object.entries(categoriesVisibility)
            // eslint-disable-next-line @typescript-eslint/no-unused-vars
            .filter(([_, value]) => value === true)
            .map(([key]) => key);

        const updatedBlueprint = {
            ...newBlueprint,
            userGroups: userGroups.map((group) => ({ uuid: group.uuid, name: group.name })),
            elements: elementsToAdd as VisibilityElement[],
            categories: categoriesToAdd,
        };

        try {
            id ? await updateBlueprint(updatedBlueprint) : await createBlueprint(updatedBlueprint);
            setNewBlueprint(emptyBlueprint);
            setUserGroups([]);
            if (!id) {
                setAlert(t("dashboard.blueprints.successfullyCreated"), "success");
            } else {
                setAlert(t("dashboard.blueprints.successfullyUpdated"), "success");
                navigate("/dashboard/custom-dashboards");
            }
        } catch (error: unknown) {
            if (error instanceof Error) setAlert(error.message, "error");
        }
    };
    return (
        <Grid container spacing={2} alignItems="center" mb={2}>
            <Grid item xs={3}>
                <TextInput
                    readOnly={disabledForm}
                    value={newBlueprint.name}
                    setValue={(event: ChangeEvent<HTMLInputElement>) => {
                        setNewBlueprint({ ...newBlueprint, name: event.target.value });
                    }}
                    mandatory={true}
                    placeholder={t("dashboard.blueprints.namePlaceholder")}
                />
            </Grid>
            <Grid item xs={3}>
                <UserGroupSelect
                    checkIsOwnerValidWithConfidentialityLevel={() => true}
                    placeholder={t("dashboard.blueprints.selectUsers")}
                    readOnly={disabledForm}
                    userGroup={userGroups}
                    setUserGroup={setUserGroups}
                    mandatory={true}
                    errorText=""
                    projectOwnersOnly={false}
                />
            </Grid>
            <Grid item xs={3}>
                {!disabledForm && (
                    <Button variant="contained" onClick={handleSave} disabled={buttonDisabled}>
                        {t("dashboard.blueprints.saveBlueprint")}
                    </Button>
                )}
            </Grid>
            <Grid item xs={3}>
                {user?.role != "External" && (
                    <Box display="flex" justifyContent="flex-end" width="100%">
                        <TooltipInfo text={t("dashboard.exportpdf")}>
                            <FileDownload
                                onClick={() => {
                                    setPdfExport(true);
                                }}
                                sx={{ fill: "#002C64", cursor: "pointer" }}
                            />
                        </TooltipInfo>
                    </Box>
                )}
            </Grid>
        </Grid>
    );
};
