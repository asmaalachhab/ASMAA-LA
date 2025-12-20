// Sport.java
package asmaa.model;

import java.io.Serializable;

public class Sport implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nom;
    private String description;
    private String imageUrl;

    public Sport() {}

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
