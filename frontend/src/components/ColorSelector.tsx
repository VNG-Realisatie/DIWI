import { FC, MouseEvent, useRef, useState } from "react";
import { BlockPicker, ColorResult } from "react-color";
import { Box, Button, Popover } from "@mui/material";

interface ColorSelectorProps {
    defaultColor: string;
    onColorChange: (color: string) => void;
    selectedColor: string | undefined;
    disabled?: boolean;
    width?: string;
}

export const defaultColors = ["#FFE3DC", "#AEBD93", "#FFE066", "#49DCB1", "#94D1BE", "#DE2130", "#8B2635", "#976880", "#F18F01", "#768948"];

const ColorSelector: FC<ColorSelectorProps> = ({ defaultColor, onColorChange, selectedColor, disabled = false, width }) => {
    const boxRef = useRef<HTMLElement>(null);
    const [color, setColor] = useState<string | undefined>(defaultColor);
    const [anchorEl, setAnchorEl] = useState<HTMLButtonElement | null>(null);

    const handleColorChange = (newColor: ColorResult) => {
        const newColorString = newColor.hex;
        setColor(newColorString);
        if (onColorChange) {
            onColorChange(newColorString);
        }
    };

    const handleButtonClick = (event: MouseEvent<HTMLButtonElement>) => {
        if (!disabled) setAnchorEl(event.currentTarget);
    };

    const handleClose = () => {
        setAnchorEl(null);
    };

    const open = Boolean(anchorEl);
    const defaultSize = "40px";

    return (
        <Box ref={boxRef} width={width ?? defaultSize}>
            <Button onClick={handleButtonClick} fullWidth disabled={disabled} sx={{ padding: 0 }}>
                <Box
                    width={"100%"}
                    height={defaultSize}
                    borderRadius={"5px"}
                    sx={{
                        backgroundColor: selectedColor ? selectedColor : color,
                    }}
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
                <Box width={boxRef.current?.offsetWidth ?? "auto"}>
                    <BlockPicker width={width} colors={defaultColors} color={selectedColor ? selectedColor : color} onChange={handleColorChange} />
                </Box>
            </Popover>
        </Box>
    );
};

export default ColorSelector;
