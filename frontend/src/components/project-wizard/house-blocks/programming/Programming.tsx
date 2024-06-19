import { FormControl, FormControlLabel, Radio, RadioGroup, Stack, Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { HouseBlock } from "../../../../types/houseBlockTypes";
import { ChangeEvent } from "react";

export type ProgrammingProps = {
    houseBlock: HouseBlock;
    setHouseBlock(houseBlock: HouseBlock): void;
    readOnly: boolean;
};

export const Programming = ({ houseBlock, setHouseBlock, readOnly }: ProgrammingProps) => {
    const translationPath = "createProject.houseBlocksForm.programming";
    return (
        <WizardCard>
            <Typography fontWeight={600} mb={2}>
                {t(`${translationPath}.title`)}
            </Typography>
            <Stack direction="row" alignItems="center" my={1}>
                <FormControl disabled={readOnly}>
                    <RadioGroup
                        id="programming"
                        name="programming"
                        value={houseBlock?.programming !== null ? (houseBlock?.programming === true ? "yes" : "no") : null}
                        onChange={(e: ChangeEvent<HTMLInputElement>) => setHouseBlock({ ...houseBlock, programming: e.target.value === "yes" })}
                    >
                        <FormControlLabel value="yes" control={<Radio />} label={t("generic.yes")} />
                        <FormControlLabel value="no" control={<Radio />} label={t("generic.no")} />
                    </RadioGroup>
                </FormControl>
            </Stack>
        </WizardCard>
    );
};
