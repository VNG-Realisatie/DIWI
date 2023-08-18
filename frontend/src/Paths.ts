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
export const projectAdd = createPathObject(config.baseurl + "projects/add");
export const projectDetail= createPathObject(config.baseurl + "projects/:id");

export const dashboard = createPathObject(config.baseurl + "dashboard");
export const wizard = createPathObject(config.baseurl + "admin/wizard");



export const login = createPathObject(config.baseurl + "rest/auth/login");
export const loggedIn = createPathObject(config.baseurl + "rest/auth/loggedIn");
export const logout = createPathObject(config.baseurl + "rest/auth/logout");
export const home = createPathObject(config.baseurl + "home");
