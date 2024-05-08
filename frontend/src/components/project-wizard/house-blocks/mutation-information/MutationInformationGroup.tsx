import { SelectChangeEvent, Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { HouseBlock } from "../../../../types/houseBlockTypes";

import { AmountInput } from "./AmountInput";
import { MutationKindSelect } from "./MutationKindSelect";
import { MutationKind } from "../../../../types/enums";
import { TooltipInfo } from "../../../../widgets/TooltipInfo";

export type MutationInformationProps = {
    houseBlock: HouseBlock;
    setHouseBlock(houseBlock: HouseBlock): void;
    readOnly: boolean;
};

export const MutationInformationGroup = ({ houseBlock, setHouseBlock, readOnly }: MutationInformationProps) => {
    return (
        <WizardCard>
            <Typography
                fontWeight={600}
                mb={2}
                sx={{
                    display: "flex",
                }}
            >
                {t("createProject.houseBlocksForm.mutationData")}
                <TooltipInfo text={t("tooltipInfo.mutatieGegevens.title")} />
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
