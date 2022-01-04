package com.example.vg_appcostos;

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
import javax.mail.internet.MimeMessage;
import javax.mail.*;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailJob extends AsyncTask<MailJob.Mail,Void,Void> {
    private final String user;
    private final String pass;

    /*public MailJob(String user, String pass) {
        super();
        this.user=user;
        this.pass=pass;
    }*/
    public MailJob() {
        super();
        this.user="venturadatavg";
        this.pass="VG#V3ntur4D4t4!#";
    }

    @Override
    protected Void doInBackground(Mail... mails) {
        Properties props = new Properties();

        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.user", user);
        props.put("mail.smtp.clave", pass);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.port", "587");



        // Session session = Session.getInstance(props);
        Session session =javax.mail.Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, pass);
                    }
                });
        //Session sessions = Session.getInstance(props);
        for (Mail mail:mails) {
           // try {

                try {
                    MimeMessage message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(user));
                    /*ArrayList<Usuario> listado = new ControlDB_Usuario(mail.typeConnection).buscarUsuario_Permiso_AutorizarRecobros();
                    if(listado != null) {
                        for (Usuario us : listado) {
                            if(!us.getCorreo().equals("")) {
                                if (us.getCorreo() != null) {
                                    message.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(us.getCorreo()));
                                }
                            }
                        }
                    }*/
                    message.setRecipients(MimeMessage.RecipientType.TO,"wrivas@oppgraneles.com" /*InternetAddress.parse("wrivas@oppgraneles.com")*/);
                    message.setSubject(mail.subject);
                    message.setText(mail.content);

                    System.out.println("###########################################################################");
                    Transport.send(message);
                } catch (MessagingException e) {
                    System.out.println("MailJob");
                }
            //} catch (SQLException e) {
              //  e.printStackTrace();
            //}

        }
        return null;
    }


    public static class Mail{
        private final String typeConnection;
        private final String subject;
        private final String content;
        //private final String from;
        //private final String to;

        public Mail( /*String to, */String typeConnection,String subject, String content){
            this.typeConnection=typeConnection;
            this.subject=subject;
            this.content=content;
            //this.from=from;
            //this.to=to;
        }
    }
}