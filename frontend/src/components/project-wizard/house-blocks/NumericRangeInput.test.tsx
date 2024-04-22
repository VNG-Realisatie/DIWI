import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { NumericRangeInput } from "./NumericRangeInput";

it("should enter a number in numeric range input and check the value passed to update callback", () => {
    const updateCallBack = jest.fn();
    const value = { value: null, min: null, max: null };

    render(<NumericRangeInput value={value} updateCallBack={updateCallBack} labelText="hi" />);

    const input = screen.getByRole("textbox");
    userEvent.type(input, "10");
    userEvent.keyboard("{enter}");

    expect(updateCallBack).toHaveBeenLastCalledWith({ value: 10, min: null, max: null });
});

it("should enter a range in numeric range input and check the value passed to update callback", () => {
    const updateCallBack = jest.fn();
    const value = { value: null, min: null, max: null };

    render(<NumericRangeInput value={value} updateCallBack={updateCallBack} labelText="hi" />);

    const input = screen.getByRole("textbox");
    userEvent.type(input, "10-20");
    userEvent.keyboard("{enter}");

    expect(updateCallBack).toHaveBeenLastCalledWith({ value: null, min: 10, max: 20 });
});

it("should handle only min and treat as open ended range", () => {
    const updateCallBack = jest.fn();
    const value = { value: null, min: null, max: null };

    render(<NumericRangeInput value={value} updateCallBack={updateCallBack} labelText="hi" />);

    const input = screen.getByRole("textbox");
    userEvent.type(input, "10-");
    userEvent.keyboard("{enter}");

    expect(updateCallBack).toHaveBeenLastCalledWith({ value: null, min: 10, max: null });
});

it("should handle only max and treat as value", () => {
    const updateCallBack = jest.fn();
    const value = { value: null, min: null, max: null };

    render(<NumericRangeInput value={value} updateCallBack={updateCallBack} labelText="hi" />);

    const input = screen.getByRole("textbox");
    userEvent.type(input, "-10");
    userEvent.keyboard("{enter}");

    expect(updateCallBack).toHaveBeenLastCalledWith({ value: 10, min: null, max: null });
});
