import { Stack, Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { HouseBlock } from "../types";
import { SingleNumberInput } from "../physical-appearence/SingleNumberInput";

export type PurposeInformationProps = {
    projectForm: HouseBlock;
    setProjectForm(project: HouseBlock): void;
    readOnly: boolean;
};

export const PurposeGroup = ({ projectForm, setProjectForm, readOnly }: PurposeInformationProps) => {
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
                readOnly={readOnly}
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
                readOnly={readOnly}
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
                readOnly={readOnly}
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
                readOnly={readOnly}
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
                readOnly={readOnly}
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
                readOnly={readOnly}
                value="ghz"
                translationPath={translationPath}
            />
        </WizardCard>
    );
};
