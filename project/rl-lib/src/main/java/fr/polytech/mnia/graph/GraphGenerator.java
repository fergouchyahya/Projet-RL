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
 * GraphGenerator.java
 *
 * Classe utilitaire pour générer et sauvegarder automatiquement des graphes
 * illustrant les résultats d'entraînement d'un agent.
 *
 * Fonctionnalités :
 * - Générer des graphes de récompense cumulée et moyenne
 * - Générer un histogramme de la distribution des actions choisies
 * - Gérer automatiquement le dossier d'enregistrement
 * 
 * Utilise la bibliothèque JFreeChart.
 * 
 * Exemple d'utilisation :
 * GraphGenerator.createLineChartRewards(rewards, "MyAgent");
 * GraphGenerator.createHistogramActions(actions, "MyAgent");
 */
public class GraphGenerator {

    private static final String OUTPUT_DIR = "graphs/"; // Dossier pour enregistrer les graphes
    private static boolean cleaned = false; // Pour ne nettoyer qu'une seule fois par exécution

    /**
     * Nettoie le dossier "graphs/" en supprimant tous les anciens fichiers PNG.
     *
     * Exemple :
     * GraphGenerator.clearGraphsFolder();
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
     * Crée deux graphes (récompense cumulée et moyenne) à partir de la liste des
     * récompenses.
     *
     * @param rewards   liste des récompenses obtenues étape par étape
     * @param agentName nom de l'agent utilisé pour nommer les fichiers
     *
     *                  Exemple :
     *                  GraphGenerator.createLineChartRewards(rewards,
     *                  "BanditAgent");
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

        // Graphe de la récompense cumulée
        JFreeChart chartCumulative = ChartFactory.createXYLineChart(
                "Cumulative Reward - " + agentName,
                "Step",
                "Cumulative Reward",
                datasetCumulative,
                PlotOrientation.VERTICAL,
                false, true, false);
        saveChart(chartCumulative, agentName + "_cumulative");

        // Graphe de la récompense moyenne
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
     * Crée un histogramme représentant la distribution des actions choisies.
     *
     * @param actions   liste des actions exécutées
     * @param agentName nom de l'agent utilisé pour nommer le fichier
     *
     *                  Exemple :
     *                  GraphGenerator.createHistogramActions(actions, "UCBAgent");
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
     * Sauvegarde un graphique sous format PNG dans le dossier "graphs/".
     *
     * @param chart graphique JFreeChart à sauvegarder
     * @param name  nom de base du fichier sans extension
     *
     *              Exemple :
     *              GraphGenerator.saveChart(monChart, "rewards_graph");
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
     * Vérifie si le dossier "graphs/" doit être nettoyé avant de sauvegarder de
     * nouveaux fichiers.
     * Cette opération est faite une seule fois par exécution.
     *
     * Exemple :
     * GraphGenerator.checkAndClean();
     */
    private static void checkAndClean() throws Exception {
        if (!cleaned) {
            clearGraphsFolder();
        }
    }
}
