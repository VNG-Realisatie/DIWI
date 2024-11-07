import { BrowserRouter, Route, Routes } from "react-router-dom";
import { useEffect, useState, useContext } from "react";
import { ScopedCssBaseline, ThemeProvider } from "@mui/material";
import "./App.css";
import AlertContext from "./context/AlertContext";
import { AlertProvider } from "./context/AlertContext";
import AlertPopup from "./components/AlertPopup";
import { Layout } from "./components/Layout";
import { Projects } from "./pages/Projects";
import { NoMatch } from "./pages/NoMatch";
import * as Paths from "./Paths";
import { ProjectDetail } from "./pages/ProjectDetail";
import { ProjectProvider } from "./context/ProjectContext";
import { PolicyLists } from "./pages/PolicyLists";
import { DashboardProjects } from "./pages/DashboardProjects";
import { ExchangeData } from "./pages/ExchangeData";
import { ExchangeImportData } from "./pages/ExchangeImportData";
import { ExportProject } from "./pages/ExportProject";
import { ImportedProjects } from "./pages/ImportedProjects";
import { About } from "./pages/About";
import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { Swagger } from "./pages/Swagger";
import { diwiFetch } from "./utils/requests";
import { ProjectsWithHouseBlock } from "./components/project/project-with-house-block/ProjectWithHouseBlock";
import { Settings } from "./components/admin/Settings";
import { theme } from "./theme";
import { dateFormats } from "./localization";
import { ProjectTimeline } from "./pages/ProjectTimeline";
import ProjectPlotSelector from "./components/map/ProjectPlotSelector";
import { ConfigProvider } from "./context/ConfigContext";
import UserContext, { UserProvider } from "./context/UserContext";
import "dayjs/locale/nl";
import { HouseBlockProvider } from "./context/HouseBlockContext";
import ProjectWizard from "./pages/ProjectWizard";
import ProjectWizardBlocks from "./pages/ProjectWizardBlocks";
import { LoadingProvider } from "./context/LoadingContext";
import { ImportPage } from "./pages/ImportPage";
import UserManagement from "./pages/UserManagement";
import { Forbidden } from "./pages/Forbidden";
import { DashboardProject } from "./pages/DashboardProject";
import PriceCategories from "./pages/PriceCategories";
import { CreateCustomDashboard } from "./pages/CreateCustomDashboard";
import { CustomDashboardList } from "./pages/CustomDashboardList";
import { Goals } from "./pages/Goals";
import { GoalWizard } from "./pages/GoalWizard";
import ExportAdminPage from "./pages/ExportAdminPage";
import useCurrentUserRole from "./hooks/useCurrentUserRole";
import ExportSettings from "./pages/ExportSettings";
import ExportWizard from "./pages/ExportWizard";

enum UserStatus {
    Authenticated,
    Unauthenticated,
}

enum LoadingStatus {
    Loading,
    NotLoading,
}

type UserLoadingStatus = {
    user: UserStatus;
    loading: LoadingStatus;
};

function RequiresLogin() {
    const [kcAuthenticated, setKcAuthenticated] = useState<boolean>(false);
    const [status, setStatus] = useState<UserLoadingStatus>({
        user: UserStatus.Unauthenticated,
        loading: LoadingStatus.Loading,
    });
    const { setAlert } = useContext(AlertContext);

    useEffect(() => {
        diwiFetch(Paths.loggedIn.path)
            .then((res) => {
                if (res.ok) {
                    setKcAuthenticated(true);
                } else {
                    setKcAuthenticated(false);
                }
            })
            .catch((error) => {
                setAlert(error.message, "error");
                setKcAuthenticated(false);
            });
    }, [setAlert]);

    useEffect(() => {
        if (kcAuthenticated) {
            fetch(Paths.userInfo.path)
                .then((response) => {
                    if (response.ok) {
                        setStatus((prevStatus) => ({
                            ...prevStatus,
                            user: UserStatus.Authenticated,
                        }));
                    }
                    if (response.status === 401) {
                        const returnUrl = window.location.origin + window.location.pathname + window.location.search;
                        window.location.href = `${Paths.login.path}?returnUrl=${encodeURIComponent(returnUrl)}`;
                        setKcAuthenticated(false);
                        setStatus((prevStatus) => ({
                            ...prevStatus,
                            user: UserStatus.Unauthenticated,
                        }));
                    }
                    if (response.status === 403) {
                        setStatus((prevStatus) => ({
                            ...prevStatus,
                            user: UserStatus.Unauthenticated,
                        }));
                    }
                })
                .catch(() => {
                    setStatus((prevStatus) => ({
                        ...prevStatus,
                        user: UserStatus.Unauthenticated,
                    }));
                })
                .finally(() => {
                    setStatus((prevStatus) => ({
                        ...prevStatus,
                        loading: LoadingStatus.NotLoading,
                    }));
                });
        } else {
            setStatus((prevStatus) => ({
                ...prevStatus,
                user: UserStatus.Unauthenticated,
            }));
        }
    }, [kcAuthenticated]);

    if (kcAuthenticated) {
        if (status.user === UserStatus.Authenticated) {
            return (
                <UserProvider>
                    <ConfigProvider>
                        {/* configprovider does a fetch, so first check login for this specific one */}
                        <Layout />
                    </ConfigProvider>
                </UserProvider>
            );
        } else if (status.loading === LoadingStatus.NotLoading) {
            return <Forbidden />;
        }
    }
    // default just returns null so page stays empty and we can redirect
    return null;
}

const Providers = ({ children }: { children: React.ReactNode }) => {
    return (
        <ThemeProvider theme={theme}>
            <LoadingProvider>
                <AlertProvider>
                    <AlertPopup />
                    <ScopedCssBaseline>
                        <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="nl" dateFormats={dateFormats}>
                            {children}
                        </LocalizationProvider>
                    </ScopedCssBaseline>
                </AlertProvider>
            </LoadingProvider>
        </ThemeProvider>
    );
};
function App() {
    const { user } = useContext(UserContext);
    return (
        <Providers>
            <BrowserRouter>
                <Routes>
                    <Route path={Paths.forbidden.path} element={<Forbidden />} />
                    <Route path="/" element={<RequiresLogin />}>
                        {user?.role === "Admin" ? (
                            <Route index element={<UserManagement />} />
                        ) : (
                            <Route
                                index
                                element={
                                    <>
                                        <ProjectProvider>
                                            <Projects />
                                        </ProjectProvider>
                                    </>
                                }
                            />
                        )}
                        <Route
                            path={Paths.projects.path}
                            element={
                                <ProjectProvider>
                                    <Projects />
                                </ProjectProvider>
                            }
                        />
                        <Route
                            path={Paths.projectsTable.path}
                            element={
                                <ProjectProvider>
                                    <Projects />
                                </ProjectProvider>
                            }
                        />
                        <Route
                            path={Paths.projectWizard.path}
                            element={
                                <ProjectProvider>
                                    <ProjectWizard />
                                </ProjectProvider>
                            }
                        />
                        <Route
                            path={Paths.projectWizardWithId.path}
                            element={
                                <ProjectProvider>
                                    <ProjectWizard />
                                </ProjectProvider>
                            }
                        />
                        <Route
                            path={Paths.projectWizardBlocks.path}
                            element={
                                <ProjectProvider>
                                    <HouseBlockProvider>
                                        <ProjectWizardBlocks />
                                    </HouseBlockProvider>
                                </ProjectProvider>
                            }
                        />
                        <Route
                            path={Paths.projectWizardMap.path}
                            element={
                                <ProjectProvider>
                                    <ProjectPlotSelector wizard={true} />
                                </ProjectProvider>
                            }
                        />
                        <Route
                            path={Paths.projectDetail.path}
                            element={
                                <ProjectProvider>
                                    <ProjectDetail>
                                        <HouseBlockProvider>
                                            <ProjectPlotSelector />
                                        </HouseBlockProvider>
                                    </ProjectDetail>
                                </ProjectProvider>
                            }
                        />
                        <Route
                            path={Paths.projectDetailCharacteristics.path}
                            element={
                                <ProjectProvider>
                                    <HouseBlockProvider>
                                        <ProjectDetail>
                                            <ProjectsWithHouseBlock />
                                        </ProjectDetail>
                                    </HouseBlockProvider>
                                </ProjectProvider>
                            }
                        />
                        <Route
                            path={Paths.projectDetailTimeline.path}
                            element={
                                <ProjectProvider>
                                    <ProjectDetail>
                                        <ProjectTimeline />
                                    </ProjectDetail>
                                </ProjectProvider>
                            }
                        />
                        <Route path={Paths.policygoal.path} element={<PolicyLists />} />
                        <Route path={Paths.policygoalDashboard.path} element={<PolicyLists />} />
                        <Route path={Paths.goals.path} element={<Goals />} />
                        <Route path={Paths.goalWizard.path} element={<GoalWizard />} />
                        <Route path={Paths.goalMenu.path} element={<GoalWizard />} />
                        <Route
                            path={Paths.dashboard.path}
                            element={
                                <ProjectProvider>
                                    <DashboardProjects />
                                </ProjectProvider>
                            }
                        />
                        <Route
                            path={Paths.createCustomDashboard.path}
                            element={
                                <ProjectProvider>
                                    <CreateCustomDashboard />
                                </ProjectProvider>
                            }
                        />
                        <Route
                            path={Paths.updateCustomDashboard.path}
                            element={
                                <ProjectProvider>
                                    <CreateCustomDashboard />
                                </ProjectProvider>
                            }
                        />
                        <Route path={Paths.customDashboardList.path} element={<CustomDashboardList />} />
                        <Route
                            path={Paths.dashboardProject.path}
                            element={
                                <ProjectProvider>
                                    <HouseBlockProvider>
                                        <DashboardProject />
                                    </HouseBlockProvider>
                                </ProjectProvider>
                            }
                        />
                        <Route path={Paths.exchangedata.path} element={<ExchangeData />} />
                        <Route path={Paths.exchangeimportdata.path} element={<ExchangeImportData />} />
                        <Route path={Paths.importExcel.path} element={<ImportPage functionality="excel" />} />
                        <Route path={Paths.importGeoJson.path} element={<ImportPage functionality="geojson" />} />
                        <Route path={Paths.importSquit.path} element={<ImportPage functionality="squit" />} />
                        <Route
                            path={Paths.exportExcel.path}
                            element={
                                <ProjectProvider>
                                    <ExportProject excelExport />
                                </ProjectProvider>
                            }
                        />
                        <Route
                            path={Paths.exportProvince.path}
                            element={
                                <ProjectProvider>
                                    <ExportProject excelExport={false} />
                                </ProjectProvider>
                            }
                        />
                        <Route
                            path={Paths.exportSettings.path}
                            element={
                                <ProjectProvider>
                                    <ExportSettings />
                                </ProjectProvider>
                            }
                        />
                        <Route
                            path={Paths.configuredExport.path + "/:id"}
                            element={
                                <ProjectProvider>
                                    <ExportWizard />
                                </ProjectProvider>
                            }
                        />
                        <Route path={Paths.userSettings.path} element={<Settings />} />
                        <Route path={Paths.userManagement.path} element={<UserManagement />} />
                        <Route path={Paths.priceCategories.path} element={<PriceCategories />} />
                        <Route path={Paths.importExcelProjects.path} element={<ImportedProjects type="Excel" />} />
                        <Route path={Paths.importSquitProjects.path} element={<ImportedProjects type="Squit" />} />
                        <Route path={Paths.createExportSettings.path} element={<ExportAdminPage />} />
                        <Route path={Paths.updateExportSettings.path} element={<ExportAdminPage />} />
                        <Route path={Paths.about.path} element={<About />} />
                        <Route path={Paths.swagger.path} element={<Swagger />} />
                        <Route path="*" element={<NoMatch />} />
                    </Route>
                </Routes>
            </BrowserRouter>
        </Providers>
    );
}

export default App;
