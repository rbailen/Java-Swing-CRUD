package dao;

import db.DB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Usuario;

public class UsuarioDAO {
    private static final String SQL_LOGIN="SELECT * FROM Usuario WHERE email=? AND password=?";
    private static final String SQL_USUARIOS="SELECT * FROM Usuario";
    private static final String SQL_BUSCAR_USUARIO_POR_ID = "SELECT * FROM Usuario WHERE id=?";
    private static final String SQL_MAX_ID = "SELECT MAX(id) as maxId FROM Usuario";
    private static final String SQL_CREA="INSERT INTO Usuario (id,nombre,primerapellido,segundoapellido,dni,email,password) VALUES (?,?,?,?,?,?,?)";
    private static final String SQL_ACTUALIZA="UPDATE Usuario SET nombre=?,primerapellido=?,segundoapellido=?,dni=?,email=?,password=? WHERE id=?";
    private static final String SQL_BORRA="DELETE FROM Usuario WHERE id=?";
    
    private static Usuario usuarioMapper(ResultSet rs) throws SQLException {
        Usuario u=null;
        
        try {
            u=new Usuario();
            
            u.setId(Integer.parseInt(rs.getString("id")));
            u.setNombre(rs.getString("nombre"));
            u.setApellido1(rs.getString("primerapellido"));
            u.setApellido2(rs.getString("segundoapellido")); 
            u.setDni(rs.getString("dni"));
            u.setEmail(rs.getString("email"));
            u.setPassword(rs.getString("password"));
        } catch (Exception ex) {
             Logger.getLogger(UsuarioDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }

        return u;
    } 
    
    public Usuario buscarUsuarioPorID(int id){
        Usuario u = null;
     
        DB db = new DB();
        db.conectar();
        
        try {          
            Connection conn = db.getConn();
              
            PreparedStatement stmn = conn.prepareStatement(SQL_BUSCAR_USUARIO_POR_ID);
                        
            stmn.setInt(1, id);
            
            ResultSet rs = stmn.executeQuery();
                    
            if (rs.next()) {
                u = usuarioMapper(rs);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UsuarioDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        
        db.desconectar();
        
        return u;
    }
    
    public Usuario login(String email, String password) {
        Usuario u = null;
     
        DB db = new DB();
        db.conectar();
        
        try {          
            Connection conn = db.getConn();
              
            PreparedStatement stmn = conn.prepareStatement(SQL_LOGIN);
                        
            stmn.setString(1, email);
            stmn.setString(2, password);
            
            ResultSet rs = stmn.executeQuery();
                    
            if (rs.next()) {
                u = usuarioMapper(rs);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UsuarioDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        
        db.desconectar();
        
        return u;
    }
    
    public List<Usuario> buscaTodos() {
        List<Usuario> usuarios=new ArrayList<>();
        
        DB db = new DB();
        db.conectar();
        
        try{
            Connection conn = db.getConn();
                
            Statement stmn=conn.createStatement();
            ResultSet rs=stmn.executeQuery(SQL_USUARIOS);

            while (rs.next()) {
                usuarios.add(usuarioMapper(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(UsuarioDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        
        db.desconectar();
        
        return usuarios;
    }
     
    public ResultSet buscaTodosRS() {
        DB db = new DB();
        db.conectar();
        
        ResultSet rs = null;
        
        try{
            Connection conn = db.getConn();
                
            Statement stmn=conn.createStatement();
            rs=stmn.executeQuery(SQL_USUARIOS);
            
        } catch (SQLException ex) {
            Logger.getLogger(UsuarioDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        
        db.desconectar();
        
        return rs;
    }
      
    public void insertar(Usuario usuario){
        
        DB db = new DB();
        db.conectar();
        
        ResultSet rs = null;
        
        int maxId = 0;
        
        try{
            Connection conn = db.getConn();
            
            Statement stmn2 = conn.createStatement();
            rs=stmn2.executeQuery(SQL_MAX_ID);
            
            if (rs.next()) {
                maxId = rs.getInt("maxId");
            }
            
            PreparedStatement stmn = conn.prepareStatement(SQL_CREA);
            
            stmn.setString(1, Integer.toString(maxId + 1));
            stmn.setString(2, usuario.getNombre());
            stmn.setString(3, usuario.getApellido1());
            stmn.setString(4, usuario.getApellido2());
            stmn.setString(5, usuario.getDni());
            stmn.setString(6, usuario.getEmail());
            stmn.setString(7, usuario.getPassword());
            
            stmn.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.getLogger(UsuarioDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        
        db.desconectar();
    }
    
    public void actualizar(Usuario usuario){
        DB db = new DB();
        db.conectar();
        
        try {
            Connection conn = db.getConn();
         
            PreparedStatement stmn=conn.prepareStatement(SQL_ACTUALIZA);
        
            stmn.setString(1, usuario.getNombre());
            stmn.setString(2, usuario.getApellido1());
            stmn.setString(3, usuario.getApellido2());
            stmn.setString(4, usuario.getDni());
            stmn.setString(5, usuario.getEmail());
            stmn.setString(6, usuario.getPassword());
            stmn.setString(7, Integer.toString(usuario.getId()));
            
            stmn.executeUpdate();
         } catch (Exception ex) {
            Logger.getLogger(UsuarioDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
         }   
        
        db.desconectar();
    }
    
    public void eliminar(int id){
        DB db = new DB();
        db.conectar();
        
        Usuario usuario = buscarUsuarioPorID(id);
        
        try {
            Connection conn = db.getConn();
            
            PreparedStatement stmn=conn.prepareStatement(SQL_BORRA);
        
            stmn.setString(1, Integer.toString(usuario.getId()));
            
            stmn.executeUpdate();  
        } catch (Exception ex) {
            Logger.getLogger(UsuarioDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        
        db.desconectar();
    }
}
