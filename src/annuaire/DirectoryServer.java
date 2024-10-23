package annuaire;

import java.io.*;
import java.net.*;
import java.util.*;

public class DirectoryServer {
    private static Map<String, Etudiant> annuaireEtudiants = new HashMap<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8292);
            System.out.println("Le Serveur est démarré. En attente de client...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("LE Client est bien Connecté au Serveur !");

                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                out.writeObject("INSERER, RECHERCHER, LISTE_TOUS, LISTE_SERVICES");
                out.flush();

                while (true) {
                    String requete = (String) in.readObject();
                    if (requete.equals("EXIT")) {
                        break;
                    }
                    String reponse = traiterRequete(requete);
                    out.writeObject(reponse);
                    out.flush();
                }

                socket.close();
                System.out.println("le Client est déconnecté.");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    

    private static String traiterRequete(String requete) {
        String[] tokens = requete.split(",");
        String operation = tokens[0];
        switch (operation) {
            case "INSERER":
                if (tokens.length != 7) {
                    return "Erreur : Mauvais format pour INSERER.";
                }
                String nom = tokens[1];
                String prenom = tokens[2];
                String telephone = tokens[3];
                String email = tokens[4];
                String url = tokens[5];
                String dateNaissance = tokens[6];
                insererEtudiant(nom, prenom, telephone, email, url, dateNaissance);
                return "Etudiant inséré avec succès.";
            case "RECHERCHER":
                if (tokens.length != 3) {
                    return "Erreur : Mauvais format pour RECHERCHER.";
                }
                String nomRecherche = tokens[1];
                String prenomRecherche = tokens[2];
                Etudiant etudiant = rechercherEtudiant(nomRecherche, prenomRecherche);
                if (etudiant == null) {
                    return "Aucun étudiant trouvé avec ce nom et prénom.";
                }
                return etudiant.toString();

            case "LISTE_TOUS":
                StringBuilder sb = new StringBuilder();
                for (Etudiant e : annuaireEtudiants.values()) {
                    sb.append(e.toString()).append("\n");
                }
                return sb.toString();
            case "LISTE_SERVICES":
                return "INSERER, RECHERCHER, LISTE_TOUS, LISTE_SERVICES";
            default:
                return "Erreur : Requête invalide.";
        }
    }

    private static Etudiant rechercherEtudiant(String nomRecherche, String prenomRecherche) {
        for (Etudiant etudiant : annuaireEtudiants.values()) {
            if (etudiant.getNom().equals(nomRecherche) && etudiant.getPrenom().equals(prenomRecherche)) {
                return etudiant;
            }
        }
        return null;
    }
	private static void insererEtudiant(String nom, String prenom, String telephone, String email, String url, String dateNaissance) {
        annuaireEtudiants.put(nom + " " + prenom, new Etudiant(nom, prenom, telephone, email, url, dateNaissance));
    }

    private static class Etudiant {
        private String nom;
        private String prenom;
        private String telephone;
        private String email;
        private String url;
        private String dateNaissance;

        public Etudiant(String nom, String prenom, String telephone, String email, String url, String dateNaissance) {
            this.nom = nom;
            this.prenom = prenom;
            this.telephone = telephone;
            this.email = email;
            this.url = url;
            this.dateNaissance = dateNaissance;
        }

        public String getNom() {
            return nom;
        }

        public String getPrenom() {
            return prenom;
        }
        
        @Override
        public String toString() {
            return "Etudiant{" +
                    "nom='" + nom + '\'' +
                    ", prenom='" + prenom + '\'' +
                    ", telephone='" + telephone + '\'' +
                    ", email='" + email + '\'' +
                    ", url='" + url + '\'' +
                    ", dateNaissance='" + dateNaissance + '\'' +
                    '}';
        }
    }
}
