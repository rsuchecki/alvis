package al2tex;

import java.io.*;
import java.util.*;
import java.awt.image.*;
import javax.swing.*;
import java.awt.*;
import javax.imageio.*;
import java.lang.*;

public class PileupImage {
    private static final int MAX_COVERAGE=500;
    private BufferedImage bImage = null;
    private int rowHeight = 8;
    private int chromosomeGap = 48;
    private int heatMapHeight = 64;
    private int imageWidth = 3000;
    private int labelWidth = imageWidth / 12;
    private int width = imageWidth - labelWidth-16;
    private int heatMapWidth = imageWidth / 4;
    private int heatMapX = imageWidth - (imageWidth/4) - (imageWidth/16);
    private int heatMapY = heatMapHeight;
    private int barOffset = labelWidth;
    private int imageHeight = 0;
    private int nContigs = 0;
    private int chrStart = 0;
    private double pixelMultiplier;
    private int unitWidth;
    private int y = heatMapHeight + heatMapHeight + chromosomeGap + (2 * rowHeight);
    private HeatMapScale heatMap;
    private DiagramOptions options;
    
    public PileupImage(int n, int nc, int hp, int hc, DiagramOptions o) {
        options = o;
        int maxCoverage = options.getMaxCoverage();
        int heatMapSize = hc;
        
        if (maxCoverage > 0) {
            heatMapSize = hc > maxCoverage ? maxCoverage:hc;
        }
        
        heatMap = new HeatMapScale(heatMapSize);
        imageHeight = (2*heatMapHeight) + (n*rowHeight) + (nc * chromosomeGap) + chromosomeGap;
        pixelMultiplier = (double)width / (double)hp;
        unitWidth = (int)(Math.ceil(pixelMultiplier));
        
        storeStart();
        
        bImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        
        if (bImage == null) {
            System.out.println("ERROR: Couldn't create image\n");
            System.exit(0);
        }
        
        Graphics g = bImage.getGraphics();
        g.setColor(new Color(255, 255, 255));
        g.fillRect(0, 0, imageWidth, imageHeight);        
        g.setColor(Color.BLACK);
        
        heatMap.plotHeatMap(bImage, heatMapX, heatMapY, heatMapWidth, heatMapHeight, options.getMaxCoverage() > 0 ? true:false);
    }
    
    public void addContig(PileupContig contig) {
        Graphics g = bImage.getGraphics();
        pixelMultiplier = (double)width / (double)contig.getSize();
        unitWidth = (int)(Math.ceil(pixelMultiplier));
        
        if (options.isNewHeatMapForEachContig()) {
            int hc = contig.getHighestCoverage();
            int maxCoverage = options.getMaxCoverage();
            int heatMapSize = hc;
            if (maxCoverage > 0) {
                heatMapSize = hc > maxCoverage ? maxCoverage:hc;
            }
            heatMap = new HeatMapScale(heatMapSize);
        }
        
        for (int i=0; i<contig.getSize(); i++) {
            int xStart = barOffset + (int)((double)i * pixelMultiplier);
            int xEnd = xStart + unitWidth;
            g.setColor(heatMap.getColour(contig.getCoverage(i)));
            g.fillRect(xStart, y, unitWidth, rowHeight - 2);
        }
        nContigs++;
        y+=rowHeight;
    }
    
    public void saveImageFile(String filename) {
        try {
            ImageIO.write(bImage, "PNG", new File(filename));
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }   
    
    public HeatMapScale getHeatMap() {
        return heatMap;
    }
    
    public void addChromosomeBreak() {
        y+=chromosomeGap;
    }
    
    public void storeStart() {
        chrStart = y;
    }
    
    public void labelSet(String label) {
        int middle = chrStart + ((y - chrStart)/2);
        Graphics g = bImage.getGraphics();
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 64));
        g.drawString(label, 16, middle+40);
    }
}
