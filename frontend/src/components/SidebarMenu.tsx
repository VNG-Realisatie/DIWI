import * as Paths from "../Paths";

export type MenuItem = {
    name: string;
    required_permission: string[];
    link: string;
};

export type MenuSection = {
    header: string;
    menuItems: MenuItem[];
};

export const menu: MenuSection[] = [
    {
        header: "sidebar.projects",
        menuItems: [
            {
                name: "sidebar.projectOverview",
                required_permission: ["CAN_OWN_PROJECTS", "VIEW_OTHERS_PROJECTS", "VIEW_OWN_PROJECTS"],
                link: "/projects/table",
            },
            {
                name: "sidebar.addProject",
                required_permission: ["CREATE_NEW_PROJECT"],
                link: "/project/create",
            },
        ],
    },
    {
        header: "sidebar.dashboards",
        menuItems: [
            {
                name: "sidebar.goals",
                required_permission: ["VIEW_GOALS"],
                link: Paths.goals.path,
            },
            {
                name: "sidebar.dashboardProject",
                required_permission: ["VIEW_ALL_BLUEPRINTS"],
                link: Paths.dashboard.path,
            },
            {
                name: "sidebar.makeCustomDashboard",
                required_permission: ["EDIT_ALL_BLUEPRINTS"],
                link: Paths.createCustomDashboard.path,
            },
            {
                name: "sidebar.customDashboardList",
                required_permission: ["VIEW_OWN_BLUEPRINTS"],
                link: Paths.customDashboardList.path,
            },
        ],
    },
    {
        header: "sidebar.settings",
        menuItems: [
            {
                name: "customProperties.title",
                required_permission: ["VIEW_CUSTOM_PROPERTIES"],
                link: Paths.userSettings.path,
            },
            {
                name: "sidebar.priceCategories",
                required_permission: ["VIEW_CUSTOM_PROPERTIES"],
                link: Paths.priceCategories.path,
            },
            {
                name: "sidebar.users",
                required_permission: ["VIEW_USERS", "VIEW_GROUPS"],
                link: Paths.userManagement.path,
            },
        ],
    },
    {
        header: "sidebar.dataExchange",
        menuItems: [
            {
                name: "sidebar.import",
                required_permission: ["IMPORT_PROJECTS"],
                link: Paths.exchangeimportdata.path,
            },
            {
                name: "sidebar.exportSettings",
                required_permission: ["EXPORT_PROJECTS"],
                link: Paths.exportSettings.path,
            },
            {
                name: "sidebar.export",
                required_permission: ["EXPORT_PROJECTS"],
                link: Paths.configuredExport.path,
            },
        ],
    },
];
