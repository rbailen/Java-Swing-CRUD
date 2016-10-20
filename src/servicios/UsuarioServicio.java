package servicios;

import dao.UsuarioDAO;
import java.sql.ResultSet;
import model.Usuario;

public class UsuarioServicio {
    private final UsuarioDAO usuarioDAO;
    
    public UsuarioServicio(){
        usuarioDAO = new UsuarioDAO();
    }
    
    public Usuario login(String email, String password){
        return usuarioDAO.login(email, password);
    }
    
    public Usuario buscarUsuarioPorID(int id){
        return usuarioDAO.buscarUsuarioPorID(id);
    }
    
    public ResultSet buscaTodosRS(){
        return usuarioDAO.buscaTodosRS();
    }
    
    public void insertar(Usuario usuario){
        usuarioDAO.insertar(usuario);
    }
    
    public void actualizar(Usuario usuario){
        usuarioDAO.actualizar(usuario);
    }
    
    public void eliminar(int id){
        usuarioDAO.eliminar(id);
    }
}
