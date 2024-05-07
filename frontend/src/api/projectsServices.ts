import { getJson, postJson, deleteJson, postJsonNoResponse } from "../utils/requests";
import { components } from "../types/schema";
import { API_URI } from "../utils/urls";
import { GeoJSONGeometry, GeoJSONMultiPolygon, GeoJSONPolygon } from "ol/format/GeoJSON";
import { putCustomPropertyValue } from "./customPropServices";

export type Organization = components["schemas"]["OrganizationModel"];
export type OrganizationUser = components["schemas"]["OrganizationUserModel"];
export type ProjectListModel = components["schemas"]["ProjectListModel"];
export type Project = components["schemas"]["ProjectSnapshotModel"];
export type ProjectSnapshotModel = components["schemas"]["ProjectSnapshotModel"];
export type ProjectCreate = components["schemas"]["ProjectCreateSnapshotModel"];
export type SelectModel = components["schemas"]["SelectModel"];
export type PriorityModel = components["schemas"]["PriorityModel"];

export type PlotGeoJSON = {
    type: string;
    features: {
        id: string;
        type: string;
        bbox: number[];
        geometry: GeoJSONGeometry;
        properties: {
            AKRKadastraleGemeenteCodeCode: string;
            AKRKadastraleGemeenteCodeWaarde: string;
            beginGeldigheid: string;
            identificatieLokaalID: string;
            identificatieNamespace: string;
            kadastraleGemeenteCode: string;
            kadastraleGemeenteWaarde: string;
            kadastraleGrootteWaarde: string;
            perceelnummer: string;
            perceelnummerPlaatscoordinaatX: string;
            perceelnummerPlaatscoordinaatY: string;
            perceelnummerRotatie: string;
            perceelnummerVerschuivingDeltaX: string;
            perceelnummerVerschuivingDeltaY: string;
            sectie: string;
            soortGrootteCode: string;
            soortGrootteWaarde: string;
            statusHistorieCode: string;
            statusHistorieWaarde: string;
            tijdstipRegistratie: string;
            volgnummer: string;
        };
    }[];
    crs: {
        properties: {
            name: string;
        };
        type: string;
    };
};

type SizeData = {
    size: number;
};

// The generated plot model doesn't work as the geojson definition is not correct.
// Replace by PlotGeoJSON for now.
type OgPlot = components["schemas"]["PlotModel"];
// export type Plot = Pick<OgPlot, Exclude<keyof OgPlot, "geoJson">> & { plotFeature: PlotGeoJSON };
export type Plot = Omit<OgPlot, "subselectionGeometry" | "plotFeature"> & {
    plotFeature: PlotGeoJSON;
    subSelectionGeometry?: GeoJSONMultiPolygon | GeoJSONPolygon;
};

export async function updateProjectWithCustomProperties(project: Project): Promise<Project> {
    // destructure houseblock from customproperties as they need to be sent separately
    const { customProperties, ...projectNoProperties } = project;
    const newProject = await updateProject(projectNoProperties);

    if (customProperties) {
        // for each custom prop, send it to the backend
        const [...newCustomProperties] = await Promise.all(customProperties.map((cp) => putCustomPropertyValue(project.projectId, cp)));
        return { ...newProject, customProperties: newCustomProperties };
    }
    return newProject;
}

export async function getProjects(pageNumber: number, pageSize: number): Promise<Array<Project>> {
    return getJson(`${API_URI}/projects/table?pageNumber=${pageNumber}&pageSize=${pageSize}`);
}

export async function getProjectsSize(): Promise<SizeData> {
    return getJson(`${API_URI}/projects/table/size`);
}

export async function getProject(id: string): Promise<Project> {
    return getJson(`${API_URI}/projects/${id}`);
}

export async function updateProject(newData: ProjectSnapshotModel): Promise<ProjectSnapshotModel> {
    return postJson(`${API_URI}/projects/update`, newData);
}

export async function deleteProject(id: string | null) {
    return deleteJson(`${API_URI}/projects/${id}`);
}

export async function createProject(projectData: ProjectCreate): Promise<Project> {
    return postJson(`${API_URI}/projects`, projectData);
}

export async function getProjectPlots(id: string): Promise<Plot[]> {
    return getJson(`${API_URI}/projects/${id}/plots`);
}

export async function updateProjectPlots(id: string, plots: Plot[]): Promise<Response> {
    return postJsonNoResponse(`${API_URI}/projects/${id}/plots`, plots);
}
