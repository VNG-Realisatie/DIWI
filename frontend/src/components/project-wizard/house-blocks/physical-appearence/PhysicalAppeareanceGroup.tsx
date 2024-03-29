import { Stack, Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { HouseBlock } from "../types";
import { SingleNumberInput } from "./SingleNumberInput";

export type PhysicalAppeareanceInformationProps = {
    houseBlock: HouseBlock;
    setHouseBlock(houseBlock: HouseBlock): void;
    readOnly: boolean;
};

export const PhysicalAppeareanceGroup = ({ houseBlock, setHouseBlock, readOnly }: PhysicalAppeareanceInformationProps) => {
    const translationPath = "createProject.houseBlocksForm.physicalAppearance";
    return (
        <WizardCard>
            <Typography fontWeight={600} mb={2}>
                {t(`${translationPath}.title`)}
            </Typography>
            <Stack direction="row" alignItems="center" spacing={3} my={1}>
                <Typography fontWeight={600} flex={3}>
                    {t(`${translationPath}.houseType`)}
                </Typography>
                <Typography fontWeight={600} flex={1}>
                    {t(`${translationPath}.value`)}
                </Typography>
            </Stack>
            <SingleNumberInput
                property={houseBlock?.physicalAppearance?.tussenwoning}
                update={(e) =>
                    houseBlock &&
                    setHouseBlock({
                        ...houseBlock,
                        physicalAppearance: {
                            ...houseBlock.physicalAppearance,
                            tussenwoning: e,
                        },
                    })
                }
                readOnly={readOnly}
                value="tussenwoning"
                translationPath={translationPath}
            />
            <SingleNumberInput
                property={houseBlock?.physicalAppearance?.tweeondereenkap}
                update={(e) =>
                    houseBlock &&
                    setHouseBlock({
                        ...houseBlock,
                        physicalAppearance: {
                            ...houseBlock.physicalAppearance,
                            tweeondereenkap: e,
                        },
                    })
                }
                readOnly={readOnly}
                value="tweeondereenkap"
                translationPath={translationPath}
            />
            <SingleNumberInput
                property={houseBlock?.physicalAppearance?.portiekflat}
                update={(e) =>
                    houseBlock &&
                    setHouseBlock({
                        ...houseBlock,
                        physicalAppearance: {
                            ...houseBlock.physicalAppearance,
                            portiekflat: e,
                        },
                    })
                }
                readOnly={readOnly}
                value="portiekflat"
                translationPath={translationPath}
            />
            <SingleNumberInput
                property={houseBlock?.physicalAppearance?.hoekwoning}
                update={(e) =>
                    houseBlock &&
                    setHouseBlock({
                        ...houseBlock,
                        physicalAppearance: {
                            ...houseBlock.physicalAppearance,
                            hoekwoning: e,
                        },
                    })
                }
                readOnly={readOnly}
                value="hoekwoning"
                translationPath={translationPath}
            />
            <SingleNumberInput
                property={houseBlock?.physicalAppearance?.vrijstaand}
                update={(e) =>
                    houseBlock &&
                    setHouseBlock({
                        ...houseBlock,
                        physicalAppearance: {
                            ...houseBlock.physicalAppearance,
                            vrijstaand: e,
                        },
                    })
                }
                readOnly={readOnly}
                value="vrijstaand"
                translationPath={translationPath}
            />
            <SingleNumberInput
                property={houseBlock?.physicalAppearance?.gallerijflat}
                update={(e) =>
                    houseBlock &&
                    setHouseBlock({
                        ...houseBlock,
                        physicalAppearance: {
                            ...houseBlock.physicalAppearance,
                            gallerijflat: e,
                        },
                    })
                }
                readOnly={readOnly}
                value="gallerijflat"
                translationPath="createProject.houseBlocksForm.physicalAppearance"
            />
        </WizardCard>
    );
};
