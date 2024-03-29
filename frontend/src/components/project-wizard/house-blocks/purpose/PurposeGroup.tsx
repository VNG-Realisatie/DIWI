import { Stack, Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { HouseBlock } from "../types";
import { SingleNumberInput } from "../physical-appearence/SingleNumberInput";

export type PurposeInformationProps = {
    houseBlock: HouseBlock;
    setHouseBlock(houseBlock: HouseBlock): void;
    readOnly: boolean;
};

export const PurposeGroup = ({ houseBlock, setHouseBlock, readOnly }: PurposeInformationProps) => {
    const translationPath = "createProject.houseBlocksForm.purpose";
    return (
        <WizardCard>
            <Typography fontWeight={600} mb={2}>
                {t(`${translationPath}.title`)}
            </Typography>
            <Stack direction="row" alignItems="center" spacing={3} my={1}>
                <Typography fontWeight={600} flex={3}>
                    {t(`${translationPath}.typeOfResidents`)}
                </Typography>
                <Typography fontWeight={600} flex={1}>
                    {t(`${translationPath}.value`)}
                </Typography>
            </Stack>
            <SingleNumberInput
                property={houseBlock?.purpose.regular}
                update={(e) =>
                    houseBlock &&
                    setHouseBlock({
                        ...houseBlock,
                        purpose: {
                            ...houseBlock.purpose,
                            regular: e,
                        },
                    })
                }
                readOnly={readOnly}
                value="regular"
                translationPath={translationPath}
            />
            <SingleNumberInput
                property={houseBlock?.purpose.youth}
                update={(e) =>
                    houseBlock &&
                    setHouseBlock({
                        ...houseBlock,
                        purpose: {
                            ...houseBlock.purpose,
                            youth: e,
                        },
                    })
                }
                readOnly={readOnly}
                value="youth"
                translationPath={translationPath}
            />
            <SingleNumberInput
                property={houseBlock?.purpose.student}
                update={(e) =>
                    houseBlock &&
                    setHouseBlock({
                        ...houseBlock,
                        purpose: {
                            ...houseBlock.purpose,
                            student: e,
                        },
                    })
                }
                readOnly={readOnly}
                value="student"
                translationPath={translationPath}
            />
            <SingleNumberInput
                property={houseBlock?.purpose.elderly}
                update={(e) =>
                    houseBlock &&
                    setHouseBlock({
                        ...houseBlock,
                        purpose: {
                            ...houseBlock.purpose,
                            elderly: e,
                        },
                    })
                }
                readOnly={readOnly}
                value="elderly"
                translationPath={translationPath}
            />
            <SingleNumberInput
                property={houseBlock?.purpose.largeFamilies}
                update={(e) =>
                    houseBlock &&
                    setHouseBlock({
                        ...houseBlock,
                        purpose: {
                            ...houseBlock.purpose,
                            largeFamilies: e,
                        },
                    })
                }
                readOnly={readOnly}
                value="largeFamilies"
                translationPath={translationPath}
            />
            <SingleNumberInput
                property={houseBlock?.purpose.ghz}
                update={(e) =>
                    houseBlock &&
                    setHouseBlock({
                        ...houseBlock,
                        purpose: {
                            ...houseBlock.purpose,
                            ghz: e,
                        },
                    })
                }
                readOnly={readOnly}
                value="ghz"
                translationPath={translationPath}
            />
        </WizardCard>
    );
};
