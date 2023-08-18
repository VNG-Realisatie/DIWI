import { BrowserRouter, Route, Routes } from "react-router-dom";
import { ScopedCssBaseline, ThemeProvider, createTheme } from "@mui/material";
import "./App.css";
import { AlertProvider } from "./context/AlertContext";
import AlertPopup from "./components/AlertPopup";
import { Layout } from "./components/Layout";
import { Home } from "./pages/Home";
import { Projects } from "./pages/Projects";
import { Dashboard } from "./pages/Dashboard";
import { NoMatch } from "./pages/NoMatch";
import * as Paths from "./Paths";
import { ProjectDetail } from "./pages/ProjectDetail";

export const drawerWidth = 290;
function App() {
  const theme = createTheme({
    typography: {
      fontFamily: "Inter, Arial",
      h6: {
        fontWeight: 600,
      },
      caption: {
        fontWeight: 500,
        fontSize: "13px",
        lineHeight: "120%",
      },
    },
    components: {
      MuiDrawer: {
        styleOverrides: {
          paper: {
            width: drawerWidth,
            backgroundColor: "#002C64",
            color: "#FFFFFF",
            boxSizing: "border-box",
          },
        },
      },
      MuiAppBar: {
        styleOverrides: {
          root: {
            backgroundColor: "#FFFFFF",
            position: "fixed",
          },
        },
      },
      MuiListItemIcon: {
        styleOverrides: {
          root: {
            color: "#FFFFFF",
            minWidth: "40px",
          },
        },
      },
    },
    palette: {
      primary: {
        // TODO add colors later
        main: "#002C64",
      },
      secondary: {
        main: "#900A0A",
      },
    },
  });
  return (
    <ThemeProvider theme={theme}>
      <AlertProvider>
        <AlertPopup />
        <ScopedCssBaseline>
          <BrowserRouter>
            <Routes>
              <Route path="/" element={<Layout />}>
                <Route index element={<Home />} />
                <Route path={Paths.projects.path} element={<Projects />} />
                <Route path={Paths.projectDetail.path} element={<ProjectDetail />} />
                <Route path={Paths.projectAdd.path} element={<Dashboard />} />
                <Route path="*" element={<NoMatch />} />
              </Route>
            </Routes>
          </BrowserRouter>
        </ScopedCssBaseline>
      </AlertProvider>
    </ThemeProvider>
  );
}

export default App;
