import { render } from "@testing-library/react";
import NetherlandsMap from "./NetherlandsMap";

it("should render", () => {
    render(<NetherlandsMap width="100px" height="100px" />);
});
