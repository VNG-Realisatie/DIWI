import { Box, Grid, Switch, Typography } from "@mui/material";
import { t } from "i18next";
import { WizardCard } from "../project-wizard/WizardCard";
import { LabelComponent } from "../project/LabelComponent";
import { CellContainer } from "../project/project-with-house-block/CellContainer";
import { Visibility } from "./DashboardCharts";
type Props = {
    demolitionAmount: number;
    constructionAmount: number;
    visibility: Visibility | undefined;
    handleToggle?: () => void;
};

export const MutationCard = ({ demolitionAmount, constructionAmount, visibility = undefined, handleToggle }: Props) => {
    return (
        <Grid item xs={12} m={{ xs: 0, md: 1 }}>
            <WizardCard>
                <Box display="flex" justifyContent="space-between" alignItems="center">
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.totalValues")}
                    </Typography>
                    {visibility && handleToggle && <Switch checked={visibility.MUTATION} onChange={handleToggle} inputProps={{ "aria-label": "controlled" }} />}
                </Box>
                <Grid container spacing={2} alignItems="stretch">
                    {/* Demolition */}
                    <Grid item xs={12} md={4}>
                        <LabelComponent required={false} text={t("createProject.houseBlocksForm.demolition")} tooltipInfoText={t("tooltipInfo.sloop.title")} />
                        <CellContainer>
                            <LabelComponent required={false} text={demolitionAmount.toString()} />
                        </CellContainer>
                    </Grid>
                    {/* Construction */}
                    <Grid item xs={12} md={4}>
                        <LabelComponent
                            required={false}
                            text={t("createProject.houseBlocksForm.grossPlanCapacity")}
                            tooltipInfoText={t("tooltipInfo.brutoPlancapaciteit.title")}
                        />
                        <CellContainer>
                            <LabelComponent required={false} text={constructionAmount.toString()} />
                        </CellContainer>
                    </Grid>
                    {/* Total */}
                    <Grid item xs={12} md={4}>
                        <LabelComponent
                            required={false}
                            text={t("createProject.houseBlocksForm.netPlanCapacity")}
                            tooltipInfoText={t("tooltipInfo.nettoPlancapaciteit.title")}
                        />
                        <CellContainer>
                            <LabelComponent required={false} text={(constructionAmount - demolitionAmount).toString()} />
                        </CellContainer>
                    </Grid>
                </Grid>
            </WizardCard>
        </Grid>
    );
};
