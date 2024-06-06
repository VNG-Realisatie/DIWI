import { render, screen } from "@testing-library/react";
import { vi } from "vitest";
import { HouseBlockProvider } from "../context/HouseBlockContext";
import { ProjectProvider } from "../context/ProjectContext";
import TestComponentWrapper from "../test/TestComponentWrapper";
import ProjectWizard from "./ProjectWizard";

vi.mock("../api/adminSettingServices", () => ({
    getCustomProperties: vi.fn().mockResolvedValue([]),
    getCustomPropertiesWithQuery: vi.fn().mockResolvedValue([]),
}));

vi.mock("../api/projectsTableServices", () => ({
    getOrganizationList: vi.fn().mockResolvedValue([]),
}));

test("renders project wizard page 1", () => {
    render(
        <TestComponentWrapper>
            <ProjectProvider>
                <HouseBlockProvider>
                    <ProjectWizard />
                </HouseBlockProvider>
            </ProjectProvider>
        </TestComponentWrapper>,
    );

    // Test whether the next button doesn't work when the required fields are not filled in
    const nextButton = screen.getByRole("button", { name: "generic.next" });
    expect(nextButton).toBeDisabled();
});
