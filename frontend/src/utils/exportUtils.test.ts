import { Property } from "../api/adminSettingServices";
import { ExportProperty } from "../api/exportServices";
import { doesPropertyMatchExportProperty } from "./exportUtils";

it("should allow the correct types", () => {
    const property: ExportProperty = {
        name: "",
        objectType: "PROJECT",
        mandatory: true,
        propertyTypes: ["BOOLEAN", "TEXT"],
    };

    const customProperty: Property = {
        name: "",
        type: "CUSTOM",
        objectType: "PROJECT",
        propertyType: "TEXT",
        disabled: false,
        mandatory: true,
    };

    expect(doesPropertyMatchExportProperty(property, customProperty)).toBe(true);
});
