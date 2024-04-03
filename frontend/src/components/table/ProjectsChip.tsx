import { Chip, Stack, Tooltip } from "@mui/material";

type OptionType = {
    id: string;
    name: string;
};

type Props = {
    tagLimit: number;
    values: OptionType[];
};

export const ProjectsChip = ({ tagLimit, values }: Props) => {
    const valuesWithSpace = values.slice(tagLimit).map((value) => {
        return `${value.name}\n`;
    });

    return (
        <Stack
            direction="row"
            sx={{
                maxWidth: "220px",
            }}
        >
            {values.slice(0, tagLimit).map((option) => (
                <Tooltip key={option.id} title={option.name} arrow placement="top" disableInteractive>
                    <Chip
                        sx={{
                            margin: 0.2,
                        }}
                        key={option.id}
                        label={option.name}
                    />
                </Tooltip>
            ))}
            {values.length > tagLimit && (
                <Tooltip title={<span style={{ whiteSpace: "pre-line" }}>{valuesWithSpace}</span>} arrow placement="top" disableInteractive>
                    <Chip
                        sx={{
                            margin: 0.2,
                        }}
                        key={tagLimit}
                        label={`+${values.length - tagLimit}`}
                    />
                </Tooltip>
            )}
        </Stack>
    );
};
