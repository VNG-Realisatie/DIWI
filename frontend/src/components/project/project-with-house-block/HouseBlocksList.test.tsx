import { screen, fireEvent, render } from "@testing-library/react";
import { getCustomProperties } from "../../../api/adminSettingServices";
import { saveHouseBlockWithCustomProperties } from "../../../api/houseBlockServices";
import { getEmptyHouseBlock } from "../../../context/HouseBlockContext";
import { HouseBlockWithCustomProperties } from "../../../types/houseBlockTypes";
import { HouseBlockAccordionWithControls } from "./HouseBlocksList";
import TestComponentWrapper from "../../../test/TestComponentWrapper";
import { act } from "react-dom/test-utils";

// mock saveHouseBlockWithCustomProperties
jest.mock("../../../api/houseBlockServices", () => ({
    ...jest.requireActual("../../../api/houseBlockServices"),
    saveHouseBlockWithCustomProperties: vi.fn(),
}));

const saveHouseBlockWithCustomPropertiesMock = saveHouseBlockWithCustomProperties as jest.Mock;

//mock getCustomProperties
jest.mock("../../../api/adminSettingServices", () => ({
    ...jest.requireActual("../../../api/adminSettingServices"),
    getCustomProperties: vi.fn(),
}));

const getCustomPropertiesMock = getCustomProperties as jest.Mock;

it.each([{ disabled: true }, { disabled: false }])("should save houseblock with enabled or disabled categories", async (config) => {
    const physicalAppearanceCategoryId = "physicalAppearance custom property option id";
    const targetGroupCategoryId = "targetGroup custom property option id";
    const customPropertyDefinitions = [
        {
            id: "physicalAppearance custom property id",
            name: "physicalAppearance",
            disabled: false,
            objectType: "WONINGBLOK",
            categories: [
                {
                    id: physicalAppearanceCategoryId,
                    name: "physicalAppearance custom property option",
                    disabled: config.disabled,
                },
            ],
        },
        {
            id: "targetGroup custom property id",
            name: "targetGroup",
            disabled: false,
            objectType: "WONINGBLOK",
            categories: [
                {
                    id: targetGroupCategoryId,
                    name: "targetGroup custom property option",
                    disabled: config.disabled,
                },
            ],
        },
    ];

    const houseBlock: HouseBlockWithCustomProperties = getEmptyHouseBlock();
    houseBlock.houseblockId = "houseblock id";
    houseBlock.houseblockName = "houseblock name";
    houseBlock.startDate = "2022-01-01";
    houseBlock.endDate = "2022-12-31";
    houseBlock.mutation = { kind: "CONSTRUCTION", amount: 1000 };
    houseBlock.ownershipValue = [
        {
            type: "KOOPWONING",
            amount: 1000,
            value: { value: 1000, min: null, max: null },
            rentalValue: { value: null, min: null, max: null },
        },
    ];

    houseBlock.physicalAppearance = [{ id: physicalAppearanceCategoryId, amount: 1000 }];
    houseBlock.targetGroup = [{ id: targetGroupCategoryId, amount: 1000 }];

    getCustomPropertiesMock.mockReturnValue({
        then: (cb: any) => {
            act(() => cb(customPropertyDefinitions));
        },
    });

    saveHouseBlockWithCustomPropertiesMock.mockReturnValue({
        then: (cb: any) => {
            act(cb);
        },
    });

    render(
        <TestComponentWrapper>
            <HouseBlockAccordionWithControls houseBlock={houseBlock} refresh={vi.fn()} />
        </TestComponentWrapper>,
    );

    // Go to edit mode
    fireEvent.click(screen.getByTestId("edit-houseblock"));

    // Save the houseblock
    fireEvent.click(screen.getByTestId("save-houseblock"));

    const expected = config.disabled ? { ...houseBlock, physicalAppearance: [], targetGroup: [] } : houseBlock;
    expect(saveHouseBlockWithCustomPropertiesMock).toHaveBeenCalledWith(expected);
});
