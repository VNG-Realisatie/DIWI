import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { GrossPlanEditInput } from "./GrossPlanCapacityInput";
import { DemolitionPlanEditInput } from "./DemolitionPlanCapacityInput";
import { NetPlanEditInput } from "./NetPlanCapacityInput";

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
