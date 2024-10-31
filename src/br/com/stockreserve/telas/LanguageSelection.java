package br.com.stockreserve.telas;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageSelection extends JFrame {

    // Variável global para armazenar o idioma selecionado
    public static boolean selectedLanguage;

    // Bundle de recursos para armazenar as traduções
    private ResourceBundle bundle;

    public LanguageSelection() {
        // Define o idioma inicial com base no valor de selectedLanguage
        updateLocale();

        // Configurações do JFrame
        setTitle(bundle.getString("lang_title"));
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Define o ícone do aplicativo
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/br.com.stockreserve.icones/logo_stockreserve_64x64.png"))); // Ajuste o caminho conforme necessário

        // Painel principal com layout centralizado
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        // Restrições para centralizar os botões
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Margens entre botões
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Botão "English"
        JButton btnEnglish = new JButton("English");
        btnEnglish.addActionListener(e -> {
            selectedLanguage = true;
            updateLocale();
            updateTexts();
        });
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(btnEnglish, gbc);

        // Botão "Português"
        JButton btnPortugues = new JButton("Português");
        btnPortugues.addActionListener(e -> {
            selectedLanguage = false;
            updateLocale();
            updateTexts();
        });
        gbc.gridx = 1;
        panel.add(btnPortugues, gbc);

        // Botão "OK"
        JButton btnOk = new JButton("OK");
        btnOk.addActionListener(e -> {
            new TelaLogin().setVisible(true); // Abre a tela de login
            this.dispose(); // Fecha a tela de seleção de idioma
        });
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2; // Ocupa duas colunas
        panel.add(btnOk, gbc);

        // Adiciona o painel ao JFrame
        add(panel);
    }

    // Método para atualizar o Locale e carregar o ResourceBundle correto
    private void updateLocale() {
        Locale locale = selectedLanguage ? new Locale("en", "US") : new Locale("pt", "BR");
        bundle = ResourceBundle.getBundle("br.com.stockreserve.erp", locale);
    }

    // Método para atualizar os textos dos botões e do título
    private void updateTexts() {
        setTitle(bundle.getString("lang_title"));
    }

    public static void main(String[] args) {
        try {
            // Define o Look and Feel Nimbus (ou outro de sua preferência)
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Look and Feel não disponível, usando o padrão.");
        }
        SwingUtilities.invokeLater(() -> {
            LanguageSelection frame = new LanguageSelection();
            frame.setVisible(true);
        });
    }
}
