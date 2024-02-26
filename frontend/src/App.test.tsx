import React from "react";
import { render, screen } from "@testing-library/react";
import { Layout } from "./components/Layout";
import { MemoryRouter } from "react-router-dom";

test("renders projecten", () => {
    render(
        <MemoryRouter>
            <Layout />
        </MemoryRouter>,
    );
    const vngElement = screen.getByText(/Overzicht projecten/i);
    expect(vngElement).toBeInTheDocument();
});
