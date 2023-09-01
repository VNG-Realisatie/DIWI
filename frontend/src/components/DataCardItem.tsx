import { Button, Stack } from "@mui/material";
import { useNavigate } from "react-router-dom";
import CloudUploadIcon from "@mui/icons-material/CloudUpload";
import CloudDownloadIcon from "@mui/icons-material/CloudDownload";
import { ReactNode } from "react";

type Props = {
  text: string;
  link: string;
  isImport?: boolean;
  children?: ReactNode;
};
export const DataCardItem: React.FC<Props> = ({
  text,
  link,
  isImport,
  children
}) => {
  const navigate = useNavigate();
  return (
    <Stack
      border="solid 1px #ddd"
      width="200px"
      height="200px"
      direction="column"
      alignItems="center"
      display="flex"
      borderRadius="0px 0px 3px 3px"
      sx={{ cursor: "pointer" }}
      onClick={() => navigate(link)}
    >
      <Stack mt={3}>
        {children}
      </Stack>
      <Button
        fullWidth
        variant="contained"
        component="label"
        color="primary"
        startIcon={!isImport ? <CloudDownloadIcon /> : <CloudUploadIcon />}
        sx={{ marginTop: "auto" }}
      >
        {text}
      </Button>
    </Stack>
  );
};
