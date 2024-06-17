import { Stack } from "@mui/material";
import { LabelComponent } from "../LabelComponent";

type InputLabelStackProps = {
    title: string;
    children: React.ReactNode;
    mandatory: boolean;
    tooltipInfoText?: string;
};

const InputLabelStack = ({ title, children, mandatory, tooltipInfoText }: InputLabelStackProps) => {
    return (
        <Stack width="100%" data-testid="input-label-stack">
            <LabelComponent required={mandatory} text={title} tooltipInfoText={tooltipInfoText} />
            {children}
        </Stack>
    );
};

export default InputLabelStack;
