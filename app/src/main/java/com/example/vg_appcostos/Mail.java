package com.example.vg_appcostos;
import com.example.vg_appcostos.ModuloEquipo.Model.SolicitudListadoEquipo;
import com.example.vg_appcostos.Sistemas.Controlador.ControlDB_Usuario;
import com.example.vg_appcostos.Sistemas.Modelo.Usuario;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import javax.mail.*;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Mail {
    private String tipoConexion;

    public Mail(String tipoConexion) {
        this.tipoConexion=tipoConexion;
    }
    public  void enviarMailPorRegistroRecobro (Usuario userRemitente, String moduloApp) throws SQLException {
        //Añadimos los destinatarios al correo, Nota: Se agregan los que tienen permisos para asignar equipos en el sistema.
       // ArrayList<Usuario> listadoUsuarioAutorizaRecobro=new ControlDB_Usuario(tipoConexion).buscarUsuario_Permiso_AutorizarRecobros();
        //if(listadoUsuarioAutorizaRecobro != null){
            String remitente = "venturadatavg";  //Para la dirección nomcuenta@gmail.com
            String clave = "VG#V3ntur4D4t4!#";  //Para la dirección nomcuenta@gmail.com

            Properties props = new Properties();//System.getProperties();
            props.put("mail.smtp.host", "smtp.gmail.com");  //El servidor SMTP de Google
            props.put("mail.smtp.user", remitente);
            props.put("mail.smtp.clave", clave);    //La clave de la cuenta
            props.put("mail.smtp.auth", "true");    //Usar autenticación mediante usuario y clave
            props.put("mail.smtp.starttls.enable", "true"); //Para conectar de manera segura al servidor SMTP
            //props.put("mail.smtp.port", "587");//El puerto SMTP seguro de Google
            props.put("mail.smtp.port", "587");//El puerto SMTP seguro de Google

            Session session = Session.getDefaultInstance(props);
            Message message = new MimeMessage(session);

            try {
                message.setFrom(new InternetAddress(remitente));
               // for(Usuario user : listadoUsuarioAutorizaRecobro){
                   // message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getCorreo()));   //Se podrían añadir varios de la misma manera
                    message.setRecipients(Message.RecipientType.TO,  InternetAddress.parse("wrivas@oppgraneles.com"));   //Se podrían añadir varios de la misma manera
               // }
                message.setSubject("Registro de Recobro");
                String cuerpo="Se ha registrado un nuevo recobro en el sistema en el modulo "+moduloApp+" por "+userRemitente.getNombres() +" "+userRemitente.getApellidos()+", se debe validar para su posterior autorización o rechazo";
                System.out.println("0===============================================================================================>"+cuerpo);
                message.setText(cuerpo);
                Transport.send(message);
                /*Transport transport = session.getTransport("smtp");
                transport.connect("smtp.gmail.com", remitente, clave);
                transport.sendMessage(message, message.getAllRecipients());
                transport.close();*/
            }
            catch (Exception me) {
                me.printStackTrace();   //Si se produce un error
                String cuerpo="Se ha registrado un nuevo recobro en el sistema en el modulo "+moduloApp+" por "+userRemitente.getNombres() +" "+userRemitente.getApellidos()+", se debe validar para su posterior autorización o rechazo";
                System.out.println("2===============================================================================================>"+cuerpo);

            }
        //}
        String cuerpo="Se ha registrado un nuevo recobro en el sistema en el modulo "+moduloApp+" por "+userRemitente.getNombres() +" "+userRemitente.getApellidos()+", se debe validar para su posterior autorización o rechazo";
        System.out.println("1===============================================================================================>"+cuerpo);

    }
}
