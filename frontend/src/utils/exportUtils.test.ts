import { Property } from "../api/adminSettingServices";
import { ExportProperty } from "../api/exportServices";
import { doesPropertyMatchExportProperty } from "./exportUtils";

it.each([
    [{ propertyTypes: ["BOOLEAN", "TEXT"] }, { propertyType: "TEXT" }],
    [{ propertyTypes: ["CATEGORY", "TEXT"] }, { propertyType: "TEXT" }],
    [{ propertyTypes: ["CATEGORY", "TEXT"] }, { propertyType: "CATEGORY" }],
])("should allow the correct types", (dxProp, diwiProp) => {
    const defaultDxProp: ExportProperty = {
        name: "",
        objectType: "PROJECT",
        mandatory: true,
        propertyTypes: ["BOOLEAN", "TEXT"],
    };

    const defaultDiwiProp: Property = {
        name: "",
        type: "CUSTOM",
        objectType: "PROJECT",
        propertyType: "TEXT",
        disabled: false,
        mandatory: true,
    };

    expect(doesPropertyMatchExportProperty(defaultDxProp, defaultDiwiProp)).toBe(true);
});
