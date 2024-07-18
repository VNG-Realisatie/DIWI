import { useState } from "react";
import { CustomDashboardForm } from "../components/dashboard/CustomDashboardForm";
import { DashboardCharts } from "../components/dashboard/DashboardCharts";

export const CreateCustomDashboard = () => {
    const [visibility, setVisibility] = useState({
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
    });

    if (!visibility) {
        return null;
    }

    return (
        <>
            <CustomDashboardForm visibility={visibility} />
            <DashboardCharts visibility={visibility} setVisibility={setVisibility} customizable={true} />
        </>
    );
};
