import { Stack, TextField } from "@mui/material";
import { ChangeEvent } from "react";

type Props = {
    name?: string | null;
    setName: (name: string) => void;
};
export const ProjectNameEditForm = ({ name, setName }: Props) => {
    const handleNameChange = (event: ChangeEvent<HTMLInputElement>) => {
        setName(event.target.value);
    };

    return (
        <Stack direction="row" alignItems="center" spacing={1}>
            <TextField fullWidth size="small" sx={{ border: "solid 1px white" }} value={name} onChange={handleNameChange} />
        </Stack>
    );
};
