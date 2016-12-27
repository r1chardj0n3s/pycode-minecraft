package net.mechanicalcat.pycode.obj;

import java.util.ArrayList;
import java.util.List;

public class Face {
    public String material;
    public List<Integer> vertexes = new ArrayList<>();
    public List<Integer> textures = new ArrayList<>();
    public List<Integer> normals = new ArrayList<>();

    public Face(String material) {
        this.material = material;
    }
}
