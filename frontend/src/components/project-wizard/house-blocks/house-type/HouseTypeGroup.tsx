import { Stack, Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { HouseBlock } from "../types";
import { SingleNumberInput } from "../physical-appearence/SingleNumberInput";

export type HouseTypeInformationProps = {
    projectForm: HouseBlock;
    setProjectForm(project: HouseBlock): void;
    edit: boolean;
    editForm: boolean;
};

export const HouseTypeGroup = ({ projectForm, setProjectForm, edit, editForm }: HouseTypeInformationProps) => {
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
                property={projectForm?.physicalAppearance?.meergezinswoning}
                update={(e) =>
                    projectForm &&
                    setProjectForm({
                        ...projectForm,
                        houseType: {
                            ...projectForm.houseType,
                            meergezinswoning: e,
                        },
                    })
                }
                edit={edit}
                editForm={editForm}
                value="meergezinswoning"
                translationPath={translationPath}
            />
            <SingleNumberInput
                property={projectForm?.physicalAppearance?.eengezinswoning}
                update={(e) =>
                    projectForm &&
                    setProjectForm({
                        ...projectForm,
                        houseType: {
                            ...projectForm.houseType,
                            eengezinswoning: e,
                        },
                    })
                }
                edit={edit}
                editForm={editForm}
                value="eengezinswoning"
                translationPath={translationPath}
            />
        </WizardCard>
    );
};
