import { Paper, Typography } from "@mui/material";

type Props = {
    errorMessage: string;
};

const ActionNotAllowed = ({ errorMessage }: Props) => {
    return (
        <Paper style={{ padding: "20px", margin: "20px" }}>
            <Typography variant="h6">{errorMessage}</Typography>
        </Paper>
    );
};

export default ActionNotAllowed;
