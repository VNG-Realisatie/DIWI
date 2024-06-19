import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import RangeNumberInput from "./RangeNumberInput";
import { vi } from "vitest";

it("should enter a number in numeric range input and check the value passed to update callback", () => {
    const updateCallBack = vi.fn();
    const value = { value: null, min: null, max: null };

    render(<RangeNumberInput value={value} updateCallBack={updateCallBack} mandatory={false} readOnly={false} />);

    const input = screen.getByRole("textbox");
    userEvent.type(input, "10");
    userEvent.keyboard("{enter}");

    expect(updateCallBack).toHaveBeenLastCalledWith({ value: 10, min: null, max: null });
});

it("should enter a range in numeric range input and check the value passed to update callback", () => {
    const updateCallBack = vi.fn();
    const value = { value: null, min: null, max: null };

    render(<RangeNumberInput value={value} updateCallBack={updateCallBack} mandatory={false} readOnly={false} />);

    const input = screen.getByRole("textbox");
    userEvent.type(input, "10-20");
    userEvent.keyboard("{enter}");

    expect(updateCallBack).toHaveBeenLastCalledWith({ value: null, min: 10, max: 20 });
});

it("should handle only min and treat as open ended range", () => {
    const updateCallBack = vi.fn();
    const value = { value: null, min: null, max: null };

    render(<RangeNumberInput value={value} updateCallBack={updateCallBack} mandatory={false} readOnly={false} />);

    const input = screen.getByRole("textbox");
    userEvent.type(input, "10-");
    userEvent.keyboard("{enter}");

    expect(updateCallBack).toHaveBeenLastCalledWith({ value: null, min: 10, max: null });
});

it("should handle only max and treat as value", () => {
    const updateCallBack = vi.fn();
    const value = { value: null, min: null, max: null };

    render(<RangeNumberInput value={value} updateCallBack={updateCallBack} mandatory={false} readOnly={false} />);

    const input = screen.getByRole("textbox");
    userEvent.type(input, "-10");
    userEvent.keyboard("{enter}");

    expect(updateCallBack).toHaveBeenLastCalledWith({ value: 10, min: null, max: null });
});

// Tests for isMonetary = true

it("should enter a number in monetary range input and check the value passed to update callback", () => {
    const updateCallBack = vi.fn();
    const value = { value: null, min: null, max: null };

    render(<RangeNumberInput value={value} updateCallBack={updateCallBack} mandatory={false} readOnly={false} isMonetary={true} />);

    const input = screen.getByRole("textbox");
    userEvent.type(input, "10");
    userEvent.keyboard("{enter}");

    expect(updateCallBack).toHaveBeenLastCalledWith({ value: 1000, min: null, max: null });
});

it("should enter a range in monetary range input and check the value passed to update callback", () => {
    const updateCallBack = vi.fn();
    const value = { value: null, min: null, max: null };

    render(<RangeNumberInput value={value} updateCallBack={updateCallBack} mandatory={false} readOnly={false} isMonetary={true} />);

    const input = screen.getByRole("textbox");
    userEvent.type(input, "10-20");
    userEvent.keyboard("{enter}");

    expect(updateCallBack).toHaveBeenLastCalledWith({ value: null, min: 1000, max: 2000 });
});

it("should handle only min and treat as open ended range in monetary range input", () => {
    const updateCallBack = vi.fn();
    const value = { value: null, min: null, max: null };

    render(<RangeNumberInput value={value} updateCallBack={updateCallBack} mandatory={false} readOnly={false} isMonetary={true} />);

    const input = screen.getByRole("textbox");
    userEvent.type(input, "10-");
    userEvent.keyboard("{enter}");

    expect(updateCallBack).toHaveBeenLastCalledWith({ value: null, min: 1000, max: null });
});

it("should handle only max and treat as value in monetary range input", () => {
    const updateCallBack = vi.fn();
    const value = { value: null, min: null, max: null };

    render(<RangeNumberInput value={value} updateCallBack={updateCallBack} mandatory={false} readOnly={false} isMonetary={true} />);

    const input = screen.getByRole("textbox");
    userEvent.type(input, "-10");
    userEvent.keyboard("{enter}");

    expect(updateCallBack).toHaveBeenLastCalledWith({ value: 1000, min: null, max: null });
});

it.each([
    [100, "1,00"],
    [111, "1,11"],
    [101, "1,01"],
    [110, "1,10"],
    [10, "0,10"],
    [10000, "100,00"],
])("should display input in cents as euros", (input, expected) => {
    const updateCallBack = vi.fn();
    const value = { value: input, min: null, max: null };

    render(<RangeNumberInput value={value} updateCallBack={updateCallBack} labelText="hi" mandatory={false} readOnly={false} isMonetary={true} />);

    expect(screen.getByRole("textbox")).toHaveValue(expected);
});

it.each([
    ["1,00", 100],
    ["1,11", 111],
    ["1,01", 101],
    ["1,10", 110],
    ["0,10", 10],
    ["100,00", 10000],
    ["1", 100],
    ["10", 1000],
    ["1,1", 110],
    ["1,0", 100],
    ["100,1", 10010],
    ["100,0", 10000],
    ["1", 100],
])("should convert euros to cents", (input, expected) => {
    const updateCallBack = vi.fn();
    const value = { value: null, min: null, max: null };

    render(<RangeNumberInput value={value} updateCallBack={updateCallBack} labelText="hi" mandatory={false} readOnly={false} isMonetary={true} />);

    const textbox = screen.getByRole("textbox");
    userEvent.type(textbox, input);
    userEvent.keyboard("{enter}");

    expect(updateCallBack).toHaveBeenLastCalledWith({ value: expected, min: null, max: null });
});

it("should limit the input to only accept up to two numbers after the comma", () => {
    const updateCallBack = vi.fn();
    const value = { value: null, min: null, max: null };

    render(<RangeNumberInput value={value} updateCallBack={updateCallBack} labelText="hi" mandatory={false} readOnly={false} isMonetary={true} />);

    const input = screen.getByRole("textbox");
    userEvent.type(input, "1,234");
    userEvent.keyboard("{enter}");

    expect(screen.getByRole("textbox")).toHaveValue("1,23");
    expect(updateCallBack).toHaveBeenLastCalledWith({ value: 123, min: null, max: null });
});

it("should not limit the input length if there is no comma", () => {
    const updateCallBack = vi.fn();
    const value = { value: null, min: null, max: null };

    render(<RangeNumberInput value={value} updateCallBack={updateCallBack} labelText="hi" mandatory={false} readOnly={false} isMonetary={true} />);

    const input = screen.getByRole("textbox");
    userEvent.type(input, "123456");
    userEvent.keyboard("{enter}");

    expect(screen.getByRole("textbox")).toHaveValue("123456");
    expect(updateCallBack).toHaveBeenLastCalledWith({ value: 12345600, min: null, max: null });
});

it("should clear the input value on focus if it's '0,00'", () => {
    const updateCallBack = vi.fn();
    const value = { value: null, min: null, max: null };

    render(<RangeNumberInput value={value} updateCallBack={updateCallBack} labelText="hi" mandatory={false} readOnly={false} isMonetary={true} />);

    const input = screen.getByRole("textbox");
    userEvent.click(input);

    expect(screen.getByRole("textbox")).toHaveValue("");
    expect(updateCallBack).not.toHaveBeenCalled();
});

it("should set the input value to empty string on click of the clear end adornment", () => {
    const updateCallBack = vi.fn();
    const value = { value: null, min: null, max: null };

    render(<RangeNumberInput value={value} updateCallBack={updateCallBack} labelText="hi" mandatory={false} readOnly={false} isMonetary={true} />);

    const input = screen.getByRole("textbox");
    userEvent.type(input, "12345");
    userEvent.click(screen.getByTestId("clear-input"));

    expect(screen.getByRole("textbox")).toHaveValue("");
    expect(updateCallBack).toHaveBeenLastCalledWith({ value: null, min: null, max: null });
});

it("should handle a range with one comma and one hyphen", () => {
    const updateCallBack = vi.fn();
    const value = { value: null, min: null, max: null };

    render(<RangeNumberInput value={value} updateCallBack={updateCallBack} labelText="hi" mandatory={false} readOnly={false} isMonetary={true} />);

    const input = screen.getByRole("textbox");
    userEvent.type(input, "10,20-3");
    userEvent.keyboard("{enter}");

    expect(updateCallBack).toHaveBeenLastCalledWith({ value: null, min: 1020, max: 300 });
});

it("should handle a range with one hyphen and two commas", () => {
    const updateCallBack = vi.fn();
    const value = { value: null, min: null, max: null };

    render(<RangeNumberInput value={value} updateCallBack={updateCallBack} labelText="hi" mandatory={false} readOnly={false} isMonetary={true} />);

    const input = screen.getByRole("textbox");
    userEvent.type(input, "10,2-20,30");
    userEvent.keyboard("{enter}");

    expect(updateCallBack).toHaveBeenLastCalledWith({ value: null, min: 1020, max: 2030 });
});

it("should not allow characters except for hyphens and commas", () => {
    const updateCallBack = vi.fn();
    const value = { value: null, min: null, max: null };

    render(<RangeNumberInput value={value} updateCallBack={updateCallBack} labelText="hi" mandatory={false} readOnly={false} isMonetary={true} />);

    const input = screen.getByRole("textbox");
    userEvent.type(input, "10,2a-20,30");
    userEvent.keyboard("{enter}");

    expect(screen.getByRole("textbox")).toHaveValue("10,2-20,30");
    expect(updateCallBack).toHaveBeenLastCalledWith({ value: null, min: 1020, max: 2030 });
});
