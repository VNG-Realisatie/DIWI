import { TextField } from "@mui/material";
import { ChangeEvent, useContext } from "react";
import ProjectContext from "../../../context/ProjectContext";

type Props = {
    totalValue: string | undefined;
    setTotalValue: (totalValue: string | undefined) => void;
};
export const TotalValueEditForm = ({ totalValue, setTotalValue }: Props) => {
    const { selectedProject } = useContext(ProjectContext);
    const handleTotalValueChange = (event: ChangeEvent<HTMLInputElement>) => {
        setTotalValue(event.target.value);
    };
    return (
        <TextField
            value={totalValue ? totalValue : selectedProject?.totalValue}
            size="small"
            id="total-value"
            variant="outlined"
            onChange={handleTotalValueChange}
        />
    );
};
