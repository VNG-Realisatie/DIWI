import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { GrossPlanEditInput } from "./GrossPlanCapacityInput";

it("should render GrossPlanEditInput", () => {
    const updateHouseBlockGrossPlan = jest.fn();
    const houseBlockGrossPlan = null;

    render(<GrossPlanEditInput houseBlockGrossPlan={houseBlockGrossPlan} updateHouseBlockGrossPlan={updateHouseBlockGrossPlan} />);

    expect(screen.getByRole("spinbutton")).toBeInTheDocument();
});

it("should enter a number and check the value passed to update updateHouseBlockGrossPlan", () => {
    const updateHouseBlockGrossPlan = jest.fn();
    const houseBlockGrossPlan = null;

    render(<GrossPlanEditInput houseBlockGrossPlan={houseBlockGrossPlan} updateHouseBlockGrossPlan={updateHouseBlockGrossPlan} />);

    const input = screen.getByRole("spinbutton");
    userEvent.type(input, "10");

    expect(updateHouseBlockGrossPlan).toHaveBeenLastCalledWith(10);
});
