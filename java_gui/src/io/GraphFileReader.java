package io;

import GUI_Project.model.Edge;
import GUI_Project.model.Node;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GraphFileReader {

    static public List<Node> readNodes (String filePath) throws Exception {
        List<Node> nodes = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader(filePath))) {
        String firstLine = br.readLine();

        if(firstLine == null || firstLine.trim().isEmpty()){
            return nodes;
        }
            int numberOfNodes;
        try {
            numberOfNodes = Integer.parseInt(firstLine.trim());
        } catch (NumberFormatException e) {
            throw new Exception("Format of the text file is corrupted");
        }

            for (int i = 0; i < numberOfNodes; i++) {
                String line = br.readLine();

                if (line == null) {
                    break;
                }

                line = line.trim();


                if (line.isEmpty()) {
                    i--;
                    continue;
                }

                String[] parts = line.split("\\s+");

                if (parts.length >= 3) {
                    try {
                        int id = Integer.parseInt(parts[0]);
                        double x = Double.parseDouble(parts[1]);
                        double y = Double.parseDouble(parts[2]);
                        nodes.add(new Node(id, x, y));
                    } catch (NumberFormatException e) {
                        i--;
                    }
                } else {
                    i--;
                }
            }
        }
        return nodes;
    }

    static public List<Node> readNodesFromBinary(String filePath) throws Exception {
        List<Node> nodes = new ArrayList<>();

        try(DataInputStream dis = new DataInputStream(new FileInputStream(filePath))) {

            int numberOfNodes;
            try {
                numberOfNodes = Integer.reverseBytes(dis.readInt());
            } catch (EOFException e) {
                throw new Exception("Binary file is corrupted: there is no header with nodes' number");
            }

            for(int i = 0; i < numberOfNodes; i++) {

                try {
                    int id = Integer.reverseBytes(dis.readInt());

                    long xBytes = Long.reverseBytes(dis.readLong());
                    double x = Double.longBitsToDouble(xBytes);

                    long yBytes = Long.reverseBytes(dis.readLong());
                    double y = Double.longBitsToDouble(yBytes);

                    nodes.add(new Node(id, x, y));
                } catch (EOFException e) {
                    throw new Exception ("Binary file is corrupted: Unexpected End of File");
                }
            }
        }
        return nodes;
    }

    static public List<Edge> readEdges (String filePath) throws Exception {
        List<Edge> edges = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while((line = br.readLine()) != null) {
                line = line.trim();
                if(line.isEmpty()) continue;

                String parts[] = line.split("\\s+");

                if(parts.length >= 4) {
                    try {
                        String edgeName = parts[0];
                        int sourceNodeId = Integer.parseInt(parts[1]);
                        int targetNodeId = Integer.parseInt(parts[2]);
                        double weight = Double.parseDouble(parts[3]);

                        edges.add(new Edge(edgeName, sourceNodeId, targetNodeId, weight));
                    } catch (NumberFormatException e) {

                    }
                }
            }
        }
        return edges;
    }
}
