import { Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { HouseBlock } from "../../../../types/houseBlockTypes";

import { AmountInput } from "./AmountInput";
import { MutationKind, mutationKindOptions } from "../../../../types/enums";
import CategoryInput from "../../../project/inputs/CategoryInput";

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
                houseBlockAmount={houseBlock.mutation.amount ?? null}
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
            <CategoryInput
                readOnly={readOnly}
                values={houseBlock.mutation.kind}
                setValue={(_, newValue) => {
                    newValue &&
                        setHouseBlock({
                            ...houseBlock,
                            mutation: {
                                ...houseBlock.mutation,
                                kind: newValue as MutationKind,
                            },
                        });
                }}
                mandatory={true}
                title={t("createProject.houseBlocksForm.mutationType")}
                options={mutationKindOptions}
                multiple={false}
                error={t("wizard.houseBlocks.mutationKindWarning")}
                translationPath="createProject.houseBlocksForm."
            />
        </WizardCard>
    );
};
