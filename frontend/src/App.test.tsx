import React from "react";
import { render, screen } from "@testing-library/react";
import App from "./App";

test("renders projecten", () => {
    render(<App />);
    const vngElement = screen.getByText(/Overzicht projecten/i);
    expect(vngElement).toBeInTheDocument();
});
