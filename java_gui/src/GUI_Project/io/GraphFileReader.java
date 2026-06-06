package GUI_Project.io;

import GUI_Project.model.Edge;
import GUI_Project.model.Node;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
public class GraphFileReader {

    private static final int BINARY_RECORD_SIZE = Integer.BYTES + 2 * Double.BYTES;

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

    /**
     * Reads a binary coordinates file: a sequence of
     * (int32 id, double x, double y) records in little-endian order, until EOF.
     *
     * @throws EOFException when the file ends mid-record (corrupted file).
     *                      Catching it prevents creating incomplete Node objects.
     * @throws IOException  on disk access problems
     */
    public List<Node> readCoordinatesBinary(Path path) throws IOException {
        byte[] raw = Files.readAllBytes(path);
        if (raw.length % BINARY_RECORD_SIZE != 0) {
            throw new EOFException("Truncated binary file: size " + raw.length
                    + " is not a multiple of record size " + BINARY_RECORD_SIZE);
        }

        ByteBuffer buffer = ByteBuffer.wrap(raw).order(ByteOrder.LITTLE_ENDIAN);
        int recordCount = raw.length / BINARY_RECORD_SIZE;
        List<Node> nodes = new ArrayList<>(recordCount);

        try {
            for (int i = 0; i < recordCount; i++) {
                int id = buffer.getInt();
                double x = buffer.getDouble();
                double y = buffer.getDouble();
                nodes.add(new Node(id, x, y));
            }
        } catch (java.nio.BufferUnderflowException e) {
            throw new EOFException("Unexpected end of binary file: " + path);
        }
        return nodes;
    }

    /** Streaming variant of {@link #readCoordinatesBinary(Path)}. */
    public List<Node> readCoordinatesBinaryStream(java.io.InputStream in) throws IOException {
        List<Node> nodes = new ArrayList<>();
        try (DataInputStream dis = new DataInputStream(in)) {
            byte[] record = new byte[BINARY_RECORD_SIZE];
            while (true) {
                int read = dis.read(record);
                if (read == -1) {
                    break;
                }
                if (read < BINARY_RECORD_SIZE) {
                    throw new EOFException("Truncated record in binary stream.");
                }
                ByteBuffer rec = ByteBuffer.wrap(record).order(ByteOrder.LITTLE_ENDIAN);
                int id = rec.getInt();
                double x = rec.getDouble();
                double y = rec.getDouble();
                nodes.add(new Node(id, x, y));
            }
        }
        return nodes;
    }
}
