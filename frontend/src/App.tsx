import { BrowserRouter, Route, Routes, useNavigate } from "react-router-dom";
import { useEffect, useState, useContext } from "react";
import { ScopedCssBaseline, ThemeProvider } from "@mui/material";
import "./App.css";
import AlertContext from "./context/AlertContext";
import { AlertProvider } from "./context/AlertContext";
import AlertPopup from "./components/AlertPopup";
import { Layout } from "./components/Layout";
import { Projects } from "./pages/Projects";
import { CreateProject } from "./pages/CreateProject";
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

function RequiresLogin() {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const { setAlert } = useContext(AlertContext);
    const navigate = useNavigate();

    useEffect(() => {
        diwiFetch(Paths.loggedIn.path)
            .then((res) => {
                if (res.ok) {
                    setIsLoggedIn(true);
                }
            })
            .catch((error) => {
                setAlert(error.message, "error");
            });
    }, [setAlert, navigate]);

    return isLoggedIn ? <Layout /> : null;
}

const Providers = ({ children }: { children: React.ReactNode }) => {
    return (
        <ThemeProvider theme={theme}>
            <ConfigProvider>
                <AlertProvider>
                    <AlertPopup />
                    <ScopedCssBaseline>
                        <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="nl" dateFormats={dateFormats}>
                            {children}
                        </LocalizationProvider>
                    </ScopedCssBaseline>
                </AlertProvider>
            </ConfigProvider>
        </ThemeProvider>
    );
};
function App() {
    return (
        <Providers>
            <BrowserRouter>
                <Routes>
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
                                    <CreateProject />
                                </ProjectProvider>
                            }
                        />
                        <Route
                            path={Paths.projectWizardWithId.path}
                            element={
                                <ProjectProvider>
                                    <CreateProject />
                                </ProjectProvider>
                            }
                        />
                        <Route
                            path={Paths.projectWizardBlocks.path}
                            element={
                                <ProjectProvider>
                                    <CreateProject />
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
                                        <ProjectPlotSelector />
                                    </ProjectDetail>
                                </ProjectProvider>
                            }
                        />
                        <Route
                            path={Paths.projectDetailCharacteristics.path}
                            element={
                                <ProjectProvider>
                                    <ProjectDetail>
                                        <ProjectsWithHouseBlock />
                                    </ProjectDetail>
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
