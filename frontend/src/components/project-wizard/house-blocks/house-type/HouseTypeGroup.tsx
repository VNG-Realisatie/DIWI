import { Stack, Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { HouseBlock } from "../../../../types/houseBlockTypes";
import { SingleNumberInput } from "../../../project/inputs/SingleNumberInput";
import { TooltipInfo } from "../../../../widgets/TooltipInfo";

export type HouseTypeInformationProps = {
    houseBlock: HouseBlock;
    setHouseBlock(houseBlock: HouseBlock): void;
    readOnly: boolean;
};

export const HouseTypeGroup = ({ houseBlock, setHouseBlock, readOnly }: HouseTypeInformationProps) => {
    const translationPath = "createProject.houseBlocksForm.houseType";
    return (
        <WizardCard>
            <Typography fontWeight={600} mb={2}>
                {t(`${translationPath}.title`)}
                <TooltipInfo text={t("tooltipInfo.woningtype.title")} />
            </Typography>
            <Stack direction="row" alignItems="center" spacing={3} my={1}>
                <Typography fontWeight={600} flex={3}>
                    {t(`${translationPath}.type`)}
                </Typography>
                <Typography fontWeight={600} flex={1}>
                    {t(`${translationPath}.value`)}
                </Typography>
            </Stack>
            <SingleNumberInput
                value={houseBlock?.houseType?.meergezinswoning}
                onChange={(e) =>
                    houseBlock &&
                    setHouseBlock({
                        ...houseBlock,
                        houseType: {
                            ...houseBlock.houseType,
                            meergezinswoning: e,
                        },
                    })
                }
                readOnly={readOnly}
                name={t(`${translationPath}.meergezinswoning`)}
                translationPath={translationPath}
                mandatory={false}
                tooltipInfoText={t("tooltipInfo.woningtype.meergezinswoningen")}
            />
            <SingleNumberInput
                value={houseBlock?.houseType?.eengezinswoning}
                onChange={(e) =>
                    houseBlock &&
                    setHouseBlock({
                        ...houseBlock,
                        houseType: {
                            ...houseBlock.houseType,
                            eengezinswoning: e,
                        },
                    })
                }
                readOnly={readOnly}
                name={t(`${translationPath}.eengezinswoning`)}
                translationPath={translationPath}
                mandatory={false}
                tooltipInfoText={t("tooltipInfo.woningtype.eengezinswoningen")}
            />
        </WizardCard>
    );
};
