import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import PropertyDialog from "./PropertyDialog";
import TestComponentWrapper from "../../test/TestComponentWrapper";

import { addCustomProperty } from "../../api/adminSettingServices";

jest.mock("../../api/adminSettingServices");

const addCustomPropertyMock = addCustomProperty as jest.Mock;

describe("PropertyDialog", () => {
    beforeEach(() => {
        addCustomPropertyMock.mockImplementation((p) => Promise.resolve({ id: "new-id", ...p }));
    });

    it("renders without errors for new property", () => {
        const setOpenDialog = jest.fn();
        render(
            <TestComponentWrapper>
                <PropertyDialog openDialog={true} setOpenDialog={setOpenDialog} setCustomProperties={jest.fn()} />
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
        });
        expect(setOpenDialog).toHaveBeenCalledWith(false);
    });
});
