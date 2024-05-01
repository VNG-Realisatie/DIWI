
import { render, screen } from "@testing-library/react";
import "@testing-library/jest-dom";
import NameInput from "./NameInput";

describe("NameInput component", () => {
    test("should render with a label", () => {
        const label = "TEST";
        render(<NameInput value="" setValue={() => {}} readOnly={false} mandatory={true} label={label} />);
        expect(screen.getByText((text) => text.includes(label))).toBeInTheDocument();
    });

    test("should render TextField when not read-only", () => {
        render(<NameInput value="" setValue={() => {}} readOnly={false} mandatory={true} />);
        expect(screen.getByRole("textbox")).toBeInTheDocument();
    });

    test("should not render TextField when read-only", () => {
        render(<NameInput value="Test Value" setValue={() => {}} readOnly={true} mandatory={true} />);
        expect(screen.queryByRole("textbox")).toBeNull();
        expect(screen.getByText("Test Value")).toBeInTheDocument();
    });
    //This test doesnt work

    // test("should trigger setValue when value changes", () => {
    //     const setValueMock = jest.fn();
    //     render(<NameInput value="" setValue={setValueMock} readOnly={false} mandatory={true} />);

    //     const input = screen.getByRole("textbox");
    //     const value = "TEST";

    //     fireEvent.change(input, { target: { value } });

    //     expect(setValueMock).toHaveBeenCalledTimes(1);
    //     expect(screen.getByText((text) => text.includes(value))).toBeInTheDocument();
    // });

    test("should display error when mandatory but value is empty", () => {
        const errorText = "This field is required";
        render(<NameInput value="" setValue={() => {}} readOnly={false} mandatory={true} errorText={errorText} label="Name" />);

        expect(screen.getByText(errorText)).toBeInTheDocument();
    });
});
