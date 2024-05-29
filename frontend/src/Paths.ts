import { compile } from "path-to-regexp";

import config from "./config";

function createPathObject<P extends object>(path: string) {
    return {
        path: path, // Should only be used for routes
        toPath: compile<P>(path), // Should be used for links
    };
}

export const root = createPathObject(config.baseurl);

type ProjectId = { projectId: string };

export const projects = createPathObject(config.baseurl + "projects");
export const projectsTable = createPathObject(config.baseurl + "projects/table");
export const projectWizard = createPathObject(config.baseurl + "project/create");
export const projectWizardWithId = createPathObject<ProjectId>(config.baseurl + "project/create/:projectId");
export const projectWizardBlocks = createPathObject<ProjectId>(config.baseurl + "project/create/:projectId/blocks");
export const projectWizardMap = createPathObject<ProjectId>(config.baseurl + "project/create/:projectId/map");
export const projectDetail = createPathObject<ProjectId>(config.baseurl + "projects/:projectId");
export const projectDetailCharacteristics = createPathObject<ProjectId>(config.baseurl + "projects/:projectId/characteristics");
export const projectDetailTimeline = createPathObject<ProjectId>(config.baseurl + "projects/:projectId/timeline");

export const policygoal = createPathObject(config.baseurl + "policygoal");
export const policygoalDashboard = createPathObject(config.baseurl + "policygoal/dashboard");
export const dashboard = createPathObject(config.baseurl + "dashboard");
export const wizard = createPathObject(config.baseurl + "admin/wizard");
export const exchangedata = createPathObject(config.baseurl + "exchangedata");
export const exportExcel = createPathObject(config.baseurl + "exchangedata/exportexcel");
export const exportProvince = createPathObject(config.baseurl + "exchangedata/exportprovince");
export const importExcel = createPathObject(config.baseurl + "exchangedata/importexcel");
export const importGeoJson = createPathObject(config.baseurl + "exchangedata/importgeojson");
export const importSquit = createPathObject(config.baseurl + "exchangedata/importsquit");
export const importExcelProjects = createPathObject(config.baseurl + "exchangedata/importexcel/projects");
export const importSquitProjects = createPathObject(config.baseurl + "exchangedata/importsquit/projects");

export const login = createPathObject(config.baseurl + "rest/auth/login");
export const loggedIn = createPathObject(config.baseurl + "rest/auth/loggedIn");
export const logout = createPathObject(config.baseurl + "rest/auth/logout");
export const home = createPathObject(config.baseurl + "home");
export const about = createPathObject(config.baseurl + "about");
export const forbidden = createPathObject(config.baseurl + "forbidden");

export const userInfo = createPathObject(config.baseurl + "users/userinfo");

export const userSettings = createPathObject(config.baseurl + "admin/settings");

export const swagger = createPathObject(config.baseurl + "swagger");
