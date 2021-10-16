package org.thshsh.crypt.serv;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thshsh.crypt.User;
import org.thshsh.crypt.serv.UserService.ClasspathDataSource;
import org.thshsh.util.MapUtils;

@Service
public class MailService {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);
	
	@Autowired
	JavaMailSender mailSender;
	
	@Value("${app.url}")
	String baseUrl;
	
	//public static final String CONTENT_PLACEHOLDER = "<!--CONTENT-->";
	
	public void sendAccountConfirmEmail(User user, String token) {
		
		//htmlText = String.format(htmlText, confirmToken,baseUrl+"login?confirm="+confirmToken);
		//String url = baseUrl+"login?confirm="+confirmToken;
		Map<String,Object> context = MapUtils.createHashMap("confirm.token", token, "app.url",baseUrl);
		sendMailFromResource(user, "email-confirm.html", context);
	}
	
	public void sendMailFromResource(User user, String contentResource,Map<String,Object> context) {
		
		try {
			
			String content = createEmailContent(contentResource, context);
			LOGGER.info("content: {}",content);
			sendMailFromString(user,content);
			
		} 
		catch (IOException e) {
			throw new IllegalStateException(e);
		}
		
	}
	
	public void sendMailFromString(User user, String contentHtml) {
		
		try {	
			
			LOGGER.info("contentHtml: {}",contentHtml);
			
			Map<String,Object> context = Collections.singletonMap("content.html", contentHtml);
			String htmlText = createEmailContent("email-outer.html", context);
			
			LOGGER.info("htmlText: {}",htmlText);
			
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
			
			MimeMultipart multipart = new MimeMultipart("related");

			BodyPart messageBodyPart = new MimeBodyPart();
			//String htmlText = IOUtils.toString(UserService.class.getResourceAsStream("email-outer.html"),Charset.defaultCharset());
			
			//htmlText = htmlText.replace(CON, replacement)
			//htmlText = String.format(htmlText, confirmToken,baseUrl+"login?confirm="+confirmToken);
			
			messageBodyPart.setContent(htmlText, "text/html");
			// add it
			multipart.addBodyPart(messageBodyPart);
			//Next add the image by creating a Datahandler as follows:
	
			
			// second part (the image)
			MimeBodyPart imageBodyPart = new MimeBodyPart();
			DataSource fds = new ClasspathDataSource(UserService.class,"cryptools-logo.png");
			
			//DataSource fds = new StreamDataSource(UserService.class.getResourceAsStream("cryptools-logo.png"), "cryptools-logo.png");
	
			imageBodyPart.setDataHandler(new DataHandler(fds));
			//imageBodyPart.setHeader("Content-ID", "logoimage");
			imageBodyPart.setContentID("<logoimage>");
			//imageBodyPart.setFileName("cryptools-logo.png");
			imageBodyPart.setDisposition(MimeBodyPart.INLINE);
			
			
			multipart.addBodyPart(imageBodyPart);
			//Next set the multipart in the message as follows:
			mimeMessage.setContent(multipart);
			
			

			//String htmlMsg = String.format(CONFIRMATION_TEXT, confirmToken,baseUrl+"login?confirm="+confirmToken);
			//mimeMessage.setContent(htmlMsg, "text/html");
		
			//helper.setText(htmlMsg, true); // Use this or above line.
			
			helper.setTo(user.getEmail());
			String subject = "Cryptools Account Confirmation";
			/*if(u.getUserName()!=null) {
				subject+=u.getUserName();
			}
			else {
				subject += u.getEmail();
			}*/
			helper.setSubject(subject);
			helper.setFrom("cryptools@thshsh.org");
			
			mailSender.send(mimeMessage);
		} 
		catch (Exception e) {
			LOGGER.warn("error",e);
			throw new IllegalStateException(e);
		}
	}
	
	public String createEmailContent(String resourceName,Map<String,Object> contextMap) throws IOException {
		
        String htmlText = IOUtils.toString(MailService.class.getResourceAsStream(resourceName),Charset.defaultCharset());
        LOGGER.info("resource text: {}",htmlText);
        
        LOGGER.info("contextMap: {}",contextMap);

        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(htmlText, new TemplateParserContext());
        StandardEvaluationContext context = new StandardEvaluationContext();
        
        if(contextMap != null) {
        	context.setRootObject(contextMap);
        }

        String result = expression.getValue(context,String.class);
        LOGGER.info("result text: {}",result);
        
        return result;
		
        
	}

}
