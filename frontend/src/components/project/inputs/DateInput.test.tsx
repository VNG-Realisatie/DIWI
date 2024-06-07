import { render, screen } from "@testing-library/react";
import "@testing-library/jest-dom";
import DateInput from "./DateInput";
import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import userEvent from "@testing-library/user-event";

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const renderWithLocalization = (ui: any) => {
    return render(<LocalizationProvider dateAdapter={AdapterDayjs}>{ui}</LocalizationProvider>);
};

describe("DateInput Component Tests", () => {
    it("should render correctly", () => {
        renderWithLocalization(<DateInput value={null} setValue={() => {}} readOnly={false} mandatory={true} errorText="Required" />);

        expect(screen.getByRole("textbox")).toBeInTheDocument();
    });

    it("should show error message when error exists", () => {
        renderWithLocalization(<DateInput value={null} setValue={() => {}} readOnly={false} mandatory errorText="Required" error="Error message" />);

        expect(screen.getByText("Error message")).toBeInTheDocument();
    });

    it("should show error message when value is null and it's mandatory", () => {
        renderWithLocalization(<DateInput value={null} setValue={() => {}} readOnly={false} mandatory={true} errorText="Required" />);

        expect(screen.getByText("Required")).toBeInTheDocument();
    });

    it("should not be editable when readOnly is true", () => {
        render(
            <LocalizationProvider dateAdapter={AdapterDayjs}>
                <DateInput value={"2023-01-01"} setValue={() => {}} readOnly={true} mandatory={true} errorText="Required" />
            </LocalizationProvider>,
        );
        const dateInput = screen.getByRole("textbox");
        expect(dateInput).toBeDisabled();

        userEvent.click(dateInput);
        expect(screen.queryByRole("dialog")).toBeNull();

        expect(dateInput).toHaveValue("01-01-2023");
    });
});
