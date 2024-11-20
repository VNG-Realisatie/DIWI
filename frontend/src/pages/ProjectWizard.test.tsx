import { render, screen } from "@testing-library/react";
import { vi } from "vitest";
import { HouseBlockProvider } from "../context/HouseBlockContext";
import { ProjectProvider } from "../context/ProjectContext";
import TestComponentWrapper from "../test/TestComponentWrapper";
import ProjectWizard from "./ProjectWizard";
import UserContext from "../context/UserContext";
import { UserRole } from "../api/userServices";

const mockUser = {
    name: "Test User",
    uuid: "test",
    firstName: "Test",
    lastName: "User",
    initials: "TU",
    role: "UserPlus" as UserRole,
};

vi.mock("../api/adminSettingServices", () => ({
    getCustomProperties: vi.fn().mockResolvedValue([]),
    getCustomPropertiesWithQuery: vi.fn().mockResolvedValue([]),
}));

vi.mock("../api/projectsTableServices", () => ({
    filterTable: vi.fn().mockResolvedValue([]),
    getUserGroupList: vi.fn().mockResolvedValue([]),
}));

vi.mock("../utils/requests", () => ({
    getJson: vi.fn().mockResolvedValue({}),
}));

test("renders project wizard page 1", () => {
    render(
        <TestComponentWrapper>
            <UserContext.Provider value={{ user: mockUser, allowedActions: [] }}>
                <ProjectProvider>
                    <HouseBlockProvider>
                        <ProjectWizard />
                    </HouseBlockProvider>
                </ProjectProvider>
            </UserContext.Provider>
        </TestComponentWrapper>,
    );

    // Test whether the next button doesn't work when the required fields are not filled in
    const nextButton = screen.getByRole("button", { name: "generic.next" });
    expect(nextButton).toBeDisabled();
});
