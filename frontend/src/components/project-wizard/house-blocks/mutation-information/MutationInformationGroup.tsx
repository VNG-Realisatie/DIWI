import { SelectChangeEvent, Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { HouseBlock } from "../../../../types/houseBlockTypes";

import { AmountInput } from "./AmountInput";
import { MutationKindSelect } from "./MutationKindSelect";
import { MutationKind } from "../../../../types/enums";

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
            <AmountInput
                readOnly={readOnly}
                houseBlockAmount={houseBlock.mutation.amount ?? 0}
                updateHouseBlockAmount={(e) =>
                    setHouseBlock({
                        ...houseBlock,
                        mutation: {
                            ...houseBlock.mutation,
                            amount: e,
                        },
                    })
                }
            />
            <MutationKindSelect
                readOnly={readOnly}
                houseBlockMutationKind={houseBlock.mutation.kind}
                updateHouseBlockMutationKind={(event: SelectChangeEvent<MutationKind>) => {
                    const {
                        target: { value },
                    } = event;
                    if (typeof value !== "string") {
                        setHouseBlock({
                            ...houseBlock,
                            mutation: {
                                ...houseBlock.mutation,
                                kind: value,
                            },
                        });
                    }
                }}
            />
        </WizardCard>
    );
};
