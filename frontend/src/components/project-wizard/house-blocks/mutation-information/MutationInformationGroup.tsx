import { Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { HouseBlock } from "../types";

import { GrossPlanCapacityInput } from "./GrossPlanCapacityInput";
import { NetPlanCapacityInput } from "./NetPlanCapacityInput";
import { DemolitionPlanCapacityInput } from "./DemolitionPlanCapacityInput";
import { MutationKindSelect } from "./MutationKindSelect";

export type MutationInformationProps = {
    projectForm: HouseBlock;
    setProjectForm(project: HouseBlock): void;
    edit: boolean;
    editForm: boolean;
};

export const MutationInformationGroup = ({ projectForm, setProjectForm, edit, editForm }: MutationInformationProps) => {
    return (
        <WizardCard>
            <Typography fontWeight={600} mb={2}>
                {t("createProject.houseBlocksForm.mutationData")}
            </Typography>
            <GrossPlanCapacityInput edit={edit} editForm={editForm} projectForm={projectForm} setProjectForm={setProjectForm} />
            <DemolitionPlanCapacityInput edit={edit} editForm={editForm} projectForm={projectForm} setProjectForm={setProjectForm} />
            <NetPlanCapacityInput edit={edit} editForm={editForm} projectForm={projectForm} setProjectForm={setProjectForm} />
            <MutationKindSelect edit={edit} editForm={editForm} projectForm={projectForm} setProjectForm={setProjectForm} />
        </WizardCard>
    );
};
