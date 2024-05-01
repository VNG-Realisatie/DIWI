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
                    value: { value: 0, min: null, max: null },
                    rentalValue: { value: 0, min: null, max: null },
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
            <Stack direction="row" alignItems="center" spacing={2} my={1}>
                <Typography fontWeight={600} flex={4.2}>
                    {t(`${translationPath}.type`)}
                </Typography>
                <Typography fontWeight={600} flex={2}>
                    {t(`${translationPath}.amount`)}
                </Typography>
                <Typography fontWeight={600} flex={2}>
                    {t(`${translationPath}.value`)}
                </Typography>
                <Typography fontWeight={600} flex={2}>
                    {t(`${translationPath}.rent`)}
                </Typography>
                <Typography fontWeight={600} flex={2}></Typography>
            </Stack>
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
