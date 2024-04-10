import { Stack, Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { AmountModel, HouseBlock } from "../../../../types/houseBlockTypes";
import { SingleNumberInput } from "./SingleNumberInput";
import { useCustomPropertyDefinitions } from "../../../../hooks/useCustomPropertyDefinitions";
import { useEffect } from "react";
import { CategoryType } from "../../../../api/adminSettingServices";

export type PhysicalAppeareanceInformationProps = {
    houseBlock: HouseBlock;
    setHouseBlock(houseBlock: HouseBlock): void;
    readOnly: boolean;
};

export const PhysicalAppeareanceGroup = ({ houseBlock, setHouseBlock, readOnly }: PhysicalAppeareanceInformationProps) => {
    const translationPath = "createProject.houseBlocksForm.physicalAppearance";
    const { physicalAppearanceCategories } = useCustomPropertyDefinitions();

    // update houseblock so it will have amounts for every category
    useEffect(() => {
        const missingCategories = physicalAppearanceCategories?.filter((cat) => !houseBlock.physicalAppearance.map((cat) => cat.id).includes(cat.id));
        if (missingCategories && missingCategories.length > 0) {
            const missingAmountObj = missingCategories.map((cat) => ({ id: cat.id, amount: 0 })) ?? [];
            const newPhysicalAppearances = [...houseBlock.physicalAppearance, ...missingAmountObj];
            setHouseBlock({ ...houseBlock, physicalAppearance: newPhysicalAppearances });
        }
    }, [houseBlock, physicalAppearanceCategories, setHouseBlock]);

    const sortByNameAndId = (a: CategoryType, b: CategoryType) => {
        const firstSmaller = -1;
        // first sort by name
        if (a.name < b.name) return firstSmaller;
        if (a.name > b.name) return -firstSmaller;
        // if names identical, sort by id which cannot be identical
        if (a.id && b.id && a.id < b.id) return firstSmaller;
        return -firstSmaller;
    };

    return (
        <WizardCard>
            <Typography fontWeight={600} mb={2}>
                {t(`${translationPath}.title`)}
            </Typography>
            <Stack direction="row" alignItems="center" spacing={3} my={1}>
                <Typography fontWeight={600} flex={3}>
                    {t(`${translationPath}.houseType`)}
                </Typography>
                <Typography fontWeight={600} flex={1}>
                    {t(`${translationPath}.value`)}
                </Typography>
            </Stack>
            {houseBlock.physicalAppearance &&
                houseBlock.physicalAppearance.length > 0 &&
                physicalAppearanceCategories &&
                physicalAppearanceCategories.sort(sortByNameAndId).map((pa) => {
                    const propAmount = houseBlock.physicalAppearance.find((def) => def.id === pa.id)?.amount ?? null;

                    function handleChange(newValue: number | null): void {
                        const newAmountObj = { id: pa.id, amount: newValue ?? 0 } as AmountModel;
                        const physicalAppearanceNewRemoved = houseBlock.physicalAppearance.filter((cat) => cat.id !== pa.id);
                        setHouseBlock({
                            ...houseBlock,
                            physicalAppearance: [...physicalAppearanceNewRemoved, newAmountObj],
                        });
                    }

                    return (
                        <SingleNumberInput
                            key={pa.id}
                            // this has no translation as it is set by the user.
                            name={pa.name}
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
