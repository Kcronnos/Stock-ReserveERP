/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package br.com.stockreserve.telas;

import br.com.stockreserve.dal.ModuloConexao;
import java.awt.Color;
import java.awt.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.lang.model.util.ElementFilter;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import net.proteanit.sql.DbUtils;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Classe que representa a tela de status do estoque, exibindo informações sobre
 * os produtos em estoque e seu status atual.
 *
 * @author Feliipee013
 * @version 2.0
 */
public class TelaEstoqueStatus extends javax.swing.JInternalFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    ResourceBundle bundle;

    /**
     * Cria uma nova instância de TelaEstoqueStatus. Inicializa os componentes
     * da interface e estabelece a conexão com o banco de dados. O idioma da
     * interface é definido com base na seleção de idioma do usuário.
     *
     * @author Feliipee013
     * @version 2.0
     */
    public TelaEstoqueStatus() {
        Locale locale;
        if (LanguageSelection.selectedLanguage) {
            locale = Locale.of("en", "US");
        } else {
            locale = Locale.of("pt", "BR");
        }
        bundle = ResourceBundle.getBundle("br.com.stockreserve.erp", locale);

        initComponents();
        setTitle(bundle.getString("stock_status"));
        conexao = ModuloConexao.conector();
    }

    /**
     * Método que verifica o estoque de produtos e atualiza a tabela de exibição
     * com as informações, incluindo a quantidade, vencimento e o status do
     * produto (ex.: Vazio, Precisa Abastecer). Configura renderizadores
     * personalizados para colorir as colunas `STATUS` e `VENCIMENTO` conforme a
     * condição de cada produto.
     *
     * @throws SQLException caso ocorra algum erro durante a consulta ao banco
     * de dados.
     *
     * @author Feliipee013
     * @version 2.0
     */
    public void verificarEstoque() {
        String sql;
        if (LanguageSelection.selectedLanguage) {
            // SQL para selecionar os produtos e adicionar uma coluna `STATUS` com base na quantidade
            sql = """
                SELECT idproduto AS ID, 
                nomeproduto AS NOME, 
                preco / 5.78 AS PREÇO, 
                quantidade AS QUANTIDADE, 
                limite_minimo AS LIMITE_MÍNIMO, 
                vencimento AS VENCIMENTO,
                CASE 
                    WHEN quantidade = 0 THEN 'Vazio' 
                    WHEN quantidade < limite_minimo THEN 'Precisa Abastecer' 
                    ELSE 'OK' 
                END AS STATUS
        FROM tbprodutos;
                """;
        } else {
            sql = """
        SELECT idproduto AS ID, 
               nomeproduto AS NOME, 
               preco AS PREÇO, 
               quantidade AS QUANTIDADE, 
               limite_minimo AS LIMITE_MÍNIMO, 
               vencimento AS VENCIMENTO,
               CASE 
                   WHEN quantidade = 0 THEN 'Vazio' 
                   WHEN quantidade < limite_minimo THEN 'Precisa Abastecer' 
                   ELSE 'OK' 
               END AS STATUS
        FROM tbprodutos;
        """;
        }
        // SQL para selecionar os produtos e adicionar uma coluna `STATUS` com base na quantidade

        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tblEstoStatus.setModel(DbUtils.resultSetToTableModel(rs));

            // Aplica o renderizador colorido à coluna `status`
            tblEstoStatus.getColumnModel().getColumn(tblEstoStatus.getColumnCount() - 1)
                    .setCellRenderer(new StatusCellRenderer());

            // Aplica o renderizador colorido à coluna `VENCIMENTO`
            int vencimentoColIndex = tblEstoStatus.getColumnModel().getColumnIndex("VENCIMENTO");
            tblEstoStatus.getColumnModel().getColumn(vencimentoColIndex).setCellRenderer(new VencimentoCellRenderer());

            atualizarNomesColunas();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    /**
     * Atualiza os nomes das colunas na interface da tabela de estoque com as
     * strings de idioma apropriadas, carregadas de um arquivo de recursos
     * (bundle). Este método garante que os títulos das colunas estejam no
     * idioma correto, conforme definido na aplicação.
     *
     * @author ElinaldoLopes
     * @version 2.0
     */
    private void atualizarNomesColunas() {
        tblEstoStatus.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("prod_id"));
        tblEstoStatus.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("prod_name"));
        tblEstoStatus.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("prod_price"));
        tblEstoStatus.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("amount"));
        tblEstoStatus.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("min_limit"));
        tblEstoStatus.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("expiry"));
        tblEstoStatus.getColumnModel().getColumn(6).setHeaderValue(bundle.getString("stock_status"));
        tblEstoStatus.getTableHeader().repaint(); // Re-renderiza o cabeçalho para exibir as novas strings
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
                    Date vencimentoDate = DATE_FORMAT.parse(value.toString());
                    Date currentDate = new Date();
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
     * Este método é chamado a partir do construtor para inicializar o
     * formulário. ATENÇÃO: Não modifique este código. O conteúdo deste método é
     * sempre regenerado pelo Editor de Formulários.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblEstoStatus = new javax.swing.JTable();

        setClosable(true);
        setIconifiable(true);
        setTitle(bundle.getString("stock_status"));
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
                formInternalFrameDeiconified(evt);
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
        });

        tblEstoStatus = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int ColIndex){
                return false;
            }
        };
        tblEstoStatus.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", bundle.getString("name"), bundle.getString("price"), bundle.getString("amount"), bundle.getString("min_limit"), bundle.getString("expiry"), "STATUS"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblEstoStatus);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 697, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 4, Short.MAX_VALUE))
        );

        setBounds(279, 306, 721, 325);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Método chamado quando a janela interna é aberta. Chama o método
     * {@link #verificarEstoque()} para atualizar o estoque.
     *
     * @param evt Evento gerado ao abrir a janela interna.
     *
     */
    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        //Chamando o método verificar estoque
        verificarEstoque();
    }//GEN-LAST:event_formInternalFrameOpened

    /**
     * Método chamado quando a janela interna é restaurada (deiconificada).
     * Chama o método {@link #verificarEstoque()} para atualizar o estoque.
     *
     * @param evt Evento gerado ao restaurar a janela interna.
     * 
     */
    private void formInternalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameDeiconified
        //Chamando o método verificar estoque
        verificarEstoque();
    }//GEN-LAST:event_formInternalFrameDeiconified


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblEstoStatus;
    // End of variables declaration//GEN-END:variables
}
