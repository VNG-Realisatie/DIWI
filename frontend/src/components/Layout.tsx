import * as React from "react";
import { styled } from "@mui/material/styles";
import Box from "@mui/material/Box";
import CssBaseline from "@mui/material/CssBaseline";

import {  Outlet } from "react-router-dom";
import { Footer } from "./Footer";
import { Header } from "./Header";
import { SideBar } from "./Sidebar";

export const Layout = () => {
  const [open, setOpen] = React.useState(false);

  const handleDrawerOpen = () => {
    setOpen(true);
  };

  const handleDrawerClose = () => {
    setOpen(false);
  };

  return (
    <>
      <Box >
        <CssBaseline />
        <Header open={open} handleDrawerOpen={handleDrawerOpen} />
        <SideBar open={open} handleDrawerClose={handleDrawerClose}/>
        <Box minHeight="90px"/>
          <Box width="65%" m="auto">
          <Outlet />
          </Box>
      </Box>
      <Footer />
    </>
  );
};
