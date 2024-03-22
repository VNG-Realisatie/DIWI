import { Grid, Typography } from "@mui/material";
import { columnTitleStyle } from "./ImportProjectCardItem";

export const ImportHouseBlockCardItem = (props: any) => {
    const { hb } = props;

    return (
        <>
            <Grid
                item
                sm={12}
                sx={{
                    backgroundColor: "#00A9F3",
                    color: "#FFFFFF",
                    p: 1,
                }}
                display="flex"
                justifyContent="space-between"
            >
                {hb.name}
            </Grid>
            <Grid item sm={2}>
                <Typography sx={columnTitleStyle}>StartDatum</Typography>

                <Typography
                    sx={{
                        border: "solid 1px #ddd",
                        p: 0.5,
                    }}
                >
                    {hb.startdate}
                </Typography>
            </Grid>
            <Grid item sm={2}>
                <Typography sx={columnTitleStyle}>EindDatum</Typography>

                <Typography
                    sx={{
                        border: "solid 1px #ddd",
                        p: 0.5,
                    }}
                >
                    {hb.finishDate}
                </Typography>
            </Grid>
            <Grid item sm={2}>
                <Typography sx={columnTitleStyle}>Mutatiesoort</Typography>

                <Typography
                    sx={{
                        border: "solid 1px #ddd",
                        p: 0.5,
                    }}
                >
                    {hb.mutationType}
                </Typography>
            </Grid>
            <Grid item sm={2}>
                <Typography sx={columnTitleStyle}>Bruto plancapaciteit</Typography>

                <Typography
                    sx={{
                        border: "solid 1px #ddd",
                        p: 0.5,
                    }}
                >
                    {hb.grossPlanCapacity}
                </Typography>
            </Grid>
            <Grid item sm={2}>
                <Typography sx={columnTitleStyle}>Sloop</Typography>

                <Typography
                    sx={{
                        border: "solid 1px #ddd",
                        p: 0.5,
                    }}
                >
                    {hb.demolition}
                </Typography>
            </Grid>
            <Grid item sm={2}>
                <Typography sx={columnTitleStyle}>Netto Plancapaciteit</Typography>

                <Typography
                    sx={{
                        border: "solid 1px #ddd",
                        p: 0.5,
                    }}
                >
                    {hb.netPlanCapacity}
                </Typography>
            </Grid>
            <Grid item sm={2}>
                <Typography sx={columnTitleStyle}>Grootte</Typography>

                <Typography
                    sx={{
                        border: "solid 1px #ddd",
                        p: 0.5,
                    }}
                >
                    {hb.size}
                </Typography>
            </Grid>
            <Grid item sm={2}>
                <Typography sx={columnTitleStyle}>Koopwoning</Typography>

                <Typography
                    sx={{
                        border: "solid 1px #ddd",
                        p: 0.5,
                    }}
                >
                    {hb.ownerOccupied}
                </Typography>
            </Grid>
            <Grid item sm={3}>
                <Typography sx={columnTitleStyle}>Huurwoning Particuliere verhuurder</Typography>

                <Typography
                    sx={{
                        border: "solid 1px #ddd",
                        p: 0.5,
                    }}
                >
                    {hb.rentalPrivateHouse}
                </Typography>
            </Grid>
            <Grid item sm={3}>
                <Typography sx={columnTitleStyle}>Huurwoning Woningcorporatie</Typography>

                <Typography
                    sx={{
                        border: "solid 1px #ddd",
                        p: 0.5,
                    }}
                >
                    {hb.rentalHousingCompany}
                </Typography>
            </Grid>
            <Grid item sm={2}>
                <Typography sx={columnTitleStyle}>Waarde</Typography>

                <Typography
                    sx={{
                        border: "solid 1px #ddd",
                        p: 0.5,
                    }}
                >
                    {hb.price}
                </Typography>
            </Grid>
            <Grid item sm={2}>
                <Typography sx={columnTitleStyle}>Huurbedrag</Typography>

                <Typography
                    sx={{
                        border: "solid 1px #ddd",
                        p: 0.5,
                    }}
                >
                    {hb.rentingPrice}
                </Typography>
            </Grid>
            <Grid item sm={2} mb={6}>
                <Typography sx={columnTitleStyle}>Woningtype:</Typography>

                <Typography
                    sx={{
                        border: "solid 1px #ddd",
                        p: 0.5,
                    }}
                >
                    {hb.houseType}
                </Typography>
            </Grid>
        </>
    );
};
