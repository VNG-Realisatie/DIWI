import { Stack, Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { HouseBlock } from "../../../../types/houseBlockTypes";
import { SingleNumberInput } from "../../../project/inputs/SingleNumberInput";

export type GroundPositionInformationProps = {
    houseBlock: HouseBlock;
    setHouseBlock(houseBlock: HouseBlock): void;
    readOnly: boolean;
};

export const GroundPositionGroup = ({ houseBlock, setHouseBlock, readOnly }: GroundPositionInformationProps) => {
    const translationPath = "createProject.houseBlocksForm.groundPosition";
    return (
        <WizardCard>
            <Typography fontWeight={600} mb={2}>
                {t(`${translationPath}.title`)}
            </Typography>
            <Stack direction="row" alignItems="center" spacing={3} my={1}>
                <Typography fontWeight={600} flex={3}>
                    {t(`${translationPath}.situation`)}
                </Typography>
                <Typography fontWeight={600} flex={1}>
                    {t(`${translationPath}.value`)}
                </Typography>
            </Stack>
            <SingleNumberInput
                value={houseBlock?.groundPosition?.noPermissionOwner}
                onChange={(e) =>
                    houseBlock &&
                    setHouseBlock({
                        ...houseBlock,
                        groundPosition: {
                            ...houseBlock.groundPosition,
                            noPermissionOwner: e,
                        },
                    })
                }
                readOnly={readOnly}
                name={t(`${translationPath}.noPermissionOwner`)}
                translationPath={translationPath}
                mandatory={false}
            />
            <SingleNumberInput
                value={houseBlock?.groundPosition?.intentionPermissionOwner}
                onChange={(e) =>
                    houseBlock &&
                    setHouseBlock({
                        ...houseBlock,
                        groundPosition: {
                            ...houseBlock.groundPosition,
                            intentionPermissionOwner: e,
                        },
                    })
                }
                readOnly={readOnly}
                name={t(`${translationPath}.intentionPermissionOwner`)}
                translationPath={translationPath}
                mandatory={false}
            />
            <SingleNumberInput
                value={houseBlock?.groundPosition?.formalPermissionOwner}
                onChange={(e) =>
                    houseBlock &&
                    setHouseBlock({
                        ...houseBlock,
                        groundPosition: {
                            ...houseBlock.groundPosition,
                            formalPermissionOwner: e,
                        },
                    })
                }
                readOnly={readOnly}
                name={t(`${translationPath}.formalPermissionOwner`)}
                translationPath={translationPath}
                mandatory={false}
            />
        </WizardCard>
    );
};
