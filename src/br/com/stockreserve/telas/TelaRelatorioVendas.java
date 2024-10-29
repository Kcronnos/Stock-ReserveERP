/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package br.com.stockreserve.telas;

import br.com.stockreserve.dal.JsonUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import java.sql.*;
import br.com.stockreserve.dal.ModuloConexao;
import br.com.stockreserve.dal.Produto;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import net.proteanit.sql.DbUtils;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Felipe
 */
public class TelaRelatorioVendas extends javax.swing.JInternalFrame {
    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    /**
     * Creates new form TelaRelatorioVendas
     */
    public TelaRelatorioVendas() {
        initComponents();
        dcPesquisarData.addPropertyChangeListener("date", evt -> pesquisarNota());
        adicionarListeners();
        conexao = ModuloConexao.conector();
        graficoBarra();
    }
    
    //Método para preencher a tabela ao abrir a aba de notas fiscais
    private void preencherTabelaNotasFiscais() {
        String sql = "SELECT idnotafiscal AS ID_NOTA, nomevendedor AS VENDEDOR, nomecliente AS CLIENTE, valor AS VALOR_VENDA, datacompra AS `DATA/HORA` " +
                 "FROM tbnotasfiscais ";
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tblVendedores.setModel(DbUtils.resultSetToTableModel(rs));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    //Método para preencher a tabela ao pesquisar nota
    private void pesquisarNota() {
    String sql = "SELECT idnotafiscal AS ID_NOTA, nomevendedor AS VENDEDOR, nomecliente AS CLIENTE, " +
                 "valor AS VALOR, datacompra AS `DATA/HORA` " +
                 "FROM tbnotasfiscais " +
                 "WHERE (idnotafiscal LIKE ? OR nomevendedor LIKE ? OR nomecliente LIKE ?) " +
                 "AND (? IS NULL OR DATE(datacompra) = ?)";

    try {
        pst = conexao.prepareStatement(sql);

        // Obtendo o termo de busca dos campos de texto
        String searchTerm = txtVendPesquisar.getText() + "%";
        pst.setString(1, searchTerm);
        pst.setString(2, searchTerm);
        pst.setString(3, searchTerm);

        // Obtendo a data do JDateChooser e convertendo para java.sql.Date
        java.util.Date selectedDate = dcPesquisarData.getDate();
        if (selectedDate != null) {
            java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());
            pst.setDate(4, sqlDate); // Comparação com a data na cláusula SQL
            pst.setDate(5, sqlDate);
        } else {
            pst.setNull(4, java.sql.Types.DATE); // Definindo como NULL se não houver data
            pst.setNull(5, java.sql.Types.DATE);
        }

        // Executando a consulta
        rs = pst.executeQuery();

        // A linha abaixo usa a biblioteca rs2xml.jar para preencher a tabela
        tblVendedores.setModel(DbUtils.resultSetToTableModel(rs));
         // Calcular o total dos valores
        double total = 0;

        int colunaValor = 3; // Defina o índice da coluna que você deseja somar
        // Obtendo o modelo da tabela
        TableModel model = tblVendedores.getModel();

        // Iterando pelas linhas da tabela
        for (int i = 0; i < model.getRowCount(); i++) {
            Object valor = model.getValueAt(i, colunaValor); // Pega o valor na linha i da coluna especificada
                if (valor != null) { // Verifica se o valor não é nulo
                // Adiciona à lista, convertendo para Double se necessário
                try {
                    total += Double.valueOf(valor.toString()); // Soma ao total
                } catch (NumberFormatException e) {
                   System.out.println("Valor inválido na linha " + i + ": " + valor);
        }
    }
}

            // Exibir o total em uma nova tabela
            DefaultTableModel totalModel = new DefaultTableModel();
            totalModel.addColumn("Total");
            totalModel.addRow(new Object[]{total});
            tblTotal.setModel(totalModel); 

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Erro ao buscar dados: " + e.getMessage());
    }
}
    
    // Método para mostrar detalhes da nota fiscal
private void mostrarDetalhesNotaFiscal() throws SQLException, ParseException {
    String sql = "SELECT * FROM tbnotasfiscais WHERE idnotafiscal = ?";
    String sql2 = "SELECT produtos FROM tbnotasfiscais WHERE idnotafiscal = ?";
    
    int selectedRow = tblVendedores.getSelectedRow();
    if (selectedRow >= 0) {
        // Obtendo o ID da nota fiscal selecionada
        String idNota = (String) tblVendedores.getValueAt(selectedRow, 0);

        try (
            PreparedStatement pst1 = conexao.prepareStatement(sql);
            PreparedStatement pst2 = conexao.prepareStatement(sql2)
        ) {
            // Consulta principal
            pst1.setString(1, idNota);
            try (ResultSet rs1 = pst1.executeQuery()) {
                if (rs1.next()) {
                    // Criando um JFrame para mostrar os detalhes
                    JFrame detalhesFrame = new JFrame("Detalhes da Nota Fiscal");
                    detalhesFrame.setSize(600, 400);
                    detalhesFrame.setLayout(new BorderLayout());

                    // Painel para organizar o conteúdo
                    JPanel panelDetalhes = new JPanel(new GridBagLayout());
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.insets = new Insets(5, 5, 5, 5);
                    gbc.anchor = GridBagConstraints.WEST;

                    // Adicionando informações da nota fiscal
                    adicionarLabel(panelDetalhes, "ID Nota:", tblVendedores.getValueAt(selectedRow, 0).toString(), 0, gbc);
                    adicionarLabel(panelDetalhes, "Vendedor:", tblVendedores.getValueAt(selectedRow, 1).toString(), 1, gbc);
                    adicionarLabel(panelDetalhes, "Cliente:", tblVendedores.getValueAt(selectedRow, 2).toString(), 2, gbc);
                    adicionarLabel(panelDetalhes, "Valor:", String.valueOf(tblVendedores.getValueAt(selectedRow, 3)), 3, gbc);
                    adicionarLabel(panelDetalhes, "Data/Hora:", tblVendedores.getValueAt(selectedRow, 4).toString(), 4, gbc);

                    // Adicionando a lista de produtos
                    gbc.gridy = 5;
                    gbc.gridwidth = 2;
                    gbc.anchor = GridBagConstraints.CENTER;
                    panelDetalhes.add(new JLabel("Produtos:"), gbc);

                    // Configuração da tabela de produtos
                    String[] colunas = {"ID", "Nome", "Preço", "Quantidade"};
                    DefaultTableModel produtoTableModel = new DefaultTableModel(colunas, 0);
                    JTable tableProdutos = new JTable(produtoTableModel);

                    // Consulta para obter os produtos
                    pst2.setString(1, idNota);
                    try (ResultSet rs2 = pst2.executeQuery()) {
                        if (rs2.next()) {
                            String jsonProdutos = rs2.getString("produtos");
                            List<Produto> produtos = JsonUtil.jsonParaProdutos(jsonProdutos);

                            // Adicionando produtos à tabela
                            for (Produto produto : produtos) {
                                produtoTableModel.addRow(new Object[]{
                                    produto.getId(),
                                    produto.getNome(),
                                    produto.getPreco(),
                                    produto.getQuantidade()
                                });
                            }
                        }
                    }

                    JScrollPane scrollPaneProdutos = new JScrollPane(tableProdutos);
                    gbc.gridy = 6;
                    gbc.fill = GridBagConstraints.BOTH;
                    gbc.weightx = 1.0;
                    gbc.weighty = 1.0;
                    panelDetalhes.add(scrollPaneProdutos, gbc);

                    detalhesFrame.add(panelDetalhes, BorderLayout.CENTER);
                    detalhesFrame.setLocationRelativeTo(null);
                    detalhesFrame.setVisible(true);

                } else {
                    JOptionPane.showMessageDialog(this, "Nota Fiscal não encontrada!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao buscar detalhes: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    } else {
        JOptionPane.showMessageDialog(this, "Nenhuma nota fiscal selecionada!", "Aviso", JOptionPane.WARNING_MESSAGE);
    }
}

// Método auxiliar para adicionar JLabel ao painel
private void adicionarLabel(JPanel panel, String labelText, String valueText, int row, GridBagConstraints gbc) {
    gbc.gridx = 0;
    gbc.gridy = row;
    panel.add(new JLabel(labelText), gbc);

    gbc.gridx = 1;
    panel.add(new JLabel(valueText), gbc);
}
    
    //Método para a criação do grafico da quantidade de vendas
    public void graficoBarra(){
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.setValue(200, "Quantidade", "1");
        dataset.setValue(150, "Quantidade", "2");
        dataset.setValue(18, "Quantidade", "3");
        dataset.setValue(100, "Quantidade", "4");
        dataset.setValue(80, "Quantidade", "5");
        dataset.setValue(250, "Quantidade", "6");
        dataset.setValue(250, "Quantidade", "7");
        dataset.setValue(260, "Quantidade", "8");
        dataset.setValue(150, "Quantidade", "9");
        dataset.setValue(150, "Quantidade", "10");
        dataset.setValue(150, "Quantidade", "11");
        dataset.setValue(150, "Quantidade", "12");
        
        JFreeChart chart = ChartFactory.createBarChart("Vendas Realizadas","Mês","Quantidade", 
                dataset, PlotOrientation.VERTICAL, false,true,false);
        
        CategoryPlot categoryPlot = chart.getCategoryPlot();
        //categoryPlot.setRangeGridlinePaint(Color.BLUE);
        categoryPlot.setBackgroundPaint(Color.WHITE);
        BarRenderer renderer = (BarRenderer) categoryPlot.getRenderer();
        Color clr3 = new Color(204,0,51);
        renderer.setSeriesPaint(0, clr3);
        
        ChartPanel barpChartPanel = new ChartPanel(chart);
        panelGraficoBarra.removeAll();
        panelGraficoBarra.add(barpChartPanel, BorderLayout.CENTER);
        panelGraficoBarra.validate();
        
        
    }
    // Método para habilitar ou desabilitar o botão ver deltalhes
    private void verificarSelecaoTabela() {
        int linhaSelecionada = tblVendedores.getSelectedRow();
        btnVerMais.setEnabled(linhaSelecionada != -1); // Habilita o botão se houver uma linha selecionada
    }
    
    private void buscarDados() throws SQLException {
    String sql = "";
    PreparedStatement pst = null;
    ResultSet rs = null;

    if (radioBtnDia.isSelected()) {
        // Aqui é para filtrar as vendas de um mês específico e comparar as vendas por dias do mês
        String mes = String.valueOf(comonBoxMes1.getSelectedItem()); // Pega o mês selecionado
        String ano = String.valueOf(comonBoxAno1.getSelectedItem()); // Pega o ano selecionado

        sql = "SELECT DAY(datacompra) AS dia, SUM(valor) AS total_vendas FROM tbnotasfiscais " +
              "WHERE MONTH(datacompra) = ? AND YEAR(datacompra) = ? " +
              "GROUP BY DAY(datacompra) " +
              "ORDER BY dia";

        // Prepare a declaração antes de definir os parâmetros
        pst = conexao.prepareStatement(sql);
        // Preencha os parâmetros com mês e ano
        pst.setInt(1, Integer.parseInt(mes));
        pst.setInt(2, Integer.parseInt(ano));

    } else if (radioBtnMes.isSelected()) {
        // Aqui para filtrar as vendas de um ano específico e comparar as vendas por meses do ano
        String ano = (String) comonBoxAno2.getSelectedItem(); // Assume que comonBoxAno2 retorna um valor adequado

        sql = "SELECT MONTH(datacompra) AS mes, SUM(valor) AS total_vendas FROM tbnotasfiscais " +
              "WHERE YEAR(datacompra) = ? " +
              "GROUP BY MONTH(datacompra) " +
              "ORDER BY mes";

        // Prepare a declaração antes de definir os parâmetros
        pst = conexao.prepareStatement(sql);
        // Preencha o parâmetro com o ano
        pst.setInt(1, Integer.parseInt(ano));

    } else if (radioBtnAno.isSelected()) {
        // Aqui seleciona um ano específico e compara as vendas com os anos seguintes até o ano atual
        String ano = (String) comonBoxAno3.getSelectedItem();
        sql = "SELECT YEAR(datacompra) AS ano, SUM(valor) AS total_vendas FROM tbnotasfiscais " +
              "WHERE YEAR(datacompra) >= ? " + // Filtra por ano e anos seguintes
              "GROUP BY YEAR(datacompra) " +
              "ORDER BY ano";

        // Prepare a declaração antes de definir o parâmetro
        pst = conexao.prepareStatement(sql);
        // Preencha o parâmetro com o ano selecionado
        pst.setInt(1, Integer.parseInt(ano));
    }

    try {
        // Execute a consulta após preparar a declaração
        rs = pst.executeQuery();

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Verifica se o resultado está vazio e adiciona zero quando necessário
        if (!rs.isBeforeFirst()) { // Se não há resultados
            dataset.addValue(0, "Vendas", "Nenhum Resultado"); // Adiciona um valor de 0 para evitar gráficos vazios
        } else {
            while (rs.next()) {
                String periodo; // Armazena o período (dia, mês ou ano)
                if (radioBtnDia.isSelected()) {
                    periodo = rs.getString("dia"); // Dia do mês
                } else if (radioBtnMes.isSelected()) {
                    periodo = String.valueOf(rs.getInt("mes")); // Mês do ano
                } else {
                    periodo = rs.getString("ano"); // Ano
                }

                double totalVendas = rs.getDouble("total_vendas");
                // Caso o total seja nulo, adicione 0
                if (totalVendas == 0) {
                    totalVendas = 0;
                }

                dataset.addValue(totalVendas, "Vendas", periodo);
            }
        }

        // Atualiza o gráfico
        if (radioBtnDia.isSelected()){
        atualizarGrafico1(dataset);
        } else if (radioBtnMes.isSelected()){
        atualizarGrafico2(dataset);
        }else if (radioBtnMes.isSelected()){
        atualizarGrafico3(dataset);
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro ao buscar dados: " + e.getMessage());
    } finally {
        // Fechamento dos recursos
        if (rs != null) try { rs.close(); } catch (SQLException e) {}
        if (pst != null) try { pst.close(); } catch (SQLException e) {}
    }
}
    
    private void atualizarGrafico1(DefaultCategoryDataset dataset) {
    // Cria um gráfico de barras
    JFreeChart chart = ChartFactory.createBarChart(
        "Vendas por Período",  // Título do gráfico
        "Comparação por Dias do Mes Selecionado",             // Eixo X
        "Total de Vendas",     // Eixo Y
        dataset,               // Conjunto de dados
        PlotOrientation.VERTICAL,
        true,                  // Inclui legenda
        true,                  // Inclui tooltips
        false                  // Inclui URLs
    );

    ChartPanel chartPanel = new ChartPanel(chart);
    panelGraficoBarra.removeAll();
    panelGraficoBarra.add(chartPanel, BorderLayout.CENTER);
    panelGraficoBarra.validate();
    }
    
    private void atualizarGrafico2(DefaultCategoryDataset dataset) {
    // Cria um gráfico de barras
    JFreeChart chart = ChartFactory.createBarChart(
        "Vendas por Período",  // Título do gráfico
        " Comparação por Meses do Ano Selecionado",             // Eixo X
        "Total de Vendas",     // Eixo Y
        dataset,               // Conjunto de dados
        PlotOrientation.VERTICAL,
        true,                  // Inclui legenda
        true,                  // Inclui tooltips
        false                  // Inclui URLs
    );

    ChartPanel chartPanel = new ChartPanel(chart);
    panelGraficoBarra.removeAll();
    panelGraficoBarra.add(chartPanel, BorderLayout.CENTER);
    panelGraficoBarra.validate();
    }
    
    private void atualizarGrafico3(DefaultCategoryDataset dataset) {
    // Cria um gráfico de barras
    JFreeChart chart = ChartFactory.createBarChart(
        "Vendas por Período",  // Título do gráfico
        "Comparação por Anos",             // Eixo X
        "Total de Vendas",     // Eixo Y
        dataset,               // Conjunto de dados
        PlotOrientation.VERTICAL,
        true,                  // Inclui legenda
        true,                  // Inclui tooltips
        false                  // Inclui URLs
    );

    ChartPanel chartPanel = new ChartPanel(chart);
    panelGraficoBarra.removeAll();
    panelGraficoBarra.add(chartPanel, BorderLayout.CENTER);
    panelGraficoBarra.validate();
    }
    
     private void adicionarListeners() {
        comonBoxMes1.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if (radioBtnDia.isSelected()) {
                        try {
                            buscarDados();
                        } catch (SQLException ex) {
                            Logger.getLogger(TelaRelatorioVendas.class.getName()).log(Level.SEVERE, null, ex);
                            JOptionPane.showMessageDialog(TelaRelatorioVendas.this, "Erro ao buscar dados: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });
        comonBoxAno1.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if (radioBtnDia.isSelected()) {
                        try {
                            buscarDados();
                        } catch (SQLException ex) {
                            Logger.getLogger(TelaRelatorioVendas.class.getName()).log(Level.SEVERE, null, ex);
                            JOptionPane.showMessageDialog(TelaRelatorioVendas.this, "Erro ao buscar dados: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });
        comonBoxAno2.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if (radioBtnMes.isSelected()) {
                        try {
                            buscarDados();
                        } catch (SQLException ex) {
                            Logger.getLogger(TelaRelatorioVendas.class.getName()).log(Level.SEVERE, null, ex);
                            JOptionPane.showMessageDialog(TelaRelatorioVendas.this, "Erro ao buscar dados: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });
        comonBoxAno3.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if (radioBtnAno.isSelected()) {
                        try {
                            buscarDados();
                        } catch (SQLException ex) {
                            Logger.getLogger(TelaRelatorioVendas.class.getName()).log(Level.SEVERE, null, ex);
                            JOptionPane.showMessageDialog(TelaRelatorioVendas.this, "Erro ao buscar dados: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });
    }

    private void ConfigurarComonBox(){
    
        for (int mes = 1; mes <= 12; mes++) {
            comonBoxMes1.addItem(String.valueOf(mes));
        }
        
        int anoAtual = Calendar.getInstance().get(Calendar.YEAR);
        for (int ano = 1970; ano <= anoAtual; ano++) {
        comonBoxAno1.addItem(String.valueOf(ano));
        }
        
        for (int ano = 1970; ano <= anoAtual; ano++) {
        comonBoxAno2.addItem(String.valueOf(ano));
        }
        
        for (int ano = 1970; ano <= anoAtual; ano++) {
        comonBoxAno3.addItem(String.valueOf(ano));
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        grupoBotoesData = new javax.swing.ButtonGroup();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblVendedores = new javax.swing.JTable();
        txtVendPesquisar = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        dcPesquisarData = new com.toedter.calendar.JDateChooser();
        panelGraficoBarra = new javax.swing.JPanel();
        btnVerMais = new javax.swing.JButton();
        radioBtnAno = new javax.swing.JRadioButton();
        radioBtnMes = new javax.swing.JRadioButton();
        radioBtnDia = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        comonBoxMes1 = new javax.swing.JComboBox<>();
        comonBoxAno1 = new javax.swing.JComboBox<>();
        comonBoxAno2 = new javax.swing.JComboBox<>();
        comonBoxAno3 = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTotal = new javax.swing.JTable();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Relatório de Vendas");
        setPreferredSize(new java.awt.Dimension(1002, 336));
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tblVendedores = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int ColIndex){
                return false;
            }
        };
        tblVendedores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID NOTA", "VENDEDOR", "CLIENTE", "VALOR VENDA", "DATA/HORA"
            }
        ));
        jScrollPane2.setViewportView(tblVendedores);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 30, 550, 280));

        txtVendPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtVendPesquisarKeyReleased(evt);
            }
        });
        getContentPane().add(txtVendPesquisar, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 0, 220, -1));

        jLabel1.setText("Buscar");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 0, 40, -1));

        dcPesquisarData.setDateFormatString("yyyy-MM-dd");
        dcPesquisarData.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                dcPesquisarDataKeyReleased(evt);
            }
        });
        getContentPane().add(dcPesquisarData, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 0, 110, -1));

        panelGraficoBarra.setLayout(new java.awt.BorderLayout());
        getContentPane().add(panelGraficoBarra, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 420, 300));

        btnVerMais.setText("VER DETALHES");
        btnVerMais.setEnabled(false);
        btnVerMais.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerMaisActionPerformed(evt);
            }
        });
        getContentPane().add(btnVerMais, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 0, 130, -1));

        grupoBotoesData.add(radioBtnAno);
        radioBtnAno.setText("ANO");
        radioBtnAno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioBtnAnoActionPerformed(evt);
            }
        });
        getContentPane().add(radioBtnAno, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 510, -1, -1));

        grupoBotoesData.add(radioBtnMes);
        radioBtnMes.setText("MÊS");
        radioBtnMes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioBtnMesActionPerformed(evt);
            }
        });
        getContentPane().add(radioBtnMes, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 430, -1, -1));

        grupoBotoesData.add(radioBtnDia);
        radioBtnDia.setText("DIA");
        radioBtnDia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioBtnDiaActionPerformed(evt);
            }
        });
        getContentPane().add(radioBtnDia, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 360, 50, -1));

        jLabel2.setText("FILTRAR E COMPARAR POR:");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 310, -1, -1));

        comonBoxMes1.setEnabled(false);
        getContentPane().add(comonBoxMes1, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 360, -1, -1));

        comonBoxAno1.setEnabled(false);
        getContentPane().add(comonBoxAno1, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 360, -1, -1));

        comonBoxAno2.setEnabled(false);
        getContentPane().add(comonBoxAno2, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 430, -1, -1));

        comonBoxAno3.setEnabled(false);
        getContentPane().add(comonBoxAno3, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 510, -1, -1));

        tblTotal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "TOTAL"
            }
        ));
        jScrollPane1.setViewportView(tblTotal);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 320, 140, 60));

        setBounds(0, 0, 1000, 634);
    }// </editor-fold>//GEN-END:initComponents

    private void txtVendPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtVendPesquisarKeyReleased
        // TODO add your handling code here:
        pesquisarNota();
    }//GEN-LAST:event_txtVendPesquisarKeyReleased

    private void dcPesquisarDataKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dcPesquisarDataKeyReleased
        // TODO add your handling code here:
        pesquisarNota();
    }//GEN-LAST:event_dcPesquisarDataKeyReleased

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        // TODO add your handling code here:
        preencherTabelaNotasFiscais();
        //chamando o método para ativar o botão de ver detalhes
        tblVendedores.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                verificarSelecaoTabela(); // Atualiza o estado do botão
            }
        });
        
        ConfigurarComonBox();
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnVerMaisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerMaisActionPerformed
        // TODO add your handling code here:
        try {
            // TODO add your handling code here:
            mostrarDetalhesNotaFiscal();
        } catch (SQLException ex) {
            Logger.getLogger(TelaNotasFiscais.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(TelaNotasFiscais.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnVerMaisActionPerformed

    private void radioBtnDiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioBtnDiaActionPerformed
        // Habilita o ComboBox se radioBtnDia estiver selecionado; caso contrário, desabilita.
        comonBoxMes1.setEnabled(radioBtnDia.isSelected());
        comonBoxAno1.setEnabled(radioBtnDia.isSelected());
        comonBoxAno2.setEnabled(radioBtnMes.isSelected());
        comonBoxAno3.setEnabled(radioBtnAno.isSelected());
        try {
            buscarDados();
        } catch (SQLException ex) {
            Logger.getLogger(TelaRelatorioVendas.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_radioBtnDiaActionPerformed

    private void radioBtnMesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioBtnMesActionPerformed
        // TODO add your handling code here:
        comonBoxMes1.setEnabled(radioBtnDia.isSelected());
        comonBoxAno1.setEnabled(radioBtnDia.isSelected());
        comonBoxAno2.setEnabled(radioBtnMes.isSelected());
        comonBoxAno3.setEnabled(radioBtnAno.isSelected());
        try {
            buscarDados();
        } catch (SQLException ex) {
            Logger.getLogger(TelaRelatorioVendas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_radioBtnMesActionPerformed

    private void radioBtnAnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioBtnAnoActionPerformed
        // TODO add your handling code here:
        comonBoxMes1.setEnabled(radioBtnDia.isSelected());
        comonBoxAno1.setEnabled(radioBtnDia.isSelected());
        comonBoxAno2.setEnabled(radioBtnMes.isSelected());
        comonBoxAno3.setEnabled(radioBtnAno.isSelected());
        try {
            buscarDados();
        } catch (SQLException ex) {
            Logger.getLogger(TelaRelatorioVendas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_radioBtnAnoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnVerMais;
    private javax.swing.JComboBox<String> comonBoxAno1;
    private javax.swing.JComboBox<String> comonBoxAno2;
    private javax.swing.JComboBox<String> comonBoxAno3;
    private javax.swing.JComboBox<String> comonBoxMes1;
    private com.toedter.calendar.JDateChooser dcPesquisarData;
    private javax.swing.ButtonGroup grupoBotoesData;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel panelGraficoBarra;
    private javax.swing.JRadioButton radioBtnAno;
    private javax.swing.JRadioButton radioBtnDia;
    private javax.swing.JRadioButton radioBtnMes;
    private javax.swing.JTable tblTotal;
    private javax.swing.JTable tblVendedores;
    private javax.swing.JTextField txtVendPesquisar;
    // End of variables declaration//GEN-END:variables
}
