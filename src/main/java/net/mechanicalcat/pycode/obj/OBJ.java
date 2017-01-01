/*
 * Copyright (c) 2017 Richard Jones <richard@mechanicalcat.net>
 * All Rights Reserved
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

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
