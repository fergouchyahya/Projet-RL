package fr.polytech.mnia.graph;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Classe utilitaire pour générer et sauvegarder des graphes :
 * - Récompense cumulée
 * - Récompense moyenne
 * - Distribution des actions
 * Utilise la bibliothèque JFreeChart.
 */
public class GraphGenerator {

    private static final String OUTPUT_DIR = "graphs/"; // Dossier pour enregistrer les graphes
    private static boolean cleaned = false; // Pour ne nettoyer qu'une seule fois par exécution

    /**
     * Nettoie le dossier graphs/ en supprimant tous les anciens fichiers.
     */
    public static void clearGraphsFolder() throws Exception {
        File dir = new File(OUTPUT_DIR);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isFile()) {
                        f.delete();
                    }
                }
            }
        } else {
            dir.mkdirs();
        }
        System.out.println("[Graph] Dossier de graphes nettoyé.");
        cleaned = true;
    }

    /**
     * Crée deux graphes :
     * - Récompense cumulée
     * - Récompense moyenne
     * 
     * @param rewards   Liste des récompenses au fil du temps
     * @param agentName Nom de l'agent (pour nommer les fichiers)
     */
    public static void createLineChartRewards(List<Double> rewards, String agentName) throws Exception {
        checkAndClean();

        XYSeries seriesCumulative = new XYSeries("Cumulative Reward");
        XYSeries seriesAverage = new XYSeries("Average Reward");

        double cumulative = 0.0;
        for (int i = 0; i < rewards.size(); i++) {
            cumulative += rewards.get(i);
            seriesCumulative.add(i, cumulative);
            seriesAverage.add(i, cumulative / (i + 1));
        }

        XYSeriesCollection datasetCumulative = new XYSeriesCollection(seriesCumulative);
        XYSeriesCollection datasetAverage = new XYSeriesCollection(seriesAverage);

        // Graphe récompense cumulée
        JFreeChart chartCumulative = ChartFactory.createXYLineChart(
                "Cumulative Reward - " + agentName,
                "Step",
                "Cumulative Reward",
                datasetCumulative,
                PlotOrientation.VERTICAL,
                false, true, false);
        saveChart(chartCumulative, agentName + "_cumulative");

        // Graphe récompense moyenne
        JFreeChart chartAverage = ChartFactory.createXYLineChart(
                "Average Reward - " + agentName,
                "Step",
                "Average Reward",
                datasetAverage,
                PlotOrientation.VERTICAL,
                false, true, false);
        saveChart(chartAverage, agentName + "_average");
    }

    /**
     * Crée un histogramme de la distribution des actions choisies.
     * 
     * @param actions   Liste des actions prises pendant l'entraînement
     * @param agentName Nom de l'agent
     */
    public static void createHistogramActions(List<String> actions, String agentName) throws Exception {
        checkAndClean();

        Map<String, Integer> counts = new HashMap<>();
        for (String action : actions) {
            counts.put(action, counts.getOrDefault(action, 0) + 1);
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            dataset.addValue(entry.getValue(), "Actions", entry.getKey());
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Actions Distribution - " + agentName,
                "Action",
                "Count",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);
        saveChart(barChart, agentName + "_actions");
    }

    /**
     * Sauvegarde un graphique au format PNG dans le dossier graphs/.
     * 
     * @param chart Le graphique à sauvegarder
     * @param name  Le nom de base du fichier
     */
    private static void saveChart(JFreeChart chart, String name) throws Exception {
        File dir = new File(OUTPUT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = OUTPUT_DIR + name + "_" + timestamp + ".png";

        ChartUtils.saveChartAsPNG(new File(filename), chart, 800, 600);
        System.out.println("[Graph] Saved : " + filename);
    }

    /**
     * Vérifie si le dossier doit être nettoyé avant de sauvegarder.
     */
    private static void checkAndClean() throws Exception {
        if (!cleaned) {
            clearGraphsFolder();
        }
    }
}
