import React from "react";
import { Stack, Typography, Box } from "@mui/material";
import { Link, useLocation } from "react-router-dom";
import Search from "../Search";

interface titleLink {
    title: string;
    link: string;
}

interface BreadcrumbBarProps {
    pageTitle: string;
    links?: titleLink[];
}

const BreadcrumbBar: React.FC<BreadcrumbBarProps> = ({ pageTitle, links }) => {
    const location = useLocation();
    return (
        <Stack direction="row" justifyContent="flex-start" alignItems="flex-start">
            <Box width="25%">
                <Search label="Zoeken..." searchList={[]} isDetailSearch={false} />
            </Box>
            <Stack width="75%" direction="row" alignItems="center" justifyContent="space-between" sx={{ backgroundColor: "#002C64", color: "#FFFFFF" }} p={1}>
                <Stack direction="row">
                    {pageTitle}:{" "}
                    {links?.map((object, index) => (
                        <Typography key={object.title} ml={1}>
                            {index > 0 && <span>/ </span>}
                            {object.link ? (
                                <Link
                                    to={object.link}
                                    style={{ color: location.pathname !== object.link ? "#FFFFFF" : "#0288d1", textDecoration: "none", display: "inline" }}
                                >
                                    {object.title}
                                </Link>
                            ) : (
                                <span>{object.title}</span>
                            )}
                        </Typography>
                    ))}
                </Stack>
            </Stack>
        </Stack>
    );
};

export default BreadcrumbBar;
