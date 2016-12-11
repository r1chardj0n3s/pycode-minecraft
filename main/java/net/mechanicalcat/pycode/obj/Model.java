package net.mechanicalcat.pycode.obj;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Model {
    public List<Vector3f> vertices = new ArrayList<>();
    public List<Vector3f> normals = new ArrayList<>();
    public List<Face> faces = new ArrayList<>();
    public Map<String, Material> materials = new HashMap<>();
    private int glList;
    public Model(){}

    public void render() {
        GL11.glCallList(this.glList);
    }

    public void genList() {
        this.glList = GL11.glGenLists(1);
        GL11.glNewList(this.glList, GL11.GL_COMPILE);
//        if use_texture: glEnable(GL_TEXTURE_2D)
        GL11.glFrontFace(GL11.GL_CCW);
        GL11.glEnable(GL11.GL_CULL_FACE);

        GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_FILL);
        GL11.glDepthFunc(GL11.GL_LESS);
        GL11.glCullFace(GL11.GL_BACK);
        String currentMaterial = "";
        Material mtl;
        for (Face face : this.faces) {
            if (!face.material.equals(currentMaterial)) {
                currentMaterial = face.material;
                mtl = this.materials.get(face.material);
                if (mtl == null) {
                    GL11.glColor3f(1, 1, 1);
                } else {
//                    if 'texture_Kd' in mtl:
//                    # use diffuse texmap
//                    glBindTexture(GL_TEXTURE_2D, mtl['texture_Kd'])
                    GL11.glColor3f(mtl.diffuse.x, mtl.diffuse.y, mtl.diffuse.z);
                }
            }

            GL11.glBegin(GL11.GL_POLYGON);
            for (int i = 0; i < face.vertexes.size(); i++) {
                if (face.normals.get(i) != 0) {
                    Vector3f n = this.normals.get(face.normals.get(i));
                    GL11.glNormal3f(n.x, n.y, n.z);
                }
//                if texture_coords[i]:
//                    glTexCoord2fv(self.texcoords[texture_coords[i] - 1])
                Vector3f v = this.vertices.get(face.vertexes.get(i));
                GL11.glVertex3f(v.x, v.y, v.z);
            }
            GL11.glEnd();
        }

        GL11.glCullFace(GL11.GL_BACK);
        GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_FILL);

        GL11.glDisable(GL11.GL_CULL_FACE);

//      if use_texture: glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEndList();
    }
}
