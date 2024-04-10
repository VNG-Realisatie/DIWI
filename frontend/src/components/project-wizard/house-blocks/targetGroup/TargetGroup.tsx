import { Stack, Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { AmountModel, HouseBlock } from "../../../../types/houseBlockTypes";
import { SingleNumberInput } from "../physical-appearence/SingleNumberInput";
import { useCustomPropertyDefinitions } from "../../../../hooks/useCustomPropertyDefinitions";
import { useEffect } from "react";

export type TargetGroupProps = {
    houseBlock: HouseBlock;
    setHouseBlock(houseBlock: HouseBlock): void;
    readOnly: boolean;
};

export const TargetGroup = ({ houseBlock, setHouseBlock, readOnly }: TargetGroupProps) => {
    const translationPath = "createProject.houseBlocksForm.purpose";
    const { targetGroupCategories } = useCustomPropertyDefinitions();

    // update houseblock so it will have amounts for every category
    useEffect(() => {
        const missingCategories = targetGroupCategories?.filter((cat) => !houseBlock.targetGroup.map((cat) => cat.id).includes(cat.id));
        if (missingCategories && missingCategories.length > 0) {
            const missingAmountObj = missingCategories.map((cat) => ({ id: cat.id, amount: 0 })) ?? [];
            const newPhysicalAppearances = [...houseBlock.targetGroup, ...missingAmountObj];
            setHouseBlock({ ...houseBlock, targetGroup: newPhysicalAppearances });
        }
    }, [houseBlock, targetGroupCategories, setHouseBlock]);

    return (
        <WizardCard>
            <Typography fontWeight={600} mb={2}>
                {t(`${translationPath}.title`)}
            </Typography>
            <Stack direction="row" alignItems="center" spacing={3} my={1}>
                <Typography fontWeight={600} flex={3}>
                    {t(`${translationPath}.typeOfResidents`)}
                </Typography>
                <Typography fontWeight={600} flex={1}>
                    {t(`${translationPath}.value`)}
                </Typography>
            </Stack>
            {houseBlock.targetGroup &&
                houseBlock.targetGroup.length > 0 &&
                targetGroupCategories &&
                targetGroupCategories.map((tg) => {
                    const propAmount = houseBlock.targetGroup.find((def) => def.id === tg.id)?.amount ?? null;

                    function handleChange(newValue: number | null): void {
                        const newAmountObj = { id: tg.id, amount: newValue ?? 0 } as AmountModel;
                        const targetGroupNewRemoved = houseBlock.targetGroup.filter((cat) => cat.id !== tg.id);
                        setHouseBlock({
                            ...houseBlock,
                            targetGroup: [...targetGroupNewRemoved, newAmountObj],
                        });
                    }

                    return (
                        <SingleNumberInput
                            key={tg.id}
                            // this has no translation as it is set by the user.
                            name={tg.name}
                            value={propAmount}
                            onChange={handleChange}
                            readOnly={readOnly}
                            translationPath={translationPath}
                        />
                    );
                })}
        </WizardCard>
    );
};
