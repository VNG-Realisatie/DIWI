import { Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { HouseBlock } from "../../../../types/houseBlockTypes";
import { SingleNumberInput } from "../../../project/inputs/SingleNumberInput";
import { mutationKindOptions } from "../../../../types/enums";
import CategoryInput from "../../../project/inputs/CategoryInput";

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
            </Typography>
            <SingleNumberInput
                name={t("createProject.houseBlocksForm.amount")}
                value={houseBlock.mutation.amount ?? null}
                onChange={(e) =>
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
                isDemolition={houseBlock.mutation.kind === "DEMOLITION"}
            />
            <CategoryInput
                readOnly={readOnly}
                values={houseBlock.mutation.kind ? { id: houseBlock.mutation.kind, name: houseBlock.mutation.kind } : null}
                setValue={(_, newValue) => {
                    newValue &&
                        setHouseBlock({
                            ...houseBlock,
                            mutation: {
                                ...houseBlock.mutation,
                                kind: newValue.id,
                            },
                        });
                }}
                mandatory={true}
                title={t("createProject.houseBlocksForm.mutationType")}
                options={mutationKindOptions.map((value) => ({ id: value, name: value }))}
                multiple={false}
                error={t("wizard.houseBlocks.mutationKindWarning")}
                translationPath="createProject.houseBlocksForm."
                tooltipInfoText="tooltipInfo.mutatieSoort."
            />
        </WizardCard>
    );
};
