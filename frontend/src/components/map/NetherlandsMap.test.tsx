import { render } from "@testing-library/react";
import NetherlandsMap from "./NetherlandsMap";
import { dummyMapData } from "../../pages/ProjectDetail";

it("should render", () => {
    render(<NetherlandsMap width="100px" height="100px" mapData={dummyMapData} />);
});
