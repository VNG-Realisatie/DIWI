import { SelectChangeEvent, Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { HouseBlock } from "../../../../types/houseBlockTypes";
import { MutationKindSelect } from "./MutationKindSelect";
import { MutationKind } from "../../../../types/enums";
import { SingleNumberInput } from "../../../project/inputs/SingleNumberInput";

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
            <SingleNumberInput
                name={t("createProject.houseBlocksForm.amount")}
                value={houseBlock.mutation.amount ?? null}
                onChange={(e: any) =>
                    setHouseBlock({
                        ...houseBlock,
                        mutation: {
                            ...houseBlock.mutation,
                            amount: e,
                        },
                    })
                }
                readOnly={readOnly}
                mandatory={true}
                error={t("wizard.houseBlocks.mutationAmountWarning")}
                isInputLabel={true}
            />
            <MutationKindSelect
                readOnly={readOnly}
                houseBlockMutationKind={houseBlock.mutation.kind}
                updateHouseBlockMutationKind={(event: SelectChangeEvent<MutationKind | null>) => {
                    const {
                        target: { value },
                    } = event;

                    value &&
                        setHouseBlock({
                            ...houseBlock,
                            mutation: {
                                ...houseBlock.mutation,
                                kind: value as MutationKind,
                            },
                        });
                }}
            />
        </WizardCard>
    );
};
