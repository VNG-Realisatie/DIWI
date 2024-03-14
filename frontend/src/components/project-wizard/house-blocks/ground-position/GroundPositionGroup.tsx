import { Stack, Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { HouseBlock } from "../types";
import { SingleNumberInput } from "../physical-appearence/SingleNumberInput";

export type GroundPositionInformationProps = {
    projectForm: HouseBlock;
    setProjectForm(project: HouseBlock): void;
    edit: boolean;
    editForm: boolean;
};

export const GroundPositionGroup = ({ projectForm, setProjectForm, edit, editForm }: GroundPositionInformationProps) => {
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
                property={projectForm?.physicalAppeareance.noPermissionOwner}
                update={(e) =>
                    projectForm &&
                    setProjectForm({
                        ...projectForm,
                        physicalAppeareance: {
                            ...projectForm.physicalAppeareance,
                            noPermissionOwner: e,
                        },
                    })
                }
                edit={edit}
                editForm={editForm}
                value="noPermissionOwner"
                translationPath={translationPath}
            />
            <SingleNumberInput
                property={projectForm?.physicalAppeareance.intentionPermissionOwner}
                update={(e) =>
                    projectForm &&
                    setProjectForm({
                        ...projectForm,
                        physicalAppeareance: {
                            ...projectForm.physicalAppeareance,
                            intentionPermissionOwner: e,
                        },
                    })
                }
                edit={edit}
                editForm={editForm}
                value="intentionPermissionOwner"
                translationPath={translationPath}
            />
            <SingleNumberInput
                property={projectForm?.physicalAppeareance.formalPermissionOwner}
                update={(e) =>
                    projectForm &&
                    setProjectForm({
                        ...projectForm,
                        physicalAppeareance: {
                            ...projectForm.physicalAppeareance,
                            formalPermissionOwner: e,
                        },
                    })
                }
                edit={edit}
                editForm={editForm}
                value="formalPermissionOwner"
                translationPath={translationPath}
            />
        </WizardCard>
    );
};
