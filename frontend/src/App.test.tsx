import { render, screen } from "@testing-library/react";
import { Layout } from "./components/Layout";
import { MemoryRouter } from "react-router-dom";
import ConfigContext from "./context/ConfigContext";

jest.mock("query-string", () => ({
    //mock whatever you use from query-string
    parse: jest.fn(),
    stringify: jest.fn(),
}));

test("renders projecten", () => {
    render(
        <MemoryRouter>
            <ConfigContext.Provider value={{ municipalityName: "test", mapBounds: null }}>
                <Layout />
            </ConfigContext.Provider>
        </MemoryRouter>,
    );
    const vngElement = screen.getByText(/Overzicht projecten/i);
    expect(vngElement).toBeInTheDocument();
});
