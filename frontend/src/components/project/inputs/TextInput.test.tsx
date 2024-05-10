import { render, screen } from "@testing-library/react";
import "@testing-library/jest-dom";
import TextInput from "./TextInput";

describe("TextInput component", () => {
    test("should render with a label", () => {
        const label = "TEST";
        render(<TextInput value="" setValue={() => {}} readOnly={false} mandatory={true} title={label} />);
        expect(screen.getByText((text) => text.includes(label))).toBeInTheDocument();
    });

    test("should render TextField when not read-only", () => {
        render(<TextInput value="" setValue={() => {}} readOnly={false} mandatory={true} />);
        expect(screen.getByRole("textbox")).toBeInTheDocument();
    });

    test("should render TextField but it should be disabled when read-only", () => {
        render(<TextInput value="Test Value" setValue={() => {}} readOnly={true} mandatory={true} />);

        const textField = screen.getByRole("textbox");
        expect(textField).toBeInTheDocument();

        expect(textField).toBeDisabled();
    });

    test("should display error when mandatory but value is empty", () => {
        const errorText = "This field is required";
        render(<TextInput value="" setValue={() => {}} readOnly={false} mandatory={true} errorText={errorText} title="Name" />);

        expect(screen.getByText(errorText)).toBeInTheDocument();
    });
});
