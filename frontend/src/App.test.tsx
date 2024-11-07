import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { expect, test, vi } from "vitest";
import { Layout } from "./components/Layout";
import ConfigContext from "./context/ConfigContext";
import { t } from "i18next";
import { UserProvider } from "./context/UserContext";

vi.mock("query-string", () => ({
    //mock whatever you use from query-string
    parse: vi.fn(),
    stringify: vi.fn(),
}));

vi.mock("./api/userServices", () => ({
    getCurrentUser: vi.fn().mockResolvedValue({}),
}));

vi.mock("utils/requests", () => ({
    getJson: vi.fn().mockResolvedValue({}),
}));

test("renders sidebar", () => {
    render(
        <MemoryRouter>
            <UserProvider>
                <ConfigContext.Provider value={{ municipalityName: "test", mapBounds: { corner1: { lng: 0, lat: 0 }, corner2: { lng: 0, lat: 0 } } }}>
                    <Layout />
                </ConfigContext.Provider>
            </UserProvider>
        </MemoryRouter>,
    );
    const vngElement = screen.getByText(t("sidebar.knowledgeBase"));
    expect(vngElement).toBeInTheDocument();
});
