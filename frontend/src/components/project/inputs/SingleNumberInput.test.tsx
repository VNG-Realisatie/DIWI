import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { SingleNumberInput } from "./SingleNumberInput";

describe("SingleNumberInput component", () => {
    it("renders with error message when mandatory and value is not provided", () => {
        render(<SingleNumberInput value={null} onChange={() => {}} readOnly={false} mandatory={true} error="Value is required" />);
        expect(screen.getByText("Value is required")).toBeInTheDocument();
    });

    it("calls onChange callback with entered value", () => {
        const handleChange = vi.fn();
        render(<SingleNumberInput value={null} onChange={handleChange} readOnly={false} mandatory={false} />);
        const input = screen.getByRole("spinbutton");
        userEvent.type(input, "42");
        expect(handleChange).toHaveBeenCalledWith(42);
    });

    it("renders with provided name", () => {
        render(<SingleNumberInput name="Quantity" value={null} onChange={() => {}} readOnly={false} mandatory={false} />);
        expect(screen.getByText("Quantity")).toBeInTheDocument();
    });

    it("renders input as disabled when readOnly is true", () => {
        render(<SingleNumberInput value={null} onChange={() => {}} readOnly={true} mandatory={false} />);
        const input = screen.getByRole("spinbutton");
        expect(input).toBeDisabled();
    });

    it("renders input as enabled when readOnly is false", () => {
        render(<SingleNumberInput value={null} onChange={() => {}} readOnly={false} mandatory={false} />);
        const input = screen.getByRole("spinbutton");
        expect(input).toBeEnabled();
    });

    it("renders with input label when isInputLabel is true", () => {
        render(<SingleNumberInput value={null} onChange={() => {}} readOnly={false} mandatory={false} isInputLabel={true} />);
        expect(screen.getByTestId("input-label-stack")).toBeInTheDocument();
    });

    it("renders without input label when isInputLabel is false", () => {
        render(<SingleNumberInput value={null} onChange={() => {}} readOnly={false} mandatory={false} isInputLabel={false} />);
        expect(screen.queryByTestId("input-label-stack")).toBeNull();
    });
});
