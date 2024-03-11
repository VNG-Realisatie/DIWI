import { FormControl, FormControlLabel, FormLabel, Radio, RadioGroup, Stack, Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { HouseBlock } from "../types";
import { ChangeEvent } from "react";

export type ProgrammingProps = {
    projectForm: HouseBlock;
    setProjectForm(project: HouseBlock): void;
};

export const Programming = ({ projectForm, setProjectForm }: ProgrammingProps) => {
    const translationPath = "createProject.houseBlocksForm.programming";
    return (
        <WizardCard>
            <Typography fontWeight={600} mb={2}>
                {t(`${translationPath}.title`)}
            </Typography>
            <Stack direction="row" alignItems="center" my={1}>
                <FormControl>
                    <RadioGroup
                        name="programming"
                        value={projectForm?.programming !== null ? (projectForm?.programming === true ? "yes" : "no") : null}
                        onChange={(e: ChangeEvent<HTMLInputElement>) => setProjectForm({ ...projectForm, programming: e.target.value === "ali" })}
                    >
                        <FormControlLabel value="yes" control={<Radio />} label={t("generic.yes")} />
                        <FormControlLabel value="no" control={<Radio />} label={t("generic.no")} />
                    </RadioGroup>
                </FormControl>
            </Stack>
        </WizardCard>
    );
};
