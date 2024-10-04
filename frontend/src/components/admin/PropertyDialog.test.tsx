import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import TestComponentWrapper from "../../test/TestComponentWrapper";
import PropertyDialog from "./PropertyDialog";

import { vi } from "vitest";
import { Property, addCustomProperty, getCustomProperties, getCustomProperty } from "../../api/adminSettingServices";

vi.mock("../../api/adminSettingServices");

const addCustomPropertyMock = addCustomProperty as jest.Mock;
const getCustomPropertiesMock = getCustomProperties as jest.Mock;
const getCustomPropertyMock = getCustomProperty as jest.Mock;

describe("PropertyDialog", () => {
    const customProperties: Property[] = [];

    beforeEach(() => {
        addCustomPropertyMock.mockImplementation((p) => Promise.resolve({ id: "new-id", ...p }));
        getCustomPropertiesMock.mockImplementation(() => Promise.resolve(customProperties));
    });

    it("renders without errors for new property", async () => {
        const setOpenDialog = vi.fn();
        render(
            <TestComponentWrapper>
                <PropertyDialog openDialog={true} setOpenDialog={setOpenDialog} setCustomProperties={vi.fn()} />
            </TestComponentWrapper>,
        );

        fireEvent.change(screen.getByRole("textbox", { name: "admin.settings.tableHeader.name" }), { target: { value: "New Property" } });

        fireEvent.click(screen.getByRole("button", { name: "generic.save" }));

        expect(addCustomProperty).toHaveBeenCalledWith({
            id: undefined,
            name: "New Property",
            type: "CUSTOM",
            objectType: "PROJECT",
            propertyType: "TEXT",
            disabled: false,
            categories: undefined,
            ordinals: undefined,
            mandatory: false,
            singleSelect: undefined,
        });

        // Wait for promises to resolve

        await waitFor(() => expect(setOpenDialog).toHaveBeenCalledWith(false));
    });

    it("edits ordinal properties correctly", async () => {
        const prop: Property = {
            id: "17",
            name: "Ordinal Property",
            type: "CUSTOM",
            objectType: "PROJECT",
            propertyType: "ORDINAL",
            disabled: false,
            categories: undefined,
            ordinals: [
                { id: "2", name: "Second", level: 2, disabled: false },
                { id: "1", name: "First", level: 1, disabled: false },
            ],
            mandatory: false,
            singleSelect: undefined,
        };

        getCustomPropertyMock.mockReturnValue({
            then: async (cb: (prop: Property) => void) => {
                await waitFor(() => cb(prop));
            },
        });

        const { rerender } = render(
            <TestComponentWrapper>
                <PropertyDialog openDialog={true} setOpenDialog={vi.fn()} setCustomProperties={vi.fn()} id="17" />
            </TestComponentWrapper>,
        );

        rerender(
            <TestComponentWrapper>
                <PropertyDialog openDialog={true} setOpenDialog={vi.fn()} setCustomProperties={vi.fn()} id="17" />
            </TestComponentWrapper>,
        );

        const propertyType = screen.getByText("admin.settings.propertyType.ORDINAL");
        expect(propertyType).toBeVisible();

        const categories = screen.getAllByRole("textbox");
        expect(categories[1]).toHaveValue("First");
        expect(categories[2]).toHaveValue("Second");

        // fireEvent
        fireEvent.change(categories[1], { target: { value: "First1" } });
        fireEvent.change(categories[2], { target: { value: "Second2" } });

        expect(categories[1]).toHaveValue("First1");
        expect(categories[2]).toHaveValue("Second2");
    });
});

it("ensures singleSelect is true or false for CATEGORY propertyType", async () => {
    const prop: Property = {
        id: "18",
        name: "Category Property",
        type: "CUSTOM",
        objectType: "PROJECT",
        propertyType: "CATEGORY",
        disabled: false,
        categories: [
            {
                id: "1",
                name: "1",
                disabled: false,
            },
            {
                id: "2",
                name: "2",
                disabled: false,
            },
        ],
        ordinals: undefined,
        mandatory: false,
        singleSelect: undefined,
    };

    getCustomPropertyMock.mockReturnValue({
        then: async (cb: (prop: Property) => void) => {
            await waitFor(() => cb(prop));
        },
    });

    const { rerender } = render(
        <TestComponentWrapper>
            <PropertyDialog openDialog={true} setOpenDialog={vi.fn()} setCustomProperties={vi.fn()} id="18" />
        </TestComponentWrapper>,
    );

    rerender(
        <TestComponentWrapper>
            <PropertyDialog openDialog={true} setOpenDialog={vi.fn()} setCustomProperties={vi.fn()} id="18" />
        </TestComponentWrapper>,
    );

    const singleSelect = screen.getByRole("checkbox", { name: "admin.settings.singleSelect" });
    expect(singleSelect).toBeInTheDocument();
    expect(singleSelect).not.toBeUndefined();
    expect(singleSelect).toHaveProperty("checked", expect.any(Boolean));
});
