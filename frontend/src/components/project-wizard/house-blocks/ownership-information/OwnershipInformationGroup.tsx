import { Grid, IconButton, Stack, Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { HouseBlock, OwnershipSingleValue } from "../../../../types/houseBlockTypes";
import AddIcon from "@mui/icons-material/Add";
import { OwnershipRowInputs } from "./OwnershipRowInputs";

export type OwnershipInformationProps = {
    houseBlock: HouseBlock;
    setHouseBlock(houseBlock: HouseBlock): void;
    readOnly: boolean;
};

export const OwnershipInformationGroup = ({ houseBlock, setHouseBlock, readOnly }: OwnershipInformationProps) => {
    const handleAddRow = () => {
        setHouseBlock({
            ...houseBlock,
            ownershipValue: [
                ...houseBlock.ownershipValue,
                {
                    type: undefined,
                    amount: 0,
                    value: { value: null, min: null, max: null },
                    rentalValue: { value: null, min: null, max: null },
                },
            ],
        });
    };

    const handleInputChange = (index: number, value: OwnershipSingleValue) => {
        const updatedValues = [...houseBlock.ownershipValue];
        updatedValues[index] = value;
        setHouseBlock({
            ...houseBlock,
            ownershipValue: updatedValues,
        });
    };

    const handleRemoveRow = (index: number) => {
        const updatedValues = [...houseBlock.ownershipValue];
        updatedValues.splice(index, 1);
        setHouseBlock({
            ...houseBlock,
            ownershipValue: updatedValues,
        });
    };

    const translationPath = "createProject.houseBlocksForm";
    return (
        <WizardCard>
            <Typography fontWeight={600} mb={2}>
                {t(`${translationPath}.ownershipAndValue.title`)}
            </Typography>
            <Grid container spacing={2}>
                <Grid item xs={4}>
                    <Typography fontWeight={600}>{t(`${translationPath}.type`)}</Typography>
                </Grid>
                <Grid item xs={1.25}>
                    <Typography fontWeight={600}>{t(`${translationPath}.amount`)}</Typography>
                </Grid>
                <Grid item xs={2.8}>
                    <Typography fontWeight={600}>{t(`${translationPath}.value`)}</Typography>
                </Grid>
                <Grid item xs={2.8}>
                    <Typography fontWeight={600}>{t(`${translationPath}.rent`)}</Typography>
                </Grid>
            </Grid>
            <Grid container>
                {houseBlock.ownershipValue.map((ownership, index) => (
                    <OwnershipRowInputs
                        key={index}
                        index={index}
                        handleRemoveRow={handleRemoveRow}
                        handleInputChange={handleInputChange}
                        ownership={ownership}
                        readOnly={readOnly}
                    />
                ))}
                {!readOnly && (
                    <IconButton onClick={handleAddRow}>
                        <AddIcon sx={{ color: "green" }} />
                    </IconButton>
                )}
            </Grid>
        </WizardCard>
    );
};
