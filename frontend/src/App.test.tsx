import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { expect, test, vi } from "vitest";
import { Layout } from "./components/Layout";
import ConfigContext from "./context/ConfigContext";

vi.mock("query-string", () => ({
    //mock whatever you use from query-string
    parse: jest.fn(),
    stringify: jest.fn(),
}));

test("renders projecten", () => {
    render(
        <MemoryRouter>
            <ConfigContext.Provider value={{ municipalityName: "test", mapBounds: { corner1: { lng: 0, lat: 0 }, corner2: { lng: 0, lat: 0 } } }}>
                <Layout />
            </ConfigContext.Provider>
        </MemoryRouter>,
    );
    const vngElement = screen.getByText(/Overzicht projecten/i);
    expect(vngElement).toBeInTheDocument();
});
