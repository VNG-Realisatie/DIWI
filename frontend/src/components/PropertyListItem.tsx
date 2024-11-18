import { ListItem, Typography } from "@mui/material";
import { capitalizeFirstLetters } from "../utils/stringFunctions";

type PropertyListItemProps = {
    label: string;
    value: string | number | undefined;
};

export const PropertyListItem = ({ label, value }: PropertyListItemProps) => {
    if (value) {
        let displayValue = typeof value === "number" ? value.toString() : value;
        displayValue = displayValue.trim();
        if (displayValue !== "") {
            return (
                <ListItem className={label}>
                    <Typography>
                        {capitalizeFirstLetters(label)}: {displayValue}
                    </Typography>
                </ListItem>
            );
        }
    }
    return <></>;
};
