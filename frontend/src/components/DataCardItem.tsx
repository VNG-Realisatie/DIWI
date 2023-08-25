import { Button, Stack } from "@mui/material";
import { useNavigate } from "react-router-dom";
import CloudUploadIcon from "@mui/icons-material/CloudUpload";
import CloudDownloadIcon from "@mui/icons-material/CloudDownload";

type Props = {
  text: string;
  link: string;
  icon: React.FunctionComponent<React.SVGProps<SVGSVGElement>>;
  isImport?: boolean;
};
export const DataCardItem: React.FC<Props> = ({
  icon: Icon,
  text,
  link,
  isImport,
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
        <Icon />
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
