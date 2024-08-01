import { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import { DashboardCharts } from "../components/dashboard/DashboardCharts";
import { Blueprint, getBlueprint, VisibilityElement } from "../api/dashboardServices";
import useAlert from "../hooks/useAlert";
import { UserGroup } from "../api/projectsServices";
import { Typography } from "@mui/material";
import { t } from "i18next";
import { CustomDashboardForm } from "../components/dashboard/CustomDashboardForm";

const emptyBlueprint: Blueprint = {
    name: "",
    userGroups: [],
    elements: [],
};

export const CreateCustomDashboard = () => {
    const initialVisibility = {
        MUTATION: true,
        PROJECT_PHASE: true,
        TARGET_GROUP: true,
        PHYSICAL_APPEARANCE: true,
        OWNERSHIP_BUY: true,
        OWNERSHIP_RENT: true,
        PROJECT_MAP: true,
        RESIDENTIAL_PROJECTS: true,
        DELIVERABLES: true,
        DELAYED_PROJECTS: true,
    };
    const [visibility, setVisibility] = useState(initialVisibility);
    const [newBlueprint, setNewBlueprint] = useState<Blueprint>(emptyBlueprint);
    const [userGroups, setUserGroups] = useState<UserGroup[]>([]);
    const [pdfExport, setPdfExport] = useState(false);

    const { id } = useParams();
    const { setAlert } = useAlert();

    useEffect(() => {
        if (id) {
            const fetchBlueprint = async () => {
                try {
                    const blueprint = await getBlueprint(id);

                    Object.keys(initialVisibility).forEach((key) => {
                        if (!blueprint.elements.includes(key as VisibilityElement)) {
                            initialVisibility[key as keyof typeof initialVisibility] = false;
                        }
                    });
                    setVisibility({ ...initialVisibility });
                    setNewBlueprint(blueprint);
                } catch (error) {
                    if (error instanceof Error) setAlert(error.message, "error");
                }
            };

            fetchBlueprint();
        } else {
            setVisibility({ ...initialVisibility });
            setNewBlueprint(emptyBlueprint);
            setUserGroups([]);
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [id]);
    if (!visibility) {
        return null;
    }
    return (
        <>
            <Typography variant="h5">{t("dashboard.blueprints.title")}</Typography>
            <CustomDashboardForm
                visibility={visibility}
                newBlueprint={newBlueprint}
                setNewBlueprint={setNewBlueprint}
                userGroups={userGroups}
                setUserGroups={setUserGroups}
                setPdfExport={setPdfExport}
                pdfExport={pdfExport}
            />
            <DashboardCharts isPdf={pdfExport} visibility={visibility} setVisibility={setVisibility} />
        </>
    );
};
