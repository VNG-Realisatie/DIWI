import { BrowserRouter,  Route, Routes } from "react-router-dom";
import {ScopedCssBaseline, ThemeProvider, createTheme} from "@mui/material";
import './App.css';
import { AlertProvider } from "./context/AlertContext";
import AlertPopup from "./components/AlertPopup";
import { Layout } from "./components/Layout";
import { Home } from "./pages/Home";
import { About } from "./pages/About";
import { Dashboard } from "./pages/Dashboard";
import { NoMatch } from "./pages/NoMatch";

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
        MuiBottomNavigation: {
            defaultProps: {
                style: {
                    backgroundColor: "#F40009",
                },
            },
        },
        MuiBottomNavigationAction: {
            defaultProps: {
                style: {
                    color: "#FFFFFF",
                    fontWeight: "bolder",
                },
            },
        },
    },
    palette: {
        primary: {
          // TODO add colors later
            main: "#F40009",
        },
        secondary: {
            main: "#212121",
        },
    },
});
  return (
    <ThemeProvider theme={theme}>
      <AlertProvider>
      <AlertPopup />
      <ScopedCssBaseline>
      <BrowserRouter>
      <BrowserRouter>
                                <Routes>
                                    <Routes>
                                        <Route path="/" element={<Layout />}>
                                            <Route index element={<Home />} />
                                            <Route path="about" element={<About />} />
                                            <Route path="dashboard" element={<Dashboard />} />

                                            {/* Using path="*"" means "match anything", so this route
                acts like a catch-all for URLs that we don't have explicit
                routes for. */}
                                            <Route path="*" element={<NoMatch />} />
                                        </Route>
                                    </Routes>
                                </Routes>
                            </BrowserRouter>
      </BrowserRouter>
      </ScopedCssBaseline>
      </AlertProvider>
      
    </ThemeProvider>
  );
}

export default App;
