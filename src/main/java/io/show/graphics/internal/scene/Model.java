package io.show.graphics.internal.scene;

import io.show.graphics.internal.Renderer;

public class Model implements AutoCloseable {

    private Mesh m_Mesh;
    private Material m_Material;

    public Model(Mesh mesh, Material material) {
        m_Mesh = mesh;
        m_Material = material;
    }

    public Model render() {
        Renderer.render(m_Mesh.getVertexArray(), m_Mesh.getIndexBuffer(), m_Material);
        return this;
    }

    public Mesh getMesh() {
        return m_Mesh;
    }

    public Material getMaterial() {
        return m_Material;
    }

    public Model setMesh(Mesh mesh) {
        m_Mesh = mesh;
        return this;
    }

    public Model setMaterial(Material material) {
        m_Material = material;
        return this;
    }

    @Override
    public void close() {
        if (m_Material != null) m_Material.close();
        if (m_Mesh != null) m_Mesh.close();
    }
}
