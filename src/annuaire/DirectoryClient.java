package annuaire;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class DirectoryClient extends JFrame {
    private static final long serialVersionUID = 1L;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private JTextArea textArea;
    private JTextField textField;
    private JPanel formPanel;

    public DirectoryClient() {
        super("Client Annuaire");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        textField = new JTextField();
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendRequest();
            }
        });
        add(textField, BorderLayout.SOUTH);

        try {
            Socket socket = new Socket("localhost", 8292);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            String services = (String) in.readObject();
            textArea.append("Voici la liste des Services disponibles :\n" + services + "\n");

            JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
            String[] servicesArray = services.split(", ");
            for (String service : servicesArray) {
                JButton button = new JButton(service);
                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String choix = ((JButton) e.getSource()).getText();
                        if (choix.equals("INSERER")) {
                            showInsertDialog();
                        } else if (choix.equals("RECHERCHER")) {
                            showSearchDialog();
                        } else {
                            textField.setText(choix);
                            sendRequest();
                        }
                    }
                });
                buttonPanel.add(button);
            }
            add(buttonPanel, BorderLayout.NORTH);

            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        try {
                            String reponse = (String) in.readObject();
                            textArea.append("Le Serveur Répond : " + reponse + "\n");
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void sendRequest() {
        try {
            String choix = textField.getText();
            out.writeObject(choix);
            out.flush();
            textField.setText("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showInsertDialog() {
        formPanel = new JPanel(new GridLayout(0, 2));
        formPanel.add(new JLabel("Nom:"));
        JTextField nomField = new JTextField();
        formPanel.add(nomField);
        formPanel.add(new JLabel("Prénom:"));
        JTextField prenomField = new JTextField();
        formPanel.add(prenomField);
        formPanel.add(new JLabel("Téléphone:"));
        JTextField telField = new JTextField();
        formPanel.add(telField);
        formPanel.add(new JLabel("Email:"));
        JTextField emailField = new JTextField();
        formPanel.add(emailField);
        formPanel.add(new JLabel("URL:"));
        JTextField urlField = new JTextField();
        formPanel.add(urlField);
        formPanel.add(new JLabel("Date de naissance:"));
        JTextField dateNaissanceField = new JTextField();
        formPanel.add(dateNaissanceField);

        int result = JOptionPane.showConfirmDialog(null, formPanel, "Inserer un étudiant",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String nom = nomField.getText();
            String prenom = prenomField.getText();
            String telephone = telField.getText();
            String email = emailField.getText();
            String url = urlField.getText();
            String dateNaissance = dateNaissanceField.getText();
            String insertion = "INSERER," + nom + "," + prenom + "," + telephone + "," + email + "," + url + "," + dateNaissance;
            textField.setText(insertion);
            sendRequest();
        }
    }

    private void showSearchDialog() {
        formPanel = new JPanel(new GridLayout(0, 2));
        formPanel.add(new JLabel("Nom:"));
        JTextField nomField = new JTextField();
        formPanel.add(nomField);
        formPanel.add(new JLabel("Prénom:"));
        JTextField prenomField = new JTextField();
        formPanel.add(prenomField);

        int result = JOptionPane.showConfirmDialog(null, formPanel, "Rechercher un étudiant",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String nom = nomField.getText();
            String prenom = prenomField.getText();
            String recherche = "RECHERCHER," + nom + "," + prenom;
            textField.setText(recherche);
            sendRequest();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new DirectoryClient().setVisible(true);
            }
        });
    }
}
