package com.example.vg_appcostos;

import java.util.Properties;
import android.os.AsyncTask;
import android.os.Message;
//import android.se.omapi.Session;
//import java.net.PasswordAuthentication;
import com.example.vg_appcostos.Sistemas.Controlador.ControlDB_Usuario;
import com.example.vg_appcostos.Sistemas.Modelo.Usuario;

import javax.mail.PasswordAuthentication;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMessage;
import javax.mail.*;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EnviarGmail {
    private String correoRemitente;
    private String passwordCorreoRemitente;
    private  String typeConnection;
    private ArrayList<Usuario> listado = null;

    public EnviarGmail(String typeConnection) {
        this.correoRemitente="venturadatavg";
        this.passwordCorreoRemitente="VG#V3ntur4D4t4!#";
        this.typeConnection= typeConnection;
        /*try {
            listado = new ControlDB_Usuario(typeConnection).buscarUsuario_Permiso_AutorizarRecobros();
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
    }

    public void enviarNotificaciónRecobro() {
        //if (listado != null) {
            Properties props = new Properties();

            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.user", correoRemitente);
            props.put("mail.smtp.clave", passwordCorreoRemitente);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.port", "587");

            Session session = javax.mail.Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(correoRemitente, passwordCorreoRemitente);
                        }
                    });
            try {
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(correoRemitente));
                /*for (Usuario us : listado) {
                    //if (!us.getCorreo().equals("")) {
                        if (us.getCorreo() != null) {
                            message.setRecipients(MimeMessage.RecipientType.TO, us.getCorreo());
                            //System.out.println(us.getCorreo() + "=====================================================###########################################################################");
                        }
                    //}

                }*/
                message.setRecipients(MimeMessage.RecipientType.TO, "wrivas@oppgraneles.com" /*InternetAddress.parse("wrivas@oppgraneles.com")*/);
                message.setSubject("YA");
                message.setText("YA QUE");

                System.out.println("###########################################################################");
                Transport.send(message);


            } catch(AddressException e){
                e.printStackTrace();
            } catch(MessagingException e){
                e.printStackTrace();
            }
       // }
    }
}
