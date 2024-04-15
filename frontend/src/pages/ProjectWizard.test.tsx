import { render, screen } from "@testing-library/react";

import { ProjectProvider } from "../context/ProjectContext";
import TestComponentWrapper from "../test/TestComponentWrapper";
import ProjectWizard from "./ProjectWizard";

test("renders project wizard page 1", () => {
    render(
        <TestComponentWrapper>
            <ProjectProvider>
                <ProjectWizard />
            </ProjectProvider>
        </TestComponentWrapper>,
    );

    // Test whether the next button doesn't work when the required fields are not filled in
    const nextButton = screen.getByRole("button", { name: "generic.next" });
    expect(nextButton).toBeDisabled();
});
