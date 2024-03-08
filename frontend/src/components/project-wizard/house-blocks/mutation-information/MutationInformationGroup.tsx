import { Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { MutationInformations } from "../types";

import { GrossPlanCapacityInput } from "./GrossPlanCapicityInput";
import { NetPlanCapacityInput } from "./NetPlanCapicityInput";
import { DemolitionPlanCapacityInput } from "./DemolitionPlanCapicityInput";
import { MutationKindSelect } from "./MutationKindSelect";

export type MutationInformationProps = {
    projectForm: MutationInformations;
    setProjectForm(project: MutationInformations): void;
};

export const MutationInformationGroup = ({ projectForm, setProjectForm }: MutationInformationProps) => {
    return (
        <WizardCard>
            <Typography fontWeight={600} mb={2}>
                {t("createProject.houseBlocksForm.mutationData")}
            </Typography>
            <GrossPlanCapacityInput projectForm={projectForm} setProjectForm={setProjectForm} />
            <DemolitionPlanCapacityInput projectForm={projectForm} setProjectForm={setProjectForm} />
            <NetPlanCapacityInput projectForm={projectForm} setProjectForm={setProjectForm} />
            <MutationKindSelect projectForm={projectForm} setProjectForm={setProjectForm} />
        </WizardCard>
    );
};
