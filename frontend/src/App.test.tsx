import { render, screen } from "@testing-library/react";
import { Layout } from "./components/Layout";
import { MemoryRouter } from "react-router-dom";
import ConfigContext from "./context/ConfigContext";
import { t } from "i18next";

jest.mock("query-string", () => ({
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
    const vngElement = screen.getByText(t("sidebar.projectOverview"));
    expect(vngElement).toBeInTheDocument();
});
