package com.emergentes.controlador;
import com.emergentes.ConexionDB;
import com.emergentes.modelo.tarea;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@WebServlet(name = "MainController", urlPatterns = {"/MainController"})
public class MainController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String op;
            op = (request.getParameter("op") != null) ? request.getParameter("op") : "list";
            
            ArrayList<tarea> lista = new ArrayList <tarea>();
            
            ConexionDB canal = new ConexionDB();
            Connection conn = canal.conectar();
            PreparedStatement ps;
            ResultSet rs;
            if (op.equals("list")) {
                    String sql = "SELECT * from tareas";
                    ps = conn.prepareStatement(sql);
                    rs = ps.executeQuery();
                    
                    while (rs.next()) {
                        tarea lib = new tarea();
                        lib.setId(rs.getInt("id"));
                        lib.setTarea(rs.getString("tarea"));
                        lib.setPrioridad(rs.getInt("prioridad"));
                        lib.setCompletado(rs.getInt("completado"));
                        lista.add(lib);  
                    }
                    request.setAttribute("lista", lista);
                    request.getRequestDispatcher("index.jsp").forward(request, response);
            }
            if (op.equals("nuevo")) {
                tarea li = new tarea();
                request.setAttribute("tarea", li);
                request.getRequestDispatcher("editar.jsp").forward(request, response);
            }
            if(op.equals("editar")){
                    int id = Integer.parseInt(request.getParameter("id"));
                    String sql = "SELECT * from tareas";
                    ps = conn.prepareStatement(sql);
                    rs = ps.executeQuery();
                    tarea liba=new tarea();
                    while (rs.next()) {
                        tarea li = new tarea();
                        li.setId(rs.getInt("id"));
                        li.setTarea(rs.getString("tarea"));
                        li.setPrioridad(rs.getInt("prioridad"));
                        li.setCompletado(rs.getInt("completado"));
                        liba=li;
                    }
                request.setAttribute("tarea", liba);   
                request.getRequestDispatcher("editar.jsp").forward(request, response);
           }
            if (op.equals("eliminar")) {
                int id = Integer.parseInt(request.getParameter("id"));
               
                    String sql = "delete from tareas where id = ?";
                    ps = conn.prepareStatement(sql);
                    ps.setInt(1, id); //Parametro numero 1
                    ps.executeUpdate();
               
                response.sendRedirect("MainController");
            }
        } catch (SQLException ex) {
            System.out.println("Error al conectar: " + ex.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
            int id = Integer.parseInt(request.getParameter("id"));
            String tarea = request.getParameter("tarea");
            int prioridad = Integer.parseInt(request.getParameter("prioridad"));
            int completado = Integer.parseInt(request.getParameter("completado"));
            
            tarea l = new tarea();
            l.setId(id);
            l.setTarea(tarea);
            l.setPrioridad(prioridad);
            l.setCompletado(completado);
            
            ConexionDB canal =new ConexionDB();
            Connection conn = canal.conectar();
            PreparedStatement ps;
            ResultSet rs;
            try{
                if (id==0) {
                    String sql="insert into tareas(tarea,prioridad,completado) values(?,?,?)";
                    ps = conn.prepareStatement(sql);
                    ps.setString(1, l.getTarea());
                    ps.setInt(2, l.getPrioridad());
                    ps.setInt(3, l.getCompletado());
                    ps.executeUpdate(); 
                } else{
                    String sql="update tareas set tarea=?,prioridad=?,completado=? where id=?";
                    ps = conn.prepareStatement(sql);
                    ps.setString(1, l.getTarea());
                    ps.setInt(2, l.getPrioridad());
                    ps.setInt(3, l.getCompletado());
                    ps.setInt(4, l.getId());
                    ps.executeUpdate(); 
                }
                response.sendRedirect("MainController") ;
                }catch (SQLException ex) {
                    System.out.println("Error en SQL: " + ex.getMessage());
                } finally {
                    canal.desconectar();
                }
        } 
    }
