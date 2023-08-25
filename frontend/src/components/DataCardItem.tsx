import { Button, CircularProgress, Stack, Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";
import CloudUploadIcon from "@mui/icons-material/CloudUpload";
import { useState } from "react";

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
  const [uploading, setUploading] = useState(false);
  const navigate = useNavigate();
  return (
    <Stack
      border="solid 1px #ddd"
      width="200px"
      height="200px"
      direction="column"
      alignItems="center"
      display="flex"
      sx={{ cursor: "pointer" }}
      onClick={() => navigate(link)}
    >
      <Stack mt={3}>
        <Icon />
      </Stack>
      {!isImport && (
        <Typography
          sx={{
            marginTop: "auto",
            p: 1,
            backgroundColor: "#002C64",
            color: "#FFFFFF",
            width: "100%",
            textAlign: "center",
          }}
        >
          {text}
        </Typography>
      )}
      {isImport && (
        <Button
          fullWidth
          variant="contained"
          component="label"
          color="primary"
          startIcon={<CloudUploadIcon />}
          sx={{ marginTop: "auto" }}
        >
          {text}
          {uploading && <CircularProgress sx={{ color: "white" }} size={16} />}
          <input hidden type="file" onChange={(e) => {
           setUploading(true)
           //ToDo Add upload endpoint
           console.log(e)
           setTimeout(()=>setUploading(false), 3000);
          }} />
        </Button>
      )}
    </Stack>
  );
};
