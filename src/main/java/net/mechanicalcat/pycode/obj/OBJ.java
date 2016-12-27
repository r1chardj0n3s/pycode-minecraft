package net.mechanicalcat.pycode.obj;

import org.lwjgl.util.vector.Vector3f;

import java.io.*;

public class OBJ {
    public static Model loadModel(String filename) throws IOException {
        File f = new File(filename, "r");
        return loadModel(f);
    }

    public static Model loadModel(File f) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(f));
        Model m = new Model();
        String line;
        while((line = reader.readLine()) != null){
            if (!line.contains(" ")) continue;
            String[] parts = line.split(" ");
            String currentMaterial = "";
            switch (parts[0]) {
                case "v":
                    m.vertices.add(s2v(parts[1], parts[2], parts[3]));
                    break;
                case "vn":
                    m.normals.add(s2v(parts[1], parts[2], parts[3]));
                    break;
                case "vt":
//                    m.texCoords.add(s2v(parts[1], parts[2]));
                    break;
                case "f":
                    Face face = new Face(currentMaterial);
                    for (String s : parts) {
                        face.vertexes.add(Integer.valueOf(s.split("/")[0]) - 1);
                        face.normals.add(Integer.valueOf(s.split("/")[2]) - 1);
                    }
                    m.faces.add(face);
                    break;
                case "mtllib":
                    String name = line.split(" ")[1];
                    Material mat = Material.loadMaterial(name);
                    if (mat != null) {
                        m.materials.put(mat.name, mat);
                    }
                    break;
                case "usemat":
                case "usemtl":
                    currentMaterial = parts[1];
                    break;
            }
        }
        reader.close();
        return m;
    }

    private static Vector3f s2v(String x, String y, String z) {
        return new Vector3f(Float.valueOf(x), Float.valueOf(y), Float.valueOf(z));
    }
}
