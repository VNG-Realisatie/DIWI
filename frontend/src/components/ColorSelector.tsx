import React, { useState } from "react";
import { ChromePicker, ColorResult } from "react-color";
import { Box, Button, Popover } from "@mui/material";

interface ColorSelectorProps {
  defaultColor: string;
  onColorChange: (color: string) => void;
  selectedColor: any;
}

const ColorSelector: React.FC<ColorSelectorProps> = ({
  defaultColor,
  onColorChange,
  selectedColor,
}) => {
  const [color, setColor] = useState<string>(defaultColor);
  const [anchorEl, setAnchorEl] = useState<HTMLButtonElement | null>(null);

  const handleColorChange = (newColor: ColorResult) => {
    const newColorString = `rgba(${newColor.rgb.r}, ${newColor.rgb.g}, ${newColor.rgb.b}, ${newColor.rgb.a})`;
    setColor(newColorString);
    if (onColorChange) {
      onColorChange(newColorString);
    }
  };

  const handleButtonClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const open = Boolean(anchorEl);

  return (
    <div>
      <Button onClick={handleButtonClick}>
        {" "}
        <Box
          sx={{
            width: "30px",
            height: "30px",
            backgroundColor:
              selectedColor && selectedColor.color
                ? selectedColor.color
                : "blue",
            borderRadius: "5px",
          }}
          mr={1}
        />
      </Button>
      <Popover
        open={open}
        anchorEl={anchorEl}
        onClose={handleClose}
        anchorOrigin={{
          vertical: "bottom",
          horizontal: "left",
        }}
      >
        <ChromePicker color={color} onChange={handleColorChange} />
      </Popover>
    </div>
  );
};

export default ColorSelector;
