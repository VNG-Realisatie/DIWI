import { Stack, Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { HouseBlock } from "../types";
import { SingleNumberInput } from "../physical-appearence/SingleNumberInput";

export type GroundPositionInformationProps = {
    projectForm: HouseBlock;
    setProjectForm(project: HouseBlock): void;
    readOnly: boolean;
};

export const GroundPositionGroup = ({ projectForm, setProjectForm, readOnly }: GroundPositionInformationProps) => {
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
                property={projectForm?.groundPosition?.noPermissionOwner}
                update={(e) =>
                    projectForm &&
                    setProjectForm({
                        ...projectForm,
                        groundPosition: {
                            ...projectForm.groundPosition,
                            noPermissionOwner: e,
                        },
                    })
                }
                readOnly={readOnly}
                value="noPermissionOwner"
                translationPath={translationPath}
            />
            <SingleNumberInput
                property={projectForm?.groundPosition?.intentionPermissionOwner}
                update={(e) =>
                    projectForm &&
                    setProjectForm({
                        ...projectForm,
                        groundPosition: {
                            ...projectForm.groundPosition,
                            intentionPermissionOwner: e,
                        },
                    })
                }
                readOnly={readOnly}
                value="intentionPermissionOwner"
                translationPath={translationPath}
            />
            <SingleNumberInput
                property={projectForm?.groundPosition?.formalPermissionOwner}
                update={(e) =>
                    projectForm &&
                    setProjectForm({
                        ...projectForm,
                        groundPosition: {
                            ...projectForm.groundPosition,
                            formalPermissionOwner: e,
                        },
                    })
                }
                readOnly={readOnly}
                value="formalPermissionOwner"
                translationPath={translationPath}
            />
        </WizardCard>
    );
};
