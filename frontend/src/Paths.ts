import { compile } from "path-to-regexp";

import config from "./config";

type Path = {
    path: string;
    toPath: Function;
};

function createPathObject(path: string): Path {
    return {
        path: path,
        toPath: compile(path),
    };
}

export const root = createPathObject(config.baseurl);

export const projects = createPathObject(config.baseurl + "projects");
export const projectAdd = createPathObject(config.baseurl + "project/create");
export const projectDetail= createPathObject(config.baseurl + "projects/:id");

export const policygoal = createPathObject(config.baseurl + "policygoal");
export const dashboard = createPathObject(config.baseurl + "dashboard");
export const wizard = createPathObject(config.baseurl + "admin/wizard");
export const exchangedata = createPathObject(config.baseurl + "exchangedata");
export const exportExcel = createPathObject(config.baseurl + "exchangedata/exportexcel");
export const exportProvince = createPathObject(config.baseurl + "exchangedata/exportprovince");
export const importExcel = createPathObject(config.baseurl + "exchangedata/importexcel");
export const importExcelProjects = createPathObject(config.baseurl + "exchangedata/importexcel/projects");



export const login = createPathObject(config.baseurl + "rest/auth/login");
export const loggedIn = createPathObject(config.baseurl + "rest/auth/loggedIn");
export const logout = createPathObject(config.baseurl + "rest/auth/logout");
export const home = createPathObject(config.baseurl + "home");
