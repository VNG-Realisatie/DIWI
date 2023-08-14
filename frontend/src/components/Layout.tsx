import { Box } from "@mui/material";
import { Outlet } from "react-router-dom";

export const Layout = () => {
  return (
    <>
      <Box>Header</Box>
      <Box>SideBar</Box>
      <Outlet />
      <Box>Footer</Box>
    </>
  );
};
