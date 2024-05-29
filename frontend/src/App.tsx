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
import { ExportProject } from "./pages/ExportProject";
import { ImportExcel } from "./pages/ImportExcel";
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
import { ProjectWizardMap } from "./pages/ProjectWizardMap";
import "dayjs/locale/nl";
import { HouseBlockProvider } from "./context/HouseBlockContext";
import ProjectWizard from "./pages/ProjectWizard";
import ProjectWizardBlocks from "./pages/ProjectWizardBlocks";
import { LoadingProvider } from "./context/LoadingContext";
import { ImportGeoJson } from "./pages/ImportGeoJson";
import { Forbidden } from "./pages/Forbidden";

type LoginStatus = "UNKNOWN" | "AUTHORIZED" | "FORBIDDEN";

function RequiresLogin() {
    const [loginStatus, setLoginStatus] = useState<LoginStatus>("UNKNOWN");
    const { setAlert } = useContext(AlertContext);

    useEffect(() => {
        diwiFetch(Paths.loggedIn.path)
            .then((res) => {
                console.log("diwifetchres", res);
                if (res.ok) {
                    setLoginStatus("AUTHORIZED");
                } else if (res.status === 401) {
                    // UNAUTHORIZED
                    setLoginStatus("UNKNOWN");
                } else if (res.status === 403) {
                    // FORBIDDEN
                    setLoginStatus("FORBIDDEN");
                }
            })
            .catch((error) => {
                console.log("diwifetcherror", error.message);
                setAlert(error.message, "error");
                setLoginStatus("UNKNOWN");
            });
    }, [setAlert]);

    if (loginStatus === "FORBIDDEN") {
        return <Forbidden />;
    }
    if (loginStatus === "AUTHORIZED") {
        return (
            <ConfigProvider>
                {/* configprovider does a fetch, so first check login for this specific one */}
                <Layout />
            </ConfigProvider>
        );
    }
    // UNKNOWN = default just returns null so page stays empty and we can redirect
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
    return (
        <Providers>
            <BrowserRouter>
                <Routes>
                    <Route path={Paths.forbidden.path} element={<Forbidden />} />
                    <Route path="/" element={<RequiresLogin />}>
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
                                    <ProjectWizardMap />
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
                        <Route path={Paths.dashboard.path} element={<DashboardProjects />} />
                        <Route path={Paths.exchangedata.path} element={<ExchangeData />} />
                        <Route path={Paths.importExcel.path} element={<ImportExcel excelImport />} />
                        <Route path={Paths.importGeoJson.path} element={<ImportGeoJson />} />
                        <Route path={Paths.importSquit.path} element={<ImportExcel excelImport={false} />} />
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
                        <Route path={Paths.userSettings.path} element={<Settings />} />
                        <Route path={Paths.importExcelProjects.path} element={<ImportedProjects type="Excel" />} />
                        <Route path={Paths.importSquitProjects.path} element={<ImportedProjects type="Squit" />} />
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
