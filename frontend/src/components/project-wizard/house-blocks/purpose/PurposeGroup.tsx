import { Stack, Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { HouseBlock } from "../types";
import { SingleNumberInput } from "./SingleNumberInput";

export type PurposeInformationProps = {
    projectForm: HouseBlock;
    setProjectForm(project: HouseBlock): void;
    edit: boolean;
    editForm: boolean;
};

export const PurposeGroup = ({ projectForm, setProjectForm, edit, editForm }: PurposeInformationProps) => {
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
                property={projectForm?.purpose.regular}
                update={(e) =>
                    projectForm &&
                    setProjectForm({
                        ...projectForm,
                        purpose: {
                            ...projectForm.purpose,
                            regular: e,
                        },
                    })
                }
                edit={edit}
                editForm={editForm}
                value="regular"
                translationPath={translationPath}
            />
            <SingleNumberInput
                property={projectForm?.purpose.youth}
                update={(e) =>
                    projectForm &&
                    setProjectForm({
                        ...projectForm,
                        purpose: {
                            ...projectForm.purpose,
                            youth: e,
                        },
                    })
                }
                edit={edit}
                editForm={editForm}
                value="youth"
                translationPath={translationPath}
            />
            <SingleNumberInput
                property={projectForm?.purpose.student}
                update={(e) =>
                    projectForm &&
                    setProjectForm({
                        ...projectForm,
                        purpose: {
                            ...projectForm.purpose,
                            student: e,
                        },
                    })
                }
                edit={edit}
                editForm={editForm}
                value="student"
                translationPath={translationPath}
            />
            <SingleNumberInput
                property={projectForm?.purpose.elderly}
                update={(e) =>
                    projectForm &&
                    setProjectForm({
                        ...projectForm,
                        purpose: {
                            ...projectForm.purpose,
                            elderly: e,
                        },
                    })
                }
                edit={edit}
                editForm={editForm}
                value="elderly"
                translationPath={translationPath}
            />
            <SingleNumberInput
                property={projectForm?.purpose.largeFamilies}
                update={(e) =>
                    projectForm &&
                    setProjectForm({
                        ...projectForm,
                        purpose: {
                            ...projectForm.purpose,
                            largeFamilies: e,
                        },
                    })
                }
                edit={edit}
                editForm={editForm}
                value="largeFamilies"
                translationPath={translationPath}
            />
            <SingleNumberInput
                property={projectForm?.purpose.ghz}
                update={(e) =>
                    projectForm &&
                    setProjectForm({
                        ...projectForm,
                        purpose: {
                            ...projectForm.purpose,
                            ghz: e,
                        },
                    })
                }
                edit={edit}
                editForm={editForm}
                value="ghz"
                translationPath={translationPath}
            />
        </WizardCard>
    );
};
