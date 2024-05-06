import React from "react";
import { Stack } from "@mui/material";
import { LabelComponent } from "../LabelComponent";

type InputLabelStackProps = {
    title: string;
    children: React.ReactNode;
    mandatory: boolean;
};

const InputLabelStack = ({ title, children, mandatory }: InputLabelStackProps) => {
    return (
        <Stack width="100%">
            <LabelComponent required={mandatory} text={title} />
            {children}
        </Stack>
    );
};

export default InputLabelStack;
