// AlDiTex
//
// Alignment Diagrams in LaTeX
//
// Copyright 2012 Richard Leggett
// richard.leggett@tgac.ac.uk
// 
// This is free software, supplied without warranty.

package al2tex;

import java.util.*;
import java.io.*;

public class Al2Tex {
    public static void main(String[] args) {
        DiagramOptions options = new DiagramOptions();

        System.out.println("AlDiTex\n");
        options.parseArgs(args);
    
        if ((options.getInputFormat().equals("pileup")) &&
            (options.getDiagramType().equals("coverage"))) {
            System.out.println("\nOpening pileup file");
            PileupFile pileupFile = new PileupFile(options.getInputFilename(), options.getListFilename());
            System.out.println("Building coverage diagram");
            PileupCoverageDiagram pileupCoverageDiagram = new PileupCoverageDiagram(options, pileupFile);
            System.out.println("Making bitmaps");
            pileupCoverageDiagram.makeBitmaps();
            
        } else if (options.getInputFormat().equals("psl")) {
            System.out.println("\nOpening PSL file");
            PSLFile pslFile = new PSLFile(options.getInputFilename());
            
            if ((options.getDiagramType().equals("coverage")) ||
                (options.getDiagramType().equals("all"))) {
                System.out.println("Building coverage diagram");
                PSLCoverageDiagram pslCoverageDiagram = new PSLCoverageDiagram(options);
                System.out.println("Making bitmaps");
                pslCoverageDiagram.makeBitmaps(pslFile, options.getOutputDirectory());
                System.out.println("Writing LaTeX files");
                pslCoverageDiagram.writeTexFile();
            }

            if ((options.getDiagramType().equals("alignment"))  ||
                (options.getDiagramType().equals("all"))) {
                System.out.println("Building alignment diagram");
                PSLAlignmentDiagram pslAlignmentDiagram = new PSLAlignmentDiagram(options, pslFile);
                System.out.println("Writing LaTeX files");
                pslAlignmentDiagram.writeTexFile(options.getOutputDirectory());
            }
        } else if (options.getInputFormat().equals("coords") || options.getInputFormat().equals("tiling")) {
            int type = 0;
            if (options.getInputFormat().equals("coords")) {
                System.out.println("\nOpening MUMmer show-coords file");
                type = MummerAlignment.SHOW_COORDS;
            } else if (options.getInputFormat().equals("tiling")) {
                System.out.println("\nOpening MUMmer show-tiling file");
                type = MummerAlignment.SHOW_TILING;
            }
            MummerFile alignmentFile = new MummerFile(options.getInputFilename(), type);
            if ((options.getDiagramType().equals("alignment"))  ||
                (options.getDiagramType().equals("all"))) {
                System.out.println("Building alignment diagram");
                MummerAlignmentDiagram nucmerAlignmentDiagram = new MummerAlignmentDiagram(options, alignmentFile);
                System.out.println("Writing LaTeX files");
                nucmerAlignmentDiagram.writeTexFile(options.getOutputDirectory(), options.getOutputDirectory()+"/"+File.separatorChar+options.getInputFilenameLeaf()+".tex");
            }            
        }
        System.out.println("Done");
    }
}
