import { useState } from "react";
import { Button, Grid } from "@mui/material";
import TextInput from "../project/inputs/TextInput";

import UserGroupSelect from "../../widgets/UserGroupSelect";
import { UserGroup } from "../../api/projectsServices";
import { Blueprint, createBlueprint, VisibilityElement } from "../../api/dashboardServices";
import useAlert from "../../hooks/useAlert";
import { t } from "i18next";
import { Visibility } from "./DashboardCharts";

const emptyBlueprint: Blueprint = {
    name: "",
    userGroups: [],
    elements: [],
};

type Props = {
    visibility: Visibility;
};

export const CustomDashboardForm = ({ visibility }: Props) => {
    const [userGroups, setUserGroups] = useState<UserGroup[]>([]);
    const [newBlueprint, setNewBlueprint] = useState<Blueprint>(emptyBlueprint);
    const { setAlert } = useAlert();

    const buttonDisabled = !newBlueprint.name || userGroups.length === 0 || !Object.values(visibility).some((value) => value === true);

    const handleSave = async () => {
        const elementsToAdd = Object.entries(visibility)
            .filter(([_, value]) => value === true)
            .map(([key]) => key);
        const updatedBlueprint = {
            ...newBlueprint,
            userGroups: userGroups.map((group) => ({ uuid: group.uuid })),
            elements: elementsToAdd as VisibilityElement[],
        };

        try {
            await createBlueprint(updatedBlueprint);
            setAlert(t("dashboard.blueprints.successfullyCreated"), "success");
            setNewBlueprint(emptyBlueprint);
            setUserGroups([]);
        } catch (error: unknown) {
            if (error instanceof Error) setAlert(error.message, "error");
        }
    };
    return (
        <Grid container spacing={2} alignItems="center">
            <Grid item xs={4}>
                <TextInput
                    readOnly={false}
                    value={newBlueprint.name}
                    setValue={(event: React.ChangeEvent<HTMLInputElement>) => {
                        setNewBlueprint({ ...newBlueprint, name: event.target.value });
                    }}
                    mandatory={true}
                />
            </Grid>
            <Grid item xs={4}>
                <UserGroupSelect readOnly={false} userGroup={userGroups} setUserGroup={setUserGroups} mandatory={true} errorText="" />
            </Grid>
            <Grid item xs={4}>
                <Button variant="contained" onClick={handleSave} disabled={buttonDisabled}>
                    {t("dashboard.blueprints.saveBlueprint")}
                </Button>
            </Grid>
        </Grid>
    );
};
