import { render, fireEvent, screen } from "@testing-library/react";
import "@testing-library/jest-dom";
import DateInput from "./DateInput"; // Adjust the import path as needed
import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";

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
        renderWithLocalization(<DateInput value={"2023-01-01"} setValue={() => {}} readOnly mandatory={true} errorText="Required" />);

        expect(screen.queryByRole("textbox")).toBeNull();

        const displayedDate = screen.getByText("01-01-2023");
        expect(displayedDate).toBeInTheDocument();
    });
});
