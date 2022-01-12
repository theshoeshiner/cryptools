package org.thshsh.crypt.serv;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thshsh.crypt.Portfolio;
import org.thshsh.crypt.PortfolioAlert;
import org.thshsh.crypt.User;
import org.thshsh.crypt.serv.UserService.ClasspathDataSource;
import org.thshsh.crypt.web.AppConfiguration;
import org.thshsh.util.MapUtils;

@Service
public class MailService {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);
	
	@Autowired
	JavaMailSender mailSender;
	
	@Value("${app.url}")
	String baseUrl;
	
	@Autowired
	AppConfiguration appConfig;
	//public static final String CONTENT_PLACEHOLDER = "<!--CONTENT-->";
	
	public void sendAccountConfirmEmail(User user, String token) {
		
		Map<String,Object> context = createContext( MapUtils.createHashMap("token", token));
		sendMailFromResource(user, "email-confirm.html", context,"Cryptools Account Confirmation");
	}
	
	public void sendPortfolioAlert(PortfolioAlert alert) {
		
		Portfolio p = alert.getPortfolio();


		//String subject = "Portfolio '' Imbalance Alert: "+format.format(history.getMaxToTriggerPercent().doubleValue());
		//String subject = String.format(subjectFormat, p.getName(),(int)(history.getMaxToTriggerPercent().doubleValue()*100));
		String subject = "Cryptools Portfolio Alert: "+p.getName(); 
		//String text = "Total Imbalance: "+format.format(history.getTotalImbalance().doubleValue()*100);\


		StringBuilder assets = new StringBuilder();
		p.getLatest().getEntries().forEach(entry -> {
			Map<String,Object> c = createContext(MapUtils.createHashMap("entry", entry));
			String content = createEmailContent("email-alert-entry.html", c);
			assets.append(content);
		});
		
		//Map<String,Object> context = MapUtils.createHashMap("portfolio", p,"summary",assets.toString());
		
		Map<String,Object> context = createContext(MapUtils.createHashMap("portfolio", p,"summary",assets.toString()));

		sendMailFromResource(p.getUser(), "email-alert.html", context, subject); 
		
		/*StringBuilder emailText = new StringBuilder();
		emailText.append(String.format(textFormat, NumberUtils.BigDecimalToPercentInt(history.getTotalAdjustPercent())));
		
		history.getEntries().forEach(entry -> {
			LOGGER.info("entry: {}",entry.getThresholdPercent());
			if(entry.getToTriggerPercent().compareTo(BigDecimal.ONE) > 0) {
				String entryText = String.format(entryFormat, entry.getCurrency().getKey(),NumberUtils.BigDecimalToPercentInt(entry.getToTriggerPercent()));
				emailText.append("\n");
				emailText.append(entryText);
			}
		});
		
		//String text = ;
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("cryptools@thshsh.org");
		message.setTo("dcwatson84@gmail.com");
		message.setSubject(subject);
		message.setText(emailText.toString());
		mailSender.send(message);*/
		
	}
	
	public Map<String,Object> createContext(Map<String,Object> add){
		String timestamp = DateTimeFormatter.ISO_DATE_TIME.format(ZonedDateTime.now());
		Map<String,Object> context = MapUtils.createHashMap("app",appConfig,"timestamp",timestamp);
		context.putAll(add);
		LOGGER.info("created context: {}",context.keySet());
		return context;
	}
	
	public void sendMailFromResource(User user, String contentResource,Map<String,Object> context,String subject) {

		LOGGER.info("context: {}",context.keySet());
		String content = createEmailContent(contentResource, context);
		//LOGGER.info("content: {}",content);
		sendMailFromString(user,content,subject);

	}
	 
	public void sendMailFromString(User user, String contentHtml,String subject) {
		
		try {	
			
			LOGGER.trace("contentHtml: {}",contentHtml);
			
			Map<String,Object> context = createContext(Collections.singletonMap("content", contentHtml));
			String htmlText = createEmailContent("email-outer.html", context);
			
			LOGGER.trace("htmlText: {}",htmlText);
			
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
			
			mimeMessage.saveChanges();
			String id = "cryptools-alert+"+System.currentTimeMillis()+"@thshsh.org";
			mimeMessage.setHeader("Message-ID", id);
			mimeMessage.setHeader("References", id);
			mimeMessage.setHeader("X-Entity-Ref-ID", id);
			
			
			//mimeMessage.setReplyTo(new Addressre);
			

			//String htmlMsg = String.format(CONFIRMATION_TEXT, confirmToken,baseUrl+"login?confirm="+confirmToken);
			//mimeMessage.setContent(htmlMsg, "text/html");
		
			//helper.setText(htmlMsg, true); // Use this or above line.
			
			helper.setTo(user.getEmail());
			//String subject = "Cryptools Account Confirmation";
			/*if(u.getUserName()!=null) {
				subject+=u.getUserName();
			}
			else {
				subject += u.getEmail();
			}*/
			helper.setSubject(subject);
			helper.setFrom("cryptools@thshsh.org");
			helper.setReplyTo(id);
			
			
			mailSender.send(mimeMessage);
		} 
		catch (Exception e) {
			LOGGER.warn("error",e);
			throw new IllegalStateException(e);
		}
	}
	
	
	
	public String createEmailContent(String resourceName,Map<String,Object> contextMap)  {
		
        try {
			String htmlText = IOUtils.toString(MailService.class.getResourceAsStream(resourceName),Charset.defaultCharset());
			//LOGGER.info("resource text: {}",htmlText);
			
			LOGGER.info("context: {}",contextMap.keySet());

			ExpressionParser parser = new SpelExpressionParser();
			TemplateParserContext tc = new TemplateParserContext();
			Expression expression = parser.parseExpression(htmlText,tc);
			
			StandardEvaluationContext context = new StandardEvaluationContext();
			MapAccessor ma = new MapAccessor();
			context.addPropertyAccessor(ma);
			
			
			
			/* if(contextMap != null) {
				context.setRootObject(contextMap);
			}*/

			String result = (String) expression.getValue(context, contextMap);
			
			//String result = expression.getValue(context,String.class);
			LOGGER.trace("result text: {}",result);
			
			return result;
		} 
        catch (IOException | ParseException | EvaluationException e) {
			throw new IllegalArgumentException("Could not create content", e);
		} 
		
        
	}

}
