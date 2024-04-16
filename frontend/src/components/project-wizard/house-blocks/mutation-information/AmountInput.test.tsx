import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { AmountEditInput } from "./AmountInput";

it("should render AmountEditInput", () => {
    const updateHouseBlockAmount = jest.fn();
    const houseBlockAmount = undefined;

    render(<AmountEditInput houseBlockAmount={houseBlockAmount} updateHouseBlockAmount={updateHouseBlockAmount} />);

    expect(screen.getByRole("spinbutton")).toBeInTheDocument();
});

it("should enter a number and check the value passed to update updateHouseBlockAmount", () => {
    const updateHouseBlockAmount = jest.fn();
    const houseBlockAmount = undefined;

    render(<AmountEditInput houseBlockAmount={houseBlockAmount} updateHouseBlockAmount={updateHouseBlockAmount} />);

    const input = screen.getByRole("spinbutton");
    userEvent.type(input, "10");

    expect(updateHouseBlockAmount).toHaveBeenLastCalledWith(10);
});
