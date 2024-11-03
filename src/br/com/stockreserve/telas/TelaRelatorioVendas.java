/*
 * The MIT License
 *
 * Copyright 2024 Stock&Reserve.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import net.proteanit.sql.DbUtils;
import org.json.simple.parser.ParseException;
import weka.core.Attribute;
import weka.core.Instances;
import weka.classifiers.functions.LinearRegression;
import weka.core.FastVector;
import weka.core.Instance;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Classe responsável pela interface gráfica de relatório de vendas, onde são
 * exibidas as notas fiscais e informações detalhadas dos produtos. Permite
 * consultar, visualizar detalhes e gerar relatórios das vendas. Realiza a
 * conexão com o banco de dados para recuperar e manipular dados.
 *
 * @author leog4
 * @version 2.0
 */
public class TelaRelatorioVendas extends javax.swing.JInternalFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    ResourceBundle bundle;

    /**
     * Construtor da classe TelaRelatorioVendas. Configura o local de exibição
     * com base no idioma selecionado, inicializa os componentes, adiciona
     * ouvintes de eventos e conecta ao banco de dados.
     */
    public TelaRelatorioVendas() {
        Locale locale;
        if (LanguageSelection.selectedLanguage) {
            locale = Locale.of("en", "US");
        } else {
            locale = Locale.of("pt", "BR");
        }
        bundle = ResourceBundle.getBundle("br.com.stockreserve.erp", locale);
        initComponents();
        dcPesquisarData.addPropertyChangeListener("date", evt -> pesquisarNota());
        adicionarListeners();
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                tblVendedores.clearSelection();
            }
        });
        conexao = ModuloConexao.conector();
        graficoBarra();
    }

    /**
     * Preenche a tabela de notas fiscais ao abrir a aba de notas fiscais. Exibe
     * os dados de vendas com os cabeçalhos de coluna apropriados para o idioma.
     * Converte o valor para outra moeda dividindo por 5.78, caso o idioma
     * selecionado seja inglês.
     */
    private void preencherTabelaNotasFiscais() {
        String sql;
        if (LanguageSelection.selectedLanguage) {
            sql = "SELECT idnotafiscal AS " + bundle.getString("ID_invoices") + ", nomevendedor AS " + bundle.getString("seller") + ", nomecliente AS " + bundle.getString("client") + ", valor / 5.78 AS " + bundle.getString("value") + ", datacompra AS `" + bundle.getString("dt") + "` "
                    + "FROM tbnotasfiscais ";
        } else {
            sql = "SELECT idnotafiscal AS " + bundle.getString("ID_invoices") + ", nomevendedor AS " + bundle.getString("seller") + ", nomecliente AS " + bundle.getString("client") + ", valor AS " + bundle.getString("value") + ", datacompra AS `" + bundle.getString("dt") + "` "
                    + "FROM tbnotasfiscais ";
        }
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tblVendedores.setModel(DbUtils.resultSetToTableModel(rs));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    /**
     * Pesquisa uma nota fiscal com base em critérios de busca, como ID da nota,
     * nome do vendedor, nome do cliente e data da compra. O valor exibido é
     * convertido dependendo do idioma selecionado. Calcula o valor total das
     * notas exibidas e o mostra em uma tabela separada.
     */
    private void pesquisarNota() {
        String sql;
        if (LanguageSelection.selectedLanguage) {
            sql = "SELECT idnotafiscal AS " + bundle.getString("ID_invoices") + ", nomevendedor AS " + bundle.getString("seller") + ", nomecliente AS " + bundle.getString("client") + ", valor / 5.78 AS " + bundle.getString("value") + ", datacompra AS `" + bundle.getString("dt") + "` "
                    + "FROM tbnotasfiscais "
                    + "WHERE (idnotafiscal LIKE ? OR nomevendedor LIKE ? OR nomecliente LIKE ?) "
                    + "AND (? IS NULL OR DATE(datacompra) = ?)";
        } else {
            sql = "SELECT idnotafiscal AS " + bundle.getString("ID_invoices") + ", nomevendedor AS " + bundle.getString("seller") + ", nomecliente AS " + bundle.getString("client") + ", valor AS " + bundle.getString("value") + ", datacompra AS `" + bundle.getString("dt") + "` "
                    + "FROM tbnotasfiscais "
                    + "WHERE (idnotafiscal LIKE ? OR nomevendedor LIKE ? OR nomecliente LIKE ?) "
                    + "AND (? IS NULL OR DATE(datacompra) = ?)";
        }

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
                        System.out.println(bundle.getString("invalid_value") + i + ": " + valor);
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
            JOptionPane.showMessageDialog(null, bundle.getString("error_fetching_data") + e.getMessage());
        }
    }

    /**
     * Mostra os detalhes da nota fiscal selecionada na interface. Exibe
     * informações do cliente, vendedor, valor e lista de produtos associados à
     * nota. Utiliza um painel personalizado para exibir as informações.
     *
     * @throws SQLException Se houver erro de acesso ao banco de dados.
     * @throws ParseException Se ocorrer erro de conversão de dados.
     */
    private void mostrarDetalhesNotaFiscal() throws SQLException, ParseException {
        String sql = "SELECT * FROM tbnotasfiscais WHERE idnotafiscal = ?";
        String sql2 = "SELECT produtos FROM tbnotasfiscais WHERE idnotafiscal = ?";

        int selectedRow = tblVendedores.getSelectedRow();
        if (selectedRow >= 0) {
            // Obtendo o ID da nota fiscal selecionada
            String idNota = (String) tblVendedores.getValueAt(selectedRow, 0);

            try (
                    PreparedStatement pst1 = conexao.prepareStatement(sql); PreparedStatement pst2 = conexao.prepareStatement(sql2)) {
                // Consulta principal
                pst1.setString(1, idNota);
                try (ResultSet rs1 = pst1.executeQuery()) {
                    if (rs1.next()) {
                        // Criando um JFrame para mostrar os detalhes
                        JFrame detalhesFrame = new JFrame(bundle.getString("invoice_details"));
                        detalhesFrame.setSize(600, 400);
                        detalhesFrame.setLayout(new BorderLayout());

                        // Painel para organizar o conteúdo
                        JPanel panelDetalhes = new JPanel(new GridBagLayout());
                        GridBagConstraints gbc = new GridBagConstraints();
                        gbc.insets = new Insets(5, 5, 5, 5);
                        gbc.anchor = GridBagConstraints.WEST;

                        // Adicionando informações da nota fiscal
                        adicionarLabel(panelDetalhes, bundle.getString("ID_invoices") + ":", tblVendedores.getValueAt(selectedRow, 0).toString(), 0, gbc);
                        adicionarLabel(panelDetalhes, bundle.getString("seller") + ":", tblVendedores.getValueAt(selectedRow, 1).toString(), 1, gbc);
                        adicionarLabel(panelDetalhes, bundle.getString("client") + ":", tblVendedores.getValueAt(selectedRow, 2).toString(), 2, gbc);
                        adicionarLabel(panelDetalhes, bundle.getString("value") + ":", String.valueOf(tblVendedores.getValueAt(selectedRow, 3)), 3, gbc);
                        adicionarLabel(panelDetalhes, bundle.getString("dt") + ":", tblVendedores.getValueAt(selectedRow, 4).toString(), 4, gbc);

                        // Adicionando a lista de produtos
                        gbc.gridy = 5;
                        gbc.gridwidth = 2;
                        gbc.anchor = GridBagConstraints.CENTER;
                        panelDetalhes.add(new JLabel(bundle.getString("prods")), gbc);

                        // Configuração da tabela de produtos
                        String[] colunas = {"ID", bundle.getString("name"), bundle.getString("value"), bundle.getString("amount")};
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
                        JOptionPane.showMessageDialog(this, bundle.getString("invoice_not_found"), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException | ParseException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, bundle.getString("error_fetching_details") + e.getMessage(), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, bundle.getString("no_invoice_selected"), bundle.getString("attention"), JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Método auxiliar que adiciona um JLabel ao painel especificado na
     * interface gráfica. Utilizado para exibir dados organizados com labels e
     * valores.
     *
     * @param panel O painel ao qual o JLabel será adicionado.
     * @param labelText O texto do rótulo que será exibido na primeira coluna.
     * @param valueText O texto do valor que será exibido na segunda coluna.
     * @param row A linha na qual o JLabel será posicionado.
     * @param gbc As restrições de layout para organizar os componentes no
     * painel.
     */
    private void adicionarLabel(JPanel panel, String labelText, String valueText, int row, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        panel.add(new JLabel(valueText), gbc);
    }

    /**
     * Cria e exibe um gráfico de barras com a quantidade de vendas por mês.
     * Exibe a quantidade de vendas mensais com base em dados fictícios para
     * ilustração.
     */
    public void graficoBarra() {
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

        JFreeChart chart = ChartFactory.createBarChart(bundle.getString("sales_made"), bundle.getString("month"), bundle.getString("amount"),
                dataset, PlotOrientation.VERTICAL, false, true, false);

        CategoryPlot categoryPlot = chart.getCategoryPlot();
        //categoryPlot.setRangeGridlinePaint(Color.BLUE);
        categoryPlot.setBackgroundPaint(Color.WHITE);
        BarRenderer renderer = (BarRenderer) categoryPlot.getRenderer();
        Color clr3 = new Color(204, 0, 51);
        renderer.setSeriesPaint(0, clr3);

        ChartPanel barpChartPanel = new ChartPanel(chart);
        panelGraficoBarra.removeAll();
        panelGraficoBarra.add(barpChartPanel, BorderLayout.CENTER);
        panelGraficoBarra.validate();

    }

    /**
     * Habilita ou desabilita o botão de visualização de detalhes com base na
     * seleção da linha na tabela. O botão será habilitado apenas se uma linha
     * estiver selecionada.
     */
    private void verificarSelecaoTabela() {
        int linhaSelecionada = tblVendedores.getSelectedRow();
        btnVerMais.setEnabled(linhaSelecionada != -1); // Habilita o botão se houver uma linha selecionada
    }

    /**
     * Busca e exibe dados de vendas no gráfico com base no filtro selecionado
     * (dia, mês ou ano). Calcula a soma das vendas para cada intervalo e exibe
     * os resultados no gráfico.
     *
     * @throws SQLException Em caso de erro de acesso ao banco de dados.
     */
    private void buscarDados() throws SQLException {
        String sql = "";
        PreparedStatement pst = null;
        ResultSet rs = null;

        if (radioBtnDia.isSelected()) {
            // Aqui é para filtrar as vendas de um mês específico e comparar as vendas por dias do mês
            String mes = String.valueOf(comonBoxMes1.getSelectedItem()); // Pega o mês selecionado
            String ano = String.valueOf(comonBoxAno1.getSelectedItem()); // Pega o ano selecionado
            if (LanguageSelection.selectedLanguage) {
                sql = "SELECT DAY(datacompra) AS dia, SUM(valor) / 5.78 AS total_vendas FROM tbnotasfiscais "
                        + "WHERE MONTH(datacompra) = ? AND YEAR(datacompra) = ? "
                        + "GROUP BY DAY(datacompra) "
                        + "ORDER BY dia";
            } else {
                sql = "SELECT DAY(datacompra) AS dia, SUM(valor) AS total_vendas FROM tbnotasfiscais "
                        + "WHERE MONTH(datacompra) = ? AND YEAR(datacompra) = ? "
                        + "GROUP BY DAY(datacompra) "
                        + "ORDER BY dia";
            }

            // Prepare a declaração antes de definir os parâmetros
            pst = conexao.prepareStatement(sql);
            // Preencha os parâmetros com mês e ano
            pst.setInt(1, Integer.parseInt(mes));
            pst.setInt(2, Integer.parseInt(ano));

        } else if (radioBtnMes.isSelected()) {
            // Aqui para filtrar as vendas de um ano específico e comparar as vendas por meses do ano
            String ano = (String) comonBoxAno2.getSelectedItem(); // Assume que comonBoxAno2 retorna um valor adequado
            if (LanguageSelection.selectedLanguage) {
                sql = "SELECT MONTH(datacompra) AS mes, SUM(valor) / 5.78 AS total_vendas FROM tbnotasfiscais "
                        + "WHERE YEAR(datacompra) = ? "
                        + "GROUP BY MONTH(datacompra) "
                        + "ORDER BY mes";
            } else {
                sql = "SELECT MONTH(datacompra) AS mes, SUM(valor) AS total_vendas FROM tbnotasfiscais "
                        + "WHERE YEAR(datacompra) = ? "
                        + "GROUP BY MONTH(datacompra) "
                        + "ORDER BY mes";
            }

            // Prepare a declaração antes de definir os parâmetros
            pst = conexao.prepareStatement(sql);
            // Preencha o parâmetro com o ano
            pst.setInt(1, Integer.parseInt(ano));

        } else if (radioBtnAno.isSelected()) {
            // Aqui seleciona um ano específico e compara as vendas com os anos seguintes até o ano atual
            String ano = (String) comonBoxAno3.getSelectedItem();
            if (LanguageSelection.selectedLanguage) {
                sql = "SELECT YEAR(datacompra) AS ano, SUM(valor) / 5.78 AS total_vendas FROM tbnotasfiscais "
                        + "WHERE YEAR(datacompra) >= ? "
                        + // Filtra por ano e anos seguintes
                        "GROUP BY YEAR(datacompra) "
                        + "ORDER BY ano";
            } else {
                sql = "SELECT YEAR(datacompra) AS ano, SUM(valor) AS total_vendas FROM tbnotasfiscais "
                        + "WHERE YEAR(datacompra) >= ? "
                        + // Filtra por ano e anos seguintes
                        "GROUP BY YEAR(datacompra) "
                        + "ORDER BY ano";
            }

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
                dataset.addValue(0, bundle.getString("sales"), bundle.getString("no_result")); // Adiciona um valor de 0 para evitar gráficos vazios
            } else {
                while (rs.next()) {
                    String periodo; // Armazena o período (dia, mês ou ano)
                    if (radioBtnDia.isSelected()) {
                        periodo = rs.getString(bundle.getString("day")); // Dia do mês
                    } else if (radioBtnMes.isSelected()) {
                        periodo = rs.getString(bundle.getString("month")); // Mês do ano
                    } else {
                        periodo = rs.getString(bundle.getString("year")); // Ano
                    }

                    double totalVendas = rs.getDouble("total_vendas");
                    // Caso o total seja nulo, adicione 0
                    if (totalVendas == 0) {
                        totalVendas = 0;
                    }

                    dataset.addValue(totalVendas, bundle.getString("sales"), periodo);
                }
            }

            // Atualiza o gráfico
            if (radioBtnDia.isSelected()) {
                atualizarGrafico1(dataset);
            } else if (radioBtnMes.isSelected()) {
                atualizarGrafico2(dataset);
            } else if (radioBtnAno.isSelected()) {
                atualizarGrafico3(dataset);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, bundle.getString("error_fetching_data") + e.getMessage());
        } finally {
            // Fechamento dos recursos
            if (rs != null) try {
                rs.close();
            } catch (SQLException e) {
            }
            if (pst != null) try {
                pst.close();
            } catch (SQLException e) {
            }
        }
    }

    /**
     * Atualiza o gráfico de vendas por dia, exibindo o conjunto de dados
     * fornecido em um gráfico de barras com configuração específica para o
     * período diário.
     *
     * @param dataset O conjunto de dados a ser exibido no gráfico.
     */
    private void atualizarGrafico1(DefaultCategoryDataset dataset) {
        // Cria um gráfico de barras
        JFreeChart chart = ChartFactory.createBarChart(
                bundle.getString("sales_period"), // Título do gráfico
                bundle.getString("comparisonD"), // Eixo X
                bundle.getString("total_sales"), // Eixo Y
                dataset, // Conjunto de dados
                PlotOrientation.VERTICAL,
                true, // Inclui legenda
                true, // Inclui tooltips
                false // Inclui URLs
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        panelGraficoBarra.removeAll();
        panelGraficoBarra.add(chartPanel, BorderLayout.CENTER);
        panelGraficoBarra.validate();
    }

    /**
     * Atualiza o gráfico de vendas por mês, exibindo o conjunto de dados
     * fornecido em um gráfico de barras com configuração específica para o
     * período mensal.
     *
     * @param dataset O conjunto de dados a ser exibido no gráfico.
     */
    private void atualizarGrafico2(DefaultCategoryDataset dataset) {
        // Cria um gráfico de barras
        JFreeChart chart = ChartFactory.createBarChart(
                bundle.getString("sales_period"), // Título do gráfico
                bundle.getString("comparisonM"), // Eixo X
                bundle.getString("total_sales"), // Eixo Y
                dataset, // Conjunto de dados
                PlotOrientation.VERTICAL,
                true, // Inclui legenda
                true, // Inclui tooltips
                false // Inclui URLs
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        panelGraficoBarra.removeAll();
        panelGraficoBarra.add(chartPanel, BorderLayout.CENTER);
        panelGraficoBarra.validate();
    }

    /**
     * Atualiza o gráfico de vendas por ano, exibindo o conjunto de dados
     * fornecido em um gráfico de barras com configuração específica para o
     * período anual.
     *
     * @param dataset O conjunto de dados a ser exibido no gráfico.
     */
    private void atualizarGrafico3(DefaultCategoryDataset dataset) {
        // Cria um gráfico de barras
        JFreeChart chart = ChartFactory.createBarChart(
                bundle.getString("sales_period"), // Título do gráfico
                bundle.getString("comaparisonY"), // Eixo X
                bundle.getString("total_sales"), // Eixo Y
                dataset, // Conjunto de dados
                PlotOrientation.VERTICAL,
                true, // Inclui legenda
                true, // Inclui tooltips
                false // Inclui URLs
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        panelGraficoBarra.removeAll();
        panelGraficoBarra.add(chartPanel, BorderLayout.CENTER);
        panelGraficoBarra.validate();
    }

    /**
     * Adiciona listeners aos componentes de seleção de período para atualizar o
     * gráfico automaticamente quando uma nova opção é selecionada.
     */
    private void adicionarListeners() {
        // Método auxiliar para reduzir repetição
        ItemListener itemListener = new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    try {
                        buscarDados();
                    } catch (SQLException ex) {
                        Logger.getLogger(TelaRelatorioVendas.class.getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(TelaRelatorioVendas.this, bundle.getString("error_fetching_data") + ex.getMessage(), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        };

        comonBoxMes1.addItemListener(e -> {
            if (radioBtnDia.isSelected()) {
                itemListener.itemStateChanged(e);
            }
        });

        comonBoxAno1.addItemListener(e -> {
            if (radioBtnDia.isSelected()) {
                itemListener.itemStateChanged(e);
            }
        });

        comonBoxAno2.addItemListener(e -> {
            if (radioBtnMes.isSelected()) {
                itemListener.itemStateChanged(e);
            }
        });

        comonBoxAno3.addItemListener(e -> {
            if (radioBtnAno.isSelected()) {
                itemListener.itemStateChanged(e);
            }
        });
    }

    /**
     * Configura os componentes JComboBox (caixas de seleção) com valores de
     * meses e anos. Preenche a comonBoxMes1 com os 12 meses do ano e
     * comonBoxAno1, comonBoxAno2, e comonBoxAno3 com os anos de 1970 até o ano
     * atual.
     */
    private void ConfigurarComonBox() {

        for (int mes = 1; mes <= 12; mes++) {
            comonBoxMes1.addItem(String.valueOf(mes));
        }

        int anoAtual = Calendar.getInstance().get(Calendar.YEAR);
        for (int ano = anoAtual; ano >= 1970; ano--) {
            comonBoxAno1.addItem(String.valueOf(ano));
        }

        for (int ano = anoAtual; ano >= 1970; ano--) {
            comonBoxAno2.addItem(String.valueOf(ano));
        }

        for (int ano = anoAtual; ano >= 1970; ano--) {
            comonBoxAno3.addItem(String.valueOf(ano));
        }
    }

    /**
     * Obtém os dados mensais de vendas a partir do banco de dados. Para cada
     * mês, calcula o total de vendas e, se necessário, converte os valores com
     * base na taxa de câmbio.
     *
     * @return Uma lista de arrays de double, onde cada array contém o mês e o
     * total de vendas desse mês.
     * @throws Exception Se ocorrer um erro ao acessar o banco de dados.
     */
    public List<double[]> getDadosMensais() throws Exception {
        List<double[]> salesData = new ArrayList<>();

        try (Statement statement = conexao.createStatement()) {
            String sql;
            if (LanguageSelection.selectedLanguage) {
                sql = "SELECT MONTH(datacompra) AS mes, SUM(valor) / 5.78 AS total "
                        + "FROM tbnotasfiscais "
                        + "GROUP BY MONTH(datacompra)";
            } else {
                sql = "SELECT MONTH(datacompra) AS mes, SUM(valor) AS total "
                        + "FROM tbnotasfiscais "
                        + "GROUP BY MONTH(datacompra)";
            }
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                int mes = resultSet.getInt("mes");
                double total = resultSet.getDouble("total");
                salesData.add(new double[]{mes, total}); // Adiciona um array de doubles
            }
        }
        return salesData;
    }

    /**
     * Faz uma previsão de vendas para o próximo mês com base nos dados
     * históricos. Utiliza regressão linear para estimar o total de vendas.
     *
     * @param salesData Os dados históricos de vendas mensais.
     * @return A previsão de vendas para o próximo mês.
     * @throws Exception Se ocorrer um erro ao prever os dados.
     */
    public double predictNextMonthSales(List<double[]> salesData) throws Exception {
        // Definir atributos usando FastVector
        FastVector attributes = new FastVector(2);
        attributes.addElement(new Attribute("mes")); // Mês
        attributes.addElement(new Attribute("total")); // Total de vendas

        // Criar conjunto de dados
        Instances dataset = new Instances("SalesData", attributes, salesData.size());
        dataset.setClassIndex(1); // A classe (o que queremos prever) é o total de vendas

        // Adicionar dados
        for (double[] data : salesData) {
            double[] values = new double[dataset.numAttributes()];
            values[0] = data[0]; // Mês
            values[1] = data[1]; // Total de vendas

            // Criar uma nova instância e adicioná-la ao dataset
            Instance instance = new Instance(1.0, values);
            dataset.add(instance);
        }

        // Treinar o modelo de regressão
        LinearRegression model = new LinearRegression();
        model.buildClassifier(dataset);

        // Criar um novo registro para prever o próximo mês
        double[] nextMonthValues = new double[dataset.numAttributes()];
        nextMonthValues[0] = salesData.size() + 1; // Próximo mês
        Instance nextMonthInstance = new Instance(1.0, nextMonthValues);
        nextMonthInstance.setDataset(dataset); // Associar ao dataset

        // Fazer previsão
        return model.classifyInstance(nextMonthInstance);
    }

    /**
     * Carrega a tabela com a previsão de vendas para o próximo mês. Obtém os
     * dados mensais de vendas, calcula a previsão e exibe na tabela. Também
     * formata o texto para exibição centralizada e com duas casas decimais.
     */
    public void carregarTabelaPrevisoes() {
        try {
            List<double[]> monthlySalesData = getDadosMensais();

            // Criar o modelo da tabela com as coluna: Vendas Previstas
            DefaultTableModel model = new DefaultTableModel(new String[]{bundle.getString("expected_sales")}, 0);
            tblPrevisoes.setModel(model);

            // Fazer a previsão para o próximo mês
            double predictedSales = predictNextMonthSales(monthlySalesData);

            // Formatar o valor para duas casas decimais
            DecimalFormat df = new DecimalFormat("#.00");
            String formattedSales = df.format(predictedSales);

            // Adicionar a previsão do próximo mês na tabela
            model.addRow(new Object[]{formattedSales});

            // Centralizar o texto da coluna
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
            tblPrevisoes.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

            // Centralizar o texto do cabeçalho da coluna
            DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) tblPrevisoes.getTableHeader().getDefaultRenderer();
            headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, bundle.getString("error_fetching_data") + e.getMessage(),
                    bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
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
        jScrollPane3 = new javax.swing.JScrollPane();
        tblPrevisoes = new javax.swing.JTable();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle(bundle.getString("sales_Rep")); // NOI18N
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

        jLabel1.setText(bundle.getString("search"));
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

        btnVerMais.setText(bundle.getString("see_details")); // NOI18N
        btnVerMais.setEnabled(false);
        btnVerMais.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerMaisActionPerformed(evt);
            }
        });
        getContentPane().add(btnVerMais, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 0, 130, -1));

        grupoBotoesData.add(radioBtnAno);
        radioBtnAno.setText(bundle.getString("year"));
        radioBtnAno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioBtnAnoActionPerformed(evt);
            }
        });
        getContentPane().add(radioBtnAno, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 510, -1, -1));

        grupoBotoesData.add(radioBtnMes);
        radioBtnMes.setText(bundle.getString("month"));
        radioBtnMes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioBtnMesActionPerformed(evt);
            }
        });
        getContentPane().add(radioBtnMes, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 430, -1, -1));

        grupoBotoesData.add(radioBtnDia);
        radioBtnDia.setText(bundle.getString("day"));
        radioBtnDia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioBtnDiaActionPerformed(evt);
            }
        });
        getContentPane().add(radioBtnDia, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 360, 50, -1));

        jLabel2.setText(bundle.getString("filter_compare")); // NOI18N
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

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 320, 130, 50));

        tblPrevisoes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Mês", "Total de Vendas", "Vendas Previstas"
            }
        ));
        tblPrevisoes.setEnabled(false);
        jScrollPane3.setViewportView(tblPrevisoes);

        getContentPane().add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 377, 550, 220));

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
        carregarTabelaPrevisoes();
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
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPanel panelGraficoBarra;
    private javax.swing.JRadioButton radioBtnAno;
    private javax.swing.JRadioButton radioBtnDia;
    private javax.swing.JRadioButton radioBtnMes;
    private javax.swing.JTable tblPrevisoes;
    private javax.swing.JTable tblTotal;
    private javax.swing.JTable tblVendedores;
    private javax.swing.JTextField txtVendPesquisar;
    // End of variables declaration//GEN-END:variables
}
