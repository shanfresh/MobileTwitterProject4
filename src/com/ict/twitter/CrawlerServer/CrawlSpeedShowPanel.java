package com.ict.twitter.CrawlerServer;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;



public class CrawlSpeedShowPanel extends JPanel {

	/**
	 * @param args
	 */
	TimeSeries total;
	public CrawlSpeedShowPanel(){
		this.setLayout(new BorderLayout());
		this.total = new TimeSeries("总的采集速度", Millisecond.class);
		total.setMaximumItemAge(10000);
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(this.total);
		DateAxis domain = new DateAxis("Time");
		NumberAxis range = new NumberAxis("Memory");
		XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
		renderer.setSeriesPaint(0, Color.red);
		renderer.setSeriesStroke(0,(new BasicStroke(3f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL)));		
		XYPlot plot = new XYPlot(dataset, domain, range, renderer);

		JFreeChart chart = new JFreeChart("AjaxTwitter采集速度", new Font(
				"SansSerif", Font.BOLD, 24), plot, true);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(4, 4, 4, 4),
				BorderFactory.createLineBorder(Color.black)));
		add(chartPanel,BorderLayout.CENTER);
		
		
	}
	public void addTotalObservation(double y) {
		this.total.add(new Millisecond(), y);
	}
	public static void main(String[] args) {		
		JFrame frame = new JFrame("ShowCrawlSpeed");
		CrawlSpeedShowPanel panel = new CrawlSpeedShowPanel();
		panel.new DataGenerator(1000).start();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		frame.setBounds(300, 120, 800,600);
		frame.setVisible(true);		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}
	class DataGenerator extends Timer implements ActionListener{
		DataGenerator(int interval) {
			super(interval, null);
			addActionListener(this);
		}
		public void actionPerformed(ActionEvent event) {
			long f = Runtime.getRuntime().freeMemory();
			long t = 1000;
			addTotalObservation(t);
		}
	}

}
