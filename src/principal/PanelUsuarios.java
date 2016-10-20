package principal;

import com.sun.glass.events.KeyEvent;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import model.Usuario;
import servicios.UsuarioServicio;
import util.ButtonColumn;
import util.DbUtils;
import util.JTextFieldLimit;
import util.TwoColumnLayoutWithHeader;

public final class PanelUsuarios extends javax.swing.JFrame {
    
    private final String nombreUsuario;
    private UsuarioServicio usuarioServicio;

    /**
     * Creates new form PanelUsuarios
     * @throws java.sql.SQLException
     */
    
    Action visualizarUsuario = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            /* Obtenemos la fila seleccionada de la tabla */
            int fila = jTable1.getSelectedRow();
            
            /* La columna siempre será la primera */
            int columna = 0;
            
            /* A partir de la fila obtenemos el ID del usuario de dicha fila*/
            int idUsuario = Integer.parseInt((String) jTable1.getValueAt(fila, columna));
            
            usuarioServicio = new UsuarioServicio();
            Usuario usuario = usuarioServicio.buscarUsuarioPorID(idUsuario);
            
            JFrame ventana = new JFrame();
            
            JOptionPane.showMessageDialog(ventana, 
                    "ID: " + idUsuario + "\n" + 
                    "Nombre: " + usuario.getNombre() + "\n" + 
                    "Primer Apellido: " + usuario.getApellido1() +  "\n" + 
                    "Segundo Apellido: " + usuario.getApellido2() + "\n" + 
                    "DNI: " + usuario.getDni() + "\n" +
                    "Email: " + usuario.getEmail() + "\n" +
                    "Password: " + usuario.getPassword(), "Visualizar usuario", 1);
        }
    };

    Action editarUsuario = new AbstractAction(){
        @Override
        public void actionPerformed(ActionEvent e) {
            /* Obtenemos la fila seleccionada de la tabla */
            int fila = jTable1.getSelectedRow();
            
            /* La columna siempre será la primera */
            int columna = 0;
            
            /* A partir de la fila obtenemos el ID del usuario de dicha fila*/
            int idUsuario = Integer.parseInt((String) jTable1.getValueAt(fila, columna));
            
            Usuario usuario = usuarioServicio.buscarUsuarioPorID(idUsuario);
            
            JTextField nombre = new JTextField(20);
            nombre.setText(usuario.getNombre());
            
            JTextField primerApellido = new JTextField(20);
            primerApellido.setText(usuario.getApellido1());
   
            JTextField segundoApellido = new JTextField(20);
            segundoApellido.setText(usuario.getApellido2());

            JTextField dni = new JTextField(20);
             /* Clase auxiliar para limitar la introducción de caracteres */
            dni.setDocument(new JTextFieldLimit(9));
            dni.setText(usuario.getDni());

            JTextField email = new JTextField(20);
            email.setText(usuario.getEmail());
            
            JPasswordField password = new JPasswordField(20);
            password.setText(usuario.getPassword());
            
            /* Clase auxiliar para pintar la ventana de edición */
            TwoColumnLayoutWithHeader util = new TwoColumnLayoutWithHeader();
            
            JComponent[] components = {nombre, primerApellido, segundoApellido, dni, email, password};

            String[] labels = {"Nombre:", "Primer Apellido:", "Segundo Apellido:", "DNI:", "Email:","Password:"};
                
            JComponent labelsAndFields = util.getTwoColumnLayout(labels,components);
            
            JComponent form = new JPanel(new BorderLayout(5,5));
            form.add(labelsAndFields, BorderLayout.CENTER);
            
            Object[] opciones = {"Guardar", "Cancelar"};
                    
            int selection = JOptionPane.showOptionDialog(null, form, "Editar usuario", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, opciones, opciones[0]);
            
            /* Actualizamos el usuario en caso de seleccionar la opción de guardar */
            if(selection == JOptionPane.OK_OPTION){
                try {
                    /* Recogemos los datos */
                    if(dni.getText().length() == 9){
                        usuario.setNombre(nombre.getText());
                        usuario.setApellido1(primerApellido.getText());
                        usuario.setApellido2(segundoApellido.getText());
                        usuario.setDni(dni.getText());
                        usuario.setEmail(email.getText());
                        usuario.setPassword(new String(password.getPassword()));

                         /* Actualizamos el usuario */
                        usuarioServicio.actualizar(usuario);

                        /* Recargamos la tabla mediante una clase auxiliar y la mostramos */
                        jTable1.setModel(DbUtils.resultSetToTableModel(usuarioServicio.buscaTodosRS()));
                        mostrarTablaUsuarios();
                    }else{
                         JOptionPane.showMessageDialog(jPanel1, "DNI no válido",  "Error", JOptionPane.ERROR_MESSAGE);
                    } 
                } catch (SQLException ex) {
                    Logger.getLogger(PanelUsuarios.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    };
    
    Action eliminarUsuario = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                /* Obtenemos la fila seleccionada de la tabla */
                int fila = jTable1.getSelectedRow();

                /* La columna siempre será la primera (ID) */
                int columna = 0;

                /* A partir de la fila obtenemos el ID del usuario de dicha fila*/
                int idUsuario = Integer.parseInt((String) jTable1.getValueAt(fila, columna));
                
                usuarioServicio.eliminar(idUsuario);
                
                /* Recargamos la tabla mediante una clase auxiliar y la mostramos */
                jTable1.setModel(DbUtils.resultSetToTableModel(usuarioServicio.buscaTodosRS()));
                mostrarTablaUsuarios();
                
            } catch (SQLException ex) {
                Logger.getLogger(PanelUsuarios.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    };
    
    public void mostrarTablaUsuarios() throws SQLException{
        usuarioServicio = new UsuarioServicio();
        
        ResultSet rs = usuarioServicio.buscaTodosRS();
        
        ResultSetMetaData rsmd = rs.getMetaData();
        DefaultTableModel tm = (DefaultTableModel) jTable1.getModel();
        
        tm.setColumnCount(0);
        
        /* Creamos las columnas de la tabla */
        for(int i = 1; i <= rsmd.getColumnCount(); i++){
            tm.addColumn(rsmd.getColumnName(i));
        }
        
        tm.addColumn("");
        tm.addColumn("");
        tm.addColumn("");

        tm.setRowCount(0);
        
        /* Bucle para cada resultado de la consulta */
        while(rs.next()){
            /* Array para cada fila de la tabla */
            String[] data = new String[rsmd.getColumnCount() + 3];
            
            /* Se rellena cada posición del array con una de las columnas de la tabla en base de datos */
            for(int i = 0; i < rsmd.getColumnCount(); i++){
                data[i] = rs.getString(i + 1);
                
                data[rsmd.getColumnCount()] = "Visualizar";
                data[rsmd.getColumnCount()+1] = "Editar";
                data[rsmd.getColumnCount()+2] = "Eliminar";
            }
            
            /* Botones */
            ButtonColumn botonVisualizar = new ButtonColumn(jTable1, visualizarUsuario, 7);
            botonVisualizar.setMnemonic(KeyEvent.VK_D);
            
            ButtonColumn botonEditar = new ButtonColumn(jTable1, editarUsuario, 8);
            botonEditar.setMnemonic(KeyEvent.VK_D);
            
            ButtonColumn botonEliminar = new ButtonColumn(jTable1, eliminarUsuario, 9);
            botonEliminar.setMnemonic(KeyEvent.VK_D);
            
            /* Se añade a la tabla del modelo la fila */
            tm.addRow(data);
        }
        
        tm.fireTableDataChanged();
    }
 
    public PanelUsuarios(String usuario) throws SQLException{
        initComponents();
        
        /* Ventana en pantalla completa */
        this.setExtendedState(MAXIMIZED_BOTH);
        
        nombreUsuario = usuario;
        jLabel1.setText(jLabel1.getText() + " " + nombreUsuario);
        
        mostrarTablaUsuarios();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(jTable1);

        jButton1.setText("Insertar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Cerrar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel1.setText("Bienvenido/a");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton2)
                .addGap(58, 58, 58))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 860, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 13, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        jPanel1.setVisible(false);
        this.dispose();

        new Login().setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        /* Insertar usuario */
        JTextField nombre = new JTextField(20);
        JTextField primerApellido = new JTextField(20);
        JTextField segundoApellido = new JTextField(20);
        
        JTextField dni = new JTextField(9);
        /* Clase auxiliar para limitar la introducción de caracteres */
        dni.setDocument(new JTextFieldLimit(9));
        
        JTextField email = new JTextField(20);
        JPasswordField password = new JPasswordField(20);
            
        /* Clase auxiliar para pintar la ventana de inserción */
        TwoColumnLayoutWithHeader util = new TwoColumnLayoutWithHeader();
            
        JComponent[] components = {nombre, primerApellido, segundoApellido, dni, email, password};

        String[] labels = {"Nombre:", "Primer Apellido:", "Segundo Apellido:", "DNI:", "Email:","Password:"};
                
        JComponent labelsAndFields = util.getTwoColumnLayout(labels,components);
            
        JComponent form = new JPanel(new BorderLayout(5,5));
        form.add(labelsAndFields, BorderLayout.CENTER);
            
        Object[] opciones = {"Guardar", "Cancelar"};
                    
        int selection = JOptionPane.showOptionDialog(null, form, "Insertar usuario", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, opciones, opciones[0]);
            
        /* Insertamos el usuario en caso de seleccionar la opción de guardar */
        if(selection == JOptionPane.OK_OPTION){
            try {
                /* Recogemos los datos */
                Usuario usuario = new Usuario();

                if(dni.getText().length() == 9){
                    usuario.setNombre(nombre.getText());
                    usuario.setApellido1(primerApellido.getText());
                    usuario.setApellido2(segundoApellido.getText());
                    usuario.setDni(dni.getText());
                    usuario.setEmail(email.getText());
                    usuario.setPassword(new String(password.getPassword()));
                    
                     /* Insertamos el usuario */
                    usuarioServicio.insertar(usuario);

                    /* Recargamos la tabla mediante una clase auxiliar y la mostramos */
                    jTable1.setModel(DbUtils.resultSetToTableModel(usuarioServicio.buscaTodosRS()));
                    mostrarTablaUsuarios();
                }else{
                     JOptionPane.showMessageDialog(jPanel1, "DNI no válido",  "Error", JOptionPane.ERROR_MESSAGE);
                }   
            } catch (SQLException ex) {
                Logger.getLogger(PanelUsuarios.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PanelUsuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PanelUsuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PanelUsuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PanelUsuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new PanelUsuarios("").setVisible(true);
                } catch (SQLException ex) {
                    Logger.getLogger(PanelUsuarios.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
