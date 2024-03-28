import { SelectChangeEvent, Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { HouseBlock } from "../types";

import { GrossPlanCapacityInput } from "./GrossPlanCapacityInput";
import { NetPlanCapacityInput } from "./NetPlanCapacityInput";
import { DemolitionPlanCapacityInput } from "./DemolitionPlanCapacityInput";
import { MutationKindSelect } from "./MutationKindSelect";
import { MutationSelectOptions } from "../../../../types/enums";

export type MutationInformationProps = {
    projectForm: HouseBlock;
    setProjectForm(project: HouseBlock): void;
    readOnly: boolean;
};

export const MutationInformationGroup = ({ projectForm, setProjectForm, readOnly }: MutationInformationProps) => {
    return (
        <WizardCard>
            <Typography fontWeight={600} mb={2}>
                {t("createProject.houseBlocksForm.mutationData")}
            </Typography>
            <GrossPlanCapacityInput
                readOnly={readOnly}
                houseBlockGrossPlan={projectForm.mutation.grossPlanCapacity}
                updateHouseBlockGrossPlan={(e) =>
                    setProjectForm({
                        ...projectForm,
                        mutation: {
                            ...projectForm.mutation,
                            grossPlanCapacity: e,
                        },
                    })
                }
            />
            <DemolitionPlanCapacityInput
                readOnly={readOnly}
                houseBlockDemolitionPlan={projectForm.mutation.demolition}
                updateHouseBlockDemolitionPlan={(e) =>
                    setProjectForm({
                        ...projectForm,
                        mutation: {
                            ...projectForm.mutation,
                            demolition: e,
                        },
                    })
                }
            />
            <NetPlanCapacityInput
                readOnly={readOnly}
                houseBlockNetPlan={projectForm.mutation.netPlanCapacity}
                updateHouseBlockNetPlan={(e) =>
                    setProjectForm({
                        ...projectForm,
                        mutation: {
                            ...projectForm.mutation,
                            netPlanCapacity: e,
                        },
                    })
                }
            />
            <MutationKindSelect
                readOnly={readOnly}
                houseBlockMutationKind={projectForm.mutation.mutationKind}
                updateHouseBlockMutationKind={(event: SelectChangeEvent<MutationSelectOptions[]>) => {
                    const {
                        target: { value },
                    } = event;
                    if (typeof value !== "string") {
                        setProjectForm({
                            ...projectForm,
                            mutation: {
                                ...projectForm.mutation,
                                mutationKind: value,
                            },
                        });
                    }
                }}
            />
        </WizardCard>
    );
};
