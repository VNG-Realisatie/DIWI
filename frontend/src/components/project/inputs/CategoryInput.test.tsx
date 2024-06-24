import "@testing-library/jest-dom";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { vi } from "vitest";
import CategoryInput from "./CategoryInput";

describe("CategoryInput Component", () => {
    const options = [
        { id: "1", name: "Option 1" },
        { id: "2", name: "Option 2" },
    ];

    it("should render label", () => {
        render(<CategoryInput values={null} setValue={() => {}} readOnly={false} mandatory={false} title="Category" options={options} multiple={false} />);

        expect(screen.getByText("Category")).toBeInTheDocument();
    });

    it("should be disabled when read-only is true", () => {
        render(<CategoryInput values={null} setValue={() => {}} readOnly={true} mandatory={false} title="Category" options={options} multiple={false} />);

        const autocomplete = screen.getByRole("combobox");
        expect(autocomplete).toBeDisabled();
    });

    it("should render with a default value", () => {
        const defaultValues = options[0];
        render(
            <CategoryInput values={defaultValues} setValue={() => {}} readOnly={false} mandatory={false} title="Category" options={options} multiple={false} />,
        );

        const autocompleteInput = screen.getByRole("combobox");
        expect(autocompleteInput).toHaveValue("Option 1");
    });

    it("should handle empty options", async () => {
        render(<CategoryInput values={null} setValue={() => {}} readOnly={false} mandatory={false} title="Category" options={[]} multiple={false} />);

        const autocompleteInput = screen.getByRole("combobox");
        fireEvent.click(autocompleteInput);
        await waitFor(() => expect(screen.queryByRole("listbox")).toBeNull());
    });

    it("should be clearable", () => {
        const mockSetValue = vi.fn();
        render(
            <CategoryInput
                values={options[0]}
                setValue={mockSetValue}
                readOnly={false}
                mandatory={false}
                title="Category"
                options={options}
                multiple={false}
            />,
        );

        const autocompleteInput = screen.getByRole("combobox");
        fireEvent.change(autocompleteInput, { target: { value: "" } });

        const clearButton = screen.getByLabelText("Clear");

        if (clearButton) {
            fireEvent.click(clearButton);
        }

        expect(mockSetValue).toHaveBeenCalled();
    });
});
