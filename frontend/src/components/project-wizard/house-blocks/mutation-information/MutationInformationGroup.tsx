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
    houseBlock: HouseBlock;
    setHouseBlock(houseBlock: HouseBlock): void;
    readOnly: boolean;
};

export const MutationInformationGroup = ({ houseBlock, setHouseBlock, readOnly }: MutationInformationProps) => {
    return (
        <WizardCard>
            <Typography fontWeight={600} mb={2}>
                {t("createProject.houseBlocksForm.mutationData")}
            </Typography>
            <GrossPlanCapacityInput
                readOnly={readOnly}
                houseBlockGrossPlan={houseBlock.mutation.grossPlanCapacity}
                updateHouseBlockGrossPlan={(e) =>
                    setHouseBlock({
                        ...houseBlock,
                        mutation: {
                            ...houseBlock.mutation,
                            grossPlanCapacity: e,
                        },
                    })
                }
            />
            <DemolitionPlanCapacityInput
                readOnly={readOnly}
                houseBlockDemolitionPlan={houseBlock.mutation.demolition}
                updateHouseBlockDemolitionPlan={(e) =>
                    setHouseBlock({
                        ...houseBlock,
                        mutation: {
                            ...houseBlock.mutation,
                            demolition: e,
                        },
                    })
                }
            />
            <NetPlanCapacityInput
                readOnly={readOnly}
                houseBlockNetPlan={houseBlock.mutation.netPlanCapacity}
                updateHouseBlockNetPlan={(e) =>
                    setHouseBlock({
                        ...houseBlock,
                        mutation: {
                            ...houseBlock.mutation,
                            netPlanCapacity: e,
                        },
                    })
                }
            />
            <MutationKindSelect
                readOnly={readOnly}
                houseBlockMutationKind={houseBlock.mutation.mutationKind}
                updateHouseBlockMutationKind={(event: SelectChangeEvent<MutationSelectOptions[]>) => {
                    const {
                        target: { value },
                    } = event;
                    if (typeof value !== "string") {
                        setHouseBlock({
                            ...houseBlock,
                            mutation: {
                                ...houseBlock.mutation,
                                mutationKind: value,
                            },
                        });
                    }
                }}
            />
        </WizardCard>
    );
};
