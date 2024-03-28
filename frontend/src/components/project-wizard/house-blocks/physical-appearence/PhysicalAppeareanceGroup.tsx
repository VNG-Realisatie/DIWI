import { Stack, Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { HouseBlock } from "../types";
import { SingleNumberInput } from "./SingleNumberInput";

export type PhysicalAppeareanceInformationProps = {
    projectForm: HouseBlock;
    setProjectForm(project: HouseBlock): void;
    readOnly: boolean;
};

export const PhysicalAppeareanceGroup = ({ projectForm, setProjectForm, readOnly }: PhysicalAppeareanceInformationProps) => {
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
                property={projectForm?.physicalAppearance?.tussenwoning}
                update={(e) =>
                    projectForm &&
                    setProjectForm({
                        ...projectForm,
                        physicalAppearance: {
                            ...projectForm.physicalAppearance,
                            tussenwoning: e,
                        },
                    })
                }
                readOnly={readOnly}
                value="tussenwoning"
                translationPath={translationPath}
            />
            <SingleNumberInput
                property={projectForm?.physicalAppearance?.tweeondereenkap}
                update={(e) =>
                    projectForm &&
                    setProjectForm({
                        ...projectForm,
                        physicalAppearance: {
                            ...projectForm.physicalAppearance,
                            tweeondereenkap: e,
                        },
                    })
                }
                readOnly={readOnly}
                value="tweeondereenkap"
                translationPath={translationPath}
            />
            <SingleNumberInput
                property={projectForm?.physicalAppearance?.portiekflat}
                update={(e) =>
                    projectForm &&
                    setProjectForm({
                        ...projectForm,
                        physicalAppearance: {
                            ...projectForm.physicalAppearance,
                            portiekflat: e,
                        },
                    })
                }
                readOnly={readOnly}
                value="portiekflat"
                translationPath={translationPath}
            />
            <SingleNumberInput
                property={projectForm?.physicalAppearance?.hoekwoning}
                update={(e) =>
                    projectForm &&
                    setProjectForm({
                        ...projectForm,
                        physicalAppearance: {
                            ...projectForm.physicalAppearance,
                            hoekwoning: e,
                        },
                    })
                }
                readOnly={readOnly}
                value="hoekwoning"
                translationPath={translationPath}
            />
            <SingleNumberInput
                property={projectForm?.physicalAppearance?.vrijstaand}
                update={(e) =>
                    projectForm &&
                    setProjectForm({
                        ...projectForm,
                        physicalAppearance: {
                            ...projectForm.physicalAppearance,
                            vrijstaand: e,
                        },
                    })
                }
                readOnly={readOnly}
                value="vrijstaand"
                translationPath={translationPath}
            />
            <SingleNumberInput
                property={projectForm?.physicalAppearance?.gallerijflat}
                update={(e) =>
                    projectForm &&
                    setProjectForm({
                        ...projectForm,
                        physicalAppearance: {
                            ...projectForm.physicalAppearance,
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
