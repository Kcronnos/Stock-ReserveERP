/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package br.com.stockreserve.telas;

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
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import net.proteanit.sql.DbUtils;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

/**
 * TelaRelatorioProdutos é uma classe que representa uma interface gráfica
 * interna para relatar e gerenciar produtos em um sistema de estoque.
 * Esta classe estende JInternalFrame e permite realizar operações
 * como alteração de preços, geração de gráficos de validade de produtos,
 * pesquisa e análise de concorrência.
 *
 * @author Felipe
 */
public class TelaRelatorioProdutos extends javax.swing.JInternalFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    ResourceBundle bundle;

    /**
     * Construtor da classe. Inicializa a interface gráfica e estabelece
     * a conexão com o banco de dados. Configura o título da tela
     * com base no idioma selecionado e gera um gráfico de barras
     * com dados de validade dos produtos.
     */
    public TelaRelatorioProdutos() {
        Locale locale;
        if (LanguageSelection.selectedLanguage) {
            locale = Locale.of("en", "US");
        } else {
            locale = Locale.of("pt", "BR");
        }
        bundle = ResourceBundle.getBundle("br.com.stockreserve.erp", locale);
        initComponents();
        setTitle(bundle.getString("Prod_Rep"));
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                tblProdutos.clearSelection();
            }
        });
        conexao = ModuloConexao.conector();
        graficoBarra();
    }

    /**
     * Altera o preço de um produto específico no banco de dados. O método
     * valida os campos de entrada e atualiza o preço caso estejam preenchidos.
     * Após a atualização, a tabela de produtos é recarregada e os campos são
     * limpos.
     *
     * @author Feliipee013
     * @version 2.0
     */
    private void alterarPreco() {
        String sql = "update tbprodutos set preco =? where idproduto =?";

        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtNovoPreco.getText().replace(",", "."));
            pst.setString(2, txtIdProdu.getText());

            if (txtNovoPreco.getText().isEmpty() || txtIdProdu.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, bundle.getString("mandatory"));
            } else {
                int alterado = pst.executeUpdate();
                if (alterado > 0) {
                    JOptionPane.showMessageDialog(null, bundle.getString("product_updated_success"));
                    preencherTabelaProduto();
                    limpar();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    /**
     * Gera um gráfico de barras que exibe a quantidade de produtos com
     * diferentes status de validade (vencidos, perto de vencer, longe de
     * vencer, sem data). Os dados são obtidos do banco de dados e o gráfico é
     * exibido em um painel da interface.
     *
     * @author Feliipee013
     * @version 2.0
     */
    public void graficoBarra() {
        int produtosVencidos = 0;
        int produtosPertoDeVencer = 0;
        int produtosLongeDeVencer = 0;
        int produtosSemData = 0;

        String sql = """
    SELECT 
        COUNT(CASE WHEN vencimento < CURDATE() THEN 1 END) AS vencidos,
        COUNT(CASE WHEN vencimento >= CURDATE() AND vencimento <= DATE_ADD(CURDATE(), INTERVAL 31 DAY) THEN 1 END) AS perto,
        COUNT(CASE WHEN vencimento > DATE_ADD(CURDATE(), INTERVAL 31 DAY) THEN 1 END) AS longe,
        COUNT(CASE WHEN vencimento IS NULL THEN 1 END) AS sem_data
    FROM tbprodutos;
    """;

        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();

            if (rs.next()) {
                produtosVencidos = rs.getInt("vencidos");
                produtosPertoDeVencer = rs.getInt("perto");
                produtosLongeDeVencer = rs.getInt("longe");
                produtosSemData = rs.getInt("sem_data");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, bundle.getString("error_loading_chart") + e);
        }

        // Configuração do gráfico de barras
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(produtosVencidos, bundle.getString("expired"), "Status");
        dataset.addValue(produtosPertoDeVencer, bundle.getString("near_expiry"), "Status");
        dataset.addValue(produtosLongeDeVencer, bundle.getString("long_expiry"), "Status");
        dataset.addValue(produtosSemData, bundle.getString("no_date"), "Status");

        JFreeChart chart = ChartFactory.createBarChart(
                bundle.getString("product_expiry_data"),
                "Status",
                bundle.getString("amount"),
                dataset,
                PlotOrientation.VERTICAL,
                true, // Exibe legenda
                true,
                false
        );

        CategoryPlot categoryPlot = chart.getCategoryPlot();
        categoryPlot.setBackgroundPaint(Color.WHITE);

        // Configuração das cores para as barras
        BarRenderer renderer = (BarRenderer) categoryPlot.getRenderer();
        renderer.setSeriesPaint(0, Color.RED);       // Vencido
        renderer.setSeriesPaint(1, Color.YELLOW);    // Perto de Vencer
        renderer.setSeriesPaint(2, Color.GREEN);     // Longe de Vencer
        renderer.setSeriesPaint(3, Color.GRAY);      // Sem Data

        ChartPanel barChartPanel = new ChartPanel(chart);
        panelGraficoBarra1.removeAll();
        panelGraficoBarra1.add(barChartPanel, BorderLayout.CENTER);
        panelGraficoBarra1.validate();
    }

    /**
     * Preenche a tabela de produtos com dados do banco de dados ao abrir a aba
     * de relatório. O método também configura renderizadores para colunas,
     * aplicando cores com base no status e na data de vencimento.
     *
     * @author Feliipee013
     * @version 2.0
     */
    private void preencherTabelaProduto() {
        String sql;
        String nome = bundle.getString("name");
        String preco = bundle.getString("price");
        String quantidade = bundle.getString("amount");
        String limiteMinimo = bundle.getString("min_limit");
        String vencimento = bundle.getString("expiry");
        if (LanguageSelection.selectedLanguage) {
            sql = String.format("""
        SELECT idproduto AS ID, 
               nomeproduto AS %s, 
               preco / 5.78 AS %s, 
               quantidade AS %s, 
               limite_minimo AS %s, 
               vencimento AS %s,
               CASE 
                   WHEN quantidade = 0 THEN 'Vazio' 
                   WHEN quantidade < limite_minimo THEN 'Precisa Abastecer' 
                   ELSE 'OK' 
               END AS STATUS
        FROM tbprodutos;
        """, nome, preco, quantidade, limiteMinimo, vencimento);
        } else {
            sql = String.format("""
        SELECT idproduto AS ID, 
               nomeproduto AS %s, 
               preco AS %s, 
               quantidade AS %s, 
               limite_minimo AS %s, 
               vencimento AS %s,
               CASE 
                   WHEN quantidade = 0 THEN 'Vazio' 
                   WHEN quantidade < limite_minimo THEN 'Precisa Abastecer' 
                   ELSE 'OK' 
               END AS STATUS
        FROM tbprodutos;
        """, nome, preco, quantidade, limiteMinimo, vencimento);
        }

        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tblProdutos.setModel(DbUtils.resultSetToTableModel(rs));

            // Aplica o renderizador colorido à coluna `STATUS`
            tblProdutos.getColumnModel().getColumn(tblProdutos.getColumnCount() - 1)
                    .setCellRenderer(new TelaRelatorioProdutos.StatusCellRenderer());

            // Aplica o renderizador colorido à coluna `VENCIMENTO`
            int vencimentoColIndex = tblProdutos.getColumnModel().getColumnIndex("VENCIMENTO");
            tblProdutos.getColumnModel().getColumn(vencimentoColIndex)
                    .setCellRenderer(new TelaRelatorioProdutos.VencimentoCellRenderer());

        } catch (Exception e) {
            String n = "penis";
        }
    }

    /**
     * Realiza uma busca no banco de dados e preenche a tabela de produtos com
     * resultados que correspondem ao nome digitado. O método configura
     * renderizadores de cor para status e data de vencimento.
     *
     * @author Feliipee013
     * @version 2.0
     */
    private void pesquisarProdutos() {
        String nome = bundle.getString("name");
        String preco = bundle.getString("price");
        String quantidade = bundle.getString("amount");
        String limiteMinimo = bundle.getString("min_limit");
        String vencimento = bundle.getString("expiry");
        String sql = String.format("""
    SELECT idproduto AS ID, 
           nomeproduto AS %s, 
           preco AS %s,
           quantidade AS %s,
           limite_minimo AS %s,
           vencimento AS %s,
           CASE 
               WHEN quantidade = 0 THEN 'Vazio' 
               WHEN quantidade < limite_minimo THEN 'Precisa Abastecer' 
               ELSE 'OK' 
           END AS STATUS
    FROM tbprodutos
    WHERE nomeproduto LIKE ?
    """, nome, preco, quantidade, limiteMinimo, vencimento);

        try {
            pst = conexao.prepareStatement(sql);
            //passando o conteúdo da caixa de pesquisa para o ?
            //atenção ao % que é a continuação da string sql
            pst.setString(1, txtProduPesquisar.getText() + "%");
            rs = pst.executeQuery();
            //A linha abaixo usa a biblioteca rs2xml.jar para preencher a tabela
            tblProdutos.setModel(DbUtils.resultSetToTableModel(rs));

            // Configura o renderizador de cores para a coluna `STATUS`
            int statusColIndex = tblProdutos.getColumnModel().getColumnIndex("STATUS");
            tblProdutos.getColumnModel().getColumn(statusColIndex)
                    .setCellRenderer(new TelaRelatorioProdutos.StatusCellRenderer());

            // Configura o renderizador de cores para a coluna `VENCIMENTO`
            int vencimentoColIndex = tblProdutos.getColumnModel().getColumnIndex("VENCIMENTO");
            tblProdutos.getColumnModel().getColumn(vencimentoColIndex)
                    .setCellRenderer(new TelaRelatorioProdutos.VencimentoCellRenderer());

        } catch (Exception e) {
            String n = "penis";
        }
    }

    /**
     * Define o ID do produto nos campos de texto ao selecionar uma linha na
     * tabela de produtos. Esse método facilita a edição do produto ao exibir
     * seu ID nos campos apropriados.
     *
     * @author Feliipee013
     * @version 2.0
     */
    private void setarCampos() {
        int setar = tblProdutos.getSelectedRow();
        txtIdProdu.setText(tblProdutos.getModel().getValueAt(setar, 0).toString());
    }

    /**
     * Limpa os campos de ID e preço novo após a alteração do preço. Esse método
     * é usado para evitar que dados antigos permaneçam nos campos após a
     * operação.
     *
     * @author Feliipee013
     * @version 2.0
     */
    private void limpar() {
        txtIdProdu.setText(null);
        txtNovoPreco.setText(null);
    }

    /**
     * Renderizador personalizado para a coluna `STATUS` da tabela. Aplica uma
     * cor de fundo à célula com base no valor (por exemplo, `Vazio` em
     * vermelho, `OK` em verde).
     *
     * @author Feliipee013
     * @version 2.0
     */
    private static class StatusCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // Aplica cores com base no valor do `status`
            if (value != null) {
                switch (value.toString()) {
                    case "Vazio" ->
                        cell.setBackground(Color.RED);
                    case "Precisa Abastecer" ->
                        cell.setBackground(Color.YELLOW);
                    case "OK" ->
                        cell.setBackground(Color.GREEN);
                    default ->
                        cell.setBackground(Color.WHITE);
                }
            } else {
                cell.setBackground(Color.WHITE);
            }

            return cell;
        }
    }

    /**
     * Renderizador personalizado para a coluna `VENCIMENTO` da tabela. Aplica
     * cores de fundo para células de acordo com a proximidade da data de
     * vencimento. A célula fica vermelha para produtos vencidos, amarela para
     * os próximos de vencer, e verde para vencimento distante.
     *
     * @author Feliipee013
     * @version 2.0
     */
    private static class VencimentoCellRenderer extends DefaultTableCellRenderer {

        private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value != null) {
                try {
                    java.util.Date vencimentoDate = DATE_FORMAT.parse(value.toString());
                    java.util.Date currentDate = new java.util.Date();
                    long diffInMillis = vencimentoDate.getTime() - currentDate.getTime();
                    long daysUntilVencimento = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);

                    if (daysUntilVencimento < 0) {
                        cell.setBackground(Color.RED); // Vencido
                    } else if (daysUntilVencimento <= 30) {
                        cell.setBackground(Color.YELLOW); // Faltam 30 dias ou menos
                    } else {
                        cell.setBackground(Color.GREEN); // Mais de 30 dias
                    }
                } catch (Exception e) {
                    cell.setBackground(Color.WHITE); // Caso a data não seja válida ou seja nula
                }
            } else {
                cell.setBackground(Color.WHITE); // Sem data de vencimento
            }

            return cell;
        }
    }

    /**
     * Calcula a média de preços da concorrência para cada produto. Este método
     * consulta tabelas de preços de concorrentes e calcula a média para cada
     * produto.
     *
     * @return Um mapa onde as chaves são os nomes dos produtos e os valores são
     * as médias de preço da concorrência.
     * @throws SQLException caso ocorra algum erro durante a consulta ao banco
     * de dados.
     *
     * @author leog4
     * @version 2.0
     */
    public Map<String, Double> calcularMediaPrecosConcorrenciaPorProduto() throws SQLException {
        String query = "SELECT nome, AVG(preco) AS media_preco FROM ("
                + "SELECT nome, preco FROM concorrencia1 "
                + "UNION ALL "
                + "SELECT nome, preco FROM concorrencia2"
                + ") AS precos_concorrencia GROUP BY nome";

        Map<String, Double> mediasConcorrencia = new HashMap<>();

        try (
                PreparedStatement pstmt = conexao.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String nomeProduto = rs.getString("nome");
                double mediaPreco = rs.getDouble("media_preco");
                mediasConcorrencia.put(nomeProduto, mediaPreco);
            }
        }
        return mediasConcorrencia;
    }

    /**
     * Sugere um novo preço para todos os produtos com base na média de preços
     * da concorrência. Se a média de um concorrente for menor que o preço
     * atual, aplica-se um desconto; caso contrário, mantém o preço.
     *
     * @return Um mapa onde as chaves são os nomes dos produtos e os valores são
     * os preços sugeridos.
     * @throws SQLException caso ocorra algum erro durante a consulta ao banco
     * de dados.
     *
     * @author leog4
     * @version 2.0
     */
    public Map<String, Double> sugerirNovoPrecoParaTodos() throws SQLException {
        Map<String, Double> mediasConcorrencia = calcularMediaPrecosConcorrenciaPorProduto();
        Map<String, Double> sugestoesNovosPrecos = new HashMap<>();

        String queryProduto = "SELECT nomeproduto, preco FROM tbprodutos";

        try (
                PreparedStatement pstmt = conexao.prepareStatement(queryProduto); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String nomeProduto = rs.getString("nomeproduto");
                double precoAtual = rs.getDouble("preco");

                // Se a concorrência possui uma média para o produto, calcula o preço sugerido
                if (mediasConcorrencia.containsKey(nomeProduto)) {
                    double mediaConcorrencia = mediasConcorrencia.get(nomeProduto);
                    double novoPrecoSugerido = (mediaConcorrencia < precoAtual) ? mediaConcorrencia * 0.9 : precoAtual;
                    sugestoesNovosPrecos.put(nomeProduto, novoPrecoSugerido);
                }
            }
        }
        return sugestoesNovosPrecos;
    }

    /**
     * Preenche a tabela de análise de concorrência com sugestões de novos
     * preços. Os dados exibidos incluem o preço atual, a média de preços da
     * concorrência e o preço sugerido para cada produto.
     *
     * @throws SQLException caso ocorra algum erro durante a consulta ao banco
     * de dados.
     * 
     * @author leog4
     * @version 2.0
     */
    public void preencherTabelaAnaliseConcorrencia() throws SQLException {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn(bundle.getString("name"));
        model.addColumn(bundle.getString("current_price"));
        model.addColumn(bundle.getString("average_price"));
        model.addColumn(bundle.getString("new_price_suggested"));
        tblAnaliseConcorrencia.setModel(model);

        // Obtém as médias de preço da concorrência e as sugestões de novo preço
        Map<String, Double> mediasConcorrencia = calcularMediaPrecosConcorrenciaPorProduto();
        Map<String, Double> sugestoesNovosPrecos = sugerirNovoPrecoParaTodos();

        model.setRowCount(0); // Limpa as linhas atuais da tabela

        String queryPrecoAtual = "SELECT nomeproduto, preco FROM tbprodutos";
        try (PreparedStatement pstmt = conexao.prepareStatement(queryPrecoAtual); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String nomeProduto = rs.getString("nomeproduto");
                double precoAtual = rs.getDouble("preco");

                // Verifica se o produto possui uma média de concorrência
                if (mediasConcorrencia.containsKey(nomeProduto)) {
                    double mediaConcorrencia = mediasConcorrencia.get(nomeProduto);
                    double novoPreco = sugestoesNovosPrecos.getOrDefault(nomeProduto, precoAtual);

                    // Adiciona os dados como uma nova linha na tabela
                    model.addRow(new Object[]{
                        nomeProduto,
                        String.format("R$ %.2f", precoAtual),
                        String.format("R$ %.2f", mediaConcorrencia),
                        String.format("R$ %.2f", novoPreco)
                    });
                }
            }
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

        panelGraficoBarra1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProdutos = new javax.swing.JTable();
        txtProduPesquisar = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtIdProdu = new javax.swing.JTextField();
        txtNovoPreco = new javax.swing.JTextField();
        btnAlterarPreco = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblAnaliseConcorrencia = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Relatório de Produtos");
        setDoubleBuffered(true);
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameActivated(evt);
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

        panelGraficoBarra1.setLayout(new java.awt.BorderLayout());
        getContentPane().add(panelGraficoBarra1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 420, 300));

        tblProdutos = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int ColIndex){
                return false;
            }
        };
        tblProdutos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "NOME", "PREÇO", "QUANTIDADE", "LIMITE_MÍNIMO", "VENCIMENTO", "STATUS"
            }
        ));
        tblProdutos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblProdutosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblProdutos);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 30, 560, 270));

        txtProduPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtProduPesquisarKeyReleased(evt);
            }
        });
        getContentPane().add(txtProduPesquisar, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 0, 230, -1));

        jLabel2.setText(bundle.getString("search"));
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 0, 40, -1));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText(bundle.getString("change_price"));
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 310, -1, -1));

        jLabel3.setText("ID");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 350, 50, -1));

        jLabel4.setText(bundle.getString("new_price"));
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 390, -1, -1));

        txtIdProdu.setEnabled(false);
        getContentPane().add(txtIdProdu, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 350, 110, -1));
        getContentPane().add(txtNovoPreco, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 390, 110, -1));

        btnAlterarPreco.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br.com.stockreserve.icones/produto_editar.png"))); // NOI18N
        btnAlterarPreco.setToolTipText(bundle.getString("change_price"));
        btnAlterarPreco.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAlterarPreco.setPreferredSize(new java.awt.Dimension(50, 50));
        btnAlterarPreco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAlterarPrecoActionPerformed(evt);
            }
        });
        getContentPane().add(btnAlterarPreco, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 350, -1, 50));

        tblAnaliseConcorrencia.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblAnaliseConcorrencia.setEnabled(false);
        jScrollPane2.setViewportView(tblAnaliseConcorrencia);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 330, 560, 270));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText(bundle.getString("competition_analysis"));
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 310, -1, -1));

        setBounds(0, 0, 1000, 631);
    }// </editor-fold>//GEN-END:initComponents

    private void tblProdutosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProdutosMouseClicked
        //Chamando o método para setar campos
        setarCampos();
    }//GEN-LAST:event_tblProdutosMouseClicked

    private void formInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameActivated
        //Era pra a tabela está sendo ativada com a chamada do método abaixo ahhhhhh
        //Fuciona normal nas outras telas, mas aqui parou de funcionar não sei o porquê
        //Agora só ativa quando clica em qualquer lugar dentro da aba
        preencherTabelaProduto();
    }//GEN-LAST:event_formInternalFrameActivated

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        //Chamando o método pra preencher a tabela ao abrir a aba
        preencherTabelaProduto();
        try {
            preencherTabelaAnaliseConcorrencia();
        } catch (SQLException ex) {
            Logger.getLogger(TelaRelatorioProdutos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_formInternalFrameOpened

    private void txtProduPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtProduPesquisarKeyReleased
        //Chamando o método de pesquisar produtos
        pesquisarProdutos();
    }//GEN-LAST:event_txtProduPesquisarKeyReleased

    private void btnAlterarPrecoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlterarPrecoActionPerformed
        //Chamando o método para alterar preço
        alterarPreco();
    }//GEN-LAST:event_btnAlterarPrecoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAlterarPreco;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel panelGraficoBarra1;
    private javax.swing.JTable tblAnaliseConcorrencia;
    private javax.swing.JTable tblProdutos;
    private javax.swing.JTextField txtIdProdu;
    private javax.swing.JTextField txtNovoPreco;
    private javax.swing.JTextField txtProduPesquisar;
    // End of variables declaration//GEN-END:variables
}
