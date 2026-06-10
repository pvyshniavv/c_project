package GUI_Project.io;

import GUI_Project.model.Edge;
import GUI_Project.model.Node;
import GUI_Project.model.OutputGraph;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads the file formats used by the project:
 * <ul>
 *   <li><b>Edges file</b> (text). One edge per line:
 *       <pre>name source target weight</pre>
 *       Example:
 *       <pre>AB 1 2 1
 *DB 4 2 1.407</pre>
 *   </li>
 *   <li><b>Coordinates file</b> (text). One node per line:
 *       <pre>id x y</pre>
 *   </li>
 *   <li><b>Binary graph file</b> (output of the C module). A single file
 *       holding the whole planar graph, in little-endian byte order, with
 *       NO struct padding:
 *       <pre>
 *  int32  nodeCount
 *  int32  edgeCount
 *  per node : int32 id, double x, double y       (20 bytes)
 *  per edge : int32 source, int32 target, double weight  (16 bytes)
 *       </pre>
 *       Edge names are not stored — synthetic names "source-target" are
 *       generated on read.
 *   </li>
 * </ul>
 * Blank lines and lines that start with {@code #} are skipped in text inputs.
 */
public class GraphFileReader {

    /** Bytes per node in the binary format. */
    private static final int BINARY_NODE_SIZE = Integer.BYTES + 2 * Double.BYTES;
    /** Bytes per edge in the binary format. */
    private static final int BINARY_EDGE_SIZE = 2 * Integer.BYTES + Double.BYTES;
    /** Bytes in the header. */
    private static final int BINARY_HEADER_SIZE = 2 * Integer.BYTES;

    //text edges

    /**
     * Reads an edges file ({@code name source target weight} per line).
     *
     * @throws IOException            on disk access problems or malformed lines
     * @throws NumberFormatException  when a numeric token contains letters
     */
    public List<Edge> readEdges(Path path) throws IOException {
        List<Edge> edges = new ArrayList<>();
        int lineNumber = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                String[] parts = trimmed.split("\\s+");
                if (parts.length < 4) {
                    throw new IOException("Line " + lineNumber
                            + ": expected 'name source target weight' but got: \"" + line + "\"");
                }
                String name = parts[0];
                int source = Integer.parseInt(parts[1]);
                int target = Integer.parseInt(parts[2]);
                double weight = Double.parseDouble(parts[3]);
                edges.add(new Edge(source, target, name, weight));
            }
        }
        return edges;
    }

    // -- text coordinates -------------------------------------------------

    /**
     * Reads a text coordinates file ({@code id x y} per line).
     *
     * @throws IOException            on disk access problems
     * @throws NumberFormatException  when a numeric token contains letters
     */
    public List<Node> readCoordinatesText(Path path) throws IOException {
        List<Node> nodes = new ArrayList<>();
        int lineNumber = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                String[] parts = trimmed.split("\\s+");
                if (parts.length < 3) {
                    throw new IOException("Line " + lineNumber
                            + ": expected 'id x y' but got: \"" + line + "\"");
                }
                int id = Integer.parseInt(parts[0]);
                double x = Double.parseDouble(parts[1]);
                double y = Double.parseDouble(parts[2]);
                nodes.add(new Node(id, x, y));
            }
        }
        return nodes;
    }

    // -- binary full-graph ------------------------------------------------

    /**
     * Reads a complete planar graph (nodes + edges) from a binary file
     * produced by the C module. Format is documented in the class JavaDoc.
     *
     * @throws EOFException when the file is shorter than the header declares
     *                      (corrupted or truncated file). Catching this
     *                      prevents creating incomplete Node objects.
     * @throws IOException  on disk access problems or a malformed header
     */
    public OutputGraph readBinaryGraph(Path path) throws IOException {
        byte[] raw = Files.readAllBytes(path);
        if (raw.length < BINARY_HEADER_SIZE) {
            throw new EOFException("Binary file too short: " + raw.length
                    + " bytes (need at least " + BINARY_HEADER_SIZE + ").");
        }

        ByteBuffer buffer = ByteBuffer.wrap(raw).order(ByteOrder.LITTLE_ENDIAN);
        int nodeCount = buffer.getInt();
        int edgeCount = buffer.getInt();

        if (nodeCount < 0 || edgeCount < 0) {
            throw new IOException("Invalid binary header: negative count "
                    + "(nodeCount=" + nodeCount + ", edgeCount=" + edgeCount
                    + "). Wrong byte order or wrong format?");
        }

        long expected = (long) BINARY_HEADER_SIZE
                + (long) nodeCount * BINARY_NODE_SIZE
                + (long) edgeCount * BINARY_EDGE_SIZE;
        if (raw.length < expected) {
            throw new EOFException("Truncated binary graph file: header says "
                    + nodeCount + " nodes + " + edgeCount + " edges (expects "
                    + expected + " bytes), but the file has only "
                    + raw.length + " bytes.");
        }

        List<Node> nodes = new ArrayList<>(nodeCount);
        List<Edge> edges = new ArrayList<>(edgeCount);

        try {
            for (int i = 0; i < nodeCount; i++) {
                int id = buffer.getInt();
                double x = buffer.getDouble();
                double y = buffer.getDouble();
                nodes.add(new Node(id, x, y));
            }
            for (int i = 0; i < edgeCount; i++) {
                int source = buffer.getInt();
                int target = buffer.getInt();
                double weight = buffer.getDouble();
                // The binary format does not store edge names; synthesize one.
                edges.add(new Edge(source, target, source + "-" + target, weight));
            }
        } catch (BufferUnderflowException e) {
            throw new EOFException("Unexpected end of binary graph file: " + path);
        }
        return new OutputGraph(nodes, edges);
    }
}
