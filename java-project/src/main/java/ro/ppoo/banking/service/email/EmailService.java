//package ro.ppoo.banking.service.email;
//
//import javax.mail.*;
//import javax.mail.internet.*;
//import java.util.Properties;
//
//public class EmailService {
//
////    private static final String FROM_EMAIL = "noreply@banca-ppoo.ro";
////    private static final String FROM_PASSWORD = "parolaAplicatiei";
////
////    public void trimiteAcordGdpr(String toEmail, String numeClient) {
////        String subject = "Acord GDPR - Banca Virtuală PPOO";
////        String body = """
////                Bună, %s!
////
////                Pentru a respecta reglementările GDPR, te rugăm să confirmi că ești de acord
////                cu prelucrarea datelor tale personale (inclusiv CNP) în scopul gestionării conturilor bancare.
////
////                Te rugăm să bifezi căsuța 'Accept GDPR' în aplicația ta de client.
////
////                Mulțumim,
////                Echipa Banca PPOO
////                """.formatted(numeClient);
////
////        Properties props = new Properties();
////        props.put("mail.smtp.auth", "true");
////        props.put("mail.smtp.starttls.enable", "true");
////        props.put("mail.smtp.host", "smtp.gmail.com");
////        props.put("mail.smtp.port", "587");
////
////        Session session = Session.getInstance(props, new Authenticator() {
////            protected PasswordAuthentication getPasswordAuthentication() {
////                return new PasswordAuthentication(FROM_EMAIL, FROM_PASSWORD);
////            }
////        });
////
////        try {
////            Message message = new MimeMessage(session);
////            message.setFrom(new InternetAddress(FROM_EMAIL));
////            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
////            message.setSubject(subject);
////            message.setText(body);
////            Transport.send(message);
////        } catch (MessagingException e) {
////            throw new RuntimeException("Eroare la trimiterea emailului: " + e.getMessage());
////        }
////    }
//}
