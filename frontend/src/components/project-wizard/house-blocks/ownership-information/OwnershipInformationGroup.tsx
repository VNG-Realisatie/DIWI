import { Grid, IconButton, Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { HouseBlock, OwnershipSingleValue } from "../../../../types/houseBlockTypes";
import AddIcon from "@mui/icons-material/Add";
import { OwnershipRowInputs } from "./OwnershipRowInputs";
import { checkConsistencyOwnerShipValueAndMutation } from "../../../../utils/houseblocks/houseBlocksFunctions";
import { useMemo } from "react";

export type OwnershipInformationProps = {
    houseBlock: HouseBlock;
    setHouseBlock(houseBlock: HouseBlock): void;
    readOnly: boolean;
};

export const OwnershipInformationGroup = ({ houseBlock, setHouseBlock, readOnly }: OwnershipInformationProps) => {
    const handleAddRow = useMemo(() => {
        return () => {
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
    }, [houseBlock, setHouseBlock]);

    const handleInputChange = useMemo(() => {
        return (index: number, value: OwnershipSingleValue) => {
            const updatedValues = [...houseBlock.ownershipValue];
            updatedValues[index] = value;
            setHouseBlock({
                ...houseBlock,
                ownershipValue: updatedValues,
            });
        };
    }, [houseBlock, setHouseBlock]);

    const handleRemoveRow = useMemo(() => {
        return (index: number) => {
            const updatedValues = [...houseBlock.ownershipValue];
            updatedValues.splice(index, 1);
            setHouseBlock({
                ...houseBlock,
                ownershipValue: updatedValues,
            });
        };
    }, [houseBlock, setHouseBlock]);

    const translationPath = "createProject.houseBlocksForm";
    return (
        <WizardCard>
            <Typography fontWeight={600} mb={2}>
                {t(`${translationPath}.ownershipAndValue.title`)}
            </Typography>
            <Grid container>
                {houseBlock.ownershipValue.map((ownership, index) => (
                    <OwnershipRowInputs
                        key={index}
                        index={index}
                        handleRemoveRow={handleRemoveRow}
                        handleInputChange={handleInputChange}
                        ownership={ownership}
                        readOnly={readOnly}
                        isOwnerShipValueAndMutationConsistent={checkConsistencyOwnerShipValueAndMutation(houseBlock)}
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
