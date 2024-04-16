import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { NetPlanEditInput } from "./NetPlanCapacityInput";

it("should render NetPlanEditInput", () => {
    const updateHouseBlockNetPlan = jest.fn();
    const houseBlockNetPlan = null;

    render(<NetPlanEditInput houseBlockNetPlan={houseBlockNetPlan} updateHouseBlockNetPlan={updateHouseBlockNetPlan} />);

    expect(screen.getByRole("spinbutton")).toBeInTheDocument();
});

it("should enter a number and check the value passed to update updateHouseBlockNetPlan", () => {
    const updateHouseBlockNetPlan = jest.fn();
    const houseBlockNetPlan = null;

    render(<NetPlanEditInput houseBlockNetPlan={houseBlockNetPlan} updateHouseBlockNetPlan={updateHouseBlockNetPlan} />);

    const input = screen.getByRole("spinbutton");
    userEvent.type(input, "10");

    expect(updateHouseBlockNetPlan).toHaveBeenLastCalledWith(10);
});
