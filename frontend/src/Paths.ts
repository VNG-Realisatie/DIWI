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
export const projectAudit = createPathObject<ProjectId>(config.baseurl + "projects/:projectId/audit");

export const policygoal = createPathObject(config.baseurl + "policygoal");
export const policygoalDashboard = createPathObject(config.baseurl + "policygoal/dashboard");
export const dashboard = createPathObject(config.baseurl + "dashboard");
export const createCustomDashboard = createPathObject(config.baseurl + "dashboard/create-custom-dashboard");
export const updateCustomDashboard = createPathObject(config.baseurl + "dashboard/custom-dashboards/:id");
export const customDashboardList = createPathObject(config.baseurl + "dashboard/custom-dashboards");
export const dashboardProject = createPathObject(config.baseurl + "dashboard/project/:projectId");
export const goals = createPathObject(config.baseurl + "goals");
export const goalWizard = createPathObject(config.baseurl + "goals/create");
export const goalMenu = createPathObject(config.baseurl + "goals/:goalId");
export const wizard = createPathObject(config.baseurl + "admin/wizard");
export const exchangedata = createPathObject(config.baseurl + "exchangedata");
export const exchangeimportdata = createPathObject(config.baseurl + "exchangeimportdata");
export const exportExcel = createPathObject(config.baseurl + "exchangedata/exportexcel");
export const exportProvince = createPathObject(config.baseurl + "exchangedata/exportprovince");
export const configuredExport = createPathObject(config.baseurl + "exchangedata/export/:exportId");
export const confidentialityUpdate = createPathObject(config.baseurl + "exchangedata/confidentiality-update/:exportId");
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

export const userInfo = createPathObject(config.baseurl + "rest/users/userinfo");

export const userSettings = createPathObject(config.baseurl + "admin/settings");
export const userManagement = createPathObject(config.baseurl + "admin/user-management");
export const priceCategories = createPathObject(config.baseurl + "admin/price-categories");
export const exportSettings = createPathObject(config.baseurl + "admin/export-settings");
export const createExportSettings = createPathObject(config.baseurl + "admin/export-settings/create");
export const updateExportSettings = createPathObject(config.baseurl + "admin/export-settings/:id");

export const swagger = createPathObject(config.baseurl + "swagger");
