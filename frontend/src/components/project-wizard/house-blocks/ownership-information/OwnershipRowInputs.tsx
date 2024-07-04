import DeleteIcon from "@mui/icons-material/Delete";
import { Grid, IconButton, TextField } from "@mui/material";
import { t } from "i18next";
import { OwnershipValueType, ownershipValueOptions } from "../../../../types/enums";
import { OwnershipSingleValue } from "../../../../types/houseBlockTypes";
import CategoryInput from "../../../project/inputs/CategoryInput";
import RangeNumberInput from "../../../project/inputs/RangeNumberInput";
import InputLabelStack from "../../../project/inputs/InputLabelStack";

const translationPath = "createProject.houseBlocksForm";

type Props = {
    ownership: OwnershipSingleValue;
    index: number;
    handleInputChange: (index: number, value: OwnershipSingleValue) => void;
    handleRemoveRow: (index: number) => void;
    readOnly: boolean;
    isOwnerShipValueAndMutationConsistent: boolean;
};
type OwnershipProps = {
    ownership: OwnershipSingleValue;
    index: number;
    handleInputChange: (index: number, value: OwnershipSingleValue) => void;
    readOnly: boolean;
    title: string;
    mandatory: boolean;
    isOwnerShipValueAndMutationConsistent: boolean;
};

// eslint-disable-next-line react-refresh/only-export-components
export const isOwnershipAmountValid = (amount: number): boolean => {
    return Number.isInteger(amount) && amount >= 0;
};

const OwnershipAmountInput = ({ handleInputChange, ownership, index, readOnly, title, mandatory, isOwnerShipValueAndMutationConsistent }: OwnershipProps) => {
    const isAmountValid = isOwnershipAmountValid(ownership.amount);
    const isConsistent = isOwnerShipValueAndMutationConsistent;
    const checkIfOwnerShipValueAndMutationConsistent = () => {
        if (!isAmountValid) {
            return t("createProject.hasMissingRequiredAreas.amount");
        } else {
            if (!isConsistent) {
                return t("createProject.hasMissingRequiredAreas.amountNotConsistent");
            }
            return "";
        }
    };
    return (
        <InputLabelStack title={title || ""} mandatory={mandatory}>
            <TextField
                size="small"
                type="number"
                required
                fullWidth
                value={ownership.amount !== 0 && !Number.isNaN(ownership.amount) ? ownership.amount : ""}
                onChange={(e) => handleInputChange(index, { ...ownership, amount: parseInt(e.target.value) })}
                error={!isAmountValid || !isConsistent}
                helperText={checkIfOwnerShipValueAndMutationConsistent()}
                disabled={readOnly}
            />
        </InputLabelStack>
    );
};

export const OwnershipRowInputs = ({ ownership, index, handleInputChange, handleRemoveRow, readOnly, isOwnerShipValueAndMutationConsistent }: Props) => {
    const isKoopwoning = ownership.type === "KOOPWONING";
    const isHuurwoning = ownership.type === "HUURWONING_PARTICULIERE_VERHUURDER" || ownership.type === "HUURWONING_WONINGCORPORATIE";

    return (
        <Grid container spacing={2} mt={1} direction="row">
            <Grid item xs={3} className="ownership-category">
                <CategoryInput
                    title={t(`${translationPath}.type`)}
                    readOnly={readOnly}
                    values={ownership.type ? { id: ownership.type, name: ownership.type } : null}
                    setValue={(_, newValue) => handleInputChange(index, { ...ownership, type: newValue ? (newValue.id as OwnershipValueType) : undefined })}
                    mandatory={false}
                    options={ownershipValueOptions.map((value) => ({ id: value, name: value }))}
                    multiple={false}
                    translationPath="createProject.houseBlocksForm.ownershipAndValue.type."
                    tooltipInfoText={t("tooltipInfo.soort.title")}
                />
            </Grid>
            <Grid item xs={1.5} className="ownership-house-amount">
                <OwnershipAmountInput
                    mandatory={false}
                    readOnly={readOnly}
                    title={t(`${translationPath}.amount`)}
                    index={index}
                    handleInputChange={handleInputChange}
                    ownership={ownership}
                    isOwnerShipValueAndMutationConsistent={isOwnerShipValueAndMutationConsistent}
                />

                {/* {readOnly && (
                    <InputContainer>
                        <Typography>{ownership?.amount}</Typography>
                    </InputContainer>
                )} */}
            </Grid>
            <Grid item xs={3} className="ownership-house-value">
                <RangeNumberInput
                    value={!isHuurwoning ? ownership.value : { ...ownership.value, value: null }}
                    updateCallBack={(e) => handleInputChange(index, { ...ownership, value: e })}
                    readOnly={readOnly ? true : isHuurwoning}
                    mandatory={false}
                    isMonetary={true}
                    title={t(`${translationPath}.value`)}
                />
            </Grid>
            <Grid item xs={3} className="ownership-house-rent">
                <RangeNumberInput
                    value={!isKoopwoning ? ownership.rentalValue : { ...ownership.value, value: null }}
                    updateCallBack={(e) => handleInputChange(index, { ...ownership, rentalValue: e })}
                    readOnly={readOnly ? true : isKoopwoning}
                    mandatory={false}
                    isMonetary={true}
                    title={t(`${translationPath}.rent`)}
                />
            </Grid>
            <Grid item xs={0.2} className="ownership-delete-icon" style={{ display: "flex", flexDirection: "column", justifyContent: "flex-end" }}>
                {!readOnly && (
                    <IconButton onClick={() => handleRemoveRow(index)}>
                        <DeleteIcon sx={{ color: "red" }} />
                    </IconButton>
                )}
            </Grid>
        </Grid>
    );
};
