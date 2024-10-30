package br.com.stockreserve.telas;

import br.com.stockreserve.dal.ModuloConexao;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageSelector extends JFrame {
    private ResourceBundle bundle;
    private Locale currentLocale;
    
    private final JLabel labelTituloRelatorioVendas;
    private final JLabel labelBuscarRelatorioVendas;
    private final JLabel labelTituloSobre;
    private final JLabel labelDesenvolvidoSobre;
    private final JLabel labelSistemaERPSobre;
    private final JLabel labelTituloRelatorioProdutos;
    private final JLabel labelBuscarRelatorioProdutos;
    private final JLabel labelCamposObrigatoriosProdutos;
    private final JLabel labelLimiteMinimoProdutos;
    private final JLabel labelPrecoProdutoProdutos;
    private final JLabel labelQuantidadeProdutos;
    private final JLabel labelTituloProdutos;
    private final JLabel labelNomeProdutoProdutos;
    private final JLabel labelIdProdutoProdutos;
    private final JLabel labelVencimentoProdutos;
    private final JButton btnRemoverProduto;
    private final JButton btnAlterarProduto;
    private final JButton btnAdicionarProduto;
    private final JTable tabelaProdutos;
    private final DefaultTableModel tabelaModelo;

    public LanguageSelector() {
        setLayout(new BorderLayout());

        // Inicializar os componentes da UI
        labelTituloRelatorioVendas = new JLabel();
        labelBuscarRelatorioVendas = new JLabel();
        labelTituloSobre = new JLabel();
        labelDesenvolvidoSobre = new JLabel();
        labelSistemaERPSobre = new JLabel();
        labelTituloRelatorioProdutos = new JLabel();
        labelBuscarRelatorioProdutos = new JLabel();
        labelCamposObrigatoriosProdutos = new JLabel();
        labelLimiteMinimoProdutos = new JLabel();
        labelPrecoProdutoProdutos = new JLabel();
        labelQuantidadeProdutos = new JLabel();
        labelTituloProdutos = new JLabel();
        labelNomeProdutoProdutos = new JLabel();
        labelIdProdutoProdutos = new JLabel();
        labelVencimentoProdutos = new JLabel();
        btnRemoverProduto = new JButton();
        btnAlterarProduto = new JButton();
        btnAdicionarProduto = new JButton();
        tabelaModelo = new DefaultTableModel(new String[] {"Produto", "Preço"}, 0);
        tabelaProdutos = new JTable(tabelaModelo);

        JPanel languagePanel = createLanguageSelectorPanel();
        add(languagePanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(20, 1));
        contentPanel.add(labelTituloRelatorioVendas);
        contentPanel.add(labelBuscarRelatorioVendas);
        contentPanel.add(labelTituloSobre);
        contentPanel.add(labelDesenvolvidoSobre);
        contentPanel.add(labelSistemaERPSobre);
        contentPanel.add(labelTituloRelatorioProdutos);
        contentPanel.add(labelBuscarRelatorioProdutos);
        contentPanel.add(labelCamposObrigatoriosProdutos);
        contentPanel.add(labelLimiteMinimoProdutos);
        contentPanel.add(labelPrecoProdutoProdutos);
        contentPanel.add(labelQuantidadeProdutos);
        contentPanel.add(labelTituloProdutos);
        contentPanel.add(labelNomeProdutoProdutos);
        contentPanel.add(labelIdProdutoProdutos);
        contentPanel.add(labelVencimentoProdutos);
        contentPanel.add(btnRemoverProduto);
        contentPanel.add(btnAlterarProduto);
        contentPanel.add(btnAdicionarProduto);
        contentPanel.add(new JScrollPane(tabelaProdutos));
        add(contentPanel, BorderLayout.CENTER);

        // Configurar a janela
        setTitle("Seleção de Idioma e Conversão de Moeda");
        setSize(600, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Carregar inicialmente no idioma padrão
        carregarIdioma("pt_BR", "BRL");
    }

    // Método para criar o painel de seleção de idioma
    public JPanel createLanguageSelectorPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        // Botão para selecionar o idioma português e configurar Real
        JButton btnPortugues = new JButton("Português");
        btnPortugues.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                carregarIdioma("pt_BR", "BRL");
            }
        });

        // Botão para selecionar o idioma inglês e configurar Dólar
        JButton btnEnglish = new JButton("English");
        btnEnglish.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                carregarIdioma("en_US", "USD");
            }
        });

        panel.add(btnPortugues);
        panel.add(btnEnglish);

        return panel;
    }

    private void carregarIdioma(String idioma, String moeda) {
        switch (idioma) {
            case "pt_BR":
                currentLocale = new Locale("pt", "BR");
                break;
            case "en_US":
                currentLocale = new Locale("en", "US");
                break;
            default:
                currentLocale = Locale.getDefault();
                break;
        }
        bundle = ResourceBundle.getBundle("br.com.stockreserve.telas.Bundle", currentLocale);
        atualizarTextos();
        carregarProdutos(moeda);
    }

    private void carregarProdutos(String moeda) {
        Connection conexao = ModuloConexao.conector();
        String sql = "SELECT nome, preco FROM produtos";
        tabelaModelo.setRowCount(0); // Limpar a tabela

        try {
            PreparedStatement pst = conexao.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String nomeProduto = rs.getString("nome");
                double precoProduto = rs.getDouble("preco");
                double precoConvertido = converterMoeda(precoProduto, moeda);
                tabelaModelo.addRow(new Object[]{nomeProduto, String.format("%.2f %s", precoConvertido, moeda)});
            }
            rs.close();
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private double converterMoeda(double valor, String moedaDestino) {
        // Conversão simples com taxa fixa
        double taxaConversao;
        switch (moedaDestino) {
            case "USD":
                taxaConversao = 0.20; // Exemplo: 1 BRL = 0.20 USD
                break;
            case "BRL":
            default:
                taxaConversao = 1.0; // Mesma moeda
                break;
        }
        return valor * taxaConversao;
    }

    // Método para atualizar a UI com as traduções do idioma selecionado
    private void atualizarTextos() {
        // Atualizar os componentes da UI de acordo com as novas traduções
        labelTituloRelatorioVendas.setText(bundle.getString("TelaRelatorioVendas.title"));
        labelBuscarRelatorioVendas.setText(bundle.getString("TelaRelatorioVendas.jLabel1.text"));
        labelTituloSobre.setText(bundle.getString("TelaSobre.title"));
        labelDesenvolvidoSobre.setText(bundle.getString("TelaSobre.jLabel3.text"));
        labelSistemaERPSobre.setText(bundle.getString("TelaSobre.jLabel2.text"));
        labelTituloRelatorioProdutos.setText(bundle.getString("TelaRelatorioProdutos.title"));
        labelBuscarRelatorioProdutos.setText(bundle.getString("TelaRelatorioProdutos.jLabel2.text"));
        labelCamposObrigatoriosProdutos.setText(bundle.getString("TelaProdutos.jLabel12.text"));
        labelLimiteMinimoProdutos.setText(bundle.getString("TelaProdutos.jLabel5.text"));
        labelPrecoProdutoProdutos.setText(bundle.getString("TelaProdutos.jLabel4.text"));
        labelQuantidadeProdutos.setText(bundle.getString("TelaProdutos.jLabel3.text"));
        labelTituloProdutos.setText(bundle.getString("TelaProdutos.title"));
        labelNomeProdutoProdutos.setText(bundle.getString("TelaProdutos.jLabel2.text"));
        btnRemoverProduto.setText(bundle.getString("TelaProdutos.btnRemover.toolTipText"));
        btnAlterarProduto.setText(bundle.getString("TelaProdutos.btnAlterar.toolTipText"));
        btnAdicionarProduto.setText(bundle.getString("TelaProdutos.btnAdicionar.toolTipText"));
        labelIdProdutoProdutos.setText(bundle.getString("TelaProdutos.jLabel1.text"));
        labelVencimentoProdutos.setText(bundle.getString("TelaProdutos.jLabel6.text"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LanguageSelector().setVisible(true));
    }
}
