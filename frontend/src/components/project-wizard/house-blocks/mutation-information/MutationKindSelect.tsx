import { Stack, InputLabel, Select, MenuItem, SelectChangeEvent, OutlinedInput } from "@mui/material";
import { t } from "i18next";
import { MutationInformationProps } from "./MutationInformationGroup";
import { mutationSelectOptions } from "../constants";
import { MutationSelectOptions } from "../../../../types/enums";

export const MutationKindSelect = ({ projectForm, setProjectForm }: MutationInformationProps) => {
    return (
        <Stack>
            <InputLabel id="mutationtype"> {t("createProject.houseBlocksForm.mutationType")}</InputLabel>
            <Select
                size="small"
                labelId="mutationtype"
                id="fase"
                multiple
                value={projectForm ? projectForm.mutation.mutationKind : []}
                label={t("createProject.houseBlocksForm.mutationType")}
                onChange={(event: SelectChangeEvent<MutationSelectOptions[]>) => {
                    const {
                        target: { value },
                    } = event;
                    if (typeof value !== "string") {
                        setProjectForm({
                            ...projectForm,
                            mutation: {
                                ...projectForm.mutation,
                                mutationKind: value,
                            },
                        });
                    }
                }}
                input={<OutlinedInput />}
                renderValue={(selected) => selected.join(", ")}
            >
                {mutationSelectOptions.map((m) => {
                    return (
                        <MenuItem key={m} value={m}>
                            {m}
                        </MenuItem>
                    );
                })}
            </Select>
        </Stack>
    );
};
