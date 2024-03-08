import { MenuItem, Select, SelectChangeEvent } from "@mui/material";
import { confidentialityLevelOptions } from "../../table/constants";
import { useTranslation } from "react-i18next";

type Props = {
    confidentialityLevel: string | undefined;
    setConfidentialityLevel: (c: string | undefined) => void;
};

export const ConfidentialityLevelEditForm = ({ confidentialityLevel, setConfidentialityLevel }: Props) => {
    const { t } = useTranslation();

    const handleConfidentialityLevelChange = (event: SelectChangeEvent) => {
        setConfidentialityLevel(event.target.value as string);
    };

    return (
        <Select fullWidth size="small" id="confidentiality-level-select" value={confidentialityLevel} onChange={handleConfidentialityLevelChange}>
            {confidentialityLevelOptions.map((ppo) => {
                return (
                    <MenuItem key={ppo.id} value={ppo.id}>
                        {t(`projectTable.confidentialityLevelOptions.${ppo.name}`)}
                    </MenuItem>
                );
            })}
        </Select>
    );
};
