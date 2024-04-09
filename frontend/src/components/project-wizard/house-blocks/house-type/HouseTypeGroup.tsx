import { Stack, Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { HouseBlock } from "../../../../types/houseBlockTypes";
import { SingleNumberInput } from "../physical-appearence/SingleNumberInput";

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
                property={houseBlock?.houseType?.meergezinswoning}
                update={(e) =>
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
                value="meergezinswoning"
                translationPath={translationPath}
            />
            <SingleNumberInput
                property={houseBlock?.houseType?.eengezinswoning}
                update={(e) =>
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
                value="eengezinswoning"
                translationPath={translationPath}
            />
        </WizardCard>
    );
};
