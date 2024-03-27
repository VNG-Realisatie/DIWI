import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { DemolitionPlanEditInput } from "./DemolitionPlanCapacityInput";

it("should render DemolitionPlanEditInput", () => {
    const updateHouseBlockDemolitionPlan = jest.fn();
    const houseBlockDemolitionPlan = null;

    render(<DemolitionPlanEditInput houseBlockDemolitionPlan={houseBlockDemolitionPlan} updateHouseBlockDemolitionPlan={updateHouseBlockDemolitionPlan} />);

    expect(screen.getByRole("spinbutton")).toBeInTheDocument();
});

it("should enter a number and check the value passed to update updateHouseBlockDemolitionPlan", () => {
    const updateHouseBlockDemolitionPlan = jest.fn();
    const houseBlockDemolitionPlan = null;

    render(<DemolitionPlanEditInput houseBlockDemolitionPlan={houseBlockDemolitionPlan} updateHouseBlockDemolitionPlan={updateHouseBlockDemolitionPlan} />);

    const input = screen.getByRole("spinbutton");
    userEvent.type(input, "10");

    expect(updateHouseBlockDemolitionPlan).toHaveBeenLastCalledWith(10);
});
