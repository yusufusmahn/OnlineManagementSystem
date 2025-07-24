package com.lms.utils;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.lms.utils.Env.get;

@Service
public class EmailService {

    private static final String API_KEY = get("SENDGRID_API_KEY");
    private static final String FROM_EMAIL = get("SENDGRID_FROM_EMAIL");

    public void sendEmail(String to, String subject, String content) throws IOException {
        Email from = new Email(FROM_EMAIL);
        Email toEmail = new Email(to);
        Content emailContent = new Content("text/plain", content);  // correct type
        Mail mail = new Mail(from, subject, toEmail, emailContent);

        SendGrid sg = new SendGrid(API_KEY);
        Request request = new Request();

        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sg.api(request);
        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("Body: " + response.getBody());
        System.out.println("Headers: " + response.getHeaders());
    }
}
