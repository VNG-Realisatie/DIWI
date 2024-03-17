import { render, screen } from "@testing-library/react";
import { Layout } from "./components/Layout";
import { MemoryRouter } from "react-router-dom";

jest.mock("query-string", () => ({
    //mock whatever you use from query-string
    parse: jest.fn(),
    stringify: jest.fn(),
}));

test("renders projecten", () => {
    render(
        <MemoryRouter>
            <Layout />
        </MemoryRouter>,
    );
    const vngElement = screen.getByText(/Overzicht projecten/i);
    expect(vngElement).toBeInTheDocument();
});
