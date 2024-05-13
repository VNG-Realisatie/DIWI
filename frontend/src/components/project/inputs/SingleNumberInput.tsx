import { Stack, Typography, TextField, Box } from "@mui/material";
import { InputContainer } from "../../project-wizard/house-blocks/InputContainer";
import InputLabelStack from "./InputLabelStack";

type SingleNumberEdit = {
    name: string;
    value: number | null;
    onChange: (newValue: number | null) => void;
    readOnly: boolean;
    mandatory: boolean;
    translationPath?: string;
    error?: string;
};

export const SingleNumberInput = ({ name, value, onChange, readOnly, mandatory, error }: SingleNumberEdit) => {
    const hasError = mandatory && (!value || value <= 0);
    return (
        <InputLabelStack title={name} mandatory={mandatory}>
            <TextField
                required={mandatory}
                disabled={readOnly}
                sx={{ flex: 1 }}
                InputProps={{
                    inputProps: {
                        min: 0,
                    },
                }}
                type="number"
                id={name ? name : ""}
                size="small"
                variant="outlined"
                value={value ? value : ""}
                onChange={(e) => onChange(+e.target.value)}
                error={hasError}
                helperText={hasError ? error : ""}
            />
        </InputLabelStack>
    );
};

// export const SingleNumberInput = ({ value, onChange, name, translationPath, readOnly }: Props) => {
//     return (
//         <Stack direction="row" alignItems="center" spacing={2} my={2}>
//             <Typography variant="subtitle1" fontWeight="500" border="solid 1px #ddd" borderRadius="5px" p={0.6} flex={3}>
//                 {name}
//             </Typography>
//             {!readOnly && <SingleNumberEditInput name={name} value={value} onChange={onChange} />}
//             {readOnly && (
//                 <Box sx={{ flex: 1 }}>
//                     <InputContainer>
//                         <Typography minHeight="20px">{value}</Typography>
//                     </InputContainer>
//                 </Box>
//             )}
//         </Stack>
//     );
// };
