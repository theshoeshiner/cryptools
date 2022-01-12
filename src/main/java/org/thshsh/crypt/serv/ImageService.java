package org.thshsh.crypt.serv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.thshsh.crypt.HasImage;
import org.thshsh.crypt.cryptocompare.CryptoCompare;
import org.thshsh.crypt.repo.ExchangeRepository;
import org.thshsh.crypt.web.view.ManagePortfolioView;

import com.vaadin.flow.component.html.Image;

@Service
@RestController
public class ImageService {

	public static final Logger LOGGER = LoggerFactory.getLogger(ImageService.class);

	@Autowired
	ExchangeRepository exRepo;

	@Autowired
	CryptoCompare compare;
	
	@Value("${app.media.archive}")
	String mediaPath;

	File mediaFolder;
	File imageFolder;


	@PostConstruct
	public void postConstruct() {
		mediaFolder = new File(mediaPath);
		imageFolder = new File(mediaPath,"images");
		
		if(!imageFolder.exists()) {
			imageFolder.mkdirs();
		}
	}


	public String getImageUrl(HasImage img) {
		if(img == null) return "image/unallocated.png";
		String imageUrl = "image/" + img.getImageUrl();
		return imageUrl;
	}

	public void syncImage(HasImage entity, String mediaUrl) throws IOException {
		LOGGER.info("sync image: {} , {}",entity,mediaUrl);

		String imageName = entity.getKey().toLowerCase()+"."+FilenameUtils.getExtension(mediaUrl);
		
		LOGGER.info("sync image: {}",imageName);

		if(entity.getImageUrl()!=null) {
			LOGGER.info("Checking for file: {}",entity.getImageUrl());
			File imageFile = new File(imageFolder,entity.getImageUrl());
			if(!imageFile.exists()) {
				//check for old file
				//String adjusted = mediaUrl.replaceAll(mediaUrl, imageName);
				File mediaUrlFile = new File(mediaUrl);
				File old = new File("./images/"+mediaUrlFile.getName());
				LOGGER.info("Checking for OLD file: {}",old);
				if(old.exists()) {
					//copy old file
					LOGGER.info("Copying OLD file");
					FileInputStream fis = new FileInputStream(old);
					FileOutputStream fos = new FileOutputStream(imageFile);
					IOUtils.copy(fis, fos);
					entity.setImageUrl(imageName);
				}
				else {
					LOGGER.info("Setting null");
					entity.setImageUrl(null);
				}
			}
		
		}

		if(entity.getImageUrl()==null) {
			File imageFile = new File(imageFolder,imageName);

			if(imageFile.exists()) {
				entity.setImageUrl(imageName);
			}
			else {
				if(copyFromApi(mediaUrl, imageFile)) {
					LOGGER.info("saving image: {}",imageFile);
					entity.setImageUrl(imageName);
				}
			}
		}

	}

	protected boolean copyFromApi(String mediaUrl,File archive) throws IOException {
		InputStream is = compare.getImage(mediaUrl);
		if(is != null) {
			FileOutputStream fos = new FileOutputStream(archive);
			IOUtils.copy(is, fos);
			return true;
		}
		return false;
	}

	public InputStream getImage(HasImage hi) throws FileNotFoundException {
		String imageName = hi.getImageUrl();
		if(imageName == null) {
			return null;
		}
		return getImage(imageName);
	}
	
	public Boolean checkForImage(String imageName) {
		File imageFile = new File(imageFolder,imageName);
		return imageFile.exists();
	}

	public FileInputStream getImage(String imageName) throws FileNotFoundException {
		File imageFile = new File(imageFolder,imageName);
		FileInputStream fis = new FileInputStream(imageFile);
		return fis;
	}

	@GetMapping( value = "/image/{name}",
			 produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
	)
	public @ResponseBody ResponseEntity<?> getImageRequest(@PathVariable String name) throws IOException {
		//LOGGER.info("getimage: {}",name);
		FileInputStream is = getImage(name);
		HttpHeaders httpHeaders = new HttpHeaders();
		//LOGGER.info("is: {}",is);

		//return IOUtils.toByteArray(is);

		InputStreamResource inputStreamResource = new InputStreamResource(is);
		httpHeaders.setContentLength(is.available());
		return new ResponseEntity<>(inputStreamResource, httpHeaders, HttpStatus.OK);

		//InputStream in = getClass().getResourceAsStream("/com/baeldung/produceimage/data.txt");
		//return IOUtils.toByteArray(in);
		/*if(exchange != null) {
			Exchange e = exRepo.findByKey(exchange);
			InputStream is = getImage(e);
			return IOUtils.toByteArray(is);
		}
		return null;*/
	}


}
