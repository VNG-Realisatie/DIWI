import { render } from "@testing-library/react";
import NetherlandsMap from "./NetherlandsMap";
import { dummyMapData } from "../../pages/ProjectDetail";
import { MemoryRouter } from "react-router-dom";

it("should render", () => {
    render(
        <MemoryRouter>
            <NetherlandsMap width="100px" height="100px" mapData={dummyMapData} />
        </MemoryRouter>,
    );
});
